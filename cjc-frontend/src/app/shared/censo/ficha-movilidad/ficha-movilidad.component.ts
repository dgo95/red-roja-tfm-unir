import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatStepper, MatStepperModule } from '@angular/material/stepper';
import { ApiResponse } from 'src/app/models/respuestaApi';
import { Select } from 'src/app/models/select';
import { PerfilService } from '../../services/perfil.service';
import { MilitanteService } from '../../services/militante.service';
import { ToastrService } from 'ngx-toastr';
import { TranslateModule } from '@ngx-translate/core';


@Component({
    selector: 'app-ficha-movilidad',
    templateUrl: './ficha-movilidad.component.html',
    styleUrls: ['./ficha-movilidad.component.scss'],
    standalone: true,
    imports: [MatStepperModule, FormsModule, ReactiveFormsModule, TranslateModule]
})
export class FichaMovilidadComponent implements OnInit {

  @Input() public militanteId: string | null = null;
  @Output() public cerrar = new EventEmitter<void>();

  @ViewChild('stepper') stepper!: MatStepper;

  datosPersonalesFormGroup: FormGroup;
  experienciaTrabajoFormGroup: FormGroup;
  trabajoInternoFormGroup: FormGroup;
  otrasObservacionesFormGroup: FormGroup;

  fichaCreada: boolean = false;

  temporal: boolean = true;
  comunidadesAutonomas: Select[] = [];
  provincias: Select[] = [];
  municipios: Select[] = [];

  // Propiedades de estado
  isLoadingDocx: boolean = false;
  isLoadingPdf: boolean = false;

  constructor(private fb: FormBuilder, private perfilService: PerfilService,
    private militanteService: MilitanteService,
    private toastr: ToastrService) {
    this.datosPersonalesFormGroup = this.fb.group({
      municipio: [0, Validators.required],
      provincia: [0, Validators.required],
      comunidadAutonoma: [0, Validators.required],
      objetoTraslado: ['', Validators.required],
      fechaInicio: ['', Validators.required],
      fechaFin: ['']
    });


    this.experienciaTrabajoFormGroup = this.fb.group({
      frentesTrabajo: [''],
      sindicatoEstudiantil: [''],
    });

    this.trabajoInternoFormGroup = this.fb.group({
      otrasResponsabilidades: [''],
      responsabilidadDestacada: [''],
      puntosPositivos: [''],
      habitosMejorar: [''],
    });

    this.otrasObservacionesFormGroup = this.fb.group({
      otrasObservaciones: [''],
    });
  }

