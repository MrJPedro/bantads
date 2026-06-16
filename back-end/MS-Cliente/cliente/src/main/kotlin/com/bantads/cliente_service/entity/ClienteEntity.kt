package com.bantads.cliente_service.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "tb_cliente")
class ClienteEntity (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var nome: String,

    @Column(nullable = false, unique = true, length = 11)
    var cpf: String,

    @Column(nullable = false)
    var email: String,

    @Column(nullable = false, length = 20)
    var telefone: String,

    @Column(nullable = false, precision = 19, scale = 2)
    var salario: BigDecimal,

    @Column(nullable = false)
    var endereco: String,

    @Column(nullable = false, length = 8)
    var cep: String,

    @Column(nullable = false)
    var cidade: String,

    @Column(nullable = false)
    var estado: String,

    @Column(nullable = true, length = 11)
    var gerenteCpf: String? = null,

    @Column(nullable = true, columnDefinition = "TEXT")
    var motivoRejeicao: String? = null,

    @Column(nullable = true)
    var dataRejeicao: LocalDateTime? = null,

    @Column(nullable = false)
    var status: String = "AGUARDANDO_APROVACAO" // AGUARDANDO_APROVACAO, APROVADO, REJEITADO
)
