/**
 * 
 */
package io.radien.ms.usermanagement.providers;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

import io.radien.ms.usermanagement.util.UserModelMapper;
import io.radien.persistence.entities.user.User;

/**
 * @author mawe
 *
 */
public class UserModelMessageBodyWriter implements MessageBodyWriter<User> {

	 @Override
	    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
	        return type.equals(User.class);
	    }

	    /*
	    Deprecated in JAX RS 2.0
	     */
	    @Override
	    public long getSize(User model, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
	        return 0;
	    }

	    /**
	     * Marsahl Book to OutputStream
	     *
	     * @param model
	     * @param type
	     * @param genericType
	     * @param annotations
	     * @param mediaType
	     * @param httpHeaders
	     * @param entityStream
	     * @throws IOException
	     * @throws WebApplicationException
	     */
	    @Override
	    public void writeTo(User model, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
	        JsonWriter jsonWriter = Json.createWriter(entityStream);
	        JsonObject jsonObject = UserModelMapper.map(model);
	        jsonWriter.writeObject(jsonObject);
	        jsonWriter.close();
	    }

}
