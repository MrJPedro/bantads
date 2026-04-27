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
      salario = dto.salario
    )
    
    novoCliente = clienteRepository.save(novoCliente)

    return toDTO(novoCliente)
  }

  @Transactional
  fun alterar(cpf: String, dto: PerfilInfo): DadosClienteResponse{
    val cliente = clienteRepository.findByCpf(cpf)?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado")

    cliente.nome = dto.nome
    cliente.email = dto.email
    cliente.telefone = dto.telefone
    cliente.salario = dto.salario

    clienteRepository.save(cliente)

    return toDTO(cliente)

  }

  @Transactional
  fun aprovar(cpf: String): DadosClienteResponse{

    val cliente = clienteRepository.findByCpf(cpf)?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado")
    //a ser implementada aprovação

    return toDTO(cliente)
  }

  @Transactional
  fun rejeitar(cpf: String, dto: RejeicaoRequest): DadosClienteResponse{

    val cliente = clienteRepository.findByCpf(cpf)?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado")
    //a ser implementada rejeição

    return toDTO(cliente)
  }

  private fun toDTO(entity: ClienteEntity): DadosClienteResponse{
    return DadosClienteResponse(
      id = entity.id,
      nome = entity.nome,
      cpf = entity.cpf,
      email = entity.email,
      telefone = entity.telefone,
      salario = entity.salario
    )
  }
}


