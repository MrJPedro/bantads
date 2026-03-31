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
        login: 'joao@mail.com',
        senha: 'joaopass'
      },
      {
        login: 'kauan@mail.com',
        senha: 'kauanpass'
      },
      {
        login: 'thiago@mail.com',
        senha: 'thiagopass'
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

  login(login: string, senha: string): boolean {
    

    const credencial = this.users.find(
      u => u.login === login && u.senha === senha
    );
          
    if(!credencial) {
      return false
    }

    localStorage.setItem("Usuario_logado", credencial.login)
    return true

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

  loginEstaLogado(login: string): boolean{
    const usuarioLogado = localStorage.getItem("Usuario_logado")
    if(login === usuarioLogado){
      return true
    }
    return false
  }

  getUsuario() {
    const user = localStorage.getItem("Usuario_logado");
    return user ? JSON.parse(user) : null;
  }

  getCpf() {
    return this.getUsuario()?.cpf;
  }
}
//