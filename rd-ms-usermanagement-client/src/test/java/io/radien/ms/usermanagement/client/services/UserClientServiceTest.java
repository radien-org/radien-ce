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

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.entity.Page;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.ms.usermanagement.client.util.ClientServiceUtil;
import io.radien.ms.usermanagement.client.util.FactoryUtilService;
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
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author Nuno Santana
 * @author Bruno Gama
 */
public class UserClientServiceTest {

    @InjectMocks
    UserClientService target;

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

        when(resourceClient.getUsersBy(a,null,null,true,true))
                .thenReturn(response);

        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);

        assertEquals(Optional.empty(),target.getUserBySub(a));
    }

    private String getUserManagementUrl(){
        String url = "";
        when(oafAccess.getProperty(OAFProperties.USER_MANAGEMENT_MS_URL)).thenReturn(url);
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
        when(resourceClient.getUsersBy(a,null,null,true,true))
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
        when(resourceClient.getUsersBy(a,null,null,true,true))
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
        }catch (ExtensionException es){
            success = true;
        }
        assertTrue(success);
    }
    @Test
    public void testGetUserBySubProcessingException() throws Exception {
        boolean success = false;
        String a = "a";
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.getUsersBy(a,null,null,true,true))
                .thenThrow(new ProcessingException("test"));
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);

        try {
            target.getUserBySub(a);
        }catch (ProcessingException es){
            success = true;
        }
        assertTrue(success);
    }
    @Test
    public void testCreate() throws MalformedURLException {
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.save(any())).thenReturn(Response.ok().build());
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);

        assertTrue(target.create(new User()));

    }
    @Test
    public void testCreateFail() throws MalformedURLException {
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
        }catch (ProcessingException es){
            success = true;
        }
        assertTrue(success);

    }

}