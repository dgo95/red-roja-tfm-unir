import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormGroup, FormBuilder, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MilitanteService } from '../../services/militante.service';
import { ToastrService } from 'ngx-toastr';


@Component({
    selector: 'app-conceder-militancia',
    templateUrl: './conceder-militancia.component.html',
    styleUrls: ['./conceder-militancia.component.css'],
    standalone: true,
    imports: [FormsModule, ReactiveFormsModule]
})
export class ConcederMilitanciaComponent implements OnInit {

  @Input() public militanteId!: string | null;
  @Output() public cerrar = new EventEmitter<void>();
  @Output() public confirmar = new EventEmitter<void>();

  form!: FormGroup;

  constructor(private fb: FormBuilder,
    private militanteService: MilitanteService,
    private toastrService: ToastrService
  ) { }

  ngOnInit(): void {
    this.form = this.fb.group({
      numeroCarnet: ['', [Validators.required, Validators.pattern(/^\d{1,4}$/)]],
      cuota: [5, [Validators.required, Validators.min(5), Validators.pattern(/^\d+$/)]],
      activar: [true, Validators.required]
    });
  }

  public cancelar(): void {
    this.cerrar.emit();
  }

  public aceptar(): void {
    if (this.form.valid) {
      // Lógica para rellenar con ceros a la izquierda el número de carnet
      const numeroCarnet = this.form.get('numeroCarnet')?.value;
      if (numeroCarnet && numeroCarnet.length < 4) {
        this.form.get('numeroCarnet')?.setValue(numeroCarnet.padStart(4, '0'));
      }
      this.militanteService.concederMilitancia(this.militanteId!, this.form.value).subscribe({
        next: () => {
          this.toastrService.success('Militancia concedida correctamente');
          this.confirmar.emit();
        },
        error: () => {
          this.toastrService.error('Error al conceder la militancia');
        }
      });
    }
  }
}
