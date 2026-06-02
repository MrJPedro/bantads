package com.bantads.gerente_service.dto

import java.io.Serializable

data class SagaMessage(
    var sagaId: java.util.UUID? = null,
    var tipoSaga: String? = null,
    var acao: String? = null,
    var sucesso: Boolean? = null,
    var payload: String? = null
) : Serializable