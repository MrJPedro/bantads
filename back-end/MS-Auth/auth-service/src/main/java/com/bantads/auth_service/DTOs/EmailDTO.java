package com.bantads.auth_service.DTOs;

import com.bantads.auth_service.models.TipoEmail;

public record EmailDTO(
        String nome,
        String email,
        TipoEmail tipo,
        String atributo
) {
}
