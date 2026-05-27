package com.bantads.conta_service.entity.comando

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "contaComando")
class Conta(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "cliente", nullable = false)
    var cliente: String,

    @Column(name = "numero", nullable = false, unique = true)
    var numero: String,

    @Column(name = "saldo", nullable = false, precision = 19, scale = 2)
    var saldo: BigDecimal,

    @Column(name = "limite", nullable = false, precision = 19, scale = 2)
    var limite: BigDecimal,

    @Column(name = "gerente", nullable = false)
    var gerente: String,

    @Column(name = "criacao", nullable = false)
    var criacao: LocalDateTime

)