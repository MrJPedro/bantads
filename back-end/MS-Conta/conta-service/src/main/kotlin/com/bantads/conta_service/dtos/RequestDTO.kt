package com.bantads.conta_service.dtos

import java.math.BigDecimal

data class DepositoRequestDTO(
    val valor: BigDecimal
)

data class SaqueRequestDTO(
    val valor: BigDecimal
)

data class TransferenciaRequestDTO(
    val contaDestino: String,
    val valor: BigDecimal
)