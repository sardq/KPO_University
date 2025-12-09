package demo.exceptions;

public class GenerateException extends RuntimeException {
    public GenerateException(String message, Throwable cause) {
        super(message, cause);
    }

    public GenerateException(String message) {
        super(message);
    }
}
