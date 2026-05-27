package com.bantads.conta_service.dto

import com.bantads.conta_service.entity.comando.Conta
import jakarta.persistence.Column
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
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
    val contaOrigem: Conta,
    val contaDestino: Conta?,
    val valor: BigDecimal,
    val saldofinal: BigDecimal,
    val data: LocalDateTime
)

