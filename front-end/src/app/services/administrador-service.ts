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

  consultarTodosClientesAdm(): Observable<RelatorioClientesResponse>{
    const params = new HttpParams().set('filtro', 'adm_relatorio_clientes');

    return this.httpClient.get<RelatorioClientesResponse>(
      API_URL + "/clientes", {
        ...this.httpOptions,
        params
      }
    )
  }

  inserirGerente(dados: DTO.DadoGerenteInsercao): Observable<DTO.DadoGerente>{
    return this.httpClient.post<DTO.DadoGerente>(
      API_URL + "/gerentes", dados, this.httpOptions
    )
  }

  removerGerente(cpf: string): Observable<DTO.DadoGerente> {
    return this.httpClient.delete<DTO.DadoGerente>(
      API_URL + "/gerentes/" + cpf, this.httpOptions
    )
  }

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

  consultarGerente(cpf: string): Observable<DTO.DadoGerente> {
    return this.httpClient.get<DTO.DadoGerente>(
      API_URL + "/gerentes/" + cpf, this.httpOptions
    )

  }

  alterarGerente(cpf: string, novo: DTO.DadoGerenteAtualizacao): Observable<DTO.DadoGerente> {
    return this.httpClient.put<DTO.DadoGerente>(
      API_URL + "/gerentes/" + cpf, novo, this.httpOptions
    )
  }

}
