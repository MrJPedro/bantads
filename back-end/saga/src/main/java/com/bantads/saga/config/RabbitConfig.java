package com.bantads.saga.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String SAGA_EXCHANGE = "saga-exchange";
    
    // Fila onde o SAGA recebe os pedidos para INICIAR uma nova orquestração
    public static final String SAGA_REQUEST_QUEUE = "saga-request-queue";

    // Fila onde o SAGA escuta as respostas dos microsserviços
    public static final String SAGA_REPLY_QUEUE = "saga-reply-queue";
    
    // Filas para onde o SAGA envia os comandos (e cada MS escuta a sua)
    public static final String CLIENTE_COMMAND_QUEUE = "cliente-command-queue";
    public static final String CONTA_COMMAND_QUEUE = "conta-command-queue";
    public static final String GERENTE_COMMAND_QUEUE = "gerente-command-queue";
    public static final String AUTH_COMMAND_QUEUE = "auth-command-queue";

    @Bean
    public DirectExchange sagaExchange() {
        return new DirectExchange(SAGA_EXCHANGE);
    }

    // Configurando as Filas
    @Bean public Queue sagaRequestQueue() { return new Queue(SAGA_REQUEST_QUEUE, true); }
    @Bean public Queue sagaReplyQueue() { return new Queue(SAGA_REPLY_QUEUE, true); }
    @Bean public Queue clienteCommandQueue() { return new Queue(CLIENTE_COMMAND_QUEUE, true); }
    @Bean public Queue contaCommandQueue() { return new Queue(CONTA_COMMAND_QUEUE, true); }
    @Bean public Queue gerenteCommandQueue() { return new Queue(GERENTE_COMMAND_QUEUE, true); }
    @Bean public Queue authCommandQueue() { return new Queue(AUTH_COMMAND_QUEUE, true); }

    // Bindings: Cada MS terá sua routing key
    @Bean public Binding requestBinding(Queue sagaRequestQueue, DirectExchange sagaExchange) {
        return BindingBuilder.bind(sagaRequestQueue).to(sagaExchange).with("saga.request");
    }
    @Bean public Binding replyBinding(Queue sagaReplyQueue, DirectExchange sagaExchange) {
        return BindingBuilder.bind(sagaReplyQueue).to(sagaExchange).with("saga.reply");
    }
    @Bean public Binding clienteBinding(Queue clienteCommandQueue, DirectExchange sagaExchange) {
        return BindingBuilder.bind(clienteCommandQueue).to(sagaExchange).with("cliente.command");
    }
    @Bean public Binding contaBinding(Queue contaCommandQueue, DirectExchange sagaExchange) {
        return BindingBuilder.bind(contaCommandQueue).to(sagaExchange).with("conta.command");
    }
    @Bean public Binding gerenteBinding(Queue gerenteCommandQueue, DirectExchange sagaExchange) {
        return BindingBuilder.bind(gerenteCommandQueue).to(sagaExchange).with("gerente.command");
    }
    @Bean public Binding authBinding(Queue authCommandQueue, DirectExchange sagaExchange) {
        return BindingBuilder.bind(authCommandQueue).to(sagaExchange).with("auth.command");
    }

    // Converter para JSON para enviar os DTOs diretamente
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