package com.bantads.auth_service.DTOs;

public record GerenteEvent(
    String tipo,
    String cpfGerente,
    String nome,
    String email,
    String senha,
    String cpfNovoGerente,
    String cpfGerenteAnterior,
    String numeroConta
) {}
