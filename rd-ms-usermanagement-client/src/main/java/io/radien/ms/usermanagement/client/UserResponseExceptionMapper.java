package io.radien.ms.usermanagement.client;

import io.radien.ms.usermanagement.client.exceptions.NotFoundException;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class UserResponseExceptionMapper implements
        ResponseExceptionMapper<Exception> {

    @Override
    public boolean handles(int statusCode, MultivaluedMap<String, Object> headers) {
        return statusCode == 404  // Not Found
            || statusCode == 409; // Conflict
    }

    @Override
    public Exception toThrowable(Response response) {
        switch(response.getStatus()) {
        case 404: return new NotFoundException();
        //case 409: return new PlaylistAlreadyExistsException();
        }
        return null;
    }

}