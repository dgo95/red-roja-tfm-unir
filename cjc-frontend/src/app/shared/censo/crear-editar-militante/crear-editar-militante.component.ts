import { Component, EventEmitter, Input, OnInit, Output, ViewChild, AfterViewInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { PerfilService } from '../../services/perfil.service';
import { ApiResponse } from 'src/app/models/respuestaApi';
import { MatStepper, MatStepperModule } from '@angular/material/stepper';
import { Select, SelectWithPadreId } from 'src/app/models/select';
import { FormularioPerfil } from 'src/app/models/perfilForms';
import { UserPreferencesService } from '../../services/user-preferences.service';
import { MilitanteService } from '../../services/militante.service';
import { ToastrService } from 'ngx-toastr';
import { TranslateModule } from '@ngx-translate/core';
import { NgIf, NgFor, NgClass } from '@angular/common';

@Component({
  selector: 'app-crear-editar-militante',
  templateUrl: './crear-editar-militante.component.html',
  styleUrls: ['./crear-editar-militante.component.scss'],
  standalone: true,
  imports: [MatStepperModule, NgIf, FormsModule, ReactiveFormsModule, NgFor, NgClass, TranslateModule]
})
export class CrearEditarMilitanteComponent implements OnInit {
  @Input() public militanteId: string | null = null;
  @Input() public comiteId!: number;
  @Output() public cerrar = new EventEmitter<void>();
  @Output() public confirmar = new EventEmitter<any>();

  @ViewChild('stepper') stepper!: MatStepper;

  tipoFormGroup: FormGroup;
  datosFormGroup: FormGroup;
  informacionBasicaFormGroup: FormGroup;
  trabajadorFormGroup: FormGroup;
  estudianteFormGroup: FormGroup;
  sindicadoFormGroup: FormGroup;
  cuotaFormGroup: FormGroup;
  esPremilitante: boolean = false;

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

  constructor(private fb: FormBuilder, private perfilService: PerfilService,
    private userPreferencesService: UserPreferencesService,
    private toastr: ToastrService,
    private militanteService: MilitanteService) {
    this.tipoFormGroup = this.fb.group({
      premilitante: [false, Validators.required]
    });
    this.datosFormGroup = this.fb.group({
      nombre: ['', Validators.required],
      primerApellido: ['', Validators.required],
      segundoApellido: [''],
      fechaNacimiento: ['', Validators.required],
      sexo: ['', Validators.required],
      numeroCarnet: ['']
    });
    this.informacionBasicaFormGroup = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      telefono: ['', Validators.required],
      municipio: ['', Validators.required],
      provincia: ['', Validators.required],
      comunidadAutonoma: ['', Validators.required],
      direccion: [''],
      sindicado: [false],
      estudiando: [false],
      trabajando: [false]
    });
    this.trabajadorFormGroup = this.fb.group({
      actividadEconomica: ['0', Validators.required],
      tipoContrato: ['0', Validators.required],
      modalidadTrabajo: ['0', Validators.required],
      existeOrganoRepresentacion: ['0', Validators.required],
      participaOrganoRepresentacion: [false],
      profesion: ['', Validators.required],
      nombreEmpresa: ['', Validators.required],
      numeroTrabajadores: [0, Validators.required],
      nombreCentroTrabajo: ['', Validators.required],
      numeroTrabajadoresCentroTrabajo: [0, Validators.required],
      direccionCentroTrabajo: ['', Validators.required],
      fechaInicioContrato: ['', Validators.required],
    });

    this.estudianteFormGroup = this.fb.group({
      tipoEstudio: [0, Validators.required],
      subtipoEstudio: [0, Validators.required],
      nombreCentroEducativo: ['', Validators.required],
      nivelEstudio: [0, Validators.required],
      nombreEstudios: ['', Validators.required],
      anhoFinalizacion: ['', Validators.required],
      sindicatoEstudiantil: [false]
    });

    this.sindicadoFormGroup = this.fb.group({
      sindicato: [0, [this.notZeroValidator]],
      federacion: 0,
      participaAreaJuventud: false,
      cargo: '',
      sindicatoOtros: '',
      federacionOtros: ''

    });

    this.cuotaFormGroup = this.fb.group({
      cuota: [5],
      activar: [true]
    });
  }

  ngOnInit(): void {
    if (this.militanteId) {
      this.perfilService.getInicializaPerfil(this.militanteId).subscribe({
        next: (responsabilidades: ApiResponse) => {
          const datos = responsabilidades.data;

          this.seleccionarTipo(datos.premilitante, this.stepper);

          this.comunidadesAutonomas = datos.comunidades;
          this.provincias = datos.provincias;
          this.municipios = datos.municipios;
          this.actividadesEconomicas = datos.actividadesEconomicas;
          this.tiposContratos = datos.tiposContratos;
          this.nivelesEstudio = datos.nivelesEducativos;
          this.subNivelEstudios = datos.subdivisiones;
          this.subsubNivelEstudios = datos.subsubdivisiones;
          this.actividadesEconomicas = datos.actividadesEconomicas;
          this.modalidadesTrabajo = datos.modalidadesTrabajo;
          this.sindicatos = datos.sindicatos;
          this.federaciones = datos.federaciones;



          this.datosFormGroup.patchValue({
            nombre: datos.nombre,
            primerApellido: datos.primerApellido,
            segundoApellido: datos.segundoApellido,
            fechaNacimiento: datos.fechaNacimiento,
            sexo: datos.sexo,
            numeroCarnet: datos.numeroCarnet
          });
          this.informacionBasicaFormGroup.patchValue({
            email: datos.email,
            telefono: datos.telefono,
            direccion: datos.direccion,
            municipio: datos.municipio,
            provincia: datos.provincia,
            comunidadAutonoma: datos.comunidadAutonoma,
            sindicado: datos.sindicado,
            estudiando: datos.estudiante,
            trabajando: datos.trabajador,
          });

          //Inicializa los campos de trabajador
          this.trabajadorFormGroup.patchValue({
            actividadEconomica: datos.actividadEconomica,
            tipoContrato: datos.tipoContrato,
            modalidadTrabajo: datos.modalidadTrabajo,
            nivelEstudio: datos.nivelEstudio,
            subNivelEstudio: datos.subNivelEstudio,
            subsubNivelEstudio: datos.subsubNivelEstudio,
            existeOrganoRepresentacion: datos.existeOrganoRepresentacionTrabajadores,
            participaOrganoRepresentacion: datos.participaOrganoRepresentacion,
            profesion: datos.profesion,
            nombreEmpresa: datos.nombreEmpresa,
            numeroTrabajadores: datos.numeroTrabajadores,
            nombreCentroTrabajo: datos.nombreCentroTrabajo,
            numeroTrabajadoresCentroTrabajo: datos.numeroTrabajadoresCentroTrabajo,
            direccionCentroTrabajo: datos.direccionCentroTrabajo,
            fechaInicioContrato: datos.fechaInicioContrato
          });

          //Inicializa los campos de estudiante
          this.estudianteFormGroup.patchValue({
            tipoEstudio: datos.subdivision,
            subtipoEstudio: datos.subsubdivision,
            nombreCentroEducativo: datos.nombreCentroEducativo,
            nivelEstudio: datos.nivelEducativo,
            nombreEstudios: datos.nombreEstudios,
            anhoFinalizacion: datos.anhoFinalizacion,
            sindicatoEstudiantil: datos.sindicatoEstudiantil
          });

          //Inicializa los campos de sindicado
          this.sindicadoFormGroup.patchValue({
            sindicato: datos.sindicato,
            federacion: datos.federacion,
            participaAreaJuventud: datos.participaAreaJuventud,
            cargo: datos.cargo,
            sindicatoOtros: datos.sindicatoOtros,
            federacionOtros: datos.federacionOtros
          });
        },
        error: (error: any) => {
          console.error(error);
        }
      });
    } else {
      this.datosFormGroup.reset();
      this.informacionBasicaFormGroup.reset();
      this.perfilService.getInicializa().subscribe({
        next: (responsabilidades: ApiResponse) => {
          const datos = responsabilidades.data;
          const data: FormularioPerfil = datos as FormularioPerfil;

          this.comunidadesAutonomas = data.comunidades;
          this.informacionBasicaFormGroup.get('municipio')?.disable();
          this.informacionBasicaFormGroup.get('provincia')?.disable();
          this.informacionBasicaFormGroup.patchValue({
            provincia: 0,
            municipio: 0,
            comunidadAutonoma: 0
          });
          this.actividadesEconomicas = data.actividadesEconomicas;
          this.tiposContratos = data.tiposContratos;
          this.nivelesEstudio = data.nivelesEducativos;
          this.subNivelEstudios = data.subdivisiones;
          this.subsubNivelEstudios = data.subsubdivisiones;
          this.actividadesEconomicas = data.actividadesEconomicas;
          this.modalidadesTrabajo = data.modalidadesTrabajo;
          this.sindicatos = data.sindicatos;
          this.federaciones = data.federaciones;
        },
        error: (error: any) => {
          console.error("Error en la llamada a inicializa: ", error);
        }
      });
    }

    // Establece los validadores condicionales
    this.setupConditionalValidators();
    this.initDynamicValidators();

    // coherencia inicial
    this.toggleGroupValidators(this.trabajadorFormGroup,
      this.informacionBasicaFormGroup.get('trabajando')!.value);
    this.toggleGroupValidators(this.estudianteFormGroup,
      this.informacionBasicaFormGroup.get('estudiando')!.value);
    this.toggleGroupValidators(this.sindicadoFormGroup,
      this.informacionBasicaFormGroup.get('sindicado')!.value);
  }

  /** true si el grupo está válido **o** deshabilitado */
  private isOk(ctrl: AbstractControl): boolean {
    return ctrl.disabled || ctrl.valid;
  }


  setupConditionalValidators(): void {
    if (!this.militanteId && !this.esPremilitante) {
      this.cuotaFormGroup.get('cuota')?.setValidators([Validators.required, Validators.min(1)]);
    } else {
      this.cuotaFormGroup.get('cuota')?.clearValidators();
      this.cuotaFormGroup.get('activar')?.clearValidators();
    }
    this.cuotaFormGroup.get('cuota')?.updateValueAndValidity();
    this.cuotaFormGroup.get('activar')?.updateValueAndValidity();
  }

  seleccionarTipo(premilitante: boolean, stepper: MatStepper) {
    this.esPremilitante = premilitante;
    this.tipoFormGroup.get('premilitante')?.setValue(premilitante);

    if (premilitante) {
      this.datosFormGroup.get('numeroCarnet')?.clearValidators();
    } else {
      this.datosFormGroup.get('numeroCarnet')?.setValidators([Validators.required, Validators.minLength(1), Validators.maxLength(4), Validators.pattern('[0-9]{1,4}')]);
    }
    this.datosFormGroup.get('numeroCarnet')?.updateValueAndValidity();
    this.setupConditionalValidators();
    stepper.next();
  }

  /** Utilidad para trazar el estado de un FormGroup */
  private logFormGroupStatus(formGroup: FormGroup, nombre: string): void {
    const estado = formGroup.disabled ? '🔇 ignorado' :
      formGroup.valid ? '✅ válido' : '❌ inválido';

    console.groupCollapsed(`${nombre}: ${estado}`);

    if (!formGroup.disabled && !formGroup.valid) {
      Object.entries(formGroup.controls).forEach(([campo, control]) => {
        if (control.invalid) {
          console.warn(
            `%c[${nombre}] ${campo} inválido`,
            'color: red; font-weight: bold;',
            { valor: control.value, errores: control.errors }
          );
        }
      });
    }
    console.groupEnd();
  }


  /** Activa/desactiva validadores de TODO el grupo */
  private toggleGroupValidators(group: FormGroup, enabled: boolean): void {
    Object.values(group.controls).forEach(ctrl => {
      if (enabled) {
        // restauramos los validadores que declaraste al crear el grupo
        const validators = (ctrl as any)._origValidators ?? ctrl.validator;
        ctrl.setValidators(validators);
      } else {
        // guardamos los validadores actuales la primera vez
        if (!(ctrl as any)._origValidators) {
          (ctrl as any)._origValidators = ctrl.validator;
        }
        ctrl.clearValidators();
      }
      ctrl.updateValueAndValidity({ emitEvent: false });
    });

    // si realmente no se usa, deshabilita el grupo (opcional)
    enabled ? group.enable({ emitEvent: false }) : group.disable({ emitEvent: false });
  }

  private initDynamicValidators(): void {

    /** cuando cambia “trabajando”      */
    this.informacionBasicaFormGroup.get('trabajando')!
      .valueChanges.subscribe((trabajando: boolean) =>
        this.toggleGroupValidators(this.trabajadorFormGroup, trabajando));

    /** cuando cambia “estudiando”      */
    this.informacionBasicaFormGroup.get('estudiando')!
      .valueChanges.subscribe((estudiando: boolean) =>
        this.toggleGroupValidators(this.estudianteFormGroup, estudiando));

    /** cuando cambia “sindicado”       */
    this.informacionBasicaFormGroup.get('sindicado')!
      .valueChanges.subscribe((sindicado: boolean) =>
        this.toggleGroupValidators(this.sindicadoFormGroup, sindicado));
  }



  aceptar(): void {
    // 1.  Trazamos cada grupo; la función devuelve true/false
    const tipoOK = this.isOk(this.tipoFormGroup);
    const datosOK = this.isOk(this.datosFormGroup);
    const infoBasicaOK = this.isOk(this.informacionBasicaFormGroup);
    const trabajadorOK = this.isOk(this.trabajadorFormGroup);
    const estudianteOK = this.isOk(this.estudianteFormGroup);
    const sindicadoOK = this.isOk(this.sindicadoFormGroup);
    const cuotaOK = this.esPremilitante ? true : this.isOk(this.cuotaFormGroup);

    // 2.  Resumen en una sola línea
    const todoValido = tipoOK && datosOK && infoBasicaOK &&
      trabajadorOK && estudianteOK && sindicadoOK &&
      (this.esPremilitante || cuotaOK);

    console.table([
      { grupo: 'tipo', valido: tipoOK },
      { grupo: 'datos', valido: datosOK },
      { grupo: 'info básica', valido: infoBasicaOK },
      { grupo: 'trabajador', valido: trabajadorOK },
      { grupo: 'estudiante', valido: estudianteOK },
      { grupo: 'sindicado', valido: sindicadoOK },
      { grupo: 'cuota', valido: cuotaOK }
    ]);

    console.log(`¿Todos los formularios válidos? ${todoValido}`);

    // 3.  Si no está todo ok, salimos pronto
    if (!todoValido) {
      // Puede venirte bien marcar todos los formularios como tocados
      this.tipoFormGroup.markAllAsTouched();
      this.datosFormGroup.markAllAsTouched();
      this.informacionBasicaFormGroup.markAllAsTouched();
      this.trabajadorFormGroup.markAllAsTouched();
      this.estudianteFormGroup.markAllAsTouched();
      this.sindicadoFormGroup.markAllAsTouched();
      this.cuotaFormGroup.markAllAsTouched();
      return;
    }
    if (this.tipoFormGroup.valid && this.datosFormGroup.valid && this.informacionBasicaFormGroup.valid && (this.esPremilitante || this.cuotaFormGroup.valid)) {
      const formData = {
        ...this.tipoFormGroup.value,
        ...this.datosFormGroup.value,
        ...this.informacionBasicaFormGroup.value,
        ...this.trabajadorFormGroup.value,
        ...this.estudianteFormGroup.value,
        ...this.sindicadoFormGroup.value,
        ...this.cuotaFormGroup.value
      };

      //Obtiene el numero de carnet si no es premilitante
      if (!this.esPremilitante) {
        const carnet = this.datosFormGroup.get('numeroCarnet')?.value;
        if (carnet.length < 4) {
          const carnetN = carnet.padStart(4, '0');
          formData.numeroCarnet = carnetN;
        }
      }

      const organizacion = this.userPreferencesService.getOrganizacion();
      //incluye organizacion en el objeto formData
      formData.organizacion = organizacion;
      //Crea un objeto con el formData.municipio y formData.dirrecion y lo añade al formData como direccion
      formData.direccion = { municipioId: formData.municipio, direccion: formData.direccion };
      //Los campos formData.estudiando y formData.trabajando pasan a llamarse formData.estudiante y formData.trabajador manteniendo su valor
      formData.estudiante = formData.estudiando;
      formData.trabajador = formData.trabajando;
      //Elimina los campos estudiando y trabajando
      delete formData.estudiando;
      delete formData.trabajando;
      //Elimina los campos provincia y comunidadAutonoma
      delete formData.provincia;
      delete formData.comunidadAutonoma;
      //Los campos primerApellido y segundoApellido pasan a llamarse apellido y apellido2 manteniendo su valor
      formData.apellido = formData.primerApellido;
      formData.apellido2 = formData.segundoApellido;
      //Elimina los campos primerApellido y segundoApellido
      delete formData.primerApellido;
      delete formData.segundoApellido;

      //Idioma por defecto es español
      formData.idioma = 'CASTELLANO';
      //Crea un set<number> con comite.id y lo añade al formData como comitesBaseIds
      //formData.comitesBaseIds = new Set<number>();
      //formData.comitesBaseIds.add(this.comiteId);

      formData.roles = new Set<string>();
      //formData.roles.add("MIEMBRO");

      // Convertir los Sets a arrays
      formData.comiteBaseId = this.comiteId;
      formData.roles = Array.from(formData.roles);

      //Si this.militanteId es null entonces se está creando un nuevo militante
      if (this.militanteId === null) {

        this.militanteService.crearMilitante(formData).subscribe({
          next: (data: ApiResponse) => {
            this.toastr.success('Militante creado correctamente');
            this.confirmar.emit(formData);
          },
          error: (error: any) => {
            this.toastr.error('Error al crear el militante');
            console.error(error);
          }
        });
      } else {

        if (!this.militanteId) return;
        this.militanteService.editarMilitante(formData, this.militanteId).subscribe({
          next: (data: ApiResponse) => {
            this.toastr.success('Militante editado correctamente');
            this.confirmar.emit(formData);
          },
          error: (error: any) => {
            this.toastr.error('Error al editar el militante');
            console.error(error);
          }
        });
      }
    }
  }

  cancelar() {
    this.cerrar.emit();
  }

  cambiaProvincia(idNuevaProvincia: number) {
    this.informacionBasicaFormGroup.patchValue({
      municipio: 0
    });
    this.municipios = [];
    this.perfilService.getMunicipios(idNuevaProvincia).subscribe({
      next: (data: ApiResponse) => {
        this.municipios = data.data;
        this.informacionBasicaFormGroup.get('municipio')?.enable();
      },
      error: (error: any) => {
        console.error(error);
      }
    });
  }

  cambiaComunidad(idNuevaComunidad: number) {
    this.informacionBasicaFormGroup.patchValue({
      provincia: 0,
      municipio: 0
    });
    this.provincias = [];
    this.municipios = [];
    this.perfilService.getProvincias(idNuevaComunidad).subscribe({
      next: (data: ApiResponse) => {
        this.provincias = data.data;
        this.informacionBasicaFormGroup.get('provincia')?.enable();
      },
      error: (error: any) => {
        console.error(error);
      }
    });
  }

  onFooterButtonClick(action: 'next' | 'previous') {
    if (action === 'next' && !this.isLastStep()) {
      this.stepper.next();
    } else if (action === 'previous' && !this.isFirstStep()) {
      this.stepper.previous();
    }
  }

  isFirstStep(): boolean {
    return this.stepper?.selectedIndex === 0;
  }

  isLastStep(): boolean {
    return this.stepper?.selectedIndex === this.stepper?.steps.length - 1;
  }

  habilitaParticipa(a: string): void {

    if (a === 'SI') {
      this.trabajadorFormGroup.get('participaOrganoRepresentacion')?.enable();
    } else {
      this.trabajadorFormGroup.get('participaOrganoRepresentacion')?.setValue(false);
      this.trabajadorFormGroup.get('participaOrganoRepresentacion')?.disable();
    }
  }
  getFutureYears(): number[] {
    const currentYear = new Date().getFullYear();
    return Array.from({ length: 11 }, (_, i) => currentYear + i);
  }
  obtenerTiposDeEstudio(nivelEstudio: number, change: boolean = false): void {
    this.tiposDeEstudio = this.subNivelEstudios.filter((subNivel: SelectWithPadreId) => subNivel.padreId === +nivelEstudio).map(child => ({
      id: child.id,
      nombre: child.nombre
    }));
    if (change) {
      this.estudianteFormGroup.patchValue({
        tipoEstudio: 0,
        subtipoEstudio: 0,
      });
      //actualiza this.datosEstudio para que revalue los validadores
      this.estudianteFormGroup.updateValueAndValidity();
    }
  }
  obtenerSubTiposDeEstudio(id: number, change: boolean = false): void {
    this.subtiposDeEstudio = this.subsubNivelEstudios.filter((subNivel: SelectWithPadreId) => subNivel.padreId === +id);
    if (change)
      this.estudianteFormGroup.patchValue({
        subtipoEstudio: 0,
      });
  }
  obtenerFederaciones(sindicato: number): void {
    this.federaciones = [];
    if (sindicato == -1) {
      this.sindicadoFormGroup.get('federacion')?.setValue(-1);
      this.sindicadoFormGroup.get('federacion')?.disable();
      this.sindicadoFormGroup.get('sindicatoOtros')?.setValidators([Validators.required]);
      this.sindicadoFormGroup.get('sindicatoOtros')?.updateValueAndValidity();
      return;
    }
    this.sindicadoFormGroup.get('sindicatoOtros')?.setValidators(null);
    this.sindicadoFormGroup.get('sindicatoOtros')?.updateValueAndValidity();
    this.sindicadoFormGroup.get('federacion')?.enable();
    this.sindicadoFormGroup.patchValue({
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
  notZeroValidator(control: AbstractControl): ValidationErrors | null {
    return control.value === 0 ? { 'zeroValue': true } : null;
  }
}