package com.bantads.conta_service.dto

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

// DTO para a resposta do Saldo atual (R3)
data class SaldoResponseDTO(
    val numeroConta: String,
    val saldo: BigDecimal
)

// DTO para representar cada linha de transação no Extrato (R8)
data class MovimentacaoDTO(
    val dataHora: LocalDateTime,
    val tipoOperacao: String, // DEPOSITO SAQUE TRANSFERENCIA
    val valor: BigDecimal,
    val clienteOrigem: String? = null,  
    val clienteDestino: String? = null
)

// DTO para representar o saldo consolidado de um dia específico (R8)
data class SaldoDiarioDTO(
    val data: LocalDate,
    val saldoConsolidado: BigDecimal
)

// DTO principal para a resposta do Extrato completo (R8)
data class ExtratoResponseDTO(
    val numeroConta: String,
    val dataInicio: LocalDate?,
    val dataFim: LocalDate?,
    val movimentacoes: List<MovimentacaoDTO>,
    val saldosDiarios: List<SaldoDiarioDTO> 
)