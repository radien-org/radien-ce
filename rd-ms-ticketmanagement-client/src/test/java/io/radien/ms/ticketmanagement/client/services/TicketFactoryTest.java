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

import io.radien.api.entity.Page;
import io.radien.ms.ticketmanagement.client.entities.Ticket;
import junit.framework.TestCase;
import org.junit.Test;

import javax.json.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
        Ticket newTicketConstructed = TicketFactory.create(2L, "token", 1L, "data", 1L);

        assertEquals(ticket.getToken(), newTicketConstructed.getToken());
        assertEquals(ticket.getUserId(), newTicketConstructed.getUserId());
    }

    @Test
    public void testCreateInvalid(){
        assertThrows(IllegalStateException.class, () ->TicketFactory.create(2L, "token", null, "data", 1L));
    }

    @Test
    public void testConvert() throws ParseException {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.addNull("id");
        builder.add("userId", 1L);
        builder.add("token", "token");
        builder.add("data", "myData");
        builder.add("ticketType", 1L);

        json = builder.build();
        Ticket newJsonTicket = TicketFactory.convert(json);

        assertEquals(ticket.getToken(), newJsonTicket.getToken());
        assertEquals(ticket.getCreateUser(), newJsonTicket.getCreateUser());
    }

    @Test
    public void testConvert2() throws ParseException {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.addNull("id");
        builder.add("userId", 1L);
        builder.add("token", "token");
        builder.add("data", "myData");
        builder.add("ticketType", 1L);
        builder.add("createDate", LocalDate.now().toString());
        builder.add("lastUpdate", LocalDate.now().toString());

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
        builder.addNull("ticketType");
        builder.add("data", "data");
        builder.addNull("expireDate");
        builder.addNull("createUser");
        builder.addNull("lastUpdateUser");
        builder.addNull("createDate");
        builder.addNull("lastUpdate");

        json = builder.build();

        assertEquals(json.toString(), constructedNewJson.toString());
    }

    @Test
    public void testConversionToPage() {
        String example = "{\n" +
                "\"currentPage\": 1,\n" +
                "\"results\": [\n" +
                "{\n" +
                "\"id\":1," +
                "\"userId\": 1," +
                "\"ticketType\": 1" +
                "}\n" +
                "],\n" +
                "\"totalPages\": 1,\n" +
                "\"totalResults\": 4\n" +
                "}";

        InputStream in = new ByteArrayInputStream(example.getBytes());

        try(JsonReader reader = Json.createReader(in)) {
            JsonObject jsonObject = reader.readObject();
            Page<Ticket> tenant = TicketFactory.convertJsonToPage(jsonObject);
            assertEquals(1, tenant.getCurrentPage());
            assertEquals(1, tenant.getTotalPages());
            assertEquals(4, tenant.getTotalResults());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}