package com.bantads.auth_service.models;

import com.bantads.auth_service.DTOs.UsuarioDTO;
//import com.bantads.auth_service.models.Usuario;

import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class UsuarioMapper {

    @Autowired
    ModelMapper modelMapper;

    /*public UsuarioDTO toDTO(Usuario usuario){
        return  modelMapper.map(usuario, UsuarioDTO.class);
    };*/

    public UsuarioDTO toDTO(Usuario usuario) {
    return new UsuarioDTO(
        usuario.getCpfUsuario(),
        usuario.getTipoUsuario(),
        usuario.getLogin(),
        null);
}

    public List<UsuarioDTO> toDTOs(List<Usuario> usuarios){
        return usuarios.stream()
            .map((Usuario usuario) -> this.toDTO(usuario))
            .toList();
    };

    /*public Usuario toUsuario(UsuarioDTO usuarioDTO){
        return modelMapper.map(usuarioDTO, Usuario.class);
    };*/

    public Usuario toUsuario(UsuarioDTO usuarioDTO){
        return new Usuario(
            usuarioDTO.cpf(),
            usuarioDTO.tipo(),
            usuarioDTO.login(),
            null);
    }

    public List<Usuario> toUsuarios(List<UsuarioDTO> usuarioDTOs){
        return usuarioDTOs.stream()
            .map((UsuarioDTO usuarioDTO) -> this.toUsuario(usuarioDTO))
            .toList();
    }
}