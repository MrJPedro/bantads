import { Component, computed, OnInit, signal } from '@angular/core';
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
    selector: 'app-consultar-clientes',
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
    templateUrl: './consultar-clientes.html'
})
export class ConsultarClientes implements OnInit {
    clientes = signal<any[]>([]);
    termoBusca = signal('');

    ngOnInit() {
        this.mockDados();
    }

    sortField = 'nome';
    sortOrder = 1;

    clientesFiltrados = computed(() => {
        const termo = this.termoBusca().trim().toLowerCase();

        if (!termo) {
            return this.clientes();
        }

        return this.clientes().filter((cliente) =>
            cliente.nome.toLowerCase().includes(termo) ||
            cliente.cpf.toLowerCase().includes(termo)
        );
    });

    aoPesquisar(valor: string) {
    this.termoBusca.set(valor);
}
    mockDados() {
        this.clientes.set([
            {
                cpf: '123.456.789-00',
                nome: 'Ricardo Silva',
                email: 'ricardo@email.com',
                salario: 3500.00,
                endereco: 'Rua A',
                cidade: 'Curitiba',
                estado: 'PR',
                saldo: 300.00,
                limite: 10500.00
            },
            {
                cpf: '987.654.321-11',
                nome: 'Ana Souza',
                email: 'ana@email.com',
                salario: 1500.00,
                endereco: 'Rua B',
                cidade: 'Pinhais',
                estado: 'PR',
                saldo: 4500.00,
                limite: 9500.00
            }
        ]);
    }
}