import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { ToastModule } from 'primeng/toast';
import { finalize } from 'rxjs';
import * as DTO from '../../../DTO/gerente';
import { AdministradorService } from '../../../services/administrador-service';
import { CpfPipe } from '../../../shared/pipes/cpf.pipe';
import { TelefonePipe } from '../../../shared/pipes/telefone.pipe';

interface GerenteView {
  cpf: string;
  nome: string;
  email: string;
  telefone: string;
}

@Component({
  selector: 'app-crud-gerentes',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    TableModule,
    ButtonModule,
    DialogModule,
    InputTextModule,
    IconFieldModule,
    InputIconModule,
    PasswordModule,
    ToastModule,
    TagModule,
    CpfPipe,
    TelefonePipe
  ],
  providers: [MessageService],
  templateUrl: './crud-gerentes.html',
  styleUrl: './crud-gerentes.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CrudGerentes implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly messageService = inject(MessageService);
  private readonly administradorService = inject(AdministradorService);

  readonly gerentes = signal<GerenteView[]>([]);

  readonly termoBusca = signal('');
  readonly dialogFormularioAberto = signal(false);
  readonly dialogRemocaoAberto = signal(false);
  readonly modoEdicao = signal(false);
  readonly cpfEmEdicao = signal<string | null>(null);
  readonly carregandoGerentes = signal(false);
  readonly salvandoGerente = signal(false);
  readonly removendoGerente = signal(false);

  readonly formularioGerente = this.formBuilder.nonNullable.group({
    cpf: ['', [Validators.required, Validators.minLength(11)]],
    nome: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    telefone: ['', [Validators.required, Validators.minLength(10)]],
    senha: ['', [Validators.required, Validators.minLength(6)]],
  });

  ngOnInit(): void {
    this.carregarGerentes();
  }

  readonly gerentesFiltrados = computed(() => {
    const termo = this.termoBusca().trim().toLowerCase();
    const ordenarPorNome = (a: GerenteView, b: GerenteView) => a.nome.localeCompare(b.nome);

    if (!termo) {
      return [...this.gerentes()].sort(ordenarPorNome);
    }

    return this.gerentes()
      .filter((gerente) =>
        gerente.nome.toLowerCase().includes(termo)
        || gerente.cpf.toLowerCase().includes(termo)
        || gerente.email.toLowerCase().includes(termo),
      )
      .sort(ordenarPorNome);
  });

  get cpfControl() {
    return this.formularioGerente.controls.cpf;
  }

  get nomeControl() {
    return this.formularioGerente.controls.nome;
  }

  get emailControl() {
    return this.formularioGerente.controls.email;
  }

  get senhaControl() {
    return this.formularioGerente.controls.senha;
  }

  abrirNovoGerente(): void {
    this.modoEdicao.set(false);
    this.cpfEmEdicao.set(null);
    this.formularioGerente.reset({
      cpf: '',
      nome: '',
      email: '',
      telefone: '',
      senha: '',
    });
    this.cpfControl.enable();
    this.formularioGerente.controls.telefone.enable();
    this.senhaControl.setValidators([Validators.required, Validators.minLength(6)]);
    this.senhaControl.updateValueAndValidity();
    this.dialogFormularioAberto.set(true);
  }

  abrirEdicaoGerente(gerente: GerenteView): void {
    this.modoEdicao.set(true);
    this.cpfEmEdicao.set(gerente.cpf);
    this.formularioGerente.reset({
      cpf: gerente.cpf,
      nome: gerente.nome,
      email: gerente.email,
      telefone: gerente.telefone,
      senha: '',
    });
    this.cpfControl.disable();
    this.formularioGerente.controls.telefone.disable();
    this.senhaControl.setValidators([Validators.minLength(6)]);
    this.senhaControl.updateValueAndValidity();
    this.dialogFormularioAberto.set(true);
  }

  fecharDialogFormulario(): void {
    this.dialogFormularioAberto.set(false);
    this.formularioGerente.markAsPristine();
  }

  salvarGerente(): void {
    if (this.formularioGerente.invalid) {
      this.formularioGerente.markAllAsTouched();
      return;
    }

    const gerenteFormulario = this.formularioGerente.getRawValue();
    this.salvandoGerente.set(true);

    if (!this.modoEdicao()) {
      const payload: DTO.DadoGerenteInsercao = {
        cpf: gerenteFormulario.cpf,
        nome: gerenteFormulario.nome,
        email: gerenteFormulario.email,
        telefone: gerenteFormulario.telefone,
        senha: gerenteFormulario.senha,
        tipo: 'GERENTE',
      };

      this.administradorService.inserirGerente(payload).pipe(
        finalize(() => {
          this.salvandoGerente.set(false);
        })
      ).subscribe({
        next: () => {
          this.carregarGerentes();
          this.fecharDialogFormulario();
          this.messageService.add({
            severity: 'success',
            summary: 'Gerente criado',
            detail: 'Registro adicionado com sucesso.',
          });
        },
        error: (err) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Erro ao criar gerente',
            detail: this.extrairMensagemErro(err, 'Não foi possível criar o gerente.'),
          });
        },
      });
      return;
    }

    const cpfOriginal = this.cpfEmEdicao();
    if (!cpfOriginal) {
      this.salvandoGerente.set(false);
      return;
    }
    const senha = gerenteFormulario.senha.trim();
    const payload: DTO.DadoGerenteAtualizacao = {
      nome: gerenteFormulario.nome,
      email: gerenteFormulario.email,
      ...(senha ? { senha } : {}),
    };

    this.administradorService.alterarGerente(cpfOriginal, payload).pipe(
      finalize(() => {
        this.salvandoGerente.set(false);
      })
    ).subscribe({
      next: () => {
        this.carregarGerentes();
        this.fecharDialogFormulario();
        this.messageService.add({
          severity: 'success',
          summary: 'Gerente atualizado',
          detail: 'Registro alterado com sucesso.',
        });
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Erro ao atualizar gerente',
          detail: this.extrairMensagemErro(err, 'Não foi possível atualizar o gerente.'),
        });
      },
    });
  }

  abrirConfirmacaoRemocao(gerente: GerenteView): void {
    this.cpfEmEdicao.set(gerente.cpf);
    this.dialogRemocaoAberto.set(true);
  }

  cancelarRemocao(): void {
    this.dialogRemocaoAberto.set(false);
    this.cpfEmEdicao.set(null);
  }

  confirmarRemocao(): void {
    const cpf = this.cpfEmEdicao();
    if (!cpf) {
      return;
    }
    this.removendoGerente.set(true);

    this.administradorService.removerGerente(cpf).pipe(
      finalize(() => {
        this.removendoGerente.set(false);
      })
    ).subscribe({
      next: () => {
        this.cancelarRemocao();
        this.carregarGerentes();
        this.messageService.add({
          severity: 'success',
          summary: 'Gerente removido',
          detail: 'Registro removido com sucesso.',
        });
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Erro ao remover gerente',
          detail: this.extrairMensagemErro(err, 'Não foi possível remover o gerente.'),
        });
      },
    });
  }

  aoPesquisar(valor: string): void {
    this.termoBusca.set(valor);
  }

  private carregarGerentes(): void {
    this.carregandoGerentes.set(true);

    this.administradorService.consultarGerentesCrud().pipe(
      finalize(() => {
        this.carregandoGerentes.set(false);
      })
    ).subscribe({
      next: (gerentes: DTO.GerentesResponse) => {
        const gerentesOrdenados = gerentes
          .map((gerente) => this.paraGerenteView(gerente))
          .sort((a, b) => a.nome.localeCompare(b.nome));
        this.gerentes.set(gerentesOrdenados);
      },
      error: (err) => {
        this.gerentes.set([]);
        this.messageService.add({
          severity: 'error',
          summary: 'Erro ao carregar gerentes',
          detail: this.extrairMensagemErro(err, 'Não foi possível carregar os gerentes.'),
        });
      },
    });
  }

  private paraGerenteView(gerente: DTO.DadoGerente): GerenteView {
    return {
      cpf: gerente.cpf,
      nome: gerente.nome,
      email: gerente.email,
      telefone: gerente.telefone ?? '',
    };
  }

  private extrairMensagemErro(err: any, fallback: string): string {
    return err?.error?.message || err?.error?.detail || fallback;
  }
}
