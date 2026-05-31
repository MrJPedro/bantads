package com.bantads.gerente_service.dto

import java.io.Serializable

data class SagaMessage(
    var sagaId: String = "",
    var acao: String? = null,
    var dados: Map<String, Any>? = null,
    var erro: String? = null,
    var sucesso: Boolean = false
) : Serializable