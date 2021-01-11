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
package io.radien.ms.usermanagement.client.providers;

import io.radien.api.entity.Page;
import io.radien.ms.usermanagement.client.util.PageModelMapper;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author mawe
 *
 */
@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class PageMessageBodyReader implements MessageBodyReader<Page> {
	 	@Override
	    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
	 		return type.equals(Page.class);
	    }

	    @Override
	    public Page readFrom(Class<Page> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
	 		return PageModelMapper.map(entityStream);
	    }
}
