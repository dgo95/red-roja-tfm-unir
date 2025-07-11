package es.juventudcomunista.redroja.cjcemail.entity.email;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "email_enviado")
public class EmailEnviado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_email", nullable = false)
    private String from;

    @Column(name = "to_email", nullable = false)
    private String to;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmailStatus estado;

    @Column(name = "numero_reintentos", nullable = false)
    private int numeroReintentos;

    @Column
    private String comentarios;
    
    @Column
    private String mensajeError;

    // Campos adicionales
    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;
    
    @Column(name = "ultima_modificacion")
    private LocalDateTime ultimaModificacion;
}
