import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { LoginInfo } from '../DTO/auth/login-info';
import { LoginResponse } from '../DTO/auth/login-response';

const API_URL = "http://localhost:3001"

@Injectable({
  providedIn: 'root',
})

export class AuthService {

  private http = inject(HttpClient);

  private readonly users = [
    {
      "access_token": "string",
      "token_type": "bearer",
      "tipo": "CLIENTE",
      "usuario": {
        "nome": "Carlos Oliveira",
        "cpf": "52998224725",
        "email": "carlos.oliveira@bantads.com.br"
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

  login(credentials: LoginInfo): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${API_URL}/login`, credentials);
  }


  storeCredentials(credencial: LoginResponse): void {
    localStorage.setItem("Usuario_logado", JSON.stringify(credencial))
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
    if (!user) {
      return null;
    }

    try {
      return JSON.parse(user);
    } catch {
      return null;
    }
  }

  getCpf() {
    const usuarioLogado = this.getUsuarioLogado();
    return usuarioLogado?.cpf ?? usuarioLogado?.usuario?.cpf ?? null;
  }

  getContaNumero() {
    const usuarioLogado = this.getUsuarioLogado();
    return usuarioLogado?.conta?.numero ?? usuarioLogado?.cliente?.conta?.numero ?? null;
  }

  getToken() {
    const usuarioLogado = this.getUsuarioLogado();
    return usuarioLogado?.token ?? usuarioLogado?.access_token ?? null;
  }
}
//