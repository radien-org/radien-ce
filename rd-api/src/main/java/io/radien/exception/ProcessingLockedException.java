package io.radien.exception;

import io.radien.exception.generic.AbstractRestException;

import javax.ws.rs.core.Response;

public class ProcessingLockedException extends AbstractRestException {

    public ProcessingLockedException(){
        super("User processing is locked. Action can't be commited", Response.Status.PRECONDITION_FAILED);
    }
}
