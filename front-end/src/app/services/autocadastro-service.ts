import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AutocadastroInfo } from '../DTO/cliente/autocadastro-info.dto';
import { DadosClienteResponse } from '../DTO/cliente/dados-cliente-response.dto';


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

  cadastrarCliente(dados: AutocadastroInfo): Observable<DadosClienteResponse> {
    return this.httpClient.post<DadosClienteResponse>(
      API_URL + "/clientes",
      dados,
      this.httpOptions
    )
  }
}
