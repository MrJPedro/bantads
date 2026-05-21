package com.bantads.conta_service.entity.leitura

import com.bantads.conta_service.entity.comando.Conta
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
@Table(name = "transferenciaLeitura")
class Transferencia(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @JoinColumn(name = "conta_origem", nullable = false)
    var contaOrigem: String,

    @JoinColumn(name = "conta_destino", nullable = true)
    var contaDestino: String,

    @JoinColumn(name = "conta_destino_nome", nullable = true)
    var contaDestinoNome: String,

    @Column(name = "valor", nullable = false)
    var valor: BigDecimal,

    @Column(name = "data", nullable = false)
    var data: LocalDateTime,

    @Column(name = "saldofinal", nullable = false)
    var saldofinal: BigDecimal

)