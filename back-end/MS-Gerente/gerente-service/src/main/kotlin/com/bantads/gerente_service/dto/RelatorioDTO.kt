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

data class DashboardGerenteItemDTO(
    val gerente: DadoGerente,
    val clientes: List<ContaDashboardDTO>,
    val saldo_positivo: BigDecimal,
    val saldo_negativo: BigDecimal
)

data class ContaDashboardDTO(
    val cliente: String,
    val numero: String,
    val saldo: BigDecimal,
    val limite: BigDecimal,
    val gerente: String,
    val criacao: String? = null
)

data class RelatorioClienteCompostoDTO(
    val cpf: String,
    val conta: String? = null,
    val saldo: BigDecimal? = null,
    val limite: BigDecimal? = null,
    val gerenteCpf: String? = null
)
