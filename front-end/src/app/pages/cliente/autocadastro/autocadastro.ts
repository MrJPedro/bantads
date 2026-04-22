import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { NgxMaskDirective, provideNgxMask } from 'ngx-mask';
import { ButtonModule } from 'primeng/button';
import { Dialog } from 'primeng/dialog';
import { InputNumberModule } from 'primeng/inputnumber';
import { InputTextModule } from 'primeng/inputtext';
import { Subject } from 'rxjs';
import { debounceTime, takeUntil } from 'rxjs/operators';
import { CepService } from '../../../services/cep-service';

@Component({
  selector: 'app-autocadastro',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    ButtonModule,
    InputTextModule,
    InputNumberModule,
    Dialog,
    NgxMaskDirective,
  ],
  templateUrl: './autocadastro.html',
  providers: [provideNgxMask()],
  styleUrl: './autocadastro.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Autocadastro implements OnInit, OnDestroy {
  private readonly formBuilder = inject(FormBuilder);
  private readonly cepService = inject(CepService);
  private readonly router = inject(Router);

  private readonly cepSubject = new Subject<string>();
  private readonly destroy$ = new Subject<void>();

  visible = false;
  showConfirmation = false;

  readonly formularioCadastro = this.formBuilder.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    nome: ['', [Validators.required, Validators.minLength(3)]],
    cpf: ['', [Validators.required, Validators.minLength(11)]],
    telefone: ['', [Validators.required, Validators.minLength(10)]],
    salario: [0, [Validators.required, Validators.min(0.01)]],
  });

  readonly formularioEndereco = this.formBuilder.nonNullable.group({
    cep: ['', [Validators.required, Validators.minLength(8)]],
    estado: ['', [Validators.required]],
    cidade: ['', [Validators.required]],
    logradouro: ['', [Validators.required]],
    numero: ['', [Validators.required]],
    complemento: [''],
  });

  get emailControl() { return this.formularioCadastro.controls.email; }
  get nomeControl() { return this.formularioCadastro.controls.nome; }
  get cpfControl() { return this.formularioCadastro.controls.cpf; }
  get telefoneControl() { return this.formularioCadastro.controls.telefone; }
  get salarioControl() { return this.formularioCadastro.controls.salario; }

  get cepControl() { return this.formularioEndereco.controls.cep; }
  get estadoControl() { return this.formularioEndereco.controls.estado; }
  get cidadeControl() { return this.formularioEndereco.controls.cidade; }
  get logradouroControl() { return this.formularioEndereco.controls.logradouro; }
  get numeroControl() { return this.formularioEndereco.controls.numero; }

  ngOnInit(): void {
    this.cepSubject.pipe(
      debounceTime(800),
      takeUntil(this.destroy$),
    ).subscribe(() => this.buscarCEP());
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  showDialogEndereco(): void {
    this.visible = true;
  }

  onCepInput(valor: string): void {
    this.cepSubject.next(valor);
  }

  buscarCEP(): void {
    const cepLimpo = this.cepControl.value.replace(/\D/g, '');
    if (cepLimpo.length === 8) {
      this.cepService.buscar(cepLimpo).subscribe({
        next: (res) => {
          if (!res.erro) {
            this.formularioEndereco.patchValue({
              logradouro: res.logradouro,
              cidade: res.localidade,
              estado: res.uf,
            });
          } else {
            alert('CEP não encontrado!');
          }
        },
        error: () => alert('Erro ao buscar o CEP.'),
      });
    }
  }

  salvarEndereco(): void {
    if (this.formularioEndereco.invalid) {
      this.formularioEndereco.markAllAsTouched();
      return;
    }
    this.visible = false;
  }

  cadastrar(): void {
    if (this.formularioCadastro.invalid) {
      this.formularioCadastro.markAllAsTouched();
      return;
    }

    if (this.formularioEndereco.invalid) {
      this.formularioEndereco.markAllAsTouched();
      alert('Por favor, preencha o endereço antes de continuar.');
      return;
    }

    this.showConfirmation = true;
  }

  voltar(): void {
    this.router.navigate(['/']);
  }
}
