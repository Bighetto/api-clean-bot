package api.security.auth.app.exception;

public class AESCipherInitializationException extends RuntimeException {
    public AESCipherInitializationException(String message) {
        super(message);
    }
}