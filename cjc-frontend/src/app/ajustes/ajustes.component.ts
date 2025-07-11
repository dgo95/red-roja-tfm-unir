import { Component, ViewChild } from '@angular/core';
import { CambiarContrasenaComponent } from './cambiar-contrasena/cambiar-contrasena.component';
import { TranslateModule } from '@ngx-translate/core';
import { IdiomaComponent } from './idioma/idioma.component';

@Component({
    selector: 'app-ajustes',
    templateUrl: './ajustes.component.html',
    styleUrls: ['./ajustes.component.scss'],
    standalone: true,
    imports: [CambiarContrasenaComponent, IdiomaComponent, TranslateModule]
})
export class AjustesComponent {
  @ViewChild('cambiarContrasena') cambiarContrasenaComponent!: CambiarContrasenaComponent;

  resetForm() {
    if (this.cambiarContrasenaComponent) {
      this.cambiarContrasenaComponent.resetForm();
    }
  }
}
