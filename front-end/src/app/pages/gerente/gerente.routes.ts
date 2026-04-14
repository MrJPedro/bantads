import { Routes } from '@angular/router';

export const gerenteRoutes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./gerente-layout/gerente-layout').then((m) => m.GerenteLayout),
    children: [
      {
        path: '',
        redirectTo: 'tela-inicial',
        pathMatch: 'full'
      },
      {
        path: 'tela-inicial',
        loadComponent: () =>
          import('./tela-inicial-gerente/tela-inicial-gerente').then(
            (m) => m.TelaInicialGerente
          )
      },
      {
        path: 'consultar-clientes',
        loadComponent: () =>
          import('./consultar-clientes/consultar-clientes').then(
            (m) => m.ConsultarClientes
          )
      },
      {
        path: 'consultar-cliente-individual',
        loadComponent: () =>
          import('./consultar-cliente-individual/consultar-cliente-individual').then(
            (m) => m.ConsultarClienteIndividual
          )
      },
      {
        path: 'consultar-clientes-melhores',
        loadComponent: () =>
          import('./consultar-clientes-melhores/consultar-clientes-melhores').then(
            (m) => m.ConsultarClientesMelhores
          )
      }
    ]
  }
];