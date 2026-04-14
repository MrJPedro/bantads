import { Routes } from '@angular/router';

export const clienteRoutes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./cliente-layout/cliente-layout').then((m) => m.ClienteLayout),
    children: [
      {
        path: '',
        redirectTo: 'tela-inicial',
        pathMatch: 'full'
      },
      {
        path: 'tela-inicial',
        loadComponent: () =>
          import('./tela-inicial-cliente/tela-inicial-cliente').then(
            (m) => m.TelaInicialCliente
          )
      },
      {
        path: 'perfil',
        loadComponent: () => import('./perfil/perfil').then((m) => m.Perfil)
      },
      {
        path: 'extrato',
        loadComponent: () =>
          import('./extrato-cliente/extrato-cliente').then((m) => m.ExtratoCliente)
      },
      {
        path: 'transferir',
        loadComponent: () =>
          import('./transferir-cliente/transferir-cliente').then(
            (m) => m.TransferirCliente
          )
      },
      {
        path: 'depositar',
        loadComponent: () =>
          import('./depositar-cliente/depositar-cliente').then(
            (m) => m.DepositarCliente
          )
      },
      {
        path: 'sacar',
        loadComponent: () =>
          import('./sacar-cliente/sacar-cliente').then((m) => m.SacarCliente)
      }
    ]
  }
];