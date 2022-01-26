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
package io.radien.ms.ticketmanagement.client.util;

import io.radien.api.entity.Page;
import io.radien.ms.ticketmanagement.client.entities.Ticket;
import io.radien.ms.ticketmanagement.client.services.TicketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.InputStream;
import java.text.ParseException;


/**
 * Mapper from a given information into a JSON or a contract
 * @author Santana
 */
public class TicketModelMapper {

    protected static final Logger log = LoggerFactory.getLogger(TicketModelMapper.class);

    private TicketModelMapper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Maps into a Json Object a Ticket
     * @param model ticket that has the information to be converted
     * @return a json object created based the ticket
     */
    public static JsonObject map(Ticket model) {
        return TicketFactory.convertToJsonObject(model);
    }

    /**
     * Creates a ticket based a received inputted information
     * @param is inputted information to be converted into the object
     * @return a ticket object based in the received information
     */
    public static Ticket map(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            return TicketFactory.convert(jsonReader.readObject());
        }
    }

    /**
     * Creates a tickets based a received inputted information
     * @param is inputted information to be converted into the object
     * @return a page of tickets object based in the received information
     */
    public static Page<Ticket> mapToPage(InputStream is) {
        Page<Ticket> page = null;
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();

            page = TicketFactory.convertJsonToPage(jsonObject);
        } catch (ParseException e) {
            log.error(e.getMessage(),e);
        }
        return page;
    }
}
