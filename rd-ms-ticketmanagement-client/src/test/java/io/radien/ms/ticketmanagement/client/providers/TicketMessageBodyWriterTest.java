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
import io.radien.ms.ticketmanagement.client.services.TicketFactory;
import junit.framework.TestCase;
import org.junit.Test;

import javax.json.Json;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;

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
        String result = "{\"id\":null," +
                "\"userId\":1," +
                "\"token\":\"token\"," +
                "\"ticketType\":1," +
                "\"expireDate\":" + Json.createValue(LocalDate.now().plusDays(30).toString()) + "," +
                "\"createUser\":1," +
                "\"lastUpdateUser\":1}";
        TicketMessageBodyWriter target = new TicketMessageBodyWriter();
        Ticket ticket = TicketFactory.create(1L,"token", 1L,null, 1L);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        target.writeTo(ticket,null,null,null, null,null, baos);

        assertEquals(result,baos.toString().substring(0, baos.toString().indexOf("createDate")-2).trim() + "}");

    }
}