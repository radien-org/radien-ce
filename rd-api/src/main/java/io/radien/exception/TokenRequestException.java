package io.radien.exception;

public class TokenRequestException extends Exception {

    public TokenRequestException(String message) { super(message); }

    public TokenRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenRequestException(Exception e) { super(e); }
}
