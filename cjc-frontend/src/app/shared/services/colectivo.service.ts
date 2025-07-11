import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs/internal/Observable';
import { environment } from 'src/environments/environment';

import { ApiResponse } from '../../models/respuestaApi';
import { Reunion } from 'src/app/models/reunion';

@Injectable({
  providedIn: 'root'
})
export class ColectivoService {
  
  private readonly restUrl = environment.apiHost+"/rest/v1";
  private readonly documentosUrl = environment.apiHost+"/documentos/v1";
  private plantilla = 'assets/data/ordenDelDiaDefault.json';

  constructor(private http: HttpClient) { }


  getOrdenDiaDefecto(): Observable<any> {
    return this.http.get(this.plantilla);
  }

  obtenerCensoLaboral(id: number, esComite: boolean, pageIndex: number, pageSize: number, active: string, direction: string) {
    let params = new HttpParams()
      .set('page', pageIndex.toString())
      .set('size', pageSize.toString())
      .set('sort', active === undefined ? 'numeroCarnet' : active)
      .set('order', direction === '' ? 'asc' : direction);
    const colectivo = esComite ? 'comite' : 'colectivo';
    const url = `${this.restUrl}/${colectivo}/${id}/censoLaboral`;
    return this.http.get<ApiResponse>(url, { params });
  }

  obtenerCensoGeneral(id:number,comite:boolean,pageIndex: number, pageSize: number, sortActive: string, sortDirection: string)  {
    
    let params = new HttpParams()
      .set('page', pageIndex.toString())
      .set('size', pageSize.toString())
      .set('sort', sortActive === undefined ? 'numeroCarnet' : sortActive)
      .set('order', sortDirection === '' ? 'asc' : sortDirection);
    
    const colectivo = comite ? 'comite' : 'colectivo';
    const url = `${this.restUrl}/${colectivo}/${id}/censoGeneral`;

    return this.http.get<ApiResponse>(url, { params });
  }

  descargarExcel(tipo: string, esComite: boolean, id: number): Observable<Blob> {
    const colectivo = esComite ? 'comite' : 'colectivo';
    const url = `${this.documentosUrl}/archivos/xlsx/${colectivo}/censo/${tipo}/${id}`;
    const headers = new HttpHeaders({
      'Accept': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    });
    return this.http.get(url, { headers, responseType: 'blob' });
  }

  getDatosCenso(tipo: string, esComite: boolean, id: number, pageIndex: number, pageSize: number) {

    let params = new HttpParams()
      .set('page', pageIndex.toString())
      .set('size', pageSize.toString());

    const colectivo = esComite ? 'comite' : 'colectivo';
    const url = `${this.restUrl}/${colectivo}/${id}/${tipo}/getCenso`;

    return this.http.get<ApiResponse>(url, { params });
  }

  actualizarReunion(id: number, reunion: Reunion) {
    const url = `${this.restUrl}/colectivo/${id}/reunion`;
    return this.http.put<ApiResponse>(url,reunion);
  }
  crearReunion(id: number, reunion: any) {
    const url = `${this.restUrl}/colectivo/${id}/reunion`;
    return this.http.post<ApiResponse>(url,reunion);
  }

  actualizarResponsabilidades(id: number, responsabilidades: any[]) {
    const url = `${this.restUrl}/colectivo/${id}/actualizarResponsabilidades`;
    return this.http.put<ApiResponse>(url,responsabilidades);
  }

  getInicializaColectivo(id: number): Observable<ApiResponse> {
    const url = `${this.restUrl}/colectivo/${id}/inicializaColectivo`;
    return this.http.get<ApiResponse>(url);
  }

  inicializaContacto(id: number | null,comiteId:number|null): Observable<ApiResponse> {
    let url = `${this.restUrl}/colectivo/inicializaContacto`;
    
    if (id) {
      url = url.concat(`?id=${id}`);
    }
    if (comiteId) {
      url = url.concat(`?comiteId=${comiteId}`);
    }
    return this.http.get<ApiResponse>(url);
  }
}