import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../shared/services/auth.service';
import { TranslateModule } from '@ngx-translate/core';
import { NgClass } from '@angular/common';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { bootstrapInfoCircle, bootstrapCheckCircle } from '@ng-icons/bootstrap-icons';



@Component({
  selector: 'app-restablecer-contrasena',
  templateUrl: './restablecer-contrasena.component.html',
  styleUrls: ['./restablecer-contrasena.component.scss'],
  standalone: true,
  imports: [FormsModule, ReactiveFormsModule, NgClass, TranslateModule, NgIcon],
  providers: [provideIcons({ bootstrapInfoCircle, bootstrapCheckCircle })]
})

export class RestablecerContrasenaComponent implements OnInit {
  //Form Validables
  nuevaPassForm: any = FormGroup;
  carnetForm: any = FormGroup;
  submitted = false;
  mandado = false;
  loginFailed = false;
  errorServidor = false;
  isLoading = false;
  primeraParte = true;
  token: string = "";
  constructor(private formBuilder: FormBuilder, private auth: AuthService, private router: Router) { }
  //Add user form actions
  get f() { return this.carnetForm.controls; }
  //Add user form actions
  get g() { return this.nuevaPassForm.controls; }


  enviarEmail() {
    this.submitted = true;
    //si hay error en el formulario, no hace nada
    if (this.carnetForm.invalid) {
      return;
    }
    this.isLoading = true;

    //si el carnet tiene menos de 4 numeros, se completa con ceros a la izquierda hasta que tenga 4 números
    if (this.carnetForm.value.carnet.length < 4) {
      let carnetN = this.carnetForm.value.carnet;
      //Se añade el string 0 tantas veces como ceros faltantes
      carnetN = "0".repeat(4 - carnetN.length) + carnetN;

      this.carnetForm.patchValue({
        carnet: carnetN
      });
    }
    // Obtiene el parámetro 'carnet' del formulario 'carnetForm' y lo envía al servicio 'auth.mandaEmail'
    this.auth.mandaEmail(this.carnetForm.value.carnet).subscribe({
      next: (data) => {
        
        this.isLoading = false;
        this.mandado = true;
      },
      error: (error) => {
        console.error('Error al enviar el correo:', error); // Log para errores
        this.isLoading = false;
        this.errorServidor = true;
      },
      complete: () => {
      }
    });

  }

  restablecerPass() {
    this.submitted = true;

    // Si hay error en el formulario, no hace nada
    if (this.nuevaPassForm.invalid) {
      return;
    }
    this.isLoading = true;

    // Coge el this.token y el parámetro newPassword del formulario nuevaPassForm y lo mandamos al servicio auth.restablecerPass
    this.auth.restablecerPass(this.token, this.nuevaPassForm.value.password).subscribe(
      data => {
        this.isLoading = false;
        this.mandado = true;

        // Espera 5 segundos y redirige a login
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 5000);

      },
      error => {
        this.isLoading = false;
        this.errorServidor = true;
      });
  }


  cancelar() {
    this.router.navigate(['/login']);
  }

  // Función de validación personalizada para comparar los valores de los input de password
  matchPassword(control: FormControl) {
    const password = control.root.get('password');

    return password && control.value !== password.value ? { matchPassword: true } : null;
  }
  ngOnInit() {
    this.token = this.router.routerState.snapshot.root.queryParams['token'];

    // si el token es null o this.auth.tokenValido(token) devuelve false, es que estamos en la primera par

    const observer = {
      next: () => {
        this.primeraParte = false;
        this.isLoading = false;
        //Add User form validations
        //carnet validation: tienen que ser entre 1 y 4 numeros regex: [0-9]{1,4}
        this.nuevaPassForm = this.formBuilder.group({
          password: ['', [Validators.required]],
          confirmPassword: ['', [Validators.required, this.matchPassword]]
        });
      },
      error: () => {
        this.primeraParte = true;
        this.isLoading = false;
        this.carnetForm = this.formBuilder.group({
          carnet: ['', [Validators.required]]
        });
      }
    };

    if (this.token == null) {
      observer.error();
    } else {
      this.auth.tokenValido(this.token).subscribe(observer);
    }
  }
}
