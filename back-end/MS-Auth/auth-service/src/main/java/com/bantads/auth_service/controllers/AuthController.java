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

@CrossOrigin
@RestController
public class AuthController {
    
    @Autowired
    CadastroService cadastroService;
    
    public AuthController(){}

    // ===== Pronto para uso com dados mockados =====
    @GetMapping("/cadastros")
    public List<Cadastro> getAllCadastros(){
        
        return cadastroService.getAllCadastros();
    }

    // ===== Pronto para uso com dados mockados =====
    @GetMapping("/cadastros/{cpfUsuario}")
    public Cadastro getCadastro(@PathVariable("cpfUsuario") String cpfUsuario){
        return cadastroService.getCadastro(cpfUsuario);
    }

    @PostMapping("/cadastros")
    public String postCadastro(@RequestBody String cadastro){
        return "Not Implemented Yet";
    }

    @PutMapping("/cadastros/{cpfUsuario}")
    public String putCadastro(@PathVariable("cpfUsuario") String cpfUsuario, @RequestBody String cadastro) {
        
        return "Not Implemented Yet";
    }

    @DeleteMapping("/cadastros/{cpfUsuario}")
    public String deleteCadastro(@PathVariable("cpfUsuario") String cpfUsuario) {
        return "Not Implemented Yet";
    }

    @PostMapping("/login")
    public String autenticar(@RequestBody String login){
        return "Not Implemented Yet";
    }

    @GetMapping("/reboot")
    public void reboot(){}
}
