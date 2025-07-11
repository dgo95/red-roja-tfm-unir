import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Toast, ToastrService } from 'ngx-toastr';
import { AuthService } from 'src/app/shared/services/auth.service';
import { TranslateModule } from '@ngx-translate/core';


@Component({
    selector: 'app-cambiar-contrasena',
    templateUrl: './cambiar-contrasena.component.html',
    styleUrls: ['./cambiar-contrasena.component.scss'],
    standalone: true,
    imports: [FormsModule, ReactiveFormsModule, TranslateModule]
})
export class CambiarContrasenaComponent {
  changePasswordForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private toastr: ToastrService
  ) {
    this.changePasswordForm = this.fb.group({
      currentPassword: ['', [Validators.required]],
      newPassword: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', [Validators.required]]
    }, { validator: this.passwordMatchValidator });
  }

  passwordMatchValidator(form: FormGroup) {
    return form.get('newPassword')?.value === form.get('confirmPassword')?.value
      ? null : { mismatch: true };
  }

  resetForm() {
    this.changePasswordForm.reset();
  }

  onSubmit() {
    //TODO
    /**if (this.changePasswordForm.valid) {
      const militanteId = this.authService.getUserData().militanteId;
      const changePasswordDTO = {
        currentPassword: this.changePasswordForm.get('currentPassword')?.value,
        newPassword: this.changePasswordForm.get('newPassword')?.value
      };

      this.authService.cambiarPassword(militanteId, changePasswordDTO).subscribe(
        response => {
          this.toastr.success('Contraseña cambiada exitosamente.', 'Guardado con éxito');
        },
        error => {
          this.toastr.error('Error al cambiar la contraseña.', 'Error');

        }
      );
    }*/
  }
}
