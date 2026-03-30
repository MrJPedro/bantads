import { Routes } from '@angular/router';
import { LandingPage } from './pages/sem-perfil/landing-page/landing-page';
import { Login } from './pages/sem-perfil/login/login';
import { ClienteLayout } from './pages/cliente/cliente-layout/cliente-layout';
import { Autocadastro } from './pages/cliente/autocadastro/autocadastro';
import { TelaInicialCliente } from './pages/cliente/tela-inicial-cliente/tela-inicial-cliente';
import { GerenteLayout } from './pages/gerente/gerente-layout/gerente-layout';
import { TelaInicialGerente } from './pages/gerente/tela-inicial-gerente/tela-inicial-gerente';
import { ConsultarClientes } from './pages/gerente/consultar-clientes/consultar-clientes';
import { ConsultarClienteIndividual } from './pages/gerente/consultar-cliente-individual/consultar-cliente-individual';
import { ConsultarClientesMelhores } from './pages/gerente/consultar-clientes-melhores/consultar-clientes-melhores';
import { AdministradorLayout } from './pages/administrador/administrador-layout/administrador-layout';
import { TelaInicialAdministrador } from './pages/administrador/tela-inicial-administrador/tela-inicial-administrador';
import { RelatorioClientes } from './pages/administrador/relatorio-clientes/relatorio-clientes';
import { CrudGerentes } from './pages/administrador/crud-gerentes/crud-gerentes';
import { Perfil } from './pages/cliente/perfil/perfil';

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

  {
    path: 'autocadastro',
    component: Autocadastro
      },

  //Cliente
  {
    path: 'cliente',
    component: ClienteLayout,
    children: [

      {
        path: 'tela-inicial',
        component: TelaInicialCliente
      },

      {
        path: 'perfil',
        component: Perfil
      }
    ]
  },
  //Gerente
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
      },
      {
        path: 'consultar-cliente-individual',
        component: ConsultarClienteIndividual
      },
      {
        path: 'consultar-clientes-melhores',
        component: ConsultarClientesMelhores
      }
    ]
  },
  //Administrador
  {
    path: 'administrador',
    component: AdministradorLayout,
    children: [
      {
        path: 'tela-inicial',
        component: TelaInicialAdministrador
      },

      { path: 'relatorio-clientes',
        component: RelatorioClientes
      },

      {
        path: 'gerenciar-gerentes',
        component: CrudGerentes
      }
    ]
  }
];
