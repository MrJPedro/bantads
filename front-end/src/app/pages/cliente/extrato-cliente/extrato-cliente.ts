import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { TableModule } from 'primeng/table';
import { DatePickerModule } from 'primeng/datepicker';
import { ButtonModule } from 'primeng/button';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';

import { AuthService } from '../../../services/auth-service';
import { Cliente } from '../../../services/cliente-service'; 
import { ItemExtratoResponse } from '../../../DTO/gerente/item-extrato-response.dto';


export interface Movimentacao {
  dataReferencia: number; 
  saldoDia: number;
  diaSemMovimento?: boolean;
  dataHora?: Date;
  operacao?: 'Depósito' | 'Saque' | 'Transferência';
  clienteOrigemDestino?: string;
  valor?: number;
  tipoMovimento?: 'ENTRADA' | 'SAIDA';
}

@Component({
  selector: 'app-extrato-cliente',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    TableModule,
    DatePickerModule,
    ButtonModule
  ],
  providers: [MessageService],
  templateUrl: './extrato-cliente.html',
  styleUrl: './extrato-cliente.css',
})

export class ExtratoCliente implements OnInit {
  dataInicio!: Date;
  dataFim!: Date;
  numeroConta!: string;

  movimentacoes: Movimentacao[] = [];

  constructor(
    private messageService: MessageService,
    private clienteService: Cliente,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.numeroConta = this.authService.getContaNumero();

    if (!this.numeroConta) {
      this.messageService.add({
        severity: 'error',
        summary: 'Erro',
        detail: 'Conta do usuário não encontrada.'
      });
      return; 
    }

    const hoje = new Date();
    this.dataFim = new Date(hoje);
    
    this.dataInicio = new Date();
    this.dataInicio.setFullYear(2025);
    this.dataInicio.setMonth(6);
    this.dataInicio.setDate(15); 
    this.filtrar();
  }

  filtrar() {
    if (!this.dataInicio || !this.dataFim || !this.numeroConta) return;

    const dataInicioStr = this.dataInicio.toISOString();
    const dataFimStr = this.dataFim.toISOString();

    this.clienteService.consultaExtrato(this.numeroConta, dataInicioStr, dataFimStr)
      .subscribe({
        next: (response: any) => {
          if (response && response.movimentacoes) {
            this.processarExtratoDaApi(response.movimentacoes);
          }
        },
        error: (erro) => {
          console.error("Erro ao buscar as movimentações: ", erro);
          this.messageService.add({
            severity: 'error',
            summary: 'Erro',
            detail: 'Falha ao buscar movimentações.'
          });
        }
      });
  }

  private processarExtratoDaApi(movimentacoesDaApi: ItemExtratoResponse[]) {
    this.movimentacoes = [];
    let saldoAtual = 0; 

    let dataAtual = new Date(this.dataInicio);
    const dataLimite = new Date(this.dataFim);

    dataAtual.setHours(0, 0, 0, 0);
    dataLimite.setHours(0, 0, 0, 0);

    while (dataAtual <= dataLimite) {
      const transacoesDoDia = movimentacoesDaApi.filter(m => {
        const dataTx = new Date(m.data);
        return dataTx.getDate() === dataAtual.getDate() &&
               dataTx.getMonth() === dataAtual.getMonth() &&
               dataTx.getFullYear() === dataAtual.getFullYear();
      });

      const dataReferenciaPrimitiva = dataAtual.getTime(); 

      if (transacoesDoDia.length > 0) {
        
        let saldoFinalDoDia = saldoAtual;
        transacoesDoDia.forEach(m => {
          const ehEntrada = m.tipo === 'depósito' || (m.tipo === 'transferência' && m.destino === this.numeroConta);
          if (ehEntrada) {
            saldoFinalDoDia += m.valor;
          } else {
            saldoFinalDoDia -= m.valor;
          }
        });

        transacoesDoDia.forEach(m => {
          let tipoMovimento = 'ENTRADA';
          let operacao = 'Depósito';
          let clienteOrigemDestino = '';

          if (m.tipo === 'depósito') {
            tipoMovimento = 'ENTRADA';
            operacao = 'Depósito';
          } else if (m.tipo === 'saque') {
            tipoMovimento = 'SAIDA';
            operacao = 'Saque';
          } else if (m.tipo === 'transferência') {
            operacao = 'Transferência';
            
            if (m.origem === this.numeroConta) {
              tipoMovimento = 'SAIDA';
              clienteOrigemDestino = m.destino || '';
            } else {
              tipoMovimento = 'ENTRADA';
              clienteOrigemDestino = m.origem || '';
            }
          }

          this.movimentacoes.push({
            dataReferencia: dataReferenciaPrimitiva, 
            saldoDia: saldoFinalDoDia,
            diaSemMovimento: false,
            dataHora: new Date(m.data),
            operacao: operacao as any,
            clienteOrigemDestino: clienteOrigemDestino,
            valor: m.valor,
            tipoMovimento: tipoMovimento as any
          });
        });

        saldoAtual = saldoFinalDoDia;

      } else {
        this.movimentacoes.push({
          dataReferencia: dataReferenciaPrimitiva,
          saldoDia: saldoAtual,
          diaSemMovimento: true
        });
      }

      dataAtual.setDate(dataAtual.getDate() + 1);
    }
  }
}