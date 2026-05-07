package com.bantads.conta_service.entity

import java.time.LocalDateTime

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import java.math.BigDecimal

@Entity
@Table(name = "transferencia")
data class Transferencia(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "conta_origem_id", nullable = false)
    val contaOrigem: Conta,

    @Column(name = "conta_destino_id", nullable = false)
    val contaDestino: Conta,

    @Column(name = "valor", nullable = false)
    val valor: BigDecimal,

    @Column(name = "data", nullable = false)
    val data: LocalDateTime
    
)