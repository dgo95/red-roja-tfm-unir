import { AuthService } from './../shared/services/auth.service';
import { Component, NgZone, OnInit } from '@angular/core';
import { Militante } from 'src/app/models/militante';
import { AbstractControl, FormArray, FormBuilder, FormGroup, ValidationErrors, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { PerfilService } from 'src/app/shared/services/perfil.service';
import { ToastrService } from 'ngx-toastr';
import { Select, SelectWithPadreId } from 'src/app/models/select';
import { ApiResponse } from 'src/app/models/respuestaApi';
import { FormularioPerfil } from 'src/app/models/perfilForms';
import { SafeUrl } from '@angular/platform-browser';
import { format, differenceInYears, isToday, parseISO, isValid } from 'date-fns';
import { TranslateModule } from '@ngx-translate/core';
import { ModalRecorteComponent } from './modal-recorte/modal-recorte.component';
import { NgIf, NgFor, NgClass } from '@angular/common';
import { MatTabsModule } from '@angular/material/tabs';
import { first } from 'rxjs';


@Component({
    selector: 'app-perfil',
    templateUrl: './perfil.component.html',
    styleUrls: ['./perfil.component.scss'],
    standalone: true,
    imports: [MatTabsModule, FormsModule, ReactiveFormsModule, NgIf, NgFor, NgClass, ModalRecorteComponent, TranslateModule]
})
export class PerfilComponent implements OnInit {
  m: Militante = new Militante();
  //---- Editor Imagenes ----
  public imageChangedEvent: any = '';
  public croppedImage: any = '';
  public mostrarModal: boolean = false;
  //---- Notas flotantes ----
  mensaje: string | null = null;
  esError = false;
  //----                 ----
  comunidadesAutonomas: Select[] = [];
  provincias: Select[] = [];
  municipios: Select[] = [];
  actividadesEconomicas: Select[] = [];
  tiposContratos: Select[] = [];
  nivelesEstudio: Select[] = [];
  modalidadesTrabajo: Select[] = [];
  subNivelEstudios: SelectWithPadreId[] = [];
  subsubNivelEstudios: SelectWithPadreId[] = [];
  tiposDeEstudio: Select[] = [];
  subtiposDeEstudio: Select[] = [];
  sindicatos: Select[] = [];
  federaciones: Select[] = [];
  fotoPerfil!: SafeUrl;
  datosBasicos: FormGroup = this.fb.group({
    email: '',
    telefono: '',
    esTrabajador: false,
    esEstudiante: false,
    sindicado: false,
    comunidadAutonoma: 0,
    provincia: 0,
    municipio: 0,
    direccion: '',
    habilidades: this.fb.array([]),
    idiomas: this.fb.array([]),
  });
  listaHabilidades: any[] = [];
  habilidadesDisponibles: any[][] = [];
  listaIdiomas: any[] = [];
  idiomasDisponibles: any[][] = [];
  datosTrabajo: FormGroup = this.fb.group({
    nombreEmpresa: '',
    numeroTrabajadores: '',
    direccionCentroTrabajo: '',
    numeroTrabajadoresCentroTrabajo: '',
    situacionLaboral: false,
    modalidadTrabajo: 0,
    existeOrganoRepresentacion: 0,
    participaOrganoRepresentacion: false,
    fechaInicioContrato: '',
    actividadEconomica: 0,
    tipoContrato: 0,
    profesion: '',
    nombreCentroTrabajo: '',
  });
  // Inicialización del formulario de Datos de Estudio
  datosEstudio: FormGroup = this.fb.group({
    nombreEstudios: '',
    tipoEstudio: 0,
    subtiposDeEstudio: 0,
    nivelEstudio: 0,
    nombreCentroEducativo: '',
    sindicatoEstudiantil: false,
    anhoFinalizacion: new Date().getFullYear(),
  });
  datosSindicacion: FormGroup = this.fb.group({
    sindicato: '',
    federacion: '',
    participaAreaJuventud: false,
    cargo: '',
    sindicatoOtros: '',
    federacionOtros: ''
  });

  // Patrones de validación
  emailPattern = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$";
  telefonoPattern = "^(\\+?\\d{1,3}\\ ?)?\\d{9,15}$";
  numeroCarnetPattern = "^(\\d{1,3})?\\d$";
  numerosPattern = "^\\d*$";

  nivelesIdioma: string[] = ['Nativo','A1', 'A2', 'B1', 'B2', 'C1', 'C2'];

  notZeroValidator(control: AbstractControl): ValidationErrors | null {
    return control.value === 0 ? { 'zeroValue': true } : null;
  }


  // Validador personalizado
  tipoEstudioValidador(control: AbstractControl): { [key: string]: boolean } | null {
    const nivelEstudio = control.get('nivelEstudio')?.value;
    const tipoEstudio = control.get('tipoEstudio')?.value;

    if ((+nivelEstudio === 1 || +nivelEstudio === 2) && tipoEstudio <= 0) {
      return { 'tipoEstudioInvalido': true };
    }
    return null;
  }
  // Segundo validador personalizado
  subtipoValidador(control: AbstractControl): { [key: string]: boolean } | null {
    const nivelEstudio = control.get('nivelEstudio')?.value;
    const subtiposDeEstudio = control.get('subtiposDeEstudio')?.value;

    if (+nivelEstudio === 2 && +subtiposDeEstudio === 0) {
      return { 'subtipoInvalido': true };
    }
    return null;
  }

  constructor(private toastr: ToastrService, private fb: FormBuilder, private authService: AuthService, private perfilService: PerfilService, private ngZone: NgZone) { }

  ngOnInit(): void {

    this.datosBasicos = this.fb.group({
      email: ['', [Validators.required, Validators.pattern(this.emailPattern)]],
      telefono: ['', [Validators.required, Validators.pattern(this.telefonoPattern)]],
      esTrabajador: [false],
      esEstudiante: [false],
      comunidadAutonoma: 0,
      provincia: 0,
      municipio: 0,
      direccion: [''],
      sindicado: false,
      habilidades: this.fb.array([]),
      idiomas: this.fb.array([]),
    });

    this.datosSindicacion = this.fb.group({
      sindicato: [0, [this.notZeroValidator]],
      federacion: 0,
      participaAreaJuventud: false,
      cargo: '',
      sindicatoOtros: '',
      federacionOtros: ''
    });


    this.datosEstudio = this.fb.group({
      nombreEstudios: ['', [Validators.required]],
      tipoEstudio: 0,
      subtiposDeEstudio: 0,
      nivelEstudio: [0, [Validators.min(1)]],
      nombreCentroEducativo: ['', [Validators.required]],
      sindicatoEstudiantil: false,
      anhoFinalizacion: new Date().getFullYear(),
    }, { validators: [this.tipoEstudioValidador, this.subtipoValidador] });


    this.datosTrabajo = this.fb.group({
      actividadEconomica: [0, [Validators.min(1)]],
      tipoContrato: [0, [Validators.min(1)]],
      modalidadTrabajo: [0, [Validators.min(1)]],
      profesion: ['', [Validators.required]],
      existeOrganoRepresentacion: [0, [Validators.min(1)]],
      participaOrganoRepresentacion: false,
      nombreCentroTrabajo: '',
      numeroTrabajadoresCentroTrabajo: 0,
      direccionCentroTrabajo: '',
      nombreEmpresa: ['', [Validators.required]],
      numeroTrabajadores: [null, [Validators.required]],
      fechaInicioContrato: ['', [Validators.required]],
    });
    this.datosTrabajo.get('modalidadTrabajo')?.valueChanges.subscribe(value => {
      const nombreCentroTrabajoControl = this.datosTrabajo.get('nombreCentroTrabajo');
      const numeroTrabajadoresCentroTrabajo = this.datosTrabajo.get('numeroTrabajadoresCentroTrabajo');
      const direccionCentroTrabajo = this.datosTrabajo.get('direccionCentroTrabajo');


      if (value != '2') {
        nombreCentroTrabajoControl?.setValidators([Validators.required]);
        numeroTrabajadoresCentroTrabajo?.setValidators([Validators.required]);
        direccionCentroTrabajo?.setValidators([Validators.required]);
      } else {
        nombreCentroTrabajoControl?.setValidators(null);
        numeroTrabajadoresCentroTrabajo?.setValidators(null);
        direccionCentroTrabajo?.setValidators(null);
      }

      // Es importante llamar a updateValueAndValidity() para que los cambios en los validadores se reflejen
      nombreCentroTrabajoControl?.updateValueAndValidity();
      numeroTrabajadoresCentroTrabajo?.updateValueAndValidity();
      direccionCentroTrabajo?.updateValueAndValidity();

    });

    this.perfilService.getInicializaPerfil(this.m.militanteId).subscribe(
      {
        next: (responsabilidades: ApiResponse) => {
          const datos = responsabilidades.data;
          //se asegura de que data es de tipo FormularioPerfil y lo guarda en una varieble de ese tipo
          const data: FormularioPerfil = datos as FormularioPerfil;
          this.listaHabilidades = datos.habilidades;
          this.habilidadesDisponibles = [...this.listaHabilidades];

          this.listaIdiomas = datos.idiomas;
          //Ordena this.listaIdiomas alfabéticamente segun el nombre de cada elemento
          this.listaIdiomas.sort((a, b) => (a.nombre > b.nombre) ? 1 : -1);
          this.idiomasDisponibles = [...this.listaIdiomas];
          
          // Inicializar idiomas
          if (datos.mapaIdiomas) {
            Object.entries(datos.mapaIdiomas).forEach(([id, nivelIdioma]) => {
              const nivel = String(nivelIdioma).toLowerCase().replace(/^\w/, c => c.toUpperCase());
              const idiomaForm = this.fb.group({
                idioma: [id, Validators.required],
                nivel: [nivel, Validators.required]
              });
              this.idiomas.push(idiomaForm);
            });
            
            this.actualizarIdiomasDisponibles();
          }

          // Inicializar habilidades
          if (datos.mapaHabilidades) {
            Object.entries(datos.mapaHabilidades).forEach(([id, descripcion]) => {
              const habilidadForm = this.fb.group({
                habilidad: [id, Validators.required],
                descripcion: [descripcion, Validators.required]
              });
              this.habilidades.push(habilidadForm);
            });
            this.actualizarHabilidadesDisponibles();
          }


          this.comunidadesAutonomas = data.comunidades;
          this.provincias = data.provincias;
          this.municipios = data.municipios;
          this.actividadesEconomicas = data.actividadesEconomicas;
          this.tiposContratos = data.tiposContratos;
          this.nivelesEstudio = data.nivelesEducativos;
          this.subNivelEstudios = data.subdivisiones;
          this.subsubNivelEstudios = data.subsubdivisiones;
          this.actividadesEconomicas = data.actividadesEconomicas;
          this.modalidadesTrabajo = data.modalidadesTrabajo;
          this.sindicatos = data.sindicatos;
          this.federaciones = data.federaciones;

          this.obtenerTiposDeEstudio(data.nivelEducativo);
          this.obtenerSubTiposDeEstudio(data.subdivision);
          this.datosBasicos.patchValue({
            email: data.email,
            telefono: data.telefono,
            direccion: data.direccion,
            comunidadAutonoma: data.comunidadAutonoma,
            provincia: data.provincia,
            municipio: data.municipio,
            esTrabajador: data.trabajador,
            esEstudiante: data.estudiante,
            sindicado: data.sindicado,
          });

          this.datosEstudio.patchValue({
            nivelEstudio: data.nivelEducativo === null ? 0 : data.nivelEducativo,
            tipoEstudio: data.nivelEducativo === null ? 0 : data.subdivision,
            subtiposDeEstudio: data.nivelEducativo === null ? 0 : data.subsubdivision,
            nombreCentroEducativo: data.nombreCentroEducativo,
            nombreEstudios: data.nombreEstudios,
            anhoFinalizacion: data.anhoFinalizacion === null ? new Date().getFullYear() : data.anhoFinalizacion,
            sindicatoEstudiantil: data.sindicatoEstudiantil,
          });


          this.datosTrabajo.patchValue({
            actividadEconomica: data.actividadEconomica == null ? 0 : data.actividadEconomica,
            tipoContrato: data.tipoContrato == null ? 0 : data.tipoContrato,
            modalidadTrabajo: data.modalidadTrabajo == null ? 0 : data.modalidadTrabajo,
            participaOrganoRepresentacion: data.participaOrganoRepresentacion == null ? false : data.participaOrganoRepresentacion,
            existeOrganoRepresentacion: data.existeOrganoRepresentacionTrabajadores == null ? "DESCONOCIDO" : data.existeOrganoRepresentacionTrabajadores,
            direccionCentroTrabajo: data.direccionCentroTrabajo == null ? "" : data.direccionCentroTrabajo,
            nombreCentroTrabajo: data.nombreCentroTrabajo == null ? "" : data.nombreCentroTrabajo,
            numeroTrabajadoresCentroTrabajo: data.numeroTrabajadoresCentroTrabajo == null ? 0 : data.numeroTrabajadoresCentroTrabajo,
            nombreEmpresa: data.nombreEmpresa == null ? "" : data.nombreEmpresa,
            numeroTrabajadores: data.numeroTrabajadores == null ? 0 : data.numeroTrabajadores,
            fechaInicioContrato: data.fechaInicioContrato == null ? "" : data.fechaInicioContrato,
            profesion: data.profesion == null ? "" : data.profesion,
          });
          this.datosTrabajo.get('modalidadTrabajo')?.updateValueAndValidity();

          this.datosSindicacion.patchValue({
            sindicato: data.sindicato == null ? 0 : data.sindicato,
            federacion: data.federacion == null ? 0 : data.federacion,
            participaAreaJuventud: data.participaAreaJuventud == null ? false : data.participaAreaJuventud,
            cargo: data.cargo == null ? "" : data.cargo,
            sindicatoOtros: data.sindicatoOtros == null ? "" : data.sindicatoOtros,
            federacionOtros: data.federacionOtros == null ? "" : data.federacionOtros,
          });

          this.habilitaParticipa(data.existeOrganoRepresentacionTrabajadores);
        },
        error: (error: any) => {
          console.error(error);
        }
      });

    this.cargarImagenPerfil(this.m.militanteId, this.m.sexo);
  }
  get habilidades(): FormArray {
    return this.datosBasicos.get('habilidades') as FormArray;
  }
  get idiomas(): FormArray {
    return this.datosBasicos.get('idiomas') as FormArray;
  }
  agregarIdioma(): void {
  if (Array.isArray(this.habilidadesDisponibles) && this.habilidadesDisponibles.length > 0) {
      const ultimaPosicion = this.habilidadesDisponibles[this.habilidadesDisponibles.length - 1];
      if (Array.isArray(ultimaPosicion) && ultimaPosicion.length <= 172) {
        return;
      }
    }
    const idiomaForm = this.fb.group({
      idioma: [0, Validators.required],
      nivel: [0, Validators.required]
    });
    this.idiomas.push(idiomaForm);
    this.actualizarIdiomasDisponibles();
  }
  agregarHabilidad(): void {
    // Verificar si habilidadesDisponibles es un array de arrays y su última posición está vacía
    if (Array.isArray(this.habilidadesDisponibles) && this.habilidadesDisponibles.length > 0) {
      const ultimaPosicion = this.habilidadesDisponibles[this.habilidadesDisponibles.length - 1];
      if (Array.isArray(ultimaPosicion) && ultimaPosicion.length === 0) {
        return;
      }
    } 
    const habilidadForm = this.fb.group({
      habilidad: [0, Validators.required],
      descripcion: ['', Validators.required]
    });
    habilidadForm.get('habilidad')?.valueChanges.subscribe(() => {
      this.actualizarHabilidadesDisponibles();
    });
    this.habilidades.push(habilidadForm);
    this.actualizarHabilidadesDisponibles();
  }
  eliminarIdioma(index: number): void {
    this.idiomas.removeAt(index);
    this.actualizarIdiomasDisponibles();
  }

  eliminarHabilidad(index: number): void {
    this.habilidades.removeAt(index);
    this.actualizarHabilidadesDisponibles();
  }
  actualizarIdiomasDisponibles(): void {
    this.idiomasDisponibles = [];
    let idiomasSeleccionados: any[] = [];

    this.idiomas.controls.forEach((control, index) => {
      const seleccionado = control.get('idioma')?.value;
      const disponibles = this.listaIdiomas.filter(idioma => !idiomasSeleccionados.includes('' + idioma.id));

      this.idiomasDisponibles.push(disponibles);
      if (seleccionado) {
        idiomasSeleccionados.push(seleccionado);
      }
      // Eliminar el elemento seleccionado de las listas anteriores en idiomasDisponibles
      for (let i = 0; i < index; i++) {
        this.idiomasDisponibles[i] = this.idiomasDisponibles[i].filter(idioma => '' + idioma.id !== '' + seleccionado);
      }
    }

    );
    // Añadir una lista de idiomas disponibles para el próximo `select` si corresponde
    const ultimaSeleccion = this.idiomas.controls[this.idiomas.length - 1]?.get('idioma')?.value;
    if (ultimaSeleccion) {
      const disponibles = this.listaIdiomas.filter(idioma => !idiomasSeleccionados.includes('' + idioma.id));
      this.idiomasDisponibles.push(disponibles);
    }
  }

  actualizarHabilidadesDisponibles(): void {
    this.habilidadesDisponibles = [];
    let habilidadesSeleccionadas: any[] = [];

    this.habilidades.controls.forEach((control, index) => {
      const seleccionada = control.get('habilidad')?.value;
      const disponibles = this.listaHabilidades.filter(habilidad => !habilidadesSeleccionadas.includes('' + habilidad.id));

      this.habilidadesDisponibles.push(disponibles);
      if (seleccionada) {
        habilidadesSeleccionadas.push(seleccionada);
      }
      // Eliminar el elemento seleccionado de las listas anteriores en habilidadesDisponibles
      for (let i = 0; i < index; i++) {
        this.habilidadesDisponibles[i] = this.habilidadesDisponibles[i].filter(habilidad => '' + habilidad.id !== '' + seleccionada);
      }
    });

    // Añadir una lista de habilidades disponibles para el próximo `select` si corresponde
    const ultimaSeleccion = this.habilidades.controls[this.habilidades.length - 1]?.get('habilidad')?.value;
    if (ultimaSeleccion) {
      const disponibles = this.listaHabilidades.filter(habilidad => !habilidadesSeleccionadas.includes('' + habilidad.id));
      this.habilidadesDisponibles.push(disponibles);
    }
  }
  todosSelectsSeleccionados(): boolean {
    return this.habilidades.controls.every(control => control.get('habilidad')?.value);
  }
  
  getFechaNacimiento(): string {
    const fechaISO = this.m?.fechaNacimiento;
    if (!fechaISO) {
      return '—';
    }
  
    const fecha = parseISO(fechaISO);
    if (!isValid(fecha)) {
      console.warn('Fecha de nacimiento inválida:', fechaISO);
      return '—';
    }
  
    const fechaFmt = format(fecha, 'dd/MM/yyyy');
    const edad      = differenceInYears(new Date(), fecha);
    const cumpleHoy = isToday(fecha) ? ' 🎂' : '';
  
    return `${fechaFmt}${cumpleHoy} (${edad} años)`;
  }

  calculaEdad(fechaNacimiento: Date): number {
    return differenceInYears(new Date(), fechaNacimiento);
  }
  cargarImagenPerfil(usuarioId: string, sexo: string) {
    this.perfilService.getUrlFotoPerfil(usuarioId, sexo).subscribe((url: SafeUrl) => {
      this.fotoPerfil = url;
    });
  }

  habilitaParticipa(a: string): void {

    if (a === 'SI') {
      this.datosTrabajo.get('participaOrganoRepresentacion')?.enable();
    } else {
      this.datosTrabajo.get('participaOrganoRepresentacion')?.setValue(false);
      this.datosTrabajo.get('participaOrganoRepresentacion')?.disable();
    }
  }

  onSubmitTrabajo() {
    const id = this.m.militanteId;
    const datos = this.datosTrabajo.getRawValue();
    this.perfilService.actualizarDatosTrabajo(id, datos)
      .subscribe(
        {
          next: () => {
            this.toastr.success('Los datos de trabajo se han actualizado correctamente.', 'Guardado con éxito');
            this.datosTrabajo.markAsPristine();
            if (this.datosBasicos.touched) {
              this.onSubmitBasico();
            }
          },
          error: (error: any) => {
            console.error(error);
            this.toastr.error('Ha ocurrido un error al actualizar los datos.', 'Error');
          }
        });
  }

  onSubmitEstudio() {
    const id = this.m.militanteId;
    const datos = this.datosEstudio.getRawValue();
    this.perfilService.actualizarDatosEstudio(id, datos)
      .subscribe(
        {
          next: (respuesta: any) => {
            // this.toastr.success(respuesta.message, 'Guardado con éxito');
            this.toastr.success('Los datos del estudio se han actualizado correctamente.', 'Guardado con éxito');
            this.datosEstudio.markAsPristine();
            if (this.datosBasicos.touched) {
              this.onSubmitBasico();
            }
          },
          error: (error: any) => {
            console.error(error);
            this.toastr.error('Ha ocurrido un error al actualizar los datos.', 'Error');
          }
        });

  }

  onSubmitBasico() {
    let id: string | null = null;
    this.authService.user$.pipe(first()).subscribe(u => id = u?.militanteId ?? null);
    if (id === null) { throw new Error('…'); }
        // Se asegura de que el id no sea null, si lo es, se lanza un error
    if (id === null) {
      throw new Error('El ID del militante no puede ser nulo');
    }
    const h = this.datosBasicos.get('habilidades') as FormArray;
    //Crea mapa de habilidades, donde la clave es el id de la habilidad y el valor es la descripción
    const mapaHabilidades = h.getRawValue().reduce((acc: any, habilidad: any) => {
      if (habilidad.habilidad && habilidad.habilidad.trim() !== '') {
        acc[habilidad.habilidad] = habilidad.descripcion;
      }
      return acc;
    }, {});

    const i = this.datosBasicos.get('idiomas') as FormArray;
    //Crea mapa de idiomas, donde la clave es el id del idioma y el valor es el nivel
    const mapaIdiomas = i.getRawValue().reduce((acc: any, idioma: any) => {
      if (idioma.idioma && idioma.idioma.trim() !== '') {
        acc[idioma.idioma] = idioma.nivel.toUpperCase();
      }
      return acc;
    }, {});
    const datos = {
      email: this.datosBasicos.value.email,
      telefono: this.datosBasicos.value.telefono,
      esTrabajador: this.datosBasicos.value.esTrabajador,
      esEstudiante: this.datosBasicos.value.esEstudiante,
      municipio: this.datosBasicos.value.municipio,
      direccion: this.datosBasicos.value.direccion,
      sindicado: this.datosBasicos.value.sindicado,
      habilidades: mapaHabilidades,
      idiomas: mapaIdiomas,
    }; // Crear objeto con los valores del formulario
    if (this.datosBasicos.value.esEstudiante == true && !this.datosEstudio.valid) {
      this.toastr.error('Por favor, completa la pestaña Datos estudio.', 'Error');
      return;
    }
    if (this.datosBasicos.value.esTrabajador == true && !this.datosTrabajo.valid) {
      this.toastr.error('Por favor, completa la pestaña Datos trabajo.', 'Error');
      return;
    }
    if (this.datosBasicos.value.sindicado == true && !this.datosSindicacion.valid) {
      this.toastr.error('Por favor, completa la pestaña Datos sindicación.', 'Error');
      return;
    }
    this.perfilService.actualizarDatosBasicos(id, datos)
      .subscribe(
        {
          next: () => {
            // Actualizar los datos del usuario en el servicio de autenticación
            this.m.email = datos.email;
            //TODO this.authService.setMilitanteData(this.m);
            this.toastr.success('Los datos básicos se han actualizado correctamente.', 'Guardado con éxito');
            var a = this.datosBasicos.value.esTrabajador;
            if (this.datosEstudio.value.esEstudiante === false) {
              this.datosEstudio.patchValue({
                nivelEstudio: 0,
                tipoEstudio: 0,
                subtiposDeEstudio: 0,
                nombreCentroEducativo: null,
                nombreEstudios: null,
                anhoFinalizacion: new Date().getFullYear(),
                sindicatoEstudiantil: false,
              });
            }
            this.datosBasicos.markAsPristine();
          },
          error: (error: any) => {
            console.error(error);
          }
        });

  }

  onSubmitSindicacion() {
    const id = this.m.militanteId; // Obtener el ID del perfil actual
    const datos = this.datosSindicacion.getRawValue();
    this.perfilService.actualizarDatosSindicacion(id, datos)
      .subscribe(
        {
          next: () => {
            this.toastr.success('Los datos de sindicación se han actualizado correctamente.', 'Guardado con éxito');
            this.datosSindicacion.markAsPristine();
            if (this.datosBasicos.touched) {
              this.onSubmitBasico();
            }
          },
          error: (error: any) => {
            console.error(error);
            this.toastr.error('Ha ocurrido un error al actualizar los datos.', 'Error');
          }
        });

  }

  cerrarMensaje() {
    this.mensaje = null;
    this.esError = false;
  }

  cambiaProvincia(idNuevaProvincia: number) {
    this.datosBasicos.patchValue({
      municipio: 0
    });
    this.municipios = [];
    this.perfilService.getMunicipios(idNuevaProvincia).subscribe(
      {
        next: (data: ApiResponse) => {
          this.municipios = data.data;
        },
        error: (error: any) => {
          console.error(error);
        }
      });
  }

  cambiaComunidad(idNuevaComunidad: number) {
    this.datosBasicos.patchValue({
      provincia: 0,
      municipio: 0
    });
    this.provincias = [];
    this.municipios = [];
    this.perfilService.getProvincias(idNuevaComunidad).subscribe(
      {
        next: (data: ApiResponse) => {
          this.provincias = data.data;
        },
        error: (error: any) => {
          console.error(error);
        }
      });
  }

  obtenerTiposDeEstudio(nivelEstudio: number, change: boolean = false): void {
    this.tiposDeEstudio = this.subNivelEstudios.filter((subNivel: SelectWithPadreId) => subNivel.padreId === +nivelEstudio).map(child => ({
      id: child.id,
      nombre: child.nombre
    }));
    if (change) {
      this.datosEstudio.patchValue({
        tipoEstudio: 0,
        subtiposDeEstudio: 0,
      });
      //actualiza this.datosEstudio para que revalue los validadores
      this.datosEstudio.updateValueAndValidity();
    }
  }

  obtenerFederaciones(sindicato: number): void {
    this.federaciones = [];
    if (sindicato == -1) {
      this.datosSindicacion.get('federacion')?.setValue(-1);
      this.datosSindicacion.get('federacion')?.disable();
      this.datosSindicacion.get('sindicatoOtros')?.setValidators([Validators.required]);
      this.datosSindicacion.get('sindicatoOtros')?.updateValueAndValidity();
      return;
    }
    this.datosSindicacion.get('sindicatoOtros')?.setValidators(null);
    this.datosSindicacion.get('sindicatoOtros')?.updateValueAndValidity();
    this.datosSindicacion.get('federacion')?.enable();
    this.datosSindicacion.patchValue({
      federacion: 0,
    });
    this.perfilService.getFederaciones(sindicato).subscribe(
      {
        next: (data: ApiResponse) => {
          this.federaciones = data.data;
        },
        error: (error: any) => {
          console.error(error);
        }
      });
  }

  obtenerSubTiposDeEstudio(id: number, change: boolean = false): void {
    this.subtiposDeEstudio = this.subsubNivelEstudios.filter((subNivel: SelectWithPadreId) => subNivel.padreId === +id);
    if (change)
      this.datosEstudio.patchValue({
        subtiposDeEstudio: 0,
      });
  }
  getFutureYears(): number[] {
    const currentYear = new Date().getFullYear();
    return Array.from({ length: 11 }, (_, i) => currentYear + i);
  }
  fileChangeEvent(event: any): void {
    this.imageChangedEvent = event;
  }

  public onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input && input.files && input.files.length > 0) {
      const file = input.files[0];
      this.imageChangedEvent = event;
      this.mostrarModal = true;
    }
  }
  public cerrarModal(): void {
    this.mostrarModal = false;
  }

  public confirmar(imagenRecortada: any): void {
    const id = this.m.militanteId; // Obtener el ID del perfil actual
    const formData = new FormData();
    const imageBlob = imagenRecortada;
    formData.append('imagen', imageBlob);

    this.perfilService.subirImagen(formData, id).subscribe(
      {
        next: (data: any) => {
          this.toastr.success('La imagen se ha actualizado correctamente.', 'Guardado con éxito');
          this.cerrarModal();
          this.cargarImagenPerfil(id, this.m.sexo);
        },
        error: (error: any) => {
          console.error(error);
          this.toastr.error('Ha ocurrido un error al actualizar la imagen.', 'Error');
        }
      });
  }
}

