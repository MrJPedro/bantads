import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { DatePickerModule } from 'primeng/datepicker';
import { ButtonModule } from 'primeng/button';

export interface Movimentacao {
  dataReferencia: Date;
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
  templateUrl: './extrato-cliente.html',
  styleUrl: './extrato-cliente.css',
})
export class ExtratoCliente implements OnInit {
  dataInicio!: Date;
  dataFim!: Date;
  
  // Array final que será consumido pelo p-table
  movimentacoes: Movimentacao[] = [];

  // Saldo inicial fictício (antes do período filtrado)
  saldoInicial = 2500.00;

  // Mock de transações "brutas" vindas de uma suposta API
  private mockDataBruto: any[] = [];

  ngOnInit() {
    // Define o período inicial para os últimos 5 dias
    const hoje = new Date();
    this.dataFim = new Date(hoje);
    
    this.dataInicio = new Date(hoje);
    this.dataInicio.setDate(hoje.getDate() - 4);

    this.gerarMockDatabase();
    this.filtrar();
  }

  gerarMockDatabase() {
    // Gerando dados baseados na data atual para sempre ter resultados ao testar
    const d1 = new Date(); d1.setDate(d1.getDate() - 4); d1.setHours(10, 30, 0);
    const d2 = new Date(); d2.setDate(d2.getDate() - 4); d2.setHours(15, 45, 0);
    const d3 = new Date(); d3.setDate(d3.getDate() - 2); d3.setHours(9, 15, 0);
    const d4 = new Date(); d4.setDate(d4.getDate()); d4.setHours(14, 20, 0);

    this.mockDataBruto = [
      { dataHora: d1, operacao: 'Depósito', clienteOrigemDestino: '', valor: 1500.00, tipoMovimento: 'ENTRADA' },
      { dataHora: d2, operacao: 'Transferência', clienteOrigemDestino: 'João Silva', valor: 350.00, tipoMovimento: 'SAIDA' },
      // O dia (Hoje - 3) ficará sem movimentação propositalmente para testar o HTML
      { dataHora: d3, operacao: 'Saque', clienteOrigemDestino: '', valor: 200.00, tipoMovimento: 'SAIDA' },
      // O dia (Hoje - 1) também ficará sem movimentação
      { dataHora: d4, operacao: 'Transferência', clienteOrigemDestino: 'Maria Souza', valor: 800.00, tipoMovimento: 'ENTRADA' }
    ];
  }

  filtrar() {
    if (!this.dataInicio || !this.dataFim) return;

    this.movimentacoes = [];
    let saldoAtual = this.saldoInicial;

    // Clonando as datas para não alterar os valores do ngModel
    let dataAtual = new Date(this.dataInicio);
    const dataLimite = new Date(this.dataFim);

    // Zerando as horas para comparar apenas os dias
    dataAtual.setHours(0, 0, 0, 0);
    dataLimite.setHours(0, 0, 0, 0);

    // Loop dia a dia da data início até a data fim
    while (dataAtual <= dataLimite) {
      // Busca transações apenas do dia atual do loop
      const transacoesDoDia = this.mockDataBruto.filter(t => {
        const dataTx = new Date(t.dataHora);
        return dataTx.getDate() === dataAtual.getDate() &&
               dataTx.getMonth() === dataAtual.getMonth() &&
               dataTx.getFullYear() === dataAtual.getFullYear();
      });

      if (transacoesDoDia.length > 0) {
        // Se houver transações no dia, processa cada uma delas
        transacoesDoDia.forEach(tx => {
          if (tx.tipoMovimento === 'ENTRADA') {
            saldoAtual += tx.valor;
          } else {
            saldoAtual -= tx.valor;
          }

          this.movimentacoes.push({
            dataReferencia: new Date(dataAtual), // Campo chave do groupRowsBy
            saldoDia: saldoAtual,
            diaSemMovimento: false,
            dataHora: tx.dataHora,
            operacao: tx.operacao,
            clienteOrigemDestino: tx.clienteOrigemDestino,
            valor: tx.valor,
            tipoMovimento: tx.tipoMovimento
          });
        });
      } else {
        // Se NÃO houver transações, cria um registro dummy apenas para exibir a data e o saldo
        this.movimentacoes.push({
          dataReferencia: new Date(dataAtual),
          saldoDia: saldoAtual,
          diaSemMovimento: true
        });
      }

      // Avança para o próximo dia
      dataAtual.setDate(dataAtual.getDate() + 1);
    }
  }
}