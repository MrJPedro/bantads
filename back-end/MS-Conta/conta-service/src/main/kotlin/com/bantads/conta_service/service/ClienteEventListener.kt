package com.bantads.conta_service.service

import com.bantads.conta_service.dto.ClienteEvent
import com.bantads.conta_service.dto.criarContaDTO
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class ClienteEventListener(
    private val contaService: ContaService
) {

    @RabbitListener(queues = ["cliente-event-queue"])
    fun receberEvento(evento: ClienteEvent) {
        println("[RABBITMQ] Evento recebido em MS-Conta: tipo=${evento.tipo}, cpf=${evento.cpf}")

    // Faz cadastro quando recebe, só um teste. Gerente tem que aprovar a solicitação antes
    // Ver isso melhor dpois

        if (evento.tipo == "autocadastro") {
            val request = criarContaDTO(
                cliente = evento.cpf,
                saldo = BigDecimal.ZERO,
                limite = BigDecimal(1000),
                gerente = "TESTE"
            )
            contaService.criar(evento.cpf, request)
            println("[RABBITMQ] Conta criada automaticamente para cliente ${evento.cpf}")
        }
    }
}
