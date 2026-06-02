package com.bantads.gerente_service.config

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

const val GERENTE_EVENT_QUEUE = "gerente-event-queue"
const val GERENTE_EVENT_EXCHANGE = "gerente-event-exchange"
const val GERENTE_EVENT_ROUTING_KEY = "gerente.event.#"

const val GERENTE_COMMAND_QUEUE = "gerente-command-queue"
const val SAGA_EXCHANGE = "saga-exchange"

@Configuration
class RabbitConfig {

    @Bean
    fun gerenteCommandQueue(): Queue {
        return Queue(GERENTE_COMMAND_QUEUE, true)
    }

    @Bean
    fun sagaExchange(): TopicExchange {
        return TopicExchange(SAGA_EXCHANGE)
    }

    @Bean
    fun gerenteEventQueue(): Queue {
        return Queue(GERENTE_EVENT_QUEUE, true)
    }

    @Bean
    fun gerenteEventExchange(): TopicExchange {
        return TopicExchange(GERENTE_EVENT_EXCHANGE)
    }

    @Bean
    fun binding(gerenteEventQueue: Queue, gerenteEventExchange: TopicExchange): Binding {
        return BindingBuilder.bind(gerenteEventQueue)
            .to(gerenteEventExchange)
            .with(GERENTE_EVENT_ROUTING_KEY)
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