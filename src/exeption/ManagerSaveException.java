package exeption;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(String message, Throwable exception) {
        super(message, exception);
    }
}
