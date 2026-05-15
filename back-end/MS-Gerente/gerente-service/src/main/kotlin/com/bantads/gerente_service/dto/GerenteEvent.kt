package com.bantads.gerente_service.dto

data class GerenteEvent(
    val tipo: String,
    val cpfGerente: String,
    val cpfNovoGerente: String? = null
)