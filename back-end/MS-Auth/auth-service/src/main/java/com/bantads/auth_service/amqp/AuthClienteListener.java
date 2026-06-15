package com.bantads.auth_service.amqp;

import com.bantads.auth_service.DTOs.ClienteEvent;
import com.bantads.auth_service.DTOs.EmailDTO;
import com.bantads.auth_service.DTOs.SagaMessage;
import com.bantads.auth_service.DTOs.UsuarioDTO;
import com.bantads.auth_service.config.RabbitConfig;
import com.bantads.auth_service.models.TipoEmail;
import com.bantads.auth_service.services.UsuarioService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
public class AuthClienteListener {

    private final UsuarioService usuarioService;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public AuthClienteListener(UsuarioService usuarioService, RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.usuarioService = usuarioService;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitConfig.AUTH_CLIENTE_EVENT_QUEUE)
    public void onClienteEvent(ClienteEvent evento) throws Exception {
        System.out.println("[MS-AUTH] Evento de cliente recebido: " + evento);

        if (Objects.equals(evento.tipo(), "aprovacao")) {
            String senha = UUID.randomUUID().toString().substring(0, 8);

            String cpf = evento.cpf();
            String email = evento.email();
            String nome = evento.nome();

            UsuarioDTO usuarioDTO = new UsuarioDTO(cpf, "CLIENTE", email, nome, senha);

            usuarioService.editUsuario(usuarioDTO);
            System.out.println("[MS-AUTH] Senha atualizada com sucesso para CPF: " + cpf);

            EmailDTO emailDTO = new EmailDTO(usuarioDTO.nome(), usuarioDTO.login(), TipoEmail.APROVACAO, senha);
            rabbitTemplate.convertAndSend(RabbitConfig.NOTIFICATION_EXCHANGE,
                    "notification.email.cliente", emailDTO);
        }
    }
}
