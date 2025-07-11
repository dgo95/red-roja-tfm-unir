// app.module.ts
import { Routes } from '@angular/router';
import { AuthGuard } from './app/shared/services/auth-guard.service';

export const routes: Routes = [
    {
        path: '',
        loadComponent: () =>
            import('./app/inicio/inicio.component').then((m) => m.InicioComponent),
        pathMatch: 'full',
        canActivate: [AuthGuard],
    },
    {
        path: 'inicio',
        loadComponent: () =>
            import('./app/inicio/inicio.component').then((m) => m.InicioComponent),
        canActivate: [AuthGuard],
    },
    {
        path: 'comite/:id',
        loadComponent: () =>
            import('./app/colectivo/colectivo.component').then(
                (m) => m.ColectivoComponent
            ),
        canActivate: [AuthGuard],
        data: { esComite: true }
    },
    {
        path: 'colectivo/:id',
        loadComponent: () =>
            import('./app/colectivo/colectivo.component').then(
                (m) => m.ColectivoComponent
            ),
        canActivate: [AuthGuard],
        data: { esComite: false }
    },
    {
        path: 'perfil',
        loadComponent: () =>
            import('./app/perfil/perfil.component').then((m) => m.PerfilComponent),
        canActivate: [AuthGuard],
    },
    {
        path: 'documentos',
        loadComponent: () =>
            import('./app/documentos/documentos.component').then(
                (m) => m.DocumentosComponent
            ),
        canActivate: [AuthGuard],
    },
    {
        path: 'ajustes',
        loadComponent: () =>
            import('./app/ajustes/ajustes.component').then(
                (m) => m.AjustesComponent
            ),
        canActivate: [AuthGuard],
    },
];
