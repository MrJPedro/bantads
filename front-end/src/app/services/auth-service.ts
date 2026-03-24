import { Injectable } from '@angular/core';
import { LoginInfo } from '../DTO/auth/login-info';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { API_URL } from '../DTO/api/api';

@Injectable({
  providedIn: 'root',
})

export class AuthService {

  private readonly httpOptionsComBody = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    }),
    observe: 'response'
  } as const

  constructor(
    private httpClient: HttpClient
  ) {}

  login(login: string, senha: string) {
    
    let body: LoginInfo = {login, senha}

    return this.httpClient.post(
      API_URL + "/login",
      JSON.stringify(body),
      this.httpOptionsComBody
    )
  }

  logout() {

    return this.httpClient.post(
      API_URL + "/logout",
      null,
      this.httpOptionsComBody
    )
  }
}
