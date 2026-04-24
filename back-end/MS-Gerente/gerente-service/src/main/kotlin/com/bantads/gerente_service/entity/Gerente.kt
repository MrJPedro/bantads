package com.bantads.gerente_service.entity

import jakarta.persistence.*
import java.io.Serializable

@Entity
@Table(name = "tb_gerente")
class GerenteEntity(
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true, length = 11)
    var cpf: String,

    @Column(nullable = false)
    var nome: String,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(length = 20)
    var telefone: String,

    @Column(name = "qtd_clientes", nullable = false)
    var quantidadeClientes: Int = 0

) : Serializable