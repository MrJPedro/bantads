package com.bantads.cliente_service.amqp

import com.bantads.cliente_service.config.CLIENTE_GERENTE_EVENT_QUEUE
import com.bantads.cliente_service.dto.GerenteEvent
import com.bantads.cliente_service.service.ClienteService
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class GerenteEventListener(
    private val clienteService: ClienteService
) {

    @RabbitListener(queues = [CLIENTE_GERENTE_EVENT_QUEUE])
    fun onGerenteEvent(evento: GerenteEvent) {
        println("[MS-CLIENTE] Evento de gerente recebido: ${evento.tipo}")
        if (evento.tipo == "remocao" && evento.cpfNovoGerente != null) {
            clienteService.remanejarClientes(evento.cpfGerente, evento.cpfNovoGerente)
            println("[MS-CLIENTE] Clientes do gerente ${evento.cpfGerente} transferidos para ${evento.cpfNovoGerente}")
        }
    }
}
