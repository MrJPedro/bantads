import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { DatePickerModule } from 'primeng/datepicker';
import { TableModule } from 'primeng/table';

import { ItemExtratoResponse } from '../../../DTO/gerente/item-extrato-response.dto';
import { AuthService } from '../../../services/auth-service';
import { Cliente } from '../../../services/cliente-service';


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

    const dataInicioHistoricoStr = new Date(1970, 0, 1).toISOString();
    const dataFimStr = this.dataFim.toISOString();

    this.clienteService.consultaExtrato(this.numeroConta, dataInicioHistoricoStr, dataFimStr)
      .subscribe({
        next: (response: any) => {
          if (response && response.movimentacoes) {
            this.processarExtratoDaApi(response.movimentacoes);
          } else {
            this.movimentacoes = [];
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

  private calcularDeltaSaldo(movimentacao: ItemExtratoResponse): number {
    if (movimentacao.tipo === 'depósito') {
      return movimentacao.valor;
    }

    if (movimentacao.tipo === 'saque') {
      return -movimentacao.valor;
    }

    if (movimentacao.tipo === 'transferência') {
      if (movimentacao.destino === this.numeroConta) {
        return movimentacao.valor;
      }

      if (movimentacao.origem === this.numeroConta) {
        return -movimentacao.valor;
      }
    }

    return 0;
  }

  private processarExtratoDaApi(movimentacoesDaApi: ItemExtratoResponse[]) {
    this.movimentacoes = [];
    const inicioPeriodo = new Date(this.dataInicio);
    const fimPeriodo = new Date(this.dataFim);

    inicioPeriodo.setHours(0, 0, 0, 0);
    fimPeriodo.setHours(0, 0, 0, 0);

    const movimentacoesOrdenadas = [...movimentacoesDaApi].sort(
      (a, b) => new Date(a.data).getTime() - new Date(b.data).getTime()
    );

    let saldoAtual = movimentacoesOrdenadas.reduce((saldo, movimentacao) => {
      const dataMovimentacao = new Date(movimentacao.data);
      return dataMovimentacao < inicioPeriodo
        ? saldo + this.calcularDeltaSaldo(movimentacao)
        : saldo;
    }, 0);

    let dataAtual = new Date(inicioPeriodo);
    const dataLimite = new Date(fimPeriodo);

    while (dataAtual <= dataLimite) {
      const transacoesDoDia = movimentacoesOrdenadas.filter(m => {
        const dataTx = new Date(m.data);
        return dataTx.getDate() === dataAtual.getDate() &&
               dataTx.getMonth() === dataAtual.getMonth() &&
               dataTx.getFullYear() === dataAtual.getFullYear();
      });

      const dataReferenciaPrimitiva = dataAtual.getTime(); 

      if (transacoesDoDia.length > 0) {
        
        const variacaoDoDia = transacoesDoDia.reduce(
          (acumulado, movimentacao) => acumulado + this.calcularDeltaSaldo(movimentacao),
          0
        );
        const saldoFinalDoDia = saldoAtual + variacaoDoDia;

        transacoesDoDia.forEach(m => {
          const ehEntrada = this.calcularDeltaSaldo(m) >= 0;
          let tipoMovimento: 'ENTRADA' | 'SAIDA' = ehEntrada ? 'ENTRADA' : 'SAIDA';
          let operacao: 'Depósito' | 'Saque' | 'Transferência' = 'Depósito';
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
            } else if (m.destino === this.numeroConta) {
              tipoMovimento = 'ENTRADA';
              clienteOrigemDestino = m.origem || '';
            }
          }

          this.movimentacoes.push({
            dataReferencia: dataReferenciaPrimitiva, 
            saldoDia: saldoFinalDoDia,
            diaSemMovimento: false,
            dataHora: new Date(m.data),
            operacao,
            clienteOrigemDestino: clienteOrigemDestino,
            valor: m.valor,
            tipoMovimento
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