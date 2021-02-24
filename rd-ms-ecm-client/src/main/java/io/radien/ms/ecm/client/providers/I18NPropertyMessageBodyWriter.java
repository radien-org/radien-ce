/*
 * Copyright (c) 2006-present openappframe.org & its legal owners. All rights reserved.
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
package io.radien.ms.ecm.client.providers;

import io.radien.ms.ecm.client.entities.I18NProperty;
import io.radien.ms.ecm.client.util.I18NPropertyMapper;

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


/**
 * @author Marco Weiland
 *
 */
public class I18NPropertyMessageBodyWriter implements MessageBodyWriter<I18NProperty> {

	 	@Override
	    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
	        return type.equals(I18NProperty.class);
	    }

	    /*
	    Deprecated in JAX RS 2.0
	     */
	    @Override
	    public long getSize(I18NProperty model, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
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
	    public void writeTo(I18NProperty model, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
							MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
	        JsonWriter jsonWriter = Json.createWriter(entityStream);
	        JsonObject jsonObject = I18NPropertyMapper.map(model);
	        jsonWriter.writeObject(jsonObject);
	        jsonWriter.close();
	    }

}
