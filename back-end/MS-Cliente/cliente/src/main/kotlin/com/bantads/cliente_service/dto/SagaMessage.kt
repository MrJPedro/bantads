package com.bantads.cliente_service.dto

import java.util.UUID

data class SagaMessage(
    var sagaId: UUID? = null,
    var tipoSaga: String? = null,
    var acao: String? = null,
    var sucesso: Boolean? = null,
    var payload: String? = null
)