package com.bantads.auth_service.services;

import com.bantads.auth_service.repositories.UsuarioRepository;
import com.bantads.auth_service.DTOs.UsuarioDTO;
import com.bantads.auth_service.models.Usuario;
import com.bantads.auth_service.models.UsuarioMapper;
import com.bantads.auth_service.utils.AuthUtil;
import com.bantads.auth_service.utils.CPFUtil;
import com.bantads.auth_service.utils.EmailUtil;
import com.bantads.auth_service.utils.UsuarioUtil;

import java.util.List;
import java.util.NoSuchElementException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final CPFUtil cpfUtil;
    private final EmailUtil emailUtil;
    private final UsuarioUtil usuarioUtil;
    private final AuthUtil authUtil;


    @Transactional(readOnly = true)
    public List<UsuarioDTO> getAllUsuarios() {

        List<Usuario> usuarios = usuarioRepository.findAll();
        List<UsuarioDTO> uDTOs = usuarios.stream()
            .map((Usuario usuario) -> this.usuarioMapper.toDTO(usuario))
            .toList();
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

    /* public UsuarioDTO getUsuarioByCpfUsuario(String cpfUsuario) throws NoSuchElementException, IllegalArgumentException {
        String cpfUsuarioFormatado = cpfUtil.formatarCPF(cpfUsuario);
        boolean cpfUsuarioEhValido = cpfUtil.validarCPF(cpfUsuarioFormatado);
        if(!cpfUsuarioEhValido) throw new IllegalArgumentException("CPF inválido!");

        Usuario u = usuarioRepository.findByCpfUsuario(cpfUsuarioFormatado).orElseThrow(() -> new NoSuchElementException(
            "Login não encontrado!"
        ));

        UsuarioDTO uDTO = usuarioMapper.toDTO(u);
        return uDTO;
    } */

    @Transactional
    public UsuarioDTO insertUsuario(String cpfUsuario, String tipoUsuario, String loginUsuario, String senhaUsuario) throws Exception, IllegalArgumentException{

        String cpfUsuarioFormatado = cpfUtil.formatarCPF(cpfUsuario);
        
        boolean cpfUsuarioEhValido = cpfUtil.validarCPF(cpfUsuarioFormatado);
        boolean loginEhValido = emailUtil.validarEmail(loginUsuario);
        boolean tipoUsuarioEhValido = usuarioUtil.validarTipoUsuario(tipoUsuario);
        boolean cpfUsuarioJaExiste = !usuarioRepository.findByCpfUsuario(cpfUsuarioFormatado).equals(null);
        boolean loginUsuarioJaExiste = !usuarioRepository.findByLogin(loginUsuario).equals(null);

        if(!cpfUsuarioEhValido) throw new IllegalArgumentException("CPF inválido!");
        if(!loginEhValido) throw new IllegalArgumentException("Login inválido!");
        if(!tipoUsuarioEhValido) throw new IllegalArgumentException("Tipo Usuário inválido!");
        if(loginUsuarioJaExiste) throw new Exception("Login já cadastrado!");
        if(cpfUsuarioJaExiste) throw new Exception("CPF já cadastrado!");

        UsuarioDTO uDTO = new UsuarioDTO(cpfUsuarioFormatado, tipoUsuario, loginUsuario);
        Usuario u = usuarioMapper.toUsuario(uDTO);
        u.setSenha(authUtil.hashearSenha(senhaUsuario));
        usuarioRepository.insert(u);
        return uDTO;
    }

    @Transactional
    public UsuarioDTO editUsuario(String cpfUsuario, String tipoUsuario, String loginUsuario, String senhaUsuario) throws Exception, IllegalArgumentException{
        
        String cpfUsuarioFormatado = cpfUtil.formatarCPF(cpfUsuario);
        boolean cpfUsuarioEhValido = cpfUtil.validarCPF(cpfUsuarioFormatado);
        boolean loginEhValido = emailUtil.validarEmail(loginUsuario);
        boolean tipoUsuarioEhValido = usuarioUtil.validarTipoUsuario(tipoUsuario);

        if(!cpfUsuarioEhValido) throw new IllegalArgumentException("CPF inválido!");
        if(!loginEhValido) throw new IllegalArgumentException("Login inválido!");
        if(!tipoUsuarioEhValido) throw new IllegalArgumentException("Tipo Usuário inválido!");

        UsuarioDTO uDTOnovo = new UsuarioDTO(cpfUsuarioFormatado, tipoUsuario, loginUsuario);
        Usuario uNovo = usuarioMapper.toUsuario(uDTOnovo);
        uNovo.setSenha(authUtil.hashearSenha(senhaUsuario));

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
        return uDTOnovo;
    }

    public Usuario deleteUsuario(String loginUsuario){
        boolean loginEhValido = emailUtil.validarEmail(loginUsuario);
        if(!loginEhValido) throw new IllegalArgumentException("Login inválido!");

        Usuario u = usuarioRepository.findByCpfUsuario(loginUsuario).orElseThrow(() -> new NoSuchElementException("Usuário não cadastrado!"));
        
        usuarioRepository.delete(u);

        return u;
    }

    public void reboot(){
        usuarioRepository.deleteAll();

        try {
            this.insertUsuario("12912861012", "CLIENTE", "cli1@bantads.com.br", "tads");
            this.insertUsuario("09506382000", "CLIENTE", "cli2@bantads.com.br", "tads");
            this.insertUsuario("85733854057", "CLIENTE", "cli3@bantads.com.br", "tads");
            this.insertUsuario("58872160006", "CLIENTE", "cli4@bantads.com.br", "tads");
            this.insertUsuario("76179646090", "CLIENTE", "cli5@bantads.com.br", "tads");
            this.insertUsuario("98574307084", "GERENTE", "ger1@bantads.com.br", "tads");
            this.insertUsuario("64065268052", "GERENTE", "ger2@bantads.com.br", "tads");
            this.insertUsuario("23862179060", "GERENTE", "ger3@bantads.com.br", "tads");
            this.insertUsuario("40501740066", "ADMINISTRADOR", "adm1@bantads.com.br", "tads");
        } catch(Exception e){
            System.out.println(e);
        }

        
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
