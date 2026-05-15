package com.bantads.gerente_service.dto

import java.math.BigDecimal

data class RelatorioClienteDTO(
    val cpf: String,
    val nome: String,
    val cidade: String,
    val estado: String,
    val saldo: BigDecimal,
    val limite: BigDecimal
)

data class DashboardAdminDTO(
    val nome: String,
    val cpf: String,
    val quantidadeClientes: Int,
    val saldoPositivo: BigDecimal,
    val saldoNegativo: BigDecimal
)