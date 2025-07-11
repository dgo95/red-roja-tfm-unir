import { Component, OnInit, ViewChild, Input } from '@angular/core';
import { MatPaginator, PageEvent, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { format } from 'date-fns';
import { MilitanteService } from './../services/militante.service';
import { ColectivoService } from './../services/colectivo.service';
import { ToastrService } from 'ngx-toastr';
import { Comite } from 'src/app/models/colectivo';
import { CrearEditarContactoComponent } from './crear-editar-contacto/crear-editar-contacto.component';
import { CrearEditarMilitanteComponent } from './crear-editar-militante/crear-editar-militante.component';
import { ConcederMilitanciaComponent } from './conceder-militancia/conceder-militancia.component';
import { AjustesAvanzadosComponent } from './ajustes-avanzados/ajustes-avanzados.component';
import { FichaMovilidadComponent } from './ficha-movilidad/ficha-movilidad.component';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatButtonModule } from '@angular/material/button';
import { MatOptionModule } from '@angular/material/core';

import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';

interface CensoData {
  id: string;
  premilitante: boolean;  // Mantén el campo id aquí para referencia interna
  fichaMovilidad: boolean;
  [key: string]: any;
}
@Component({
    selector: 'app-censo',
    templateUrl: './censo.component.html',
    styleUrls: ['./censo.component.scss'],
    standalone: true,
    imports: [
    MatFormFieldModule,
    MatSelectModule,
    MatOptionModule,
    MatTableModule,
    MatSortModule,
    MatButtonModule,
    MatMenuModule,
    MatIconModule,
    MatPaginatorModule,
    FichaMovilidadComponent,
    AjustesAvanzadosComponent,
    ConcederMilitanciaComponent,
    CrearEditarMilitanteComponent,
    CrearEditarContactoComponent
],
})
export class CensoComponent implements OnInit {
  dataSource!: MatTableDataSource<CensoData>;
  displayedColumns: string[] = [];

  @Input() esComite: boolean = false;
  @Input() comite!: Comite;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  tiposCenso: string[] = ['GENERAL', 'MOS', 'MES'];
  tipoCenso: string = this.tiposCenso[0];

  generandoExcel: boolean = false;

  openCrearEditarMilitante: boolean = false;
  openFichaMovilidad: boolean = false;
  openAjustesAvanzados: boolean = false;
  openConcederMilitancia: boolean = false;
  openCrearEditarContacto: boolean = false;

  militanteIdEditar: string | null = null;
  militanteIdMovilidad: string | null = null;
  militanteIdAjustes: string | null = null;
  militanteIdConcederMilitancia: string | null = null;
  contactoIdEditar: number | null = null;


  constructor(private colectivoService: ColectivoService,
    private militanteService: MilitanteService,
    private toastr: ToastrService) { }

  ngOnInit(): void {
    this.tipoCenso = this.tiposCenso[0];
    this.cargarDatosCenso(this.tipoCenso, 0, 5);
  }

  openCrearEditarMilitanteModal(element: any) {
    if (element) {
      this.militanteIdEditar = element.id;
    }
    this.openCrearEditarMilitante = true;
  }

  openCrearEditarContactoModal(element: any) {
    if (element) {
      this.contactoIdEditar = element.id;
    }
    this.openCrearEditarContacto = true;
  }

  crearEditarMilitante(form: any) {
    this.openCrearEditarMilitante = false;
    const pageIndex = this.paginator.pageIndex;
    const pageSize = this.paginator.pageSize;
    this.cargarDatosCenso(this.tipoCenso, pageIndex, pageSize);
  }
  crearEditarContacto(form: any) {
    //TODO: Implementar
  }

