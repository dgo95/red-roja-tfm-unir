package es.juventudcomunista.redroja.cjccommonutils.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

@Slf4j
@UtilityClass
public class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{7,15}$");
    private static final Pattern STRONG_PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");

    public boolean esEmailValido(String email) {
        if (email == null) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public boolean esTelefonoValido(String telefono) {
        if (telefono == null) {
            return false;
        }
        return PHONE_PATTERN.matcher(telefono).matches();
    }

    public boolean esContrasenaFuerte(String contrasena) {
        if (contrasena == null) {
            return false;
        }
        return STRONG_PASSWORD_PATTERN.matcher(contrasena).matches();
    }

    public boolean esLongitudValida(String str, int longitudMinima, int longitudMaxima) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        return length >= longitudMinima && length <= longitudMaxima;
    }

    public boolean esSoloLetras(String str) {
        if (str == null) {
            return false;
        }
        return str.chars().allMatch(Character::isLetter);
    }

    public boolean esSoloLetrasYNumeros(String str) {
        if (str == null) {
            return false;
        }
        return str.chars().allMatch(Character::isLetterOrDigit);
    }
}
