package com.bantads.gerente_service.config

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

/*Eventos publicados pelo MS-Gerente*/
const val GERENTE_EVENT_QUEUE = "gerente-event-queue"
const val GERENTE_EVENT_EXCHANGE = "gerente-event-exchange"
const val GERENTE_EVENT_ROUTING_KEY = "gerente.event.#"

/*Comandos recebidos da Saga pelo MS-Gerente*/
const val GERENTE_COMMAND_QUEUE = "gerente-command-queue"
const val GERENTE_COMMAND_ROUTING_KEY = "gerente.command"
const val SAGA_EXCHANGE = "saga-exchange"

@Configuration
class RabbitConfig {

    /*Fila onde o gerente recebe comandos da Saga*/
    @Bean
    fun gerenteCommandQueue(): Queue {
        return Queue(GERENTE_COMMAND_QUEUE, true)
    }

    /*Exchange usada pelo orquestrador de Saga*/
    @Bean
    fun sagaExchange(): DirectExchange {
        return DirectExchange(SAGA_EXCHANGE)
    }

    /*Fila de eventos emitidos pelo gerente*/
    @Bean
    fun gerenteEventQueue(): Queue {
        return Queue(GERENTE_EVENT_QUEUE, true)
    }

    /*Exchange de eventos do gerente*/
    @Bean
    fun gerenteEventExchange(): TopicExchange {
        return TopicExchange(GERENTE_EVENT_EXCHANGE)
    }

    /*Liga eventos gerente.event.* na fila de eventos do gerente*/
    @Bean
    fun binding(gerenteEventQueue: Queue, gerenteEventExchange: TopicExchange): Binding {
        return BindingBuilder.bind(gerenteEventQueue)
            .to(gerenteEventExchange)
            .with(GERENTE_EVENT_ROUTING_KEY)
    }

    /*Liga comandos gerente.command da Saga na fila do gerente*/
    @Bean
    fun gerenteCommandBinding(gerenteCommandQueue: Queue, sagaExchange: DirectExchange): Binding {
        return BindingBuilder.bind(gerenteCommandQueue)
            .to(sagaExchange)
            .with(GERENTE_COMMAND_ROUTING_KEY)
    }

    /*Converte mensagens RabbitMQ para JSON*/
    @Bean
    fun messageConverter(): MessageConverter {
        return Jackson2JsonMessageConverter()
    }

    /*Objeto usado pelos services/listeners para publicar mensagens*/
    @Bean
    fun rabbitTemplate(connectionFactory: ConnectionFactory, messageConverter: MessageConverter): RabbitTemplate {
        val template = RabbitTemplate(connectionFactory)
        template.messageConverter = messageConverter
        return template
    }
}
