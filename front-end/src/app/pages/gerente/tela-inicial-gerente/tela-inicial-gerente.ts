import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { TextareaModule } from 'primeng/textarea';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { finalize } from 'rxjs';
import { ClienteParaAprovarResponse } from '../../../DTO/cliente/cliente-para-aprovar-response.dto';
import { Gerente } from '../../../services/gerente-service';
import { ParaAprovarResponse } from '../../../DTO/cliente';
import { CpfPipe } from '../../../shared/pipes/cpf.pipe';

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
    ToastModule,
    CpfPipe,
  ],
  providers: [MessageService],
  templateUrl: './tela-inicial-gerente.html',
})
export class TelaInicialGerente implements OnInit {
  clientes = signal<ClienteParaAprovarResponse[]>([]);

  dialogRecusa: boolean = false;
  dialogAceita: boolean = false;
  processandoRecusa: boolean = false;
  processandoAceite: boolean = false;

  clienteSelecionado?: ClienteParaAprovarResponse;

  motivoRecusa: string = '';

  constructor(
    private messageService: MessageService,
    private gerenteService: Gerente,
  ) {}

  ngOnInit() {
    this.carregarClientes();
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
    if (!this.motivoRecusa.trim() || !this.clienteSelecionado) return;

    this.processandoRecusa = true;
    const cpf = this.clienteSelecionado.cpf;
    const motivo = this.motivoRecusa.trim();

    this.gerenteService.rejeitarCliente(cpf, motivo).pipe(
      finalize(() => {
        this.processandoRecusa = false;
      })
    ).subscribe({
      next: () => {
        this.fecharDialogRecusa();
        this.carregarClientes();
        this.messageService.add({
          severity: 'info',
          summary: 'Rejeitado',
          detail: 'Cadastro rejeitado com sucesso.',
        });
      },
      error: (err) => {
        console.error(err);
        this.messageService.add({
          severity: 'error',
          summary: 'Erro',
          detail: 'Não foi possível rejeitar o cadastro.',
        });
      }
    });
  }

  confirmarAceite() {
    if (!this.clienteSelecionado) return;

    this.processandoAceite = true;
    const cpf = this.clienteSelecionado.cpf;

    this.gerenteService.aprovarCliente(cpf).pipe(
      finalize(() => {
        this.processandoAceite = false;
      })
    ).subscribe({
      next: () => {
        this.fecharDialogAceite();
        this.carregarClientes();
        this.messageService.add({
          severity: 'success',
          summary: 'Aceito',
          detail: 'Cadastro aprovado com sucesso.',
        });
      },
      error: (err) => {
        console.error(err);
        this.messageService.add({
          severity: 'error',
          summary: 'Erro',
          detail: 'Não foi possível aprovar o cadastro.',
        });
      }
    });
  }

  carregarClientes() {
    this.gerenteService.clientesParaAprovar().subscribe({
      next: (clientes: ParaAprovarResponse) => {
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
}
