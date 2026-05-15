package com.bantads.gerente_service.service

import com.bantads.gerente_service.config.GERENTE_EVENT_EXCHANGE
import com.bantads.gerente_service.dto.*
import com.bantads.gerente_service.entity.GerenteEntity
import com.bantads.gerente_service.repository.GerenteRepository
import org.springframework.amqp.rabbit.core.RabbitTemplate
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
    private val restTemplate: RestTemplate
) {

    fun listarTodos(): List<DadoGerente> {
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
            try {
                val url = "http://ms-conta:8083/contas/gerente/${g.cpf}"
                val response = restTemplate.getForObject(url, Array<ContaDTO>::class.java)
                if (response != null) {
                    contasPossiveis.addAll(response)
                }
            } catch (e: Exception) {
                // Ignore
            }
        }

        if (contasPossiveis.isNotEmpty()) {
            val contaEscolhida = contasPossiveis.filter { it.saldo > BigDecimal.ZERO }.minByOrNull { it.saldo }
                ?: contasPossiveis.minByOrNull { it.saldo }

            if (contaEscolhida != null) {
                val gerenteDoador = gerentesMax.find { it.cpf == contaEscolhida.gerente }
                if (gerenteDoador != null) {
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

        if (gerenteRepository.count() <= 1) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é possível remover o último gerente do sistema.")
        }

        val todos = gerenteRepository.findAll().filter { it.cpf != cpf }
        val herdeiro = todos.minByOrNull { it.quantidadeClientes }
            ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Nenhum herdeiro encontrado.")

        herdeiro.quantidadeClientes += gerente.quantidadeClientes
        gerenteRepository.save(herdeiro)

        gerenteRepository.delete(gerente)

        val evento = GerenteEvent(
            tipo = "remocao",
            cpfGerente = cpf,
            cpfNovoGerente = herdeiro.cpf
        )
        
        rabbitTemplate.convertAndSend(GERENTE_EVENT_EXCHANGE, "gerente.event.remocao", evento)
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