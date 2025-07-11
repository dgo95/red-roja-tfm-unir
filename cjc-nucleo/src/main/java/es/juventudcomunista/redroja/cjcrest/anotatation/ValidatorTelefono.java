package es.juventudcomunista.redroja.cjcrest.anotatation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorTelefono implements ConstraintValidator<ValidaTelefono, String> {

    private static final String TEL_PATTERN = "^[+]*[(]{0,1}\\d{1,4}[)]{0,1}[-\\s\\./0-9]*$";

    @Override
    public void initialize(ValidaTelefono constraintAnnotation) {
    }

    @Override
    public boolean isValid(String telefono, ConstraintValidatorContext context) {
        return (validateTel(telefono));
    }

    private boolean validateTel(String telefono) {

        Pattern pattern = Pattern.compile(TEL_PATTERN);
        Matcher matcher = pattern.matcher(telefono);
        return matcher.matches();
    }
}
