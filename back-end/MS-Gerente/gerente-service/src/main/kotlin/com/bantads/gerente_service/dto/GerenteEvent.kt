package com.bantads.gerente_service.dto

data class GerenteEvent(
    val tipo: String,
    val cpfGerente: String,
    val nome: String? = null,
    val email: String? = null,
    val senha: String? = null,
    val cpfNovoGerente: String? = null,
    val cpfGerenteAnterior: String? = null,
    val numeroConta: String? = null
)