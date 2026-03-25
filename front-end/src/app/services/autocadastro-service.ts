import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AutocadastroInfo } from '../DTO/cliente/autocadastro-info.dto';


const API_URL = "http://localhost:3001"

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
