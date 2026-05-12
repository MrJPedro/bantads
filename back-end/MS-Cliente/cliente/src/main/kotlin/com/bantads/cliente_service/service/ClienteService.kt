package com.bantads.cliente_service.service

import com.bantads.cliente_service.dto.*
import com.bantads.cliente_service.entity.ClienteEntity
import com.bantads.cliente_service.repository.ClienteRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class ClienteService(
  private val clienteRepository: ClienteRepository
) {

  fun listarTodos(): List<DadosClienteResponse> {
    return clienteRepository.findAll()
      .sortedBy {it.nome}
      .map {toDTO(it)}
  }

  fun buscarPorCpf(cpf: String): DadosClienteResponse {
    val cliente = clienteRepository.findByCpf(cpf)?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado")

    return toDTO(cliente)
  }

  @Transactional
  fun autocadastro(dto: AutocadastroInfo): DadosClienteResponse{
    if (clienteRepository.findByCpf(dto.cpf) != null){
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

    println("[MOCK RABBITMQ] Evento de autocadastro enviado para SAGA (Conta & Auth): ${novoCliente.cpf}")

    return toDTO(novoCliente)
  }

  @Transactional
  fun alterar(cpf: String, dto: PerfilInfo): DadosClienteResponse{
    val cliente = clienteRepository.findByCpf(cpf)?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado")

    cliente.nome = dto.nome
    cliente.email = dto.email
    cliente.telefone = dto.telefone
    cliente.salario = dto.salario.toDouble().toBigDecimal()

    clienteRepository.save(cliente)

    println("[MOCK RABBITMQ] Evento enviado para MS-Conta solicitando recálculo do limite para: $cpf")

    return toDTO(cliente)

  }

  @Transactional
  fun aprovar(cpf: String): DadosClienteResponse{

    val cliente = clienteRepository.findByCpf(cpf)?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado")
    
    cliente.status = "APROVADO"
    clienteRepository.save(cliente)

    println("[MOCK RABBITMQ] Evento de aprovação enviado (Criação de Conta e Envio de Email): $cpf")

    return toDTO(cliente)
  }

  @Transactional
  fun rejeitar(cpf: String, dto: RejeicaoRequest): DadosClienteResponse{

    val cliente = clienteRepository.findByCpf(cpf)?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado")
    
    cliente.status = "REJEITADO"
    clienteRepository.save(cliente)

    println("[MOCK RABBITMQ] Evento de rejeição enviado via Rabbit (Motivo: ${dto.motivo}) para: $cpf")

    return toDTO(cliente)
  }

  private fun toDTO(entity: ClienteEntity): DadosClienteResponse{
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


