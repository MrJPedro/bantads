package com.bantads.cliente_service.entity

import jakarta.persistence.*
import java.math.BigDecimal

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

    @Column(nullable = false)
    var salario: BigDecimal,

    @Column(nullable = false)
    var endereco: String,

    @Column(nullable = false, length = 8)
    var cep: String,

    @Column(nullable = false)
    var cidade: String,

    @Column(nullable = false)
    var estado: String
)
