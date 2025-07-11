import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AbstractControl, FormArray, FormBuilder, FormControl, FormGroup, ValidationErrors, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { format, parse } from 'date-fns';
import { Invitado } from 'src/app/models/invitado';
import { Punto, Subpunto } from 'src/app/models/punto';
import { CrearPuntoComponent } from './crear-punto/crear-punto.component';


@Component({
    selector: 'app-editar-reuniones',
    templateUrl: './editar-reuniones.component.html',
    styleUrls: ['./editar-reuniones.component.scss'],
    standalone: true,
    imports: [FormsModule, ReactiveFormsModule, CrearPuntoComponent]
})
export class EditarReunionesComponent implements OnInit {

  nuevaplantilla: Punto[] = [
    new Punto(0, 1, 'Político', 'Se trata el rimbo de la vida', [
      new Subpunto(0, 1, 'Boletín Central', ''),
      new Subpunto(0, 2, 'El proletario', ''),
    ]),
    new Punto(0, 2, 'Organización', '', [
      new Subpunto(0, 1, 'Simpatizantes', '')
    ]),
    new Punto(0, 3, 'Frentes de Masas', '', [
      new Subpunto(0, 1, 'Frente de Estudiates', ''),
      new Subpunto(0, 2, 'AMOC', ''),
      new Subpunto(0, 3, 'MOS', ''),
    ]),
    new Punto(0, 4, 'Agitación y Propaganda', '', []),
    new Punto(0, 5, 'Formación', '', [
      new Subpunto(0, 1, 'III Bloque formativo', '')
    ]),
    new Punto(0, 6, 'Finanzas', '', []),
    new Punto(0, 7, 'Varios', '', []),
    // Repite este patrón para los demás puntos y subpuntos...
    new Punto(0, 8, 'Conclusiones', 'Resumen de decisiones y próximos pasos')
  ];

  

  @Input()
  public reunion!: any;
  @Input()
  public edita!: boolean;

  @Output() public cerrar = new EventEmitter<void>();
  @Output() public confirmar = new EventEmitter<any>();

  form!: FormGroup;
  editingPuntoIndex: number | null = null;
  editingSubpuntoIndex: number | null = null;
  editingValueOriginal: Punto | null = null;
  editingSubpuntoValueOriginal: Subpunto | null = null;

  modalCrearPuntoVisible = false;
  puntoNuevo = new Punto(0, 0, '', '', []);
  subPuntoNuevo = new Subpunto(0, 0, '', '');

  // Indica si no se esta creando nada, si es true se esta creando un punto, si es false se esta creando un subpunto y si es null no se esta creando nada
  creando: boolean | null = null;
  puntoCreando: AbstractControl | null = null;



  constructor(private fb: FormBuilder) { }

  ngOnInit(): void {

    this.form = this.fb.group({
      fecha: [null, Validators.required],
      duracion: [null, Validators.required],
      direccion: [null, Validators.required],
      premilitantes: [true, Validators.required],
      puntos: this.fb.array([], minArrayLengthValidator(1)),
      invitados: this.fb.array([], invitadoValidator),
    });


    
    //Si this.reunion.puntos es null o undefined o [] se le asigna la plantillaPuntos
    if (!this.reunion.puntos || this.reunion.puntos === '') {
      this.reunion.puntos = this.nuevaplantilla;
    }
    this.form.patchValue({
      fecha: this.convertToLocaleDateTime(this.reunion.fechaInicio),
      duracion: this.reunion.duracion,
      direccion: this.reunion.direccion,
      premilitantes: this.reunion.premilitantes,
      puntos: this.reunion.puntos,
      invitados: this.reunion.invitados,
    });

    //Recorre this.reunion.invitados y por cada uno llama a this.agregarInvitado
    this.reunion.invitados.forEach((invitado: any) => {
      this.inicializarInvitado(invitado);
    });

    this.reunion.puntos.forEach((punto: any) => {
      this.incializarPunto(punto);
    });

    //Recarga el formulario
    this.form.updateValueAndValidity();

    if (!this.edita) {
      this.form.markAsPristine();
    }
  }

