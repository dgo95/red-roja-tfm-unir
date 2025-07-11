package es.juventudcomunista.redroja.cjccommonutils.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CryptoService {

    private final Environment environment;
    private final ResourceLoader resourceLoader;



    private String privateKeyPath = "borrar toda la clase";

    private PrivateKey privateKey;

    @PostConstruct
    public void init() throws Exception {
        if(isProProfileActive())
            loadPrivateKey();
    }

    private void loadPrivateKey() throws Exception {
        Resource resource = resourceLoader.getResource(privateKeyPath);
        try (InputStream inputStream = resource.getInputStream()) {
            byte[] keyBytes = inputStream.readAllBytes();
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.privateKey = keyFactory.generatePrivate(spec);
        }
    }

    public String encryptValue(String value) throws Exception {
        if(!isProProfileActive())
            return value;
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] encryptedBytes = cipher.doFinal(value.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }


    public String decryptValue(String encryptedValue, String publicKeyStr) throws Exception {
        if (publicKeyStr.isBlank()) {
            return encryptedValue;
        }

        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] decodedValue = Base64.getDecoder().decode(encryptedValue);
        return new String(cipher.doFinal(decodedValue));
    }

    public List<String> decryptRoles(String rolesHeader, String publicKey)  {
        return Arrays.stream(rolesHeader.replace("[", "").replace("]", "").split(","))
                .map(encryptedRole -> {
                    try {
                        return decryptValue(encryptedRole, publicKey);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    public String getPublicKey(String micro) {
        if (!isProProfileActive()) {
            return "";
        }
        return micro;
    }

    private boolean isProProfileActive() {
        return Arrays.asList(environment.getActiveProfiles()).contains("pro");
    }
}
