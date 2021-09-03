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
package io.radien.webapp.role;

import io.radien.api.model.role.SystemRole;
import io.radien.api.service.role.RoleRESTServiceAccess;

import io.radien.ms.rolemanagement.client.entities.Role;

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
 * Class that aggregates UnitTest cases for RoleConverter
 *
 * @author Rajesh Gavvala
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({JSFUtil.class, FacesContext.class, ExternalContext.class})
public class RoleConverterTest extends JSFUtilAndFaceContextMessagesTest {
    @InjectMocks
    private RoleConverter roleConverter;

    @Mock
    private RoleRESTServiceAccess roleRESTServiceAccess;

    FacesContext facesContext;

    SystemRole systemRole;
    Optional<SystemRole> optionalSystemRole;

    /**
     * Constructs mock object
     */
    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);

        facesContext = getFacesContext();

        systemRole = new Role();
        systemRole.setName("testAction");

        optionalSystemRole = Optional.of(systemRole);
    }

    /**
     * Test method getAsString()
     * Asserts Object couldn't be null
     */
    @Test
    public void testGetAsObject() throws Exception {
        doReturn(optionalSystemRole).when(roleRESTServiceAccess).getRoleByName("testRole");
        assertNotNull(roleConverter.getAsObject(facesContext, null, "testRole"));
    }

    /**
     * Test method getAsString()
     * Asserts Object null
     */
    @Test
    public void testGetAsObjectNull() {
        assertNull(roleConverter.getAsObject(facesContext, null, ""));
    }

    /**
     * Test method getAsString()
     * Asserts ConversionException
     */
    @Test(expected = ConverterException.class)
    public void testGetAsObjectConversionError() throws Exception {
        doReturn(optionalSystemRole).when(roleRESTServiceAccess).getRoleByName("");
        roleConverter.getAsObject(facesContext, null, "testRole");
    }

    /**
     * Test method getAsString()
     * Asserts equality the test method
     * Returns value
     */
    @Test
    public void testGetAsString(){
        String expected = roleConverter.getAsString(null, null, systemRole);
        assertEquals(systemRole.getName(), expected);
    }
}