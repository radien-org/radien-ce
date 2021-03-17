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
import io.radien.exception.SystemException;
import io.radien.ms.permissionmanagement.client.entities.Action;
import io.radien.ms.permissionmanagement.client.entities.Permission;
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
import static org.mockito.Mockito.when;

/**
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

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

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

    private String getActionManagementUrl(){
        String url = "";
        when(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_PERMISSIONMANAGEMENT)).thenReturn(url);
        return url;
    }

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

    @Test
    public void testGetActionByIdWithResults() throws Exception {
        Action action = ActionFactory.create("permission-a", 2l);
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

    @Test
    public void testGetActionByNameExtensionException() throws Exception {
        boolean success = false;
        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).thenThrow(new ExtensionException(new Exception()));
        try {
            target.getActionByName("a");
        }catch (SystemException se){
        	if (se.getMessage().contains(ExtensionException.class.getName())) {
        		success = true;
        	}
        }
        assertTrue(success);
    }

    @Test
    public void testGetActionByNameProcessingException() throws Exception {
        boolean success = false;
        String a = "a";
        ActionResourceClient resourceClient = Mockito.mock(ActionResourceClient.class);
        when(resourceClient.getActions(a,true,true))
                .thenThrow(new ProcessingException("test"));
        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).thenReturn(resourceClient);

        try {
            target.getActionByName(a);
        }
        catch (SystemException se){
        	if (se.getMessage().contains(ProcessingException.class.getName())) {
        		success = true;
        	}
        }
        assertTrue(success);
    }
    @Test
    public void testCreate() throws MalformedURLException {
        ActionResourceClient resourceClient = Mockito.mock(ActionResourceClient.class);
        when(resourceClient.save(any())).thenReturn(Response.ok().build());
        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).thenReturn(resourceClient);
        boolean success = false;
        try {
			assertTrue(target.create(new Action()));
		} catch (SystemException e) {
			success = true;
        }
        assertFalse(success);
    }
    @Test
    public void testCreateFail() throws MalformedURLException {
        ActionResourceClient resourceClient = Mockito.mock(ActionResourceClient.class);
        when(resourceClient.save(any())).thenReturn(Response.serverError().entity("test error msg").build());
        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).thenReturn(resourceClient);
        boolean success = false;
        try {
			assertFalse(target.create(new Action()));
		} catch (SystemException e) {
			 success = true;
        }
        assertFalse(success);
    }

    @Test
    public void testCreateProcessingException() throws MalformedURLException {
        ActionResourceClient resourceClient = Mockito.mock(ActionResourceClient.class);
        when(resourceClient.save(any())).thenThrow(new ProcessingException(""));
        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).thenReturn(resourceClient);
        boolean success = false;
        try {
            target.create(new Action());
        }catch (ProcessingException | SystemException es){
            success = true;
        }
        assertTrue(success);

    }

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

    @Test
    public void testAccessingOAF() {
        OAFAccess oafAccess = target.getOAF();
        assertNotNull(oafAccess);
    }


    @Test
    public void testGetActionByIdExtensionException() throws Exception {
        boolean success = false;
        when(clientServiceUtil.getActionResourceClient(
                getActionManagementUrl())).thenThrow(new ExtensionException(new Exception()));
        try {
            target.getActionById(1L);
        }catch (SystemException se){
            if (se.getMessage().contains(ExtensionException.class.getName())) {
                success = true;
            }
        }
        assertTrue(success);
    }

    @Test
    public void testGetActionByIdProcessingException() throws Exception {
        boolean success = false;
        ActionResourceClient resourceClient = Mockito.mock(ActionResourceClient.class);
        when(resourceClient.getById(anyLong()))
                .thenThrow(new ProcessingException("test"));
        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).thenReturn(resourceClient);

        try {
            target.getActionById(1L);
        }
        catch (SystemException se){
            if (se.getMessage().contains(ProcessingException.class.getName())) {
                success = true;
            }
        }
        assertTrue(success);
    }

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
        assertEquals(retrievedPage.getResults().size(), 3);
    }

    @Test
    public void testGetActionsWhenFailure() throws MalformedURLException {
        List<String> sortBy = new ArrayList<>();
        Response errorResponse = Response.status(500).build();
        ActionResourceClient resourceClient = Mockito.mock(ActionResourceClient.class);
        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).
                thenThrow(new ProcessingException("error"));
        when(resourceClient.getAll("action%", 1, 100, sortBy, true)).
                then(i -> errorResponse);
        assertThrows(SystemException.class, () -> target.getAll("action%", 1, 100, sortBy, true));
    }

    @Test
    public void testGetTotalRecordsCount() throws MalformedURLException {
        Action action = ActionFactory.create("test", null);

        JsonArrayBuilder builder = Json.createArrayBuilder();
        builder.add(ActionFactory.convertToJsonObject(action));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeArray(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        Response response = Response.ok(is).build();

        ActionResourceClient resourceClient = Mockito.mock(ActionResourceClient.class);

        when(resourceClient.getTotalRecordsCount()).thenReturn(response);

        when(clientServiceUtil.getActionResourceClient(getActionManagementUrl())).thenReturn(resourceClient);

        assertThrows(SystemException.class, () -> target.getTotalRecordsCount());
    }

}