import { Routes } from '@angular/router';
import { LandingPage } from './pages/sem-perfil/landing-page/landing-page';
import { Login } from './pages/sem-perfil/login/login';
import { ClienteLayout } from './pages/cliente/cliente-layout/cliente-layout';
import { Autocadastro } from './pages/cliente/autocadastro/autocadastro';
import { TelaInicialCliente } from './pages/cliente/tela-inicial-cliente/tela-inicial-cliente';
import { GerenteLayout } from './pages/gerente/gerente-layout/gerente-layout';
import { TelaInicialGerente } from './pages/gerente/tela-inicial-gerente/tela-inicial-gerente';
import { ConsultarClientes } from './pages/gerente/consultar-clientes/consultar-clientes';

export const routes: Routes = [
  //Sem sem-perfil
  {
    path: '',
    redirectTo: 'landing-page',
    pathMatch: 'full'
  },

  {
    path: 'landing-page',
    component: LandingPage
  },

  {
    path: 'login',
    component: Login
  },

  //Cliente
  {
    path: 'cliente',
    component: ClienteLayout,
    children: [
      {
        path: 'autocadastro',
        component: Autocadastro
      },

      {
        path: 'tela-inicial',
        component: TelaInicialCliente
      }
    ]
  },
  //gerente
  {
    path: 'gerente',
    component: GerenteLayout,
    children: [
      {
        path: 'tela-inicial',
        component: TelaInicialGerente
      },
      {
        path: 'consultar-clientes',
        component: ConsultarClientes
      }
    ]
  }
];
