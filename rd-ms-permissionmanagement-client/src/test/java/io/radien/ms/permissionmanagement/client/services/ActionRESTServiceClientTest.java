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
 */
package io.radien.ms.permissionmanagement.client.services;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemAction;
import io.radien.api.security.TokensPlaceHolder;
import io.radien.exception.SystemException;
import io.radien.exception.TokenExpiredException;
import io.radien.ms.authz.client.UserClient;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.permissionmanagement.client.entities.Action;
import io.radien.ms.permissionmanagement.client.util.ActionModelMapper;
import io.radien.ms.permissionmanagement.client.util.ClientServiceUtil;
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
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

/**
 * Action REST Service requests and responses test
 * {@link io.radien.ms.permissionmanagement.client.services.ActionRESTServiceClient}
 *
 * @author Nuno Santana
 * @author Bruno Gama
 */
public class ActionRESTServiceClientTest {

    @InjectMocks
    ActionRESTServiceClient target;

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

    /**
     * Preparation for the tests
     */
    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test to attempt to retrieve the actions by a requested name
     * @throws Exception to be throw
     */
    @Test
    public void testGetActionByName() throws Exception {
        String a = "a";

        JsonArrayBuilder builder = Json.createArrayBuilder();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeArray(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        Response response = Response.ok(is).build();

        ActionResourceClient resourceClient = Mockito.mock(ActionResourceClient.class);

        when(resourceClient.getActions(a,true,true)).thenReturn(response);

        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).thenReturn(resourceClient);

        assertEquals(Optional.empty(),target.getActionByName(a));
    }

