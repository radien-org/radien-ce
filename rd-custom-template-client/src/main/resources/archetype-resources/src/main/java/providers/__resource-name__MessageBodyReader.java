/*
 * Copyright (c) 2016-present radien.io & its legal owners. All rights reserved.
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
package ${package}.providers;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import ${package}.entities.${resource-name};
import ${package}.util.${resource-name}ModelMapper;

/**
 * @author Rajesh Gavvala
 *
 */

@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class ${resource-name}MessageBodyReader implements MessageBodyReader<${resource-name}> {
	 	@Override
	    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
	        return type.equals(${resource-name}.class);
	    }

	    @Override
		public ${resource-name} readFrom(Class<${resource-name}> type, Type genericType, Annotation[] annotations,
		MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
			return ${resource-name}ModelMapper.map(entityStream);
	 }
}
