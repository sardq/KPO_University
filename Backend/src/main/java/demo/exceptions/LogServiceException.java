package demo.exceptions;

public class LogServiceException extends RuntimeException {
        public LogServiceException(String message) {
            super(message);
        }

        public LogServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }