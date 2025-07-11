import { Component, OnInit } from '@angular/core';
import { TranslateService } from "@ngx-translate/core";
import { SupportedLanguages } from './models/SupportedLanguages';
import { UserPreferencesService } from './shared/services/user-preferences.service';
import { SidebarComponent } from "./core/sidebar/sidebar.component";

import { RouterModule } from '@angular/router';
import { AuthService } from './shared/services/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  standalone: true,
  imports: [SidebarComponent, RouterModule]
})
export class AppComponent implements OnInit {
  title = 'RedRoja';

  constructor(private userPreferencesService: UserPreferencesService,
    private translate: TranslateService,
    private auth: AuthService) { }

  ngOnInit(): void {
    this.setupLanguage();
  }

  private setupLanguage() {
    const supportedLanguages = Object.values(SupportedLanguages);
    const sessionLang = this.userPreferencesService.getPreferredLanguage();
    const browserLang = navigator.language.split('-')[0]; // Obtiene el código de idioma del navegador

    // Determina el idioma por defecto
    const defaultLang = supportedLanguages.includes(browserLang as SupportedLanguages) ? browserLang : SupportedLanguages.Castellano;

    // Usa sessionStorage si está disponible, si no, usa el idioma del navegador o el idioma por defecto
    const selectedLanguage = sessionLang || defaultLang;

    // Establece el idioma en el servicio de traducción
    this.translate.setDefaultLang(selectedLanguage);
    this.translate.use(selectedLanguage);

    this.userPreferencesService.setPreferredLanguage(selectedLanguage);
  }

  // Método para cambiar el idioma
  changeLanguage(language: string): void {
    if (Object.values(SupportedLanguages).includes(language as SupportedLanguages)) {
      this.userPreferencesService.setPreferredLanguage(language);
      this.translate.use(language);
    }
  }

  estaAuntentificado() {
    return this.auth.isAuthenticated();
  }
}
