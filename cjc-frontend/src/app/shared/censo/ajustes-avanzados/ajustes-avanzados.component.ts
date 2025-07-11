import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormGroup, FormBuilder, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';
import moment from 'moment';
import { SupportedLanguages } from 'src/app/models/SupportedLanguages';
import { ToastrService } from 'ngx-toastr';
import { MilitanteService } from '../../services/militante.service';


@Component({
    selector: 'app-ajustes-avanzados',
    templateUrl: './ajustes-avanzados.component.html',
    styleUrls: ['./ajustes-avanzados.component.css'],
    standalone: true,
    imports: [FormsModule, ReactiveFormsModule]
})
export class AjustesAvanzadosComponent implements OnInit {

  @Input() public militanteId!: string | null;
  @Output() public cerrar = new EventEmitter<void>();
  @Output() public confirmar = new EventEmitter<any>();

  form!: FormGroup;
  idiomas = Object.keys(SupportedLanguages).map((key: string) => ({ value: key, key: SupportedLanguages[key as keyof typeof SupportedLanguages] }));
  cargando = false; // Indicador de carga
  habilitando = false; // Indicador de habilitación del usuario

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private toastrService: ToastrService,
    private militanteService: MilitanteService
  ) { }

  ngOnInit(): void {
    // Inicialización del formulario
    this.form = this.fb.group({
      militanteId: [{ value: this.militanteId, disabled: true }, Validators.required], // Solo lectura
      codigoActivacion: [{ value: '', disabled: true }], // Solo lectura
      fechaCreacion: [{ value: '', disabled: true }], // Solo lectura
      fechaModificacion: [{ value: '', disabled: true }], // Solo lectura
      ultimoIntentoLogin: [{ value: '', disabled: true }], // Solo lectura
      idiomaPreferido: ['', Validators.required], // Enum de Idioma
      caducada: [false], // Switch
      bloqueada: [false], // Switch
      credencialesExpiradas: [false], // Switch
      habilitado: [true] // Switch
    });

    if (this.militanteId) {
      this.cargarDatosUsuario(this.militanteId);
    }
  }

  private cargarDatosUsuario(militanteId: string): void {
    this.cargando = true;
    /**this.authService.obtenerUsuario(militanteId).subscribe((usuario) => {
      this.cargando = false;
      if (usuario) {
        const data = usuario.data;

        const idioma = this.idiomas.find((idioma) => idioma.value.toUpperCase() === data.idiomaPreferido);

        this.form.patchValue({
          militanteId: data.militanteId,
          codigoActivacion: data.codigoActivacion,
          fechaCreacion: this.formatearFecha(data.fechaCreacion),
          fechaModificacion: this.formatearFecha(data.fechaModificacion),
          ultimoIntentoLogin: this.formatearFecha(data.ultimoIntentoLogin),
          idiomaPreferido: idioma?.key,
          caducada: data.caducada,
          bloqueada: data.bloqueada,
          credencialesExpiradas: data.credencialesExpiradas,
          habilitado: data.habilitado
        });
        // Dehabilita el campo habilitado
        this.form.get('habilitado')?.disable();
      }
    });*/
  }

  // Función para formatear las fechas al formato dd-MM-yyyy HH:mm:ss
  private formatearFecha(fecha: string | null): string {
    if (!fecha || !moment(fecha).isValid()) {
      return 'Nunca'; // Si la fecha es nula o inválida, devolvemos 'Nunca'
    }
    return moment(fecha).format('DD-MM-yyyy HH:mm:ss'); // Formateo de la fecha
  }

  public cancelar(): void {
    this.cerrar.emit();
  }

  public aceptar(): void {
    if (this.form.valid) {
      this.cargando = true;
      const formValues = this.form.getRawValue();

      //Se ponen a null los campos que no se pueden modificar
      formValues.codigoActivacion = null;
      formValues.fechaCreacion = null;
      formValues.fechaModificacion = null;
      formValues.ultimoIntentoLogin = null;

      const idioma = this.idiomas.find((idioma) => idioma.key === formValues.idiomaPreferido);
      if (idioma) {
        formValues.idiomaPreferido = idioma.value.toUpperCase();
      }

      // TODO Enviar la actualización del usuario al backend
      /**this.authService.actualizarUsuario(this.militanteId!, formValues).pipe(
        catchError(error => {
          console.error('Error al actualizar los datos del usuario:', error);
          this.cargando = false;
          this.toastrService.error('Ocurrió un error al actualizar los datos del usuario', 'Error');
          return of(null);
        })
      ).subscribe((response) => {
        this.cargando = false;
        this.toastrService.success('Datos del usuario actualizados correctamente', 'Éxito');
        if (response) {
          this.confirmar.emit(formValues);
        }
      });*/
    }
  }

  public habilitarUsuario(): void {
    this.habilitando = true;
    this.militanteService.activaMilitante(this.militanteId!).subscribe({
      next: (res) => {
        this.habilitando = false;
        this.toastrService.success('Email de activación enviado correctamente', 'Éxito');
        this.form.patchValue({ codigoActivacion: res.data });
      },
      error: (error) => {
        this.habilitando = false;
        console.error(error);
        this.toastrService.error('Ocurrió un error al habilitar el usuario', 'Error');
      }
    });
  }
}
