package api.security.auth.app.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import api.security.auth.app.exception.AESCipherInitializationException;
import api.security.auth.app.exception.AESEncryptionException;

@Configuration
public class AESEncryptor {

    @Value("${aes.secret-key}")
    private String secretKey;

    @Bean
    public Cipher aesCipher() {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            return cipher;
        } catch (Exception e) {
            throw new AESCipherInitializationException("Failed to initialize AES Cipher. Please check the encryption key.");
        }
    }

    public String encrypt(String input) {
        try {
            Cipher cipher = aesCipher();
            byte[] encrypted = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new AESEncryptionException();
        }
    }
}
