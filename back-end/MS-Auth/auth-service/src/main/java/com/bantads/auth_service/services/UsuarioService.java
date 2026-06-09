package com.bantads.auth_service.services;

import com.bantads.auth_service.repositories.UsuarioRepository;
import com.bantads.auth_service.DTOs.UsuarioDTO;
import com.bantads.auth_service.models.Usuario;
import com.bantads.auth_service.models.UsuarioMapper;
import com.bantads.auth_service.utils.AuthUtil;
import com.bantads.auth_service.utils.CPFUtil;
import com.bantads.auth_service.utils.EmailUtil;
import com.bantads.auth_service.utils.UsuarioUtil;
import com.bantads.auth_service.utils.JaExisteException;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UsuarioService {

    @Autowired
    UsuarioRepository usuarioRepository;
    
    @Autowired
    UsuarioMapper usuarioMapper;
    
    @Autowired
    CPFUtil cpfUtil;
    
    @Autowired
    EmailUtil emailUtil;
    
    @Autowired
    UsuarioUtil usuarioUtil;
    
    @Autowired
    AuthUtil authUtil;


    @Transactional(readOnly = true)
    public List<UsuarioDTO> getAllUsuarios() {

        List<Usuario> usuarios = usuarioRepository.findAll();
        List<UsuarioDTO> uDTOs = usuarioMapper.toDTOs(usuarios);
        return uDTOs;
    }

    @Transactional(readOnly = true)
    public UsuarioDTO getUsuario(String loginReq) throws NoSuchElementException, IllegalArgumentException {

        boolean loginEhValido = emailUtil.validarEmail(loginReq);
        if(!loginEhValido) throw new IllegalArgumentException("Login inválido!");

        Usuario u = usuarioRepository.findByLogin(loginReq).orElseThrow(() -> new NoSuchElementException(
            "Login não encontrado!"
        ));

        UsuarioDTO uDTO = usuarioMapper.toDTO(u);
        return uDTO;
    }

    @Transactional
    public UsuarioDTO insertUsuario(UsuarioDTO usuarioDTO) throws Exception, IllegalArgumentException, JaExisteException{

        String cpfUsuarioFormatado = cpfUtil.formatarCPF(usuarioDTO.cpf());
        
        boolean cpfUsuarioEhValido = cpfUtil.validarCPF(cpfUsuarioFormatado);
        boolean loginEhValido = emailUtil.validarEmail(usuarioDTO.login());
        boolean tipoUsuarioEhValido = usuarioUtil.validarTipoUsuario(usuarioDTO.tipo());
        boolean cpfUsuarioJaExiste = !usuarioRepository.findByCpfUsuario(cpfUsuarioFormatado).equals(null);
        boolean loginUsuarioJaExiste = !usuarioRepository.findByLogin(usuarioDTO.login()).equals(null);

        if(!cpfUsuarioEhValido) throw new IllegalArgumentException("CPF inválido!");
        if(!loginEhValido) throw new IllegalArgumentException("Login inválido!");
        if(!tipoUsuarioEhValido) throw new IllegalArgumentException("Tipo Usuário inválido!");
        if(loginUsuarioJaExiste) throw new JaExisteException("Login já cadastrado!");
        if(cpfUsuarioJaExiste) throw new JaExisteException("CPF já cadastrado!");

        Usuario u = usuarioMapper.toUsuario(usuarioDTO);
        u.setHashSenha(authUtil.hashearSenha(usuarioDTO.senha()));
        usuarioRepository.insert(u);
        return usuarioDTO;
    }

    @Transactional
    public UsuarioDTO editUsuario(UsuarioDTO usuarioDTO) throws Exception, IllegalArgumentException{
        
        String cpfUsuarioFormatado = cpfUtil.formatarCPF(usuarioDTO.cpf());
        boolean cpfUsuarioEhValido = cpfUtil.validarCPF(cpfUsuarioFormatado);
        boolean loginEhValido = emailUtil.validarEmail(usuarioDTO.login());
        boolean tipoUsuarioEhValido = usuarioUtil.validarTipoUsuario(usuarioDTO.login());

        if(!cpfUsuarioEhValido) throw new IllegalArgumentException("CPF inválido!");
        if(!loginEhValido) throw new IllegalArgumentException("Login inválido!");
        if(!tipoUsuarioEhValido) throw new IllegalArgumentException("Tipo Usuário inválido!");

        Usuario uNovo = usuarioMapper.toUsuario(usuarioDTO);
        uNovo.setHashSenha(authUtil.hashearSenha(usuarioDTO.senha()));

        Usuario uAntigo = usuarioRepository.findByCpfUsuario(cpfUsuarioFormatado).orElseThrow(() -> new NoSuchElementException("Usuário não cadastrado!"));
        try {
            usuarioRepository.delete(uAntigo);
            usuarioRepository.save(uNovo);
        } catch (Exception e){
            System.out.println(e);
            usuarioRepository.delete(uNovo);
            usuarioRepository.save(uAntigo);
            throw e;
        }
        return usuarioDTO;
    }

    public UsuarioDTO deleteUsuario(String loginUsuario){
        boolean loginEhValido = emailUtil.validarEmail(loginUsuario);
        if(!loginEhValido) throw new IllegalArgumentException("Login inválido!");

        Usuario u = usuarioRepository.findByLogin(loginUsuario).orElseThrow(() -> new NoSuchElementException("Usuário não cadastrado!"));

        UsuarioDTO uDTO = usuarioMapper.toDTO(u);
        
        usuarioRepository.delete(u);

        return uDTO;
    }

    public void reboot(){
        usuarioRepository.deleteAll();

        try {
            this.insertUsuario(new UsuarioDTO("12912861012", "CLIENTE", "cli1@bantads.com.br", "tads"));
            this.insertUsuario(new UsuarioDTO("09506382000", "CLIENTE", "cli2@bantads.com.br", "tads"));
            this.insertUsuario(new UsuarioDTO("85733854057", "CLIENTE", "cli3@bantads.com.br", "tads"));
            this.insertUsuario(new UsuarioDTO("58872160006", "CLIENTE", "cli4@bantads.com.br", "tads"));
            this.insertUsuario(new UsuarioDTO("76179646090", "CLIENTE", "cli5@bantads.com.br", "tads"));
            this.insertUsuario(new UsuarioDTO("98574307084", "GERENTE", "ger1@bantads.com.br", "tads"));
            this.insertUsuario(new UsuarioDTO("64065268052", "GERENTE", "ger2@bantads.com.br", "tads"));
            this.insertUsuario(new UsuarioDTO("23862179060", "GERENTE", "ger3@bantads.com.br", "tads"));
            this.insertUsuario(new UsuarioDTO("40501740066", "ADMINISTRADOR", "adm1@bantads.com.br", "tads"));
        } catch(Exception e){
            System.out.println(e);
        }
    }
    
}
