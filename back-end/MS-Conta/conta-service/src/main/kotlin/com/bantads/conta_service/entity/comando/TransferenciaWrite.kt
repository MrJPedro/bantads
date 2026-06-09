package com.bantads.conta_service.entity.comando

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "transferenciaComando")
class TransferenciaWrite(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "conta_origem_id", nullable = false)
    var contaOrigem: ContaWrite,

    @ManyToOne
    @JoinColumn(name = "conta_destino_id", nullable = true)
    var contaDestino: ContaWrite?,

    @Column(name = "valor", nullable = false, precision = 19, scale = 2)
    var valor: BigDecimal,

    @Column(name = "tipo", nullable = false)
    var tipo: String,

    @Column(name = "data", nullable = false)
    var data: LocalDateTime

)