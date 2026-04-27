package com.bantads.cliente_service.dto

data class DadosClienteResponse(
    val id: Long,
    val nome: String,
    val cpf: String,
    val email: String,
    val telefone: String,
    val salario: Number
)
