import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root',
})
export class AuthGuardNAService implements CanActivate {
  constructor(private auth: AuthService, private router: Router) { }

  canActivate(): boolean | UrlTree {
    
    return true; // Permite el acceso si no está autenticado
  }
}
