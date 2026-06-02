package com.bantads.auth_service.models;

import com.bantads.auth_service.DTOs.UsuarioDTO;
//import com.bantads.auth_service.models.Usuario;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;;;


public class UsuarioMapper {

    @Autowired
    ModelMapper modelMapper;

    public UsuarioDTO toDTO(Usuario usuario){
        return  modelMapper.map(usuario, UsuarioDTO.class);
    };

    public List<UsuarioDTO> toDTOs(List<Usuario> usuarios){
        return usuarios.stream()
            .map((Usuario usuario) -> this.toDTO(usuario))
            .toList();
    };

    public Usuario toUsuario(UsuarioDTO usuarioDTO){
        return modelMapper.map(usuarioDTO, Usuario.class);
    };

    public List<Usuario> toUsuarios(List<UsuarioDTO> usuarioDTOs){
        return usuarioDTOs.stream()
            .map((UsuarioDTO usuarioDTO) -> this.toUsuario(usuarioDTO))
            .toList();
    }