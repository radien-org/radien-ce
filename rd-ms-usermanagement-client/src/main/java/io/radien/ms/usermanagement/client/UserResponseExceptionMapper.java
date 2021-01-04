package io.radien.ms.usermanagement.client;

import io.radien.ms.usermanagement.client.exceptions.BadRequestException;
import io.radien.ms.usermanagement.client.exceptions.InternalServerErrorException;
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
        return statusCode == 400        // Bad Request
                || statusCode == 404    // Not Found
                || statusCode == 500;   // Internal Server Error
    }

    @Override
    public Exception toThrowable(Response response) {
        switch(response.getStatus()) {
            case 400: return new BadRequestException(response.readEntity(String.class));
            case 404: return new NotFoundException(response.readEntity(String.class));
            case 500: return new InternalServerErrorException(response.readEntity(String.class));
        }
        return null;
    }

}