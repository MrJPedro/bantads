import { Router } from '@angular/router';
import { SaldoResponse } from '../../../DTO/conta/saldo-response';
import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { TextareaModule } from 'primeng/textarea';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { Cliente } from '../../../services/cliente-service';
import { AuthService } from '../../../services/auth-service';

@Component({
    selector: 'app-tela-inicial-cliente',
    standalone: true,
    imports: [
        CommonModule,
        FormsModule,
        CardModule,
        ButtonModule,
        DialogModule,
        TextareaModule,
        ToastModule
    ],
    providers: [MessageService],
    templateUrl: './tela-inicial-cliente.html'
})
export class TelaInicialCliente {

  conta = signal<SaldoResponse>({} as SaldoResponse);

  cor = signal(this.conta().saldo >= 0.0 ? 'green' : 'red');

  constructor(
    private messageService: MessageService,
    private clienteService: Cliente,
    private authService: AuthService
) {}

  ngOnInit() {
        // this.mockDados();
        this.carregarSaldo();
  }

    carregarSaldo(){
  
        this.clienteService.saldo(this.authService.getCpf()).subscribe({
            next: (saldo: SaldoResponse) => {
  
                // se encontrou
                this.conta.set(saldo);
            },
  
            error: (err) => {
                console.error(err);
  
                this.conta.set({
                    cliente: "",
                    conta: "",
                    saldo: 0
                    } as SaldoResponse);
  
                this.messageService.add({
                    severity: 'error',
                    summary: 'Erro',
                    detail: 'Conta não encontrada ou erro no servidor.'
                });
            }
        });
      }
/*  mockDados() {
        this.conta.set({ 
          ...this.conta(), 
          cliente: 'Ricardo Silva', 
          conta: '1234', 
          saldo: 1500.00 });

        this.cor.set(this.conta().saldo >= 0.0 ? 'green' : 'red');
    } */

}
