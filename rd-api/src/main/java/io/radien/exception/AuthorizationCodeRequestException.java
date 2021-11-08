package io.radien.exception;

public class AuthorizationCodeRequestException extends Exception {
    public AuthorizationCodeRequestException(String message) {
        super(message);
    }

    public AuthorizationCodeRequestException(Exception e) {
        super(e);
    }

    public AuthorizationCodeRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
