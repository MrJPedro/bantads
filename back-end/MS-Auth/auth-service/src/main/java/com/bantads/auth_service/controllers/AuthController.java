package com.bantads.auth_service.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

@CrossOrigin
@RestController
public class AuthController {
    public AuthController(){}

    @GetMapping("/cadastros")
    public String getAllCadastros(){
        
        return "Not Implemented Yet";
    }

    @GetMapping("/cadastros/{cpfUsuario}")
    public String getCadastro(@PathVariable("cpfUsuario") String cpfUsuario){
        return "Not Implemented Yet";
    }

    @PostMapping("/cadastros")
    public String postCadastro(@RequestBody String cadastro){
        return "Not Implemented Yet";
    }

    @PutMapping("/cadastros/{cpfUsuario}")
    public String putCadastro(@PathVariable("cpfUsuario") String cpfUsuario, @RequestBody String cadastro) {
        
        return cadastro;
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
