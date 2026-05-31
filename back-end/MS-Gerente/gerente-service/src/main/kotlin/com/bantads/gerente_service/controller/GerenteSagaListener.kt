package com.bantads.gerente_service.controller

import com.bantads.gerente_service.config.SAGA_EXCHANGE
import com.bantads.gerente_service.dto.SagaMessage
import com.bantads.gerente_service.repository.GerenteRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
class GerenteSagaListener(
    private val gerenteRepository: GerenteRepository,
    private val rabbitTemplate: RabbitTemplate,
    private val objectMapper: ObjectMapper
) {

    @RabbitListener(queues = ["\${saga.rabbitmq.queue:gerente-command-queue}"])
    fun handleCommand(message: SagaMessage) {
        if ("OBTER_GERENTE_DISPONIVEL" == message.acao) {
            try {
                // 1. Encontrar o gerente com menos clientes
                val gerente = gerenteRepository.findTopByOrderByQuantidadeClientesAsc()
                    ?: throw RuntimeException("Nenhum gerente encontrado no sistema.")

                // 2. Incrementar a quantidade de clientes dele (otimista)
                gerente.quantidadeClientes += 1
                gerenteRepository.save(gerente)

                // 3. Montar o payload com o gerente_cpf e dados originais
                val dados = message.dados?.toMutableMap() ?: mutableMapOf()
                dados["gerente_cpf"] = gerente.cpf
                dados["gerente_nome"] = gerente.nome

                val reply = SagaMessage(
                    sagaId = message.sagaId,
                    acao = message.acao,
                    dados = dados,
                    sucesso = true
                )
                rabbitTemplate.convertAndSend(SAGA_EXCHANGE, "saga.reply", reply)
            } catch (e: Exception) {
                val errorReply = SagaMessage(
                    sagaId = message.sagaId,
                    acao = message.acao,
                    erro = e.message,
                    sucesso = false
                )
                rabbitTemplate.convertAndSend(SAGA_EXCHANGE, "saga.reply", errorReply)
            }
        } 
        else if ("ROLLBACK_GERENTE" == message.acao) {
            try {
                val dados = message.dados ?: emptyMap()
                val cpfGerente = dados["gerente_cpf"] as? String
                if (cpfGerente != null) {
                    val gerente = gerenteRepository.findByCpf(cpfGerente)
                    if (gerente != null) {
                        gerente.quantidadeClientes -= 1
                        gerenteRepository.save(gerente)
                    }
                }
            } catch (e: Exception) {
                // Aqui não precisamos emitir erro pra SAGA, rollback geralmente tenta e morre quieto ou deixa na DLT
                println("Erro ao realizar rollback de gerente: \${e.message}")
            }
        }
    }
}