import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { environment } from 'src/environments/environment';

import { ApiResponse } from '../../models/respuestaApi';
import { catchError, map, shareReplay } from 'rxjs/operators';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';

@Injectable({
  providedIn: 'root'
})
export class PerfilService {

  private readonly restUrl = environment.apiHost + "/rest";
  private readonly documentosUrl = environment.apiHost + "/documentos";

  private readonly fotoCache = new Map<string, Observable<SafeUrl>>();

  constructor(private http: HttpClient,
    private sanitizer: DomSanitizer) {}

  /** Obtiene la foto de perfil o la reutiliza si ya se pidió */
  getUrlFotoPerfil(id: string, sexo: string): Observable<SafeUrl> {
    const key = id || `anon-${sexo}`;
    if (!this.fotoCache.has(key)) {
      const req$ = this.http
        .get(`${this.documentosUrl}/v1/fotosPerfil/${id}?sexo=${sexo}`, {
          responseType: 'blob'
        })
        .pipe(
          map(blob => this.toUrl(blob)),                 // ✅ success
          catchError(() => of(this.fallback(sexo))),     // ✅ error → imagen por defecto
          shareReplay(1)
        );
      this.fotoCache.set(key, req$);
    }
    return this.fotoCache.get(key)!;
  }
  private toUrl(blob: Blob): SafeUrl {
    return this.sanitizer.bypassSecurityTrustUrl(
      URL.createObjectURL(blob)
    );
  }

  private fallback(sexo: string): SafeUrl {
    const ruta = `assets/images/fotoPerfil/defecto-${sexo === 'MASCULINO' ? 'chico' : 'chica'}.jpg`;
    return this.sanitizer.bypassSecurityTrustUrl(ruta);
  }

  getMunicipios(idNuevaProvincia: number) : Observable<ApiResponse> {
    const url = `${this.restUrl}/v1/direccion/${idNuevaProvincia}/getMunicipios`;
    return this.http.get<ApiResponse>(url);
  }

  getProvincias(idNuevaComunidad: number) : Observable<ApiResponse> {
    const url = `${this.restUrl}/v1/direccion/${idNuevaComunidad}/getProvincias`;
    return this.http.get<ApiResponse>(url);
  }

  getFederaciones(sindicato: number) {
    const url = `${this.restUrl}/v1/sindicato/${sindicato}/getFederaciones`;
    return this.http.get<ApiResponse>(url);
  }

  getInicializaPerfil(id: string): Observable<ApiResponse> {
    const url = `${this.restUrl}/v1/perfil/${id}/inicializaFormularioPerfil`;
    return this.http.get<ApiResponse>(url);
  }
  getInicializaPerfilByNumeroCarnet(id: string): Observable<ApiResponse> {
    const url = `${this.restUrl}/v1/perfil/${id}/inicializaFormularioPerfilByNumeroCarnet`;
    return this.http.get<ApiResponse>(url);
  }
  getInicializa(): Observable<ApiResponse> {
    const url = `${this.restUrl}/v1/perfil/inicializaFormularioPerfil`;
    return this.http.get<ApiResponse>(url);
  }

  actualizarDatosBasicos(id: string, datos: { email: string; telefono: string; esTrabajador: boolean; esEstudiante: boolean; }) {
    const url = `${this.restUrl}/v1/perfil/${id}/basico`;

    return this.http.put(url, datos);
  }

  actualizarDatosEstudio(id: string, datos:any) {
    const url = `${this.restUrl}/v1/perfil/${id}/estudio`;

    return this.http.put(url, datos);
  }

  actualizarDatosTrabajo(id: string, datos: any) {
    const url = `${this.restUrl}/v1/perfil/${id}/trabajo`;

    return this.http.put(url, datos);
  }

  actualizarDatosSindicacion(id: string, datos: any) {
    const url = `${this.restUrl}/v1/perfil/${id}/sindicacion`;

    return this.http.put(url, datos);
  }

  subirImagen(formData: FormData, id: string) {
    const url = `${this.documentosUrl}/v1/fotosPerfil/${id}/actualizarImagenPerfil`;

    return this.http.post(url, formData);
  }

  obtenerImagen(id: number) {
    const url = `${this.documentosUrl}/documentos/${id}/imagenPerfil`;

    return this.http.get(url, { responseType: 'blob' });
  }
}

