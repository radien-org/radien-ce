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
import io.radien.api.model.permission.SystemAction;
import io.radien.api.security.TokensPlaceHolder;
import io.radien.exception.NotFoundException;
import io.radien.exception.SystemException;
import io.radien.exception.TokenExpiredException;
import io.radien.ms.authz.client.UserClient;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.permissionmanagement.client.entities.Action;
import io.radien.ms.permissionmanagement.client.util.ActionModelMapper;
import io.radien.ms.permissionmanagement.client.util.ClientServiceUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import org.apache.cxf.bus.extension.ExtensionException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
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

        when(resourceClient.getActions(a,null,true,true)).thenReturn(response);

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
        when(resourceClient.getActions(a,null,true,true))
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
        when(resourceClient.getActions(actionName,null,true,true))
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
        when(resourceClient.getActions(a,null,true,true))
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
        when(resourceClient.create(any())).thenReturn(Response.ok().build());
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
        when(resourceClient.create(any())).thenReturn(Response.serverError().entity("test error msg").build());
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
        when(resourceClient.create(any())).thenThrow(new ProcessingException(""));
        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).thenReturn(resourceClient);
        target.create(new Action());
    }

    /**
     * Test to attempt to update a action
     * @throws MalformedURLException in case of malformed URL for the endpoint communication
     * @throws SystemException in case of token expiration
     */
    @Test
    public void testUpdate() throws MalformedURLException, SystemException {
        ActionResourceClient resourceClient = Mockito.mock(ActionResourceClient.class);
        Action action = new Action(); action.setId(111L);
        when(resourceClient.update(action.getId(), action)).thenReturn(Response.ok().build());
        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).thenReturn(resourceClient);
        assertTrue(target.update(action));
    }

    /**
     * Test to attempt to update a action but without success
     * @throws MalformedURLException in case of malformed URL for the endpoint communication
     * @throws SystemException in case of token expiration
     */
    @Test
    public void testUpdateFail() throws MalformedURLException, SystemException {
        ActionResourceClient resourceClient = Mockito.mock(ActionResourceClient.class);
        Action action = new Action(); action.setId(111L);
        when(resourceClient.update(action.getId(), action)).thenReturn(Response.status(300).entity("test").build());
        when(clientServiceUtil.getActionResourceClient(any())).thenReturn(resourceClient);
        assertFalse(target.update(action));
    }


    /**
     * Test to attempt to update a action but without success due processing fail (Acton not found)
     * @throws MalformedURLException in case of malformed URL for the endpoint communication
     * @throws SystemException in case of token expiration
     */
    @Test(expected = SystemException.class)
    public void testUpdateNotFoundFail() throws MalformedURLException, SystemException {
        ActionResourceClient resourceClient = Mockito.mock(ActionResourceClient.class);
        Action action = new Action(); action.setId(111L);
        when(resourceClient.update(action.getId(), action)).thenThrow(new NotFoundException());
        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).thenReturn(resourceClient);
        target.update(action);
    }


    /**
     * Test to update a action but we are faced with a processing exception
     * @throws MalformedURLException in case of malformed URL for the endpoint communication
     * @throws SystemException in case of token expiration
     */
    @Test(expected = SystemException.class)
    public void testUpdateProcessingException() throws MalformedURLException, SystemException {
        ActionResourceClient resourceClient = Mockito.mock(ActionResourceClient.class);
        Action action = new Action(); action.setId(111L);
        when(resourceClient.update(action.getId(), action)).thenThrow(new ProcessingException(""));
        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).thenReturn(resourceClient);
        target.update(action);
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
        when(resourceClient.getActions(anyString(), nullable(List.class),anyBoolean(), anyBoolean())).thenThrow(new TokenExpiredException("test"));

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
        when(resourceClient.create(any())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());


        SystemAction systemAction = ActionFactory.create("name", 2L);
        target.create(systemAction);
    }


    /**
     * Test to create action after jwt token expired (ReTry)
     * @throws MalformedURLException in case of wrong URL when attempting to connect with endpoint
     * @throws SystemException in case of any communication/processing issue with action rest api
     */
    @Test
    public void testCreateAfterTokenExpiration() throws MalformedURLException, SystemException {
        ActionResourceClient resourceClient = Mockito.mock(ActionResourceClient.class);

        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.create(any())).thenThrow(new TokenExpiredException("test")).
                thenReturn(Response.ok().build());

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());


        SystemAction systemAction = ActionFactory.create("name", 2L);
        assertTrue(target.create(systemAction));
    }


    /**
     * Test to update actions but with token expired
     * @throws MalformedURLException in case of wrong URL when attempting to connect with endpoint
     * @throws SystemException in case of token expired
     */
    @Test(expected = SystemException.class)
    public void testUpdateTokenExpiration() throws MalformedURLException, SystemException {
        ActionResourceClient resourceClient = Mockito.mock(ActionResourceClient.class);

        when(clientServiceUtil.getActionResourceClient(any())).thenReturn(resourceClient);
        when(resourceClient.update(anyLong(), any())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());


        SystemAction systemAction = ActionFactory.create("name", 2L);
        systemAction.setId(1L);
        target.update(systemAction);
    }


    /**
     * Test to update action after jwt token expired (ReTry)
     * @throws MalformedURLException in case of wrong URL when attempting to connect with endpoint
     * @throws SystemException in case of any communication/processing issue with action rest api
     */
    @Test
    public void testUpdateAfterTokenExpiration() throws MalformedURLException, SystemException {
        ActionResourceClient resourceClient = Mockito.mock(ActionResourceClient.class);

        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.update(anyLong(), any())).thenThrow(new TokenExpiredException("test")).
                thenReturn(Response.ok().build());

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());


        SystemAction systemAction = ActionFactory.create("name", 2L);
        systemAction.setId(2L);
        assertTrue(target.update(systemAction));
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

    /**
     * Test to get multiple actions by a list of ids
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testGetActionsByIdsWithResults() throws MalformedURLException, SystemException {
        Action action1 = ActionFactory.create("p1", null);
        action1.setId(1L);

        Action action2 = ActionFactory.create("p2", null);
        action2.setId(2L);

        List<Long> ids = Arrays.asList(action1.getId(), action2.getId());

        JsonArrayBuilder builder = Json.createArrayBuilder();
        builder.add(ActionFactory.convertToJsonObject(action1));
        builder.add(ActionFactory.convertToJsonObject(action2));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeArray(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        Response response = Response.ok(is).build();

        ActionResourceClient resourceClient = Mockito.mock(ActionResourceClient.class);
        when(resourceClient.getActions(null,ids,true,true)).thenReturn(response);
        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).thenReturn(resourceClient);

        List<? extends SystemAction> outcome = target.getActionsByIds(ids);
        assertNotNull(outcome);
        assertEquals(2, outcome.size());
    }

    /**
     * Test to get actions by ids but extension exception being throw
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testGetActionsByIdsExtensionException() throws MalformedURLException, SystemException {
        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).thenThrow(new ExtensionException(new Exception()));
        target.getActionsByIds(new ArrayList());
    }

    /**
     * Test to get actions by ids but processing exception being throw
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testGetActionsByIdsProcessingException() throws MalformedURLException, SystemException {
        List<Long> ids = new ArrayList();
        ActionResourceClient resourceClient = Mockito.mock(ActionResourceClient.class);
        when(resourceClient.getActions(null,ids,true,true))
                .thenThrow(new ProcessingException("test"));
        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).thenReturn(resourceClient);
        target.getActionsByIds(ids);
    }

    /**
     * Method to attempt to get a list of actions based on their ids but with token expired
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testGetActionsByIdsTokenExpiration() throws MalformedURLException, SystemException {
        List<Long> ids = Arrays.asList(9L, 10L, 11L);

        ActionResourceClient resourceClient = Mockito.mock(ActionResourceClient.class);
        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getActions(null,ids,true,true)).thenThrow(new TokenExpiredException("teste"));

        when(tokensPlaceHolder.getRefreshToken()).thenReturn("refreshToken");
        when(userClient.refreshToken(any())).thenReturn(Response.ok().entity("refreshToken").build());
        target.getActionsByIds(ids);
    }

    /**
     * In this scenario the list of actions is retrieved (based on their ids) by reattempt (retry)
     * after a JWT token expires
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testGetActionsByIdsByReattempt() throws MalformedURLException, SystemException {
        Action action3 = ActionFactory.create("test", null);
        action3.setId(999L);

        List<Long> ids = Collections.singletonList(action3.getId());

        JsonArrayBuilder builder = Json.createArrayBuilder();
        builder.add(ActionFactory.convertToJsonObject(action3));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeArray(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        Response response = Response.ok(is).build();

        ActionResourceClient resourceClient = Mockito.mock(ActionResourceClient.class);
        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getActions(null,ids,true,true)).
                thenThrow(new TokenExpiredException("test")).
                thenReturn(response);

        when(tokensPlaceHolder.getRefreshToken()).thenReturn("refreshToken");
        when(userClient.refreshToken(any())).thenReturn(Response.ok().entity("refreshToken").build());

        List<? extends SystemAction> outcome = target.getActionsByIds(ids);
        assertNotNull(outcome);
        assertEquals(1, outcome.size());
    }
}