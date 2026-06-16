package com.bantads.auth_service.DTOs;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ClienteEvent(
    String tipo,
    String cpf,
    String nome,
    String email,
    String telefone,
    BigDecimal salario,
    String status,
    String motivo,
    LocalDateTime dataRejeicao,
    String gerenteCpf
    ) {
}
