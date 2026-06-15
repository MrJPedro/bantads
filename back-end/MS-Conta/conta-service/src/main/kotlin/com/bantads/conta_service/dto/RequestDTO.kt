package com.bantads.conta_service.dto

import com.fasterxml.jackson.annotation.JsonAlias
import java.math.BigDecimal
import java.time.LocalDateTime

data class DepositoRequestDTO(
    val valor: BigDecimal
)

data class SaqueRequestDTO(
    val valor: BigDecimal
)

data class TransferenciaRequestDTO(
    @JsonAlias("destino")
    val contaDestino: String,
    val valor: BigDecimal
)

data class ContaDTO(
    val cliente: String,
    val numero: String,
    val saldo: BigDecimal,
    val limite: BigDecimal,
    var gerente: String,
    val criacao: LocalDateTime
)
