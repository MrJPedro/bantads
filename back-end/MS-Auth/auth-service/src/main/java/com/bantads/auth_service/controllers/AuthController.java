package com.bantads.auth_service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.util.List;
import com.bantads.auth_service.DTOs.Cadastro;
import com.bantads.auth_service.services.CadastroService;
import com.bantads.auth_service.DTOs.Login;
import com.bantads.auth_service.services.LoginService;

@CrossOrigin
@RestController
public class AuthController {
    
    private final LoginService loginService;
    @Autowired
    CadastroService cadastroService;
    
    public AuthController(LoginService loginService){
        this.loginService = loginService;}

    // ===== Pronto para uso com dados mockados =====
    @GetMapping("/cadastros")
    public List<Cadastro> getAllCadastros(){
        
        List<Cadastro> response = cadastroService.getAllCadastros();

        return response;
    }

    // ===== Pronto para uso com dados mockados =====
    @GetMapping("/cadastros/{cpfUsuario}")
    public Cadastro getCadastro(@PathVariable("cpfUsuario") String cpfUsuario){
        return cadastroService.getCadastro(cpfUsuario);
    }

    @PostMapping("/cadastros")
    public Cadastro postCadastro(@RequestBody Cadastro cadastro){
        return cadastroService.postCadastro(cadastro);
    }

    @PutMapping("/cadastros/{cpfUsuario}")
    public Cadastro putCadastro(@PathVariable("cpfUsuario") String cpfUsuario, @RequestBody Cadastro cadastro) {
        
        return cadastroService.putCadastro(cadastro);
    }

    @DeleteMapping("/cadastros/{cpfUsuario}")
    public Cadastro deleteCadastro(@PathVariable("cpfUsuario") String cpfUsuario) {
        return cadastroService.deleteCadastro(cpfUsuario);
    }

    @PostMapping("/login")
    public Login autenticar(@RequestBody Login login){
        return loginService.autenticar(login);
    }

    @GetMapping("/reboot")
    public void reboot(){
        cadastroService.reboot();
    }
}
