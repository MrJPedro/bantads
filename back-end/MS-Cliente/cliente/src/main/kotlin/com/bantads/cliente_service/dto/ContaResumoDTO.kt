package com.bantads.cliente_service.dto

import java.math.BigDecimal

data class ContaResumoDTO(
    val cliente: String,
    val numero: String,
    val saldo: BigDecimal,
    val limite: BigDecimal,
    val gerente: String?,
    val criacao: String?
)
