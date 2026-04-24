package com.bantads.gerente_service.dto

data class DadoGerente(
    val id: Long?,
    val cpf: String,
    val nome: String,
    val email: String,
    val telefone: String,
    val quantidadeClientes: Int
)

data class DadoGerenteInsercao(
    val cpf: String,
    val nome: String,
    val email: String,
    val telefone: String,
    val senha: String
)

data class DadoGerenteAtualizacao(
    val nome: String,
    val email: String,
    val senha: String? = null
)