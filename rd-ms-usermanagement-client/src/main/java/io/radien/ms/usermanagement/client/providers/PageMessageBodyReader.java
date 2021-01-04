/**
 * 
 */
package io.radien.ms.usermanagement.client.providers;

import io.radien.ms.usermanagement.client.entities.Page;
import io.radien.ms.usermanagement.client.util.PageModelMapper;

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
public class PageMessageBodyReader implements MessageBodyReader<Page> {
	 	@Override
	    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
	 		return type.equals(Page.class);
	    }

	    @Override
	    public Page readFrom(Class<Page> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
	 		return PageModelMapper.map(entityStream);
	    }
}
