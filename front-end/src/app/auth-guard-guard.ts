import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './services/auth-service';

export const authGuardGuard: CanActivateFn = (route, state) => {
  const auth = inject(AuthService)
  const router = inject(Router)
  const tipoRequerido = route.data['tipoRequerido']
  const perfilAtual = auth.getUsuarioLogado()?.tipo || null

  if (perfilAtual === tipoRequerido){
    return true
  }

  // switch case para página inicial
  switch(perfilAtual) {
    case 'CLIENTE':
      router.navigate(['/cliente/tela-inicial'])
      console.log("Router Navigate para /cliente/tela-inicial")
      break
    case 'GERENTE':
      router.navigate(['/gerente/tela-inicial'])
      console.log("Router Navigate para /gerente/tela-inicial")
      break
    case 'ADMINISTRADOR':
      router.navigate(['/administrador/tela-inicial'])
      console.log("Router Navigate para /administrador/tela-inicial")
      break
    default:
      router.navigate(['/login'])
      console.log("Router Navigate para /login")
    return false
  }

  return false
};
