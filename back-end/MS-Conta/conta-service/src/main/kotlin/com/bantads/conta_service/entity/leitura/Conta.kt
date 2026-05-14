package com.bantads.conta_service.entity.leitura

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "conta")
class Conta(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "cliente", nullable = false)
    var cliente: String,

    @Column(name = "numero", nullable = false, unique = true)
    var numero: String,

    @Column(name = "saldo", nullable = false)
    var saldo: BigDecimal,

    @Column(name = "limite", nullable = false)
    var limite: BigDecimal,

)