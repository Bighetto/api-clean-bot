package api.security.auth.app.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import api.security.auth.app.exception.AESCipherInitializationException;
import api.security.auth.app.exception.AESEncryptionException;

@Configuration
public class AESEncryptor {

    @Value("${aes.secret-key}")
    private String secretKey;

    private Cipher initCipher(int mode) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); // modo seguro e padding correto
            cipher.init(mode, keySpec);
            return cipher;
        } catch (Exception e) {
            throw new AESCipherInitializationException("Erro ao inicializar o AES Cipher.");
        }
    }

    public String encrypt(String input) {
        try {
            Cipher cipher = initCipher(Cipher.ENCRYPT_MODE);
            byte[] encrypted = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new AESEncryptionException();
        }
    }

    public String decrypt(String encryptedBase64) {
        try {
            Cipher cipher = initCipher(Cipher.DECRYPT_MODE);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedBase64);
            byte[] decrypted = cipher.doFinal(decodedBytes);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
