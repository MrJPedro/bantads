package com.bantads.saga.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "saga_autocadastro")
public class SagaAutocadastro {

    @Id
    private String correlationId; // UUID gerado no início da requisição

    private String cpfCliente;

    @Enumerated(EnumType.STRING)
    private SagaStatus status; // INICIADO, CLIENTE_CRIADO, AUTH_CRIADO, CONCLUIDO, FALHA_AUTH, ABORTADO

    private LocalDateTime dataAtualizacao;

    // Getters e Setters
}
