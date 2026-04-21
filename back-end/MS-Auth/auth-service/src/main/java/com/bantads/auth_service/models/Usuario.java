// Atualizar para JPA posteriormente
package com.bantads.auth_service.models;

public class Usuario {
    private String cpfUsuario;
    private String tipoUsuario;
    private String login;
    private String senha;

    private Usuario(){
        
    };

    public Usuario( String cpfUsuario, String tipoUsuario, String login, String senha){
        this.cpfUsuario = cpfUsuario;
        this.tipoUsuario = tipoUsuario;
        this.login = login;
        this.senha = senha;
    }
}
