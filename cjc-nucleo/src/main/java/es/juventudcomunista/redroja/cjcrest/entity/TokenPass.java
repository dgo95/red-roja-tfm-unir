package es.juventudcomunista.redroja.cjcrest.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "token_pass")
public class TokenPass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_expiracion")
    private LocalDateTime fechaExpiracion;
    
    @Column(name = "tipo")
    private Integer tipo;
    
    @Column(name = "token")
    private String token;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "militante_id")
    private Militante militante;

    /* Método que establece la fecha de expiracion 24 horas tras la fecha de crecion*/
    public void calculaFechaExpiracion(int i) {
        this.fechaExpiracion = fechaCreacion.plusHours(i);
    }
    
    public void incializa() {
        this.fechaCreacion = LocalDateTime.now();
    }

    /* Método que comprueba si el token ha expirado o no.
     * Devuelve true si el token no ha expirado y no ha sido usado.
     * Devuelve false en caso contrario.
     */
    public boolean isTokenValid() {
        return this.fechaExpiracion.isAfter(LocalDateTime.now());
    }
}