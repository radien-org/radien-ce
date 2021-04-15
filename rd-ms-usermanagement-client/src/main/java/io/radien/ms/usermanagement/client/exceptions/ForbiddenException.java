package io.radien.ms.usermanagement.client.exceptions;

/**
 * This exception indicates accessing is not allowed to some resource
 * due insufficient permission (Roles, etc)
 * @author Newton Carvalho
 */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException() {
        super();
    }

    public ForbiddenException(String message) {
        super(message);
    }
}
