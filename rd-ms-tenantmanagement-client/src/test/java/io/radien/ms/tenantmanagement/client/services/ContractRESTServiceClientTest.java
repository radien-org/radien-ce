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
package io.radien.ms.tenantmanagement.client.services;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.entity.Page;
import io.radien.ms.tenantmanagement.client.entities.Contract;
import io.radien.ms.tenantmanagement.client.util.ClientServiceUtil;
import io.radien.ms.tenantmanagement.client.util.FactoryUtilService;
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
    TenantRESTServiceClient target;

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

        assertEquals(Optional.empty(),target.getTenantByName(a));
    }

    private String getContractManagementUrl(){
        String url = "";
        when(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT)).thenReturn(url);
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

        assertTrue(!target.getTenantByName(a).isEmpty());
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
            target.getTenantByName(a);
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
            target.getTenantByName("a");
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
            target.getTenantByName(a);
        }catch (ProcessingException es){
            success = true;
        }
        assertTrue(success);
    }

    // TODO: Bruno Gama 1.0 - Reve o teste
//    @Test
//    public void testCreate() throws MalformedURLException {
//        ContractResourceClient resourceClient = Mockito.mock(ContractResourceClient.class);
//        when(resourceClient.create(any())).thenReturn(Response.ok().build());
//        when(clientServiceUtil.getContractResourceClient(getContractManagementUrl())).thenReturn(resourceClient);
//
//        assertTrue(target.create(new Contract()));
//
//    }

    // TODO: Bruno Gama 1.0 - Reve o teste
//    @Test
//    public void testCreateFail() throws MalformedURLException {
//        ContractResourceClient resourceClient = Mockito.mock(ContractResourceClient.class);
//        when(resourceClient.create(any())).thenReturn(Response.serverError().entity("test error msg").build());
//        when(clientServiceUtil.getContractResourceClient(getContractManagementUrl())).thenReturn(resourceClient);
//
//        assertFalse(target.create(new Contract()));
//
//    }

    // TODO: Bruno Gama 1.0 - Reve o teste
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
//    }

    // TODO: Bruno Gama 1.0 - Reve o teste
//    @Test
//    public void testDelete() throws MalformedURLException {
//        ContractResourceClient resourceClient = Mockito.mock(ContractResourceClient.class);
//        when(resourceClient.delete(1L)).thenReturn(Response.ok().build());
//        when(clientServiceUtil.getContractResourceClient(getContractManagementUrl())).thenReturn(resourceClient);
//
//        assertTrue(target.delete(1L));
//    }

    // TODO: Bruno Gama 1.0 - Reve o teste
//    @Test
//    public void testDeleteFail() throws MalformedURLException {
//        ContractResourceClient resourceClient = Mockito.mock(ContractResourceClient.class);
//        when(resourceClient.delete(1L)).thenReturn(Response.serverError().entity("test error msg").build());
//        when(clientServiceUtil.getContractResourceClient(getContractManagementUrl())).thenReturn(resourceClient);
//
//        assertFalse(target.delete(1L));
//    }

    // TODO: Bruno Gama 1.0 - Reve o teste
//    @Test
//    public void testDeleteProcessingException() throws MalformedURLException {
//        ContractResourceClient resourceClient = Mockito.mock(ContractResourceClient.class);
//        when(resourceClient.delete(1L)).thenThrow(new ProcessingException(""));
//        when(clientServiceUtil.getContractResourceClient(getContractManagementUrl())).thenReturn(resourceClient);
//        boolean success = false;
//        try {
//            target.delete(1L);
//        }catch (ProcessingException es){
//            success = true;
//        }
//        assertTrue(success);
//    }

    // TODO: Bruno Gama 1.0 - Reve o teste
//    @Test
//    public void testUpdate() throws MalformedURLException {
//        ContractResourceClient resourceClient = Mockito.mock(ContractResourceClient.class);
//        Contract contract = new Contract();
//        contract.setId(1L);
//        when(resourceClient.update(contract.getId(),contract)).thenReturn(Response.ok().build());
//        when(clientServiceUtil.getContractResourceClient(getContractManagementUrl())).thenReturn(resourceClient);
//
//        assertTrue(target.update(contract));
//
//    }

    // TODO: Bruno Gama 1.0 - Reve o teste
//    @Test
//    public void testUpdateFail() throws MalformedURLException {
//        ContractResourceClient resourceClient = Mockito.mock(ContractResourceClient.class);
//        Contract contract = new Contract();
//        contract.setId(1L);
//        when(resourceClient.update(contract.getId(),contract)).thenReturn(Response.serverError().entity("test error msg").build());
//        when(clientServiceUtil.getContractResourceClient(getContractManagementUrl())).thenReturn(resourceClient);
//
//        assertFalse(target.update(contract));
//    }

    // TODO: Bruno Gama 1.0 - Reve o teste
//    @Test
//    public void testUpdateProcessingException() throws MalformedURLException {
//        ContractResourceClient resourceClient = Mockito.mock(ContractResourceClient.class);
//        Contract contract = new Contract();
//        contract.setId(1L);
//        when(resourceClient.update(contract.getId(), contract)).thenThrow(new ProcessingException(""));
//        when(clientServiceUtil.getContractResourceClient(getContractManagementUrl())).thenReturn(resourceClient);
//        boolean success = false;
//        try {
//            target.update(contract);
//        }catch (ProcessingException es){
//            success = true;
//        }
//        assertTrue(success);
//    }

    // TODO: Bruno Gama 1.0 - Reve o teste
//    @Test
//    public void testGetAll() throws Exception {
//        JsonArrayBuilder builder = Json.createArrayBuilder();
//
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        JsonWriter jsonWriter = Json.createWriter(baos);
//        jsonWriter.writeArray(builder.build());
//        jsonWriter.close();
//
//        InputStream is = new ByteArrayInputStream(baos.toByteArray());
//
//        Response response = Response.ok(is).build();
//
//        ContractResourceClient resourceClient = Mockito.mock(ContractResourceClient.class);
//
//        when(resourceClient.get(null))
//                .thenReturn(response);
//
//        when(clientServiceUtil.getContractResourceClient(getContractManagementUrl())).thenReturn(resourceClient);
//
//        assertEquals(0,target.getAll().size());
//    }

    // TODO: Bruno Gama 1.0 - Reve o teste
//    @Test
//    public void testGetAllException() throws Exception {
//        boolean success = false;
//        when(clientServiceUtil.getContractResourceClient(getContractManagementUrl())).thenThrow(new ExtensionException(new Exception()));
//        try {
//            target.getAll();
//        }catch (ExtensionException es){
//            success = true;
//        }
//        assertTrue(success);
//    }
}