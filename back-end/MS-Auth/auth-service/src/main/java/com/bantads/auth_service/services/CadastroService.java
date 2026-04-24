package com.bantads.auth_service.services;

import com.bantads.auth_service.DTOs.Cadastro;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;

@Service
public class CadastroService {

    @Autowired
    public static List<Cadastro> lista;

    static {
        lista.add(
            new Cadastro("12912861012", "CLIENTE", "cli1@bantads.com.br", "tads")
        );
        lista.add(
            new Cadastro("09506382000", "CLIENTE", "cli2@bantads.com.br", "tads")
        );
        lista.add(
            new Cadastro("85733854057", "CLIENTE", "cli3@bantads.com.br", "tads")
        );
        lista.add(
            new Cadastro("58872160006", "CLIENTE", "cli4@bantads.com.br", "tads")
        );
        lista.add(
            new Cadastro("76179646090", "CLIENTE", "cli5@bantads.com.br", "tads")
        );
        lista.add(
            new Cadastro("98574307084", "GERENTE", "ger1@bantads.com.br", "tads")
        );
        lista.add(
            new Cadastro("64065268052", "GERENTE", "ger2@bantads.com.br", "tads")
        );
        lista.add(
            new Cadastro("23862179060", "GERENTE", "ger3@bantads.com.br", "tads")
        );
        lista.add(
            new Cadastro("40501740066", "ADMINISTRADOR", "adm1@bantads.com.br", "tads")
        );
    }

    public List<Cadastro> getAllCadastros(){
        return lista;
    }

    public Cadastro getCadastro(String cpfReq) {
        HashMap<String, Cadastro> cadastros = new HashMap<>();

        for (Cadastro item: lista){
            cadastros.put(item.cpfUsuario(), item);
        }
        
        if(cadastros.containsKey(cpfReq))
            return cadastros.get(cpfReq);
        
        return null;
    }

    
}
