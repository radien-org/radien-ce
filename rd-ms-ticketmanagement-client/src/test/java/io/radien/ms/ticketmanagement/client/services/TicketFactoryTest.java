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
package io.radien.ms.ticketmanagement.client.services;

import io.radien.exception.GenericErrorCodeMessage;
import io.radien.ms.ticketmanagement.client.entities.Ticket;
import io.radien.ms.ticketmanagement.client.entities.TicketType;
import io.radien.ms.ticketmanagement.client.services.TicketFactory;
import junit.framework.TestCase;
import org.junit.Test;

import javax.json.*;
import java.text.ParseException;
import java.time.LocalDate;

import static org.junit.Assert.assertThrows;

/**
 * @author Rui Soares
 */
public class TicketFactoryTest extends TestCase {

    Ticket ticket = new Ticket();
    JsonObject json;

    public TicketFactoryTest() {
        ticket.setData("data");
        ticket.setUserId(2L);
        ticket.setTicketType(1L);
        ticket.setExpireDate(null);
        ticket.setToken("token");
    }

    @Test
    public void testCreate() {
        TicketFactory ticketFactory = new TicketFactory();
        Ticket newTicketConstructed = ticketFactory.create(2L, "token", 1L, LocalDate.now(), "data", 1L);

        assertEquals(ticket.getToken(), newTicketConstructed.getToken());
        assertEquals(ticket.getUserId(), newTicketConstructed.getUserId());
    }

    @Test
    public void testCreateInvalid(){
        assertThrows(IllegalStateException.class, () ->TicketFactory.create(2L, "token", null, LocalDate.now(), "data", 1L));
    }

    @Test
    public void testConvert() throws ParseException {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.addNull("id");
        builder.add("userId", 1L);
        builder.add("token", "token");
        builder.add("data", "myData");
        builder.add("ticketType", "{ \"id\": 1, \"type\": \"email_change\"} ");

        json = builder.build();
        Ticket newJsonTicket = TicketFactory.convert(json);

        assertEquals(ticket.getToken(), newJsonTicket.getToken());
        assertEquals(ticket.getCreateUser(), newJsonTicket.getCreateUser());
    }


    @Test
    public void testConvertToJsonObject() {
        ticket.setTicketType(null);
        JsonObject constructedNewJson = TicketFactory.convertToJsonObject(ticket);

        JsonObjectBuilder builder = Json.createObjectBuilder();
        JsonObjectBuilder typeBuilder = Json.createObjectBuilder();
        builder.addNull("id");
        builder.add("userId", 2);
        builder.add("token", "token");
        builder.add("ticketType","{}");
        builder.addNull("expireDate");
        builder.addNull("createUser");
        builder.addNull("lastUpdateUser");
        builder.addNull("createDate");
        builder.addNull("lastUpdate");



        json = builder.build();

        assertEquals(json.toString(), constructedNewJson.toString());
    }

}