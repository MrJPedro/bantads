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
    
    @Column(name = "cliente", nullable = false)
    val cliente: String,

    @Column(name = "numero", nullable = false)
    val numero: String,

    @Column(name = "saldo", nullable = false)
    val saldo: BigDecimal,

    @Column(name = "limite", nullable = false)
    val limite: BigDecimal,

    @Column(name = "gerente", nullable = false)
    val gerente: String,

    @Column(name = "criacao", nullable = false)
    val criacao: LocalDateTime
    
)