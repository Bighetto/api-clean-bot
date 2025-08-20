package api.bank.app.exception;

public class ExecutorNotFoundException extends RuntimeException {
    public ExecutorNotFoundException(String message) {
        super(message);
    }
}
