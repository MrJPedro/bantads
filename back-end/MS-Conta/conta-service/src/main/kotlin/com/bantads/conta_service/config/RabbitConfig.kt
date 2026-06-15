package com.bantads.conta_service.config

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
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

const val CONTA_COMMAND_QUEUE = "conta-command-queue"
const val SAGA_EXCHANGE = "saga-exchange"
const val CONTA_COMMAND_ROUTING_KEY = "conta.command"

const val CONTA_GERENTE_EVENT_QUEUE = "conta-gerente-event-queue"
const val GERENTE_EVENT_EXCHANGE = "gerente-event-exchange"
const val GERENTE_EVENT_ROUTING_KEY = "gerente.event.#"

@Configuration
class RabbitConfig {

    @Bean
    fun contaCommandQueue(): Queue {
        return Queue(CONTA_COMMAND_QUEUE, true)
    }

    @Bean
    fun sagaExchange(): DirectExchange {
        return DirectExchange(SAGA_EXCHANGE)
    }

    @Bean
    fun contaCommandBinding(contaCommandQueue: Queue, sagaExchange: DirectExchange): Binding {
        return BindingBuilder.bind(contaCommandQueue)
            .to(sagaExchange)
            .with(CONTA_COMMAND_ROUTING_KEY)
    }

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
    fun contaGerenteEventQueue(): Queue {
        return Queue(CONTA_GERENTE_EVENT_QUEUE, true)
    }

    @Bean
    fun gerenteEventExchange(): TopicExchange {
        return TopicExchange(GERENTE_EVENT_EXCHANGE)
    }

    @Bean
    fun bindingContaGerenteEvent(contaGerenteEventQueue: Queue, gerenteEventExchange: TopicExchange): Binding {
        return BindingBuilder.bind(contaGerenteEventQueue)
            .to(gerenteEventExchange)
            .with(GERENTE_EVENT_ROUTING_KEY)
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