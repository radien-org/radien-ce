package ${package}.providers;

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

import ${package}.model.${library-name};
import ${package}.util.${library-name}Mapper;

/**
 * @author Rajesh Gavvala
 *
 */

@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class ${library-name}MessageBodyReader implements MessageBodyReader<${library-name}> {
	 @Override
	    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
	        return type.equals(${library-name}.class);
	    }

	    @Override
	    public ${library-name} readFrom(Class<${library-name}> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
	        return ${library-name}Mapper.map(entityStream);

	    }
}
