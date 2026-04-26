package com.bantads.conta_service.dto

import java.math.BigDecimal

// DTO para a requisição de Depósito (R5)
data class DepositoRequestDTO(
    val valor: BigDecimal
)

// DTO para a requisição de Saque (R6)
data class SaqueRequestDTO(
    val valor: BigDecimal
)

// DTO para a requisição de Transferência (R7)
data class TransferenciaRequestDTO(
    val contaDestino: String,
    val valor: BigDecimal
)