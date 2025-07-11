package es.juventudcomunista.redroja.cjcrest.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;

@Entity
@Table(name = "idiomas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdiomaConocido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "alpha2", nullable = false, length = 2, unique = true)
    private String alpha2;

    @Column(nullable = false, length = 48)
    private String langEN;

    @Column(length = 48)
    private String langDE;

    @Column(length = 48)
    private String langFR;

    @Column(length = 48)
    private String langES;

    @Column(length = 48)
    private String langIT;

    @Column(length = 48)
    private String langGL;

    @Column(length = 48)
    private String langCA;

    @Column(length = 48)
    private String langEU;
}

