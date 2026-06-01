package com.bantads.gerente_service.service

import com.bantads.gerente_service.config.GERENTE_EVENT_EXCHANGE
import com.bantads.gerente_service.dto.*
import com.bantads.gerente_service.entity.GerenteEntity
import com.bantads.gerente_service.repository.GerenteRepository
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal

@Service
class GerenteService(
    private val gerenteRepository: GerenteRepository,
    private val rabbitTemplate: RabbitTemplate,
    private val restTemplate: RestTemplate,
    @Value("\${services.ms-cliente.base-url:http://ms-cliente:8080}")
    private val msClienteBaseUrl: String,
    @Value("\${services.ms-conta.base-url:http://ms-conta:8083}")
    private val msContaBaseUrl: String
) {

    fun listarTodos(cpf: String? = null): List<DadoGerente> {
        if (!cpf.isNullOrBlank()) {
            val gerente = gerenteRepository.findByCpf(cpf) ?: return emptyList()
            return listOf(toDTO(gerente))
        }

        return gerenteRepository.findAll()
            .sortedBy { it.nome }
            .map { toDTO(it) }
    }

    fun listarDashboard(): List<DashboardGerenteItemDTO> {
        val gerentes = gerenteRepository.findAll()

        val relatorioClientes = buscarRelatorioClientes()
        val contasPorGerente = relatorioClientes
            .mapNotNull { toContaDashboard(it) }
            .groupBy { it.gerente }

        return gerentes.map { gerente ->
            val contas = contasPorGerente[gerente.cpf].orEmpty()
            DashboardGerenteItemDTO(
                gerente = toDTO(gerente),
                clientes = contas,
                saldo_positivo = somarSaldoPositivo(contas),
                saldo_negativo = somarSaldoNegativo(contas)
            )
        }.sortedWith(compareByDescending<DashboardGerenteItemDTO> { it.saldo_positivo }.thenBy { it.gerente.nome })
    }

    fun buscarPorCpf(cpf: String): DadoGerente {
        val gerente = gerenteRepository.findByCpf(cpf) 
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Gerente não encontrado")
        
        return toDTO(gerente)
    }

    @Transactional
    fun inserir(dto: DadoGerenteInsercao): DadoGerente {
        validarDadosInsercao(dto)

        if (gerenteRepository.findByCpf(dto.cpf) != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado")
        }

        var novoGerente = GerenteEntity(
            cpf = dto.cpf,
            nome = dto.nome,
            email = dto.email,
            telefone = dto.telefone,
            quantidadeClientes = 0
        )
        
        novoGerente = gerenteRepository.save(novoGerente)

        val eventoCriacao = GerenteEvent(
            tipo = "insercao",
            cpfGerente = novoGerente.cpf,
            nome = novoGerente.nome,
            email = novoGerente.email,
            senha = dto.senha
        )
        rabbitTemplate.convertAndSend(GERENTE_EVENT_EXCHANGE, "gerente.event.insercao", eventoCriacao)

        val outrosGerentes = gerenteRepository.findAll().filter { it.cpf != novoGerente.cpf }
        
        if (outrosGerentes.isEmpty() || (outrosGerentes.size == 1 && outrosGerentes.first().quantidadeClientes <= 1)) {
            return toDTO(novoGerente)
        }

        val maxContas = outrosGerentes.maxOf { it.quantidadeClientes }
        if (maxContas == 0) {
            return toDTO(novoGerente)
        }

        val gerentesMax = outrosGerentes.filter { it.quantidadeClientes == maxContas }
        val contasPossiveis = mutableListOf<ContaDTO>()

        for (g in gerentesMax) {
            contasPossiveis.addAll(recuperarContasPorGerente(g.cpf))
        }

        if (contasPossiveis.isNotEmpty()) {
            val contaEscolhida = contasPossiveis.filter { it.saldo > BigDecimal.ZERO }.minByOrNull { it.saldo }
                ?: contasPossiveis.minByOrNull { it.saldo }

            if (contaEscolhida != null) {
                val gerenteDoador = gerentesMax.find { it.cpf == contaEscolhida.gerente }
                if (gerenteDoador != null) {
                    val contaAtualizada = atualizarContaGerente(contaEscolhida.numero, novoGerente.cpf)
                    val clienteAtualizado = contaAtualizada && atualizarClienteGerente(contaEscolhida.cliente, novoGerente.cpf)
                    if (contaAtualizada && clienteAtualizado) {
                        gerenteDoador.quantidadeClientes -= 1
                        novoGerente.quantidadeClientes += 1
                        gerenteRepository.save(gerenteDoador)
                        gerenteRepository.save(novoGerente)

                        val eventoTransferencia = GerenteEvent(
                            tipo = "transferencia_conta",
                            cpfGerente = novoGerente.cpf,
                            cpfGerenteAnterior = gerenteDoador.cpf,
                            numeroConta = contaEscolhida.numero
                        )
                        rabbitTemplate.convertAndSend(GERENTE_EVENT_EXCHANGE, "gerente.event.transferencia", eventoTransferencia)
                    }
                }
            }
        }

        return toDTO(novoGerente)
    }

    @Transactional
    fun alterar(cpf: String, dto: DadoGerenteAtualizacao): DadoGerente {
        validarDadosAlteracao(dto)

        val gerente = gerenteRepository.findByCpf(cpf)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Gerente não encontrado")

        gerente.nome = dto.nome
        gerente.email = dto.email
        
        gerenteRepository.save(gerente)

        val eventoAlteracao = GerenteEvent(
            tipo = "alteracao",
            cpfGerente = gerente.cpf,
            nome = gerente.nome,
            email = gerente.email,
            senha = dto.senha
        )
        rabbitTemplate.convertAndSend(GERENTE_EVENT_EXCHANGE, "gerente.event.alteracao", eventoAlteracao)

        return toDTO(gerente)
    }

    @Transactional
    fun remover(cpf: String): DadoGerente {
        val gerente = gerenteRepository.findByCpf(cpf)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Gerente não encontrado")

        if (gerenteRepository.count() <= 1) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é possível remover o último gerente do sistema.")
        }

        val gerenteRemovido = toDTO(gerente)

        val todos = gerenteRepository.findAll().filter { it.cpf != cpf }
        val herdeiro = todos.minByOrNull { it.quantidadeClientes }
            ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Nenhum herdeiro encontrado.")

        val contasGerente = recuperarContasPorGerente(cpf)

        val contasAtualizadas = contasGerente.count { conta ->
            val sucessoConta = atualizarContaGerente(conta.numero, herdeiro.cpf)
            val sucessoCliente = sucessoConta && atualizarClienteGerente(conta.cliente, herdeiro.cpf)
            sucessoConta && sucessoCliente
        }

        herdeiro.quantidadeClientes += contasAtualizadas
        gerenteRepository.save(herdeiro)

        gerenteRepository.delete(gerente)

        val evento = GerenteEvent(
            tipo = "remocao",
            cpfGerente = cpf,
            cpfNovoGerente = herdeiro.cpf
        )
        
        rabbitTemplate.convertAndSend(GERENTE_EVENT_EXCHANGE, "gerente.event.remocao", evento)

        return gerenteRemovido
    }

    private fun buscarRelatorioClientes(): List<RelatorioClienteCompostoDTO> {
        return try {
            restTemplate.getForObject(
                "${baseClienteUrl()}/clientes?filtro=adm_relatorio_clientes",
                Array<RelatorioClienteCompostoDTO>::class.java
            )?.toList() ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun atualizarContaGerente(numeroConta: String, cpfNovoGerente: String): Boolean {
        return try {
            val body = mapOf("gerente" to cpfNovoGerente)
            val request = HttpEntity(body)

            val response = restTemplate.exchange(
                "${baseContaUrl()}/contas/$numeroConta/gerente",
                HttpMethod.PUT,
                request,
                Any::class.java
            )

            response.statusCode.is2xxSuccessful
        } catch (ex: Exception) {
            false
        }
    }

    private fun recuperarContasPorGerente(cpfGerente: String): List<ContaDTO> {
        return try {
            restTemplate.getForObject("${baseContaUrl()}/contas/gerente/$cpfGerente", Array<ContaDTO>::class.java)
                ?.toList()
                .orEmpty()
        } catch (ex: Exception) {
            emptyList()
        }
    }

    private fun validarDadosInsercao(dto: DadoGerenteInsercao) {
        if (dto.cpf.isBlank() || dto.nome.isBlank() || dto.email.isBlank() || dto.telefone.isBlank() || dto.senha.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF, nome, email, telefone e senha são obrigatórios")
        }
        if (!validarCpf(dto.cpf)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF inválido")
        }
        if (!validarEmail(dto.email)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Email inválido")
        }
        if (dto.senha.length < 6) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha deve ter no mínimo 6 caracteres")
        }
    }

    private fun validarDadosAlteracao(dto: DadoGerenteAtualizacao) {
        if (dto.nome.isBlank() || dto.email.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome e email são obrigatórios")
        }
        if (!validarEmail(dto.email)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Email inválido")
        }
    }

    private fun validarCpf(cpf: String): Boolean {
        val numeros = cpf.filter { it.isDigit() }
        return numeros.length == 11
    }

    private fun validarEmail(email: String): Boolean {
        val regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        return email.isNotBlank() && regex.matches(email)
    }

    private fun atualizarClienteGerente(cpfCliente: String, cpfNovoGerente: String): Boolean {
        return try {
            val body = mapOf("gerenteCpf" to cpfNovoGerente)
            val request = HttpEntity(body)

            val response = restTemplate.exchange(
                "${baseClienteUrl()}/clientes/$cpfCliente/gerente",
                HttpMethod.PUT,
                request,
                Any::class.java
            )

            response.statusCode.is2xxSuccessful
        } catch (ex: Exception) {
            false
        }
    }

    private fun baseClienteUrl(): String = msClienteBaseUrl.trimEnd('/')
    private fun baseContaUrl(): String = msContaBaseUrl.trimEnd('/')

    private fun toContaDashboard(relatorio: RelatorioClienteCompostoDTO): ContaDashboardDTO? {
        val numeroConta = relatorio.conta ?: return null
        val cpfGerente = relatorio.gerenteCpf ?: return null

        return ContaDashboardDTO(
            cliente = relatorio.cpf,
            numero = numeroConta,
            saldo = relatorio.saldo ?: BigDecimal.ZERO,
            limite = relatorio.limite ?: BigDecimal.ZERO,
            gerente = cpfGerente
        )
    }

    private fun somarSaldoPositivo(contas: List<ContaDashboardDTO>): BigDecimal {
        return contas
            .filter { it.saldo >= BigDecimal.ZERO }
            .fold(BigDecimal.ZERO) { total, conta -> total + conta.saldo }
    }

    private fun somarSaldoNegativo(contas: List<ContaDashboardDTO>): BigDecimal {
        return contas
            .filter { it.saldo < BigDecimal.ZERO }
            .fold(BigDecimal.ZERO) { total, conta -> total + conta.saldo }
    }

    private fun toDTO(entity: GerenteEntity): DadoGerente {
        return DadoGerente(
            id = entity.id,
            cpf = entity.cpf,
            nome = entity.nome,
            email = entity.email,
            telefone = entity.telefone,
            quantidadeClientes = entity.quantidadeClientes
        )
    }
}
