package com.bantads.cliente_service.service

import com.bantads.cliente_service.config.CLIENTE_EVENT_EXCHANGE
import com.bantads.cliente_service.dto.*
import com.bantads.cliente_service.entity.ClienteEntity
import com.bantads.cliente_service.repository.ClienteRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class ClienteService(
    private val clienteRepository: ClienteRepository,
    private val rabbitTemplate: RabbitTemplate,
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper
) {

    fun listarClientes(filtro: String?): List<Any> {
        return when (filtro) {
            "para_aprovar" -> listarPendentes()
            "adm_relatorio_clientes" -> relatorioClientes()
            "melhores_clientes" -> melhoresClientes()
            else -> listarTodos()
        }
    }

  fun listarTodos(): List<DadosClienteResponse> {
    return clienteRepository.findAll()
      .sortedBy { it.nome }
      .map { toDTO(it) }
  }

  fun buscarPorCpf(cpf: String): DadosClienteResponse {
    val cliente = clienteRepository.findByCpf(cpf) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado")

    return toDTO(cliente)
  }

  @Transactional
  fun autocadastro(dto: AutocadastroInfo): DadosClienteResponse {
        if (clienteRepository.findByCpf(dto.cpf) != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado")
        }

        if (dto.nome.isBlank() || dto.email.isBlank() || dto.cpf.isBlank() || dto.telefone.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome, email, CPF e telefone são obrigatórios")
        }

        val salario = dto.salario.toString().toBigDecimalOrNull()
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Salário inválido")

        if (salario <= BigDecimal.ZERO) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Salário deve ser maior que zero")
        }

        val gerenteCpf = escolherGerente()

        var novoCliente = ClienteEntity(
            nome = dto.nome,
            email = dto.email,
            cpf = dto.cpf,
            telefone = dto.telefone,
            salario = salario.setScale(2, RoundingMode.HALF_EVEN),
            endereco = dto.endereco,
            cep = dto.cep,
            cidade = dto.cidade,
            estado = dto.estado,
            gerenteCpf = gerenteCpf,
            status = "AGUARDANDO_APROVACAO"
        )

        novoCliente = clienteRepository.save(novoCliente)

        // Iniciar Saga de Autocadastro!
        val payloadJson = objectMapper.writeValueAsString(toDTO(novoCliente))
        val sagaRequest = SagaMessage(
            tipoSaga = "AUTOCADASTRO",
            payload = payloadJson
        )
        rabbitTemplate.convertAndSend("saga-exchange", "saga.request", sagaRequest)

        return toDTO(novoCliente)
    }

    @Transactional
    fun atualizarGerente(cpf: String, gerenteCpf: String): DadosClienteResponse {
        if (gerenteCpf.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF do gerente é obrigatório")
        }

        val cliente = clienteRepository.findByCpf(cpf)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado")

        cliente.gerenteCpf = gerenteCpf
        clienteRepository.save(cliente)

        return toDTO(cliente)
    }

    @Transactional
    fun alterar(cpf: String, dto: PerfilInfo): DadosClienteResponse {
        val cliente = clienteRepository.findByCpf(cpf) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado")

        cliente.nome = dto.nome
        cliente.email = dto.email
        cliente.telefone = dto.telefone

        val novoSalario = dto.salario.toString().toBigDecimalOrNull()
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Salário inválido")

        cliente.salario = novoSalario.setScale(2, RoundingMode.HALF_EVEN)

        clienteRepository.save(cliente)

        enviarEvento("perfil-alterado", cliente)

        return toDTO(cliente)
    }

    @Transactional
    fun aprovar(cpf: String): DadosClienteResponse {
        val cliente = clienteRepository.findByCpf(cpf) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado")

        cliente.status = "APROVADO"
        clienteRepository.save(cliente)

        enviarEvento("aprovacao", cliente)

        return toDTO(cliente)
    }

    @Transactional
    fun rejeitar(cpf: String, dto: RejeicaoRequest): DadosClienteResponse {
        val cliente = clienteRepository.findByCpf(cpf) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado")

        cliente.status = "REJEITADO"
        clienteRepository.save(cliente)

        enviarEvento("rejeicao", cliente, dto.motivo)

        return toDTO(cliente)
    }

    private fun listarPendentes(): List<DadosClienteResponse> {
        return clienteRepository.findByStatus("AGUARDANDO_APROVACAO")
            .sortedBy { it.nome }
            .map { toDTO(it) }
    }

    private fun relatorioClientes(): List<RelatorioClienteDTO> {
        return clienteRepository.findByStatus("APROVADO")
            .sortedBy { it.nome }
            .map { cliente ->
                val conta = buscarContaCliente(cliente.cpf)
                val gerente = buscarGerente(conta?.gerente)
                toRelatorioDTO(cliente, conta, gerente)
            }
    }

    private fun melhoresClientes(): List<RelatorioClienteDTO> {
        return try {
            val topContas = restTemplate.getForObject("http://ms-conta:8083/contas/top3", Array<ContaResumoDTO>::class.java)
                ?.toList() ?: emptyList()

            topContas.mapNotNull { conta ->
                val cliente = clienteRepository.findByCpf(conta.cliente) ?: return@mapNotNull null
                val gerente = buscarGerente(conta.gerente)
                toRelatorioDTO(cliente, conta, gerente)
            }
        } catch (_: HttpClientErrorException) {
            emptyList()
        }
    }

    private fun buscarContaCliente(cpf: String): ContaResumoDTO? {
        return try {
            restTemplate.getForObject("http://ms-conta:8083/contas/cliente/$cpf", ContaResumoDTO::class.java)
        } catch (_: HttpClientErrorException) {
            null
        }
    }

    private fun buscarGerente(cpf: String?): GerenteInfoDTO? {
        if (cpf.isNullOrBlank()) return null

        return try {
            restTemplate.getForObject("http://ms-gerente:8082/gerentes/$cpf", GerenteInfoDTO::class.java)
        } catch (_: HttpClientErrorException) {
            null
        }
    }

    private fun escolherGerente(): String? {
        return try {
            restTemplate.getForObject("http://ms-gerente:8082/gerentes", Array<GerenteInfoDTO>::class.java)
                ?.toList()
                ?.minByOrNull { it.quantidadeClientes }
                ?.cpf
        } catch (_: Exception) {
            null
        }
    }

    private fun toRelatorioDTO(
        cliente: ClienteEntity,
        conta: ContaResumoDTO?,
        gerente: GerenteInfoDTO?
    ): RelatorioClienteDTO {
        return RelatorioClienteDTO(
            cpf = cliente.cpf,
            nome = cliente.nome,
            email = cliente.email,
            telefone = cliente.telefone,
            salario = cliente.salario.setScale(2, RoundingMode.HALF_EVEN),
            endereco = cliente.endereco,
            cidade = cliente.cidade,
            estado = cliente.estado,
            conta = conta?.numero,
            saldo = conta?.saldo,
            limite = conta?.limite,
            gerenteCpf = gerente?.cpf,
            gerenteNome = gerente?.nome,
            gerenteEmail = gerente?.email
        )
    }

    private fun enviarEvento(tipo: String, cliente: ClienteEntity, motivo: String? = null) {
        val evento = ClienteEvent(
            tipo = tipo,
            cpf = cliente.cpf,
            nome = cliente.nome,
            email = cliente.email,
            telefone = cliente.telefone,
            salario = cliente.salario,
            status = cliente.status,
            motivo = motivo,
            gerenteCpf = cliente.gerenteCpf
        )

        rabbitTemplate.convertAndSend(CLIENTE_EVENT_EXCHANGE, "cliente.event.$tipo", evento)
        println("[RABBITMQ] Evento '$tipo' enviado para exchange ${CLIENTE_EVENT_EXCHANGE}: ${cliente.cpf}")
    }

    private fun toDTO(entity: ClienteEntity): DadosClienteResponse {
        return DadosClienteResponse(
            id = entity.id ?: 0,
            nome = entity.nome,
            cpf = entity.cpf,
            email = entity.email,
            telefone = entity.telefone,
            salario = entity.salario,
            endereco = entity.endereco,
            cep = entity.cep,
            cidade = entity.cidade,
            estado = entity.estado,
            gerenteCpf = entity.gerenteCpf
        )
    }
}


