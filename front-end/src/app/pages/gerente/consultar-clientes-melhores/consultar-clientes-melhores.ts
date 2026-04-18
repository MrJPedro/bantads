import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { TextareaModule } from 'primeng/textarea';
import { MessageService } from 'primeng/api';
import { Gerente } from '../../../services/gerente-service';
import { TodosClientesResponse } from '../../../DTO/cliente';
import { CpfPipe } from '../../../shared/pipes/cpf.pipe';

@Component({
  selector: 'app-consultar-clientes-melhores',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    TableModule,
    ButtonModule,
    DialogModule,
    TextareaModule,
    CpfPipe,
  ],

  providers: [MessageService],
  templateUrl: './consultar-clientes-melhores.html',
})
export class ConsultarClientesMelhores implements OnInit {
  constructor(
    private gerenteService: Gerente,
    private messageService: MessageService,
  ) {}

  clientes = signal<any[]>([]);
  clientesExibidos = computed(() =>
    [...this.clientes()].sort((a, b) => b.saldo - a.saldo).slice(0, 3),
  );

  ngOnInit() {
    // this.mockDados();
    this.carregarClientes();
  }

  carregarClientes() {
    this.gerenteService.melhoresClientes().subscribe({
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

  /*    mockDados() { 
        this.clientes.set([
            {
                cpf: '123.456.789-00',
                nome: 'Ricardo Silva',
                email: 'ricardo@email.com',
                salario: 3500.00,
                endereco: 'Rua A',
                cidade: 'Curitiba',
                estado: 'PR',
                saldo: 300.00
            },
            {
                cpf: '987.654.321-11',
                nome: 'Ana Souza',
                email: 'ana@email.com',
                salario: 1500.00,
                endereco: 'Rua B',
                cidade: 'Pinhais',
                estado: 'PR',
                saldo: 4500.00
            },
            {
                cpf: '111.111.111-11',
                nome: 'João Augusto',
                email: 'joão@email.com',
                salario: 1500.00,
                endereco: 'Rua C',
                cidade: 'Piraquara',
                estado: 'PR',
                saldo: 8000.00
            },
            {
                cpf: '222.222.222-22',
                nome: 'Luana Macedo',
                email: 'luana@email.com',
                salario: 6000.00,
                endereco: 'Rua D',
                cidade: 'Araucária',
                estado: 'PR',
                saldo: 7000.00
            }
        ]);
    } */
}
