package com.bantads.auth_service.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AuthUtil {    
    public String hashearSenha(String senha) {
        
        String hashBase64 = "";

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(senha.getBytes(StandardCharsets.UTF_8));
            hashBase64 = Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e){
            System.out.println("Algoritmo não encontrado na hora de criptografar a senha!");
        } catch (Exception e) {
            throw e;
        }
        return hashBase64;
    }
}
