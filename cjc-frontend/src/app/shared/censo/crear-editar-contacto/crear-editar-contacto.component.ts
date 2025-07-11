import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatStepper, MatStepperModule } from '@angular/material/stepper';
import { ToastrService } from 'ngx-toastr';
import { Select } from 'src/app/models/select';
import { ColectivoService } from '../../services/colectivo.service';
import { PerfilService } from '../../services/perfil.service';
import { ApiResponse } from 'src/app/models/respuestaApi';
import { Responsable } from 'src/app/colectivo/colectivo.component';


@Component({
    selector: 'app-crear-editar-contacto',
    templateUrl: './crear-editar-contacto.component.html',
    styleUrls: ['./crear-editar-contacto.component.css'],
    standalone: true,
    imports: [MatStepperModule, FormsModule, ReactiveFormsModule]
})
export class CrearEditarContactoComponent implements OnInit {

  @Input() public contactoId: number | null = null;
  @Input() public comiteId!: number;
  @Output() public cerrar = new EventEmitter<void>();
  @Output() public confirmar = new EventEmitter<any>();

  @ViewChild('stepper') stepper!: MatStepper;

  formDatosBasicos!: FormGroup;
  formDatosEstado!: FormGroup;

  municipios: Select[] = [];
  provincias: Select[] = [];
  comunidadesAutonomas: Select[] = [];

  responsables: Responsable[] = [];

  constructor(private fb: FormBuilder, private toastr: ToastrService,
    private colectivoService: ColectivoService,
    private perfilService: PerfilService
  ) { }

  ngOnInit(): void {
    this.formDatosBasicos = this.fb.group({
      nombre: ['', [Validators.required]],
      fechaNacimiento: ['', [Validators.required]],
      estudiante: [false],
      trabajador: [false],
      telefono: ['', [Validators.required, Validators.pattern(/^[0-9]{9}$/)]],
      email: ['', [Validators.required, Validators.email]],
      municipio: ['', [Validators.required]],
      provincia: ['', [Validators.required]],
      comunidadAutonoma: ['', [Validators.required]],
    });

    this.formDatosEstado = this.fb.group({
      situacionOrigen: ['', [Validators.required]],
      estadoActual: ['', [Validators.required]],
      encargadoSeguimiento: ['', [Validators.required]],
      proximaTarea: ['', [Validators.required]],
    });

    this.colectivoService.inicializaContacto(this.contactoId, this.comiteId).subscribe({
      next: (data) => {
        const datos = data.data;

        this.comunidadesAutonomas = datos.comunidades;
        this.provincias = datos.provincias;
        this.municipios = datos.municipios;

        // Carga datos del primer paso
        this.formDatosBasicos.patchValue({
          nombre: datos.nombre,
          fechaNacimiento: datos.fechaNacimiento,
          estudiante: datos.estudiante,
          trabajador: datos.trabajador,
          telefono: datos.telefono,
          email: datos.email,
          municipio: datos.municipio,
          provincia: datos.provincia,
          //si datos.comunidadAutonoma es undefined, se le pone el valor ""
          comunidadAutonoma: datos.comunidad || ''
        });

        // Si municipios está vacio o su longitud es 0, se le pone el valor "" y se deshabilita
        if (!this.municipios || this.municipios.length === 0) {
          this.formDatosBasicos.patchValue({
            municipio: ''
          });
          this.formDatosBasicos.get('municipio')?.disable();
          //updateValueAndValidity() es necesario para que se actualice el estado de la validación
          this.formDatosBasicos.get('municipio')?.updateValueAndValidity();
        }
        // Si provincias está vacio o su longitud es 0, se le pone el valor "" y se deshabilita
        if (!this.provincias || this.provincias.length === 0) {
          this.formDatosBasicos.patchValue({
            provincia: ''
          });
          this.formDatosBasicos.get('provincia')?.disable();
        }

        // Carga datos del segundo paso
        this.formDatosEstado.patchValue({
          situacionOrigen: datos.situacionOrigen,
          estadoActual: datos.estadoActual,
          encargadoSeguimiento: !datos.encargadoSeguimiento ? '' : datos.encargadoSeguimiento,
          proximaTarea: datos.proximaTarea,
        });
        this.responsables = datos.responsables;
      },
      error: (error) => {
        this.toastr.error('Error al cargar los datos del contacto', 'Error');
        console.error(error);
      }
    });
  }
  cambiaProvincia(idNuevaProvincia: number) {
    this.formDatosBasicos.patchValue({
      municipio: ''
    });
    this.municipios = [];
    this.perfilService.getMunicipios(idNuevaProvincia).subscribe(
      {
        next: (data: ApiResponse) => {
          this.municipios = data.data;
          this.formDatosBasicos.get('municipio')?.enable();
          // Si solo hay un municipio, seleccionarlo automáticamente
          if (this.municipios.length === 1) {
            this.formDatosBasicos.patchValue({
              municipio: this.municipios[0].id
            });
          }
        },
        error: (error: any) => {
          console.error(error);
        }
      });
  }

  cambiaComunidad(idNuevaComunidad: number) {
    this.formDatosBasicos.patchValue({
      provincia: '',
      municipio: ''
    });
    this.provincias = [];
    this.municipios = [];
    this.perfilService.getProvincias(idNuevaComunidad).subscribe(
      {
        next: (data: ApiResponse) => {
          this.provincias = data.data;
          this.formDatosBasicos.get('provincia')?.enable();
          // Si solo hay una provincia, seleccionarla automáticamente
          if (this.provincias.length === 1) {
            this.formDatosBasicos.patchValue({
              provincia: this.provincias[0].id
            });
            this.cambiaProvincia(this.provincias[0].id);
          }
          this.formDatosBasicos.get('municipio')?.disable();
        },
        error: (error: any) => {
          console.error(error);
        }
      });
  }

  cancelar() {
    this.cerrar.emit();
  }

  aceptar() {
    if (this.formDatosBasicos.valid && this.formDatosEstado.valid) {
      const data = {
        ...this.formDatosBasicos.value,
        ...this.formDatosEstado.value
      };
      //elimina los campos provincia y comunidadAutonoma
      delete data.provincia;
      delete data.comunidadAutonoma;

      this.confirmar.emit(data);
    } else {
      this.toastr.error('Por favor complete todos los campos requeridos', 'Error');
    }
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

  debug() {
    
    // Imprime si es valido o no
    
    this.logFormGroupStatus(this.formDatosBasicos);

    
    // Imprime si es valido o no
    
    this.logFormGroupStatus(this.formDatosEstado);
  }

  private logFormGroupStatus(formGroup: FormGroup) {
    const controls = formGroup.controls;

    for (const name in controls) {
      if (controls.hasOwnProperty(name)) {
        const control = controls[name];
        
        
        

        if (!control.valid) {
          const errors = control.errors;
          
          if (errors) {
            Object.keys(errors).forEach(errorKey => {
              
            });
          }
        }
      }
    }
  }
}