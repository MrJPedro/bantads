package com.bantads.auth_service.services;

//import com.bantads.auth_service.DTOs.UsuarioDTO;
import com.bantads.auth_service.repositories.UsuarioRepository;
import com.bantads.auth_service.DTOs.UsuarioDTO;
import com.bantads.auth_service.models.Usuario;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//import java.util.ArrayList;
//import java.util.HashMap;

@Service
public class UsuarioService {

    
    @Autowired
    UsuarioRepository usuarioRepository;



    /*public static HashMap<String, UsuarioDTO> listaMap = new HashMap<>();

    static {
        listaMap.put(
            "cli1@bantads.com.br",
            new UsuarioDTO("12912861012", "CLIENTE", "cli1@bantads.com.br", "tads")
        );
        listaMap.put(
            "cli2@bantads.com.br",
            new UsuarioDTO("09506382000", "CLIENTE", "cli2@bantads.com.br", "tads")
        );
        listaMap.put(
            "cli3@bantads.com.br",
            new UsuarioDTO("85733854057", "CLIENTE", "cli3@bantads.com.br", "tads")
        );
        listaMap.put(
            "cli4@bantads.com.br",
            new UsuarioDTO("58872160006", "CLIENTE", "cli4@bantads.com.br", "tads")
        );
        listaMap.put(
            "cli5@bantads.com.br",
            new UsuarioDTO("76179646090", "CLIENTE", "cli5@bantads.com.br", "tads")
        );
        listaMap.put(
            "ger1@bantads.com.br",
            new UsuarioDTO("98574307084", "GERENTE", "ger1@bantads.com.br", "tads")
        );
        listaMap.put(
            "ger2@bantads.com.br",
            new UsuarioDTO("64065268052", "GERENTE", "ger2@bantads.com.br", "tads")
        );
        listaMap.put(
            "ger3@bantads.com.br",
            new UsuarioDTO("23862179060", "GERENTE", "ger3@bantads.com.br", "tads")
        );
        listaMap.put(
            "adm1@bantads.com.br",
            new UsuarioDTO("40501740066", "ADMINISTRADOR", "adm1@bantads.com.br", "tads")
        );

    }*/

    public List<Usuario> getAllUsuarios(){
        //List<UsuarioDTO> cadastros = new ArrayList<UsuarioDTO>(listaMap.values());
        //return cadastros;

        return usuarioRepository.findAll();
        
    }

    public Usuario getUsuario(String loginReq) {
        
        /*if(listaMap.containsKey(loginReq))
            return listaMap.get(loginReq);
        
        return null;*/

        Usuario u = null;
        try {
            u = usuarioRepository.findById(loginReq).get();
        } catch (Exception e){
            System.out.println(e);
        }
        return u;

        
    }

    public Usuario postUsuario(Usuario usuario){
        /*if (cadastro == null)
            return null;
        
        if (listaMap.containsKey(cadastro.loginUsuario()))
            return null;
        
        listaMap.put(cadastro.loginUsuario(), cadastro);

        return cadastro;*/

        Usuario u = null;

        try {
            u = usuarioRepository.insert(usuario);
        } catch (Exception e){
            System.out.println(e);
        }
        return u;
    }

    public Usuario putCadastro(Usuario usuario){
        /*if (cadastro == null)
            return null;

        if (!listaMap.containsKey(cadastro.loginUsuario()))
            return null;

        listaMap.replace(cadastro.loginUsuario(), cadastro);

        return listaMap.get(cadastro.loginUsuario());*/

        Usuario uAntigo = usuarioRepository.findById(usuario.getLogin()).get();
        Usuario uNovo = null;

        try {
            usuarioRepository.delete(uAntigo);
            uNovo = usuarioRepository.insert(usuario);
        } catch (Exception e){
            System.out.println(e);
        }
        return uNovo;
    }

    public Usuario deleteCadastro(String cpfCadastro){
        /*if (cpfCadastro == null)
            return null;

        if (!listaMap.containsKey(cpfCadastro))
            return null;

        UsuarioDTO cadastro = listaMap.get(cpfCadastro);
        listaMap.remove(cpfCadastro);

        return cadastro;*/
    }

    public void reboot(){
        /*listaMap.clear();

        listaMap.put(
            "cli1@bantads.com.br",
            new UsuarioDTO("12912861012", "CLIENTE", "cli1@bantads.com.br", "tads")
        );
        listaMap.put(
            "cli2@bantads.com.br",
            new UsuarioDTO("09506382000", "CLIENTE", "cli2@bantads.com.br", "tads")
        );
        listaMap.put(
            "cli3@bantads.com.br",
            new UsuarioDTO("85733854057", "CLIENTE", "cli3@bantads.com.br", "tads")
        );
        listaMap.put(
            "cli4@bantads.com.br",
            new UsuarioDTO("58872160006", "CLIENTE", "cli4@bantads.com.br", "tads")
        );
        listaMap.put(
            "cli5@bantads.com.br",
            new UsuarioDTO("76179646090", "CLIENTE", "cli5@bantads.com.br", "tads")
        );
        listaMap.put(
            "ger1@bantads.com.br",
            new UsuarioDTO("98574307084", "GERENTE", "ger1@bantads.com.br", "tads")
        );
        listaMap.put(
            "ger2@bantads.com.br",
            new UsuarioDTO("64065268052", "GERENTE", "ger2@bantads.com.br", "tads")
        );
        listaMap.put(
            "ger3@bantads.com.br",
            new UsuarioDTO("23862179060", "GERENTE", "ger3@bantads.com.br", "tads")
        );
        listaMap.put(
            "adm1@bantads.com.br",
            new UsuarioDTO("40501740066", "ADMINISTRADOR", "adm1@bantads.com.br", "tads")
        );*/
    }
    
}
