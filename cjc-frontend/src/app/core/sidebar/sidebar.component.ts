import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { trigger, state, style, transition, animate } from '@angular/animations';
import { Router, RouterLink } from '@angular/router';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { TranslateModule } from '@ngx-translate/core';
import { distinctUntilChanged, filter, tap } from 'rxjs/operators';

import { PerfilService } from 'src/app/shared/services/perfil.service';
import { SidebarService } from './sidebar.service';
import { AuthService, MilitanteClaims  } from 'src/app/shared/services/auth.service';
import { Subject } from 'rxjs';
import { SkeletonModule } from 'primeng/skeleton';
import { NgClass, CommonModule } from '@angular/common';


export interface RouteInfo {
  path: string;
  title: string;
  icon: string;
  class: string;
  active: boolean;
  badge?: {
    text: string;
    class: string;
  };
}

export const FIXED_ROUTES: RouteInfo[] = [
  {
    path: '/inicio',
    title: 'ui.sidebar.inicio',
    icon: 'fa-solid fa-house',
    class: '',
    active: false,
    badge: { text: 'Próx.', class: 'badge bg-warning' }
  },
  {
    path: '/perfil',
    title: 'ui.sidebar.perfil',
    icon: 'fa-solid fa-user',
    class: '',
    active: false
  }
];

export const DOCUMENTS_ROUTE: RouteInfo = {
  path: '/documentos',
  title: 'ui.sidebar.doc',
  icon: 'fa fa-book',
  class: '',
  active: false,
  badge: { text: 'Próx.', class: 'badge bg-warning' }
};

type Comite = { tipo: string; id: string; nombre: string; };

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss'],
  animations: [
    trigger('slide', [
      state('up', style({ height: 0 })),
      state('down', style({ height: '*' })),
      transition('up <=> down', animate(200))
    ])
  ],
  standalone: true,
  imports: [NgClass, RouterLink, TranslateModule, SkeletonModule, CommonModule],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SidebarComponent implements OnInit {
  loadingSidebar = true;
  loadingFotoPerfil = true;

  menuGeneral: RouteInfo[] = [];
  menuExtra: RouteInfo[] = [];

  nombreUsuario = '';
  apellidosUsuario = '';
  numeroCarnet = '';
  fotoPerfil!: SafeUrl;

  constructor(
    private sanitizer: DomSanitizer,
    public sidebarService: SidebarService,
    private perfilService: PerfilService,
    public router: Router,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) { }

  private readonly destroy$ = new Subject<void>();


  ngOnInit(): void {
    this.authService.user$.pipe(
      filter((u): u is MilitanteClaims => u !== null),
      distinctUntilChanged((a, b) => a.militanteId === b.militanteId),
      tap(user => this.setDatosUsuario(user))
    ).subscribe();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private setDatosUsuario(user: MilitanteClaims): void {
    const { militanteId, sexo, given_name, family_name, preferred_username, groups } = user;
    this.nombreUsuario   = given_name;
    this.apellidosUsuario = family_name;
    this.numeroCarnet    = preferred_username;

    console.log('[Sidebar] Cargando imagen de perfil...');
    /* ❗ Solo la primera vez pedimos la imagen */
    if (!this.fotoPerfil) {
      this.cargarImagenPerfil(militanteId, sexo);
    }
    console.log('[Sidebar] Listo');
    // 2- Localizar el grupo que acaba en Colectivo_… | Celula_…
    const grupoPrincipal = this.obtenerGrupoPrincipal(groups);
    if (!grupoPrincipal) {
      console.warn('[Sidebar] No se encontró grupo principal; se usará el primero');
    }
    const comites = this.parsearComites(grupoPrincipal ?? groups[0]);

    // 3- Separar comité base y resto
    const baseIdx = comites.length - 1;  // siempre el último
    const comiteBase = (['Colectivo', 'Celula'].includes(comites[baseIdx]?.tipo))
      ? comites.pop()!
      : null;

    // 4- Construir menú
    this.menuGeneral = [
      this.getFixedRoute('/inicio'),
      ...comites.map(c => this.createRoute(`/comite/${c.id}`, c.nombre)),
      ...(comiteBase
        ? [this.createRoute(`/${comiteBase.tipo.toLowerCase()}/${comiteBase.id}`,
          comiteBase.nombre)]
        : []),
      this.getFixedRoute('/perfil')
    ];

    this.menuExtra = [DOCUMENTS_ROUTE];
    this.loadingSidebar = false;
    this.cdr.markForCheck();
  }

  /** Devuelve la cadena de grupo cuyo último segmento empieza por Colectivo_ o Celula_ */
  private obtenerGrupoPrincipal(groups: string[]): string | null {
    return groups.find(g => {
      const lastSeg = g.split('/').filter(Boolean).pop()!;
      return lastSeg.startsWith('Colectivo_') || lastSeg.startsWith('Celula_');
    }) ?? null;
  }

  private createRoute(path: string, title: string): RouteInfo {
    return { path, title, icon: 'fa-solid fa-map', class: '', active: false };
  }

  private getFixedRoute(path: string): RouteInfo {
    const route = FIXED_ROUTES.find(r => r.path === path);
    if (!route) { throw new Error(`Ruta fija no encontrada: ${path}`); }
    return route;
  }

  /**
   * Convierte "/…/Comite_X_1/Comite_Y_2/Colectivo_Z_3" → [{tipo:'Comite',…}, …]
   */
  private parsearComites(ruta: string): Comite[] {
    return ruta
      .split('/')
      .filter(seg => seg.includes('_'))
      .map(seg => {
        const [tipo, nombre, id] = seg.split('_');
        return { tipo, id, nombre };
      });
  }

  /** Carga la foto de perfil o pone la por defecto según sexo */
  cargarImagenPerfil(usuarioId: string, sexo: string): void {
    this.perfilService.getUrlFotoPerfil(usuarioId, sexo).subscribe((url: SafeUrl) => {
      this.fotoPerfil = url;            // ✔️ tipo correcto
      this.loadingFotoPerfil = false;
      this.cdr.markForCheck();          // necesario con OnPush
    });
  }

  logOut(): void {
    this.authService.logout();
  }

  hasBackgroundImage(): boolean {
    return this.sidebarService.hasBackgroundImage;
  }

}
