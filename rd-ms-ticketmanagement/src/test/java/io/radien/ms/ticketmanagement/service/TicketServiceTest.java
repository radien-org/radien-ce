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
package io.radien.ms.ticketmanagement.service;

import io.radien.api.entity.Page;
import io.radien.api.model.ticket.SystemTicket;
import io.radien.api.service.ticket.TicketServiceAccess;
import io.radien.exception.*;
import io.radien.ms.ticketmanagement.entities.TicketEntity;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

/**
 * @author Rui Soares
 */
public class TicketServiceTest {

    Properties p;
    TicketServiceAccess ticketServiceAccess;
    SystemTicket systemTicket;

    public TicketServiceTest() throws Exception {
        p = new Properties();
        p.put("appframeDatabase", "new://Resource?type=DataSource");
        p.put("appframeDatabase.JdbcDriver", "org.hsqldb.jdbcDriver");
        p.put("appframeDatabase.JdbcUrl", "jdbc:hsqldb:mem:radienTest");
        p.put("appframeDatabase.userName", "sa");
        p.put("appframeDatabase.password", "");
        p.put("openejb.deployments.classpath.include",".*ticket.*");
        p.put("openejb.deployments.classpath.exclude",".*client.*");


        final Context context = EJBContainer.createEJBContainer(p).getContext();

        ticketServiceAccess = (TicketServiceAccess) context.lookup("java:global/rd-ms-ticketmanagement//TicketService");


        Page<? extends SystemTicket> tickets = ticketServiceAccess.getAll(null, 1, 10, new ArrayList<>(), true);
        if (tickets.getResults().isEmpty()) {
            systemTicket = new TicketEntity();
            systemTicket.setData("systemData");
            systemTicket.setToken(RandomStringUtils.random(12, true, true));
            systemTicket.setTicketType(1L);
            ticketServiceAccess.create(systemTicket);
        }
        else {
            systemTicket = tickets.getResults().get(0);
        }
    }

    /**
     * Add ticket test.
     * Will create and save the ticket.
     * Expected result: Success.
     * Tested methods: void save(Ticket ticket)
     *
     * @throws TicketException in case of issues with data
     */
    @Test
    public void testCreate() throws TicketException, UniquenessConstraintException {
        SystemTicket result = createTicket(1L);
        assertNotNull(result);
    }

    /**
     * Update ticket test with duplicated name.
     * Will create and save the ticket, with empty token.
     * Expected result: Throw treated exception Error 101 Message There is no token field provided.
     * Tested methods: void update(Ticket ticket)
     */
    @Test
    public void testUpdateEmpty() throws TicketException, UniquenessConstraintException {
        SystemTicket c = createTicket(1L);
        c.setToken(null);
        Exception exception = assertThrows(TicketException.class, () ->
                ticketServiceAccess.update(new TicketEntity(new io.radien.ms.ticketmanagement.client.entities.Ticket(
                        1L, 2L, LocalDate.now(), null, "data"))));
        String expectedMessage = GenericErrorCodeMessage.TICKET_FIELD_NOT_PROVIDED.toString("token");
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }


    /**
     * Gets tenant using the PK (id).
     * Will create a new tenant, save it into the DB and retrieve the specific tenant using the ID.
     * Expected result: will return the correct inserted tenant.
     * Tested methods: SystemTenant get(Long ticketId)
     * @throws SystemException in case of requested action is not well constructed
     * @throws TicketException in case of any issue in the data
     */
    @Test
    public void testGetById() throws SystemException, TicketException, UniquenessConstraintException {
        String data = "myData";
        SystemTicket c = new TicketEntity(new io.radien.ms.ticketmanagement.client.entities.Ticket(
                1L, 2L, LocalDate.now(), RandomStringUtils.random(12, true, true), data));
        ticketServiceAccess.create(c);
        SystemTicket result = ticketServiceAccess.get(c.getId());
        assertNotNull(result);
        assertEquals(c.getData(), result.getData());
    }

    /**
     * Deletes inserted ticket using the PK (id).
     * Will create a new ticket, save it into the DB and delete it after using the specific ID.
     * Expected result: will return null when retrieving the ticket.
     * Tested methods: void delete(Long ticketId)
     *
     * @throws SystemException in case of requested action is not well constructed
     * @throws TicketException in case of any issue in the data
     */
    @Test
    public void testDeleteById() throws TicketException, SystemException, UniquenessConstraintException {
        SystemTicket ticket = createTicket(1L);
        SystemTicket result = ticketServiceAccess.get(ticket.getId());
        assertNotNull(result);
        assertEquals(ticket.getData(), result.getData());
        ticketServiceAccess.delete(ticket.getId());
        result = ticketServiceAccess.get(ticket.getId());
        assertNull(result);
    }


    /**
     * Test updates the ticket information.
     * @throws Exception in case of ticket to be updated not found
     */
    @Test
    public void testUpdateSuccess() throws Exception {
        SystemTicket c1 = createTicket(1L);
        String token = "myToken";
        c1.setToken(token);
        ticketServiceAccess.update(c1);
        c1 = ticketServiceAccess.get(c1.getId());

        assertEquals("myToken", c1.getToken());
    }

