package io.radien.ms.usermanagement.client.exceptions;

public class InvalidRequestException extends Exception{
	private static final long serialVersionUID = 1L;

	public InvalidRequestException() {
        super();
    }

    public InvalidRequestException(String message) {
        super(message);
    }
}
