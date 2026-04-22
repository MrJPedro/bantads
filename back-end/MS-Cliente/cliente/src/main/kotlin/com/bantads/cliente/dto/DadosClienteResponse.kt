package com.bantads.cliente.dto

data class DadosClienteResponse(
    val id: Long,
    val nome: String,
    val cpf: String,
    val email: String
)
