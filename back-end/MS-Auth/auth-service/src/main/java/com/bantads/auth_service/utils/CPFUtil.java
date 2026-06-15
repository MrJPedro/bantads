package com.bantads.auth_service.utils;

//import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class CPFUtil {

    public String formatarCPF(String cpf) {
        if (cpf == null) {
            return null;
        }
        return cpf.replaceAll("[^0-9]", "");
    }

    public boolean validarCPF(String cpf) {
      cpf = cpf.replaceAll("[^0-9]", "");


      if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) {
        return false;
      }


      // Cálculo do dígito verificador
      int[] digits = new int[11];
      for (int i = 0; i < 11; i++) {
          digits[i] = cpf.charAt(i) - '0';
      }
      int sum = 0;
      for (int i = 0; i < 9; i++) {
          sum += digits[i] * (10 - i);
      }
      int remainder = sum % 11;
      int digit1 = (remainder < 2) ? 0 : (11 - remainder);


      sum = 0;
      for (int i = 0; i < 10; i++) {
          sum += digits[i] * (11 - i);
      }
      remainder = sum % 11;
      int digit2 = (remainder < 2) ? 0 : (11 - remainder);


      return (digit1 == digits[9] && digit2 == digits[10]);
  }

}
