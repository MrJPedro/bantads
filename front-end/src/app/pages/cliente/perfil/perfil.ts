import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { InputMaskModule } from 'primeng/inputmask';
import { InputNumberModule } from 'primeng/inputnumber';
import { InputTextModule } from 'primeng/inputtext';
import { ToastModule } from 'primeng/toast';
import * as DTO from '../../../DTO/cliente';
import { AuthService } from '../../../services/auth-service';
import { Cliente } from '../../../services/cliente-service';
import { Gerente } from '../../../services/gerente-service';

interface PerfilForm {
  nome: string
  email: string
  telefone: string
  endereco: string
  estado: string
  cpf: string
  salario: number
  cidade: string
  CEP: string
  gerenteNome: string
  saldo: number
  limite: number
}

@Component({
  selector: 'app-perfil',
  imports: [
    CommonModule,
    FormsModule,
    CardModule,
    InputTextModule,
    InputMaskModule,
    InputNumberModule,
    ButtonModule,
    ToastModule,
    ConfirmDialogModule
  ],
  providers: [MessageService, ConfirmationService],
  templateUrl: './perfil.html',
  styleUrl: './perfil.css',
})
export class Perfil implements OnInit {

  perfil: PerfilForm = {
    nome: '',
    email: '',
    telefone: '',
    endereco: '',
    estado: '',
    cpf: '',
    salario: 0,
    cidade: '',
    CEP: '',
    gerenteNome: '',
    saldo: 0,
    limite: 0
  }

  private perfilOriginal: PerfilForm | null = null

  constructor(
    private clienteService: Cliente,
    private gerenteService: Gerente,
    private authService: AuthService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService
  ) {}

  ngOnInit(): void {
    this.carregarPerfilDoLogin();
    this.carregarPerfil();
  }

