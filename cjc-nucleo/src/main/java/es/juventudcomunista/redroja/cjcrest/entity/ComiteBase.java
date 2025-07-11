package es.juventudcomunista.redroja.cjcrest.entity;

import lombok.*;
import es.juventudcomunista.redroja.cjccommonutils.enums.Organizacion;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comite_base")
@ToString(exclude = {"comiteDependiente", "reuniones", "materiales", "militantes"})
@EqualsAndHashCode(exclude = {"comiteDependiente", "reuniones", "materiales", "militantes"})
@Builder
public class ComiteBase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Organizacion organizacion;

    @Column(name = "nombre", nullable = false)
    private String nombre;
    
    @Column(name = "sede", nullable = true)
    private String sede;

    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @Column
    private String nivel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "depende_de", insertable = false, updatable = false)
    private Comite comiteDependiente;


    @OneToMany(mappedBy = "comiteBase", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Reunion> reuniones = new HashSet<>();

    @OneToMany(mappedBy = "comiteBase", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<MilitanteRolComite> responsabilidades = new HashSet<>();

    @ManyToMany(mappedBy = "comitesBase")
    private Set<Militante> militantes = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "inventario_id")
    private Inventario inventario;

    @OneToMany(mappedBy = "comiteBase", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<AsignacionMaterial> materiales;

    public String getPath(){
        var path = "/";
        if(organizacion == Organizacion.CJC){
            path += "Colectivo_";
        }else{
            path += "Celula_";
        }
        path += nombre+"_";
        path += id;
        return path;
    }

    public String getFullPath(){
        return comiteDependiente.getFullPath()+getPath();
    }
    
}

