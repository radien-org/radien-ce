package io.radien.ms.usermanagement.client.exceptions;

/**
 * This exception indicates accessing is not allowed to some resource
 * due insufficient permission (Roles, etc)
 *
 * @author Newton Carvalho
 */
public class ForbiddenException extends RuntimeException {

    /**
     * Forbidden exception empty constructor
     */
    public ForbiddenException() {
        super();
    }

    /**
     * Forbidden exception message constructor
     * @param message specific message to be added into the exception when being throw
     */
    public ForbiddenException(String message) {
        super(message);
    }
}
