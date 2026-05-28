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

  aprovarCliente(cpf: string): Observable<ClienteResponse>{
    return this.httpClient.post<ClienteResponse>(
      API_URL + "/clientes/" + cpf + "/aprovar", {}, this.httpOptions
    )
  }

  rejeitarCliente(cpf: string, motivo: string): Observable<ClienteResponse>{
    return this.httpClient.post<ClienteResponse>(
      API_URL + "/clientes/" + cpf + "/rejeitar", { motivo }, this.httpOptions
    )
  }
 
    consultarTodosClientesGer(): Observable<TodosClientesResponse>{
    return this.httpClient.get<TodosClientesResponse>(
      API_URL + "/clientes", this.httpOptions
    )
  }
  
  consultarCliente(cpf: string): Observable<ClienteResponse>{
    return this.httpClient.get<ClienteResponse>(
      API_URL + "/clientes/" + cpf, this.httpOptions
    )
  }
  
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
