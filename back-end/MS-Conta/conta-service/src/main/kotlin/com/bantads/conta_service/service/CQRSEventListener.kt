package com.bantads.conta_service.service

import com.bantads.conta_service.dto.ClienteEvent
import com.bantads.conta_service.dto.ContaDTO
import com.bantads.conta_service.dto.ContaWriteDTO
import com.bantads.conta_service.dto.TransferenciaWriteDTO
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime

@Component
class CQRSEventListener(
    private val contaService: ContaService
) {

    @RabbitListener(queues = ["cqrs-event-queue"])
    fun receberConta(evento: ContaWriteDTO) {
        println("[RABBITMQ] Evento recebido em MS-Conta")
    }

    @RabbitListener(queues = ["cqrs-event-queue"])
    fun receberTransferencia(evento: TransferenciaWriteDTO) {
        println("[RABBITMQ] Evento recebido em MS-Conta")
    }
}
