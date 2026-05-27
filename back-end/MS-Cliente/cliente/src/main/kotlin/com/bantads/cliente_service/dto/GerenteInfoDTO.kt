package com.bantads.cliente_service.dto

data class GerenteInfoDTO(
    val cpf: String,
    val nome: String,
    val email: String,
    val tipo: String,
    val quantidadeClientes: Int
)
