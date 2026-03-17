import { Component } from '@angular/core';
import { AuthService } from '../../../services/auth-service';

@Component({
  selector: 'app-login',
  imports: [],
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
}
