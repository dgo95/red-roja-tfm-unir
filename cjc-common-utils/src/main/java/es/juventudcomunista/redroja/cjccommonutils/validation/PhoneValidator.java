package es.juventudcomunista.redroja.cjccommonutils.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import es.juventudcomunista.redroja.cjccommonutils.utils.ValidationUtils;


public class PhoneValidator implements ConstraintValidator<ValidPhone, String> {


    @Override
    public void initialize(ValidPhone constraintAnnotation) {
    }

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        return ValidationUtils.esTelefonoValido(phone);
    }
}