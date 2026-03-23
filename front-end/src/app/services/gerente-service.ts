import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http'; 
import { Observable } from 'rxjs';

import * as DTO from '../DTO/gerente'
import { ClienteResponse } from '../DTO/cliente';

const API_URL = "http://localhost:8080"

@Injectable({
  providedIn: 'root'
})
export class Gerente {


  constructor(
    private httpClient: HttpClient
  ) {}

    private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  }

  //  R03: Tela Inicial Cliente
  clistesParaAprovar(filtro: string): Observable<ClienteResponse>{
    filtro = "para_aprovar"
    return this.httpClient.get<ClienteResponse>(
      API_URL + "/clientes",
      {
       ...this.httpOptions, 
        params: { filtro }  
      }
    )
  }

  //  R10: Aprovar Cliente
  aprovarCliente(cpf: string): Observable<ClienteResponse>{
    return this.httpClient.post<ClienteResponse>(
      API_URL + "/clientes/" + cpf + "/aprovar", this.httpOptions
    )
  }

  //  R11: Rejeitar Cliente
  rejeitarCliente(cpf: string): Observable<ClienteResponse>{
    return this.httpClient.post<ClienteResponse>(
      API_URL + "/clientes/" + cpf + "/rejeitar", this.httpOptions
    )
  }

  //  R12: Consultar Todos os Clientes
    consultarTodosClientes(): Observable<ClienteResponse>{
    return this.httpClient.get<ClienteResponse>(
      API_URL + "/clientes", this.httpOptions
    )
  }
  
  //  R13: Consultar Cliente
  consultarCliente(cpf: string): Observable<ClienteResponse>{
    return this.httpClient.get<ClienteResponse>(
      API_URL + "/clientes/" + cpf, this.httpOptions
    )
  }
  
  // R14: Consultar 3 Melhores Clientes
  melhoresClientes(filtro: string): Observable<ClienteResponse>{
    filtro = "melhores_clientes"
    return this.httpClient.get<ClienteResponse>(
      API_URL + "/clientes",
      {
        ...this.httpOptions, 
        params: { filtro } 
      }
    )
  }  
}
