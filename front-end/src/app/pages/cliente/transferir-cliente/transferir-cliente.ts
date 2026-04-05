import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { InputNumberModule } from 'primeng/inputnumber';
import { InputTextModule } from 'primeng/inputtext';
import { ToastModule } from 'primeng/toast';
import { TransferenciaResponse } from '../../../DTO/conta/transferencia-response';
import { AuthService } from '../../../services/auth-service';
import { Cliente } from '../../../services/cliente-service';

@Component({
  selector: 'app-transferir-cliente',
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    CardModule,
    InputTextModule,
    InputNumberModule,
    ButtonModule,
    ToastModule
  ],
  providers: [MessageService],
  templateUrl: './transferir-cliente.html',
  styleUrl: './transferir-cliente.css',
})
export class TransferirCliente {
  contaDestino = '';
  valor: number | null = null;
  enviando = false;

  constructor(
    private messageService: MessageService,
    private clienteService: Cliente,
    private authService: AuthService
  ) {}

  transferir(): void {
    const numeroConta = this.authService.getContaNumero();

    if (!numeroConta) {
      this.messageService.add({
        severity: 'error',
        summary: 'Erro',
        detail: 'Conta do usuário não encontrada.'
      });
      return;
    }

    if (!this.contaDestino.trim()) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Atenção',
        detail: 'Informe a conta de destino.'
      });
      return;
    }

    if (this.contaDestino.trim() === numeroConta) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Atenção',
        detail: 'A conta de destino deve ser diferente da conta de origem.'
      });
      return;
    }

    if (!this.valor || this.valor <= 0) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Atenção',
        detail: 'Informe um valor maior que zero.'
      });
      return;
    }

    this.enviando = true;
    this.clienteService.transferir(numeroConta, this.valor, this.contaDestino.trim()).subscribe({
      next: (resposta: TransferenciaResponse) => {
        this.enviando = false;
        this.contaDestino = '';
        this.valor = null;
        this.messageService.add({
          severity: 'success',
          summary: 'Sucesso',
          detail: `Transferência para conta ${resposta.destino} realizada. Saldo atual: ${resposta.saldo.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}`
        });
      },
      error: (erro) => {
        this.enviando = false;
        this.messageService.add({
          severity: 'error',
          summary: 'Erro',
          detail: erro?.error?.erro ?? 'Não foi possível realizar a transferência.'
        });
      }
    });
  }
}
