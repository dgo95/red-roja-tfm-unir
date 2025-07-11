package es.juventudcomunista.redroja.cjcrest.anotatation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/* Clase usada por NumeroDeCarnet para validar que un atributo es un String de cuatro dígitos */
public class ValidatorCarnet implements ConstraintValidator<NumeroDeCarnet, String> {

    @Override
    public void initialize(NumeroDeCarnet constraintAnnotation) {
    }

    @Override
    public boolean isValid(String carnet, ConstraintValidatorContext context) {
        return (null!= carnet && carnet.length() == 4 && carnet.matches("[0-9]+"));
    }
}
