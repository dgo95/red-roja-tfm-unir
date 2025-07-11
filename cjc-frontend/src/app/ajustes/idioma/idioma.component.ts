import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TranslateService, TranslateModule } from "@ngx-translate/core";
import { ToastrService } from 'ngx-toastr';
import { first } from 'rxjs';
import { AppComponent } from 'src/app/app.component';
import { Militante } from 'src/app/models/militante';
import { SupportedLanguages } from 'src/app/models/SupportedLanguages';
import { AuthService, MilitanteClaims } from 'src/app/shared/services/auth.service';
import { UserPreferencesService } from 'src/app/shared/services/user-preferences.service';

@Component({
    selector: 'app-idioma',
    templateUrl: './idioma.component.html',
    styleUrls: ['./idioma.component.scss'],
    standalone: true,
    imports: [FormsModule, ReactiveFormsModule, TranslateModule]
})
export class IdiomaComponent implements OnInit {
  idiomaForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private appComponent: AppComponent,
    private userPreferencesService: UserPreferencesService,
    private toastr: ToastrService,
    private translate: TranslateService
  ) {
    this.idiomaForm = this.fb.group({
      selectedLanguage: ['es'] // Valor por defecto
    });
  }

  ngOnInit(): void {
    const storedLanguage = this.userPreferencesService.getPreferredLanguage();
    const initialLanguage = storedLanguage || SupportedLanguages.Castellano;

    this.idiomaForm.patchValue({ selectedLanguage: initialLanguage });
  }

  onSubmit(): void {
    const lang: SupportedLanguages = this.idiomaForm.value.selectedLanguage;
    this.authService.user$.pipe(first()).subscribe((usuario: MilitanteClaims | null) => {
      if (!usuario) { return; }
      this.authService.cambiarIdioma(lang, usuario.militanteId).subscribe({
        next: () => {
          this.appComponent.changeLanguage(lang);
          this.toastr.success(
            this.translate.instant('ui.mensajes.idiomaCambiado'),
            this.translate.instant('ui.mensajes.exito')
          );
        },
        error: () =>
          this.toastr.error(
            this.translate.instant('ui.mensajes.errorCambioIdioma'),
            this.translate.instant('ui.mensajes.error')
          )
      });
    });
  }
  
}
