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
package io.radien.ms.tenantmanagement.client.providers;

import io.radien.ms.tenantmanagement.client.entities.ActiveTenant;
import io.radien.ms.tenantmanagement.client.util.ActiveTenantModelMapper;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.ParseException;

/**
 * Active Tenant JSON reader into object
 * Reads the given JSON object and converts it into a active tenant
 * @author Bruno Gama
 */
@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class ActiveTenantMessageBodyReader implements MessageBodyReader<ActiveTenant> {

	/**
	 * Checks if the given JSON object can be read into a active tenant one
	 * @param type of the received object
	 * @param genericType for multiple conversion purposes
	 * @param annotations annotation
	 * @param mediaType type of the given readable field
	 * @return true in case received JSON can be read into a active tenant
	 */
	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return type.equals(ActiveTenant.class);
	}

	/**
	 * Converts the given JSON object into a contract one
	 * @param type for the final object (active tenant)
	 * @param genericType for multiple conversion purposes
	 * @param annotations annotation
	 * @param mediaType type of the given readable field
	 * @param httpHeaders header of the http received
	 * @param entityStream received object
	 * @return a System Active Tenant that has been gather the information from the given JSON
	 * @throws WebApplicationException in case of any issue while parsing the JSON fields into system active tenant ones
	 */
	@Override
	public ActiveTenant readFrom(Class<ActiveTenant> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws WebApplicationException {
		ActiveTenant activeTenant = null;
		try {
			activeTenant = ActiveTenantModelMapper.map(entityStream);
		} catch (ParseException e) {
			throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
		}
		return activeTenant;
	}
}
