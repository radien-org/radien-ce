/**
 *
 */
package ${package}.providers;

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

import ${package}.model.${library-name};
import ${package}.util.${library-name}Mapper;

/**
 * @author Rajesh Gavvala
 *
 */
public class ${library-name}MessageBodyWriter implements MessageBodyWriter<${library-name}> {

	 @Override
	    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
	        return type.equals(${library-name}.class);
	    }

	    /*
	    Deprecated in JAX RS 2.0
	     */
	    @Override
	    public long getSize(${library-name} model, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
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
	    public void writeTo(${library-name} model, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
	        JsonWriter jsonWriter = Json.createWriter(entityStream);
	        JsonObject jsonObject = ${library-name}Mapper.map(model);
	        jsonWriter.writeObject(jsonObject);
	        jsonWriter.close();
	    }

}
