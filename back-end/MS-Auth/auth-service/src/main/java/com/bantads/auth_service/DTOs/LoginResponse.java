package com.bantads.auth_service.DTOs;

public record LoginResponse(
    String login,
    String tipoUsuario
) {}