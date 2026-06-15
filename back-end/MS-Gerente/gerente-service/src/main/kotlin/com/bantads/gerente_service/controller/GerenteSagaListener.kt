package com.bantads.gerente_service.controller

import com.bantads.gerente_service.config.SAGA_EXCHANGE
import com.bantads.gerente_service.dto.SagaMessage
import com.bantads.gerente_service.repository.GerenteRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component
import kotlin.math.max

@Component
class GerenteSagaListener(
    private val gerenteRepository: GerenteRepository,
    private val rabbitTemplate: RabbitTemplate,
    private val objectMapper: ObjectMapper
) {

    /*Escuta comandos enviados pela Saga para o MS-Gerente*/
    @RabbitListener(queues = ["\${saga.rabbitmq.queue:gerente-command-queue}"])
    fun handleCommand(message: SagaMessage) {

        /*R1/R10 - Define o gerente disponivel para assumir o cliente/conta*/
        if ("OBTER_GERENTE_DISPONIVEL" == message.acao) {
            try {
                val jsonNode = objectMapper.readTree(message.payload ?: "{}") as com.fasterxml.jackson.databind.node.ObjectNode
                val cpfGerenteAtual = jsonNode.get("gerenteCpf")?.asText()?.takeIf { it.isNotBlank() }

                /*Usa gerente informado ou escolhe o gerente com menos clientes*/
                val gerente = cpfGerenteAtual?.let { gerenteRepository.findByCpf(it) }
                    ?: gerenteRepository.findTopByOrderByQuantidadeClientesAsc()
                    ?: throw RuntimeException("Nenhum gerente encontrado no sistema.")

                /*Reserva o cliente para esse gerente durante a Saga*/
                gerente.quantidadeClientes += 1
                gerenteRepository.save(gerente)

                /*Devolve para a Saga o CPF do gerente escolhido*/
                jsonNode.put("gerenteCpf", gerente.cpf)

                val reply = SagaMessage(
                    sagaId = message.sagaId,
                    tipoSaga = message.tipoSaga,
                    acao = message.acao,
                    payload = objectMapper.writeValueAsString(jsonNode),
                    sucesso = true
                )
                /*Resposta de sucesso para o orquestrador*/
                rabbitTemplate.convertAndSend(SAGA_EXCHANGE, "saga.reply", reply)
            } catch (e: Exception) {
                val errorReply = SagaMessage(
                    sagaId = message.sagaId,
                    tipoSaga = message.tipoSaga,
                    acao = message.acao,
                    payload = message.payload,
                    sucesso = false
                )
                /*Resposta de falha para a Saga iniciar compensacao*/
                rabbitTemplate.convertAndSend(SAGA_EXCHANGE, "saga.reply", errorReply)
            }
        } 

        /*Compensacao: desfaz a reserva do gerente se a Saga falhar*/
        else if ("ROLLBACK_GERENTE" == message.acao) {
            try {
                val jsonNode = objectMapper.readTree(message.payload ?: "{}")
                val cpfGerente = if (jsonNode.has("gerenteCpf")) jsonNode.get("gerenteCpf").asText() else null
                if (cpfGerente != null) {
                    val gerente = gerenteRepository.findByCpf(cpfGerente)
                    if (gerente != null) {
                        gerente.quantidadeClientes = max(0, gerente.quantidadeClientes - 1)
                        gerenteRepository.save(gerente)
                    }
                }
            } catch (e: Exception) {
                println("Erro ao realizar rollback de gerente: ${e.message}")
            }
        }
    }
}
