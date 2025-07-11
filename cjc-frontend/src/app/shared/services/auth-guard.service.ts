import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  RouterStateSnapshot
} from '@angular/router';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {

  constructor(private auth: AuthService) {}

  canActivate(
    _route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {

    /* 1 · si ya hay sesión válida, adelante */
    if (this.auth.isAuthenticated()) {
      return true;
    }

    /* 2 · si estoy en el callback (code & state) dejo que Angular cargue */
    if (state.url.includes('code=') && state.url.includes('state=')) {
      return true;
    }

    /* 3 · primera vez → inicio login y corto navegación */
    this.auth.login();
    return false;
  }
}
