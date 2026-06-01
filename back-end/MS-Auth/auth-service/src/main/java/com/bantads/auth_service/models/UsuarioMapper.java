package com.bantads.auth_service.models;

import com.bantads.auth_service.DTOs.UsuarioDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel="spring")
public interface UsuarioMapper {
    @Mapping(target = "cpf", source = "cpfUsuario")
    @Mapping(target = "tipo", source = "tipoUsuario")
    @Mapping(target = "login", source = "login")
    UsuarioDTO toDTO(Usuario usuario);

    @Mapping(target = "cpfUsuario", source = "cpf")
    @Mapping(target = "tipoUsuario", source = "tipo")
    @Mapping(target = "login", source = "login")
    @Mapping(target = "senha", ignore = true)
    Usuario toUsuario(UsuarioDTO usuarioDTO);
}
