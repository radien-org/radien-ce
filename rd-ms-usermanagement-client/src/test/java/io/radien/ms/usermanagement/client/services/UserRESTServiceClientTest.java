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
package io.radien.ms.usermanagement.client.services;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;

import io.radien.api.OAFAccess;
import io.radien.api.model.user.SystemUser;
import io.radien.api.service.batch.BatchSummary;
import io.radien.api.service.batch.DataIssue;
import io.radien.api.util.FactoryUtilService;
import org.apache.cxf.bus.extension.ExtensionException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import io.radien.api.Configurable;
import io.radien.api.OAFProperties;
import io.radien.api.entity.Page;
import io.radien.exception.SystemException;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.ms.usermanagement.client.util.ClientServiceUtil;

/**
 * @author Nuno Santana
 * @author Bruno Gama
 */
public class UserRESTServiceClientTest {

    @InjectMocks
    UserRESTServiceClient target;

    @Mock
    ClientServiceUtil clientServiceUtil;

    @Mock
    OAFAccess oafAccess;

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetUserBySub() throws Exception {
        String a = "a";

        JsonArrayBuilder builder = Json.createArrayBuilder();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeArray(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        Response response = Response.ok(is).build();

        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);

        when(resourceClient.getUsers(a,null,null,true,true))
                .thenReturn(response);

        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);

        assertEquals(Optional.empty(),target.getUserBySub(a));
    }

    private String getUserManagementUrl(){
        String url = "";
        when(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_USERMANAGEMENT)).thenReturn(url);
        return url;
    }

    @Test
    public void testGetUserBySubWithResults() throws Exception {
        String a = "a";
        User user = UserFactory.create(null, null, "logon", null, null, null);
        user.setSub(a);

        JsonArrayBuilder builder = Json.createArrayBuilder();
        builder.add(UserFactory.convertToJsonObject(user));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeArray(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        Response response = Response.ok(is).build();

        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.getUsers(a,null,null,true,true))
                .thenReturn(response);
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);

        assertTrue(target.getUserBySub(a).isPresent());
    }

    @Test
    public void testGetUserBySubNonUnique() throws Exception {
        String a = "a";
        Page<User> page = new Page<>(new ArrayList<>(),1,2,0);

        JsonObjectBuilder builder = Json.createObjectBuilder();
        FactoryUtilService.addValueInt(builder, "currentPage", page.getCurrentPage());
        FactoryUtilService.addValueInt(builder, "totalPages", page.getTotalPages());
        FactoryUtilService.addValueInt(builder, "totalResults", page.getTotalResults());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeObject(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        Response response = Response.ok(is).build();

        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.getUsers(a,null,null,true,true))
                .thenReturn(response);
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        boolean success = false;
        try {
            target.getUserBySub(a);
        } catch (Exception e){
            success = true;
        }
        assertTrue(success);
    }

    @Test
    public void testGetUserBySubExtensionException() throws Exception {
        boolean success = false;
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenThrow(new ExtensionException(new Exception()));
        try {
            target.getUserBySub("a");
        }catch (SystemException es){
            if (es.getMessage().contains(ExtensionException.class.getName())) {
                success = true;
            }
        }
        assertTrue(success);
    }
    @Test
    public void testGetUserBySubProcessingException() throws Exception {
        boolean success = false;
        String a = "a";
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.getUsers(a,null,null,true,true))
                .thenThrow(new ProcessingException("test"));
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);

        try {
            target.getUserBySub(a);
        }catch (SystemException es){
            if (es.getMessage().contains(ProcessingException.class.getName())) {
                success = true;
            }
        }
        assertTrue(success);
    }
    @Test
    public void testCreate() throws MalformedURLException, SystemException {
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.save(any())).thenReturn(Response.ok().build());
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        assertTrue(target.create(new User()));
    }
    @Test
    public void testCreateFail() throws MalformedURLException, SystemException {
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.save(any())).thenReturn(Response.serverError().entity("test error msg").build());
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        assertFalse(target.create(new User()));
    }

    @Test
    public void testCreateProcessingException() throws MalformedURLException {
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.save(any())).thenThrow(new ProcessingException(""));
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        boolean success = false;
        try {
            target.create(new User());
        }catch (ProcessingException | SystemException es){
            success = true;
        }
        assertTrue(success);

    }

    @Test
    public void testCreateBatch() throws MalformedURLException {
        List<SystemUser> userList = new ArrayList<>();
        BatchSummary batchSummary = new BatchSummary(100);
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.create(anyList())).thenReturn(Response.ok().entity(batchSummary).build());
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        Optional<BatchSummary> opt = target.create(userList);
        assertTrue(opt.isPresent());
        assertNotNull(opt.get());
        assertEquals(opt.get().getInternalStatus(), BatchSummary.ProcessingStatus.SUCCESS);
    }

    @Test
    public void testCreateBatchNoneInserted() throws MalformedURLException {
        int rows = 4;
        List<SystemUser> userList = new ArrayList<>();
        List<DataIssue> issues = new ArrayList<>();
        for (int rowIndex=1; rowIndex<=rows; rowIndex++) {
            issues.add(new DataIssue(rowIndex, "Invalid fiel"));
        }
        BatchSummary batchSummary = new BatchSummary(rows, issues);
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.create(anyList())).thenReturn(Response.ok().entity(batchSummary).build());
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        Optional<BatchSummary> opt = target.create(userList);
        assertTrue(opt.isPresent());
        assertNotNull(opt.get());
        assertEquals(opt.get().getTotal(), rows);
        assertEquals(opt.get().getTotalProcessed(), 0);
        assertEquals(opt.get().getTotalNonProcessed(), rows);
        assertEquals(opt.get().getInternalStatus(), BatchSummary.ProcessingStatus.FAIL);
    }

    @Test
    public void testCreateBatchSomeNotInserted() throws MalformedURLException {
        int rows = 10;
        List<SystemUser> userList = new ArrayList<>();
        List<DataIssue> issues = new ArrayList<>();
        for (int rowIndex=1; rowIndex<=rows-6; rowIndex++) {
            issues.add(new DataIssue(rowIndex, "Invalid fiel"));
        }
        BatchSummary batchSummary = new BatchSummary(rows, issues);
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.create(anyList())).thenReturn(Response.ok().entity(batchSummary).build());
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        Optional<BatchSummary> opt = target.create(userList);
        assertTrue(opt.isPresent());
        assertNotNull(opt.get());
        assertEquals(opt.get().getTotal(), rows);
        assertEquals(opt.get().getTotalProcessed(), 6);
        assertEquals(opt.get().getNonProcessedItems().size(), 4);
        assertEquals(opt.get().getInternalStatus(), BatchSummary.ProcessingStatus.PARTIAL_SUCCESS);
    }


    @Test
    public void testCreateBatchFail() throws MalformedURLException {
        List<SystemUser> userList = new ArrayList<>();
        BatchSummary batchSummary = new BatchSummary(100);
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.create(anyList())).thenReturn(Response.serverError().entity("test error msg").build());
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);
        Optional<BatchSummary> opt = target.create(userList);
        assertFalse(opt.isPresent());
    }

}