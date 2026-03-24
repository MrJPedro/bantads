import { Injectable } from '@angular/core';
import { LoginInfo } from '../dto/auth/login-info';
import { HttpClient, HttpHeaders } from '@angular/common/http';

const API_URL = "http://localhost:3001"

@Injectable({
  providedIn: 'root',
})

export class AuthService {
  
  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  }

  constructor(
    private httpClient: HttpClient
  ) {}

  login(login: LoginInfo) {
    
    let body: LoginInfo = login

    return this.httpClient.post(
      API_URL + "/login",
      body,
      this.httpOptions
    )
  }

  logout() {
    // Obtém token do LocalStorage
    // token = LocalStorage[token]
    let token: string = ""

    let body: string = token

    return this.httpClient.post(
      API_URL + "/logout",
      body,
      this.httpOptions
    )
  }
}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//