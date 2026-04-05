import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { TableModule } from 'primeng/table';
import { TextareaModule } from 'primeng/textarea';
import { ToastModule } from 'primeng/toast';
import { RelatorioClientesResponse } from '../../../DTO/cliente';
import { AdministradorService } from '../../../services/administrador-service';
import { CpfPipe } from '../../../shared/pipes/cpf.pipe';

interface ClienteRelatorioView {
  cpf: string;
  nome: string;
  email: string;
  salario: number;
  conta: string | number | null;
  saldo: number | string | null;
  limite: number | null;
  gerenteCpf: string | null;
  gerenteNome: string | null;
}

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
      ToastModule,
      CpfPipe
  ],
  providers: [MessageService],
  templateUrl: './relatorio-clientes.html',
})

export class RelatorioClientes implements OnInit {

  clientes = signal<ClienteRelatorioView[]>([]);

  constructor(
    private messageService: MessageService,
    private administradorService: AdministradorService
  ) {}

  ngOnInit(){
    // this.mockDados(); 
    this.carregarClientes();
  }

  carregarClientes(){

      this.administradorService.consultarTodosClientesAdm().subscribe({
          next: (clientes: RelatorioClientesResponse) => {
              const clientesNormalizados = clientes.map((cliente) => {
                const clienteComCampos = cliente as unknown as {
                  numConta?: string | number;
                  cpfGerente?: string;
                  nomeGerente?: string;
                };

                return {
                  cpf: cliente.cpf,
                  nome: cliente.nome,
                  email: cliente.email,
                  salario: cliente.salario,
                  conta: cliente.conta ?? clienteComCampos.numConta ?? null,
                  saldo: cliente.saldo ?? null,
                  limite: cliente.limite ?? null,
                  gerenteCpf: cliente.gerente ?? clienteComCampos.cpfGerente ?? null,
                  gerenteNome: cliente.gerente_nome ?? clienteComCampos.nomeGerente ?? null,
                };
              });

              const clientesOrdenados = clientesNormalizados.sort((a, b) =>
                a.nome.localeCompare(b.nome, 'pt-BR', { sensitivity: 'base' })
              );

              this.clientes.set(clientesOrdenados);
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
/*  mockDados(){
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
  } */
}
