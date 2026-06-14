package com.bantads.gerente_service.service

import com.bantads.gerente_service.config.GERENTE_EVENT_EXCHANGE
import com.bantads.gerente_service.dto.*
import com.bantads.gerente_service.entity.GerenteEntity
import com.bantads.gerente_service.repository.GerenteRepository
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class GerenteService(
    private val gerenteRepository: GerenteRepository,
    private val rabbitTemplate: RabbitTemplate
) {

    /*R19 - Lista todos os gerentes ou filtra por CPF*/
    fun listarTodos(cpf: String? = null): List<DadoGerente> {
        if (!cpf.isNullOrBlank()) {
            val gerente = gerenteRepository.findByCpf(cpf) ?: return emptyList()
            return listOf(toDTO(gerente))
        }

        return gerenteRepository.findAll()
            .sortedBy { it.nome }
            .map { toDTO(it) }
    }

    /*Consulta um gerente especifico por CPF*/
    fun buscarPorCpf(cpf: String): DadoGerente {
        val gerente = gerenteRepository.findByCpf(cpf) 
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Gerente não encontrado")
        
        return toDTO(gerente)
    }

    /*R17 - Insere gerente e publica evento para integracao com Auth/Saga*/
    @Transactional
    fun inserir(dto: DadoGerenteInsercao): DadoGerente {
        validarDadosInsercao(dto)

        if (gerenteRepository.findByCpf(dto.cpf) != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado")
        }
        if (gerenteRepository.findByEmail(dto.email) != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado")
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

        return toDTO(novoGerente)
    }

    /*R20 - Altera dados do gerente e publica evento para sincronizar Auth*/
    @Transactional
    fun alterar(cpf: String, dto: DadoGerenteAtualizacao): DadoGerente {
        validarDadosAlteracao(dto)

        val gerente = gerenteRepository.findByCpf(cpf)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Gerente não encontrado")
        val gerenteComMesmoEmail = gerenteRepository.findByEmail(dto.email)
        if (gerenteComMesmoEmail != null && gerenteComMesmoEmail.cpf != cpf) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado")
        }

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

    /*R18 - Remove gerente e publica evento para redistribuicao das contas*/
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

        val evento = GerenteEvent(
            tipo = "remocao",
            cpfGerente = cpf,
            cpfNovoGerente = herdeiro.cpf
        )

        gerenteRepository.delete(gerente)
        
        rabbitTemplate.convertAndSend(GERENTE_EVENT_EXCHANGE, "gerente.event.remocao", evento)

        return gerenteRemovido
    }

    /*Validacoes da insercao de gerente*/
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

    /*Validacoes da alteracao de gerente*/
    private fun validarDadosAlteracao(dto: DadoGerenteAtualizacao) {
        if (dto.nome.isBlank() || dto.email.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome e email são obrigatórios")
        }
        if (!validarEmail(dto.email)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Email inválido")
        }
    }

    /*Valida se o CPF possui 11 digitos*/
    private fun validarCpf(cpf: String): Boolean {
        val numeros = cpf.filter { it.isDigit() }
        return numeros.length == 11
    }

    /*Valida formato basico de email*/
    private fun validarEmail(email: String): Boolean {
        val regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        return email.isNotBlank() && regex.matches(email)
    }

    /*Converte entidade JPA para DTO de resposta*/
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
