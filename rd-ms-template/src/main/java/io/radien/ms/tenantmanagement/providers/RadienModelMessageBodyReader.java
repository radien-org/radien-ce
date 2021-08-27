/**
 * 
 */
package io.radien.ms.tenantmanagement.providers;

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

import io.radien.ms.tenantmanagement.model.RadienModel;
import io.radien.ms.tenantmanagement.util.RadienModelMapper;

/**
 * @author mawe
 *
 */
@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class RadienModelMessageBodyReader implements MessageBodyReader<RadienModel> {
	 @Override
	    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
	        return type.equals(RadienModel.class);
	    }

	    @Override
	    public RadienModel readFrom(Class<RadienModel> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
	        return RadienModelMapper.map(entityStream);
	    }
}
