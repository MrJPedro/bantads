package com.bantads.auth_service.utils;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;

@Component
public class AuthUtil {    

    /*public boolean validarNome(String nome){
        if(nome.matches("[a-zA-ZÀ-Û]+")) return true;
        return false;
    }*/

    public String hashearSenha(String senha, String salt) {
        
        String senhaSalt = senha + salt;
        String hashBase64 = "";

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(senhaSalt.getBytes(StandardCharsets.UTF_8));
            hashBase64 = Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e){
            System.out.println("Algoritmo não encontrado na hora de criptografar a senha!");
        } catch (Exception e) {
            throw e;
        }
        return hashBase64;
    }
}
