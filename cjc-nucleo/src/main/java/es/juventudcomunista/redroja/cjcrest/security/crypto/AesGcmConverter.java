package es.juventudcomunista.redroja.cjcrest.security.crypto;

import es.juventudcomunista.redroja.cjcrest.config.FieldCryptoProperties;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
@Component
@Converter()
public class AesGcmConverter implements AttributeConverter<String, String> {

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;         // 96 bits para GCM
    private static final int TAG_LENGTH = 128;       // 128 bits para GCM tag
    private static final SecureRandom RNG = new SecureRandom();

    private final FieldCryptoProperties props;
    private SecretKey key;

    public AesGcmConverter(FieldCryptoProperties props) {
        this.props = props;
    }

    @PostConstruct
    public void init() {
        String keyB64 = props.getAesKey();
        if (keyB64 == null || keyB64.isBlank()) {
            throw new IllegalStateException("Propiedad security.crypto.field.aes-key no configurada");
        }
        byte[] decoded = Base64.getDecoder().decode(keyB64);
        this.key = new SecretKeySpec(decoded, "AES");
    }

    @Override
    public String convertToDatabaseColumn(String plainText) {
        if (plainText == null) return null;
        try {
            byte[] iv = new byte[IV_LENGTH];
            RNG.nextBytes(iv);

            Cipher cifrador = Cipher.getInstance(TRANSFORMATION);
            cifrador.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH, iv));
            byte[] cipherText = cifrador.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            ByteBuffer buf = ByteBuffer.allocate(iv.length + cipherText.length);
            buf.put(iv).put(cipherText);
            return Base64.getEncoder().encodeToString(buf.array());
        } catch (Exception e) {
            log.error("Error cifrando dato", e);
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            byte[] allBytes = Base64.getDecoder().decode(dbData);
            ByteBuffer buf = ByteBuffer.wrap(allBytes);

            byte[] iv = new byte[IV_LENGTH];
            buf.get(iv);
            byte[] cipherText = new byte[buf.remaining()];
            buf.get(cipherText);

            Cipher descifrador = Cipher.getInstance(TRANSFORMATION);
            descifrador.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH, iv));
            byte[] plain = descifrador.doFinal(cipherText);
            return new String(plain, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Error descifrando dato", e);
            throw new IllegalStateException(e);
        }
    }
}
