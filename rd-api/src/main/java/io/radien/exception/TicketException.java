package io.radien.exception;

public class TicketException extends Exception {

    public TicketException(String message){
        super(message);
    }

    public TicketException(String message, Throwable cause) {
        super(message, cause);
    }
}
