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

  constructor(
    private authService: AuthService
  ) {}









  login(/* Referência ao formulário */) {
    // Obtém o formulário como argumento
    // Valida e formata os valores inseridos no formulário
  }

  logout() {
    // Depois comento o fluxo...
    this.authService.logout();
  }
    checked1 = signal<boolean>(true);
}
