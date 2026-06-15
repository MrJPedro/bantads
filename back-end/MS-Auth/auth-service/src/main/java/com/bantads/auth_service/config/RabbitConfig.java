package com.bantads.auth_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String AUTH_COMMAND_QUEUE = "auth-command-queue";
    public static final String AUTH_CLIENTE_EVENT_QUEUE = "auth-cliente-event-queue";
    public static final String AUTH_GERENTE_EVENT_QUEUE = "auth-gerente-event-queue";
    public static final String SAGA_EXCHANGE = "saga-exchange";
    public static final String NOTIFICATION_EXCHANGE = "notification-exchange";

    @Bean
    public Queue authCommandQueue() {
        return new Queue(AUTH_COMMAND_QUEUE, true);
    }

    @Bean
    public Queue authClienteEventQueue() {
        return new Queue(AUTH_CLIENTE_EVENT_QUEUE, true); // Fila exclusiva do auth
    }

    @Bean
    public Binding bindingAuthClienteEvent(Queue authClienteEventQueue, TopicExchange clienteEventExchange) {
        return BindingBuilder.bind(authClienteEventQueue)
                .to(clienteEventExchange)
                .with("cliente.event.#"); // Escuta tudo que for evento de cliente
    }

    @Bean
    public Queue authGerenteEventQueue() {
        return new Queue(AUTH_GERENTE_EVENT_QUEUE, true);
    }

    @Bean
    public Binding bindingAuthGerenteEvent(Queue authGerenteEventQueue, TopicExchange gerenteEventExchange) {
        return BindingBuilder.bind(authGerenteEventQueue)
                .to(gerenteEventExchange)
                .with("gerente.event.#"); // Escuta tudo que for evento de gerente
    }

    @Bean
    public TopicExchange clienteEventExchange() {
        return new TopicExchange("cliente-event-exchange");
    }

    @Bean
    public TopicExchange gerenteEventExchange() {
        return new TopicExchange("gerente-event-exchange");
    }

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}