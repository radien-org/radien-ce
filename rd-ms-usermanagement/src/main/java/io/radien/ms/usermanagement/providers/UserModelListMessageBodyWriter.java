/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
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
package io.radien.ms.usermanagement.providers;

import io.radien.ms.usermanagement.client.entities.User;
import io.radien.ms.usermanagement.entities.UserEntity;
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

import io.radien.ms.usermanagement.util.UserModelMapper;

/**
 * User management Message body Writer from object to JSON
 *
 * @author Bruno Gama
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class UserModelListMessageBodyWriter implements MessageBodyWriter<List<UserEntity>> {

	/**
	 * Validates if the given received type is a user object
	 * @param type of the received object
	 * @param genericType for multiple conversions
	 * @param annotations annotation
	 * @param mediaType type of the given readable field
	 * @return true if received object is in fact a user one
	 */
	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return type.equals(User.class);
	}

	/**
	 * Gets the number of received objects and counts it
	 * @param t user object received
	 * @param type of the received object
	 * @param genericType for multiple conversions
	 * @param annotations annotation
	 * @param mediaType type of the given readable field
	 * @return the number of received objects
	 */
	@Override
	public long getSize(List<UserEntity> t, Class<?> type, Type genericType,
						Annotation[] annotations, MediaType mediaType) {
		return 0;
	}

	/**
	 * Writes the received linked authorization object into a json message
	 * @param t user object received to be written
	 * @param type of the received object
	 * @param genericType for multiple conversions
	 * @param annotations annotation
	 * @param mediaType type of the given readable field
	 * @param httpHeaders header of the http received
	 * @param entityStream received object
	 * @throws WebApplicationException This exception may be thrown by a resource method, provider or StreamingOutput
	 * implementation if a specific HTTP error response needs to be produced.
	 */
	@Override
	public void writeTo(List<UserEntity> t, Class<?> type, Type genericType, Annotation[] annotations,
			MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
			throws WebApplicationException {
      JsonWriter jsonWriter = Json.createWriter(entityStream);
      JsonArray jsonArray = UserModelMapper.map(t);
      jsonWriter.writeArray(jsonArray);
      jsonWriter.close();
		
	}

}
