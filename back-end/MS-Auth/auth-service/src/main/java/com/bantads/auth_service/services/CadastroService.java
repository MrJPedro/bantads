package com.bantads.auth_service.services;

import com.bantads.auth_service.DTOs.Cadastro;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

@Service
public class CadastroService {

    //@Autowired
    //public static List<Cadastro> lista;

    @Autowired
    public static HashMap<String, Cadastro> listaMap;

    static {
        listaMap.put(
            "12912861012",
            new Cadastro("12912861012", "CLIENTE", "cli1@bantads.com.br", "tads")
        );
        listaMap.put(
            "09506382000",
            new Cadastro("09506382000", "CLIENTE", "cli2@bantads.com.br", "tads")
        );
        listaMap.put(
            "85733854057",
            new Cadastro("85733854057", "CLIENTE", "cli3@bantads.com.br", "tads")
        );
        listaMap.put(
            "58872160006",
            new Cadastro("58872160006", "CLIENTE", "cli4@bantads.com.br", "tads")
        );
        listaMap.put(
            "76179646090",
            new Cadastro("76179646090", "CLIENTE", "cli5@bantads.com.br", "tads")
        );
        listaMap.put(
            "98574307084",
            new Cadastro("98574307084", "GERENTE", "ger1@bantads.com.br", "tads")
        );
        listaMap.put(
            "64065268052",
            new Cadastro("64065268052", "GERENTE", "ger2@bantads.com.br", "tads")
        );
        listaMap.put(
            "23862179060",
            new Cadastro("23862179060", "GERENTE", "ger3@bantads.com.br", "tads")
        );
        listaMap.put(
            "40501740066",
            new Cadastro("40501740066", "ADMINISTRADOR", "adm1@bantads.com.br", "tads")
        );

    }

    public List<Cadastro> getAllCadastros(){
        List<Cadastro> cadastros = new ArrayList<Cadastro>(listaMap.values());
        return cadastros;
    }

    public Cadastro getCadastro(String cpfReq) {
        
        if(listaMap.containsKey(cpfReq))
            return cadastros.get(cpfReq);
        
        return null;
    }

    public Cadastro postCadastro(Cadastro cadastro){
        if (cadastro == null)
            return null;
        
        if (listaMap.containsKey(cadastro.cpfUsuario()))
            return null;
        
        listaMap.put(cadastro.cpfUsuario(), cadaatro);

        return cadastro;
    }

    public Cadastro putCadastro(Cadastro cadastro){
        if (cadastro == null)
            return null;

        if (!listaMap.containsKey(cadastro.cpfUsuario()))
            return null;

        listaMap.replace(cadastro.cpfUsuario(), cadastro);

        return listaMap.get(cadastro.cpfUsuario());
    }

    public Cadastro deleteCadastro(String cpfCadastro){
        if (cpfCadastro == null)
            return null;

        if (!listaMap.containsKey(cpfCadastro))
            return null;

        Cadastro cadastro = listaMap.get(cpfCadastro);
        listaMap.remove(cpfCadastro);

        return cadastro;
    }
    
}