  get invitados() {
    return this.form.get('invitados') as FormArray;
  }

  get puntos() {
    return this.form.get('puntos') as FormArray;
  }

  getFormArray(control: AbstractControl | null): FormArray {
    // Si el control es null, devuelve un FormArray vacío
    if (control === null) {
      return this.fb.array([]);
    }
    return control as FormArray;
  }
  agregarInvitado() {
    this.invitados.push(this.fb.group(new Invitado()));
  }

  inicializarInvitado(i: Invitado) {
    this.invitados.push(this.fb.group(i));
  }

  incializarPunto(punto: Punto) {

    // Crear un FormArray para los subpuntos
    const subpuntosArray = this.fb.array(
      punto.subpuntos.map((subpunto) =>
        this.fb.group({
          id: [subpunto.id],
          orden: [subpunto.orden],
          titulo: [subpunto.titulo, Validators.required],
          descripcion: [subpunto.descripcion],
        })
      )
    );

    const puntoFormGroup = this.fb.group({
      id: [punto.id],
      orden: [punto.orden],
      titulo: [punto.titulo, Validators.required],
      descripcion: [punto.descripcion],
      subpuntos: subpuntosArray
    });

    (this.form.get('puntos') as FormArray).push(puntoFormGroup);
  }

  confirmarEdicion(punto: AbstractControl, index: number): void {
    this.editingPuntoIndex = null; // Salir del modo edición
  }

  confirmarEdicionSubpunto(subpunto: AbstractControl, index: number, indexSubpunto: number): void {
    this.editingSubpuntoIndex = null; // Salir del modo edición
    }

  cancelarEdicion(): void {
    if (this.editingPuntoIndex !== null && this.editingValueOriginal) {
      // Reemplaza el valor del punto que se estaba editando con el original
      this.puntos.at(this.editingPuntoIndex).setValue(this.editingValueOriginal);
    }
    this.editingPuntoIndex = null;
    this.editingValueOriginal = null; // Limpia el valor original almacenado
  }

  cancelarEdicionSubpunto(): void {
    if (this.editingPuntoIndex !== null && this.editingSubpuntoIndex !== null && this.editingSubpuntoValueOriginal) {
      // Reemplaza el valor del subpunto que se estaba editando con el original
      const subpuntos = (this.puntos.at(this.editingPuntoIndex).get('subpuntos') as FormArray);
      subpuntos.at(this.editingSubpuntoIndex).setValue(this.editingSubpuntoValueOriginal);
    }
    this.editingSubpuntoIndex = null;
    this.editingSubpuntoValueOriginal = null; // Limpia el valor original almacenado

  }

  eliminarInvitado(index: number) {
    this.invitados.removeAt(index);
  }

  eliminarInvitadosNoModificados(control: FormGroup<any> | FormArray<any>) {
    for (let i = this.invitados.length - 1; i >= 0; i--) {
      if (this.invitados.at(i).pristine) {
        this.invitados.removeAt(i);
      }
    }
  }

  
  
  eliminarSubpunto(punto: AbstractControl, subpuntoAEliminar: AbstractControl): void {
    const subpuntos: FormArray = punto.get('subpuntos') as FormArray;
  
    // Encuentra el índice del subpunto a eliminar dentro del FormArray de subpuntos
    const indexToRemove = subpuntos.controls.findIndex(subpunto => subpunto === subpuntoAEliminar);
  
    // Si se encontró el índice, eliminar el subpunto del FormArray
    if (indexToRemove !== -1) {
      subpuntos.removeAt(indexToRemove);
  
      // Actualizar el orden de los subpuntos posteriores
      for (let i = indexToRemove; i < subpuntos.length; i++) {
        const ordenControl = subpuntos.at(i).get('orden');
        if (ordenControl !== null) {
          let ordenActual = ordenControl.value;
          ordenControl.setValue(ordenActual - 1);
        }
      }
    }
  }
  
  
  editarSubpunto(iPunto: number, iSubpunto: number): void {
    this.editingSubpuntoIndex = iSubpunto;
  }

