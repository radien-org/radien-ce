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
package io.radien.ms.tenantmanagement.providers;

import io.radien.api.model.tenant.SystemContract;
import io.radien.ms.tenantmanagement.client.util.ContractModelMapper;
import io.radien.ms.tenantmanagement.entities.Contract;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.ParseException;

/**
 * @author santana
 *
 */
@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class SystemContractMessageBodyReader implements MessageBodyReader<SystemContract> {
	 	@Override
	    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
	        return type.equals(Contract.class);
	    }

	@Override
	public SystemContract readFrom(Class<SystemContract> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> multivaluedMap, InputStream inputStream) throws IOException, WebApplicationException {
		try {
			return ContractModelMapper.map(inputStream);
		} catch (ParseException e) {
			throw new WebApplicationException(e,Response.Status.BAD_REQUEST);
		}
	}

}
