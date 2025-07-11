import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { UserPreferencesService } from 'src/app/shared/services/user-preferences.service';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { AppComponent } from 'src/app/app.component';
import { SupportedLanguages } from 'src/app/models/SupportedLanguages';
import { CommonModule, NgClass } from '@angular/common';
import { NgIcon, provideIcons } from '@ng-icons/core';
import { bootstrapInfoCircle, bootstrapCheckCircle } from '@ng-icons/bootstrap-icons';
import { of } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { AuthService } from '../shared/services/auth.service';
import { trigger, state, style, transition, animate, query, sequence } from '@angular/animations';


@Component({
    selector: 'app-auth',
    templateUrl: './auth.component.html',
    styleUrls: ['./auth.component.scss'],
    standalone: true,
    imports: [
        NgClass,
        NgIcon,
        FormsModule,
        ReactiveFormsModule,
        TranslateModule,
        CommonModule
    ],
    providers: [provideIcons({ bootstrapInfoCircle, bootstrapCheckCircle })],
})
export class AuthComponent implements OnInit {
    // Determina el estado (login / reset)
    public mode: 'login' | 'reset' = 'login';

    // Formularios
    loginForm!: FormGroup;
    loginSubmitted = false;
    loginFailed = false;
    loginErrorServidor = false;

    carnetForm!: FormGroup;
    nuevaPassForm!: FormGroup;
    resetSubmitted = false;
    resetMandado = false;
    resetErrorServidor = false;
    isLoading = false;
    primeraParte = true;
    token: string = "";

    constructor(
        private formBuilder: FormBuilder,
        private auth: AuthService,
        private router: Router,
        private translate: TranslateService,
        private appComponent: AppComponent,
        private userPreferencesService: UserPreferencesService
    ) { }

    ngOnInit() {
        // Inicializar formulario de inicio de sesión
        this.loginForm = this.formBuilder.group({
            carnet: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(4), Validators.pattern('[0-9]{1,4}')]],
            password: ['', [Validators.required]]
        });

        // Inicializar formularios de restablecer contraseña
        this.carnetForm = this.formBuilder.group({
            carnet: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(4), Validators.pattern('[0-9]{1,4}')]]
        });

        this.nuevaPassForm = this.formBuilder.group({
            password: ['', [Validators.required]],
            confirmPassword: ['', [Validators.required, this.matchPassword]]
        });
    }

    // Getters para facilitar el acceso a los controles del formulario
    get loginControls() { return this.loginForm.controls; }
    get carnetControls() { return this.carnetForm.controls; }
    get nuevaPassControls() { return this.nuevaPassForm.controls; }

    // Manejar el envío del formulario de inicio de sesión
    onLoginSubmit() {
        this.loginSubmitted = true;
        this.loginFailed = false;
        this.loginErrorServidor = false;

        if (this.loginForm.invalid) {
            return;
        }

        const carnet = this.loginForm.value.carnet;
        const password = this.loginForm.value.password;

        let carnetN = carnet;
        if (carnet.length < 4) {
            carnetN = carnet.padStart(4, '0');
            this.loginForm.patchValue({ carnet: carnetN });
        }

        this.auth.login(carnetN, password)
            .pipe(
                switchMap((res) => {
                    const token = res.data.token;
                    const backendLang = res.data.lang;

                    return this.auth.getAdditionalMilitanteData(token).pipe(
                        switchMap((militanteData: any) => {
                            this.userPreferencesService.setOrganizacion('CJC');
                            this.auth.saveMilitanteData(militanteData.data, token);

                            const supportedLanguages = Object.values(SupportedLanguages);
                            if (supportedLanguages.includes(backendLang as SupportedLanguages) && backendLang !== this.translate.currentLang) {
                                this.appComponent.changeLanguage(backendLang);
                            }

                            return of(null);
                        })
                    );
                })
            )
            .subscribe({
                next: () => {
                    this.router.navigate(['/inicio']);
                },
                error: (err) => {
                    if (err.status === 0) {
                        this.loginErrorServidor = true;
                    } else if (err.status === 400) {
                        this.loginFailed = true;
                    }
                }
            });
    }

    // Enviar email para restablecer contraseña
    enviarEmail() {
        this.resetSubmitted = true;
        if (this.carnetForm.invalid) {
            return;
        }
        this.isLoading = true;

        let carnetN = this.carnetForm.value.carnet;
        if (carnetN.length < 4) {
            carnetN = "0".repeat(4 - carnetN.length) + carnetN;
            this.carnetForm.patchValue({ carnet: carnetN });
        }

        this.auth.mandaEmail(carnetN).subscribe({
            next: (data) => {
                this.isLoading = false;
                this.resetMandado = true;
            },
            error: (error) => {
                console.error('Error al enviar el correo:', error);
                this.isLoading = false;
                this.resetErrorServidor = true;
            }
        });
    }

    // Restablecer la contraseña
    restablecerPass() {
        this.resetSubmitted = true;
        if (this.nuevaPassForm.invalid) {
            return;
        }
        this.isLoading = true;

        const password = this.nuevaPassForm.value.password;
        this.auth.restablecerPass(this.token, password).subscribe(
            data => {
                this.isLoading = false;
                this.resetMandado = true;
                setTimeout(() => {
                    this.router.navigate(['/login']);
                }, 5000);
            },
            error => {
                this.isLoading = false;
                this.resetErrorServidor = true;
            }
        );
    }

    // Cancelar y volver al inicio de sesión
    cancelar() {
        this.router.navigate(['/login']);
    }

    // Cambia a reset
    switchToReset(): void {
        this.mode = 'reset';
    }

    // Cambia a login
    switchToLogin(): void {
        this.mode = 'login';
    }
    // Validación personalizada para confirmar la contraseña
    matchPassword(control: FormControl) {
        const password = control.root.get('password');
        return password && control.value !== password.value ? { matchPassword: true } : null;
    }
}
