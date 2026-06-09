package com.bantads.auth_service.DTOs;

import java.io.Serializable;
import java.util.UUID;

public class SagaMessage implements Serializable {

    private UUID sagaId;
    private String tipoSaga;
    private String acao;
    private Boolean sucesso;
    private String payload;

    public SagaMessage() {}

    public SagaMessage(UUID sagaId, String tipoSaga, String acao, Boolean sucesso, String payload) {
        this.sagaId = sagaId;
        this.tipoSaga = tipoSaga;
        this.acao = acao;
        this.sucesso = sucesso;
        this.payload = payload;
    }

    public UUID getSagaId() { return sagaId; }
    public void setSagaId(UUID sagaId) { this.sagaId = sagaId; }
    public String getTipoSaga() { return tipoSaga; }
    public void setTipoSaga(String tipoSaga) { this.tipoSaga = tipoSaga; }
    public String getAcao() { return acao; }
    public void setAcao(String acao) { this.acao = acao; }
    public Boolean getSucesso() { return sucesso; }
    public void setSucesso(Boolean sucesso) { this.sucesso = sucesso; }
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
}