package api.bank.app.exception;

public class BankUserNotFoundException extends RuntimeException {
    public BankUserNotFoundException(String message) {
        super(message);
    }
}
