package com.bantads.auth_service.utils;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ArrayList;

@Component
public class UsuarioUtil {

        public final List<String> tiposUsuarioPermitidos = new ArrayList<String>();

        public UsuarioUtil(){
            tiposUsuarioPermitidos.add(new String("ADMINISTRADOR"));
            tiposUsuarioPermitidos.add(new String("GERENTE"));
            tiposUsuarioPermitidos.add(new String("CLIENTE"));
        };

        public boolean validarTipoUsuario(String tipo){
            return tiposUsuarioPermitidos.stream().anyMatch(item -> item.equals(tipo));        }
}
