package com.bantads.auth_service.controllers;

import java.util.List;
import java.util.NoSuchElementException;

import javax.security.auth.login.LoginException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bantads.auth_service.DTOs.Login;
import com.bantads.auth_service.DTOs.LoginResponse;
import com.bantads.auth_service.DTOs.UsuarioDTO;
import com.bantads.auth_service.services.LoginService;
import com.bantads.auth_service.services.UsuarioService;
import com.bantads.auth_service.utils.JaExisteException;

@CrossOrigin
@RestController
public class AuthController {
    
    @Autowired
    UsuarioService usuarioService;

    @Autowired
    LoginService loginService;

    @GetMapping("/usuarios")
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

    @GetMapping("/usuarios/{loginUsuario}")
    public ResponseEntity<?> getUsuario(@PathVariable("loginUsuario") String loginUsuario){
        
        UsuarioDTO uDTO = null;
        
        try {

            uDTO = usuarioService.getUsuario(loginUsuario);
            if(uDTO.equals(null)) throw new NullPointerException("Referência a uDTO é 'null'!");
        
        } catch (IllegalArgumentException exception){
            return new ResponseEntity<String>(exception.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (NoSuchElementException exception){
            return new ResponseEntity<String>(exception.getMessage(), HttpStatus.NOT_FOUND);

        } catch (Exception e){
            System.out.println(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(uDTO, HttpStatus.OK);
    }

    @PostMapping("/usuarios")
    public ResponseEntity<?> postUsuario(@RequestBody UsuarioDTO usuario){
        
        UsuarioDTO uDTO = null;
        
        try {
            uDTO = usuarioService.insertUsuario(usuario);
            if(uDTO.equals(null)) throw new NullPointerException("Referência a uDTO é 'null'!");

        } catch (IllegalArgumentException exception) {
            System.out.println(exception);
            return new ResponseEntity<String>(exception.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (JaExisteException exception){
            System.out.println(exception);
            return new ResponseEntity<String>(exception.getMessage(), HttpStatus.CONFLICT);

        } catch (Exception exception) {
            System.out.println(exception);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        return new ResponseEntity<UsuarioDTO>(uDTO, HttpStatus.CREATED);
    }

    @PutMapping("/usuarios/{cpfUsuario}")
    public ResponseEntity<?> putUsuario(@PathVariable("cpfUsuario") String cpfUsuario, @RequestBody UsuarioDTO usuario) {
        
        UsuarioDTO uDTO = null;

        try{
            uDTO = usuarioService.editUsuario(usuario);
            if(uDTO.equals(null)) throw new NullPointerException("Referência a uDTO é 'null'!");

        } catch (IllegalArgumentException exception){
            System.out.println(exception);
            return new ResponseEntity<String>(exception.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (NoSuchElementException exception){
            System.out.println(exception);
            return new ResponseEntity<String>(exception.getMessage(), HttpStatus.NOT_FOUND);

        } catch (Exception exception){
            System.out.println(exception);
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<UsuarioDTO>(uDTO, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/usuarios/{loginUsuario}")
    public ResponseEntity<?> deleteUsuario(@PathVariable("loginUsuario") String loginUsuario) {
        
        UsuarioDTO uDTO = null;

        try {
            uDTO = usuarioService.deleteUsuario(loginUsuario);
            if(uDTO.equals(null)) throw new NullPointerException("Referência a uDTO é 'null'!");

        } catch (IllegalArgumentException exception){
            System.out.println(exception);
            return new ResponseEntity<String>(exception.getMessage(), HttpStatus.BAD_REQUEST);


        } catch (NoSuchElementException exception){
            System.out.println(exception);
            return new ResponseEntity<String>(exception.getMessage(), HttpStatus.NOT_FOUND);
            
        } catch (Exception exception){
            System.out.println(exception);
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<UsuarioDTO>(uDTO, HttpStatus.ACCEPTED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> autenticar(@RequestBody Login login){

        UsuarioDTO loginResponse = null;

        try {
            loginResponse = loginService.autenticar(login);

            if(loginResponse == null) {
                throw new NullPointerException("Referência a loginResponse é 'null'!");
            }

        } catch (IllegalArgumentException exception){
            System.out.println(exception);
            return new ResponseEntity<String>(
                exception.getMessage(),
                HttpStatus.UNAUTHORIZED
            );

        } catch (NoSuchElementException exception){
            System.out.println(exception);
            return new ResponseEntity<String>(
                exception.getMessage(),
                HttpStatus.UNAUTHORIZED
            );

        } catch (LoginException exception){
            System.out.println(exception);
            return new ResponseEntity<String>(
                exception.getMessage(),
                HttpStatus.UNAUTHORIZED
            );

        } catch (Exception exception){
            System.out.println(exception);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<UsuarioDTO>(
            loginResponse,
            HttpStatus.OK
        );
    }

    @GetMapping("/reboot")
    public ResponseEntity<?> reboot(){
        try{
            usuarioService.reboot();
        } catch (Exception exception){
            System.out.println(exception);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        
    }
}
