import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { ClienteResponse, ParaAprovarResponse, TodosClientesResponse } from '../DTO/cliente';
import * as DTO from '../DTO/gerente';

const API_URL = "http://localhost:3001"

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
  clientesParaAprovar(): Observable<ParaAprovarResponse>{
    var filtro = "para_aprovar"
    return this.httpClient.get<ParaAprovarResponse>(
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
    consultarTodosClientesGer(): Observable<TodosClientesResponse>{
    return this.httpClient.get<TodosClientesResponse>(
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
  melhoresClientes(): Observable<TodosClientesResponse>{
    var filtro = "melhores_clientes"
    return this.httpClient.get<TodosClientesResponse>(
      API_URL + "/clientes",
      {
        ...this.httpOptions, 
        params: { filtro } 
      }
    )
  }

  consultarGerentePorCpf(cpf: string): Observable<DTO.DadoGerente[]> {
    return this.httpClient.get<DTO.DadoGerente[]>(
      API_URL + "/gerentes",
      {
        ...this.httpOptions,
        params: { cpf }
      }
    )
  }
}
