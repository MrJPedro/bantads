package com.bantads.auth_service.config;

import com.bantads.auth_service.repositories.UsuarioRepository;
import com.bantads.auth_service.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UsuarioSeeder implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.count() == 0) {
            System.out.println("Banco de dados de autenticação vazio. Iniciando seed de usuários...");
            usuarioService.reboot();
        }
    }
}
