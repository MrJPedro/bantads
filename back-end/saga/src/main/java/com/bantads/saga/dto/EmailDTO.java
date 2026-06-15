package com.bantads.saga.dto;

import com.bantads.saga.entity.TipoEmail;

public record EmailDTO(
        String nome,
        String email,
        TipoEmail tipo,
        String atributo
) {
}

