// documento-view-modal.component.ts
import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnDestroy,
  Output,
  SimpleChanges,
  ViewChild,
  ElementRef,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { DialogModule } from 'primeng/dialog';
import { NgxExtendedPdfViewerModule } from 'ngx-extended-pdf-viewer';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { Subject, takeUntil } from 'rxjs';

/*  ⬇  librerías de preview local  */
import { renderAsync as renderDocx } from 'docx-preview';
import * as XLSX from 'xlsx';
import { DocumentosService } from '../../documentos.service';

type Preview = 'pdf' | 'docx' | 'xlsx' | 'image' | 'text' | 'none';

@Component({
  selector: 'app-documento-view-modal',
  standalone: true,
  imports: [CommonModule, DialogModule, NgxExtendedPdfViewerModule],
  templateUrl: './documento-view-modal.component.html',
  styleUrls: ['./documento-view-modal.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DocumentoViewModalComponent implements OnChanges, OnDestroy {
  /* ---------------- Inputs ---------------- */
  @Input() visible = false;
  @Input() uuid!: string;
  @Output() cancelar = new EventEmitter<void>();

  /* ---------------- Estado ---------------- */
  isLoading = false;
  error: string | null = null;
  previewType: Preview = 'none';

  fileUrlSafe!: SafeResourceUrl;   // para PDF / imágenes
  textContent?: string;            // para txt
  xlsxHtml?: string;               // tabla para xlsx

  @ViewChild('docxContainer', { static: false }) docxContainer?: ElementRef<HTMLDivElement>;

  private objectUrl?: string;
  private readonly destroy$ = new Subject<void>();

  constructor(
    private readonly docs: DocumentosService,
    private readonly sanitizer: DomSanitizer,
    private readonly cd: ChangeDetectorRef
  ) {}

  /* ---------------------------------------- */
  ngOnChanges(ch: SimpleChanges): void {
    if (ch['visible'] && this.visible && this.uuid) {
      this.cargar();
    }
  }

  ngOnDestroy(): void {
    this.liberarUrl();
    this.destroy$.next();
    this.destroy$.complete();
  }

  /* --------- core --------- */
  private cargar(): void {
    this.reset();
    this.docs
      .obtenerDocumento(this.uuid)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: async ({ blob, mimeType }) => {
          try {
            if (mimeType.startsWith('application/pdf')) {
              this.fileUrlSafe = this.blobToSafeUrl(blob);
              this.previewType = 'pdf';

            } else if (
              mimeType === 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' ||
              mimeType === 'application/msword'
            ) {
              await this.renderDocx(blob);

            } else if (
              mimeType === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' ||
              mimeType === 'application/vnd.ms-excel'
            ) {
              await this.renderXlsx(blob);

            } else if (mimeType.startsWith('image/')) {
              this.fileUrlSafe = this.blobToSafeUrl(blob);
              this.previewType = 'image';

            } else if (mimeType === 'text/plain') {
              this.textContent = await blob.text();
              this.previewType = 'text';

            } else {
              this.previewType = 'none';
            }
          } catch (e) {
            console.error(e);
            this.error = 'Error al procesar el documento.';
          } finally {
            this.isLoading = false;
            this.cd.markForCheck();
          }
        },
        error: () => {
          this.error = 'No se pudo descargar el documento.';
          this.isLoading = false;
          this.cd.markForCheck();
        },
      });
  }

  /* --------- helpers --------- */
  private async renderDocx(blob: Blob): Promise<void> {
    this.previewType = 'docx';
    await this.cd.detectChanges(); // espera a que exista el container
    if (this.docxContainer) {
      this.docxContainer.nativeElement.innerHTML = ''; // limpia
      await renderDocx(blob, this.docxContainer.nativeElement, undefined, { inWrapper: false });
    }
  }

  private async renderXlsx(blob: Blob): Promise<void> {
    const buff = await blob.arrayBuffer();
    const wb = XLSX.read(buff, { type: 'array' });
    const sheet = wb.Sheets[wb.SheetNames[0]];
    this.xlsxHtml = XLSX.utils.sheet_to_html(sheet, { id: 'previewSheet' });
    this.previewType = 'xlsx';
  }

  private blobToSafeUrl(blob: Blob): SafeResourceUrl {
    this.liberarUrl();
    this.objectUrl = URL.createObjectURL(blob);
    return this.sanitizer.bypassSecurityTrustResourceUrl(this.objectUrl);
  }

  private liberarUrl(): void {
    if (this.objectUrl) {
      URL.revokeObjectURL(this.objectUrl);
      this.objectUrl = undefined;
    }
  }

  private reset(): void {
    this.isLoading = true;
    this.error = null;
    this.previewType = 'none';
    this.textContent = undefined;
    this.xlsxHtml = undefined;
    this.liberarUrl();
    this.cd.markForCheck();
  }

  /* p-dialog hide */
  handleHide(): void {
    this.liberarUrl();
    this.cancelar.emit();
  }
}
