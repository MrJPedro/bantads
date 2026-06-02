package com.bantads.cliente_service.config

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

const val CLIENTE_EVENT_QUEUE = "cliente-event-queue"
const val CLIENTE_EVENT_EXCHANGE = "cliente-event-exchange"
const val CLIENTE_EVENT_ROUTING_KEY = "cliente.event.#"

@Configuration
class RabbitConfig {

    @Bean
    fun clienteEventQueue(): Queue {
        return Queue(CLIENTE_EVENT_QUEUE, true)
    }

    // Fila que o MS Cliente escutara da Saga
    @Bean
    fun clienteCommandQueue(): Queue {
        return Queue("cliente-command-queue", true)
    }

    @Bean
    fun clienteEventExchange(): TopicExchange {
        return TopicExchange(CLIENTE_EVENT_EXCHANGE)
    }

    @Bean
    fun binding(clienteEventQueue: Queue, clienteEventExchange: TopicExchange): Binding {
        return BindingBuilder.bind(clienteEventQueue)
            .to(clienteEventExchange)
            .with(CLIENTE_EVENT_ROUTING_KEY)
    }

    @Bean
    fun messageConverter(): MessageConverter {
        return Jackson2JsonMessageConverter()
    }

    @Bean
    fun rabbitTemplate(connectionFactory: ConnectionFactory, messageConverter: MessageConverter): RabbitTemplate {
        val template = RabbitTemplate(connectionFactory)
        template.messageConverter = messageConverter
        return template
    }
}