    /**
     * Action management endpoint url getter
     * @return the action enpoint url
     */
    private String getActionManagementUrl(){
        String url = "";
        when(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_PERMISSIONMANAGEMENT)).thenReturn(url);
        return url;
    }

    /**
     * Test to attempt to get actions by name with results to be returned
     * @throws Exception to be thrown in case of either record not found or values with wrong format
     */
    @Test
    public void testGetActionByNameWithResults() throws Exception {
        String a = "a";
        Action action = ActionFactory.create("action-aaa", null);

        JsonArrayBuilder builder = Json.createArrayBuilder();
        builder.add(ActionFactory.convertToJsonObject(action));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeArray(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        Response response = Response.ok(is).build();

        ActionResourceClient resourceClient = Mockito.mock(ActionResourceClient.class);
        when(resourceClient.getActions(a,true,true))
                .thenReturn(response);
        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).thenReturn(resourceClient);

        assertTrue(target.getActionByName(a).isPresent());
    }

    /**
     * Test to attempt to retrieve the action by id with results to be returned
     * @throws Exception to be thrown in case of either record not found or values with wrong format
     */
    @Test
    public void testGetActionByIdWithResults() throws Exception {
        Action action = ActionFactory.create("permission-a", 2L);
        action.setId(11L);
        InputStream is = new ByteArrayInputStream(ActionModelMapper.map(action).toString().getBytes());
        Response response = Response.ok(is).build();
        ActionResourceClient resourceClient = Mockito.mock(ActionResourceClient.class);
        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getById(anyLong())).
                thenReturn(response);
        Optional<SystemAction> opt = target.getActionById(action.getId());
        assertNotNull(opt);
        assertTrue(opt.isPresent());
        assertEquals(opt.get().getId(), action.getId());
    }

    /**
     * Test to get action by finding it via the name field but multiple actions have that name or similar
     * @throws Exception to be thrown in case of either record not found or values with wrong format
     */
    @Test
    public void testGetActionByNameNonUnique() throws Exception {
        String actionName = "a";
        List<Action> actionList = new ArrayList<>();
        actionList.add(ActionFactory.create("a1", null));
        actionList.add(ActionFactory.create("a2", null));

        JsonArray jsonArray = ActionModelMapper.map(actionList);
        InputStream is = new ByteArrayInputStream(jsonArray.toString().getBytes());
        Response response = Response.ok(is).build();

        ActionResourceClient resourceClient = Mockito.mock(ActionResourceClient.class);
        when(resourceClient.getActions(actionName,true,true))
                .thenReturn(response);
        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).thenReturn(resourceClient);
        Optional<SystemAction> result = target.getActionByName(actionName);
        assertNotNull(result);
        assertFalse(result.isPresent());
    }

    /**
     * Test to check behaviour when we try to attempt to get action by name but we are faced with a extension exception
     * @throws Exception to be throw
     */
    @Test(expected = SystemException.class)
    public void testGetActionByNameExtensionException() throws Exception {
        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).thenThrow(new ExtensionException(new Exception()));
        target.getActionByName("a");
    }

    /**
     * Test to check behaviour when we try to attempt to get action by name but we are faced with a processing exception
     * @throws Exception to be throw
     */
    @Test(expected = SystemException.class)
    public void testGetActionByNameProcessingException() throws Exception {
        String a = "a";
        ActionResourceClient resourceClient = Mockito.mock(ActionResourceClient.class);
        when(resourceClient.getActions(a,true,true))
                .thenThrow(new ProcessingException("test"));
        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).thenReturn(resourceClient);

        target.getActionByName(a);
    }

    /**
     * Test to attempt to create a action
     * @throws MalformedURLException in case of malformed URL for the endpoint communication
     * @throws SystemException in case of token expiration
     */
    @Test
    public void testCreate() throws MalformedURLException, SystemException {
        ActionResourceClient resourceClient = Mockito.mock(ActionResourceClient.class);
        when(resourceClient.save(any())).thenReturn(Response.ok().build());
        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).thenReturn(resourceClient);
        assertTrue(target.create(new Action()));
    }

    /**
     * Test to attempt to create a action but without success
     * @throws MalformedURLException in case of malformed URL for the endpoint communication
     * @throws SystemException in case of token expiration
     */
    @Test
    public void testCreateFail() throws MalformedURLException, SystemException {
        ActionResourceClient resourceClient = Mockito.mock(ActionResourceClient.class);
        when(resourceClient.save(any())).thenReturn(Response.serverError().entity("test error msg").build());
        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).thenReturn(resourceClient);
        assertFalse(target.create(new Action()));
    }

    /**
     * Test to create a action but we are faced with a processing exception
     * @throws MalformedURLException in case of malformed URL for the endpoint communication
     * @throws SystemException in case of token expiration
     */
    @Test(expected = SystemException.class)
    public void testCreateProcessingException() throws MalformedURLException, SystemException {
        ActionResourceClient resourceClient = Mockito.mock(ActionResourceClient.class);
        when(resourceClient.save(any())).thenThrow(new ProcessingException(""));
        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).thenReturn(resourceClient);
        target.create(new Action());
    }

    /**
     * Test to attempt the communicating with the endpoint but we are faced with a Malformed URL Exception
     * @throws MalformedURLException in case of malformed URL for the endpoint communication
     */
    @Test
    public void testCommunicationFail() throws MalformedURLException {
        MalformedURLException malformedURLException =
                new MalformedURLException("Error accessing permission microservice");
        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).
                thenThrow(malformedURLException);
        SystemException se = assertThrows(SystemException.class, () -> target.create(new Action()));
        assertNotNull(se);
        assertTrue(se.getMessage().contains(malformedURLException.getMessage()));
    }

    /**
     * Test to attempt to connect with the OAF
     */
    @Test
    public void testAccessingOAF() {
        OAFAccess oafAccess = target.getOAF();
        assertNotNull(oafAccess);
    }

    /**
     * Test to attempt to get action by id but we are faced with a extension exception
     * @throws Exception to be throw
     */
    @Test(expected = SystemException.class)
    public void testGetActionByIdExtensionException() throws Exception {
        when(clientServiceUtil.getActionResourceClient(
                getActionManagementUrl())).thenThrow(new ExtensionException(new Exception()));
        target.getActionById(1L);
    }

    /**
     * Test to attempt to get action by id but we are faced with a processing exception
     * @throws Exception to be throw
     */
    @Test(expected = SystemException.class)
    public void testGetActionByIdProcessingException() throws Exception {
        ActionResourceClient resourceClient = Mockito.mock(ActionResourceClient.class);
        when(resourceClient.getById(anyLong()))
                .thenThrow(new ProcessingException("test"));
        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).thenReturn(resourceClient);
        target.getActionById(1L);
    }

    /**
     * Test to attempt to get multiple actions
     * @throws MalformedURLException in case of wrong URL when attempting to connect with endpoint
     */
    @Test
    public void testGetActions() throws MalformedURLException {
        List<Action> list = new ArrayList<>();
        list.add(ActionFactory.create("add", 1L));
        list.add(ActionFactory.create("delete", 2L));
        list.add(ActionFactory.create("update", 3L));
        Page<SystemAction> page = new Page<>(list, 1, 3, 1);

        JsonObjectBuilder objectBuilder = Json.createObjectBuilder()
                .add("currentPage", page.getCurrentPage())
                .add("totalResults", page.getTotalResults())
                .add("totalPages", page.getTotalPages())
                .add("results", ActionModelMapper.map(list));

        JsonObject jsonObject = objectBuilder.build();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(jsonObject.toString().getBytes());
        Response expectedResponse = Response.ok(inputStream).build();

        List<String> sortBy = new ArrayList<>();

        ActionResourceClient resourceClient = Mockito.mock(ActionResourceClient.class);
        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).
                then(i -> resourceClient);
        when(resourceClient.getAll("action%", 1, 100, sortBy, true)).
                then(i -> expectedResponse);

        Page<? extends SystemAction> retrievedPage = null;
        try {
            retrievedPage = target.getAll("action%", 1, 100, sortBy, true);
        }
        catch (Exception e) {
            fail("should not happen here...");
        }
        assertNotNull(retrievedPage);
        assertNotNull(retrievedPage.getResults());
        assertFalse(retrievedPage.getResults().isEmpty());
        assertEquals(3, retrievedPage.getResults().size());
    }

    /**
     * Test to get actions but with failure
     * @throws MalformedURLException in case of wrong URL when attempting to connect with endpoint
     * @throws SystemException in case of token expiration
     */
    @Test(expected = SystemException.class)
    public void testGetActionsWhenFailure() throws MalformedURLException, SystemException {
        List<String> sortBy = new ArrayList<>();
        Response errorResponse = Response.status(500).build();
        ActionResourceClient resourceClient = Mockito.mock(ActionResourceClient.class);
        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).
                thenThrow(new ProcessingException("error"));
        when(resourceClient.getAll("action%", 1, 100, sortBy, true)).
                then(i -> errorResponse);
        target.getAll("action%", 1, 100, sortBy, true);
    }


    /**
     * Test to attempt to get all actions but with token expired
     * @throws MalformedURLException in case of wrong URL when attempting to connect with endpoint
     * @throws SystemException in case of token expiration
     */
    @Test(expected = SystemException.class)
    public void testGetAllTokenExpiration() throws MalformedURLException, SystemException {
        ActionResourceClient resourceClient = Mockito.mock(ActionResourceClient.class);

        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getAll(anyString(), anyInt(), anyInt(), anyList(), anyBoolean())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        List<String> sortBy = new ArrayList<>();
        target.getAll("search", 1, 10, sortBy, false);
    }

    /**
     * Test to get actions by id but with token expired
     * @throws MalformedURLException in case of wrong URL when attempting to connect with endpoint
     * @throws SystemException in case of token expired
     */
    @Test(expected = SystemException.class)
    public void testGetActionByIdTokenExpiration() throws MalformedURLException, SystemException {
        ActionResourceClient resourceClient = Mockito.mock(ActionResourceClient.class);

        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getById(anyLong())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getActionById(2L);
    }

    /**
     * Test to get actions by name but with token expired
     * @throws MalformedURLException in case of wrong URL when attempting to connect with endpoint
     * @throws SystemException in case of token expired
     */
    @Test(expected = SystemException.class)
    public void testGetActionByNameTokenExpiration() throws MalformedURLException, SystemException {
        ActionResourceClient resourceClient = Mockito.mock(ActionResourceClient.class);

        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getActions(anyString(), anyBoolean(), anyBoolean())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getActionByName("name");
    }

    /**
     * Test to create actions but with token expired
     * @throws MalformedURLException in case of wrong URL when attempting to connect with endpoint
     * @throws SystemException in case of token expired
     */
    @Test(expected = SystemException.class)
    public void testCreateTokenExpiration() throws MalformedURLException, SystemException {
        ActionResourceClient resourceClient = Mockito.mock(ActionResourceClient.class);

        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.save(any())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());


        SystemAction systemAction = ActionFactory.create("name", 2L);
        target.create(systemAction);
    }

    @Test
    public void testDelete() throws MalformedURLException, SystemException {
        ActionResourceClient resourceClient = Mockito.mock(ActionResourceClient.class);
        when(resourceClient.delete(anyLong())).thenReturn(Response.ok().build());
        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).thenReturn(resourceClient);
        assertTrue(target.delete(2L));
    }

    @Test(expected = SystemException.class)
    public void testDeleteMalformedException() throws MalformedURLException, SystemException {
        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).thenThrow(new MalformedURLException());
        target.delete(2L);
    }

    @Test
    public void testDeleteFail() throws MalformedURLException, SystemException {
        ActionResourceClient resourceClient = Mockito.mock(ActionResourceClient.class);
        when(resourceClient.delete(anyLong())).thenReturn(Response.serverError().entity("test error msg").build());
        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).thenReturn(resourceClient);
        assertFalse(target.delete(2L));
    }

    @Test(expected = SystemException.class)
    public void testDeleteProcessingException() throws MalformedURLException, SystemException {
        ActionResourceClient resourceClient = Mockito.mock(ActionResourceClient.class);
        when(resourceClient.delete(anyLong())).thenThrow(new ProcessingException(""));
        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).thenReturn(resourceClient);
        target.delete(2L);
    }

    @Test(expected = SystemException.class)
    public void testDeleteTokenExpiration() throws Exception {
        ActionResourceClient resourceClient = Mockito.mock(ActionResourceClient.class);

        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.delete(anyLong())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.delete(2L);
    }
}