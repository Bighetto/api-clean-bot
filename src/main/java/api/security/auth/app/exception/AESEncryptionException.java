package api.security.auth.app.exception;

public class AESEncryptionException extends RuntimeException{
    public AESEncryptionException(String message) {
        super(message);
    }

    public AESEncryptionException() {
    }
}
