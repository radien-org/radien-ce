/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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
package ${package}.service;

import io.radien.api.Configurable;
import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;

import ${package}.entities.${resource-name};
import ${package}.util.ClientServiceUtil;
import ${package}.util.FactoryUtilService;

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

/**
 * @author Rajesh Gavvala
 * @author Nuno Santana
 * @author Bruno Gama
 */
public class ${resource-name}RESTServiceClientTest {

    @InjectMocks
    ${resource-name}RESTServiceClient target;

    @Mock
    ClientServiceUtil clientServiceUtil;

    @Mock
    OAFAccess oafAccess;

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    private String get${resource-name}ManagementUrl(){
        String url = "";
        when(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_${resource-nameInUpperCase}MANAGEMENT)).thenReturn(url);
        return url;
    }


    @Test
    public void testCreate() throws MalformedURLException {
        ${resource-name}ResourceClient resourceClient = Mockito.mock(${resource-name}ResourceClient.class);
        when(resourceClient.create(any())).thenReturn(Response.ok().build());
        when(clientServiceUtil.get${resource-name}ResourceClient(get${resource-name}ManagementUrl())).thenReturn(resourceClient);

        boolean success = false;
        try {
            assertTrue(target.create(new ${resource-name}()));
        } catch (Exception e) {
            success = true;
        }
        assertFalse(success);

        assertTrue(target.create(new ${resource-name}()));

    }
    @Test
    public void testCreateFail() throws MalformedURLException {
        ${resource-name}ResourceClient resourceClient = Mockito.mock(${resource-name}ResourceClient.class);
        when(resourceClient.create(any())).thenReturn(Response.serverError().entity("test error msg").build());
        when(clientServiceUtil.get${resource-name}ResourceClient(any())).thenReturn(resourceClient);

        boolean success = false;
        try {
            assertFalse(target.create(new ${resource-name}()));
        } catch (Exception e) {
            success = true;
        }
        assertFalse(success);
    }
}