  ngOnInit(): void {
    this.militanteService.obtenerFichaMovilidad(this.militanteId!).subscribe(
      {
        next: (data: ApiResponse) => {
          if (data.data) {
            

            //Inicializa comunidades autónomas, provincias y municipios
            this.comunidadesAutonomas = data.data.comunidades;
            //lo mismo para provincias y municipios pero si es null se pone un array vacio
            this.provincias = data.data.provincias || [];
            this.municipios = data.data.municipios || [];

            //Inicializa datosPersonalesFormGroup, con data.data.municipio, data.data.provincia, data.data.comunidadAutonoma, data.data.objetoTraslado, data.data.fechaInicio, data.data.fechaFin
            this.datosPersonalesFormGroup.patchValue({
              municipio: data.data.municipio || 0,
              provincia: data.data.provincia || 0,
              comunidadAutonoma: data.data.comunidadAutonoma,
              objetoTraslado: data.data.objetoTraslado || '',
              fechaInicio: data.data.fechaInicio,
              fechaFin: data.data.fechaFin
            });

            //Si data.data.fechaFin es null, temporal es true y quita el validador de fechaFin
            if (!data.data.fechaFin) {
              this.temporal = true;
              this.datosPersonalesFormGroup.get('fechaFin')?.clearValidators();
              this.datosPersonalesFormGroup.get('fechaFin')?.updateValueAndValidity();
            }

            this.fichaCreada = this.datosPersonalesFormGroup.valid;
            //si provincias no tienes datos se deshabilita el datosPersonalesFormGroup.get('provincia')?.disable();
            if (this.provincias.length === 0) {
              this.datosPersonalesFormGroup.get('provincia')?.disable();
            }
            // lo mismo para municipios
            if (this.municipios.length === 0) {
              this.datosPersonalesFormGroup.get('municipio')?.disable();
            }

            //Inicializa experienciaTrabajoFormGroup con data.data.frentesTrabajo, data.data.sindicatoEstudiantil
            this.experienciaTrabajoFormGroup.patchValue({
              frentesTrabajo: data.data.frentesTrabajo || '',
              sindicatoEstudiantil: data.data.sindicatoEstudiantil || ''
            });

            //Inicializa trabajoInternoFormGroup con data.data.otrasResponsabilidades, data.data.responsabilidadDestacada, data.data.puntosPositivos, data.data.habitosMejorar
            this.trabajoInternoFormGroup.patchValue({
              otrasResponsabilidades: data.data.otrasResponsabilidades || '',
              responsabilidadDestacada: data.data.responsabilidadDestacada || '',
              puntosPositivos: data.data.puntosPositivos || '',
              habitosMejorar: data.data.habitosMejorar || ''
            });

            //Inicializa otrasObservacionesFormGroup con data.data.otrasObservaciones
            this.otrasObservacionesFormGroup.patchValue({
              otrasObservaciones: data.data.otrasObservaciones || ''
            });

          }
        },
        error: (error: any) => {
          console.error(error);
        }
      });
  }

  // Como estamos con pruebas se añaden muchos console.log para ver el valor de las variables en cada paso
  // y asi detectar errores
  onTiempoEstanciaChange(event: any) {
    this.temporal = event.target.checked;
    if (!this.temporal) {
      this.datosPersonalesFormGroup.get('fechaFin')?.clearValidators();
      this.datosPersonalesFormGroup.get('fechaFin')?.updateValueAndValidity();
      this.datosPersonalesFormGroup.patchValue({
        fechaFin: ''
      });
    } else {
      this.datosPersonalesFormGroup.get('fechaFin')?.setValidators([Validators.required]);
      this.datosPersonalesFormGroup.get('fechaFin')?.updateValueAndValidity();
    }
  }

