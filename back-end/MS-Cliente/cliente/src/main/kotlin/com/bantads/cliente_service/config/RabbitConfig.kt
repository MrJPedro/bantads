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

const val CLIENTE_EMAIL_QUEUE = "cliente-email-notification-queue"
const val NOTIFICATION_EXCHANGE = "notification-exchange"
const val CLIENTE_EMAIL_ROUTING_KEY = "notification.email.cliente"

const val CLIENTE_GERENTE_EVENT_QUEUE = "cliente-gerente-event-queue"
const val GERENTE_EVENT_EXCHANGE = "gerente-event-exchange"
const val GERENTE_EVENT_ROUTING_KEY = "gerente.event.#"

@Configuration
class RabbitConfig {

    @Bean
    fun clienteEventQueue(): Queue {
        return Queue(CLIENTE_EVENT_QUEUE, true)
    }

    // Fila que o MS Cliente escuta os comandos enviados pela Saga
    @Bean
    fun clienteCommandQueue(): Queue {
        return Queue("cliente-command-queue", true)
    }

    @Bean
    fun clienteEventExchange(): TopicExchange {
        return TopicExchange(CLIENTE_EVENT_EXCHANGE)
    }

    @Bean
    fun bindingClienteEvent(clienteEventQueue: Queue, clienteEventExchange: TopicExchange): Binding {
        return BindingBuilder.bind(clienteEventQueue)
            .to(clienteEventExchange)
            .with(CLIENTE_EVENT_ROUTING_KEY)
    }

    @Bean
    fun clienteEmailQueue(): Queue {
        return Queue(CLIENTE_EMAIL_QUEUE, true)
    }

    @Bean
    fun notificationExchange(): TopicExchange {
        return TopicExchange(NOTIFICATION_EXCHANGE)
    }

    @Bean
    fun bindingClienteEmail(clienteEmailQueue: Queue, notificationExchange: TopicExchange): Binding {
        return BindingBuilder.bind(clienteEmailQueue)
            .to(notificationExchange)
            .with(CLIENTE_EMAIL_ROUTING_KEY)
    }

    @Bean
    fun clienteGerenteEventQueue(): Queue {
        return Queue(CLIENTE_GERENTE_EVENT_QUEUE, true)
    }

    @Bean
    fun gerenteEventExchange(): TopicExchange {
        return TopicExchange(GERENTE_EVENT_EXCHANGE)
    }

    @Bean
    fun bindingClienteGerenteEvent(clienteGerenteEventQueue: Queue, gerenteEventExchange: TopicExchange): Binding {
        return BindingBuilder.bind(clienteGerenteEventQueue)
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