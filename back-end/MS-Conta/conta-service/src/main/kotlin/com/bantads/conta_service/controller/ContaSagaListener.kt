package com.bantads.conta_service.controller

import com.bantads.conta_service.config.SAGA_EXCHANGE
import com.bantads.conta_service.dto.ContaDTO
import com.bantads.conta_service.dto.SagaMessage
import com.bantads.conta_service.service.ContaService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.util.UUID

@Component
class ContaSagaListener(
    private val contaService: ContaService,
    private val rabbitTemplate: RabbitTemplate,
    private val objectMapper: ObjectMapper
) {

    @RabbitListener(queues = ["\${saga.rabbitmq.queue:conta-command-queue}"])
    fun handleCommand(message: SagaMessage) {
        if ("CRIAR_CONTA_INICIAL" == message.acao) {
            try {
                val jsonNode = objectMapper.readTree(message.payload ?: "{}")
                
                val clienteCpf = jsonNode.get("cpf")?.asText() ?: throw RuntimeException("CPF não fornecido")
                val gerenteCpf = jsonNode.get("gerenteCpf")?.asText() ?: throw RuntimeException("Gerente não fornecido")
                val salarioStr = jsonNode.get("salario")?.asText() ?: "0"
                val salario = BigDecimal(salarioStr)

                // Regra: limite de cheque especial (salário / 2)
                val limite = if (salario >= BigDecimal("2000.00")) {
                    salario.divide(BigDecimal("2"), 2, RoundingMode.HALF_EVEN)
                } else {
                    BigDecimal.ZERO
                }

                val novoNumero = generateNumeroConta()

                val contaRequest = ContaDTO(
                    cliente = clienteCpf,
                    numero = novoNumero,
                    saldo = BigDecimal.ZERO,
                    limite = limite,
                    gerente = gerenteCpf,
                    criacao = LocalDateTime.now()
                )

                contaService.criar(novoNumero, contaRequest)

                val reply = SagaMessage(
                    sagaId = message.sagaId,
                    tipoSaga = message.tipoSaga,
                    acao = message.acao,
                    payload = message.payload,
                    sucesso = true
                )
                rabbitTemplate.convertAndSend(SAGA_EXCHANGE, "saga.reply", reply)
            } catch (e: Exception) {
                val errorReply = SagaMessage(
                    sagaId = message.sagaId,
                    tipoSaga = message.tipoSaga,
                    acao = message.acao,
                    payload = "Erro: \${e.message}",
                    sucesso = false
                )
                rabbitTemplate.convertAndSend(SAGA_EXCHANGE, "saga.reply", errorReply)
            }
        } 
        else if ("ROLLBACK_CONTA" == message.acao) {
            // Conta é o último passo do autocadastro, rollback conta seria muito raro se chamarmos depois dela.
            // Mas, caso implementado num fluxo diferente
            println("ROLLBACK_CONTA solicitado. Ação de compensação pode ser implementada se necessário.")
        }
    }

    private fun generateNumeroConta(): String {
        return UUID.randomUUID().toString().substring(0, 8).uppercase()
    }
}