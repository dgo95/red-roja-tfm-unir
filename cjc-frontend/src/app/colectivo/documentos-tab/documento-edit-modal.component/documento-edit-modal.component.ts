import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { SelectModule } from 'primeng/select';
import { InputTextModule } from 'primeng/inputtext';
import { CardModule } from 'primeng/card';

import {
  Confidencialidad, ConfidencialidadLabel,
  TipoDocumento,  TipoDocumentoLabel,
  Categoria,       CategoriaLabel,
  NivelDocumento
} from '../../documento.enums';
import { ActualizarDocumentoDTO, DocumentoRecibidoDTO } from '../../documento.dto';

@Component({
  selector   : 'app-documento-edit-modal',
  standalone : true,
  imports    : [
    CommonModule,
    ReactiveFormsModule,
    DialogModule,
    ButtonModule,
    SelectModule,
    InputTextModule,
    CardModule
  ],
  templateUrl: './documento-edit-modal.component.html',
  styleUrls  : ['./documento-edit-modal.component.scss']
})
export class DocumentoEditModalComponent {

  /* ---------- Entrada + salida ---------- */
  @Input()  visible   = false;
  @Input()  documento!: DocumentoRecibidoDTO;
  @Output() cancelar  = new EventEmitter<void>();
  @Output() actualizar= new EventEmitter<ActualizarDocumentoDTO>();

  /* ---------- catálogos ---------- */
  readonly confidencialidades = this.enumToOptions(Confidencialidad, ConfidencialidadLabel);
  readonly tipos              = this.enumToOptions(TipoDocumento,  TipoDocumentoLabel);
  readonly categorias         = this.enumToOptions(Categoria,      CategoriaLabel);

  /* ---------- formulario ---------- */
  readonly form = this.fb.group({
    titulo          : ['', [Validators.required, Validators.maxLength(255)]],
    confidencialidad: [null as Confidencialidad | null, Validators.required],
    tipo            : [null as TipoDocumento   | null,   Validators.required],
    categoria       : [null as Categoria | null],
  });

  constructor(private fb: NonNullableFormBuilder) {}

  ngOnChanges() {
    if (this.documento) {
      this.form.reset({
        titulo          : this.documento.titulo,
        confidencialidad: this.documento.confidencialidad,
        tipo            : this.documento.tipo,
        categoria       : this.documento.categoria ?? null
      }, { emitEvent:false });
    }
  }

  guardar() {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }

    const v = this.form.getRawValue();
    const dto: ActualizarDocumentoDTO = {
      titulo          : v.titulo.trim(),
      nivel           : this.documento.nivel            as NivelDocumento, // no editable aquí
      confidencialidad: v.confidencialidad!,
      tipo            : v.tipo!,
      categorias      : v.categoria ? new Set([v.categoria]) : undefined
    };
    this.actualizar.emit(dto);
  }

  /* ---------- utils ---------- */
  private enumToOptions<E>(e:any, lbl:Record<number,string>) {
    return (Object.values(e).filter(v=>typeof v==='number') as E[])
           .map(v=>({label: lbl[v as unknown as number], value:v}));
  }
}
