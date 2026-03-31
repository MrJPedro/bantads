import { Component, Input, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';
import { InputTextModule } from 'primeng/inputtext';
import { Dialog } from "primeng/dialog";
import { AutocadastroInfo } from '../../../DTO/cliente/autocadastro-info.dto';
import { AutocadastroService } from '../../../services/autocadastro-service'
import { CepService } from '../../../services/cep-service';
import { NgxMaskDirective, provideNgxMask } from 'ngx-mask';

@Component({
  selector: 'app-autocadastro',
  imports: [CommonModule, FormsModule, ButtonModule, CheckboxModule, InputTextModule, Dialog, NgxMaskDirective],
  templateUrl: './autocadastro.html',
  providers: [provideNgxMask()],
  styleUrl: './autocadastro.css',
})
export class Autocadastro {

  private cepService = inject(CepService);
  private autocadastroService = inject(AutocadastroService);

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
@Input() mask = '';
email = '';
visible: any;

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
    this.autocadastroService.cadastrarCliente(this.dados).subscribe({
      next: () => {
        alert('Solicitação enviada com sucesso! Aguarde a aprovação do gerente.');
      },
      error: () => {
        alert('Erro ao realizar o cadastro. Tente novamente.');
      }
    });
  }
}
