/**
 *
 */
package io.radien.ms.usermanagement.client.providers;


import io.radien.ms.usermanagement.client.entities.User;
import io.radien.ms.usermanagement.client.util.UserModelMapper;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author mawe
 *
 */
@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class UserMessageBodyReader implements MessageBodyReader<User> {
	 	@Override
	    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
	 		return type.equals(User.class);
	    }

	    @Override
	    public User readFrom(Class<User> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
	 		return UserModelMapper.map(entityStream);
	    }
}
