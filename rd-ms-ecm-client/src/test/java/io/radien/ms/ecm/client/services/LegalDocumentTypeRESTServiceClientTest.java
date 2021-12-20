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
package io.radien.ms.ecm.client.services;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.entity.Page;
import io.radien.api.model.legal.SystemLegalDocumentType;
import io.radien.exception.InternalServerErrorException;
import io.radien.exception.NotFoundException;
import io.radien.exception.SystemException;
import io.radien.ms.ecm.client.entities.legal.LegalDocumentType;
import io.radien.ms.ecm.client.util.ClientServiceUtil;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test class regarding {@link LegalDocumentTypeRESTServiceClient} REST Client
 */
public class LegalDocumentTypeRESTServiceClientTest {

    @InjectMocks
    LegalDocumentTypeRESTServiceClient target;

    @Mock
    ClientServiceUtil clientServiceUtil;

    @Mock
    OAFAccess oafAccess;

    /**
     * Preparation for the tests
     */
    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Private method to create the Legal Document Type management URL endpoint
     * @return the role management url endpoint
     */
    protected String getLegalDocumentTypeManagementUrl(){
        String url = "";
        when(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ECM)).thenReturn(url);
        return url;
    }

    /**
     * Test for method {@link LegalDocumentTypeRESTServiceClient#getAll(String, int, int, List, boolean)}.
     * Scenario: Legal Document Types page successfully retrieved
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testGetAll() throws MalformedURLException, SystemException{
        String results = "[" +
                "{\"id\": 1, \"tenantId\": 1, \"name\": \"ToU\", \"toBeShown\": true, \"toBeAccepted\": true}, " +
                "{\"id\": 2, \"tenantId\": 2, \"name\": \"ToU\", \"toBeShown\": true, \"toBeAccepted\": true}, " +
                "{\"id\": 3, \"tenantId\": 3, \"name\": \"ToU\", \"toBeShown\": true, \"toBeAccepted\": true}" +
                "]";

        String pageAsJsonString = "{\"currentPage\": 1, \"totalResults\": 3, \"totalPages\": 1, " +
                "\"results\": " + results + "}";

        InputStream is = new ByteArrayInputStream(pageAsJsonString.getBytes());
        Response response = Response.ok(is).build();
        LegalDocumentTypeResourceClient client = mock(LegalDocumentTypeResourceClient.class);

        // Standard parameters
        String searchName = "ToU";
        int pageNo = 1, pageSize = 10;
        List<String> sortBy = new ArrayList<>();

        when(client.getAll(searchName, pageNo, pageSize, sortBy, true)).thenReturn(response);
        when(clientServiceUtil.getLegalDocumentTypeClient(getLegalDocumentTypeManagementUrl())).thenReturn(client);

        Page<? extends SystemLegalDocumentType> result = target.getAll(searchName, pageNo, pageSize,
                sortBy, true);

        assertNotNull(result);
        assertEquals(1, result.getTotalPages());
        assertEquals(3,result.getResults().size());
    }

    /**
     * Test for method {@link LegalDocumentTypeRESTServiceClient#getAll(String, int, int, List, boolean)}.
     * Scenario: Legal Document Types page unsuccessfully retrieved due an Malformed URL
     * @throws MalformedURLException for url informed incorrectly
     */
    @Test
    public void testGetAllWhenMalformedURL() throws MalformedURLException {
        String errorMsg = "URL under incorrect format";
        String expectedErrorMsg = "Message(s): wrapped exception: java.net.MalformedURLException with message: " + errorMsg;
        String pageAsJsonString = "{\"currentPage\": 1, \"totalResults\": 0, \"totalPages\": 1, \"results\": []}";

        InputStream is = new ByteArrayInputStream(pageAsJsonString.getBytes());
        Response response = Response.ok(is).build();
        LegalDocumentTypeResourceClient client = mock(LegalDocumentTypeResourceClient.class);

        // Standard parameters
        String searchName = "ToU";
        int pageNo = 1, pageSize = 10;
        List<String> sortBy = new ArrayList<>();

        when(client.getAll(searchName, pageNo, pageSize, sortBy, true)).thenReturn(response);
        when(clientServiceUtil.getLegalDocumentTypeClient(getLegalDocumentTypeManagementUrl())).
                thenThrow(new MalformedURLException(errorMsg));

        SystemException se = assertThrows(SystemException.class, ()-> target.getAll(searchName,
                pageNo, pageSize, sortBy, true));

        assertEquals(expectedErrorMsg, se.getMessage());
    }

    /**
     * Test for method {@link LegalDocumentTypeRESTServiceClient#getAll(String, int, int, List, boolean)}.
     * Scenario: Legal Document Types page unsuccessfully retrieved due an Internal Server Error (Endpoint processing)
     * @throws MalformedURLException for url informed incorrectly
     */
    @Test
    public void testGetAllWhenInternalServerErrorException() throws MalformedURLException {
        String errorMsg = "Internal Server Error while processing request";
        String exceptionClass = InternalServerErrorException.class.getName();
        String expectedErrorMsg = "Message(s): wrapped exception: " + exceptionClass + " with message: " + errorMsg;

        LegalDocumentTypeResourceClient client = mock(LegalDocumentTypeResourceClient.class);

        // Standard parameters
        String searchName = "ToU";
        int pageNo = 1, pageSize = 10;
        List<String> sortBy = new ArrayList<>();

        when(client.getAll(searchName, pageNo, pageSize, sortBy, true)).
                thenThrow(new InternalServerErrorException(errorMsg));
        when(clientServiceUtil.getLegalDocumentTypeClient(getLegalDocumentTypeManagementUrl())).
                thenReturn(client);

        SystemException se = assertThrows(SystemException.class, ()-> target.getAll(searchName,
                pageNo, pageSize, sortBy, true));

        assertEquals(expectedErrorMsg, se.getMessage());
    }

    /**
     * Test for method {@link LegalDocumentTypeRESTServiceClient#getLegalDocumentTypes(String, Long, Boolean, Boolean, List, boolean, boolean)}.
     * Scenario: Legal Document Types list successfully retrieved
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testGetLegalDocumentTypes() throws MalformedURLException, SystemException{
        String results = "[" +
                "{\"id\": 1, \"tenantId\": 1, \"name\": \"ToU\", \"toBeShown\": true, \"toBeAccepted\": true}, " +
                "{\"id\": 2, \"tenantId\": 2, \"name\": \"ToU\", \"toBeShown\": true, \"toBeAccepted\": true}, " +
                "{\"id\": 3, \"tenantId\": 3, \"name\": \"ToU\", \"toBeShown\": true, \"toBeAccepted\": true}" +
                "]";

        InputStream is = new ByteArrayInputStream(results.getBytes());
        Response response = Response.ok(is).build();
        LegalDocumentTypeResourceClient client = mock(LegalDocumentTypeResourceClient.class);

        // Standard parameters
        String name = "ToU";
        Long tenantId = null;
        Boolean toBeShown = null;
        Boolean toBeAccepted = null;
        List<Long> ids = new ArrayList<>();

        when(client.getLegalDocumentTypes(name, tenantId, toBeShown, toBeAccepted, ids, false, false)).
                thenReturn(response);
        when(clientServiceUtil.getLegalDocumentTypeClient(getLegalDocumentTypeManagementUrl())).
                thenReturn(client);

        List<? extends SystemLegalDocumentType> result = target.getLegalDocumentTypes(name, tenantId,
                toBeShown, toBeAccepted, ids, false, false);

        assertNotNull(result);
        assertEquals(3,result.size());
    }

    /**
     * Test for method {@link LegalDocumentTypeRESTServiceClient#getLegalDocumentTypes(String, Long, Boolean, Boolean, List, boolean, boolean)}.
     * Scenario: Legal Document Types list unsuccessfully retrieved due an Internal Server Error (Endpoint processing)
     * @throws MalformedURLException for url informed incorrectly
     */
    @Test
    public void testGetLegalDocumentTypesWhenInternalServerErrorException() throws MalformedURLException {
        String errorMsg = "Internal Server Error while processing request";
        String exceptionClass = InternalServerErrorException.class.getName();
        String expectedErrorMsg = "Message(s): wrapped exception: " + exceptionClass + " with message: " + errorMsg;

        LegalDocumentTypeResourceClient client = mock(LegalDocumentTypeResourceClient.class);

        // Standard parameters
        String name = "ToU";
        Long tenantId = null;
        Boolean toBeShown = null;
        Boolean toBeAccepted = null;
        List<Long> ids = new ArrayList<>();

        when(client.getLegalDocumentTypes(name, tenantId, toBeShown, toBeAccepted, ids, false, false)).
                thenThrow(new InternalServerErrorException(errorMsg));
        when(clientServiceUtil.getLegalDocumentTypeClient(getLegalDocumentTypeManagementUrl())).
                thenReturn(client);

        SystemException se = assertThrows(SystemException.class, ()-> target.
                getLegalDocumentTypes(name, tenantId, toBeShown, toBeAccepted, ids, false, false));

        assertEquals(expectedErrorMsg, se.getMessage());
    }

    /**
     * Test for method {@link LegalDocumentTypeRESTServiceClient#getById(Long)}.
     * Scenario: Legal Document Types object successfully retrieved
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testGetById() throws MalformedURLException, SystemException{
        // Standard parameters
        Long id = 1L, tenantId = 2L;
        String name = "ToU";

        String objPattern = "{\"id\": %d, \"tenantId\": %d, \"name\": \"%s\", \"toBeShown\": %b, \"toBeAccepted\": %b}";
        String result = String.format(objPattern, id, tenantId, name, true, true);

        InputStream is = new ByteArrayInputStream(result.getBytes());
        Response response = Response.ok(is).build();
        LegalDocumentTypeResourceClient client = mock(LegalDocumentTypeResourceClient.class);

        when(client.getById(id)).thenReturn(response);
        when(clientServiceUtil.getLegalDocumentTypeClient(getLegalDocumentTypeManagementUrl())).
                thenReturn(client);

        Optional<? extends SystemLegalDocumentType> opt = target.getById(id);

        assertTrue(opt.isPresent());

        SystemLegalDocumentType type = opt.orElse(null);
        assertNotNull(type);
        assertEquals(id, type.getId());
        assertEquals(tenantId, type.getTenantId());
        assertEquals(name, type.getName());
        assertTrue(type.isToBeShown());
        assertTrue(type.isToBeAccepted());
    }

    /**
     * Test for method {@link LegalDocumentTypeRESTServiceClient#getById(Long)}.
     * Scenario: Legal Document Types object NOT FOUND
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testGetByIdWhenNotFound() throws MalformedURLException, SystemException{
        // Standard parameters
        Long id = 1L;

        LegalDocumentTypeResourceClient client = mock(LegalDocumentTypeResourceClient.class);

        when(client.getById(id)).thenThrow(new NotFoundException());
        when(clientServiceUtil.getLegalDocumentTypeClient(getLegalDocumentTypeManagementUrl())).
                thenReturn(client);

        Optional<? extends SystemLegalDocumentType> opt = target.getById(id);
        assertFalse(opt.isPresent());
    }

    /**
     * Test for method {@link LegalDocumentTypeRESTServiceClient#getById(Long)}.
     * Scenario: Legal Document Types object unsuccessfully retrieved due an Internal Server Error (Endpoint processing)
     * @throws MalformedURLException for url informed incorrectly
     */
    @Test
    public void testGetByIdWhenInternalServerError() throws MalformedURLException {
        // Standard parameters
        Long id = 1L;

        String errorMsg = "Internal Server Error while processing request";
        String exceptionClass = InternalServerErrorException.class.getName();
        String expectedErrorMsg = "Message(s): wrapped exception: " + exceptionClass + " with message: " + errorMsg;

        LegalDocumentTypeResourceClient client = mock(LegalDocumentTypeResourceClient.class);

        when(client.getById(id)).thenThrow(new InternalServerErrorException(errorMsg));
        when(clientServiceUtil.getLegalDocumentTypeClient(getLegalDocumentTypeManagementUrl())).
                thenReturn(client);

        SystemException se = assertThrows(SystemException.class, ()-> target.getById(id));
        assertEquals(expectedErrorMsg, se.getMessage());
    }

    /**
     * Test for method {@link LegalDocumentTypeRESTServiceClient#create(SystemLegalDocumentType)}
     * Scenario: Legal Document Types object successfully created
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testCreate() throws MalformedURLException, SystemException{
        LegalDocumentTypeResourceClient client = mock(LegalDocumentTypeResourceClient.class);

        when(client.create(any())).thenReturn(Response.ok().build());
        when(clientServiceUtil.getLegalDocumentTypeClient(getLegalDocumentTypeManagementUrl())).
                thenReturn(client);

        SystemLegalDocumentType legalDocumentType = mock(LegalDocumentType.class);
        Boolean status = target.create(legalDocumentType);

        assertTrue(status);
    }

    /**
     * Test for method {@link LegalDocumentTypeRESTServiceClient#create(SystemLegalDocumentType)}
     * Scenario: Legal Document Types object unsuccessfully created
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testCreateUnsuccessful() throws MalformedURLException, SystemException{
        LegalDocumentTypeResourceClient client = mock(LegalDocumentTypeResourceClient.class);

        when(client.create(any())).thenReturn(Response.status(300).build());
        when(clientServiceUtil.getLegalDocumentTypeClient(getLegalDocumentTypeManagementUrl())).
                thenReturn(client);

        SystemLegalDocumentType legalDocumentType = mock(LegalDocumentType.class);
        Boolean status = target.create(legalDocumentType);

        assertFalse(status);
    }


    /**
     * Test for method {@link LegalDocumentTypeRESTServiceClient#create(SystemLegalDocumentType)}
     * Scenario: Legal Document Types object unsuccessfully created due Internal Server Error
     * @throws MalformedURLException for url informed incorrectly
     */
    @Test
    public void testCreateInternalServerError() throws MalformedURLException {
        LegalDocumentTypeResourceClient client = mock(LegalDocumentTypeResourceClient.class);

        String errorMsg = "Internal Server Error while processing request";
        String exceptionClass = InternalServerErrorException.class.getName();
        String expectedErrorMsg = "Message(s): wrapped exception: " + exceptionClass + " with message: " + errorMsg;

        when(client.create(any())).thenThrow(new InternalServerErrorException(errorMsg));
        when(clientServiceUtil.getLegalDocumentTypeClient(getLegalDocumentTypeManagementUrl())).
                thenReturn(client);

        SystemLegalDocumentType legalDocumentType = mock(LegalDocumentType.class);
        SystemException se = assertThrows(SystemException.class, ()-> target.create(legalDocumentType));
        assertEquals(expectedErrorMsg, se.getMessage());
    }

    /**
     * Test for method {@link LegalDocumentTypeRESTServiceClient#update(SystemLegalDocumentType)}
     * Scenario: Legal Document Types object successfully updated
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testUpdate() throws MalformedURLException, SystemException{
        LegalDocumentTypeResourceClient client = mock(LegalDocumentTypeResourceClient.class);

        Long id = 1L;
        LegalDocumentType legalDocumentType = mock(LegalDocumentType.class);
        when(legalDocumentType.getId()).thenReturn(id);

        when(client.update(legalDocumentType.getId(), legalDocumentType)).thenReturn(Response.ok().build());
        when(clientServiceUtil.getLegalDocumentTypeClient(getLegalDocumentTypeManagementUrl())).
                thenReturn(client);

        Boolean status = target.update(legalDocumentType);
        assertTrue(status);
    }

    /**
     * Test for method {@link LegalDocumentTypeRESTServiceClient#update(SystemLegalDocumentType)}
     * Scenario: Legal Document Types object unsuccessfully updated
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testUpdateUnsuccessful() throws MalformedURLException, SystemException{
        LegalDocumentTypeResourceClient client = mock(LegalDocumentTypeResourceClient.class);

        Long id = 1L;
        LegalDocumentType legalDocumentType = mock(LegalDocumentType.class);
        when(legalDocumentType.getId()).thenReturn(id);

        when(client.update(legalDocumentType.getId(), legalDocumentType)).thenReturn(Response.status(300).build());
        when(clientServiceUtil.getLegalDocumentTypeClient(getLegalDocumentTypeManagementUrl())).
                thenReturn(client);

        Boolean status = target.update(legalDocumentType);

        assertFalse(status);
    }

    /**
     * Test for method {@link LegalDocumentTypeRESTServiceClient#update(SystemLegalDocumentType)}
     * Scenario: Legal Document Types object unsuccessfully updated due Internal Server Error
     * @throws MalformedURLException for url informed incorrectly
     */
    @Test
    public void testUpdateInternalServerError() throws MalformedURLException {
        LegalDocumentTypeResourceClient client = mock(LegalDocumentTypeResourceClient.class);

        String errorMsg = "Internal Server Error while processing request";
        String exceptionClass = InternalServerErrorException.class.getName();
        String expectedErrorMsg = "Message(s): wrapped exception: " + exceptionClass + " with message: " + errorMsg;

        Long id = 1L;
        LegalDocumentType legalDocumentType = mock(LegalDocumentType.class);
        when(legalDocumentType.getId()).thenReturn(id);

        when(client.update(legalDocumentType.getId(), legalDocumentType)).thenThrow(new InternalServerErrorException(errorMsg));
        when(clientServiceUtil.getLegalDocumentTypeClient(getLegalDocumentTypeManagementUrl())).
                thenReturn(client);

        SystemException se = assertThrows(SystemException.class, ()-> target.update(legalDocumentType));
        assertEquals(expectedErrorMsg, se.getMessage());
    }

    /**
     * Test for method {@link LegalDocumentTypeRESTServiceClient#delete(long)}
     * Scenario: Legal Document Types object successfully deleted
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testDelete() throws MalformedURLException, SystemException{
        LegalDocumentTypeResourceClient client = mock(LegalDocumentTypeResourceClient.class);

        long id = 1L;
        when(client.delete(id)).thenReturn(Response.ok().build());
        when(clientServiceUtil.getLegalDocumentTypeClient(getLegalDocumentTypeManagementUrl())).
                thenReturn(client);

        Boolean status = target.delete(id);
        assertTrue(status);
    }

    /**
     * Test for method {@link LegalDocumentTypeRESTServiceClient#delete(long)}
     * Scenario: Legal Document Types object unsuccessfully deleted
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testDeleteUnsuccessful() throws MalformedURLException, SystemException{
        LegalDocumentTypeResourceClient client = mock(LegalDocumentTypeResourceClient.class);

        long id = 1L;

        when(client.delete(id)).thenReturn(Response.status(300).build());
        when(clientServiceUtil.getLegalDocumentTypeClient(getLegalDocumentTypeManagementUrl())).
                thenReturn(client);

        Boolean status = target.delete(id);
        assertFalse(status);
    }

    /**
     * Test for method {@link LegalDocumentTypeRESTServiceClient#delete(long)}
     * Scenario: Legal Document Types object unsuccessfully deleted due Internal Server Error
     * @throws MalformedURLException for url informed incorrectly
     */
    @Test
    public void testDeleteInternalServerError() throws MalformedURLException {
        LegalDocumentTypeResourceClient client = mock(LegalDocumentTypeResourceClient.class);

        String errorMsg = "Internal Server Error while processing request";
        String exceptionClass = InternalServerErrorException.class.getName();
        String expectedErrorMsg = "Message(s): wrapped exception: " + exceptionClass + " with message: " + errorMsg;

        long id = 1L;

        when(client.delete(id)).thenThrow(new InternalServerErrorException(errorMsg));
        when(clientServiceUtil.getLegalDocumentTypeClient(getLegalDocumentTypeManagementUrl())).
                thenReturn(client);

        SystemException se = assertThrows(SystemException.class, ()-> target.delete(id));
        assertEquals(expectedErrorMsg, se.getMessage());
    }
}
