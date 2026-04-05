import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import * as DTO from '../DTO/cliente';
import { OperacaoResponse } from '../DTO/conta/operacao-response';
import { SaldoResponse } from '../DTO/conta/saldo-response';
import { TransferenciaResponse } from '../DTO/conta/transferencia-response';


const API_URL = "http://localhost:3001"

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
  consultarPerfil(cpf: string): Observable<DTO.DadosClienteResponse> {
    return this.httpClient.get<DTO.DadosClienteResponse>(
      API_URL + "/clientes/" + cpf,
      this.httpOptions
    )
  }

  updatePerfil (CPF: string, novos: DTO.PerfilInfo): Observable<DTO.ClienteResponse>{

    return this.httpClient.put<DTO.ClienteResponse>(
      API_URL + "/clientes/" + CPF, novos, this.httpOptions
    )
  }

  // R05: Depositar
  depositar(cpf: string, valor: number): Observable<OperacaoResponse> {
    return this.httpClient.post<OperacaoResponse>(
      API_URL + "/contas/" + cpf + "/depositar", { valor }, this.httpOptions
    )
  }

  // R06: Saque
  sacar(cpf: string, valor: number): Observable<OperacaoResponse> {
    return this.httpClient.post<OperacaoResponse>(
      API_URL + "/contas/" + cpf + "/sacar", { valor }, this.httpOptions
    )
  }

  // R07: Transferência
  transferir(cpf: string, valor: number, destino: string): Observable<TransferenciaResponse> {
    return this.httpClient.post<TransferenciaResponse>(
      API_URL + "/contas/" + cpf + "/transferir", { destino, valor }, this.httpOptions
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
  
  saldo(numeroConta: string): Observable<SaldoResponse> {
    return this.httpClient.get<SaldoResponse>(
      API_URL + "/contas/" + numeroConta + "/saldo", this.httpOptions
    )
  }
}
