import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-gerente-layout',
  imports: [CommonModule, RouterModule, RouterLink],
  templateUrl: './gerente-layout.html',
  styleUrl: './gerente-layout.css',
})
export class GerenteLayout {}
