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
package io.radien.ms.ticketmanagement.client.providers;

import io.radien.ms.ticketmanagement.client.entities.Ticket;
import io.radien.ms.ticketmanagement.client.entities.TicketType;
import io.radien.ms.ticketmanagement.client.services.TicketFactory;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Rui Soares
 */
public class TicketMessageBodyWriterTest extends TestCase {

    @Test
    public void testIsWriteable() {
        TicketMessageBodyWriter target = new TicketMessageBodyWriter();
        assertTrue(target.isWriteable(Ticket.class,null,null,null));
    }

    @Test
    public void testGetSize() {
        TicketMessageBodyWriter target = new TicketMessageBodyWriter();
        assertEquals(0L,target.getSize(null,null,null,null,null));
    }

    @Test
    public void testWriteTo() throws IOException {
        TicketType ticketType = TicketType.getById(1L);
        long userId = 2L;
        long createUser = 3L;
        Ticket ticket = TicketFactory.create(userId,"token", ticketType.getId(),null, createUser);

        String result = "{\"id\":null," +
                "\"userId\":" + ticket.getUserId() +  "," +
                "\"token\":\"token\"," +
                "\"data\":null," +
                "\"ticketType\":" + ticketType.getId() + "," +
                "\"expireDate\":\"" + ticket.getExpireDate() + "\"," +
                "\"createUser\":" + ticket.getCreateUser() +  "," +
                "\"lastUpdateUser\":" + ticket.getCreateUser() +  "}";

        TicketMessageBodyWriter target = new TicketMessageBodyWriter();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        target.writeTo(ticket,null,null,null, null,null, baos);

        assertEquals(result,baos.toString().substring(0, baos.toString().indexOf("createDate")-2).trim() + "}");

    }
}