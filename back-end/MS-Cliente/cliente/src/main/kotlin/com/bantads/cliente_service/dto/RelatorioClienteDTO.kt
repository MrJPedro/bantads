package com.bantads.cliente_service.dto

import java.math.BigDecimal

data class RelatorioClienteDTO(
    val cpf: String,
    val nome: String,
    val email: String,
    val telefone: String,
    val salario: BigDecimal,
    val endereco: String,
    val cidade: String,
    val estado: String,
    val conta: String?,
    val saldo: BigDecimal?,
    val limite: BigDecimal?,
    val gerenteCpf: String?,
    val gerenteNome: String?,
    val gerenteEmail: String?
)
