import { Categoria, Confidencialidad, NivelDocumento, TipoDocumento } from "./documento.enums";

// DTO para documentos recibidos (sin url)
export interface DocumentoRecibidoDTO {
  id: string;
  titulo: string;
  fecha: Date;
  nivel: NivelDocumento;
  confidencialidad: Confidencialidad;
  tipo: TipoDocumento;
  categoria: Categoria;
  propietario: string;
}

// DTO para creación de nuevo documento (sin nivel, sin url, id=0, y con el archivo)
export interface NuevoDocumentoDTO {
  id: 0;
  titulo: string;
  fecha: Date | undefined;
  confidencialidad: Confidencialidad | undefined;
  tipo: TipoDocumento | undefined;
  categoria: Categoria | undefined;
  propietario: string;
  archivo: File | undefined;
}

export interface ActualizarDocumentoDTO {
  titulo: string;
  nivel?: NivelDocumento;
  confidencialidad: Confidencialidad;
  tipo: TipoDocumento;
  categorias?: Set<Categoria>;
}