package es.juventudcomunista.redroja.cjcrest.entity;

import es.juventudcomunista.redroja.cjccommonutils.enums.Organizacion;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "comite")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString(exclude = {          // evita recursion en toString()
        "comiteDependiente",
        "subComites",
        "subComitesBase",
        "miembros",
        "responsabilidades",
        "materiales"
})
public class Comite {

    // ========== 1. Identificadores ==========
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** FK explícita que nos permite saber si hay padre SIN inicializar el proxy */
    @Column(name = "depende_de")          // <-- insertable/updatable por defecto = true
    private Integer comiteDependienteId;

    // ========== 2. Atributos de negocio ==========
    @Enumerated(EnumType.STRING)
    private Organizacion organizacion;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String sede;

    // ========== 3. Relaciones ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "depende_de", insertable = false, updatable = false)
    private Comite comiteDependiente;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "Militante_Comite",
            joinColumns = @JoinColumn(name = "comite_id"),
            inverseJoinColumns = @JoinColumn(name = "militante_id"))
    private Set<Militante> miembros = new HashSet<>();

    @OneToMany(mappedBy = "comite", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<MilitanteRolComite> responsabilidades = new HashSet<>();

    @OneToMany(mappedBy = "comiteDependiente", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comite> subComites;

    @OneToMany(mappedBy = "comiteDependiente", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComiteBase> subComitesBase;

    @OneToOne
    @JoinColumn(name = "inventario_id")
    private Inventario inventario;

    @OneToMany(mappedBy = "comite", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.EAGER)
    private List<AsignacionMaterial> materiales;

    // ========== 4. Métodos de dominio ==========
    /**
     * Segmento propio de la ruta del comité: p. ej. <pre>/Comite_Andalucía_42</pre>
     */
    @Transient
    public String getPath() {
        // A partir de Java 17, String#formatted es más claro que varias concatenaciones
        return "/Comite_%s_%d".formatted(nombre, id);
    }

    /**
     * Ruta completa desde la organización raíz hasta este comité sin recursión
     * y SIN forzar la inicialización de proxies.
     */
    @Transient
    public String getFullPath() {

        // 1. Determinamos el prefijo raíz examinando sólo ESTE objeto
        final String rootPrefix = (organizacion == Organizacion.CJC) ? "/Juventud" : "/Partido";

        // 2. Acumulamos cada segmento en una pila para poder invertir el orden
        final Deque<String> segments = new ArrayDeque<>();
        Comite current = this;

        while (current != null) {              // iterativo → no hay riesgo de StackOverflow
            segments.push(current.getPath());
            // Como tenemos la FK (comiteDependienteId), podemos saber
            // cuándo parar sin inicializar innecesariamente el proxy.
            if (current.getComiteDependienteId() == null || current.getId() == current.getComiteDependienteId()) {
                break;
            }
            current = current.getComiteDependiente(); // se inicializa SÓLO si realmente lo necesitamos
        }

        // 3. Construimos la cadena final
        final StringBuilder path = new StringBuilder(rootPrefix);
        while (!segments.isEmpty()) {
            path.append(segments.pop());
        }
        return path.toString();
    }
}
