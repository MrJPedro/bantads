import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { TableModule } from 'primeng/table';
import { TextareaModule } from 'primeng/textarea';
import { ToastModule } from 'primeng/toast';
import { GerenteDashboard } from '../../../DTO/administrador/gerente-dashboard.dto';
import { ItemDashBoardResponse } from '../../../DTO/conta/item-dash-board-response';
import { DashboardResponse } from '../../../DTO/gerente';
import { AdministradorService } from '../../../services/administrador-service';

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

  gerentes = signal<GerenteDashboard[]>([]);

  gerenteSelecionado?: GerenteDashboard;

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
          next: (gerentes: DashboardResponse) => {

              const gerentesOrdenados = gerentes
                .map((item: ItemDashBoardResponse) => this.paraGerenteDashboard(item))
                .sort((a: GerenteDashboard, b: GerenteDashboard) => b.somaPositiva - a.somaPositiva);

              // se encontrou
              this.gerentes.set(gerentesOrdenados);
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

  private paraGerenteDashboard(item: ItemDashBoardResponse): GerenteDashboard {
    const somaPositiva = item.clientes
      .filter((conta) => conta.saldo >= 0)
      .reduce((acumulador, conta) => acumulador + conta.saldo, 0);

    const somaNegativa = item.clientes
      .filter((conta) => conta.saldo < 0)
      .reduce((acumulador, conta) => acumulador + conta.saldo, 0);

    return {
      nome: item.gerente.nome,
      qtdClientes: item.clientes.length,
      somaPositiva,
      somaNegativa,
    };
  }

}
