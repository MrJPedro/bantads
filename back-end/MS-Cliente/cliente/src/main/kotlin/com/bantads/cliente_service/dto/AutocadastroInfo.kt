package com.bantads.cliente_service.dto

data class AutocadastroInfo(
    val nome: String,
    val email: String,
    val cpf: String,
    val telefone: String,
    val salario: Double,
    val endereco: String,
    val CEP: String,
    val cidade: String,
    val estado: String
)
