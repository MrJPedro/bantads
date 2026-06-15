package com.bantads.conta_service.service

import com.bantads.conta_service.config.CONTA_GERENTE_EVENT_QUEUE
import com.bantads.conta_service.dto.GerenteEvent
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class GerenteEventListener(
    private val contaService: ContaService
) {

    @RabbitListener(queues = [CONTA_GERENTE_EVENT_QUEUE])
    fun onGerenteEvent(evento: GerenteEvent) {
        println("[MS-CONTA] Evento de gerente recebido: ${evento.tipo}")
        if (evento.tipo == "remocao" && evento.cpfNovoGerente != null) {
            contaService.remanejarContas(evento.cpfGerente, evento.cpfNovoGerente)
            println("[MS-CONTA] Contas do gerente ${evento.cpfGerente} transferidas para ${evento.cpfNovoGerente}")
        }
    }
}
