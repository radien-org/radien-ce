/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.rd.microservice.ms.client.providers;

import io.rd.microservice.ms.client.entities.Microservice;
import io.rd.microservice.ms.client.util.MicroserviceModelMapper;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
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
 * @author Rajesh Gavvala
 * @author mawe
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class MicroserviceMessageBodyWriter implements MessageBodyWriter<Microservice> {
	private static final long serialVersionUID = 6812608123262000043L;

	 @Override
	    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
	 	return type.equals(Microservice.class);
	    }

	    /*
	    Deprecated in JAX RS 2.0
	     */
	    @Override
	    public long getSize(Microservice model, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
	    	return 0;
	    }

	    /**
	     * Marshal Microservice to OutputStream
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
	    public void writeTo(Microservice model, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
	    	JsonWriter jsonWriter = Json.createWriter(entityStream);
	        JsonObject jsonObject = MicroserviceModelMapper.map(model);
	        jsonWriter.writeObject(jsonObject);
	        jsonWriter.close();
	    }

}