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
package io.radien.webapp.util;

import io.radien.webapp.JSFUtil;
import io.radien.webapp.JSFUtilAndFaceContextMessagesTest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.doReturn;
/**
 * Class that aggregates UnitTest cases for DateValidator
 *
 * @author Rajesh Gavvala
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({JSFUtil.class, FacesContext.class, ExternalContext.class})
public class DateValidatorTest extends JSFUtilAndFaceContextMessagesTest {
    @InjectMocks
    private DateValidator dateValidator;

    @Mock
    private UIComponent component;

    Date now = new Date();
    FacesContext facesContext;

    /**
     * Constructs mock object
     */
    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);

        facesContext = getFacesContext();
    }

    /**
     * Test method validator()
     * Asserts an exception
     */
    @Test(expected = ExceptionInInitializerError.class)
    public void testValidatorComponent() {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("validatorMessage", "");

        doReturn(hashMap).when(component).getAttributes();

        dateValidator.validate(facesContext, component, now);
    }

}