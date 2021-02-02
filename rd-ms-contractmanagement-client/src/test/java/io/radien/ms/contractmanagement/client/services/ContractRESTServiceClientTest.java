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
package io.radien.ms.contractmanagement.client.services;

import io.radien.api.Configurable;
import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.entity.Page;
import io.radien.ms.contractmanagement.client.entities.Contract;
import io.radien.ms.contractmanagement.client.util.ClientServiceUtil;
import io.radien.ms.contractmanagement.client.util.FactoryUtilService;
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
public class ContractRESTServiceClientTest {

    @InjectMocks
    ContractRESTServiceClient target;

    @Mock
    ClientServiceUtil clientServiceUtil;

    @Mock
    OAFAccess oafAccess;

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetContractByName() throws Exception {
        String a = "a";

        JsonArrayBuilder builder = Json.createArrayBuilder();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeArray(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        Response response = Response.ok(is).build();

        ContractResourceClient resourceClient = Mockito.mock(ContractResourceClient.class);

        when(resourceClient.get(a))
                .thenReturn(response);

        when(clientServiceUtil.getContractResourceClient(getContractManagementUrl())).thenReturn(resourceClient);

        assertEquals(Optional.empty(),target.getContractByName(a));
    }

    private String getContractManagementUrl(){
        String url = "";
        when(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_CONTRACTMANAGEMENT)).thenReturn(url);
        return url;
    }

    @Test
    public void testGetContractByNameWithResults() throws Exception {
        String a = "a";
        Contract contract = ContractFactory.create(a,null,null,1L);

        JsonArrayBuilder builder = Json.createArrayBuilder();
        builder.add(ContractFactory.convertToJsonObject(contract));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeArray(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        Response response = Response.ok(is).build();

        ContractResourceClient resourceClient = Mockito.mock(ContractResourceClient.class);
        when(resourceClient.get(a))
                .thenReturn(response);
        when(clientServiceUtil.getContractResourceClient(getContractManagementUrl())).thenReturn(resourceClient);

        assertTrue(target.getContractByName(a).isPresent());
    }

    @Test
    public void testGetContractByNameNonUnique() throws Exception {
        String a = "a";
        Page<Contract> page = new Page<>(new ArrayList<>(),1,2,0);

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

        ContractResourceClient resourceClient = Mockito.mock(ContractResourceClient.class);
        when(resourceClient.get(a))
                .thenReturn(response);
        when(clientServiceUtil.getContractResourceClient(getContractManagementUrl())).thenReturn(resourceClient);
        boolean success = false;
        try {
            target.getContractByName(a);
        } catch (Exception e){
            success = true;
        }
        assertTrue(success);
    }

    @Test
    public void testGetUserBySubExtensionException() throws Exception {
        boolean success = false;
        when(clientServiceUtil.getContractResourceClient(getContractManagementUrl())).thenThrow(new ExtensionException(new Exception()));
        try {
            target.getContractByName("a");
        }catch (ExtensionException es){
            success = true;
        }
        assertTrue(success);
    }

    @Test
    public void testGetContractByNameProcessingException() throws Exception {
        boolean success = false;
        String a = "a";
        ContractResourceClient resourceClient = Mockito.mock(ContractResourceClient.class);
        when(resourceClient.get(a))
                .thenThrow(new ProcessingException("test"));
        when(clientServiceUtil.getContractResourceClient(getContractManagementUrl())).thenReturn(resourceClient);

        try {
            target.getContractByName(a);
        }catch (ProcessingException es){
            success = true;
        }
        assertTrue(success);
    }

//    @Test
//    public void testCreate() throws MalformedURLException {
//        ContractResourceClient resourceClient = Mockito.mock(ContractResourceClient.class);
//        when(resourceClient.create(any())).thenReturn(Response.ok().build());
//        when(clientServiceUtil.getContractResourceClient(getContractManagementUrl())).thenReturn(resourceClient);
//
//        assertTrue(target.create(new Contract()));
//
//    }
//    @Test
//    public void testCreateFail() throws MalformedURLException {
//        ContractResourceClient resourceClient = Mockito.mock(ContractResourceClient.class);
//        when(resourceClient.create(any())).thenReturn(Response.serverError().entity("test error msg").build());
//        when(clientServiceUtil.getContractResourceClient(getContractManagementUrl())).thenReturn(resourceClient);
//
//        assertFalse(target.create(new Contract()));
//
//    }

//    @Test
//    public void testCreateProcessingException() throws MalformedURLException {
//        ContractResourceClient resourceClient = Mockito.mock(ContractResourceClient.class);
//        when(resourceClient.create(any())).thenThrow(new ProcessingException(""));
//        when(clientServiceUtil.getContractResourceClient(getContractManagementUrl())).thenReturn(resourceClient);
//        boolean success = false;
//        try {
//            target.create(new Contract());
//        }catch (ProcessingException es){
//            success = true;
//        }
//        assertTrue(success);
//
//    }

}