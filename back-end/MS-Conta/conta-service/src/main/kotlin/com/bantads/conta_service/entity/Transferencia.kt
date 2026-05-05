package com.bantads.conta_service.entity

import java.time.LocalDateTime

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import java.math.BigDecimal

@Entity
data class Transferencia(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val contaOrigem: Conta,

    @Column(nullable = false)
    val contaDestino: Conta,

    @Column(nullable = false)
    val valor: BigDecimal,

    @Column(nullable = false)
    val data: LocalDateTime
    
)