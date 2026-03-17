import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AutocadastroInfo } from '../DTO/cliente/autocadastro-info';
import { Observable } from 'rxjs';


const API_URL = "http://localhost:8080"

@Injectable({
  providedIn: 'root',
})
export class AutocadastroService {
  
  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  }
  
  constructor(
    private httpClient: HttpClient
  ) {}

  cadastrarCliente(dados: AutocadastroInfo) {

    let body: AutocadastroInfo = dados

    return this.httpClient.post(
      API_URL + "/clientes",
      body,
      this.httpOptions
    )
  }
}
