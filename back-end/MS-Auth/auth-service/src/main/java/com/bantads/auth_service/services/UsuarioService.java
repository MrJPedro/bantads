package com.bantads.auth_service.services;

import com.bantads.auth_service.models.Usuario;
import java.util.List;
import java.util.ArrayList;

public class UsuarioService {
    public static List<Usuario> lista = new ArrayList<Usuario>();

    static {
        lista.add(
            new Usuario("12912861012", "CLIENTE", "cli1@bantads.com.br", "tads")
        );
        lista.add(
            new Usuario("09506382000", "CLIENTE", "cli2@bantads.com.br", "tads")
        );
        lista.add(
            new Usuario("85733854057", "CLIENTE", "cli3@bantads.com.br", "tads")
        );
        lista.add(
            new Usuario("58872160006", "CLIENTE", "cli4@bantads.com.br", "tads")
        );
        lista.add(
            new Usuario("76179646090", "CLIENTE", "cli5@bantads.com.br", "tads")
        );

        lista.add(
            new Usuario("98574307084", "GERENTE", "ger1@bantads.com.br", "tads")
        );
        lista.add(
            new Usuario("64065268052", "GERENTE", "ger2@bantads.com.br", "tads")
        );
        lista.add(
            new Usuario("23862179060", "GERENTE", "ger3@bantads.com.br", "tads")
        );
        lista.add(
            new Usuario("40501740066", "ADMINISTRADOR", "adm1@bantads.com.br", "tads")
        );
    }

    public List<Usuario> getAllUsuarios(){
        return lista;
    }
}
