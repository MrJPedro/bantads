// Atualizar para JPA posteriormente
package com.bantads.auth_service.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("usuario")
public class Usuario {

    @Id
    private String login;
    @Field("cpf")
    @Indexed(unique = true)
    private String cpfUsuario;
    @Field("tipo")
    private String tipoUsuario;
    private String senha;

    private Usuario(){
        
    };

    public Usuario( String cpfUsuario, String tipoUsuario, String login, String senha){
        this.cpfUsuario = cpfUsuario;
        this.tipoUsuario = tipoUsuario;
        this.login = login;
        this.senha = senha;
    }

    public String getCpfUsuario() {
        return cpfUsuario;
    }

    public void setCpfUsuario(String cpfUsuario) {
        this.cpfUsuario = cpfUsuario;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }
    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }
    public String getLogin() {
        return login;
    }
    public void setLogin(String login) {
        this.login = login;
    }
    public String getSenha() {
        return senha;
    }
    public void setSenha(String senha) {
        this.senha = senha;
    }
}
