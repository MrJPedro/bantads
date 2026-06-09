package com.bantads.gerente_service.dto

import java.math.BigDecimal

data class ContaDTO(
    val cliente: String,
    val numero: String,
    val saldo: BigDecimal,
    val limite: BigDecimal,
    val gerente: String,
    val criacao: String? = null
)
