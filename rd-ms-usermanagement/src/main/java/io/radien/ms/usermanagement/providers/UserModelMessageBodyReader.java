/**
 * 
 */
package io.radien.ms.usermanagement.providers;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import io.radien.ms.usermanagement.entities.User;
import io.radien.ms.usermanagement.model.RadienModel;
import io.radien.ms.usermanagement.util.UserModelMapper;

/**
 * @author mawe
 *
 */
@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class UserModelMessageBodyReader implements MessageBodyReader<User> {
	 @Override
	    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
	        return type.equals(RadienModel.class);
	    }

	    @Override
	    public User readFrom(Class<User> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
	        return UserModelMapper.map(entityStream);
	    }
}
