/**
 * 
 */
package io.radien.ms.usermanagement.client.providers;


import io.radien.ms.usermanagement.client.entities.User;
import io.radien.ms.usermanagement.client.util.UserModelMapper;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author mawe
 *
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class UserMessageBodyWriter implements MessageBodyWriter<User> {

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
	     * Marshal User to OutputStream
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
