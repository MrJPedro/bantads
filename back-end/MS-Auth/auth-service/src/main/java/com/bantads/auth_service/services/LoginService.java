package com.bantads.auth_service.services;

import com.bantads.auth_service.DTOs.Login;
import com.bantads.auth_service.DTOs.UsuarioDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    @Autowired
    UsuarioService cadastroService;

    public Login autenticar(Login login){
        
        String emailEntrada = login.login();
        String senhaEntrada = login.senha();
        
        UsuarioDTO loginCadastrado = cadastroService.getCadastro(emailEntrada);

        if(emailEntrada == loginCadastrado.loginUsuario() && senhaEntrada == loginCadastrado.senhaUsuario()) return login;
        return null;
    }
}