  private carregarPerfil(): void {
    const cpf = this.authService.getCpf();

    if (!cpf) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Sessão inválida',
        detail: 'Não foi possível identificar o CPF do usuário logado.'
      });
      return;
    }

    this.clienteService.consultarPerfil(cpf).subscribe({
      next: (dados: DTO.DadosClienteResponse) => {
        const perfilCarregado = this.mapearParaFormulario(dados);
        this.perfil = { ...perfilCarregado };
        this.perfilOriginal = { ...perfilCarregado };

        if (!this.perfil.gerenteNome) {
          const usuarioLogado = this.authService.getUsuarioLogado();
          const gerenteCpf = usuarioLogado?.cliente?.gerente_cpf;
          this.carregarNomeGerentePorCpf(gerenteCpf);
        }
      },
      error: () => {
        this.messageService.add({
          severity: 'warn',
          summary: 'Atenção',
          detail: 'Não foi possível atualizar os dados do servidor. Exibindo dados locais.'
        });
      }
    });
  }

  private carregarPerfilDoLogin(): void {
    const usuarioLogado = this.authService.getUsuarioLogado();
    const cliente = usuarioLogado?.cliente;
    const usuarioLegado = usuarioLogado?.usuario;

    if (!cliente && !usuarioLegado) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Sem dados locais',
        detail: 'Nenhuma informação de perfil foi encontrada no login local.'
      });
      return;
    }

    const perfilCarregado: PerfilForm = {
      nome: cliente?.nome ?? usuarioLegado?.nome ?? '',
      email: cliente?.email ?? usuarioLegado?.email ?? '',
      telefone: cliente?.telefone ?? '',
      endereco: cliente?.endereco ?? '',
      estado: cliente?.estado ?? '',
      cpf: cliente?.cpf ?? usuarioLogado?.cpf ?? usuarioLegado?.cpf ?? '',
      salario: Number(cliente?.salario ?? 0),
      cidade: cliente?.cidade ?? '',
      CEP: cliente?.CEP ?? '',
      gerenteNome: usuarioLogado?.gerente_nome ?? '',
      saldo: Number(usuarioLogado?.conta?.saldo ?? 0),
      limite: Number(usuarioLogado?.conta?.limite ?? 0)
    };

    this.perfil = { ...perfilCarregado };
    this.perfilOriginal = { ...perfilCarregado };

    if (!this.perfil.gerenteNome) {
      this.carregarNomeGerentePorCpf(cliente?.gerente_cpf);
    }
  }

  private mapearParaFormulario(dados: DTO.DadosClienteResponse): PerfilForm {
    const usuarioLogado = this.authService.getUsuarioLogado();
    const cliente = usuarioLogado?.cliente;
    const usuarioLegado = usuarioLogado?.usuario;

    return {
      nome: dados.nome ?? '',
      email: dados.email ?? '',
      telefone: dados.telefone ?? '',
      endereco: dados.endereco ?? '',
      estado: dados.estado ?? '',
      cpf: dados.cpf ?? usuarioLogado?.cpf ?? usuarioLegado?.cpf ?? '',
      salario: Number(dados.salario ?? 0),
      cidade: dados.cidade ?? cliente?.cidade ?? '',
      CEP: cliente?.CEP ?? '',
      gerenteNome: dados.gerente_nome ?? usuarioLogado?.gerente_nome ?? '',
      saldo: Number(dados.saldo ?? usuarioLogado?.conta?.saldo ?? 0),
      limite: Number(dados.limite ?? usuarioLogado?.conta?.limite ?? 0)
    };
  }

  salvarPerfil(): void {
    this.confirmationService.confirm({
      header: 'Confirmar alteração',
      message: 'Deseja salvar as alterações do perfil?',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Salvar',
      rejectLabel: 'Cancelar',
      accept: () => {
        this.salvarPerfilConfirmado();
      }
    });
  }

  private salvarPerfilConfirmado(): void {
    const cpf = this.perfil.cpf || this.authService.getCpf();

    if (!cpf) {
      this.messageService.add({
        severity: 'error',
        summary: 'Erro',
        detail: 'CPF do usuário não encontrado para salvar o perfil.'
      });
      return;
    }

    const payload: DTO.PerfilInfo = {
      nome: this.perfil.nome,
      email: this.perfil.email,
      salario: this.perfil.salario,
      endereco: this.perfil.endereco,
      CEP: this.perfil.CEP,
      cidade: this.perfil.cidade,
      estado: this.perfil.estado
    };

    this.clienteService.updatePerfil(cpf, payload).subscribe({
      next: () => {
        this.perfilOriginal = { ...this.perfil };
        this.atualizarUsuarioLogado();
        this.messageService.add({
          severity: 'success',
          summary: 'Sucesso',
          detail: 'Perfil atualizado com sucesso.'
        });
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          summary: 'Erro ao salvar',
          detail: 'Não foi possível salvar as alterações do perfil.'
        });
      }
    });
  }

  cancelar(): void {
    if (!this.perfilOriginal) {
      this.messageService.add({
        severity: 'info',
        summary: 'Nada para restaurar',
        detail: 'Não há dados anteriores para restaurar.'
      });
      return;
    }

    this.perfil = { ...this.perfilOriginal };
    this.messageService.add({
      severity: 'info',
      summary: 'Alterações descartadas',
      detail: 'Os dados foram restaurados para o último estado salvo.'
    });
  }

  private atualizarUsuarioLogado(): void {
    const usuarioLogado = this.authService.getUsuarioLogado();

    if (!usuarioLogado || !usuarioLogado.cliente) {
      return;
    }

    const atualizado = {
      ...usuarioLogado,
      cliente: {
        ...usuarioLogado.cliente,
        nome: this.perfil.nome,
        email: this.perfil.email,
        telefone: this.perfil.telefone,
        salario: this.perfil.salario,
        endereco: this.perfil.endereco,
        CEP: this.perfil.CEP,
        cidade: this.perfil.cidade,
        estado: this.perfil.estado
      },
      nome: this.perfil.nome,
      email: this.perfil.email
    };

    localStorage.setItem('Usuario_logado', JSON.stringify(atualizado));
  }

  private carregarNomeGerentePorCpf(gerenteCpf?: string): void {
    if (!gerenteCpf) {
      return;
    }

    this.gerenteService.consultarGerentePorCpf(gerenteCpf).subscribe({
      next: (gerentes) => {
        const gerente = gerentes?.[0];
        if (!gerente?.nome) {
          return;
        }

        this.perfil = {
          ...this.perfil,
          gerenteNome: gerente.nome
        };

        if (this.perfilOriginal) {
          this.perfilOriginal = {
            ...this.perfilOriginal,
            gerenteNome: gerente.nome
          };
        }
      }
    });
  }
}