  crearPunto(): void {
    let nuevoOrden = 1; // Valor predeterminado si no hay puntos
    if (this.puntos.length > 0) {
      // Solo intentar acceder a 'orden' si hay puntos existentes
      const ultimoPunto = this.puntos.at(this.puntos.length - 1);
      if (ultimoPunto) {
        const ordenValue = ultimoPunto.get('orden')?.value;
        if (ordenValue != null) { // Verificar explícitamente que 'orden' no sea null
          nuevoOrden = ordenValue + 1;
        }
      }
    }
    this.puntoNuevo = new Punto(0, nuevoOrden, 'Punto '+nuevoOrden, '', []);
    this.modalCrearPuntoVisible = true;
    this.creando = true;
  }

  confirmarCrearPunto(form : any){
    //Asigna el valor del formulario a la variable puntoNuevo. titulo a titulo y descripcion a descripcion
    this.puntoNuevo.titulo = form.titulo;
    this.puntoNuevo.descripcion = form.descripcion;
    
    const puntoFormGroup = this.fb.group({
      id: [this.puntoNuevo.id],
      orden: [this.puntoNuevo.orden],
      titulo: [this.puntoNuevo.titulo, Validators.required],
      descripcion: [this.puntoNuevo.descripcion],
      subpuntos: this.fb.array([])
    });
  
    this.puntos.push(puntoFormGroup);
    //Crea un nuevo puntoNuevo estandar y lo asigna a la variable puntoNuevo
    this.puntoNuevo = new Punto(0, 0, '', '', []);
    this.modalCrearPuntoVisible = false;
  }

  crearSubpunto(punto: AbstractControl): void {
    this.puntoCreando = punto;
    let nuevoOrden = 1; // Valor predeterminado si no hay subpuntos
    const subpuntos = punto.get('subpuntos') as FormArray;
    if (subpuntos.length > 0) {
      // Solo intentar acceder a 'orden' si hay subpuntos existentes
      const ultimoSubpunto = subpuntos.at(subpuntos.length - 1);
      if (ultimoSubpunto) {
        const ordenValue = ultimoSubpunto.get('orden')?.value;
        if (ordenValue != null) { // Verificar explícitamente que 'orden' no sea null
          nuevoOrden = ordenValue + 1;
        }
      }
    }
    this.subPuntoNuevo = new Subpunto(0, nuevoOrden, 'Subpunto '+this.getLowercaseLetterFromNumber(nuevoOrden), '');
    this.modalCrearPuntoVisible = true;
    this.creando = false;
  }

  recibeModal(form : any){
    if(this.creando){
      this.confirmarCrearPunto(form);
    }else{
      this.confirmarCrearSubpunto(form, this.puntoCreando!);
      this.puntoCreando = null;
    }
    this.creando = null;
  }

  confirmarCrearSubpunto(form : any, punto: AbstractControl){
    //Asigna el valor del formulario a la variable subPuntoNuevo. titulo a titulo y descripcion a descripcion
    this.subPuntoNuevo.titulo = form.titulo;
    this.subPuntoNuevo.descripcion = form.descripcion;
    
    const subpuntosArray = punto.get('subpuntos') as FormArray;
    subpuntosArray.push(this.fb.group({
      id: [this.subPuntoNuevo.id],
      orden: [this.subPuntoNuevo.orden],
      titulo: [this.subPuntoNuevo.titulo, Validators.required],
      descripcion: [this.subPuntoNuevo.descripcion],
    }));
    //Crea un nuevo subPuntoNuevo estandar y lo asigna a la variable subPuntoNuevo
    this.subPuntoNuevo = new Subpunto(0, 0, '', '');
    this.modalCrearPuntoVisible = false;
  }
  
  
  editarPunto(index: number): void {
    this.editingPuntoIndex = index;
  }



