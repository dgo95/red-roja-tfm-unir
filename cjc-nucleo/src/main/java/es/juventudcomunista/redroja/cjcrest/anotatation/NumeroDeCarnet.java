package es.juventudcomunista.redroja.cjcrest.anotatation;

import jakarta.validation.Constraint;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;



@Target({ java.lang.annotation.ElementType.FIELD })
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidatorCarnet.class)
@Documented
public @interface NumeroDeCarnet {
    String message() default "Email no válido";
    Class<?>[] groups() default {};
    Class<? extends jakarta.validation.Payload>[] payload() default {};
}
