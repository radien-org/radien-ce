package io.radien.ms.usermanagement.providers;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonWriter;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import io.radien.ms.usermanagement.model.RadienModel;
import io.radien.ms.usermanagement.util.RadienModelMapper;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class RadienModelListMessageBodyWriter implements MessageBodyWriter<List<RadienModel>> {

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return true;
	}

	@Override
	public long getSize(List<RadienModel> t, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return 0;
	}

	@Override
	public void writeTo(List<RadienModel> t, Class<?> type, Type genericType, Annotation[] annotations,
			MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
			throws IOException, WebApplicationException {
      JsonWriter jsonWriter = Json.createWriter(entityStream);
      JsonArray jsonArray = RadienModelMapper.map(t);
      jsonWriter.writeArray(jsonArray);
      jsonWriter.close();
		
	}

}
