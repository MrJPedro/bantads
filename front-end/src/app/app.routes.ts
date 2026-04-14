import { Routes } from '@angular/router';
import { authGuardGuard } from './auth-guard-guard';

export const routes: Routes = [
  //Sem sem-perfil
  {
    path: '',
    redirectTo: 'landing-page',
    pathMatch: 'full'
  },

  {
    path: 'landing-page',
    loadComponent: () =>
      import('./pages/sem-perfil/landing-page/landing-page').then(
        (m) => m.LandingPage
      )
  },

  {
    path: 'login',
    loadComponent: () =>
      import('./pages/sem-perfil/login/login').then((m) => m.Login)
  },

  {
    path: 'autocadastro',
    loadComponent: () =>
      import('./pages/cliente/autocadastro/autocadastro').then(
        (m) => m.Autocadastro
      )
  },

  //Cliente
  {
    path: 'cliente',
    canActivate: [authGuardGuard],
    data: {tipoRequerido: 'CLIENTE'},
    loadChildren: () =>
      import('./pages/cliente/cliente.routes').then((m) => m.clienteRoutes)
  },
  //Gerente
  {
    path: 'gerente',
    canActivate: [authGuardGuard],
    data: {tipoRequerido: 'GERENTE'},
    loadChildren: () =>
      import('./pages/gerente/gerente.routes').then((m) => m.gerenteRoutes)
  },
  //Administrador
  {
    path: 'administrador',
    canActivate: [authGuardGuard],
    data: {tipoRequerido: 'ADMINISTRADOR'},
    loadChildren: () =>
      import('./pages/administrador/administrador.routes').then(
        (m) => m.administradorRoutes
      )
  }
];
