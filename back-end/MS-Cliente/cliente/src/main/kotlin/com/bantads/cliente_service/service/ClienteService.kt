package com.bantads.cliente_service.service

import com.bantads.cliente_service.config.CLIENTE_EVENT_EXCHANGE
import com.bantads.cliente_service.dto.*
import com.bantads.cliente_service.entity.ClienteEntity
import com.bantads.cliente_service.repository.ClienteRepository
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class ClienteService(
  private val clienteRepository: ClienteRepository,
  private val rabbitTemplate: RabbitTemplate
) {

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

    var novoCliente = ClienteEntity(
      nome = dto.nome,
      email = dto.email,
      cpf = dto.cpf,
      telefone = dto.telefone,
      salario = dto.salario.toDouble().toBigDecimal(),
      endereco = "Rua Desconhecida", // MOCK
      cep = "00000000",              // MOCK
      cidade = "Desconhecida",       // MOCK
      estado = "ST",                 // MOCK
      status = "AGUARDANDO_APROVACAO"
    )

    novoCliente = clienteRepository.save(novoCliente)

    enviarEvento("autocadastro", novoCliente)

    return toDTO(novoCliente)
  }

  @Transactional
  fun alterar(cpf: String, dto: PerfilInfo): DadosClienteResponse {
    val cliente = clienteRepository.findByCpf(cpf) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado")

    cliente.nome = dto.nome
    cliente.email = dto.email
    cliente.telefone = dto.telefone
    cliente.salario = dto.salario.toDouble().toBigDecimal()

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

  private fun enviarEvento(tipo: String, cliente: ClienteEntity, motivo: String? = null) {
    val evento = ClienteEvent(
      tipo = tipo,
      cpf = cliente.cpf,
      nome = cliente.nome,
      email = cliente.email,
      telefone = cliente.telefone,
      salario = cliente.salario,
      status = cliente.status,
      motivo = motivo
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
      salario = entity.salario
    )
  }
}


