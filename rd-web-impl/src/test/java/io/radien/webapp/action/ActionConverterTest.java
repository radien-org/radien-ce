/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
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
package io.radien.webapp.action;

import io.radien.exception.SystemException;
import io.radien.webapp.JSFUtilAndFaceContextMessagesTest;
import javax.faces.convert.ConverterException;

import io.radien.api.model.permission.SystemAction;
import io.radien.api.service.permission.ActionRESTServiceAccess;

import io.radien.ms.permissionmanagement.client.entities.Action;

import java.util.Optional;


import javax.faces.context.FacesContext;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import static org.mockito.Mockito.doReturn;
/**
 * Class that aggregates UnitTest cases for ActionConverter
 *
 * @author Rajesh Gavvala
 */
public class ActionConverterTest extends JSFUtilAndFaceContextMessagesTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @InjectMocks
    private ActionConverter actionConverter;

    @Mock
    private ActionRESTServiceAccess actionRESTServiceAccess;

    static FacesContext facesContext;

    SystemAction systemAction;
    Optional<SystemAction> optionalSystemAction;

    /**
     * Constructs mock object
     */
    @BeforeClass
    public static void beforeClass(){
        facesContext = getFacesContext();
    }

    @AfterClass
    public static void afterClass(){
        destroy();
    }

    @Before
    public void before(){
        systemAction = new Action();
        systemAction.setName("testAction");

        optionalSystemAction = Optional.of(systemAction);
    }

    /**
     * Test method getAsString()
     * Asserts Object couldn't be null
     */
    @Test
    public void testGetAsObject() throws SystemException {
        doReturn(optionalSystemAction).when(actionRESTServiceAccess).getActionByName("testAction");
        assertNotNull(actionConverter.getAsObject(facesContext, null, "testAction"));
    }

    /**
     * Test method getAsString()
     * Asserts Object null
     */
    @Test
    public void testGetAsObjectNull() {
        assertNull(actionConverter.getAsObject(facesContext, null, ""));
    }

    /**
     * Test method getAsString()
     * Asserts ConversionException
     */
    @Test(expected = ConverterException.class)
    public void testGetAsObjectConversionError() throws SystemException {
        doReturn(optionalSystemAction).when(actionRESTServiceAccess).getActionByName("");
        actionConverter.getAsObject(facesContext, null, "testAction");
    }

    /**
     * Test method getAsString()
     * Asserts equality the test method
     * Returns value
     */
    @Test
    public void testGetAsString(){
        String expected = actionConverter.getAsString(null, null, systemAction);
        assertEquals(systemAction.getName(), expected);
    }
}