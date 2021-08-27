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
package io.radien.webapp.permission;

import io.radien.exception.SystemException;

import io.radien.api.model.permission.SystemPermission;
import io.radien.api.service.permission.PermissionRESTServiceAccess;

import io.radien.ms.permissionmanagement.client.entities.Permission;

import io.radien.webapp.JSFUtil;
import io.radien.webapp.JSFUtilAndFaceContextMessagesTest;

import java.util.Optional;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import static org.mockito.Mockito.doReturn;
/**
 * Class that aggregates UnitTest cases for PermissionConverter
 *
 * @author Rajesh Gavvala
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({JSFUtil.class, FacesContext.class, ExternalContext.class})
public class PermissionConverterTest extends JSFUtilAndFaceContextMessagesTest {

    @InjectMocks
    private PermissionConverter permissionConverter;

    @Mock
    private PermissionRESTServiceAccess permissionRESTServiceAccess;

    FacesContext facesContext;

    SystemPermission systemPermission;
    Optional<SystemPermission> optionalSystemPermission;


    /**
     * Constructs mock object
     */
    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);

        facesContext = getFacesContext();

        systemPermission = new Permission();
        systemPermission.setName("testPermission");

        optionalSystemPermission = Optional.of(systemPermission);
    }

    /**
     * Test method getAsString()
     * Asserts Object couldn't be null
     */
    @Test
    public void testGetAsObject() throws SystemException {
        doReturn(optionalSystemPermission).when(permissionRESTServiceAccess).getPermissionByName("testPermission");
        assertNotNull(permissionConverter.getAsObject(facesContext, null, "testPermission"));
    }

    /**
     * Test method getAsString()
     * Asserts Object null
     */
    @Test
    public void testGetAsObjectNull() {
        assertNull(permissionConverter.getAsObject(facesContext, null, ""));
    }

    /**
     * Test method getAsString()
     * Asserts ConversionException
     */
    @Test(expected = ConverterException.class)
    public void testGetAsObjectConversionError() throws SystemException {
        doReturn(optionalSystemPermission).when(permissionRESTServiceAccess).getPermissionByName("");
        permissionConverter.getAsObject(facesContext, null, "testPermission");
    }

    /**
     * Test method getAsString()
     * Asserts equality the test method
     * Returns value
     */
    @Test
    public void testGetAsString(){
        String expected = permissionConverter.getAsString(null, null, systemPermission);
        assertEquals(systemPermission.getName(), expected);
    }
}