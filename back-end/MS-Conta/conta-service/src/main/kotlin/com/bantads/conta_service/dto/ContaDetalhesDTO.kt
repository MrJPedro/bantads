package com.bantads.conta_service.dto

import java.math.BigDecimal

data class ContaDetalhesDTO(
    val cliente: String,
    val numero: String,
    val saldo: BigDecimal,
    val limite: BigDecimal,
    val gerente: String?,
    val criacao: String?
)
