import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';

@Component({
    selector: 'app-crear-punto',
    templateUrl: './crear-punto.component.html',
    styleUrls: ['./crear-punto.component.scss'],
    standalone: true,
    imports: [FormsModule, ReactiveFormsModule]
})
export class CrearPuntoComponent implements OnInit {

  @Input()
  public punto!: any;
  @Output() public cerrar = new EventEmitter<void>();
  @Output() public confirmar = new EventEmitter<any>();

  form!: FormGroup;

  constructor(private fb: FormBuilder) { }

  ngOnInit(): void {
    //Se inicializa el formulario, con titulo como el valor del punto.titulo (validador requiered) y descripcion como null
    this.form = this.fb.group({
      titulo: ["Nuevo punto", Validators.required],
      descripcion: [null]
    });
  }

  public cancelar(): void {
    this.cerrar.emit();
  }

  public aceptar(): void {
    //Si el formulario es valido, se emite el valor del formulario
    if (this.form.invalid) {
      return;
    }
    this.confirmar.emit(this.form.getRawValue());
  }
}