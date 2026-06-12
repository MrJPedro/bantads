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
import { BarraPesquisa } from '../../../shared/components/barra-pesquisa/barra-pesquisa';
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
    CpfPipe,
  ],
  providers: [MessageService],
  templateUrl: './consultar-clientes.html',
})
export class ConsultarClientes implements OnInit {
  ngOnInit() {
    this.carregarClientes();
  }

  constructor(
    private gerenteService: Gerente,
    private messageService: MessageService,
  ) {}

  clientes = signal<ClienteResponse[]>([]);
  termoBusca = signal('');
  clienteSelecionado = signal<ClienteResponse | null>(null);
  modalDetalhesVisivel = signal(false);

  sortField = 'nome';
  sortOrder = 1;

  carregarClientes() {
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
          detail: 'Clientes não encontrados ou erro no servidor.',
        });
      },
    });
  }

  clientesFiltrados = computed(() => {
    const termo = this.termoBusca().trim().toLowerCase();

    if (!termo) {
      return this.clientes();
    }

    return this.clientes().filter(
      (cliente) =>
        cliente.nome.toLowerCase().includes(termo) || cliente.cpf.toLowerCase().includes(termo),
    );
  });

  aoPesquisar(valor: string) {
    this.termoBusca.set(valor);
  }

  abrirModalDetalhes(cliente: ClienteResponse) {
    this.gerenteService.consultarCliente(cliente.cpf).subscribe({
      next: (clienteCompleto) => {
        this.clienteSelecionado.set(clienteCompleto);
        this.modalDetalhesVisivel.set(true);
      },
      error: (err) => {
        console.error(err);
        this.messageService.add({
          severity: 'error',
          summary: 'Erro',
          detail: 'Não foi possível carregar os detalhes do cliente.',
        });
      },
    });
  }

  fecharModalDetalhes() {
    this.modalDetalhesVisivel.set(false);
    this.clienteSelecionado.set(null);
  }
}
