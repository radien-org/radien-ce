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
package io.radien.ms.tenantmanagement.client.providers;

import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.ms.tenantmanagement.client.util.TenantModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * Tenant JSON reader into object
 * Reads the given JSON object and converts it into a tenant
 * @author Newton Carvalho
 */
@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class TenantMessageBodyReader implements MessageBodyReader<Tenant> {
	protected static final Logger log = LoggerFactory.getLogger(TenantMessageBodyReader.class);

	/**
	 * Checks if the given JSON object can be read into a tenant one
	 * @param type of the received object
	 * @param genericType for multiple conversion purposes
	 * @param annotations annotation
	 * @param mediaType type of the given readable field
	 * @return true in case received JSON can be read into a tenant
	 */
	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return type.equals(Tenant.class);
	}

	/**
	 * Converts the given JSON object into a contract one
	 * @param type for the final object (tenant)
	 * @param type for multiple conversion purposes
	 * @param annotations annotation
	 * @param mediaType type of the given readable field
	 * @param httpHeaders header of the http received
	 * @param entityStream received object
	 * @return a System Tenant that has been gather the information from the given JSON
	 * @throws WebApplicationException in case of any issue while parsing the JSON fields into system tenant ones
	 */
	@Override
	public Tenant readFrom(Class<Tenant> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws WebApplicationException {
		Tenant tenant = null;
		try {
			tenant = TenantModelMapper.map(entityStream);
		} catch (ParseException e) {
			throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
		}
		return tenant;
	}
}
