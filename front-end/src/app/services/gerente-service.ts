import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable, throwError } from 'rxjs';

import { ClienteResponse, ParaAprovarResponse, TodosClientesResponse } from '../DTO/cliente';
import { AuthService } from './auth-service';
import * as DTO from '../DTO/gerente';

const API_URL = "http://localhost:3001"

@Injectable({
  providedIn: 'root'
})
export class Gerente {


  constructor(
    private httpClient: HttpClient,
    private authService: AuthService
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
    const cpfGerente = this.authService.getCpf()?.replace(/\D/g, '');
    if (!cpfGerente) {
      return throwError(() => new Error('Gerente logado não identificado.'));
    }

    return this.httpClient.get<TodosClientesResponse>(
      API_URL + "/gerentes/" + cpfGerente + "/clientes", this.httpOptions
    ).pipe(
      map((clientes) => [...clientes].sort((a, b) => a.nome.localeCompare(b.nome)))
    )
  }
  
  consultarCliente(cpf: string): Observable<ClienteResponse>{
    const cpfCliente = cpf.replace(/\D/g, '');
    if (cpfCliente.length !== 11) {
      return throwError(
        () => new Error('CPF do cliente inválido.')
      );
    }

    return this.httpClient.get<any>(
      API_URL + "/clientes/" + cpfCliente,
      this.httpOptions
    ).pipe(
      map((cliente) => ({
        cpf: cliente.cpf,
        nome: cliente.nome,
        email: cliente.email,
        telefone: cliente.telefone,
        endereco: cliente.endereco,
        cidade: cliente.cidade,
        estado: cliente.estado,
        conta: cliente.conta ?? '',
        saldo: Number(cliente.saldo ?? 0),
        limite: Number(cliente.limite ?? 0)
      }))
    )
  }
  
  melhoresClientes(): Observable<TodosClientesResponse>{
    return this.httpClient.get<TodosClientesResponse>(
      API_URL + "/clientes",
      {
        ...this.httpOptions,
        params: { filtro: 'melhores_clientes' }
      }
    ).pipe(
      map((clientes) =>
        [...clientes].sort((a, b) => Number(b.saldo) - Number(a.saldo)).slice(0, 3)
      )
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
