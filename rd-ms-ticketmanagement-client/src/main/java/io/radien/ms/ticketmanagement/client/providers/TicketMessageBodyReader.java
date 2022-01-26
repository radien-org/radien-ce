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
 *
 */
package io.radien.ms.ticketmanagement.client.providers;

import io.radien.api.model.ticket.SystemTicket;
import io.radien.ms.ticketmanagement.client.entities.Ticket;
import io.radien.ms.ticketmanagement.client.util.TicketModelMapper;

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
 * Message body reader that converts a Json stream into an Permission
 * @author Newton Carvalho
 */
@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class TicketMessageBodyReader implements MessageBodyReader<Ticket> {

    /**
     * Validator of the Ticket message body is readable
     * @param type of the object
     * @param genericType generic type of the object
     * @param annotations annotation
     * @param mediaType type of the given readable field
     * @return true if the given object is a Ticket object
     */
    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type != null && SystemTicket.class.isAssignableFrom(type);
    }

    /**
     * Will call the Ticket mapper to convert the given object
     * @param type of the object
     * @param genericType generic type of the received object
     * @param annotations annotation
     * @param mediaType type of the given readable field
     * @param httpHeaders header of the http received
     * @param entityStream received object
     * @return a Ticket that has been converted from the entity stream
     * @throws WebApplicationException This exception may be thrown by a resource method, provider or StreamingOutput
     * implementation if a specific HTTP error response needs to be produced.
     */
    @Override
    public Ticket readFrom(Class<Ticket> type, Type genericType,
                               Annotation[] annotations, MediaType mediaType,
                               MultivaluedMap<String, String> httpHeaders,
                               InputStream entityStream) throws IOException, WebApplicationException {
            return TicketModelMapper.map(entityStream);
    }
}
