package com.bantads.conta_service.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "conta")
data class Conta(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "cliente", nullable = false)
    val cliente: String,

    @Column(name = "numero", nullable = false, unique = true)
    val numero: String,

    @Column(name = "saldo", nullable = false)
    var saldo: BigDecimal,

    @Column(name = "limite", nullable = false)
    var limite: BigDecimal,

    @Column(name = "gerente", nullable = false)
    var gerente: String,

    @Column(name = "criacao", nullable = false)
    val criacao: LocalDateTime
    
)