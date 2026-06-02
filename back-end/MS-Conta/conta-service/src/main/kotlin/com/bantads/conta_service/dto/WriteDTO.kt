package com.bantads.conta_service.dto

import java.math.BigDecimal
import java.time.LocalDateTime

data class ContaWriteDTO (
    val cliente: String,
    val numero: String,
    val saldo: BigDecimal,
    val limite: BigDecimal,
    var gerente: String,
    val criacao: LocalDateTime
)

data class TransferenciaWriteDTO(
    val contaOrigem: String,
    val contaOrigemNome: String,
    val contaDestino: String,
    val contaDestinoNome: String,
    val tipo: String,
    val valor: BigDecimal,
    val saldofinal: BigDecimal,
    val saldoanterior: BigDecimal,
    val data: LocalDateTime
)

