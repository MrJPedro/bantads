import { AuthService } from '../../../services/auth-service';
import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';
import { InputTextModule } from 'primeng/inputtext';

@Component({
  selector: 'app-login',
  imports: [CommonModule, FormsModule, ButtonModule, CheckboxModule, InputTextModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {

  credenciais = {email:'', senha:''}

  readonly LS_KEY = "Usuario_logado"

  constructor(
    private authService: AuthService
  ) {}


  login(/* Referência ao formulário */) {
    // if(formulário é válido){
        const {email, senha} = this.credenciais;
        const loginEfetuado = this.authService.login(email, senha);

        if (!loginEfetuado) {
          // Usuário/Senha incorretos
          return;
        }

        // Login efetuado com sucesso
        // Utilizar LocalStorage para armazenar usuário logado
    //}
  }

  logout() {
    /* Ainda não estamos utilizando tokens
    this.authService.logout().subscribe({
      next: response => {
        //
      },
      err: response => {
        //
        console.log(response)
      }
    })*/
  }
    checked1 = signal<boolean>(true);
}
