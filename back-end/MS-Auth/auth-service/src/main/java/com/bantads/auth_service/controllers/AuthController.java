package com.bantads.auth_service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.util.List;
import java.util.NoSuchElementException;

import com.bantads.auth_service.DTOs.UsuarioDTO;
import com.bantads.auth_service.services.UsuarioService;
import com.bantads.auth_service.DTOs.Login;
import com.bantads.auth_service.services.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus;

@CrossOrigin
@RestController
public class AuthController {
    
    private final LoginService loginService;
    @Autowired
    UsuarioService usuarioService;
    
    public AuthController(LoginService loginService){
        this.loginService = loginService;
    }

    // ===== Pronto para uso com dados mockados =====
    @GetMapping("/Usuarios")
    public ResponseEntity<List<UsuarioDTO>> getAllUsuarios(){
        
        List<UsuarioDTO> usuarios = null;

        try {
            usuarios = usuarioService.getAllUsuarios();
            if(usuarios.equals(null)) throw new NullPointerException("Referência à lista de usuários é 'null'!");
        } catch(Exception e){
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    // ===== Pronto para uso com dados mockados =====
    @GetMapping("/Usuarios/{loginUsuario}")
    public ResponseEntity<UsuarioDTO> getUsuario(@PathVariable("loginUsuario") String loginUsuario){
        
        UsuarioDTO uDTO = null;
        
        try {

            uDTO = usuarioService.getUsuario(loginUsuario);
            if(uDTO.equals(null)) throw new NullPointerException("Referência a usuário é 'null'!");
        
        } catch (IllegalArgumentException exception){
            //
        } catch (NoSuchElementException exception){
            //
        } catch (Exception e){
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(uDTO, HttpStatus.OK);
    }

    @PostMapping("/Usuarios")
    public UsuarioDTO postUsuario(@RequestBody UsuarioDTO usuario){
        return usuarioService.insertUsuario(usuario);
    }

    @PutMapping("/Usuarios/{cpfUsuario}")
    public UsuarioDTO putUsuario(@PathVariable("cpfUsuario") String cpfUsuario, @RequestBody UsuarioDTO Usuario) {
        
        return usuarioService.putUsuario(Usuario);
    }

    @DeleteMapping("/Usuarios/{cpfUsuario}")
    public UsuarioDTO deleteUsuario(@PathVariable("cpfUsuario") String cpfUsuario) {
        return usuarioService.deleteUsuario(cpfUsuario);
    }

    @PostMapping("/login")
    public Login autenticar(@RequestBody Login login){
        return loginService.autenticar(login);
    }

    @GetMapping("/reboot")
    public void reboot(){
        usuarioService.reboot();
    }
}
