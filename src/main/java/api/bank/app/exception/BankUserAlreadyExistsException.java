package api.bank.app.exception;

public class BankUserAlreadyExistsException extends RuntimeException {
    public BankUserAlreadyExistsException(String message) {
        super(message);
    }

}