  eliminarPunto(punto: AbstractControl): void {
    // Obtener la instancia del FormArray que contiene los puntos
    const puntos = this.form.get('puntos') as FormArray;

    // Buscar el índice del punto en el FormArray
    const index = puntos.controls.findIndex(x => x === punto);

    // Si el punto existe en el array (index no es -1), eliminarlo
    if (index !== -1) {
      puntos.removeAt(index);

      // Actualizar el orden de los puntos posteriores
      for (let i = index; i < puntos.length; i++) {
        // Asegurarse de que el control 'orden' existe antes de intentar acceder a su valor
        const ordenControl = puntos.at(i).get('orden');
        if (ordenControl !== null) {
          let ordenActual = ordenControl.value;
          ordenControl.setValue(ordenActual - 1);
        }
      }
    }
  }



  toggleCarnetInput(index: number) {
    const invitado = this.invitados.at(index);
    if (invitado.get('esMilitante')?.value) {
      invitado.get('numeroCarnet')?.setValidators([Validators.required]);
    } else {
      invitado.get('numeroCarnet')?.clearValidators();
    }
    invitado.get('numeroCarnet')?.updateValueAndValidity();
  }

  private convertToLocaleDateTime(fecha: string): string {
    // Primero, parseamos la fecha del formato dd/MM/yyyy
    let parsedDate = parse(fecha, 'dd/MM/yyyy HH:mm', new Date());

    // Si parsedDate es inválido, se vuelve a parsear con el formato yyyy-MM-ddTHH:mm:ss
    if (isNaN(parsedDate.getTime())) {
      parsedDate = parse(fecha, "yyyy-MM-dd'T'HH:mm:ss", new Date());
    }
    // Luego, la convertimos al formato deseado YYYY-MM-DDTHH:mm
    // Aquí, asumimos que quieres mantener la hora como 00:00 si no se especifica
    return format(parsedDate, "yyyy-MM-dd'T'HH:mm");
  }

  public cancelar(): void {
    this.cerrar.emit();
  }

  public aceptar(): void {

    if (this.edita) {
      Object.keys(this.form.controls).forEach(key => {
        const control = this.form.get(key);
        if (control) {
          this.setNullIfPristine(control);
        }
      });
    }

    this.confirmar.emit(this.form.getRawValue());
  }
  setNullIfPristine = (control: AbstractControl) => {
    if (control instanceof FormGroup || control instanceof FormArray) {
      this.eliminarInvitadosNoModificados(control);
    } else if (control.pristine) {
      control.setValue(null);
    }
  };

  getLowercaseLetterFromNumber(number: number): string {
    let result = '';
    while (number > 0) {
      // Decrementa el número por 1 para hacerlo compatible con el índice base 0 (0-25)
      number--;
      // Encuentra el residuo, que determinará la letra actual
      let remainder = number % 26;
      // Convierte el residuo a una letra. 97 es el código ASCII de 'a'
      let letter = String.fromCharCode(remainder + 97);
      // Prepende la letra actual al resultado
      result = letter + result;
      // Divide el número por 26 para pasar al siguiente "dígito"
      number = Math.floor(number / 26);
    }
    return result;
  }


}
function invitadoValidator(control: AbstractControl): ValidationErrors | null {
  const invitados = control.value as Invitado[];
  for (let invitado of invitados) {
    if (invitado.esMilitante) {
      if (!invitado.numeroCarnet || invitado.numeroCarnet === '') {
        return { 'numeroCarnetRequerido': true };
      } else if (!invitado.numeroCarnet.match('[0-9]{1,4}')) {
        return { 'numeroCarnetInvalido': true };
      }
    } else {
      if (!invitado.nombre || invitado.nombre === '' || !invitado.email || invitado.email === '') {
        return { 'nombreEmailRequeridos': true };
      }
    }
  }
  return null;
}
function minArrayLengthValidator(min: number) {
  return (c: AbstractControl): {[key: string]: any} | null => {
    if (c.value && c.value.length >= min) {
      return null; // Validación exitosa
    }
    return { minArrayLength: { valid: false } }; // Validación fallida
  };
}
