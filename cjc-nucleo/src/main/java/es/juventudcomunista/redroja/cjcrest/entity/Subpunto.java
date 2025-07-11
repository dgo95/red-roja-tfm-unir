package es.juventudcomunista.redroja.cjcrest.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import lombok.Setter;

@Entity
@Table(name = "subpunto")
@Getter
@Setter
@NoArgsConstructor
public class Subpunto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "titulo")
    private String titulo;

    @Column(name = "orden")
    private Integer orden;

    @Column(name = "detalle")
    private String detalle;
    
    @Override
    public String toString() {
        return "    "+getLowercaseLetterFromNumber(orden) + ") " + titulo + (detalle != null && !detalle.isEmpty() ? " - " + detalle : "");
    }
    public static String getLowercaseLetterFromNumber(int number) {
        StringBuilder result = new StringBuilder();
        while (number > 0) {
            // Decrementa el número por 1 para hacerlo compatible con el índice base 0 (0-25)
            number--;
            // Encuentra el residuo, que determinará la letra actual
            int remainder = number % 26;
            // Convierte el residuo a una letra. 97 es el código ASCII de 'a'
            char letter = (char) (remainder + 97);
            // Prepende la letra actual al resultado
            result.insert(0, letter);
            // Divide el número por 26 para pasar al siguiente "dígito"
            number = number / 26;
        }
        return result.toString();
    }
}

