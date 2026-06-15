package com.bantads.gerente_service.dto

/*DTO de resposta usado para listar/consultar gerentes*/
data class DadoGerente(
    val id: Long?,
    val cpf: String,
    val nome: String,
    val email: String,
    val telefone: String,
    val quantidadeClientes: Int,
    val tipo: String = "GERENTE"
)

/*DTO de entrada do R17 - cadastro de gerente pelo administrador*/
data class DadoGerenteInsercao(
    val cpf: String,
    val nome: String,
    val email: String,
    val telefone: String? = null,
    val tipo: String? = null,
    val senha: String
)

/*DTO de entrada do R20 - alteracao de nome, email e senha*/
data class DadoGerenteAtualizacao(
    val nome: String,
    val email: String,
    val senha: String? = null
)
