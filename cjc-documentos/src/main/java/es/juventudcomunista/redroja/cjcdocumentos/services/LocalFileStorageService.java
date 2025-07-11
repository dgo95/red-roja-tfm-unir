package es.juventudcomunista.redroja.cjcdocumentos.services;

import es.juventudcomunista.redroja.cjcdocumentos.crypto.KeyManagementService;
import es.juventudcomunista.redroja.cjcdocumentos.storage.CryptoFileInfo;
import es.juventudcomunista.redroja.cjcdocumentos.storage.DecryptedFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.util.Base64;

@Service
@Profile("local")
@RequiredArgsConstructor
@Slf4j
public class LocalFileStorageService implements FileStorageService {

    /* raíz de almacenamiento  */
    @Value("${app.storage.docs-dir}")
    private Path root;

    private final KeyManagementService kms;

    /* constantes criptográficas */
    private static final String CIPHER = "AES/GCM/NoPadding";
    private static final int TAG_BYTES = 16;
    private static final int IV_BYTES  = 12;

    /* ------------- GUARDAR ------------- */
    @Override
    public CryptoFileInfo store(InputStream in, String relativePath) throws IOException {

        Path target = root.resolve(relativePath).normalize();
        Files.createDirectories(target.getParent());

        try {
            /* 1. DEK + IV */
            SecureRandom rnd = SecureRandom.getInstanceStrong();
            byte[] dek = rnd.generateSeed(32);       // 256 bits
            byte[] iv  = rnd.generateSeed(IV_BYTES);

            /* 2. Digest SHA-256 sobre el texto plano */
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            DigestInputStream dis = new DigestInputStream(in, sha);

            /* 3. Ciframos en streaming (AES-GCM)  */
            Cipher cipher = Cipher.getInstance(CIPHER);
            cipher.init(Cipher.ENCRYPT_MODE,
                    new SecretKeySpec(dek, "AES"),
                    new GCMParameterSpec(TAG_BYTES * 8, iv));

            long plainBytes;                         // contaremos los bytes reales
            try (CipherOutputStream cos = new CipherOutputStream(
                    Files.newOutputStream(target,
                            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING),
                    cipher)) {

                plainBytes = dis.transferTo(cos);    // copia + digest simultáneo
            }

            /* 4. Construir metadatos */
            return new CryptoFileInfo(
                    Hex.encodeHexString(sha.digest()),
                    plainBytes,
                    Base64.getEncoder().encodeToString(iv),
                    Base64.getEncoder().encodeToString(kms.wrap(dek))
            );

        } catch (NoSuchAlgorithmException |
                 NoSuchPaddingException   |
                 InvalidKeyException      |
                 InvalidAlgorithmParameterException e) {
            /* Todas son fallos de programación / entorno → IllegalState */
            throw new IllegalStateException("Error criptográfico al guardar fichero", e);
        }
    }

    /* ------------- LEER & VERIFICAR ------------- */
    @Override
    public DecryptedFile load(String relativePath, CryptoFileInfo meta) throws IOException {

        Path enc = root.resolve(relativePath).normalize();
        if (Files.notExists(enc)) throw new FileNotFoundException(enc.toString());

        try {
            byte[] iv  = Base64.getDecoder().decode(meta.ivB64());
            byte[] dek = kms.unwrap(Base64.getDecoder().decode(meta.dekWrappedB64()));

            Cipher cipher = Cipher.getInstance(CIPHER);
            cipher.init(Cipher.DECRYPT_MODE,
                    new SecretKeySpec(dek, "AES"),
                    new GCMParameterSpec(TAG_BYTES * 8, iv));

            MessageDigest sha = MessageDigest.getInstance("SHA-256");

            ByteArrayOutputStream plain = new ByteArrayOutputStream();
            try (CipherInputStream cis = new CipherInputStream(
                    Files.newInputStream(enc), cipher);
                 DigestOutputStream dos = new DigestOutputStream(plain, sha)) {

                cis.transferTo(dos);
            }

            String actual = Hex.encodeHexString(sha.digest());
            if (!actual.equals(meta.sha256Plain())) {
                throw new SecurityException("integrity_check_failed: SHA-256 mismatch");
            }

            return new DecryptedFile(
                    new ByteArrayInputStream(plain.toByteArray()),
                    meta.sizePlain(),
                    null);

        } catch ( IOException d){
            log.error("El documento ha sido modificado. Error: {}",d.getMessage());
            throw new IllegalStateException("El documento ha sido modificado. Error: "+d.getMessage());
        }
        catch (NoSuchPaddingException            |
                 NoSuchAlgorithmException          |
                 InvalidAlgorithmParameterException|
                 InvalidKeyException e) {
            throw new IllegalStateException("Error criptográfico al leer fichero", e);
        }
    }

    /* ------------- utilidades auxiliares ------------- */
    @Override
    public Resource loadAsResource(String relativePath) {
        return new FileSystemResource(root.resolve(relativePath).normalize());
    }

    @Override
    public void delete(String relativePath) throws IOException {
        Files.deleteIfExists(root.resolve(relativePath).normalize());
    }
}
