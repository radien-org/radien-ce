package io.radien.ms.usermanagement.client.services;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.ms.usermanagement.client.entities.Page;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.ms.usermanagement.client.util.ClientServiceUtil;
import io.radien.ms.usermanagement.client.util.FactoryUtilService;
import io.radien.ms.usermanagement.client.util.PageModelMapper;
import io.radien.ms.usermanagement.client.util.UserModelMapper;
import junit.framework.TestCase;
import org.apache.cxf.bus.extension.ExtensionException;
import org.apache.cxf.message.Message;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
        Page<User> page = new Page<>(new ArrayList<>(),1,0,0);

        JsonObjectBuilder builder = Json.createObjectBuilder();
        FactoryUtilService.addValueInt(builder, "currentPage", page.getCurrentPage());
        //FactoryUtilService.addValue(builder, "results", page.getResults());
        FactoryUtilService.addValueInt(builder, "totalPages", page.getTotalPages());
        FactoryUtilService.addValueInt(builder, "totalResults", page.getTotalResults());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeObject(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        Response response = Response.ok(is).build();

        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.getAll(
                Collections.singletonList(a), null, null, 1,
                2, null, null, null))
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
        Page<User> page = new Page<>(new ArrayList<>(),1,1,0);

        JsonObjectBuilder builder = Json.createObjectBuilder();
        FactoryUtilService.addValueInt(builder, "currentPage", page.getCurrentPage());
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

        JsonObject jsonObject = UserModelMapper.map(new User());
        arrayBuilder.add(jsonObject);


        FactoryUtilService.addValueArray(builder, "results", arrayBuilder.build());
        FactoryUtilService.addValueInt(builder, "totalPages", page.getTotalPages());
        FactoryUtilService.addValueInt(builder, "totalResults", page.getTotalResults());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeObject(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        Response response = Response.ok(is).build();

        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.getAll(
                Collections.singletonList(a), null, null, 1,
                2, null, null, null))
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
        //FactoryUtilService.addValue(builder, "results", page.getResults());
        FactoryUtilService.addValueInt(builder, "totalPages", page.getTotalPages());
        FactoryUtilService.addValueInt(builder, "totalResults", page.getTotalResults());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeObject(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        Response response = Response.ok(is).build();

        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.getAll(
                Collections.singletonList(a), null, null, 1,
                2, null, null, null))
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
        when(resourceClient.getAll(
                Collections.singletonList(a), null, null, 1,
                2, null, null, null))
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
        when(resourceClient.create(any())).thenReturn(Response.ok().build());
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);

        assertTrue(target.create(new User()));

    }
    @Test
    public void testCreateFail() throws MalformedURLException {
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.create(any())).thenReturn(Response.serverError().entity("test error msg").build());
        when(clientServiceUtil.getUserResourceClient(getUserManagementUrl())).thenReturn(resourceClient);

        assertFalse(target.create(new User()));

    }

    @Test
    public void testCreateProcessingException() throws MalformedURLException {
        UserResourceClient resourceClient = Mockito.mock(UserResourceClient.class);
        when(resourceClient.create(any())).thenThrow(new ProcessingException(""));
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