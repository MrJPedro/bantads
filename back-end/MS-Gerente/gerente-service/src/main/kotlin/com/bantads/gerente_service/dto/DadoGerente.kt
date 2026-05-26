package com.bantads.gerente_service.dto

data class DadoGerente(
    val id: Long?,
    val cpf: String,
    val nome: String,
    val email: String,
    val telefone: String,
    val quantidadeClientes: Int,
    val tipo: String = "GERENTE"
)

data class DadoGerenteInsercao(
    val cpf: String,
    val nome: String,
    val email: String,
    val telefone: String,
    val tipo: String? = null,
    val senha: String
)

data class DadoGerenteAtualizacao(
    val nome: String,
    val email: String,
    val senha: String? = null
)
