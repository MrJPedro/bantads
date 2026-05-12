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

const val CONTA_EVENT_QUEUE = "cliente-event-queue"
const val CONTA_EVENT_EXCHANGE = "cliente-event-exchange"
const val CONTA_EVENT_ROUTING_KEY = "cliente.event.#"

@Configuration
class RabbitConfig {

    @Bean
    fun clienteEventQueue(): Queue {
        return Queue(CONTA_EVENT_QUEUE, true)
    }

    @Bean
    fun clienteEventExchange(): TopicExchange {
        return TopicExchange(CONTA_EVENT_EXCHANGE)
    }

    @Bean
    fun binding(clienteEventQueue: Queue, clienteEventExchange: TopicExchange): Binding {
        return BindingBuilder.bind(clienteEventQueue)
            .to(clienteEventExchange)
            .with(CONTA_EVENT_ROUTING_KEY)
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
