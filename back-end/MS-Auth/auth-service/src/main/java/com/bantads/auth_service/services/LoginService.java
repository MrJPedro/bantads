package com.bantads.auth_service.services;

import com.bantads.auth_service.DTOs.Login;
import com.bantads.auth_service.DTOs.LoginResponse;
import com.bantads.auth_service.DTOs.UsuarioDTO;
import com.bantads.auth_service.models.Usuario;
import com.bantads.auth_service.models.UsuarioMapper;
import com.bantads.auth_service.repositories.UsuarioRepository;
import com.bantads.auth_service.utils.AuthUtil;
import com.bantads.auth_service.utils.EmailUtil;

import java.util.NoSuchElementException;

import javax.security.auth.login.LoginException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    EmailUtil emailUtil;

    @Autowired
    AuthUtil authUtil;

    @Autowired
    UsuarioMapper usuarioMapper;

    public UsuarioDTO autenticar(Login loginReq) throws NoSuchElementException, IllegalArgumentException, LoginException {
        
        String emailEntrada = loginReq.login();
        String senhaEntrada = loginReq.senha();
        String hashSenhaEntrada = authUtil.hashearSenha(senhaEntrada);
        
        boolean loginEhValido = emailUtil.validarEmail(loginReq.login());
        if(!loginEhValido) throw new IllegalArgumentException("Login inválido!");

        Usuario loginCadastrado = usuarioRepository.findByLogin(loginReq.login()).orElseThrow(() -> new NoSuchElementException(
            "Login não encontrado!"
        ));

        if (
            emailEntrada.equals(loginCadastrado.getLogin()) &&
            hashSenhaEntrada.equals(loginCadastrado.getHashSenha())
        ) {
            UsuarioDTO uDTO = usuarioMapper.toDTO(loginCadastrado);
            return uDTO;
        }

        throw new LoginException("Senha incorreta!");
    }
}
