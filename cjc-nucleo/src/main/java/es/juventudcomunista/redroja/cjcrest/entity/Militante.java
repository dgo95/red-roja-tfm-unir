package es.juventudcomunista.redroja.cjcrest.entity;


import es.juventudcomunista.redroja.cjcrest.security.crypto.AesGcmConverter;
import lombok.*;
import es.juventudcomunista.redroja.cjccommonutils.enums.Organizacion;
import es.juventudcomunista.redroja.cjccommonutils.enums.Sexo;
import jakarta.persistence.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "militante")
@ToString(exclude = {"comitesBase", "materiales"})
@EqualsAndHashCode(exclude = {"comitesBase", "materiales"})
@Builder
public class Militante {

    //Id de la base tabla de base de datos concreta
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //Id único e identificativo del militante en toda la aplicación
    @Column(name = "militante_id",nullable = false,unique = true,updatable = false)
    private String militanteId;
    @PrePersist
    public void asignarUsuarioId() {
        // Genera un UUID como identificador de usuario
        this.militanteId =  UUID.randomUUID().toString();
    }

    @Column(name = "apellido", nullable = false)
    private String apellido;

    @Column(name = "apellido2")
    private String apellido2;
    
    @Enumerated(EnumType.STRING)
    private Organizacion organizacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "direccion_id", referencedColumnName = "id")
    private Direccion direccion;

    @Convert(converter = AesGcmConverter.class)
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "estudiante")
    private Boolean estudiante;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "numero_carnet", unique = true)
    private String numeroCarnet;

    @Enumerated(EnumType.STRING)
    @Column(name = "sexo", nullable = false)
    private Sexo sexo;

    @Convert(converter = AesGcmConverter.class)
    @Column(name = "telefono", nullable = false, unique = true)
    private String telefono;

    @Column(name = "trabajador")
    private Boolean trabajador;

    @ManyToMany
    @JoinTable(
            name = "militante_comite_base",
            joinColumns = @JoinColumn(name = "militante_id"),
            inverseJoinColumns = @JoinColumn(name = "comite_base_id")
    )
    private Set<ComiteBase> comitesBase = new HashSet<>();

    @Column(name = "sindicado")
    private Boolean sindicado;
    
    @Column(name = "premilitante")
    private Boolean premilitante;

    @OneToMany(mappedBy = "militante", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<MilitanteRolComite> responsabilidades = new HashSet<>();

    @OneToMany(mappedBy = "militante", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<AsignacionMaterial> materiales;

    @OneToMany(mappedBy = "militante", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MilitanteHabilidad> militanteHabilidades = new HashSet<>();

    @OneToMany(mappedBy = "militante", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MilitanteIdioma> idiomas = new HashSet<>();

    @OneToOne(mappedBy = "militante", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private FichaMovilidad fichaMovilidad;
}
