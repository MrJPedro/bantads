package com.bantads.conta_service.dto

import java.math.BigDecimal
import java.time.LocalDateTime

data class ClienteEvent(
    val tipo: String,
    val cpf: String,
    val nome: String?,
    val email: String?,
    val telefone: String?,
    val salario: BigDecimal?,
    val status: String?,
    val motivo: String?,
    val dataRejeicao: LocalDateTime? = null,
    val gerenteCpf: String? = null
)
