package ${package}.providers;

import java.io.IOException;
import java.io.OutputStream;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import java.util.List;

import javax.json.Json;
import javax.json.JsonWriter;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import ${package}.model.${library-name};

/**
 * @author Rajesh Gavvala
 *
 */

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class ${library-name}ListMessageBodyWriter implements MessageBodyWriter<List<${library-name}>> {

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return true;
	}

	@Override
	public long getSize(List<${library-name}> templates, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return 0;
	}

	@Override
	public void writeTo(List<${library-name}> t, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> multivaluedMap, OutputStream entityStream) throws IOException, WebApplicationException {
		JsonWriter jsonWriter = Json.createWriter(entityStream);
		//JsonArray jsonArray = ${library-name}Mapper.map(t);
		//jsonWriter.writeArray(jsonArray);
		jsonWriter.close();
	}

}
