import { CommonModule } from '@angular/common';
import { Component, Input, OnDestroy, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { NgxMaskDirective, provideNgxMask } from 'ngx-mask';
import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';
import { Dialog } from "primeng/dialog";
import { InputNumberModule } from 'primeng/inputnumber';
import { InputTextModule } from 'primeng/inputtext';
import { Subject } from 'rxjs';
import { debounceTime, takeUntil } from 'rxjs/operators';
import { AutocadastroInfo } from '../../../DTO/cliente/autocadastro-info.dto';
import { CepService } from '../../../services/cep-service';

@Component({
  selector: 'app-autocadastro',
  imports: [CommonModule, FormsModule, ButtonModule, CheckboxModule, InputTextModule, InputNumberModule, Dialog, NgxMaskDirective],
  templateUrl: './autocadastro.html',
  providers: [provideNgxMask()],
  styleUrl: './autocadastro.css',
})
export class Autocadastro implements OnInit, OnDestroy {

  private cepService = inject(CepService);
  private cepSubject = new Subject<string>();
  private destroy$ = new Subject<void>();

  private router = inject(Router);

  public dados: AutocadastroInfo = {
    cpf: '',
    email: '',
    nome: '',
    telefone: '',
    salario: 0,
    logradouro: '',
    numero: '',
    complemento: '',
    CEP: '',
    cidade: '',
    estado: ''
  };

showDialog() {
this.visible = true;
}
showConfirmationModal() {
this.showConfirmation = true;
}
@Input() mask = '';
email = '';
visible: any;
showConfirmation = false;

ngOnInit() {
  this.cepSubject.pipe(
    debounceTime(800),
    takeUntil(this.destroy$)
  ).subscribe(() => {
    this.buscarCEP();
  });
}

ngOnDestroy() {
  this.destroy$.next();
  this.destroy$.complete();
}

onCepInput() {
  this.cepSubject.next(this.dados.CEP);
}

  buscarCEP() {
    const cepLimpo = this.dados.CEP.replace(/\D/g, '');

    if (cepLimpo.length === 8) {
      this.cepService.buscar(cepLimpo).subscribe({
        next: (res) => {
          if (!res.erro) {
            this.dados.logradouro = res.logradouro;
            this.dados.cidade = res.localidade;
            this.dados.estado = res.uf;
          } else {
            alert('CEP não encontrado!');
          }
        },
        error: () => alert('Erro ao buscar o CEP.')
      });
    }
  }

  cadastrar() {
    this.showConfirmationModal();
  }

  voltar() {
    this.router.navigate(['/']);
  }
}
