package com.bantads.cliente_service.amqp

import com.bantads.cliente_service.config.CLIENTE_EMAIL_QUEUE
import com.bantads.cliente_service.dto.DadosClienteResponse
import com.bantads.cliente_service.dto.EmailDTO
import com.bantads.cliente_service.service.EmailService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class EmailNotificationListener(
    private val emailService: EmailService,
    private val objectMapper: ObjectMapper
) {

    @RabbitListener(queues = [CLIENTE_EMAIL_QUEUE])
    fun handleEmailNotification(emailDTO: EmailDTO) {
        emailService.notificarClienteEmail(tipo = emailDTO.tipo, email = emailDTO.email, nome = emailDTO.nome, atributo = emailDTO.atributo)
    }
}