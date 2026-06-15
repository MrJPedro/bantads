package com.bantads.auth_service.amqp;

import com.bantads.auth_service.DTOs.GerenteEvent;
import com.bantads.auth_service.DTOs.UsuarioDTO;
import com.bantads.auth_service.config.RabbitConfig;
import com.bantads.auth_service.services.UsuarioService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AuthGerenteListener {

    private final UsuarioService usuarioService;

    public AuthGerenteListener(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @RabbitListener(queues = RabbitConfig.AUTH_GERENTE_EVENT_QUEUE)
    public void onGerenteEvent(GerenteEvent evento) throws Exception {
        System.out.println("[MS-AUTH] Evento de gerente recebido: " + evento);

        if (Objects.equals(evento.tipo(), "insercao") || Objects.equals(evento.tipo(), "alteracao")) {
            UsuarioDTO usuarioDTO = new UsuarioDTO(evento.cpfGerente(), "GERENTE", evento.email(), evento.nome(), evento.senha());
            usuarioService.editUsuarioGerente(usuarioDTO);
            System.out.println("[MS-AUTH] Gerente processado com sucesso: " + evento.cpfGerente());
        } else if (Objects.equals(evento.tipo(), "remocao")) {
            usuarioService.deleteUsuarioByCpf(evento.cpfGerente());
            System.out.println("[MS-AUTH] Gerente removido com sucesso: " + evento.cpfGerente());
        }
    }
}
