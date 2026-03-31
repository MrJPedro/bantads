import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { TextareaModule } from 'primeng/textarea';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { GerenteDashboard } from '../../../DTO/administrador/gerente-dashboard.dto';
import { AdministradorService } from '../../../services/administrador-service';
import { DashboardResponse } from '../../../DTO/gerente';
import { ItemDashBoardResponse } from '../../../DTO/conta/item-dash-board-response';

@Component({
  selector: 'app-tela-inicial-administrador',
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
  templateUrl: './tela-inicial-administrador.html',
})

export class TelaInicialAdministrador implements OnInit {

  gerentes = signal<DashboardResponse>([]);

  gerenteSelecionado?: ItemDashBoardResponse;

  constructor(
    private messageService: MessageService,
    private administradorService: AdministradorService
  ) {}

  ngOnInit(){
    // this.mockDados();
    this.carregarGerentes();
  }

  carregarGerentes(){

      this.administradorService.consultarTodosGerentes().subscribe({
          next: (gerente: DashboardResponse) => {

              // se encontrou
              this.gerentes.set(gerente);
          },

          error: (err) => {
              console.error(err);

              this.gerentes.set([]);

              this.messageService.add({
                  severity: 'error',
                  summary: 'Erro',
                  detail: 'Gerentes não encontrados ou erro no servidor.'
              });
          }
      });
    }
/*  mockDados(){
    this.gerentes.set([
      {
        nome: 'Ana',
        qtdClientes: 10,
        somaPositiva: 1530.75,
        somaNegativa: 700.20
      },
      {
        nome: 'Beto',
        qtdClientes: 2,
        somaPositiva: 25.99,
        somaNegativa: 1000
      },
      {
        nome: 'Carlos',
        qtdClientes: 23,
        somaPositiva: 8364.29,
        somaNegativa: 152.37
      },
      {
        nome: 'Diana',
        qtdClientes: 12,
        somaPositiva: 273.13,
        somaNegativa: 9128.98
      },
    ].sort((a, b) => b.somaPositiva - a.somaPositiva));
  } */
}
