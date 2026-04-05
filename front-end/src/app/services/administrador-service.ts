import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { RelatorioClientesResponse } from '../DTO/cliente';
import * as DTO from '../DTO/gerente';

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
    const params = new HttpParams().set('filtro', 'adm_relatorio_clientes');

    return this.httpClient.get<RelatorioClientesResponse>(
      API_URL + "/clientes", {
        ...this.httpOptions,
        params
      }
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
    const params = new HttpParams().set('numero', 'dashboard');

    return this.httpClient.get<DTO.DashboardResponse>(
      API_URL + "/gerentes", {
        ...this.httpOptions,
        params
      }
    )

  }

  consultarGerentesCrud(): Observable<DTO.GerentesResponse> {
    return this.httpClient.get<DTO.GerentesResponse>(
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
