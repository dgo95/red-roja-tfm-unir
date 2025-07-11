import { CommonModule } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  Input,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import {
  NonNullableFormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import {
  debounceTime,
  distinctUntilChanged,
  finalize,
  Subject,
  takeUntil,
  tap,
} from 'rxjs';
import {
  MessageService,
  ConfirmationService,
  MenuItem,
} from 'primeng/api';
import { TableModule } from 'primeng/table';
import { DropdownModule } from 'primeng/dropdown';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';
import { DialogModule } from 'primeng/dialog';
import { MultiSelectModule } from 'primeng/multiselect';
import { MenuModule } from 'primeng/menu';
import { SplitButtonModule } from 'primeng/splitbutton';
import { InputGroupModule } from 'primeng/inputgroup';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { InputGroupAddonModule } from 'primeng/inputgroupaddon';
import { TooltipModule } from 'primeng/tooltip';
import { SelectModule } from 'primeng/select';
import { DatePickerModule } from 'primeng/datepicker';
import { ToastModule } from 'primeng/toast';
import { ConfirmDialogModule } from 'primeng/confirmdialog';

import { DocumentosService } from '../documentos.service';
import {
    ActualizarDocumentoDTO,
  DocumentoRecibidoDTO,
  NuevoDocumentoDTO,
} from '../documento.dto';
import {
  NivelDocumento,
  NivelDocumentoLabel,
  Confidencialidad,
  ConfidencialidadLabel,
  TipoDocumento,
  TipoDocumentoLabel,
  Categoria,
  CategoriaLabel,
} from '../documento.enums';
import { DocumentoUploadModalComponent } from './documento-upload-modal.component/documento-upload-modal.component';
import { DocumentoEditModalComponent } from './documento-edit-modal.component/documento-edit-modal.component';
import { InputTextModule } from 'primeng/inputtext';
import { DocumentoViewModalComponent } from './document-view-modal.component/documento-view-modal.component';

@Component({
  selector: 'app-documentos-tab',
  standalone: true,
  imports: [
    /* Angular */
    CommonModule,
    ReactiveFormsModule,

    /* PrimeNG */
    TableModule,
    DropdownModule,
    ButtonModule,
    TagModule,
    DialogModule,
    MultiSelectModule,
    MenuModule,
    SplitButtonModule,
    InputGroupModule,
    InputTextModule,
    IconFieldModule,
    InputIconModule,
    InputGroupAddonModule,
    TooltipModule,
    SelectModule,
    DatePickerModule,
    ToastModule,
    ConfirmDialogModule,

    /* Extras */
    DocumentoUploadModalComponent,
    DocumentoEditModalComponent,
    DocumentoViewModalComponent,
  ],
  providers: [MessageService, ConfirmationService],
  templateUrl: './documentos-tab.component.html',
  styleUrls: ['./documentos-tab.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DocumentosTabComponent implements OnInit, OnDestroy {
  /* ---------------- Inputs ---------------- */
  @Input({ required: true }) colectivoId!: number;
  @Input() esComite = false;
  @Input() descendientes: { id: number }[] = [];

  /* --------------- ViewChild -------------- */
  @ViewChild('fileUploader') fileUploader: any;

  /* ------------- Enumeraciones ------------- */
  readonly niveles = this.enumToOptions(NivelDocumento, NivelDocumentoLabel);
  readonly confidencialidades = this.enumToOptions(
    Confidencialidad,
    ConfidencialidadLabel
  );
  readonly tipos = this.enumToOptions(TipoDocumento, TipoDocumentoLabel);
  readonly categorias = this.enumToOptions(Categoria, CategoriaLabel);

  /* --------------- Formularios ------------- */
  readonly filtrosForm = this.fb.group({
    searchText: [''],
    nivel: [null as NivelDocumento | null],
    confidencialidad: [null as Confidencialidad | null],
    tipo: [[] as TipoDocumento[]],
    categoria: [[] as Categoria[]],
  });

  /* -------------- Estado view -------------- */
  documentos: DocumentoRecibidoDTO[] = [];
  totalRecords = 0;
  loadingDocumentos = false;

  mostrarDialogoSubir = false;
  mostrarDialogoVer = false;

  /* ------------- Otras props -------------- */
  private readonly destroy$ = new Subject<void>();
  mostrarDialogoEditar = false;
  documentoEditando: DocumentoRecibidoDTO | null = null;
  uuid?: string;
  private splitMenuCache = new Map<string, MenuItem[]>();

  /* --------------- Paginación -------------- */
  page = 0;
  rows = 10;
  sortField?: string;
  sortOrder: 1 | -1 | undefined;

  /* --------------- Constructor ------------- */
  constructor(
    private readonly fb: NonNullableFormBuilder,
    private readonly documentosService: DocumentosService,
    private readonly message: MessageService,
    private readonly confirm: ConfirmationService
  ) {}

  /* ----------------- Ciclo ----------------- */
  ngOnInit(): void {
    /* recarga al cambiar filtros */
    this.filtrosForm.valueChanges
      .pipe(
        debounceTime(250),
        distinctUntilChanged(),
        tap(() => {
          this.page = 0;
          this.cargarDocumentos();
        }),
        takeUntil(this.destroy$)
      )
      .subscribe();

    this.cargarDocumentos();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /* --------- Métodos públicos UI ---------- */
  abrirDialogoSubir(): void {
    this.mostrarDialogoSubir = true;
  }

  abrirDialogoVer(doc: DocumentoRecibidoDTO): void {
    this.uuid = doc.id;
    this.mostrarDialogoVer = true;
  }

  abrirDialogoEditar(doc: DocumentoRecibidoDTO) {
    this.documentoEditando = doc;
    this.mostrarDialogoEditar = true;
  }
  
  onActualizarDocumento(dto: ActualizarDocumentoDTO) {
    if (!this.documentoEditando) return;
    this.loadingDocumentos = true;
    this.documentosService
        .editarDocumento(this.documentoEditando.id.toString(), dto)
        .pipe(finalize(()=>this.loadingDocumentos=false), takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            this.message.add({severity:'success',summary:'Actualizado',detail:'Cambios guardados'});
            this.mostrarDialogoEditar = false;
            this.cargarDocumentos();
          },
          error: () => this.message.add({severity:'error',summary:'Error',detail:'No se pudo actualizar'})
        });
  }

  eliminarDocumento(doc: DocumentoRecibidoDTO): void {
    this.confirm.confirm({
      header: 'Confirmar eliminación',
      message: '¿Eliminar definitivamente el documento?',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sí, eliminar',
      rejectLabel: 'Cancelar',
      acceptButtonStyleClass: 'p-button-danger',
      accept: () => {
        this.loadingDocumentos = true;
        this.documentosService
          .eliminarDocumento(doc.id.toString())
          .pipe(
            finalize(() => (this.loadingDocumentos = false)),
            takeUntil(this.destroy$)
          )
          .subscribe({
            next: () => {
              this.message.add({
                severity: 'success',
                summary: 'Eliminado',
                detail: 'Documento eliminado',
              });
              this.cargarDocumentos();
            },
            error: () =>
              this.message.add({
                severity: 'error',
                summary: 'Error',
                detail: 'No se pudo eliminar',
              }),
          });
      },
    });
  }

  /* -------------- Tabla (lazy) ------------- */
  onTableLazyLoad(event: any): void {
    this.page = event.first / event.rows;
    this.rows = event.rows;
    if (event.multiSortMeta?.length) {
      this.sortField = event.multiSortMeta[0].field;
      this.sortOrder = event.multiSortMeta[0].order;
    } else {
      this.sortField = undefined;
      this.sortOrder = undefined;
    }
    this.cargarDocumentos();
  }

  /* -------- Subir (modal externo) --------- */
  onSubidaAceptar(dto: NuevoDocumentoDTO): void {
    dto.propietario = this.esComite ? 'C' + this.colectivoId : 'B' + this.colectivoId;
    this.documentosService.subirDocumento(dto).subscribe({
      next: () => {
        this.mostrarDialogoSubir = false;
        this.cargarDocumentos();
        this.message.add({
          severity: 'success',
          summary: 'Éxito',
          detail: 'Documento subido correctamente',
        });
      },
      error: () =>
        this.message.add({
          severity: 'error',
          summary: 'Error',
          detail: 'No se pudo subir',
        }),
    });
  }

  /* --------------- Helpers ---------------- */
  getSplitMenuItemsCached(doc: DocumentoRecibidoDTO): MenuItem[] {
    if (!doc.id) return [];
    if (!this.splitMenuCache.has(doc.id)) {
      this.splitMenuCache.set(doc.id, this.buildSplitMenu(doc));
    }
    return this.splitMenuCache.get(doc.id)!;
  }

  severidadConfidencialidad(c: string): string {
    const valor = Confidencialidad[c as keyof typeof Confidencialidad];
    return (
      {
        [Confidencialidad.PUBLICO]: 'success',
        [Confidencialidad.INTERNO]: 'info',
        [Confidencialidad.RESTRINGIDO]: 'warning',
        [Confidencialidad.CONFIDENCIAL]: 'danger',
      }[valor] ?? 'secondary'
    );
  }

  getNombreSinExtension(titulo: string): string {
    const idx = titulo.lastIndexOf('.');
    return idx === -1 ? titulo : titulo.substring(0, idx);
  }

  getExtension(titulo: string): string {
    const idx = titulo.lastIndexOf('.');
    return idx === -1 || idx === titulo.length - 1
      ? 'Otro'
      : titulo.substring(idx + 1).toLowerCase();
  }

  /* -------------- Privados --------------- */
  private cargarDocumentos(): void {
    this.loadingDocumentos = true;
    this.splitMenuCache.clear();

    const ids = [
      this.esComite ? 'C' + this.colectivoId : 'B' + this.colectivoId,
      ...this.descendientes.map((d) => d.id.toString()),
    ];

    const {
      searchText,
      nivel,
      confidencialidad,
      tipo,
      categoria,
    } = this.filtrosForm.getRawValue();

    this.documentosService
      .getDocumentos({
        page: this.page,
        rows: this.rows,
        filtroNivel: nivel != null ? NivelDocumento[nivel] : undefined,
        filtroConfidencialidad:
          confidencialidad != null ? Confidencialidad[confidencialidad] : undefined,
        filtroTipo: tipo?.length ? tipo.map((t) => TipoDocumento[t]) : undefined,
        filtroCategoria: categoria?.length
          ? categoria.map((c) => Categoria[c])
          : undefined,
        searchText: searchText?.trim(),
        sortField: this.sortField
          ?.replace('titulo', 'nombreOriginal')
          .replace('fecha', 'fechaSubida'),
        sortOrder: this.sortOrder,
        ids,
      })
      .pipe(
        finalize(() => (this.loadingDocumentos = false)),
        takeUntil(this.destroy$)
      )
      .subscribe({
        next: ({ data, totalRecords }) => {
          this.documentos = data;
          this.totalRecords = totalRecords;
        },
        error: () => {
          this.documentos = [];
          this.totalRecords = 0;
        },
      });
  }

  private buildSplitMenu(doc: DocumentoRecibidoDTO): MenuItem[] {
    return [
      {
        label: 'Editar',
        icon: 'pi pi-pencil',
        command: () => this.abrirDialogoEditar(doc),
      },
      {
        label:
          doc.confidencialidad !== Confidencialidad.CONFIDENCIAL
            ? 'Descargar'
            : 'Descarga no permitida',
        icon:
          doc.confidencialidad !== Confidencialidad.CONFIDENCIAL
            ? 'pi pi-download'
            : 'pi pi-lock',
        disabled: true,
        command: () => {},
      },
      {
        label: 'Eliminar',
        icon: 'pi pi-trash',
        command: () => this.eliminarDocumento(doc),
      },
    ];
  }

  private enumToOptions<E>(
    e: any,
    labels: Record<number, string>
  ): { label: string; value: E }[] {
    return (Object.values(e).filter((v) => typeof v === 'number') as E[]).map(
      (value) => ({ label: labels[value as unknown as number], value })
    );
  }
}
