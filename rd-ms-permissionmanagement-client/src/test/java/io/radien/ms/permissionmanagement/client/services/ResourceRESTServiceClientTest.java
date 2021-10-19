/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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
package io.radien.ms.permissionmanagement.client.services;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemResource;
import io.radien.api.security.TokensPlaceHolder;
import io.radien.exception.NotFoundException;
import io.radien.exception.SystemException;
import io.radien.exception.TokenExpiredException;
import io.radien.ms.authz.client.UserClient;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.permissionmanagement.client.entities.Resource;
import io.radien.ms.permissionmanagement.client.util.ClientServiceUtil;
import io.radien.ms.permissionmanagement.client.util.ResourceModelMapper;
import java.io.ByteArrayOutputStream;
import org.apache.cxf.bus.extension.ExtensionException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.json.*;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

/**
 * Resource REST Service Client Testing responses and requests
 * {@link io.radien.ms.permissionmanagement.client.services.ResourceResourceClient}
 * @author Newton Carvalho
 */
public class ResourceRESTServiceClientTest {

    @InjectMocks
    ResourceRESTServiceClient target;

    @Mock
    ClientServiceUtil clientServiceUtil;

    @Mock
    OAFAccess oafAccess;

    @Mock
    AuthorizationChecker authorizationChecker;

    @Mock
    UserClient userClient;

    @Mock
    TokensPlaceHolder tokensPlaceHolder;

    private static final String testValue = "test";

