import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/internal/Observable';
import { environment } from 'src/environments/environment';

import { ApiResponse } from '../../models/respuestaApi';

@Injectable({
  providedIn: 'root'
})
export class MilitanteService {

  private readonly restUrl = environment.apiHost + "/rest";
  private readonly documentosUrl = environment.apiHost + "/documentos";

  constructor(private http: HttpClient) { }

  crearMilitante(formData: any) {
    const url = `${this.restUrl}/v1/militantes`;

    return this.http.post<ApiResponse>(url, formData);
  }

  activaMilitante(id: string) {
    const url = `${this.restUrl}/v1/militantes/${id}/activar`;

    return this.http.put<ApiResponse>(url, {});
  }

  concederMilitancia(id: string, formData: any) {
    const url = `${this.restUrl}/v1/militantes/${id}/conceder-militancia`;

    return this.http.put<ApiResponse>(url, formData);
  }

  editarMilitante(formData: any, id: string) {
    const url = `${this.restUrl}/v1/militantes/${id}`;

    return this.http.put<ApiResponse>(url, formData);
  }

  eliminarMilitante(militanteId: string, comiteBaseId: number) {
    const url = `${this.restUrl}/v1/militantes/${comiteBaseId}/${militanteId}`;

    return this.http.delete<ApiResponse>(url);
  }

  // Métodos para ficha de movilidad
  crearFichaMovilidad(militanteId: string, formData: any): Observable<ApiResponse> {
    const url = `${this.restUrl}/v1/militantes/${militanteId}/ficha-movilidad`;
    return this.http.post<ApiResponse>(url, formData);
  }

  editarFichaMovilidad(militanteId: string, formData: any): Observable<ApiResponse> {
    const url = `${this.restUrl}/v1/militantes/${militanteId}/ficha-movilidad`;
    return this.http.put<ApiResponse>(url, formData);
  }

  obtenerFichaMovilidad(militanteId: string): Observable<ApiResponse> {
    const url = `${this.restUrl}/v1/militantes/${militanteId}/ficha-movilidad`;
    return this.http.get<ApiResponse>(url);
  }

  eliminarFichaMovilidad(militanteId: string): Observable<ApiResponse> {
    const url = `${this.restUrl}/v1/militantes/${militanteId}/ficha-movilidad`;
    return this.http.delete<ApiResponse>(url);
  }

  descargarFichaMovilidadDocx(militanteId: string): Observable<Blob> {
    const url = `${this.documentosUrl}/v1/archivos/${militanteId}/ficha-movilidad/docx`;
    const headers = new HttpHeaders({ 'Accept': 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' });

    return this.http.get(url, { headers, responseType: 'blob' });
  }

  descargarFichaMovilidadPdf(militanteId: string): Observable<Blob> {
    const url = `${this.documentosUrl}/v1/archivos/${militanteId}/ficha-movilidad/pdf`;
    const headers = new HttpHeaders({ 'Accept': 'application/pdf' });

    return this.http.get(url, { headers, responseType: 'blob' });
  }
}

