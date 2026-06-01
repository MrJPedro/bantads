// Atualizar para JPA posteriormente
package com.bantads.auth_service.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("usuarios")
public class Usuario {

    @Id
    private String login;
    @Field("cpf")
    @Indexed(unique = true)
    private String cpf;
    @Field("tipo")
    private String tipo;
    @Field("senha")
    private String senha;

    public Usuario() {
    }

    public Usuario(String cpfUsuario, String tipoUsuario, String loginUsuario, String senhaUsuario){
        this.cpf = cpfUsuario;
        this.tipo = tipoUsuario;
        this.login = loginUsuario;
        this.senha = senhaUsuario;
    }

    public String getCpfUsuario() {
        return cpf;
    }

    public void setCpfUsuario(String cpfUsuario) {
        this.cpf = cpfUsuario;
    }

    public String getTipoUsuario() {
        return tipo;
    }
    public void setTipoUsuario(String tipoUsuario) {
        this.tipo = tipoUsuario;
    }
    public String getLogin() {
        return login;
    }
    public void setLogin(String loginUsuario) {
        this.login = loginUsuario;
    }
    public String getSenha() {
        return senha;
    }
    public void setSenha(String senhaUsuario) {
        this.senha = senhaUsuario;
    }
}