    /**
     * Test of creation of tickets
     * @param ticketType of the ticket to create
     * @return system ticket
     * @throws TicketException in case of any issue in the data
     */
    private SystemTicket createTicket(Long ticketType) throws TicketException, UniquenessConstraintException {
        SystemTicket systemTicket = new TicketEntity();
        systemTicket.setUserId(new Random().nextLong());
        systemTicket.setTicketType(ticketType);
        systemTicket.setData("Random Data");
        systemTicket.setToken(RandomStringUtils.random(12, true, true));
        ticketServiceAccess.create(systemTicket);
        return systemTicket;
    }

    /**
     * Test of get all the tickets
     * @throws TicketException in case of any issue in the data
     */
    @Test
    public void testGetAll() throws TicketException, UniquenessConstraintException {
        String data = "testGetAll";
        SystemTicket c = new TicketEntity();
        c.setId(100L);
        c.setUserId(new Random().nextLong());
        c.setTicketType(2L);
        c.setData(data);
        c.setToken(RandomStringUtils.random(12, true, true));
        ticketServiceAccess.create(c);
        Page<SystemTicket> result = ticketServiceAccess.getAll(null,1,10,Collections.singletonList("data"),true);
        assertNotNull(result);
    }

    @Test
    public void testGetAllSearchNotNullSort() throws TicketException, UniquenessConstraintException {
        String data = "testGetAll2";
        SystemTicket c = new TicketEntity();
        c.setId(102L);
        c.setData(data);
        c.setTicketType(1L);
        c.setUserId(new Random().nextLong());
        c.setToken(RandomStringUtils.random(12, true, true));
        ticketServiceAccess.create(c);

        List<String> sortBy = new ArrayList<>();
        sortBy.add("data");

        Page<SystemTicket> result = ticketServiceAccess.getAll("testGetAll2",1,10,sortBy,false);
        assertNotNull(result);

        Page<SystemTicket> result2 = ticketServiceAccess.getAll("testGetAll2",1,10,sortBy,true);
        assertNotNull(result2);
    }

    /**
     * Test to retrieve all the tickets
     * @throws TicketException in case of any issue in the data
     */
    @Test
    public void testRetrieveAllTickets() throws TicketException, UniquenessConstraintException {

        SystemTicket ticket = new TicketEntity();
        ticket.setTicketType(1L);
        ticket.setToken(RandomStringUtils.random(12, true, true));
        ticket.setData("myFirstTicket");
        ticket.setUserId(new Random().nextLong());
        ticketServiceAccess.create(ticket);

        ticket = new TicketEntity();
        ticket.setTicketType(2L);
        ticket.setToken(RandomStringUtils.random(12, true, true));
        ticket.setData("mySecondTicket");
        ticket.setUserId(new Random().nextLong());

        ticketServiceAccess.create(ticket);

        // Page size = 100 -> Overkill!!
        Page<SystemTicket> page = ticketServiceAccess.getAll(null,1,100,null,false);
        assertTrue(page.getTotalResults()>2);
    }

    /**
     * Tests to validate ticket rules
     */
    @Test
    public void creatingInvalidTicket() {
        SystemTicket ticket = new TicketEntity();
        TicketException e = assertThrows(TicketException.class, ()-> ticketServiceAccess.create(ticket));
        assertEquals(GenericErrorCodeMessage.TICKET_FIELD_NOT_PROVIDED.toString("token"), e.getMessage());
    }

    /**
     * Update ticket test with duplicated token.
     * Will create and save the ticket, with an already existent token.
     * Expected result: Throw treated exception Error 101 Message There is more than one ticket with the same token.
     * Tested methods: void update(Ticket ticket)
     */
    @Test
    public void testUpdateDuplicated() throws UniquenessConstraintException, TicketException {
        SystemTicket c = createTicket(2L);
        c.setToken("token11");
        TicketEntity duplicated = new TicketEntity(new io.radien.ms.ticketmanagement.client.entities.Ticket(2L, 2L, LocalDate.now(), "token11", "dataaaa"));
        Exception exception = assertThrows(UniquenessConstraintException.class, () ->
                ticketServiceAccess.update(duplicated));
        String expectedMessage = GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Token");
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testCreateDuplicated() throws UniquenessConstraintException, TicketException {
        SystemTicket c = createTicket(1L);
        c.setToken("token");
        TicketEntity duplicated = new TicketEntity(new io.radien.ms.ticketmanagement.client.entities.Ticket(2L, 2L, LocalDate.now(), "token", "dataaaa"));
        duplicated.setId(99L);
        Exception exception = assertThrows(UniquenessConstraintException.class, () ->
                ticketServiceAccess.create(duplicated));
        String expectedMessage = GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Token");
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testCreateEmptyTokenTicket() {
        Exception exception = assertThrows(TicketException.class, () ->
                ticketServiceAccess.create(new TicketEntity(new io.radien.ms.ticketmanagement.client.entities.Ticket(2L, 2L, LocalDate.now(), null, "dataaaa"))));
        String expectedMessage = GenericErrorCodeMessage.TICKET_FIELD_NOT_PROVIDED.toString("token");
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}