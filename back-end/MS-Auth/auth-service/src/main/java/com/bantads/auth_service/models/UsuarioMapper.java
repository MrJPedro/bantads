package com.bantads.auth_service.models;

import com.bantads.auth_service.DTOs.UsuarioDTO;
import com.bantads.auth_service.models.Usuario;

import java.util.ArrayList;

import org.mapstruct.Mapper;

@Mapper(componentModel="spring")
public interface UsuarioMapper {
    UsuarioDTO toDTO(Usuario usuario);
    Usuario toUsuario(UsuarioDTO usuarioDTO);

    /*List<UsuarioDTO> toDTO(List<Usuario> usuarios) {
        ArrayList<UsuarioDTO> uDTOs = new ArrayList<>();
        
        for (Usuario usuario : usuarios) {
            uDTOs.add(
                this.toDTO(usuario)
            );
        }

        return uDTOs;
    };*/
}
