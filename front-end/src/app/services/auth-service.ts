import { Injectable } from '@angular/core';
import { LoginInfo } from '../DTO/auth/login-info';

import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { API_URL } from '../DTO/api/api';


@Injectable({
  providedIn: 'root',
})

export class AuthService {

  private readonly users = [
    {
      "access_token": "string",
      "token_type": "bearer",
      "tipo": "CLIENTE",
      "usuario": {
        "nome": "Carlos Oliveira",
        "cpf": "52998224725",
      " email": "carlos.oliveira@bantads.com.br"
      }
    },
    {
      "access_token": "string",
      "token_type": "bearer",
      "tipo": "CLIENTE",
      "usuario": {
        "nome": "Fernanda Lima",
        "cpf": "12345678909",
        "email": "fernanda.lima@bantads.com.br"
      }
    },
    {
      "access_token": "string",
      "token_type": "bearer",
      "tipo": "CLIENTE",
      "usuario": {
        "nome": "Ricardo Souza",
        "cpf": "23456789173",
        "email": "ricardo.souza@bantads.com.br"
      }
    },
    {
      "access_token": "string",
      "token_type": "bearer",
      "tipo": "CLIENTE",
      "usuario": {
        "nome": "Juliana Martins",
        "cpf": "34567891228",
        "email": "juliana.martins@bantads.com.br"
      }
    },
    {
      "access_token": "string",
      "token_type": "bearer",
      "tipo": "CLIENTE",
      "usuario": {
        "nome": "Bruno Ferreira",
        "cpf": "45678912364",
        "email": "bruno.ferreira@bantads.com.br"
      }
    }
  ]

  /*private readonly httpOptionsComBody = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    }),
    observe: 'response'
  } as const*/

  constructor(
    private httpClient: HttpClient
  ) {}

  login(login: string, senha: string) {
    

    // console.log(login)
    // console.log(senha)
    
    // Por hora, loga-se sem senha -_-
    const credencial = this.users.find(
      u => u.usuario.email === login
    );
          
    // console.log("credencial = " + credencial)
    if(!credencial) {
      return false
    }

    localStorage.setItem("Usuario_logado", JSON.stringify(credencial))
    return credencial;

    /*let body: LoginInfo = {login, senha}

    return this.httpClient.post(
      API_URL + "/login", 
      JSON.stringify(body),
      this.httpOptionsComBody
    )*/
  }

  logout() {

    const usuarioLogado = localStorage.getItem("Usuario_logado")

    if (!usuarioLogado) {
      console.warn("Não há usuário logado!!")
      return
    }

    localStorage.removeItem("Usuario_logado")

    /*return this.httpClient.post(
      API_URL + "/logout",
      null,
      this.httpOptionsComBody
    )*/
  }

  getUsuarioLogado() {
    const user = localStorage.getItem("Usuario_logado");
    return user ? JSON.parse(user) : null;
  }

  getCpf() {
    return this.getUsuarioLogado()?.cpf;
  }
}
//