    /**
     * Method to prepare before the running tests
     */
    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test to get a resource by a requested name
     * @throws Exception in case of resource not found or error in the data
     */
    @Test
    public void testGetResourceByName() throws Exception {
        String resourceName = "resource-a";
        InputStream is = new ByteArrayInputStream("[]".getBytes());

        Response response = Response.ok(is).build();
        ResourceResourceClient resourceClient = Mockito.mock(ResourceResourceClient.class);
        when(resourceClient.getResources(resourceName,null,true,true)).thenReturn(response);
        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).thenReturn(resourceClient);
        assertEquals(Optional.empty(),target.getResourceByName(resourceName));
    }

    /**
     * Method to retrieve the resource management endpoint url connection
     * @return resource endpoint connection url
     */
    private String getResourceManagementUrl(){
        String url = "";
        when(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_PERMISSIONMANAGEMENT)).thenReturn(url);
        return url;
    }

    /**
     * Method to get the resource by searching for the name with results to be returned
     * @throws Exception in case of resource not found or error in the data
     */
    @Test
    public void testGetResourceByNameWithResults() throws Exception {
        String resourceName = "resource-aaa";
        Resource resource = ResourceFactory.create(resourceName, null);

        InputStream is = new ByteArrayInputStream(ResourceModelMapper.
                map(Collections.singletonList(resource)).toString().getBytes());

        Response response = Response.ok(is).build();

        ResourceResourceClient resourceClient = Mockito.mock(ResourceResourceClient.class);
        when(resourceClient.getResources(resourceName,null,true,true))
                .thenReturn(response);
        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).
                thenReturn(resourceClient);

        assertTrue(target.getResourceByName(resourceName).isPresent());
    }

    /**
     * Test to get the resource by id with results to be returned
     * @throws Exception in case of resource not found or error in the data
     */
    @Test
    public void testGetResourceByIdWithResults() throws Exception {
        Resource resource = ResourceFactory.create("resource-a", 2L);
        resource.setId(11L);
        InputStream is = new ByteArrayInputStream(ResourceModelMapper.
                map(resource).toString().getBytes());
        Response response = Response.ok(is).build();
        ResourceResourceClient resourceClient = Mockito.mock(ResourceResourceClient.class);
        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getById(anyLong())).
                thenReturn(response);
        Optional<SystemResource> opt = target.getResourceById(resource.getId());
        assertNotNull(opt);
        assertTrue(opt.isPresent());
        assertEquals(opt.get().getId(), resource.getId());
    }

    /**
     * Test to attempt to get resource via the name with similar names
     * @throws SystemException when the token has expired
     * @throws MalformedURLException when the endpoint url is incorrect
     */
    @Test
    public void testGetResourceByNameNonUnique() throws SystemException, MalformedURLException {
        String resourceName = "resource-x";
        Resource r1 = ResourceFactory.create("resource-x1", null);
        Resource r2 = ResourceFactory.create("resource-x2", null);
        Resource r3 = ResourceFactory.create("resource-x3", null);
        InputStream is = new ByteArrayInputStream(ResourceModelMapper.
                map(Arrays.asList(r1, r2, r3)).toString().getBytes());

        Response response = Response.ok(is).build();

        ResourceResourceClient resourceClient = Mockito.mock(ResourceResourceClient.class);
        when(resourceClient.getResources(resourceName,null,true,true))
                .thenReturn(response);
        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).thenReturn(resourceClient);
        Optional<SystemResource> result = target.getResourceByName(resourceName);
        assertNotNull(result);
        assertFalse(result.isPresent());
    }

    /**
     * Test to get the resource by searching for his name but with extension exception being throw
     * @throws Exception to be throw
     */
    @Test
    public void testGetResourceByNameExtensionException() throws Exception {
        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).thenThrow(new ExtensionException(new Exception()));
        Exception e = assertThrows(SystemException.class, () -> target.getResourceByName("resource"));
        assertTrue(e.getMessage().contains(ExtensionException.class.getName()));
    }

    /**
     * Test to get the resource by searching for his name but with processing exception being throw
     * @throws Exception to be throw
     */
    @Test
    public void testGetResourceByNameProcessingException() throws Exception {
        String resourceName = "resource-1";
        ResourceResourceClient resourceClient = Mockito.mock(ResourceResourceClient.class);
        when(resourceClient.getResources(resourceName,null,true,true))
                .thenThrow(new ProcessingException(testValue));
        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).
                thenReturn(resourceClient);
        Exception e = assertThrows(SystemException.class, ()-> target.getResourceByName(resourceName));
        assertTrue(e.getMessage().contains(ProcessingException.class.getName()));
    }

    /**
     * Method to test the creation of resources
     * @throws MalformedURLException when the endpoint url is incorrect
     */
    @Test
    public void testCreate() throws MalformedURLException {
        ResourceResourceClient resourceClient = Mockito.mock(ResourceResourceClient.class);
        when(resourceClient.create(any())).thenReturn(Response.ok().build());
        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).thenReturn(resourceClient);
        try {
            assertTrue(target.create(new Resource()));
        } catch (SystemException se) {
            fail("unexpected");
        }
    }


    /**
     * Method to test the creation of resources
     * @throws MalformedURLException when the endpoint url is incorrect
     * @throws SystemException in case of any communication/processing issue regarding resource rest api
     */
    @Test
    public void testUpdate() throws MalformedURLException, SystemException {
        ResourceResourceClient resourceClient = Mockito.mock(ResourceResourceClient.class);
        when(resourceClient.update(anyLong(), any())).thenReturn(Response.ok().build());
        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).thenReturn(resourceClient);
        Resource resource = new Resource();
        resource.setId(1L);
        assertTrue(target.update(resource));
    }

    /**
     * Method to test the creation of resources but without success
     * @throws MalformedURLException when the endpoint url is incorrect
     */
    @Test
    public void testCreateFail() throws MalformedURLException {
        ResourceResourceClient resourceClient = Mockito.mock(ResourceResourceClient.class);
        when(resourceClient.create(any())).thenReturn(Response.serverError().entity("test error msg").build());
        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).thenReturn(resourceClient);
        try {
            assertFalse(target.create(new Resource()));
        } catch (SystemException se) {
            fail("unexpected");
        }
    }


    /**
     * Method to test the updating of resources but without success
     * @throws MalformedURLException when the endpoint url is incorrect
     * @throws SystemException in case of any communication/processing issue regarding resource rest api
     */
    @Test
    public void testUpdateFail() throws MalformedURLException, SystemException {
        ResourceResourceClient resourceClient = Mockito.mock(ResourceResourceClient.class);
        when(resourceClient.update(anyLong(), any())).thenReturn(Response.status(300).entity("test").build());
        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).thenReturn(resourceClient);
        Resource resource = new Resource(); resource.setId(1L);
        assertFalse(target.update(resource));
    }

    /**
     * Method to test the updating of resources but without success (Resource entity not found)
     * @throws MalformedURLException when the endpoint url is incorrect
     * @throws SystemException in case of any communication/processing issue regarding resource rest api
     */
    @Test(expected = SystemException.class)
    public void testUpdateFailResourceNotFound() throws MalformedURLException, SystemException {
        ResourceResourceClient resourceClient = Mockito.mock(ResourceResourceClient.class);
        when(resourceClient.update(anyLong(), any())).thenThrow(new NotFoundException());
        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).thenReturn(resourceClient);
        Resource resource = new Resource(); resource.setId(1L);
        target.update(resource);
    }

    /**
     * Method to test the creation of resources but throwing a processing exception
     * @throws MalformedURLException when the endpoint url is incorrect
     */
    @Test
    public void testCreateProcessingException() throws MalformedURLException {
        ResourceResourceClient resourceClient = Mockito.mock(ResourceResourceClient.class);
        when(resourceClient.create(any())).thenThrow(new ProcessingException(""));
        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).thenReturn(resourceClient);
        Exception e = assertThrows(SystemException.class, ()->target.create(new Resource()));
        assertTrue(e.getMessage().contains(ProcessingException.class.getSimpleName()));
    }

    /**
     * Test to analyze the behaviour when trying to attempt a connection without success
     * @throws MalformedURLException when the endpoint url is incorrect
     */
    @Test
    public void testCommunicationFail() throws MalformedURLException {
        MalformedURLException malformedURLException =
                new MalformedURLException("Error accessing permission microservice");
        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).
                thenThrow(malformedURLException);
        SystemException se = assertThrows(SystemException.class, () -> target.create(new Resource()));
        assertNotNull(se);
        assertTrue(se.getMessage().contains(malformedURLException.getMessage()));
    }

    /**
     * Test to access the OAF
     */
    @Test
    public void testAccessingOAF() {
        OAFAccess oafAccess = target.getOAF();
        assertNotNull(oafAccess);
    }

    /**
     * Test to get the resource by his id but with an extension exception being throw
     * @throws Exception to be throw
     */
    @Test
    public void testGetResourceByIdExtensionException() throws Exception {
        when(clientServiceUtil.getResourceResourceClient(
                getResourceManagementUrl())).thenThrow(new ExtensionException(new Exception()));
        Exception e = assertThrows(SystemException.class, ()->target.getResourceById(1L));
        assertTrue(e.getMessage().contains(ExtensionException.class.getName()));
    }

    /**
     * Test to get the resource by his id but with an processing exception being throw
     * @throws Exception to be throw
     */
    @Test
    public void testGetResourceByIdProcessingException() throws Exception {
        ResourceResourceClient resourceClient = Mockito.mock(ResourceResourceClient.class);
        when(resourceClient.getById(anyLong()))
                .thenThrow(new ProcessingException(testValue));
        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).
                thenReturn(resourceClient);
        Exception e = assertThrows(SystemException.class, ()->target.getResourceById(1L));
        assertTrue(e.getMessage().contains(ProcessingException.class.getName())) ;
    }

    /**
     * Test to get the resources into a page
     * @throws MalformedURLException when the endpoint url is incorrect
     */
    @Test
    public void testGetResources() throws MalformedURLException {
        List<Resource> list = new ArrayList<>();
        list.add(ResourceFactory.create("user", 1L));
        list.add(ResourceFactory.create("contract", 2L));
        list.add(ResourceFactory.create("organization", 3L));
        list.add(ResourceFactory.create("partner", 3L));
        Page<SystemResource> page = new Page<>(list, 1, 3, 1);

        JsonObjectBuilder objectBuilder = Json.createObjectBuilder()
                .add("currentPage", page.getCurrentPage())
                .add("totalResults", page.getTotalResults())
                .add("totalPages", page.getTotalPages())
                .add("results", ResourceModelMapper.map(list));

        JsonObject jsonObject = objectBuilder.build();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(jsonObject.toString().getBytes());
        Response expectedResponse = Response.ok(inputStream).build();

        List<String> sortBy = new ArrayList<>();

        ResourceResourceClient resourceClient = Mockito.mock(ResourceResourceClient.class);
        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).
                then(i -> resourceClient);
        when(resourceClient.getAll("resource%", 1, 100, sortBy, true)).
                then(i -> expectedResponse);

        Page<? extends SystemResource> retrievedPage = null;
        try {
            retrievedPage = target.getAll("resource%", 1, 100, sortBy, true);
        }
        catch (Exception e) {
            fail("should not happen here...");
        }
        assertNotNull(retrievedPage);
        assertNotNull(retrievedPage.getResults());
        assertFalse(retrievedPage.getResults().isEmpty());
        assertEquals(4, retrievedPage.getResults().size());
    }

    /**
     * Test to validate the get resources without success
     * @throws MalformedURLException when the endpoint url is incorrect
     * @throws SystemException when token has expired
     */
    @Test(expected = SystemException.class)
    public void testGetResourcesWhenFailure() throws MalformedURLException, SystemException {
        List<String> sortBy = new ArrayList<>();
        Response errorResponse = Response.status(500).build();
        ResourceResourceClient resourceClient = Mockito.mock(ResourceResourceClient.class);
        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).
                thenThrow(new ProcessingException("error"));
        when(resourceClient.getAll("resource%", 1, 100, sortBy, true)).
                then(i -> errorResponse);
        target.getAll("action%", 1, 100, sortBy, true);
    }

    /**
     * Test the get all method with token expired
     * @throws MalformedURLException when the endpoint url is incorrect
     * @throws SystemException when token has expired
     */
    @Test(expected = SystemException.class)
    public void testGetAllTokenExpiration() throws MalformedURLException, SystemException {
        ResourceResourceClient resourceClient = Mockito.mock(ResourceResourceClient.class);

        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getAll(anyString(), anyInt(), anyInt(), anyList(), anyBoolean())).thenThrow(new TokenExpiredException(testValue));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn(testValue);
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity(testValue).build());

        List<String> sortBy = new ArrayList<>();
        target.getAll("search", 1, 10, sortBy, true);
    }

    /**
     * Test the get by id method with token expired
     * @throws MalformedURLException when the endpoint url is incorrect
     * @throws SystemException when token has expired
     */
    @Test(expected = SystemException.class)
    public void testGetResourceByIdTokenExpiration() throws MalformedURLException, SystemException {
        ResourceResourceClient resourceClient = Mockito.mock(ResourceResourceClient.class);

        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getById(anyLong())).thenThrow(new TokenExpiredException(testValue));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn(testValue);
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity(testValue).build());

        target.getResourceById(2L);
    }

    /**
     * Test the get by name method with token expired
     * @throws MalformedURLException when the endpoint url is incorrect
     * @throws SystemException when token has expired
     */
    @Test(expected = SystemException.class)
    public void testGetResourceByNameTokenExpiration() throws MalformedURLException, SystemException {
        ResourceResourceClient resourceClient = Mockito.mock(ResourceResourceClient.class);

        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getResources(anyString(), nullable(List.class),anyBoolean(), anyBoolean())).thenThrow(new TokenExpiredException(testValue));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn(testValue);
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity(testValue).build());

        target.getResourceByName("name");
    }

    /**
     * Test the create method with token expired
     * @throws MalformedURLException when the endpoint url is incorrect
     * @throws SystemException when token has expired
     */
    @Test(expected = SystemException.class)
    public void testCreateTokenExpiration() throws MalformedURLException, SystemException {
        ResourceResourceClient resourceClient = Mockito.mock(ResourceResourceClient.class);

        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.create(any())).thenThrow(new TokenExpiredException(testValue));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn(testValue);
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity(testValue).build());

        SystemResource systemResource = ResourceFactory.create("name", 2L);
        target.create(systemResource);
    }

    /**
     * Test the create method after token expired
     * @throws MalformedURLException when the endpoint url is incorrect
     * @throws SystemException when token has expired
     */
    @Test
    public void testCreateAfterTokenExpiration() throws MalformedURLException, SystemException {
        ResourceResourceClient resourceClient = Mockito.mock(ResourceResourceClient.class);

        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.create(any())).thenThrow(new TokenExpiredException(testValue)).
                thenReturn(Response.ok().build());

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn(testValue);
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity(testValue).build());

        SystemResource systemResource = ResourceFactory.create("name", 2L);
        assertTrue(target.create(systemResource));
    }

    /**
     * Test the update method with token expired
     * @throws MalformedURLException when the endpoint url is incorrect
     * @throws SystemException when token has expired
     */
    @Test(expected = SystemException.class)
    public void testUpdateTokenExpiration() throws MalformedURLException, SystemException {
        ResourceResourceClient resourceClient = Mockito.mock(ResourceResourceClient.class);

        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.update(anyLong(), any())).thenThrow(new TokenExpiredException(testValue));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn(testValue);
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity(testValue).build());

        SystemResource systemResource = ResourceFactory.create("name", 2L);
        systemResource.setId(1L);
        target.update(systemResource);
    }

    /**
     * Test the update method after token expired
     * @throws MalformedURLException when the endpoint url is incorrect
     * @throws SystemException when token has expired
     */
    @Test
    public void testUpdateAfterTokenExpiration() throws MalformedURLException, SystemException {
        ResourceResourceClient resourceClient = Mockito.mock(ResourceResourceClient.class);

        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.update(anyLong(), any())).thenThrow(new TokenExpiredException(testValue)).
                thenReturn(Response.ok().build());

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn(testValue);
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity(testValue).build());

        SystemResource systemResource = ResourceFactory.create("name", 2L);
        systemResource.setId(1L);
        assertTrue(target.update(systemResource));
    }

    @Test
    public void testDelete() throws MalformedURLException, SystemException {
        ResourceResourceClient resourceClient = Mockito.mock(ResourceResourceClient.class);
        when(resourceClient.delete(anyLong())).thenReturn(Response.ok().build());
        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).thenReturn(resourceClient);
        assertTrue(target.delete(2L));
    }

    @Test(expected = SystemException.class)
    public void testDeleteMalformedException() throws MalformedURLException, SystemException {
        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).thenThrow(new MalformedURLException());
        target.delete(2L);
    }

    @Test
    public void testDeleteFail() throws SystemException, MalformedURLException {
        ResourceResourceClient resourceClient = Mockito.mock(ResourceResourceClient.class);
        when(resourceClient.delete(anyLong())).thenReturn(Response.serverError().entity("test error msg").build());
        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).thenReturn(resourceClient);
        assertFalse(target.delete(2L));
    }

    @Test(expected = SystemException.class)
    public void testDeleteProcessingException() throws MalformedURLException, SystemException {
        ResourceResourceClient resourceClient = Mockito.mock(ResourceResourceClient.class);
        when(resourceClient.delete(anyLong())).thenThrow(new ProcessingException(""));
        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).thenReturn(resourceClient);
        target.delete(2L);
    }

    @Test(expected = SystemException.class)
    public void testDeleteTokenExpiration() throws Exception {
        ResourceResourceClient resourceClient = Mockito.mock(ResourceResourceClient.class);

        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.delete(anyLong())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.delete(2L);
    }

    /**
     * Test to get multiple resources by a list of ids
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testGetResourcesByIdsWithResults() throws MalformedURLException, SystemException {
        Resource resource1 = ResourceFactory.create("p1", null);
        resource1.setId(1L);

        Resource resource2 = ResourceFactory.create("p2", null);
        resource2.setId(2L);

        List<Long> ids = Arrays.asList(resource1.getId(), resource2.getId());

        JsonArrayBuilder builder = Json.createArrayBuilder();
        builder.add(ResourceFactory.convertToJsonObject(resource1));
        builder.add(ResourceFactory.convertToJsonObject(resource2));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeArray(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        Response response = Response.ok(is).build();

        ResourceResourceClient resourceClient = Mockito.mock(ResourceResourceClient.class);
        when(resourceClient.getResources(null,ids,true,true)).thenReturn(response);
        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).thenReturn(resourceClient);

        List<? extends SystemResource> outcome = target.getResourcesByIds(ids);
        assertNotNull(outcome);
        assertEquals(2, outcome.size());
    }

    /**
     * Test to get resources by ids but extension exception being throw
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testGetResourcesByIdsExtensionException() throws MalformedURLException, SystemException {
        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).thenThrow(new ExtensionException(new Exception()));
        target.getResourcesByIds(new ArrayList());
    }

    /**
     * Test to get resources by ids but processing exception being throw
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testGetResourcesByIdsProcessingException() throws MalformedURLException, SystemException {
        List<Long> ids = new ArrayList();
        ResourceResourceClient resourceClient = Mockito.mock(ResourceResourceClient.class);
        when(resourceClient.getResources(null,ids,true,true))
                .thenThrow(new ProcessingException("test"));
        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).thenReturn(resourceClient);
        target.getResourcesByIds(ids);
    }

    /**
     * Method to attempt to get a list of resources based on their ids but with token expired
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testGetResourcesByIdsTokenExpiration() throws MalformedURLException, SystemException {
        List<Long> ids = Arrays.asList(9L, 10L, 11L);

        ResourceResourceClient resourceClient = Mockito.mock(ResourceResourceClient.class);
        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getResources(null,ids,true,true)).thenThrow(new TokenExpiredException("teste"));

        when(tokensPlaceHolder.getRefreshToken()).thenReturn("refreshToken");
        when(userClient.refreshToken(any())).thenReturn(Response.ok().entity("refreshToken").build());
        target.getResourcesByIds(ids);
    }

    /**
     * In this scenario the list of resources is retrieved (based on their ids) by reattempt (retry)
     * after a JWT token expires
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testGetResourcesByIdsByReattempt() throws MalformedURLException, SystemException {
        Resource resource3 = ResourceFactory.create("test", null);
        resource3.setId(999L);

        List<Long> ids = Collections.singletonList(resource3.getId());

        JsonArrayBuilder builder = Json.createArrayBuilder();
        builder.add(ResourceFactory.convertToJsonObject(resource3));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeArray(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        Response response = Response.ok(is).build();

        ResourceResourceClient resourceClient = Mockito.mock(ResourceResourceClient.class);
        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getResources(null,ids,true,true)).
                thenThrow(new TokenExpiredException("test")).
                thenReturn(response);

        when(tokensPlaceHolder.getRefreshToken()).thenReturn("refreshToken");
        when(userClient.refreshToken(any())).thenReturn(Response.ok().entity("refreshToken").build());

        List<? extends SystemResource> outcome = target.getResourcesByIds(ids);
        assertNotNull(outcome);
        assertEquals(1, outcome.size());
    }
}