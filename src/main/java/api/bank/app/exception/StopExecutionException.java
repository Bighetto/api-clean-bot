package api.bank.app.exception;

public class StopExecutionException extends RuntimeException {
    public StopExecutionException(String message) {
        super(message);
    }
}
