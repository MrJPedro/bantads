import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { InputTextModule } from 'primeng/inputtext';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { NgxMaskDirective, provideNgxMask } from 'ngx-mask';

@Component({
  selector: 'app-barra-pesquisa',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    InputTextModule,
    IconFieldModule,
    InputIconModule,
    NgxMaskDirective
  ],
  templateUrl: './barra-pesquisa.html',
  styleUrl: './barra-pesquisa.css'
})
export class BarraPesquisa {
  @Input() placeholder = 'Pesquisar...';
  @Input() value = '';
  @Input() disabled = false;
  @Input() largura = '100%';
  @Input() icone = 'pi pi-search';
  @Input() mask = '';

  @Output() valueChange = new EventEmitter<string>();
  @Output() pesquisar = new EventEmitter<string>();
  @Output() limpar = new EventEmitter<void>();

  onInputChange(valor: string) {
    this.value = valor;
    this.valueChange.emit(valor);
    this.pesquisar.emit(valor);
  }

  limparCampo() {
    this.value = '';
    this.valueChange.emit('');
    this.pesquisar.emit('');
    this.limpar.emit();
  }
}