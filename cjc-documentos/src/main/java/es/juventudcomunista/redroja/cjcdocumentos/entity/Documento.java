package es.juventudcomunista.redroja.cjcdocumentos.entity;

import es.juventudcomunista.redroja.cjccommonutils.enums.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.Set;

@Entity @Table(name = "documentos")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class Documento {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    @UuidGenerator
    private String uuid;

    @Column(name = "nombre_original", nullable = false)
    private String nombreOriginal;

    @Column(name = "nombre_fisico", nullable = false, unique = true)
    private String nombreFisico;

    private String extension;
    private String mimeType;
    private long tamanoBytes;
    private String checksumSha256;
    @Column(name = "iv_b64")
    private String ivB64;
    @Column(name = "dek_wrapped_b64")
    private String dekWrappedB64;


    @Enumerated(EnumType.STRING)
    private NivelDocumento nivel;

    @Enumerated(EnumType.STRING)
    private Confidencialidad confidencialidad;

    @Enumerated(EnumType.STRING)
    private TipoDocumento tipo;

    private String propietario;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "documento_categorias",
            joinColumns = @JoinColumn(name = "documento_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "categoria")
    private Set<Categoria> categorias;

    private Instant fechaSubida;

    private String rutaRelativa;

    // ─────────────── Auditoría ──────────────────────
    @CreatedBy                     // usuario que creó
    @Column(name = "creado_por", updatable = false)
    private String creadoPor;

    @CreatedDate                   // fecha creación
    @Column(name = "creado_en", updatable = false)
    private Instant creadoEn;

    @LastModifiedBy                // último que modificó
    @Column(name = "modificado_por")
    private String modificadoPor;

    @LastModifiedDate              // fecha última modificación
    @Column(name = "modificado_en")
    private Instant modificadoEn;

    @Version
    private Integer version;
}
