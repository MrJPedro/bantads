import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http'; 
import { Observable } from 'rxjs';

import * as DTO from '../DTO/cliente'


const API_URL = "http://localhost:8080"

@Injectable({
  providedIn: 'root'
})
export class Cliente {

  constructor(
    private httpClient: HttpClient
  ) {}

    private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  }

  // R04: Alteração de Perfil
  updatePerfil (CPF: string, novos: DTO.PerfilInfo): Observable<DTO.ClienteResponse>{

    return this.httpClient.put<DTO.ClienteResponse>(
      API_URL + "/clientes/" + CPF, novos, this.httpOptions
    )
  }

  // R05: Depositar
  depositar(cpf: string, valor: number): Observable<DTO.ClienteResponse> {
    return this.httpClient.post<DTO.ClienteResponse>(
      API_URL + "/contas/" + cpf + "/depositar", { valor }, this.httpOptions
    )
  }

  // R06: Saque
  sacar(cpf: string, valor: number): Observable<DTO.ClienteResponse> {
    return this.httpClient.post<DTO.ClienteResponse>(
      API_URL + "/contas/" + cpf + "/sacar", { valor }, this.httpOptions
    )
  }

  // R07: Transferência
  transferir(cpf: string, valor: number, destino: string): Observable<DTO.ClienteResponse> {
    return this.httpClient.post<DTO.ClienteResponse>(
      API_URL + "/contas/" + cpf + "/depositar", { destino, valor }, this.httpOptions
    )
  }

  //   R08: Consulta de extrato
  consultaExtrato(cpf: string, dataInicio: string, dataFim: string): Observable<DTO.ClienteResponse> {
    return this.httpClient.get<DTO.ClienteResponse>(
      API_URL + "/contas/" + cpf + "/extrato",
      {
        ...this.httpOptions, 
        params: { dataInicio, dataFim } 
      }
    )
  }
}
