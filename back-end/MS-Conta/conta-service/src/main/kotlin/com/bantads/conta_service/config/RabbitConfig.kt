package com.bantads.conta_service.config

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

const val CQRS_EVENT_QUEUE = "cqrs-event-queue"
const val CQRS_EVENT_EXCHANGE = "cqrs-event-exchange"
const val CQRS_EVENT_ROUTING_KEY = "cqrs.event.#"

@Configuration
class RabbitConfig {

    @Bean
    fun clienteEventQueue(): Queue {
        return Queue(CLIENTE_EVENT_QUEUE, true)
    }

    @Bean
    fun clienteEventExchange(): TopicExchange {
        return TopicExchange(CLIENTE_EVENT_EXCHANGE)
    }

    @Bean
    fun clienteBinding(clienteEventQueue: Queue, clienteEventExchange: TopicExchange): Binding {
        return BindingBuilder.bind(clienteEventQueue)
            .to(clienteEventExchange)
            .with(CLIENTE_EVENT_ROUTING_KEY)
    }

    @Bean
    fun cqrsEventQueue(): Queue {
        return Queue(CQRS_EVENT_QUEUE, true)
    }

    @Bean
    fun cqrsEventExchange(): TopicExchange {
        return TopicExchange(CQRS_EVENT_EXCHANGE)
    }

    @Bean
    fun cqrsBinding(cqrsEventQueue: Queue, cqrsEventExchange: TopicExchange): Binding {
        return BindingBuilder.bind(cqrsEventQueue)
            .to(cqrsEventExchange)
            .with(CQRS_EVENT_ROUTING_KEY)
    }

    @Bean
    fun messageConverter(): MessageConverter {
        return Jackson2JsonMessageConverter()
    }

    @Bean
    fun rabbitTemplate(connectionFactory: ConnectionFactory, messageConverter: MessageConverter): RabbitTemplate {
        val rabbitTemplate = RabbitTemplate(connectionFactory)
        rabbitTemplate.messageConverter = messageConverter
        return rabbitTemplate
    }
}
