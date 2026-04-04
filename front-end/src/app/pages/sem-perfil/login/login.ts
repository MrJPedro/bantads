import { CommonModule } from '@angular/common';
import { Component, signal } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';
import { InputTextModule } from 'primeng/inputtext';
import { AuthService } from '../../../services/auth-service';

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
    if (form.valid) {
      const { email, senha } = this.credenciais;
      this.authService.login({login: email, senha }).subscribe({
        next: (response) => {
          console.log(response);

          if (response) {
            this.authService.storeCredentials(response);
          }

          switch (response.tipo) {
            case 'CLIENTE':
              console.log("Cliente logado: ");
              this.router.navigate(['/cliente/tela-inicial']);
              break;
            case 'GERENTE':
              this.router.navigate(['/gerente/tela-inicial']);
              break;
            case 'ADMINISTRADOR':
              this.router.navigate(['/administrador/tela-inicial']);
              break;
            default:
              break;
          }
        }
      });
    }
  }

  checked1 = signal<boolean>(true);
}
