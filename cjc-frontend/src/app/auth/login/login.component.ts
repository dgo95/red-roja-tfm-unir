import { UserPreferencesService } from 'src/app/shared/services/user-preferences.service';
import { AuthService } from '../../shared/services/auth.service';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { TranslateService, TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';
import { switchMap, tap } from 'rxjs/operators';
import { AppComponent } from 'src/app/app.component';
import { SupportedLanguages } from 'src/app/models/SupportedLanguages';
import { NgClass } from '@angular/common';

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.scss'],
    standalone: true,
    imports: [FormsModule, ReactiveFormsModule, NgClass, TranslateModule]
})
export class LoginComponent implements OnInit {
  //Form Validables
  registerForm: any = FormGroup;
  submitted = false;
  loginFailed = false;
  errorServidor = false;
  constructor(private formBuilder: FormBuilder, private auth: AuthService, private router: Router,
    private translate: TranslateService,
    private appComponent: AppComponent,
  private userPreferencesService: UserPreferencesService) {
  }
  //Add user form actions
  get f() { return this.registerForm.controls; }

  onSubmit() {
    this.submitted = true;
    this.loginFailed = false;
    this.errorServidor = false;
    // si el formulario es invalido para
    if (this.registerForm.invalid) {
      return;
    }
    // Si el carnet tiene menos de 4 números, se completa con ceros a la izquierda
    const carnet = this.registerForm.value.carnet;
    if (carnet.length < 4) {
      const carnetN = carnet.padStart(4, '0');
      this.registerForm.patchValue({ carnet: carnetN });
    }

    this.auth.login(this.registerForm.value.carnet, this.registerForm.value.password)
      .pipe(
        switchMap((res) => {
          // Guardar solo el token
          const token = res.data.token;
          const backendLang = res.data.lang;

          // Realizar la segunda llamada a la API para obtener los datos del militante
          return this.auth.getAdditionalMilitanteData(token).pipe(
            switchMap((militanteData: any) => {


              //TODO Cuando se pueda seleccioanar en que orginizacion se quiere hacer el login borrar
              // Guardar la organización en sessionStorage
              this.userPreferencesService.setOrganizacion('CJC');

              // Guardar los datos del militante y el token
              this.auth.saveMilitanteData(militanteData.data, token);

              
              const supportedLanguages = Object.values(SupportedLanguages);
              if (supportedLanguages.includes(backendLang as SupportedLanguages) && backendLang !== this.translate.currentLang) {
                this.appComponent.changeLanguage(backendLang);
              }

              // Este valor se ignora, pero debes retornar algo para cumplir con el contrato de switchMap
              return of(null);
            })
          );
        })
      )
      .subscribe({
        next: () => {
          // Redirigir al usuario
          this.router.navigate(['/inicio']);
        },
        error: (err) => {
          if (err.status === 0) {
            this.errorServidor = true;
          } else if (err.status === 400) {
            this.loginFailed = true;
          }
        }
      });
  }
  ngOnInit() {
    //Add User form validations
    //carnet validation: tienen que ser entre 1 y 4 numeros regex: [0-9]{1,4}
    this.registerForm = this.formBuilder.group({
      carnet: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(4), Validators.pattern('[0-9]{1,4}')]],
      password: ['', [Validators.required]]
    });
  }
}
