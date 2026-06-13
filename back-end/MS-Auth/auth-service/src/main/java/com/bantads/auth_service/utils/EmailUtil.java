package com.bantads.auth_service.utils;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class EmailUtil {

    private static final String regex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    public boolean validarEmail(String email){
        return Pattern.compile(regex).matcher(email).matches();

    } 
}
