package com.bantads.cliente_service.dto

data class PerfilInfo(
    val nome: String,
    val email: String,
    val telefone: String? = null,
    val salario: Number,
    val endereco: String? = null,
    val CEP: String? = null,
    val cep: String? = null,
    val cidade: String? = null,
    val estado: String? = null
)
