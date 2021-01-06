package io.radien.aws.utils.s3.exceptions;

public class RemoteResourceNotAvailableException extends RuntimeException{
    public RemoteResourceNotAvailableException(String message) {
        super(message);
    }

    public RemoteResourceNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
