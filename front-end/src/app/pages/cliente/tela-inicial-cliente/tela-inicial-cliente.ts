import { Router } from '@angular/router';
import { SaldoResponde } from '../../../dto/conta/saldo-responde';
import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { TextareaModule } from 'primeng/textarea';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { ClienteParaAprovarResponse } from '../../../dto/cliente/cliente-para-aprovar-response.dto';

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

  conta = signal<SaldoResponde>({} as SaldoResponde);

  cor = signal(this.conta().saldo >= 0.0 ? 'green' : 'red');

  constructor(private messageService: MessageService) {}

  ngOnInit() {
        this.mockDados();
  }

  mockDados() {
        this.conta.set({ 
          ...this.conta(), 
          cliente: 'Ricardo Silva', 
          conta: '1234', 
          saldo: 1500.00 });

        this.cor.set(this.conta().saldo >= 0.0 ? 'green' : 'red');
    }

}
