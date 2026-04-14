import { Routes } from '@angular/router';

export const administradorRoutes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./administrador-layout/administrador-layout').then(
        (m) => m.AdministradorLayout
      ),
    children: [
      {
        path: '',
        redirectTo: 'tela-inicial',
        pathMatch: 'full'
      },
      {
        path: 'tela-inicial',
        loadComponent: () =>
          import('./tela-inicial-administrador/tela-inicial-administrador').then(
            (m) => m.TelaInicialAdministrador
          )
      },
      {
        path: 'relatorio-clientes',
        loadComponent: () =>
          import('./relatorio-clientes/relatorio-clientes').then(
            (m) => m.RelatorioClientes
          )
      },
      {
        path: 'gerenciar-gerentes',
        loadComponent: () =>
          import('./crud-gerentes/crud-gerentes').then((m) => m.CrudGerentes)
      }
    ]
  }
];