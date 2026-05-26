package com.bantads.conta_service.service

import com.bantads.conta_service.dto.ClienteEvent
import com.bantads.conta_service.dto.CriarContaDTO
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime

@Component
class ClienteEventListener(
    private val contaService: ContaService
) {

    @RabbitListener(queues = ["cliente-event-queue"])
    fun receberEvento(evento: ClienteEvent) {
        println("[RABBITMQ] Evento recebido em MS-Conta: tipo=${evento.tipo}, cpf=${evento.cpf}")

        if (evento.tipo == "aprovacao") {
            val numeroConta = contaService.gerarNumeroContaUnico()
            val limite = evento.salario?.let { contaService.calcularLimite(it) } ?: BigDecimal.ZERO
            val request = CriarContaDTO(
                cliente = evento.cpf,
                numero = numeroConta,
                saldo = BigDecimal.ZERO.setScale(2),
                limite = limite.setScale(2, RoundingMode.HALF_EVEN),
                gerente = evento.gerenteCpf ?: "SEM_GERENTE",
                criacao = LocalDateTime.now()
            )
            contaService.criar(numeroConta, request)
            println("[RABBITMQ] Conta criada para cliente aprovado ${evento.cpf}: $numeroConta")
        }
    }
}
