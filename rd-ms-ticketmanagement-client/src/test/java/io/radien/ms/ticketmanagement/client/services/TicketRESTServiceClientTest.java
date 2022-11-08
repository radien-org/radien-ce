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

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.ticket.SystemTicket;
import io.radien.api.model.ticket.SystemTicketSearchFilter;
import io.radien.api.security.TokensPlaceHolder;
import io.radien.api.util.FactoryUtilService;
import io.radien.exception.SystemException;
import io.radien.exception.TokenExpiredException;
import io.radien.ms.authz.client.UserClient;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.ticketmanagement.client.entities.Ticket;
import io.radien.ms.ticketmanagement.client.entities.TicketSearchFilter;
import io.radien.ms.ticketmanagement.client.util.ClientServiceUtil;
import io.radien.ms.ticketmanagement.client.util.TicketModelMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Rui Soares
 */
public class TicketRESTServiceClientTest {

    @InjectMocks
    TicketRESTServiceClient target;

    @Mock
    ClientServiceUtil ticketServiceUtil;

    @Mock
    AuthorizationChecker authorizationChecker;

    @Mock
    UserClient userClient;

    @Mock
    TokensPlaceHolder tokensPlaceHolder;

    @Mock
    OAFAccess oafAccess;

    private final Ticket dummyTicket = new Ticket();

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);

        dummyTicket.setId(2L);
        dummyTicket.setUserId(2L);
        dummyTicket.setTicketType(1L);
        dummyTicket.setToken("token");
        dummyTicket.setData("data");
    }

    private String getTicketManagementURL(){
        String url = "";
        when(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TICKETMANAGEMENT)).thenReturn(url);
        return url;
    }

    @Test
    public void testGetTicketById() throws MalformedURLException, SystemException {
        InputStream is = new ByteArrayInputStream(TicketModelMapper.map(dummyTicket).toString().getBytes());
        Response response = Response.ok(is).build();
        TicketResourceClient resourceClient = Mockito.mock(TicketResourceClient.class);
        when(ticketServiceUtil.getTicketResourceClient(getTicketManagementURL())).thenReturn(resourceClient);
        when(resourceClient.getById(anyLong())).
                thenReturn(response);
        Optional<SystemTicket> opt = target.getTicketById(dummyTicket.getId());
        assertNotNull(opt);
        assertTrue(opt.isPresent());
        assertEquals(opt.get().getId(), dummyTicket.getId());
    }

    @Test
    public void testGetTicketByIdException() throws Exception {
        boolean success = false;
        when(ticketServiceUtil.getTicketResourceClient(getTicketManagementURL())).thenThrow(new MalformedURLException());
        try {
            target.getTicketById(3L);
        }catch (SystemException se){
            success = true;
        }
        assertTrue(success);
    }

    @Test
    public void testGetTicketByToken() throws MalformedURLException, SystemException {
        InputStream is = new ByteArrayInputStream(TicketModelMapper.map(dummyTicket).toString().getBytes());
        Response response = Response.ok(is).build();
        TicketResourceClient resourceClient = Mockito.mock(TicketResourceClient.class);
        when(ticketServiceUtil.getTicketResourceClient(getTicketManagementURL())).thenReturn(resourceClient);
        when(resourceClient.getByToken(anyString())).
                thenReturn(response);
        SystemTicket opt = target.getTicketByToken(dummyTicket.getToken());
        assertNotNull(opt);
        assertEquals(opt.getId(), dummyTicket.getId());
    }

    @Test
    public void testGetTicketByTokenException() throws Exception {
        boolean success = false;
        when(ticketServiceUtil.getTicketResourceClient(getTicketManagementURL())).thenThrow(new MalformedURLException());
        try {
            target.getTicketByToken("token");
        }catch (SystemException se){
            success = true;
        }
        assertTrue(success);
    }


    /**
     * Test for method getTicketById(id)
     * It corresponds to the unsuccessful situation where JWT expiration occurs and
     * is not possible to recover from that
     * Expected result (FAIL): SystemException thrown due JWT expiration during the reattempt
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testGetTicketByIdWithTokenException() throws MalformedURLException, SystemException {
        TicketResourceClient resourceClient = Mockito.mock(TicketResourceClient.class);
        when(ticketServiceUtil.getTicketResourceClient(getTicketManagementURL())).thenReturn(resourceClient);
        when(resourceClient.getById(anyLong())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getTicketById(1L);
    }

    @Test
    public void testGetAll() throws MalformedURLException, SystemException {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        FactoryUtilService.addValueInt(builder, "currentPage", 1);
        FactoryUtilService.addValueInt(builder, "totalPages", 1);
        FactoryUtilService.addValueInt(builder, "totalResults", 1);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeObject(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        Response response = Response.ok(is).build();

        TicketResourceClient ticketResourceClient = Mockito.mock(TicketResourceClient.class);

        when(ticketResourceClient.getAll(null, null, null, null, null, false,1, 10, null, false)).thenReturn(response);

        when(ticketServiceUtil.getTicketResourceClient(getTicketManagementURL())).thenReturn(ticketResourceClient);

        List<? extends SystemTicket> list = new ArrayList<>();

        SystemTicketSearchFilter filter = new TicketSearchFilter();
        filter.setIsLogicalConjunction(false);
        List<? extends SystemTicket> returnedList = target.getAll(filter,1, 10, null, false).getResults();

        assertEquals(list, returnedList);
    }

    @Test
    public void testGetAllException() throws Exception {
        boolean success = false;
        when(ticketServiceUtil.getTicketResourceClient(getTicketManagementURL())).thenThrow(new MalformedURLException());
        try {
            target.getAll(null,1, 10, null, false);
        }catch (SystemException se){
            success = true;
        }
        assertTrue(success);
    }

    /**
     * Test for method getAll(search, page, size, sortBy, boolean)
     * It corresponds to the unsuccessful situation where JWT expiration occurs and
     * is not possible to recover from that
     * Expected result (FAIL): SystemException thrown
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testGetAllWithTokenException() throws MalformedURLException, SystemException {
        TicketResourceClient resourceClient = Mockito.mock(TicketResourceClient.class);
        when(ticketServiceUtil.getTicketResourceClient(getTicketManagementURL())).thenReturn(resourceClient);
        when(resourceClient.getAll(any(), any(), any(), any(), any(), anyBoolean(), anyInt(),anyInt(),anyList(), anyBoolean())).
                thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        SystemTicketSearchFilter filter = new TicketSearchFilter();
        target.getAll(filter, 1, 10, new ArrayList<>(), true);
    }

    @Test
    public void testCreate() throws MalformedURLException, SystemException {
        TicketResourceClient ticketResourceClient = Mockito.mock(TicketResourceClient.class);
        when(ticketResourceClient.create(any())).thenReturn(Response.ok().build());
        when(ticketServiceUtil.getTicketResourceClient(getTicketManagementURL())).thenReturn(ticketResourceClient);
        assertTrue(target.create(new Ticket()));
    }

    @Test
    public void testCreateFalse() throws MalformedURLException, SystemException {
        TicketResourceClient ticketResourceClient = Mockito.mock(TicketResourceClient.class);
        when(ticketServiceUtil.getTicketResourceClient(getTicketManagementURL())).thenReturn(ticketResourceClient);
        when(ticketResourceClient.create(any())).thenReturn(Response.notModified().entity("teste").build());
        boolean sucess = target.create(new Ticket());
        assertFalse(sucess);
    }

    @Test
    public void testCreateException() throws Exception {
        boolean success = false;
        TicketResourceClient ticketResourceClient = Mockito.mock(TicketResourceClient.class);
        when(ticketServiceUtil.getTicketResourceClient(getTicketManagementURL())).thenReturn(ticketResourceClient);
        when(ticketResourceClient.create(any())).thenThrow(new ProcessingException("teste"));
        try {
            target.create(new Ticket());
        }catch (Exception se){
            success = true;
        }
        assertTrue(success);

        success=false;
        when(ticketServiceUtil.getTicketResourceClient(any())).thenThrow(MalformedURLException.class);

        try {
            target.create(new Ticket());
        }catch (SystemException e){
            success = true;
        }

        assertTrue(success);
    }

    /**
     * Test for method create(ticket)
     * It corresponds to the unsuccessful situation where JWT expiration occurs and
     * is not possible to recover from that
     * Expected result (FAIL): SystemException thrown
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testCreateWithTokenException() throws MalformedURLException, SystemException {
        Ticket toBeCreated = mock(Ticket.class);
        TicketResourceClient resourceClient = Mockito.mock(TicketResourceClient.class);
        when(ticketServiceUtil.getTicketResourceClient(getTicketManagementURL())).thenReturn(resourceClient);
        when(resourceClient.create(toBeCreated)).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.create(toBeCreated);
    }

    @Test
    public void testDelete() throws MalformedURLException {
        TicketResourceClient ticketResourceClient = Mockito.mock(TicketResourceClient.class);
        when(ticketResourceClient.delete(dummyTicket.getId())).thenReturn(Response.ok().build());
        when(ticketServiceUtil.getTicketResourceClient(getTicketManagementURL())).thenReturn(ticketResourceClient);

        boolean success = false;
        try {
            assertTrue(target.delete(2L));
        } catch (Exception e) {
            success = true;
        }
        assertFalse(success);
    }

    @Test
    public void testDeleteReturnFalse() throws MalformedURLException, SystemException {
        TicketResourceClient ticketResourceClient = Mockito.mock(TicketResourceClient.class);
        when(ticketResourceClient.delete(2L)).thenReturn(Response.serverError().entity("teste").build());
        when(ticketServiceUtil.getTicketResourceClient(getTicketManagementURL())).thenReturn(ticketResourceClient);

        assertFalse(target.delete(2L));
    }

    @Test
    public void testDeleteException() throws Exception {
        boolean success = false;
        TicketResourceClient ticketResourceClient = Mockito.mock(TicketResourceClient.class);
        when(ticketResourceClient.delete(dummyTicket.getId())).thenThrow(new ProcessingException("teste"));
        when(ticketServiceUtil.getTicketResourceClient(getTicketManagementURL())).thenReturn(ticketResourceClient);
        try {
            target.delete(2L);
        }catch (Exception se){
            success = true;
        }

        assertTrue(success);

        success=false;
        when(ticketServiceUtil.getTicketResourceClient(any())).thenThrow(MalformedURLException.class);

        try {
            target.delete(2L);
        }catch (SystemException e){
            success = true;
        }

        assertTrue(success);
    }



    /**
     * Test for method delete(ticketId)
     * It corresponds to the unsuccessful situation where JWT expiration occurs and
     * is not possible to recover from that
     * Expected result (FAIL): SystemException thrown
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testDeleteWithTokenException() throws MalformedURLException, SystemException {
        TicketResourceClient resourceClient = Mockito.mock(TicketResourceClient.class);
        when(ticketServiceUtil.getTicketResourceClient(getTicketManagementURL())).thenReturn(resourceClient);
        when(resourceClient.delete(anyLong())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.delete(1L);
    }


    @Test
    public void testUpdate() throws MalformedURLException, SystemException {
        TicketResourceClient ticketResourceClient = Mockito.mock(TicketResourceClient.class);
        when(ticketResourceClient.update(2L, dummyTicket)).thenReturn(Response.ok().build());
        when(ticketServiceUtil.getTicketResourceClient(getTicketManagementURL())).thenReturn(ticketResourceClient);
        dummyTicket.setData("data-update");

        assertTrue(target.update(dummyTicket));
    }

    @Test
    public void testUpdateReturnFalse() throws MalformedURLException, SystemException {
        TicketResourceClient ticketResourceClient = Mockito.mock(TicketResourceClient.class);
        when(ticketResourceClient.update(2L, dummyTicket)).thenReturn(Response.serverError().entity("teste").build());
        when(ticketServiceUtil.getTicketResourceClient(getTicketManagementURL())).thenReturn(ticketResourceClient);

        assertFalse(target.update(dummyTicket));
    }

    @Test
    public void testUpdateException() throws Exception {
        boolean success = false;
        TicketResourceClient ticketResourceClient = Mockito.mock(TicketResourceClient.class);
        when(ticketResourceClient.update(2L, dummyTicket)).thenThrow(new ProcessingException("teste"));
        when(ticketServiceUtil.getTicketResourceClient(getTicketManagementURL())).thenReturn(ticketResourceClient);
        try {
            target.update(dummyTicket);
        }catch (Exception se){
            success = true;
        }
        assertTrue(success);

        success=false;
        when(ticketServiceUtil.getTicketResourceClient(any())).thenThrow(MalformedURLException.class);

        try {
            target.update(dummyTicket);
        }catch (SystemException e){
            success = true;
        }

        assertTrue(success);
    }

    /**
     * Test for method update(ticket)
     * It corresponds to the unsuccessful situation where JWT expiration occurs and
     * is not possible to recover from that
     * Expected result (FAIL): SystemException thrown
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testUpdateWithTokenException() throws MalformedURLException, SystemException {
        long id = 1L;
        Ticket toBeUpdated = new Ticket();
        toBeUpdated.setId(id);
        TicketResourceClient resourceClient = Mockito.mock(TicketResourceClient.class);
        when(ticketServiceUtil.getTicketResourceClient(getTicketManagementURL())).thenReturn(resourceClient);
        when(resourceClient.update(id, toBeUpdated)).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.update(toBeUpdated);
    }

    @Test(expected = SystemException.class)
    public void testGetTicketByIdTokenExpiration() throws Exception {
        TicketResourceClient ticketResourceClient = Mockito.mock(TicketResourceClient.class);

        when(ticketServiceUtil.getTicketResourceClient(getTicketManagementURL())).thenReturn(ticketResourceClient);
        when(ticketResourceClient.getById(anyLong())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getTicketById(2L);
    }

    @Test(expected = SystemException.class)
    public void testGetAllTokenExpiration() throws Exception {
        TicketResourceClient ticketResourceClient = Mockito.mock(TicketResourceClient.class);

        when(ticketServiceUtil.getTicketResourceClient(getTicketManagementURL())).thenReturn(ticketResourceClient);
        when(ticketResourceClient.getAll(any(), any(), any(), any(), any(), anyBoolean(), anyInt(), anyInt(), any(), anyBoolean())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        SystemTicketSearchFilter filter = new TicketSearchFilter();
        target.getAll(filter, 1, 10, null, false);
    }

    @Test(expected = SystemException.class)
    public void testCreateTokenExpiration() throws Exception {
        TicketResourceClient ticketResourceClient = Mockito.mock(TicketResourceClient.class);

        when(ticketServiceUtil.getTicketResourceClient(getTicketManagementURL())).thenReturn(ticketResourceClient);
        when(ticketResourceClient.create(any())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.create(new Ticket());
    }

    @Test(expected = SystemException.class)
    public void testDeleteTokenExpiration() throws Exception {
        TicketResourceClient ticketResourceClient = Mockito.mock(TicketResourceClient.class);

        when(ticketServiceUtil.getTicketResourceClient(getTicketManagementURL())).thenReturn(ticketResourceClient);
        when(ticketResourceClient.delete(anyLong())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.delete(2L);
    }

    @Test(expected = SystemException.class)
    public void testUpdateTokenExpiration() throws Exception {
        TicketResourceClient ticketResourceClient = Mockito.mock(TicketResourceClient.class);

        when(ticketServiceUtil.getTicketResourceClient(getTicketManagementURL())).thenReturn(ticketResourceClient);
        when(ticketResourceClient.update(anyLong(), any())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        Ticket ticket = new Ticket();
        ticket.setId(2L);

        target.update(ticket);
    }

}