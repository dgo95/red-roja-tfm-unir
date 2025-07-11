package es.juventudcomunista.redroja.cjcrest.anotatation;

import jakarta.validation.Constraint;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;


//Anotación para validar el email a nivel de atributo
@Target({ java.lang.annotation.ElementType.FIELD })
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidatorTelefono.class)
@Documented
public @interface ValidaTelefono {
    String message() default "Teléfono no válido";
    Class<?>[] groups() default {};
    Class<? extends jakarta.validation.Payload>[] payload() default {};
}
