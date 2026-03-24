import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { InputMaskModule } from 'primeng/inputmask';
import { InputNumberModule } from 'primeng/inputnumber';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'app-perfil',
  imports: [
    CommonModule,
    FormsModule,
    CardModule,
    InputTextModule,
    InputMaskModule,
    InputNumberModule,
    ButtonModule
  ],
  templateUrl: './perfil.html',
  styleUrl: './perfil.css',
})
export class Perfil {

  perfil = {
    nome: 'Ricardo Silva',
    email: 'ricardo.silva@gmail.com',
    telefone: '(11) 98765-4321',
    endereco: 'Rua dos amigos, 47',
    estado: 'PR',
    cpf: '123.456.789-00',
    salario: 5000.00
  }

  salvarPerfil(): void {
  
  }

  cancelar(): void {
   
  }
}
