package com.bantads.saga.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bantads.saga.repository.SagaStateRepository;

@RestController
public class SagaController {

    @Autowired
    private SagaStateRepository sagaStateRepository;

    @GetMapping("/reboot")
    public ResponseEntity<?> reboot() {
        try {
            sagaStateRepository.deleteAll();
            System.out.println("Tabela de estados da Saga reiniciada com sucesso.");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            System.err.println("Erro ao reiniciar tabela da Saga: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
