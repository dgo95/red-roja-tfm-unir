package es.juventudcomunista.redroja.cjcdocumentos.crypto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
@Profile("local")
public class LocalKmsService implements KeyManagementService {

    private final SecretKeySpec kek;   // clave de entorno: `APP_KMS_KEK_BASE64`

    public LocalKmsService(
            @Value("${app.kms.kek-base64}") String b64) {
        this.kek = new SecretKeySpec(Base64.getDecoder().decode(b64), "AES");
    }

    @Override public byte[] wrap(byte[] dek) {
        try {
            Cipher c = Cipher.getInstance("AESWrap");
            c.init(Cipher.WRAP_MODE, kek);
            return c.wrap(new SecretKeySpec(dek, "AES"));
        } catch (Exception e) { throw new IllegalStateException(e); }
    }

    @Override public byte[] unwrap(byte[] wrappedDek) {
        try {
            Cipher c = Cipher.getInstance("AESWrap");
            c.init(Cipher.UNWRAP_MODE, kek);
            return ((SecretKey) c.unwrap(wrappedDek, "AES", Cipher.SECRET_KEY)).getEncoded();
        } catch (Exception e) { throw new IllegalStateException(e); }
    }
}
