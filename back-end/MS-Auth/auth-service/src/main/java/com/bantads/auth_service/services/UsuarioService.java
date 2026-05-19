package com.bantads.auth_service.services;

import com.bantads.auth_service.controllers.AuthController;
//import com.bantads.auth_service.DTOs.UsuarioDTO;
import com.bantads.auth_service.repositories.UsuarioRepository;
import com.bantads.auth_service.DTOs.UsuarioDTO;
import com.bantads.auth_service.models.Usuario;
import com.bantads.auth_service.models.UsuarioMapper;
import com.bantads.auth_service.utils.CPFUtil;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpClientErrorException.NotFound;

//import java.util.ArrayList;
//import java.util.HashMap;

@Service
public class UsuarioService {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    UsuarioMapper usuarioMapper;

    @Autowired
    CPFUtil cpfUtil;


    @Transactional(readOnly = true)
    public List<UsuarioDTO> getAllUsuarios() throws Exception{

        List<Usuario> usuarios = usuarioRepository.findAll();
        List<UsuarioDTO> uDTOs = usuarios.stream()
            .map((Usuario usuario) -> this.usuarioMapper.toDTO(usuario))
            .toList();
        return uDTOs;
    }

    @Transactional(readOnly = true)
    public UsuarioDTO getUsuario(String loginReq) throws NoSuchElementException {

        UsuarioDTO uDTO = null;
        try {
            Usuario u = usuarioRepository.findByLogin(loginReq).orElseThrow(() -> new NoSuchElementException());
            uDTO = usuarioMapper.toDTO(u);
        } catch (Exception e){
            System.out.println("==== Classe: UsuarioService ====");
            System.out.println("==== Chamada: getUsuario ====");
            System.out.println(e);
            System.out.println("==== ==== ====");
        }
        return uDTO;
    }

    @Transactional
    public UsuarioDTO postUsuario(String cpfUsuario, String tipoUsuario, String loginUsuario, String senhaUsuario) throws Exception{

        String cpfUsuarioFormatado = cpfUtil.formatarCPF(cpfUsuario);
        
        boolean cpfUsuarioEhValido = cpfUtil.validarCPF(cpfUsuario);
        boolean loginUsuarioJaExiste = usuarioRepository.findByLogin(loginUsuario) != null;
        boolean cpfUsuarioJaExiste = usuarioRepository.findByCpfUsuario(cpfUsuario) != null;

        if(!cpfUsuarioEhValido) throw new IllegalArgumentException();

        if(loginUsuarioJaExiste) throw new Exception("Login já cadastrado!");

        if(cpfUsuarioJaExiste) throw new Exception("CPF já cadastrado!");

        UsuarioDTO uDTO;
        try {
            uDTO = new UsuarioDTO(cpfUsuario, tipoUsuario, loginUsuario, senhaUsuario);
            
            // validar e-mail
            // criptografar senha
            Usuario u = usuarioMapper.toUsuario(uDTO);
            usuarioRepository.insert(u);
        } catch (Exception e){
            if()
            System.out.println("==== Classe: UsuarioService ====");
            System.out.println("==== Chamada: postUsuario ====");
            System.out.println(e);
            System.out.println("==== ==== ====");
            uDTO = null;
        }
        return uDTO;
    }

    @Transactional
    public UsuarioDTO putUsuario(String cpfUsuario, String tipoUsuario, String loginUsuario, String senhaUsuario) throws Exception{
        /*if (cadastro == null)
            return null;

        if (!listaMap.containsKey(cadastro.loginUsuario()))
            return null;

        listaMap.replace(cadastro.loginUsuario(), cadastro);

        return listaMap.get(cadastro.loginUsuario());*/
        
        Usuario uAntigo = usuarioMapper.toUsuario(uDTO);
        usuarioRepository.findById(uAntigo.getLogin()).get();
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