  onFooterButtonClick(action: 'next' | 'previous') {
    if (action === 'next' && !this.isLastStep()) {
      //Si el primer formulario no es valido, se reccorren todos sus campos para mostrar
      // por consola los campos que no son validos, su estado y su valor
      if (!this.datosPersonalesFormGroup.valid) {
        Object.keys(this.datosPersonalesFormGroup.controls).forEach(key => {
          if (this.datosPersonalesFormGroup.get(key)?.invalid) {
            
            //Además se muestra por consola el validador que no se cumple
            
          }
        });
      }
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

  isNStep(n: number): boolean {
    return this.stepper?.selectedIndex === n - 1;
  }

  aceptar(): void {
    if (this.datosPersonalesFormGroup.valid) { // Solo validar el primer formulario
      const formData = {
        ...this.datosPersonalesFormGroup.value,
        ...this.experienciaTrabajoFormGroup.value,
        ...this.trabajoInternoFormGroup.value,
        ...this.otrasObservacionesFormGroup.value
      };

      this.militanteService.editarFichaMovilidad(this.militanteId!, formData).subscribe(
        {
          next: (data: ApiResponse) => {
            
            this.fichaCreada = true;
            this.stepper.selectedIndex = 0;
            this.toastr.success(data.message, 'Éxito');
          },
          error: (error: any) => {
            console.error(error);
            this.toastr.error('Ocurrió un error al crear la ficha de movilidad', 'Error');
          }
        });
      
      // Aquí puedes enviar el formulario al servidor o procesarlo como necesites
    } else {
      
    }
  }


  cancelar() {
    this.fichaCreada = false;
    this.cerrar.emit();
  }
  cambiaProvincia(idNuevaProvincia: number) {
    this.datosPersonalesFormGroup.patchValue({
      municipio: 0
    });
    this.municipios = [];
    this.perfilService.getMunicipios(idNuevaProvincia).subscribe(
      {
        next: (data: ApiResponse) => {
          this.municipios = data.data;
          this.datosPersonalesFormGroup.get('municipio')?.enable();
          // Si solo hay un municipio, seleccionarlo automáticamente
          if (this.municipios.length === 1) {
            this.datosPersonalesFormGroup.patchValue({
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
    this.datosPersonalesFormGroup.patchValue({
      provincia: 0,
      municipio: 0
    });
    this.provincias = [];
    this.municipios = [];
    this.perfilService.getProvincias(idNuevaComunidad).subscribe(
      {
        next: (data: ApiResponse) => {
          this.provincias = data.data;
          this.datosPersonalesFormGroup.get('provincia')?.enable();
          // Si solo hay una provincia, seleccionarla automáticamente
          if (this.provincias.length === 1) {
            this.datosPersonalesFormGroup.patchValue({
              provincia: this.provincias[0].id
            });
            this.cambiaProvincia(this.provincias[0].id);
          }
          this.datosPersonalesFormGroup.get('municipio')?.disable();
        },
        error: (error: any) => {
          console.error(error);
        }
      });
  }
  descargarDocx() {
    if (!this.militanteId) {
      console.error('No hay ID de militante disponible para descargar el archivo.');
      return;
    }

    // Activar el spinner para DOCX
    this.isLoadingDocx = true;

    this.militanteService.descargarFichaMovilidadDocx(this.militanteId).subscribe({
      next: (response: Blob) => {
        const url = window.URL.createObjectURL(response);
        const a = document.createElement('a');
        a.href = url;
        a.download = `ficha_movilidad_${this.militanteId}.docx`;
        a.click();
        window.URL.revokeObjectURL(url);
        // Desactivar el spinner
        this.isLoadingDocx = false;
      },
      error: (error) => {
        console.error('Error al descargar el archivo DOCX:', error);

        // Desactivar el spinner
        this.isLoadingDocx = false;

        // Notificación de error
        this.toastr.error('Ocurrió un error al descargar el archivo DOCX', 'Error');      }
    });
  }
  descargarPdf() {
    if (!this.militanteId) {
      console.error('No hay ID de militante disponible para descargar el archivo.');
      return;
    }

    // Activar el spinner para PDF
    this.isLoadingPdf = true;

    this.militanteService.descargarFichaMovilidadPdf(this.militanteId).subscribe({
      next: (response: Blob) => {
        const url = window.URL.createObjectURL(response);
        const a = document.createElement('a');
        a.href = url;
        a.download = `ficha_movilidad_${this.militanteId}.pdf`;
        a.click();
        window.URL.revokeObjectURL(url);
        // Desactivar el spinner
        this.isLoadingPdf = false;
      },
      error: (error) => {
        console.error('Error al descargar el archivo PDF:', error);

        // Desactivar el spinner
        this.isLoadingPdf = false;

        // Notificación de error
        this.toastr.error('Ocurrió un error al descargar el archivo PDF', 'Error');
      }
    });
  }
  eliminarFicha() {
    this.militanteService.eliminarFichaMovilidad(this.militanteId!).subscribe(
      {
        next: (data: ApiResponse) => {
          
          this.toastr.success(data.message, 'Éxito');
          this.fichaCreada = false;
          this.cerrar.emit();
        },
        error: (error: any) => {
          console.error(error);
          this.toastr.error('Ocurrió un error al eliminar la ficha de movilidad', 'Error');
        }
      });
  }
}