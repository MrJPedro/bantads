import { CommonModule } from '@angular/common';
import { Component, computed, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { TableModule } from 'primeng/table';
import { TextareaModule } from 'primeng/textarea';
import { ToastModule } from 'primeng/toast';
import { ClienteResponse, TodosClientesResponse } from '../../../DTO/cliente';
import { Gerente } from '../../../services/gerente-service';
import { BarraPesquisa } from "../../../shared/components/barra-pesquisa/barra-pesquisa";
import { CpfPipe } from '../../../shared/pipes/cpf.pipe';

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
    BarraPesquisa,
    CpfPipe
],
    providers: [MessageService],
    templateUrl: './consultar-clientes.html'
})
export class ConsultarClientes implements OnInit {
    
    ngOnInit() {
        this.carregarClientes();
    }

    constructor(
        private gerenteService: Gerente,
        private messageService: MessageService
    ) {}
    
    clientes = signal<ClienteResponse[]>([]);
    termoBusca = signal('');
    clienteSelecionado = signal<ClienteResponse | null>(null);
    modalDetalhesVisivel = signal(false);

    sortField = 'nome';
    sortOrder = 1;

    carregarClientes(){

        this.gerenteService.consultarTodosClientesGer().subscribe({
            next: (clientes: TodosClientesResponse) => {

                // se encontrou
                this.clientes.set(clientes);
            },

            error: (err) => {
                console.error(err);

                this.clientes.set([]);

                this.messageService.add({
                    severity: 'error',
                    summary: 'Erro',
                    detail: 'Clientes não encontrados ou erro no servidor.'
                });
            }
        });
    }

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

    abrirModalDetalhes(cliente: ClienteResponse) {
        this.clienteSelecionado.set(cliente);
        this.modalDetalhesVisivel.set(true);
    }

    fecharModalDetalhes() {
        this.modalDetalhesVisivel.set(false);
        this.clienteSelecionado.set(null);
    }
/*  mockDados() {
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
    } */
}