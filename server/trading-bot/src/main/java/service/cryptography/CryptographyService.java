package service.cryptography;

import org.jetbrains.annotations.NotNull;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import io.ktor.server.config.ApplicationConfig;

public class CryptographyService {
    private final String algorithm;
    private final int size;

    public CryptographyService(@NotNull ApplicationConfig applicationConfig) {
        algorithm = applicationConfig.property("crypto.algorithm").getString();
        size = Integer.parseInt(applicationConfig.property("crypto.size").getString());
    }

    public @NotNull SecretKey generateSecretKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(this.algorithm);
        keyGenerator.init(size);
        return keyGenerator.generateKey();
    }

    public @NotNull String encrypt(@NotNull String plainText, @NotNull SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(this.algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public @NotNull String decrypt(@NotNull String encryptedText, @NotNull SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(this.algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(decryptedBytes);
    }

    public @NotNull SecretKey stringToKey(@NotNull String keyString) {
        byte[] keyBytes = Base64.getDecoder().decode(keyString);
        return new SecretKeySpec(keyBytes, algorithm);
    }

    public @NotNull String keyToString(@NotNull SecretKey key) {
        byte[] keyBytes = key.getEncoded();
        return Base64.getEncoder().encodeToString(keyBytes);
    }
}
