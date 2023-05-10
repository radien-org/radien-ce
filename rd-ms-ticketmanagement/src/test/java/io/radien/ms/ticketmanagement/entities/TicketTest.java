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
package io.radien.ms.ticketmanagement.entities;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;


public class TicketTest {
    TicketEntity ticket;
    TicketEntity ticket2;

    public TicketTest(){
        ticket2 = new TicketEntity(new io.radien.ms.ticketmanagement.client.entities.Ticket());
        ticket = new TicketEntity();
        ticket.setData("a");
        ticket.setId(1L);
        ticket2.setData("b");
        ticket2.setId(2L);

        ticket.setTicketType(1L);
        ticket2.setTicketType(2L);

        ticket.setUserId(1L);
        ticket2.setUserId(2L);

        ticket.setToken("myToken1");
        ticket2.setToken("myToken2");

        ticket.setExpireDate(LocalDateTime.of(1900,1, 1, 1, 1));
        ticket2.setExpireDate(LocalDateTime.of(1900,1,4, 1, 1));

    }

    @Test
    public void getId() {
        assertEquals((Long) 1L, ticket.getId());
        assertEquals((Long) 2L, ticket2.getId());
    }

    @Test
    public void getData() {
        assertEquals("a", ticket.getData());
        assertEquals("b", ticket2.getData());
    }

    @Test
    public void getUserId() {
        assertEquals((Long)1L, ticket.getUserId());
        assertEquals((Long)2L, ticket2.getUserId());
    }

    @Test
    public void getTicketType() {
        assertEquals((Long)1L, ticket.getTicketType());
        assertEquals((Long)2L, ticket2.getTicketType());
    }

    @Test
    public void getToken() {
        assertEquals("myToken1", ticket.getToken());
        assertEquals("myToken2", ticket2.getToken());
    }

    @Test
    public void getExpireDate() {
        assertEquals(LocalDateTime.of(1900,1,1, 1, 1), ticket.getExpireDate());
        assertEquals(LocalDateTime.of(1900,1,4, 1, 1), ticket2.getExpireDate());
    }

}