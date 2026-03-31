import { Router } from '@angular/router';
import { SaldoResponse } from '../../../DTO/conta/saldo-response';
import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { TextareaModule } from 'primeng/textarea';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { Cliente } from '../../../services/cliente-service';
import { AuthService } from '../../../services/auth-service';

@Component({
  selector: 'app-sacar-cliente',
  standalone: true,
  imports: [
        CommonModule,
        FormsModule,
        CardModule,
        ButtonModule,
        DialogModule,
        TextareaModule,
        ToastModule
],
  providers: [MessageService],
  templateUrl: './sacar-cliente.html',
})
export class SacarCliente {}
