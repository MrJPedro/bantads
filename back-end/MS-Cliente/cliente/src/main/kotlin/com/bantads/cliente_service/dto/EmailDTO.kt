package com.bantads.cliente_service.dto

import com.bantads.cliente_service.entity.TipoEmail

data class EmailDTO(
    val email: String,
    val nome: String,
    val tipo: TipoEmail,
    val atributo: String = ""
)