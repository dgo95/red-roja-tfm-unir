import { Injectable } from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class UserPreferencesService {

    private readonly LANGUAGE_KEY = 'RedRojaPreferredLanguage';
    private readonly ORGANIZACION_KEY = 'RedRojaOrganizacion';

    constructor() { }

    // Guardar idioma preferido
    setPreferredLanguage(language: string): void {
        localStorage.setItem(this.LANGUAGE_KEY, language);
    }

    // Leer idioma preferido
    getPreferredLanguage(): string | null {
        return localStorage.getItem(this.LANGUAGE_KEY);
    }

    // Guardar organización en sesionstorage
    setOrganizacion(organizacion: string): void {
        sessionStorage.setItem(this.ORGANIZACION_KEY, organizacion);
    }

    // Leer organización de sesionstorage
    getOrganizacion(): string {
        return sessionStorage.getItem(this.ORGANIZACION_KEY) || 'CJC';
    }
}