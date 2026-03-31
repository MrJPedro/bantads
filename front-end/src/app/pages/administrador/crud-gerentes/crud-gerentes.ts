import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
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

interface GerenteMock {
  cpf: string;
  nome: string;
  email: string;
  tipo: string;
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
  ],
  providers: [MessageService],
  templateUrl: './crud-gerentes.html',
  styleUrl: './crud-gerentes.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CrudGerentes {
  private readonly formBuilder = inject(FormBuilder);
  private readonly messageService = inject(MessageService);

  readonly gerentes = signal<GerenteMock[]>([
    {
      cpf: '123.456.789-10',
      nome: 'Aline Moraes',
      email: 'aline.moraes@bantads.com',
      tipo: 'GERENTE',
    },
    {
      cpf: '987.654.321-00',
      nome: 'Bruno Ferreira',
      email: 'bruno.ferreira@bantads.com',
      tipo: 'GERENTE',
    },
    {
      cpf: '222.333.444-55',
      nome: 'Carla Nogueira',
      email: 'carla.nogueira@bantads.com',
      tipo: 'GERENTE',
    },
  ]);

  readonly termoBusca = signal('');
  readonly dialogFormularioAberto = signal(false);
  readonly dialogRemocaoAberto = signal(false);
  readonly modoEdicao = signal(false);
  readonly cpfEmEdicao = signal<string | null>(null);

  readonly formularioGerente = this.formBuilder.nonNullable.group({
    cpf: ['', [Validators.required, Validators.minLength(11)]],
    nome: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    tipo: ['GERENTE', [Validators.required]],
    senha: ['', [Validators.required, Validators.minLength(6)]],
  });

  readonly gerentesFiltrados = computed(() => {
    const termo = this.termoBusca().trim().toLowerCase();

    if (!termo) {
      return this.gerentes();
    }

    return this.gerentes().filter((gerente) =>
      gerente.nome.toLowerCase().includes(termo)
      || gerente.cpf.toLowerCase().includes(termo)
      || gerente.email.toLowerCase().includes(termo),
    );
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
      tipo: 'GERENTE',
      senha: '',
    });
    this.cpfControl.enable();
    this.senhaControl.setValidators([Validators.required, Validators.minLength(6)]);
    this.senhaControl.updateValueAndValidity();
    this.dialogFormularioAberto.set(true);
  }

  abrirEdicaoGerente(gerente: GerenteMock): void {
    this.modoEdicao.set(true);
    this.cpfEmEdicao.set(gerente.cpf);
    this.formularioGerente.reset({
      cpf: gerente.cpf,
      nome: gerente.nome,
      email: gerente.email,
      tipo: gerente.tipo,
      senha: '',
    });
    this.cpfControl.disable();
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

    if (!this.modoEdicao()) {
      const cpfJaCadastrado = this.gerentes().some((gerente) => gerente.cpf === gerenteFormulario.cpf);
      if (cpfJaCadastrado) {
        this.messageService.add({
          severity: 'warn',
          summary: 'CPF já cadastrado',
          detail: 'Já existe um gerente com esse CPF na lista mockada.',
        });
        return;
      }

      const novaLista = [...this.gerentes(), {
        cpf: gerenteFormulario.cpf,
        nome: gerenteFormulario.nome,
        email: gerenteFormulario.email,
        tipo: gerenteFormulario.tipo,
      }].sort((a, b) => a.nome.localeCompare(b.nome));

      this.gerentes.set(novaLista);
      this.messageService.add({
        severity: 'success',
        summary: 'Gerente criado',
        detail: 'Registro adicionado com sucesso na lista mockada.',
      });
      this.fecharDialogFormulario();
      return;
    }

    const cpfOriginal = this.cpfEmEdicao();
    if (!cpfOriginal) {
      return;
    }

    const listaAtualizada = this.gerentes().map((gerente) => {
      if (gerente.cpf !== cpfOriginal) {
        return gerente;
      }

      return {
        ...gerente,
        nome: gerenteFormulario.nome,
        email: gerenteFormulario.email,
        tipo: gerenteFormulario.tipo,
      };
    });

    this.gerentes.set(listaAtualizada);
    this.messageService.add({
      severity: 'success',
      summary: 'Gerente atualizado',
      detail: 'Registro alterado com sucesso na lista mockada.',
    });
    this.fecharDialogFormulario();
  }

  abrirConfirmacaoRemocao(gerente: GerenteMock): void {
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

    const listaAtualizada = this.gerentes().filter((gerente) => gerente.cpf !== cpf);
    this.gerentes.set(listaAtualizada);
    this.cancelarRemocao();
    this.messageService.add({
      severity: 'success',
      summary: 'Gerente removido',
      detail: 'Registro removido com sucesso da lista mockada.',
    });
  }

  aoPesquisar(valor: string): void {
    this.termoBusca.set(valor);
  }
}