  confirmarEliminacionMilitante(element: any) {

    if (confirm("¿Está seguro de eliminar al militante " + element.nombre + " " + element.apellido + "?")) {
      this.militanteService.eliminarMilitante(element.id,this.comite.id).subscribe({
        next: (res) => {
          this.toastr.success('Militante eliminado correctamente', 'Éxito');
          const pageIndex = this.paginator.pageIndex;
          const pageSize = this.paginator.pageSize;
          this.cargarDatosCenso(this.tipoCenso, pageIndex, pageSize);
        },
        error: (error) => {
          console.error(error);
          this.toastr.error('Ocurrió un error al eliminar el militante', 'Error');
        }
      });
    }
  }

  concederMilitancia(element: any) {
    this.openConcederMilitancia = true;
    this.militanteIdConcederMilitancia = element.id;
  }

  movilidadMilitante(element: any) {
    this.militanteIdMovilidad = element.id;
    this.openFichaMovilidad = true;
  }

  ajustesAvanzadosMilitante(element: any) {
    this.openAjustesAvanzados = true;
    this.militanteIdAjustes = element.id;
  }

  cargarDatosCenso(tipo: string, pageIndex: number, pageSize: number) {
    if (!this.comite) {
      return;
    }
    this.colectivoService.getDatosCenso(tipo, this.esComite, this.comite.id, pageIndex, pageSize).subscribe((res: any) => {
      const pre = res.data.content as CensoData[];
      const data = pre.map((element) => {
        const obj: any = { id: element.id, premilitante: element.premilitante, fichaMovilidad: element.fichaMovilidad };  // Mantén el id en el objeto de datos
        Object.keys(element).forEach((key) => {
          if (key !== 'id' && key !== 'habilidades' && key !== 'premilitante' && key !== 'fichaMovilidad') {
            obj[key] = element[key];
          } else if (key === 'habilidades') {
            const habilidades = element[key] as { [key: string]: string };
            Object.keys(habilidades).forEach((habilidad) => {
              obj[habilidad] = habilidades[habilidad];
            });
          }
        });
        return obj;
      });
      if (data.length > 0) {
        this.displayedColumns = Object.keys(data[0]).filter(column => column !== 'id' && column !== 'premilitante' && column !== 'fichaMovilidad');
      }
      this.dataSource = new MatTableDataSource(data);
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
      this.paginator.length = res.totalElements;
    });
  }

  cambiarTipoCenso(tipo: string) {
    this.cargarDatosCenso(tipo, 0, 5);
  }

  descargarExcel() {
    if (!this.comite) {
      return;
    }
    this.generandoExcel = true;
    this.colectivoService.descargarExcel(this.tipoCenso, this.esComite, this.comite.id).subscribe((response) => {
      this.generandoExcel = false;
      const url = window.URL.createObjectURL(response);
      const a = document.createElement('a');
      a.href = url;
      a.download = `${this.getNombreCenso(this.tipoCenso)}.xlsx`;
      a.click();
      window.URL.revokeObjectURL(url);
    }, (error) => {
      console.error(error);
      this.generandoExcel = false;
    });
  }

  onPaginateChange(event: PageEvent) {
    this.cargarDatosCenso(this.tipoCenso, event.pageIndex, event.pageSize);
  }

  nombreColumna(columna: string) {
    return columna.replace(/([A-Z])/g, ' $1').replace(/^./, (str) => str.toUpperCase());
  }

  getNombreCenso(tipoCenso: string) {
    let nombre = 'Censo_';
    switch (tipoCenso) {
      case 'GENERAL':
        nombre += 'General';
        break;
      case 'MOS':
        nombre += 'Laboral_Sindical';
        break;
      case 'MES':
        nombre += 'MES';
        break;
    }
    nombre += '_' + format(new Date(), 'dd_MM_yyyy');
    return nombre;
  }

  promociona(){
    this.openConcederMilitancia=false;
    //Recarga el censo con los mismos parametros
    const pageIndex = this.paginator.pageIndex;
    const pageSize = this.paginator.pageSize;
    this.cargarDatosCenso(this.tipoCenso, pageIndex, pageSize);
  }
}