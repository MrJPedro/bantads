import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';
import { InputTextModule } from 'primeng/inputtext';
import { Dialog } from "primeng/dialog";
import { NgxMaskDirective, provideNgxMask } from 'ngx-mask';

@Component({
  selector: 'app-autocadastro',
  imports: [CommonModule, FormsModule, ButtonModule, CheckboxModule, InputTextModule, Dialog, NgxMaskDirective],
  templateUrl: './autocadastro.html',
  providers: [provideNgxMask()],
  styleUrl: './autocadastro.css',
})
export class Autocadastro {
showDialog() {
this.visible = true;
}
@Input() mask = '';
email = '';
visible: any;
}
