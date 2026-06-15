package com.bantads.cliente_service.amqp

import com.bantads.cliente_service.dto.SagaMessage
import com.bantads.cliente_service.dto.DadosClienteResponse
import com.bantads.cliente_service.repository.ClienteRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
class ClienteSagaListener(
    private val clienteRepository: ClienteRepository,
    private val rabbitTemplate: RabbitTemplate,
    private val objectMapper: ObjectMapper
) {

    @RabbitListener(queues = ["cliente-command-queue"])
    fun onClienteCommand(command: SagaMessage) {
        println("[MS-CLIENTE] Comando SAGA recebido: ${command.acao}")

        try {
            when (command.acao) {
                "ROLLBACK_CLIENTE" -> {
                    // Desfazer o cliente que ficou travado por erro do Auth/Gerente
                    println("[MS-CLIENTE] ${objectMapper.writeValueAsString(command)}")
                    val clienteDto = objectMapper.readValue(command.payload, DadosClienteResponse::class.java)
                    val clienteEntity = clienteRepository.findByCpf(clienteDto.cpf)
                    
                    if (clienteEntity != null) {
                        clienteRepository.delete(clienteEntity)
                        println("[MS-CLIENTE] Rollback executado! Cliente \${clienteDto.cpf} deletado.")
                    }
                    
                    // Responder Sucesso (pois o rollback funcionou)
                    responderSaga(command, sucesso = true)
                }
                // Adicionar futuras ações como ATUALIZAR_LIMITE, etc
                else -> {
                    println("[MS-CLIENTE] Ação desconhecida: ${command.acao}")
                }
            }
        } catch (e: Exception) {
            println("[MS-CLIENTE] Erro ao processar comando SAGA: ${e.message}")
            responderSaga(command, sucesso = false)
        }
    }

    private fun responderSaga(command: SagaMessage, sucesso: Boolean, motivo: String? = null) {
        val reply = SagaMessage(
            sagaId = command.sagaId,
            tipoSaga = command.tipoSaga,
            acao = command.acao,
            sucesso = sucesso,
            payload = command.payload
        )
        // O orquestrador esta escutando o saga-reply-queue
        rabbitTemplate.convertAndSend("saga-exchange", "saga.reply", reply)
    }
}