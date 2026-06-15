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
    @Field("nome")
    private String nome;
    @Field("cpf")
    @Indexed(unique = true)
    private String cpf;
    @Field("tipo")
    private String tipo;
    @Field("hash_senha")
    private String hashSenha;

    public Usuario() {
    }

    public Usuario(String cpf, String tipo, String login, String nome, String hashSenha){
        this.cpf = cpf;
        this.tipo = tipo;
        this.login = login;
        this.nome = nome;
        this.hashSenha = hashSenha;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getHashSenha() {
        return hashSenha;
    }

    public void setHashSenha(String hashSenha) {
        this.hashSenha = hashSenha;
    }
}
