import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { TextareaModule } from 'primeng/textarea';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { BarraPesquisa } from "../../../shared/components/barra-pesquisa/barra-pesquisa";

@Component({
    selector: 'app-consultar-cliente-individual',
    standalone: true,
    imports: [
    CommonModule,
    FormsModule,
    TableModule,
    ButtonModule,
    DialogModule,
    TextareaModule,
    ToastModule,
    BarraPesquisa
],
    providers: [MessageService],
    templateUrl: './consultar-cliente-individual.html'
})
export class ConsultarClienteIndividual implements OnInit {
    clientes = signal<any[]>([]);
    termoBusca = signal('');

    ngOnInit() {
        this.mockDados();
    }

    sortField = 'nome';
    sortOrder = 1;
    jaPesquisou = signal(false);
    private somenteNumeros(valor: string): string {
    return (valor || '').replace(/\D/g, '');
}
    resultadoBusca = signal<any[]>([]);
    constructor(private messageService: MessageService) {}

    aoPesquisar(valor: string) {
    this.jaPesquisou.set(true);
    this.termoBusca.set(valor);
    const cpfLimpo = this.somenteNumeros(valor);

    if (cpfLimpo.length !== 11) {
        this.resultadoBusca.set([]);

        this.messageService.add({
            severity: 'warn',
            summary: 'CPF incompleto',
            detail: 'Digite os 11 dígitos do CPF para pesquisar.'
        });

        return;
    }

    const clienteEncontrado = this.clientes().find(
        (cliente) => this.somenteNumeros(cliente.cpf) === cpfLimpo
    );

    this.resultadoBusca.set(clienteEncontrado ? [clienteEncontrado] : []);

    if (!clienteEncontrado) {
        this.messageService.add({
            severity: 'info',
            summary: 'Nenhum resultado',
            detail: 'Nenhum funcionário encontrado para o CPF informado.'
        });
    }
}
    mockDados() {
        this.clientes.set([
            {
                cpf: '123.456.789-00',
                nome: 'Ricardo Silva',
                email: 'ricardo@email.com',
                telefone: '98765-4321',
                salario: 3500.00,
                endereco: 'Rua A',
                cidade: 'Curitiba',
                estado: 'PR',
                conta: '1234',
                saldo: 300.00,
                limite: 10500.00
            },
            {
                cpf: '987.654.321-11',
                nome: 'Ana Souza',
                email: 'ana@email.com',
                telefone: '98765-4321',
                salario: 1500.00,
                endereco: 'Rua B',
                cidade: 'Pinhais',
                estado: 'PR',
                conta: '4321',
                saldo: 4500.00,
                limite: 9500.00
            }
        ]);
    }
}