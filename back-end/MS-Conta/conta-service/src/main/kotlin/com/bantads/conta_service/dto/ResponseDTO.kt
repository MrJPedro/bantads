package com.bantads.conta_service.dto

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class SaldoResponseDTO(
    val numeroConta: String,
    val saldo: BigDecimal
)

data class MovimentacaoDTO(
    val dataHora: LocalDateTime,
    val tipoOperacao: String,
    val valor: BigDecimal,
    val clienteOrigem: String? = null,  
    val clienteDestino: String? = null
)

data class SaldoDiarioDTO(
    val data: LocalDate,
    val saldoConsolidado: BigDecimal
)

data class ExtratoResponseDTO(
    val numeroConta: String,
    val dataInicio: LocalDate?,
    val dataFim: LocalDate?,
    val movimentacoes: List<MovimentacaoDTO>,
    val saldosDiarios: List<SaldoDiarioDTO> 
)