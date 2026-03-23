import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { TextareaModule } from 'primeng/textarea';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { ClienteParaAprovarResponse } from '../../../DTO/cliente/cliente-para-aprovar-response.dto';

@Component({
    selector: 'app-tela-inicial-gerente',
    standalone: true,
    imports: [
        CommonModule,
        FormsModule,
        TableModule,
        ButtonModule,
        DialogModule,
        TextareaModule,
        ToastModule
    ],
    providers: [MessageService],
    templateUrl: './tela-inicial-gerente.html'
})
export class TelaInicialGerente implements OnInit {
    
    clientes = signal<ClienteParaAprovarResponse[]>([]);
    
    dialogRecusa: boolean = false;
    dialogAceita: boolean = false;
    
    clienteSelecionado?: ClienteParaAprovarResponse;
    
    motivoRecusa: string = '';

    constructor(private messageService: MessageService) {}

    ngOnInit() {
        this.mockDados();
    }

    mockDados() {
        this.clientes.set([
            { cpf: '123.456.789-00', nome: 'Ricardo Silva', email: 'ricardo@email.com', salario: 3500.00, endereco: 'Rua A', cidade: 'Curitiba', estado: 'PR' },
            { cpf: '987.654.321-11', nome: 'Ana Souza', email: 'ana@email.com', salario: 1500.00, endereco: 'Rua B', cidade: 'Pinhais', estado: 'PR' }
        ]);
    }

    aprovar(cliente: ClienteParaAprovarResponse) {
        this.clienteSelecionado = cliente;
        this.dialogAceita = true;
    }

    rejeitar(cliente: ClienteParaAprovarResponse) {
        this.clienteSelecionado = cliente;
        this.motivoRecusa = '';
        this.dialogRecusa = true;
    }

    fecharDialogAceite() {
        this.dialogAceita = false;
        this.clienteSelecionado = undefined;
    }

    fecharDialogRecusa() {
        this.dialogRecusa = false;
        this.clienteSelecionado = undefined;
        this.motivoRecusa = '';
    }

    confirmarRecusa() {
    if (this.motivoRecusa.trim() && this.clienteSelecionado) {
        this.clientes.set(this.clientes().filter(c => c.cpf !== this.clienteSelecionado?.cpf));
        this.dialogRecusa = false;
        this.messageService.add({ 
            severity: 'info', 
            summary: 'Rejeitado', 
            detail: 'Cadastro removido da lista' 
        });
    }
}

    confirmarAceite() {
        if (this.clienteSelecionado) {
            this.clientes.set(
                this.clientes().filter(c => c.cpf !== this.clienteSelecionado?.cpf)
            );

            this.dialogAceita = false;
            this.clienteSelecionado = undefined;

            this.messageService.add({
                severity: 'success',
                summary: 'Aceito',
                detail: 'Cadastro aprovado'
            });
        }
    }
}