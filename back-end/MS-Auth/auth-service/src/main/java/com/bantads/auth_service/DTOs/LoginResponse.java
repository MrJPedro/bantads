package com.bantads.auth_service.DTOs;

public record LoginResponse(
    String token,
    String cpf,
    String tipo
) {}