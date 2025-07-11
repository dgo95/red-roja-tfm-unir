package es.juventudcomunista.redroja.cjccommonutils.validation;

import jakarta.validation.ConstraintValidator;

import jakarta.validation.ConstraintValidatorContext;
import es.juventudcomunista.redroja.cjccommonutils.utils.ValidationUtils;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    @Override
    public void initialize(StrongPassword constraintAnnotation) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        return ValidationUtils.esContrasenaFuerte(password);
    }
}

