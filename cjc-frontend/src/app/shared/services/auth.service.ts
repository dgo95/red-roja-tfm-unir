import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {
  OAuthService,
  OAuthErrorEvent,
  OAuthEvent,
  AuthConfig
} from 'angular-oauth2-oidc';
import {
  BehaviorSubject,
  filter,
  Observable,
  switchMap,
  tap
} from 'rxjs';
import { environment } from 'src/environments/environment';
import { SupportedLanguages } from '../../models/SupportedLanguages';
import { distinctUntilChanged } from 'rxjs/operators';
import isEqual from 'fast-deep-equal';

/* ----------------------- Config global para Keycloak ---------------------- */
const authConfig: AuthConfig = {
  issuer: environment.keycloak.issuer,
  clientId: environment.keycloak.clientId,
  redirectUri: window.location.origin,
  postLogoutRedirectUri: window.location.origin,
  responseType: 'code',
  scope: 'openid profile email offline_access', 
  useSilentRefresh: false,
  timeoutFactor: 0.75,
  showDebugInformation: !environment.production
};

/* ------------------- Claims que esperamos del ID-token -------------------- */
export interface MilitanteClaims {
  militanteId: string;
  given_name: string;
  family_name: string;
  sexo: 'MASCULINO' | 'FEMENINO' | string;
  groups: string[];
  preferred_username: string;           // atributo estándar de Keycloak
  subgroups: Record<string, string[]>;
  locale?: string;                     // atributo estándar de Keycloak
}

@Injectable({ providedIn: 'root' })
export class AuthService {

  /* ---------- estado reactivo (público sólo como Observable) ------------- */
  private accessToken$   = new BehaviorSubject<string | null>(null);
  private militante$     = new BehaviorSubject<MilitanteClaims | null>(null);

  /* ------------------------- Endpoints de tu API -------------------------- */
  private readonly securityUrl = `${environment.apiHost}/auth/v1`;
  private readonly restUrl     = `${environment.apiHost}/rest/v1`;

  constructor(
    private readonly oauth: OAuthService,
    private readonly http : HttpClient
  ) {
    this.bootstrapOAuth();
  }

  /* =======================================================================
   *                                API                                     *
   * ======================================================================= */

  /** Información del militante */
  get user$(): Observable<MilitanteClaims | null> {
    return this.militante$
      .asObservable()
      .pipe(distinctUntilChanged((a, b) => isEqual(a, b)));
  }

  /** Acceso directo al token (p.e. para el interceptor) */
  get token(): string | null {
    return this.accessToken$.value;
  }

  /** ¿Hay sesión válida? */
  isAuthenticated(): boolean {
    return this.oauth.hasValidAccessToken();
  }

  /** Arranca el flujo de login. Pasamos el idioma activo como ui_locales. */
  login(language: SupportedLanguages = this.currentLanguage): void {
    this.oauth.initCodeFlow(undefined, { ui_locales: language });
  }

  logout(): void {
    this.oauth.logOut();               // redirige a /realms/…/protocol/openid-connect/logout
    this.accessToken$.next(null);
    this.militante$.next(null);
    sessionStorage.clear();
  }

  /** Ejemplo de llamada protegida al back (usa interceptor) */
  getDatosInicio(): Observable<any> {
    return this.http.get(`${this.restUrl}/inicio`);
  }

  /** Actualiza el idioma en Keycloak **y** en tu microservicio */
  cambiarIdioma(lang: SupportedLanguages, militanteId: string): Observable<void> {
    const body   = { lang };
    // 1) actualizamos atributo locale del usuario en Keycloak
    const kc$    = this.http.put<void>(`${this.securityUrl}/militantes/${militanteId}/locale`, body);
    // 2) actualizamos columna en tu tabla Militant
    const back$  = this.http.put<void>(`${this.restUrl}/militantes/${militanteId}/idioma`, body);
    return kc$.pipe(switchMap(() => back$));
  }

  /* =======================================================================
   *                         Bootstrap & eventos                            *
   * ======================================================================= */

  /** Idioma que la SPA tiene activo en ngx-translate */
  private get currentLanguage(): SupportedLanguages {
    return (sessionStorage.getItem('preferredLang') ??
            navigator.language.split('-')[0] ??
            SupportedLanguages.Castellano) as SupportedLanguages;
  }

  private bootstrapOAuth(): void {
    this.oauth.configure(authConfig);
    this.oauth.setStorage(sessionStorage);         // <= evitar fugas en localStorage
    this.oauth.setupAutomaticSilentRefresh();      // <= refresh-token sin tu código

    this.oauth.loadDiscoveryDocumentAndTryLogin()
      .then(() => {
        if (this.isAuthenticated()) {
          this.publishSession();
        }
      })
      .catch(err => console.error('[Auth] init error:', err));

    /* Centralizamos aquí todos los eventos */
    this.oauth.events.subscribe((e: OAuthEvent | OAuthErrorEvent) => {
      switch (e.type) {
        case 'token_received':
          this.publishSession();
          break;
        case 'token_refresh_error':
        case 'session_terminated':
        case 'session_error':
          this.logout();
          break;
        default:
          // otros eventos que no nos importan
      }
    });

    /* Por si setupAutomaticSilentRefresh fallara, fuerza un refresh 5 s antes: */
    this.oauth.events.pipe(
      filter(ev => ev.type === 'token_expires'),
      tap(() => this.oauth.refreshToken().catch(console.error))
    ).subscribe();
  }

  /** Actualiza subjects y SessionStorage con la sesión actual */
  private publishSession(): void {
    const token  = this.oauth.getAccessToken();
    const claims = this.oauth.getIdentityClaims() as MilitanteClaims;

    this.accessToken$.next(token);
    this.militante$.next(claims);

    sessionStorage.setItem('access_token', token ?? '');
  }
}