import { Component, OnInit } from '@angular/core';
// Importa el componente del modal
import { TranslateService } from '@ngx-translate/core';
import { ToastrService } from 'ngx-toastr';
import { ColectivoService } from '../shared/services/colectivo.service';
import { AuthService } from '../shared/services/auth.service';
import { ApiResponse } from '../models/respuestaApi';
import { Militante } from '../models/militante';
import { Reunion } from '../models/reunion';
import { format, isBefore, parse } from 'date-fns';
import { Comite } from '../models/colectivo';
import { ActivatedRoute } from '@angular/router';
import { CensoComponent } from '../shared/censo/censo.component';
import { EditarReunionesComponent } from './editar-reuniones/editar-reuniones.component';
import { EditarResponsabilidadesModalComponent } from './editar-responsabilidades-modal/editar-responsabilidades-modal.component';

import { MatTabsModule } from '@angular/material/tabs';
import { ROLE_MAP, ROLE_MAP_INV } from '../shared/role-map';
import { DocumentosTabComponent } from "./documentos-tab/documentos-tab.component";

@Component({
  selector: 'app-colectivo',
  templateUrl: './colectivo.component.html',
  styleUrls: ['./colectivo.component.scss'],
  standalone: true,
  imports: [MatTabsModule, EditarResponsabilidadesModalComponent, EditarReunionesComponent, CensoComponent, DocumentosTabComponent]
})
export class ColectivoComponent implements OnInit {
 

  // Propiedad para controlar la visibilidad del modal
  mostrarModalEditarResponsabilidades: boolean = false;
  mostrarEditarReuniones: boolean = false;

  //fechas
  fechaUltimaReunion: string = "";
  fechaProximaReunion: string = "";
  reunionConvocada: boolean = false;

  m: Militante = new Militante();
  // Indica si el componente es un comité
  public esComite: boolean = false;
  colectivo!: Comite;
  comiteId: number = 0;

  responsabilidades: Responsabilidad[] = Object.keys(ROLE_MAP).map(k => ({
    clave: k as ClaveResponsabilidad,
    valor: ''
  }));

  militantes: Responsable[] = [
  ];

  reunion: Reunion = new Reunion();

  indiceTabCenso = 1;
  indiceSeleccionado = 0;

  alCambiarTab(indice: number) {
    this.indiceSeleccionado = indice;
  }

  constructor(private route: ActivatedRoute, private translate: TranslateService, private colectivoService: ColectivoService, private toastr: ToastrService, private authService: AuthService,) { }

  ngOnInit() {
    // Detectar el modo desde la ruta
    // Detectar si es comité desde el data de la ruta
    const esComiteRuta = this.route.snapshot.data['esComite'];
    this.esComite = !!esComiteRuta;
    this.route.paramMap.subscribe(params => {
      this.comiteId = parseInt(params.get('id')!);
      this.loadComiteData(this.comiteId);
    });
  }

  loadComiteData(comiteId: number) {

    this.colectivoService.getInicializaColectivo(comiteId).subscribe(
      {
        next: (response: ApiResponse) => {

          if (response && response.data) {

            this.colectivo = { id: comiteId, nombre: response.data.nombre, dependientes: [] }

            response.data.responsabilidades.forEach((el: { clave: string; valor: string | null }) => {
              const uiKey = ROLE_MAP_INV[el.clave];            // ← back ➜ front
              const resp = this.responsabilidades.find(r => r.clave === uiKey);
              if (resp) { resp.valor = el.valor; }
            });
            this.militantes = response.data.militantes;
            this.reunion.direccion = response.data.sede;
            this.calcularReunion(response.data.reunion);
          }
        },
        error: (error: any) => {
          console.error('Error en la solicitud:', error);
          this.toastr.error(error.error.message);
        }
      });
  }


  calcularReunion(reunion: any) {
    if (reunion.fechaReunionPasada === "") {
      this.fechaUltimaReunion = this.translate.instant("ui.colectivo.sinReuniones");
    } else {
      this.fechaUltimaReunion = reunion.fechaReunionPasada;
    }
    // Si reunion.datos es null, usar 14 días a partir de hoy
    let fechaProximaReunion = new Date();
    if (reunion.datos == null) {
      if (reunion.fechaReunionPasada === "") {
        fechaProximaReunion.setDate(fechaProximaReunion.getDate() + 14);
      } else {
        fechaProximaReunion = parse(reunion.fechaReunionPasada, 'dd/MM/yyyy HH:mm', new Date());
        fechaProximaReunion.setDate(fechaProximaReunion.getDate() + 14);
      }

      // Obtener la fecha actual al inicio del día para la comparación
      const hoy = new Date();

      // Verificar si fechaProximaReunion es menor que hoy
      if (isBefore(fechaProximaReunion, hoy)) {
        fechaProximaReunion = hoy;
      }

    } else {
      // Si reunion.datos no es null, usar fechaInicio y convertirla
      fechaProximaReunion = new Date(reunion.datos.fechaInicio);
      this.reunion.direccion = reunion.datos.direccion;
    }

    this.fechaProximaReunion = format(fechaProximaReunion, 'dd/MM/yyyy HH:mm');
    this.reunion = this.datosSiguienteReunion(reunion.datos);
    this.fechaProximaReunion = format(fechaProximaReunion, 'dd/MM/yyyy');

  }

  datosSiguienteReunion(datos: any): any {
    // Si datos es null, crear una reunión con los valores por defecto y poner reunionConvocada a false
    if (datos == null) {
      this.reunionConvocada = false;
      return { id: 0, fechaInicio: this.fechaProximaReunion, duracion: 2, direccion: this.reunion.direccion, premilitantes: true, ordenDelDia: '', invitados: [] };
    } else {
      this.reunionConvocada = true;
      return { id: datos.id, fechaInicio: datos.fechaInicio, duracion: datos.duracion, direccion: datos.direccion, premilitantes: true, ordenDelDia: datos.ordenDelDia, invitados: datos.invitados };
    }
  }



