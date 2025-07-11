import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ResponsabilidadTraducida, Responsable } from 'src/app/colectivo/colectivo.component';


@Component({
    selector: 'app-editar-responsabilidades-modal',
    templateUrl: './editar-responsabilidades-modal.component.html',
    styleUrls: ['./editar-responsabilidades-modal.component.scss'],
    standalone: true,
    imports: [FormsModule, ReactiveFormsModule]
})
export class EditarResponsabilidadesModalComponent implements OnInit {

  @Input() responsabilidades!: ResponsabilidadTraducida[];
  @Input() militantes!: Responsable[];

  @Output() public cerrar = new EventEmitter<void>();
  @Output() public confirmar = new EventEmitter<any>();

  isDesplegableAbierto = false;
  form!: FormGroup;

  constructor(private fb: FormBuilder) { }

  // Método para cambiar el estado del desplegable
  toggleDesplegable() {
    this.isDesplegableAbierto = !this.isDesplegableAbierto;
  }

  ngOnInit(): void {
    
    this.form = this.fb.group({});

    let i = 0;

    this.responsabilidades.forEach((r) => {
      
      this.form.addControl(r.id, new FormControl(r.persona));

      if (r.persona !== '') {
        i++;
      }
    });

    const responsabilidadFrente = this.responsabilidades.find(r => r.id === 'FRENTE');

    this.form.addControl("responsabilidadFrenteMasas", new FormControl(responsabilidadFrente?.persona !== ''));

    if (i > 3) {
      this.isDesplegableAbierto = true;
    }
  }

  public cancelar(): void {
    this.cerrar.emit();
  }
  get responsabilidadesFiltradas() {
    return this.responsabilidades.filter((responsibility, i) => {
      if (i < 3) {
        return false; // Excluye todos los elementos donde i <= 3
      }
      // Cuando form.responsabilidadFrenteMasas es true
      if (this.form.get('responsabilidadFrenteMasas')?.value) {
        return !['MES', 'MOS', 'VECINAL'].includes(responsibility.id);
      }
      // Cuando responsabilidadFrenteMasas es false
      return !['FRENTE'].includes(responsibility.id);
    });
  }

  public aceptar(): void {
    const esResponsableFrenteMasas = this.form.get('responsabilidadFrenteMasas')?.value;
  
    if (esResponsableFrenteMasas) {
      this.establecerValorControls(['MES', 'MOS', 'VECINAL'], '');
    } else {
      this.establecerValorControl('FRENTE', '');
    }
  
    this.confirmar.emit(this.form.getRawValue());
  }
  
  private establecerValorControls(controls: string[], valor: any): void {
    controls.forEach(control => this.establecerValorControl(control, valor));
  }
  
  private establecerValorControl(control: string, valor: any): void {
    this.form.get(control)?.setValue(valor);
  }
  
}
