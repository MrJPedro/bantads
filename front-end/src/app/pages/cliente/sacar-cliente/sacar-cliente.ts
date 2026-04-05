import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { InputNumberModule } from 'primeng/inputnumber';
import { ToastModule } from 'primeng/toast';
import { OperacaoResponse } from '../../../DTO/conta/operacao-response';
import { AuthService } from '../../../services/auth-service';
import { Cliente } from '../../../services/cliente-service';

@Component({
  selector: 'app-sacar-cliente',
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    CardModule,
    InputNumberModule,
    ButtonModule,
    ToastModule
  ],
  providers: [MessageService],
  templateUrl: './sacar-cliente.html',
  styleUrl: './sacar-cliente.css',
})
export class SacarCliente {
  valor: number | null = null;
  enviando = false;

  constructor(
    private messageService: MessageService,
    private clienteService: Cliente,
    private authService: AuthService
  ) {}

  sacar(): void {
    const numeroConta = this.authService.getContaNumero();

    if (!numeroConta) {
      this.messageService.add({
        severity: 'error',
        summary: 'Erro',
        detail: 'Conta do usuário não encontrada.'
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
    this.clienteService.sacar(numeroConta, this.valor).subscribe({
      next: (resposta: OperacaoResponse) => {
        this.enviando = false;
        this.valor = null;
        this.messageService.add({
          severity: 'success',
          summary: 'Sucesso',
          detail: `Saque realizado. Saldo atual: ${resposta.saldo.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}`
        });
      },
      error: (erro) => {
        this.enviando = false;
        this.messageService.add({
          severity: 'error',
          summary: 'Erro',
          detail: erro?.error?.erro ?? 'Não foi possível realizar o saque.'
        });
      }
    });
  }
}
