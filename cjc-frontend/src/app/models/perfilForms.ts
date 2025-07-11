import { ComunidadAutonoma } from "./comunidadAutonoma";
import { Select, SelectWithPadreId } from "./select";
import { Provincia } from "./provincia";

export class FormularioPerfil {

    email: string = "";
    telefono: string = "";
    direccion: string = "";


    municipio: number = 0;
    provincia: number = 0;
    comunidadAutonoma: number = 0;

    nivelEducativo: number = 0;
    subdivision: number = 0;
    subsubdivision: number = 0;

    nombreCentroEducativo: string = "";
    nombreEstudios: string = "";
    sindicatoEstudiantil: boolean = false;
    anhoFinalizacion: number = 0;

    estudiante: boolean = false;
    trabajador: boolean = false;
    sindicado: boolean = false;
    participaOrganoRepresentacion: boolean = false;
    existeOrganoRepresentacionTrabajadores: string = "";
    direccionCentroTrabajo: string = "";
    nombreCentroTrabajo: string = "";
    nombreEmpresa: string = "";

    actividadEconomica: number = 0;
    fechaInicioContrato: string = '';
    tipoContrato: number = 0;
    modalidadTrabajo: number = 0;
    numeroTrabajadoresCentroTrabajo: number = 0;
    numeroTrabajadores: number = 0;
    profesion: string = "";

    modalidadesTrabajo: Select[] = [];
    municipios: Select[] = [];
    provincias: Provincia[] = [];
    comunidades: ComunidadAutonoma[] = [];
    actividadesEconomicas: Select[] = [];
    tiposContratos: Select[] = [];
    nivelesEducativos: Select[] = [];
    subdivisiones: SelectWithPadreId[] = [];
    subsubdivisiones: SelectWithPadreId[] = [];
    sindicatos: Select[] = [];
    federaciones: Select[] = [];
    sindicato: number = 0;
    federacion: number = 0;
    sindicatoOtros: string = "";
    federacionOtros: string = "";
    cargo: string = "";
    participaAreaJuventud: boolean = false;
  
}
