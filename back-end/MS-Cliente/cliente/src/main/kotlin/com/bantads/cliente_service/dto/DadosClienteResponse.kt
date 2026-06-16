package com.bantads.cliente_service.dto

import java.time.LocalDateTime

data class DadosClienteResponse(
    val id: Long,
    val nome: String,
    val cpf: String,
    val email: String,
    val telefone: String,
    val salario: Number,
    val endereco: String? = null,
    val cep: String? = null,
    val cidade: String? = null,
    val estado: String? = null,
    val gerenteCpf: String? = null,
    val motivoRejeicao: String? = null,
    val dataRejeicao: LocalDateTime? = null
)
