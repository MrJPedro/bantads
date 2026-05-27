package com.bantads.conta_service.service

import com.bantads.conta_service.dto.ClienteEvent
import com.bantads.conta_service.dto.ContaDTO
import com.bantads.conta_service.dto.ContaWriteDTO
import com.bantads.conta_service.dto.TransferenciaWriteDTO
import org.springframework.amqp.rabbit.annotation.RabbitHandler
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime

@Component
@RabbitListener(queues = ["cqrs-event-queue"])
class CQRSEventListener(
    private val contaService: ContaService,
    private val transferenciaService: TransferenciaService
) {
    @RabbitHandler
    fun receberConta(evento: ContaWriteDTO) {
        println("[RABBITMQ] Conta recebida do CQRS")

        contaService.criarContaRead(evento)
    }

    @RabbitHandler
    fun receberTransferencia(evento: TransferenciaWriteDTO) {
        println("[RABBITMQ] Transferencia recebida do CQRS")
    }
}