  muestraResponsabilidades(): any[] {

    let responsabilidades = this.transformarResp(this.responsabilidades);
    let responsabilidadesFiltradas = responsabilidades.filter(r => r.persona !== '');

    return this.obtieneNombreresponsables(responsabilidadesFiltradas);
  }

  transformarResp(responsabilidades: Responsabilidad[]): ResponsabilidadTraducida[] {

    const claveTraduccion: Record<ClaveResponsabilidad, string> = {
      POLITICO: 'ui.colectivo.responsabilidades.rp',
      ORGANIZACION: 'ui.colectivo.responsabilidades.ro',
      FINANZAS: 'ui.colectivo.responsabilidades.rfin',
      FORMACION: 'ui.colectivo.responsabilidades.rfor',
      AGIT: 'ui.colectivo.responsabilidades.ragitprop',
      MES: 'ui.colectivo.responsabilidades.rmes',
      MOS: 'ui.colectivo.responsabilidades.rmos',
      MUJ: 'ui.colectivo.responsabilidades.rmt',
      VECINAL: 'ui.colectivo.responsabilidades.rmv',
      FRENTE: 'ui.colectivo.responsabilidades.rfm',
    };
    let responsabilidadesTraducidas = responsabilidades.map(responsabilidad => {
      const nombreTraducido = this.translate.instant(claveTraduccion[responsabilidad.clave]);
      return { id: responsabilidad.clave, nombre: nombreTraducido, persona: responsabilidad.valor };
    });
    return responsabilidadesTraducidas;
  }

  // Método para abrir el modal
  abrirModalEditarResponsabilidades() {
    this.mostrarModalEditarResponsabilidades = true;
  }

  obtieneNombreresponsables(responsabilidades: ResponsabilidadTraducida[]): any[] {

    let nombresResponsables = responsabilidades.map(r => {
      let nombrePersona = r.persona === '' ? '' : this.militantes.find(p => p.id === r.persona)?.nombre;
      return { id: r.id, nombre: r.nombre, persona: nombrePersona };
    });
    return nombresResponsables;
  }

  public confirmarReunion(form: any): void {
    // Si this.reunionConvocada es false se llama a this.colectivoService.crearReunion si no se llama a this.colectivoService.actualizarReunion
    form.fecha = new Date(form.fecha).toISOString();
    //si el carnet tiene menos de 4 numeros, se completa con ceros a la izquierda hasta que tenga 4 números
    if (form.esMilitante && form.numeroCarnet.length < 4) {
      let carnetN = form.numeroCarnet;
      //Se añade el string 0 tantas veces como ceros faltantes
      carnetN = "0".repeat(4 - carnetN.length) + carnetN;

      form.numeroCarnet = carnetN;
    }

    if (!this.reunionConvocada) {
      this.colectivoService.crearReunion(this.comiteId, form).subscribe(
        {
          next: (response: ApiResponse) => {
            this.reunion = response.data.reunion.datos.puntos;
            this.reunion.direccion = response.data.reunion.datos.direccion;
            this.reunion.puntos = response.data.reunion.datos.puntos;
            this.reunion.fechaInicio = response.data.reunion.datos.fechaInicio;
            this.reunion.duracion = response.data.reunion.datos.duracionEnHoras;
            this.reunion.premilitantes = response.data.reunion.datos.aptaPremilitantes;
            this.reunion.invitados = response.data.reunion.datos.invitados;
            this.calcularReunion(response.data.reunion);

            this.mostrarEditarReuniones = false;
            this.toastr.success("Reunión convocada correctamente");
          },
          error: (error: any) => {
            console.error(error);
            this.toastr.error(error.error.message);
          }
        });
    } else {
      this.colectivoService.actualizarReunion(this.reunion.id, form).subscribe(
        {
          next: (response: ApiResponse) => {
            this.reunion.direccion = response.data.sede;
            this.calcularReunion(response.data.reunion);
            this.mostrarEditarReuniones = false;
            this.toastr.success("Reunión editada correctamente");
          },
          error: (error: any) => {
            console.error(error);
            this.toastr.error(error.error.message);
          }
        });
    }

  }

  public abrirModalReunion(): void {
    this.mostrarEditarReuniones = true;
  }

  public confirmarResponsabilidades(form: any): void {
    // 1 · sincroniza array local con los valores del modal
    this.responsabilidades.forEach(r => r.valor = form[r.clave]);

    // 2 · convierte a DTO backend
    const payload = this.responsabilidades.map(r => ({
      clave: ROLE_MAP[r.clave],         
      valor: r.valor ?? ''
    }));
    this.colectivoService.actualizarResponsabilidades(this.comiteId, payload).subscribe(
      {
        next: (response: ApiResponse) => {
          this.toastr.success(response.message);
          this.mostrarModalEditarResponsabilidades = false;
        },
        error: (error: any) => {
          console.error(error);
          this.toastr.error(error.error.message);
        }
      });
    this.mostrarModalEditarResponsabilidades = false;
  }
}
interface Responsabilidad {
  clave: ClaveResponsabilidad;
  valor: string | null;
}

type ClaveResponsabilidad = 'POLITICO' | 'ORGANIZACION' | 'FINANZAS' | 'FORMACION' | 'AGIT' | 'MES' | 'MOS' | 'MUJ' | 'VECINAL' | 'FRENTE';

export interface ResponsabilidadTraducida {
  nombre: string;
  persona: null | string;
  id: ClaveResponsabilidad;
}

export interface Responsable {
  id: string;
  nombre: string;
}
