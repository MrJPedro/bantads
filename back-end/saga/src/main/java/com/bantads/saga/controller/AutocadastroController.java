package com.bantads.saga.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/autocadastro")
public class AutocadastroController {

    @PostMapping
    public ResponseEntity<String> realizarAutocadastro() {
        // TODO: Implementar lógica de orquestração do autocadastro (envio de mensagens via RabbitMQ, controle de estado, etc.)
        
        return ResponseEntity.ok("Requisição de autocadastro recebida pelo SAGA (Em desenvolvimento)");
    }
}
