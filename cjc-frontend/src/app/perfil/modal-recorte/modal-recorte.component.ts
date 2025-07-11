// modal-recorte.component.ts
import { Component, Output, EventEmitter, Input } from '@angular/core';
import { ImageCroppedEvent, ImageCropperComponent } from 'ngx-image-cropper';

@Component({
  selector: 'app-modal-recorte',
  templateUrl: './modal-recorte.component.html',
  styleUrls: ['./modal-recorte.component.scss'],
  standalone: true,
  imports: [ImageCropperComponent]
})
export class ModalRecorteComponent {

  @Input() public imageChangedEvent: any = '';

  @Output() public cerrar = new EventEmitter<void>();
  @Output() public confirmar = new EventEmitter<any>();

  public croppedImage: any = '';


  loadImageFailed() {
    throw new Error('Method not implemented.');
  }
  imageLoaded() {
    throw new Error('Method not implemented.');
  }


  public imageCropped(event: ImageCroppedEvent) {
    this.croppedImage = event.blob;
  }

  public cancelar(): void {
    this.cerrar.emit();
  }

  public aceptar(): void {
    this.confirmar.emit(this.croppedImage);
  }
}
