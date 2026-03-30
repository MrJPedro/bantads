import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { TextareaModule } from 'primeng/textarea';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { ClienteRelatorio } from '../../../DTO/administrador/cliente-relatorio.dto';

@Component({
  selector: 'app-relatorio-clientes',
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
  templateUrl: './relatorio-clientes.html',
})

export class RelatorioClientes implements OnInit {

  clientes = signal<ClienteRelatorio[]>([]);

  clienteSelecionado?: ClienteRelatorio;

  constructor(private messageService: MessageService) {}

  ngOnInit(){
    this.mockDados();
  }

  mockDados(){
    this.clientes.set([
      {
        cpf: '123.456.789-00',
        nome: 'Rosalina',
        email: 'rosalina@gmail.com',
        salario: 1000,
        numConta: 0o1,
        saldo: 1500,
        limite: 8000,
        nomeGerente: 'Mario',
        cpfGerente: '000.000.000-00'
      },
      {
        cpf: '321.654.987-12',
        nome: 'Peach',
        email: 'peach@gmail.com',
        salario: 2000,
        numConta: 0o2,
        saldo: 2000,
        limite: 10000,
        nomeGerente: 'Luigi',
        cpfGerente: '999.999.999-99'
      },
      {
        cpf: '213.546.879-98',
        nome: 'Daisy',
        email: 'daisy@gmail.com',
        salario: 3000,
        numConta: 0o3,
        saldo: 3500,
        limite: 35000,
        nomeGerente: 'Mario',
        cpfGerente: '000.000.000-00'
      },
    ].sort((a, b) => a.nome.localeCompare(b.nome)));
  }
}
