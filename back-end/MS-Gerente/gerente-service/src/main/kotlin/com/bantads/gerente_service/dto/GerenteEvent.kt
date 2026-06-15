package com.bantads.gerente_service.dto

/*Evento publicado pelo MS-Gerente para integracao via RabbitMQ*/
data class GerenteEvent(
    /*insercao, alteracao ou remocao*/
    val tipo: String,

    /*CPF do gerente principal do evento*/
    val cpfGerente: String,

    /*Dados usados principalmente em insercao/alteracao*/
    val nome: String? = null,
    val email: String? = null,
    val senha: String? = null,

    /*Gerente que deve assumir contas em uma remocao*/
    val cpfNovoGerente: String? = null,

    /*Campos para eventos de transferencia de conta*/
    val cpfGerenteAnterior: String? = null,
    val numeroConta: String? = null
)
