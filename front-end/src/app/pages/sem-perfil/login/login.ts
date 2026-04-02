import { AuthService } from '../../../services/auth-service';
import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';
import { InputTextModule } from 'primeng/inputtext';
import { Router } from '@angular/router';

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
    private authService: AuthService,
    private router: Router
  ) {}


  login(form: NgForm) {
    if(form.valid){
        const {email, senha} = this.credenciais;
        const loginEfetuado = this.authService.login(email, senha);

        console.log(email)
        console.log(senha)
        console.log(loginEfetuado)

        if (!loginEfetuado) {
          console.log("Usuário/Senha incorretos")
          return;
        }

        console.log("Login efetuado com sucesso")
        
        switch(loginEfetuado.tipo) {
          case 'CLIENTE':
            this.router.navigate(['/cliente/tela-inicial'])
            break
          case 'GERENTE':
            this.router.navigate(['/gerente/tela-inicial'])
            break
          case 'ADMINISTRADOR':
            this.router.navigate(['/administrador/tela-inicial'])
            break
          default:
            break
        }
        
    }
  }

    checked1 = signal<boolean>(true);
}
