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

package io.radien.ms.ticketmanagement.client.entities;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Active Ticket test class {@link io.radien.ms.ticketmanagement.client.entities.Ticket}
 *
 * @author Rui Soares
 */
public class TicketTest {

    Ticket ticket;

    /**
     * Active ticket entity constructor test
     */
    public TicketTest(){
        ticket = new Ticket(2L, 2L, LocalDate.now(), "token", "data");
    }

    /**
     * Test Method to validate the three existent constructors
     */
    @Test
    public void testTicketConstructor(){
        Ticket ticket2 = new Ticket(3L, 3L, LocalDate.now(), "token2", "data2");

        assertEquals((Long) 3L, ticket2.getUserId());
        assertEquals((Long) 3L, ticket2.getTicketType());
        assertEquals("token2", ticket2.getToken());
        assertEquals("data2", ticket2.getData());

        Ticket ticket3 = new Ticket(ticket);

        assertEquals((Long) 2L, ticket3.getUserId());
        assertEquals((Long) 2L, ticket3.getTicketType());
        assertEquals("token", ticket3.getToken());
        assertEquals("data", ticket3.getData());

        Ticket ticket4 = new Ticket();

        assertNull(ticket4.getToken());
        assertNull(ticket4.getUserId());
        assertNull(ticket4.getTicketType());
        assertNull(ticket4.getData());
    }
}