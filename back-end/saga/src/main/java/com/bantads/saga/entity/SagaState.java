package com.bantads.saga.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import com.bantads.saga.entity.TipoSaga;
import com.bantads.saga.entity.SagaStatus;

@Entity
@Table(name = "saga_state")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SagaState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID sagaId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoSaga tipoSaga; // Ex: AUTOCADASTRO, REMOCAO_GERENTE

    @Column(nullable = false)
    private String estadoAtual; // Ex: CRIACAO_AUTH_PENDENTE

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload; // JSON contendo os dados relevantes para a saga (Ex: DTO de Cliente)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SagaStatus status; // PENDENTE, SUCESSO, ERRO, REVERTIDO

    private LocalDateTime criadoEm;

    private LocalDateTime atualizadoEm;

    @PrePersist
    protected void onCreate() {
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }
}
