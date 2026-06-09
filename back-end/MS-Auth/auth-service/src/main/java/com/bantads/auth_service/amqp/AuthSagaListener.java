package com.bantads.auth_service.amqp;

import com.bantads.auth_service.DTOs.SagaMessage;
import com.bantads.auth_service.DTOs.UsuarioDTO;
import com.bantads.auth_service.config.RabbitConfig;
import com.bantads.auth_service.services.UsuarioService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuthSagaListener {

    private final UsuarioService usuarioService;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public AuthSagaListener(UsuarioService usuarioService, RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.usuarioService = usuarioService;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitConfig.AUTH_COMMAND_QUEUE)
    public void onAuthCommand(SagaMessage command) {
        System.out.println("[MS-AUTH] Comando SAGA recebido: " + command.getAcao());

        try {
            switch (command.getAcao()) {
                case "CRIAR_AUTH":
                    // Como a senha só é enviada ao fim por email (após aprovação do gerente),
                    // O requisito R1 diz: A senha só é enviada após a aprovação.
                    // Nós geramos uma string aleatória temporária simples ou salvamos agora:
                    String senhaTemporaria = UUID.randomUUID().toString().substring(0, 8);
                    
                    JsonNode payloadNode = objectMapper.readTree(command.getPayload());
                    String cpf = payloadNode.get("cpf").asText();
                    String email = payloadNode.get("email").asText();

                    usuarioService.insertUsuario(
                        new UsuarioDTO(cpf, "CLIENTE", email, senhaTemporaria)
                    );
                    System.out.println("[MS-AUTH] Usuário de autenticacão criado com sucesso para CPF: " + cpf);

                    responderSaga(command, true, command.getPayload());
                    break;

                case "ROLLBACK_AUTH":
                    JsonNode rollbackNode = objectMapper.readTree(command.getPayload());
                    String emailRollback = rollbackNode.get("email").asText();
                    
                    usuarioService.deleteUsuario(emailRollback);
                    System.out.println("[MS-AUTH] Rollback concluído. Usuário deletado: " + emailRollback);

                    responderSaga(command, true, command.getPayload());
                    break;

                default:
                    System.out.println("[MS-AUTH] Ação desconhecida: " + command.getAcao());
            }
        } catch (Exception e) {
            System.err.println("[MS-AUTH] Erro ao processar comando SAGA: " + e.getMessage());
            responderSaga(command, false, "Erro interno MS-Auth: " + e.getMessage());
        }
    }

    private void responderSaga(SagaMessage command, boolean sucesso, String payload) {
        SagaMessage reply = new SagaMessage(
                command.getSagaId(),
                command.getTipoSaga(),
                command.getAcao(),
                sucesso,
                payload
        );
        rabbitTemplate.convertAndSend(RabbitConfig.SAGA_EXCHANGE, "saga.reply", reply);
    }
}