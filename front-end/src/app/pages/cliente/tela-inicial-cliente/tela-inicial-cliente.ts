import { CommonModule } from '@angular/common';
import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { DialogModule } from 'primeng/dialog';
import { TextareaModule } from 'primeng/textarea';
import { ToastModule } from 'primeng/toast';
import { SaldoResponse } from '../../../DTO/conta/saldo-response';
import { AuthService } from '../../../services/auth-service';
import { Cliente } from '../../../services/cliente-service';
import { CpfPipe } from '../../../shared/pipes/cpf.pipe';

@Component({
    selector: 'app-tela-inicial-cliente',
    standalone: true,
    imports: [
        CommonModule,
        FormsModule,
        RouterModule,
        CardModule,
        ButtonModule,
        DialogModule,
        TextareaModule,
        ToastModule,
        CpfPipe
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
        const numeroConta = this.authService.getContaNumero();

        if (!numeroConta) {
            this.messageService.add({
                severity: 'error',
                summary: 'Erro',
                detail: 'Conta do usuário não encontrada.'
            });
            return;
        }

        this.clienteService.saldo(numeroConta).subscribe({
            next: (saldo: SaldoResponse) => {
  
                // se encontrou
                this.conta.set(saldo);
                this.cor.set(this.conta().saldo >= 0.0 ? 'green' : 'red');
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
