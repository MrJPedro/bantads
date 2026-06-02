package com.bantads.conta_service.entity.leitura

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "transferenciaLeitura",
    indexes = [Index(name = "idx_conta", columnList = "conta_origem")])
class TransferenciaRead(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "conta_origem", nullable = false)
    var contaOrigem: String,

    @Column(name = "conta_origem_nome", nullable = false)
    var contaOrigemNome: String,

    @Column(name = "conta_destino", nullable = true)
    var contaDestino: String,

    @Column(name = "conta_destino_nome", nullable = true)
    var contaDestinoNome: String,

    @Column(name = "valor", nullable = false)
    var valor: BigDecimal,

    @Column(name = "data", nullable = false)
    var data: LocalDateTime,

    @Column(name = "tipo", nullable = false)
    var tipo: String,

    @Column(name = "saldoanterior", nullable = false)
    var saldoanterior: BigDecimal,

    @Column(name = "saldofinal", nullable = false)
    var saldofinal: BigDecimal

)