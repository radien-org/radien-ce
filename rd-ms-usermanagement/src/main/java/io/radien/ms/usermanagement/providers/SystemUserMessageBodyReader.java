/*
 * Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.
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

import io.radien.ms.usermanagement.entities.UserEntity;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import io.radien.api.model.user.SystemUser;
import io.radien.ms.usermanagement.util.UserModelMapper;

/**
 * System User Message reader constructor class validator and converter for received messages
 *
 * @author Marco Weiland
 */
@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class SystemUserMessageBodyReader implements MessageBodyReader<SystemUser> {

	/**
	 * Validator of the User message body is readable
	 * @param type of the object
	 * @param genericType generic type of the object
	 * @param annotations annotation
	 * @param mediaType type of the given readable field
	 * @return true if the given object is a User object
	 */
	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return type.equals(UserEntity.class);
	}

	/**
	 * Will call the User mapper to convert the given object
	 * @param aClass of the object
	 * @param type generic type of the received object
	 * @param annotations annotation
	 * @param mediaType type of the given readable field
	 * @param multivaluedMap header of the http received
	 * @param inputStream received object
	 * @return a system user that has been converted from the entity stream
	 * @throws WebApplicationException This exception may be thrown by a resource method, provider or StreamingOutput
	 * implementation if a specific HTTP error response needs to be produced.
	 */
	@Override
	public SystemUser readFrom(Class<SystemUser> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> multivaluedMap, InputStream inputStream) throws WebApplicationException {
		return UserModelMapper.map(inputStream);
	}

}
