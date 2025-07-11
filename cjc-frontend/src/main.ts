import { bootstrapApplication } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import { AppComponent } from './app/app.component';
import { routes } from './app.routes';
import { ReactiveFormsModule } from '@angular/forms';
import { importProvidersFrom } from '@angular/core';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { ToastrModule } from 'ngx-toastr';
import { NgSelectModule } from '@ng-select/ng-select';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';

// Importa servicios globales
import { AuthService } from './app/shared/services/auth.service';
import { UserPreferencesService } from './app/shared/services/user-preferences.service';
import { OAuthModule } from 'angular-oauth2-oidc';
import { authInterceptor } from './app/shared/services/auth-interceptor.interceptor';

import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { providePrimeNG } from 'primeng/config';
import Aura from '@primeng/themes/aura';
import Lara from '@primeng/themes/lara';

// Carga de traducciones
export function HttpLoaderFactory(http: HttpClient): TranslateHttpLoader {
  return new TranslateHttpLoader(http);
}

// Configuración de la aplicación
bootstrapApplication(AppComponent, {
  providers: [
    provideAnimationsAsync(),
    providePrimeNG({
      theme: { preset: Lara,
        options: {
          darkModeSelector: false,
        }
       }                     // Aura, Lara, Material o Nora
    }),
    provideRouter(routes),
    provideHttpClient(
      withInterceptors([authInterceptor]) // Usamos el interceptor funcional
    ),
    provideAnimations(),
    importProvidersFrom(
      ReactiveFormsModule,
      MatSidenavModule,
      MatListModule,
      MatIconModule,
      MatToolbarModule,
      NgbModule,
      FontAwesomeModule,
      NgSelectModule,
      OAuthModule.forRoot(),
      ToastrModule.forRoot(),
      TranslateModule.forRoot({
        loader: {
          provide: TranslateLoader,
          useFactory: HttpLoaderFactory,
          deps: [HttpClient],
        },
      })
    ),
    // Proveedores globales personalizados
    AuthService,
    UserPreferencesService,
  ],
}).catch((err) => console.error(err));
