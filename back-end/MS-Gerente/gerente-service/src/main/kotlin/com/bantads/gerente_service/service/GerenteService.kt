package com.bantads.gerente_service.service

import com.bantads.gerente_service.dto.*
import com.bantads.gerente_service.entity.GerenteEntity
import com.bantads.gerente_service.repository.GerenteRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class GerenteService(
    private val gerenteRepository: GerenteRepository
) {

    fun listarTodos(): List<DadoGerente> {
        // R19: Ordenados de forma crescente por nome
        return gerenteRepository.findAll()
            .sortedBy { it.nome }
            .map { toDTO(it) }
    }

    fun buscarPorCpf(cpf: String): DadoGerente {
        val gerente = gerenteRepository.findByCpf(cpf) 
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Gerente não encontrado")
        
        return toDTO(gerente)
    }

    @Transactional
    fun inserir(dto: DadoGerenteInsercao): DadoGerente {
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

        return toDTO(novoGerente)
    }

    @Transactional
    fun alterar(cpf: String, dto: DadoGerenteAtualizacao): DadoGerente {
        val gerente = gerenteRepository.findByCpf(cpf)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Gerente não encontrado")

        gerente.nome = dto.nome
        gerente.email = dto.email
        
        gerenteRepository.save(gerente)

        return toDTO(gerente)
    }

    @Transactional
    fun remover(cpf: String) {
        val gerente = gerenteRepository.findByCpf(cpf)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Gerente não encontrado")

        // R18: Não permitir remoção do último gerente
        if (gerenteRepository.count() <= 1) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é possível remover o último gerente do sistema.")
        }

        val todos = gerenteRepository.findAll().filter { it.cpf != cpf }
        val herdeiro = todos.minByOrNull { it.quantidadeClientes }
            ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Nenhum herdeiro encontrado.")

        herdeiro.quantidadeClientes += gerente.quantidadeClientes
        gerenteRepository.save(herdeiro)

        gerenteRepository.delete(gerente)

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