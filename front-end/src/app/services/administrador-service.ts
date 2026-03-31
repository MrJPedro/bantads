import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import * as DTO from '../DTO/gerente'
import { RelatorioClientesResponse } from '../DTO/cliente';

const API_URL = "http://localhost:3001"

@Injectable({
  providedIn: 'root'
})
export class AdministradorService {

  constructor(
    private httpClient: HttpClient
  ) {}

    private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  }

  // R15: Tela Inicial Administrador
  mostraGerentes(){

  }

  // R16: Relatório de Clientes
  consultarTodosClientesAdm(): Observable<RelatorioClientesResponse>{
    var filtro = "adm_relatorio_clientes"
    return this.httpClient.get<RelatorioClientesResponse>(
      API_URL + "/clientes", this.httpOptions
    )
  }

  // R17: (CRUD de Gerentes) Inserção de Gerente
  inserirGerente(dados: DTO.DadoGerenteInsercao): Observable<DTO.GerentesResponse>{
    return this.httpClient.post<DTO.GerentesResponse>(
      API_URL + "/gerentes", dados, this.httpOptions
    )
  }

  // R18:  (CRUD de Gerentes) Remoção de Gerente
  removerGerente(cpf: String): Observable<DTO.GerentesResponse> {
    return this.httpClient.delete<DTO.GerentesResponse>(
      API_URL + "/gerentes/" + cpf, this.httpOptions
    )
  }

  // R19:  (CRUD de Gerentes) Listagem de Gerentes
  consultarTodosGerentes(): Observable<DTO.DashboardResponse> {
    return this.httpClient.get<DTO.DashboardResponse>(
      API_URL + "/gerentes", this.httpOptions
    )

  }

  consultarGerente(cpf: String): Observable<DTO.GerentesResponse> {
    return this.httpClient.get<DTO.GerentesResponse>(
      API_URL + "/gerentes/" + cpf, this.httpOptions
    )

  }

  // R20:  (CRUD de Gerentes) Alteração de Gerente
  alterarGerente(cpf: String, novo: DTO.DadoGerenteAtualizacao): Observable<DTO.GerentesResponse> {
    return this.httpClient.put<DTO.GerentesResponse>(
      API_URL + "/gerentes/" + cpf, novo, this.httpOptions
    )
  }

}
