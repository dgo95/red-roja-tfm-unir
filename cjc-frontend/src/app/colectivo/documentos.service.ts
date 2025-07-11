import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { delay, map } from 'rxjs/operators';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { ActualizarDocumentoDTO, DocumentoRecibidoDTO, NuevoDocumentoDTO } from './documento.dto';
import { NivelDocumento, Confidencialidad, TipoDocumento, Categoria } from './documento.enums';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class DocumentosService {
  private readonly documentosUrl = environment.apiHost + '/documentos/v1/archivos';

  constructor(private http: HttpClient) { }

  getDocumentos(params: {
    page: number;
    rows: number;
    filtroNivel?: NivelDocumento | string;
    filtroConfidencialidad?: Confidencialidad | string;
    filtroTipo?: (TipoDocumento | string)[];
    filtroCategoria?: (Categoria | string)[];
    searchText?: string;
    sortField?: string;
    sortOrder?: 1 | -1;
    ids: string[]; // Nuevo parámetro
  }): Observable<{ data: DocumentoRecibidoDTO[]; totalRecords: number }> {
    const queryParams: any = {
      page: params.page,
      rows: params.rows,
      sortOrder: params.sortOrder ?? 1
    };
    if (params.filtroNivel) queryParams.filtroNivel = params.filtroNivel;
    if (params.filtroConfidencialidad) queryParams.filtroConfidencialidad = params.filtroConfidencialidad;
    if (params.filtroTipo && params.filtroTipo.length > 0) queryParams.filtroTipo = params.filtroTipo;
    if (params.filtroCategoria && params.filtroCategoria.length > 0) queryParams.filtroCategoria = params.filtroCategoria;
    if (params.searchText) queryParams.searchText = params.searchText;
    if (params.sortField) queryParams.sortField = params.sortField;
    if (params.ids && params.ids.length > 0) queryParams.ids = params.ids;

    return this.http.get<{ data: DocumentoRecibidoDTO[]; totalRecords: number }>(`${this.documentosUrl}`, { params: queryParams });
  }



  subirDocumento(dto: NuevoDocumentoDTO): Observable<any> {
    const formData = new FormData();
    formData.append('archivo', dto.archivo!);
    formData.append('titulo', dto.titulo);
    formData.append('fecha', dto.fecha!.toISOString().split('T')[0]);
    formData.append('confidencialidad', Confidencialidad[dto.confidencialidad!]);
    formData.append('tipo', TipoDocumento[dto.tipo!]);
    formData.append('categoria', Categoria[dto.categoria!]);
    formData.append('propietario', dto.propietario.toString());
    return this.http.post<void>(`${this.documentosUrl}`, formData);
  }
  /* -------- OBTENER DETALLE --------------------------------------------- */
  /** Descarga el fichero y expone cabeceras, mime-type y nombre */
  obtenerDocumento(uuid: string): Observable<{
    blob: Blob;
    mimeType: string;
    filename: string | null;
  }> {
    return this.http
      .get(`${this.documentosUrl}/${uuid}`, {
        observe: 'response',
        responseType: 'blob',
      })
      .pipe(
        map((resp: HttpResponse<Blob>) => ({
          blob: resp.body!,
          mimeType: resp.headers.get('content-type') ?? 'application/octet-stream',
          filename: extraerNombre(resp.headers.get('content-disposition')),
        }))
      );
  }

  /* -------- EDITAR (PATCH) ---------------------------------------------- */
  editarDocumento(uuid: string, dto: ActualizarDocumentoDTO) {
    /* ① Pasamos todos los enum a su representación string */
    const body: any = {
      ...dto,
      confidencialidad: Confidencialidad[dto.confidencialidad],
      tipo: TipoDocumento[dto.tipo],
    };
    if (dto.categorias !== undefined) body.categorias = [...dto.categorias].map(c => Categoria[c]);
    return this.http.put<DocumentoRecibidoDTO>(
      `${this.documentosUrl}/${uuid}`,
      body
    );
  }

  /* -------- ELIMINAR ---------------------------------------------------- */
  eliminarDocumento(uuid: string) {
    return this.http.delete<void>(`${this.documentosUrl}/${uuid}`);
  }
}

/* ----------------- utilidades privadas ------------------------ */
function extraerNombre(disposition: string | null): string | null {
  if (!disposition) return null;
  const match = disposition.match(/filename\*?=(?:UTF-8''|")?([^;"']+)/i);
  return match ? decodeURIComponent(match[1]) : null;
}