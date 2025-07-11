import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { NuevoDocumentoDTO } from '../../documento.dto';
import { FileUploadModule } from 'primeng/fileupload';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { DropdownModule } from 'primeng/dropdown';
import { CalendarModule } from 'primeng/calendar';
import { ConfidencialidadLabel, NivelDocumentoLabel, TipoDocumentoLabel, CategoriaLabel, Categoria, Confidencialidad, NivelDocumento, TipoDocumento } from '../../documento.enums';
import { SelectModule } from 'primeng/select';
import { DatePickerModule } from 'primeng/datepicker';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'app-documento-upload-modal',
  standalone: true,
  templateUrl: './documento-upload-modal.component.html',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FileUploadModule,
    DialogModule,
    InputTextModule,
    DropdownModule,
    CalendarModule,
    SelectModule,
    DatePickerModule,
    ButtonModule
  ]
})
export class DocumentoUploadModalComponent {
  @Output() aceptar = new EventEmitter<NuevoDocumentoDTO>();
  @Output() cancelar = new EventEmitter<void>();

  @Input() visible: boolean = true;

  Confidencialidad = Confidencialidad;

  form: FormGroup;
  archivo: File | undefined = undefined;

  constructor(private fb: FormBuilder) {
    this.form = this.fb.group({
      archivo: [null, Validators.required],
      titulo: ['', Validators.required],
      fecha: [null, Validators.required],
      confidencialidad: [null, Validators.required],
      tipo: [null, Validators.required],
      categoria: [null, Validators.required]
    });
  }

  confidencialidades = (Object.values(Confidencialidad)
    .filter(value => typeof value === 'number') as Confidencialidad[])
    .map(c => ({ label: ConfidencialidadLabel[c], value: c }));

  niveles = (Object.values(NivelDocumento)
    .filter(value => typeof value === 'number') as NivelDocumento[])
    .map(n => ({ label: NivelDocumentoLabel[n], value: n }));

  tipos = (Object.values(TipoDocumento)
    .filter(value => typeof value === 'number') as TipoDocumento[])
    .map(t => ({ label: TipoDocumentoLabel[t], value: t }));

  categorias = (Object.values(Categoria)
    .filter(value => typeof value === 'number') as Categoria[])
    .map(c => ({ label: CategoriaLabel[c], value: c }));


  onFileSelect(event: any) {
    const files = event.files || event.target.files;
    if (files && files.length > 0) {
      this.archivo = files[0];
      this.form.get('archivo')?.setValue(this.archivo);
      this.form.get('titulo')?.setValue(this.archivo?.name || '');
      this.form.get('fecha')?.setValue(new Date());
      this.form.get('archivo')?.markAsTouched();
      this.form.get('titulo')?.markAsTouched();
      this.form.get('fecha')?.markAsTouched();
    }
  }

  onAceptar() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const dto: NuevoDocumentoDTO = {
      ...this.form.value,
      archivo: this.form.get('archivo')?.value,
      propietario: ''
    };
    this.aceptar.emit(dto);
    this.visible = false;
    this.reset();
  }

  onCancelar() {
    this.cancelar.emit();
    this.visible = false;
    this.reset();
  }

  reset() {
    this.form.reset();
    this.archivo = undefined;
  }
}
