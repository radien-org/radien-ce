/*
 *
 *  * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
 *  * <p>
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  * <p>
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  * <p>
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package io.radien.ms.ecm.client.providers;

import io.radien.ms.ecm.client.entities.i18n.DeletePropertyFilter;
import io.radien.ms.ecm.client.factory.I18NFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class DeletePropertyFilterMessageBodyReader implements MessageBodyReader<DeletePropertyFilter> {
    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return DeletePropertyFilter.class.isAssignableFrom(type);
    }

    @Override
    public DeletePropertyFilter readFrom(Class<DeletePropertyFilter> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> multivaluedMap, InputStream inputStream) throws IOException, WebApplicationException {
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject obj = (JSONObject) jsonParser.parse(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            DeletePropertyFilter filter = new DeletePropertyFilter();
            filter.setApplication((String) obj.get("application"));
            filter.setProperties(I18NFactory.convertJSONArray((JSONArray) obj.get("properties")));
            return filter;
        } catch (IOException | ParseException e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
    }
}
