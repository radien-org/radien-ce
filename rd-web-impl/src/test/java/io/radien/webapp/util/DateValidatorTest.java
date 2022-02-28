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

import io.radien.webapp.JSFUtilAndFaceContextMessagesTest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.mockito.Mockito.doReturn;
/**
 * Class that aggregates UnitTest cases for DateValidator
 *
 * @author Rajesh Gavvala
 */
public class DateValidatorTest extends JSFUtilAndFaceContextMessagesTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @InjectMocks
    private DateValidator dateValidator;

    @Mock
    private UIComponent component;

    Date now = new Date();
    static FacesContext facesContext;

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

    /**
     * Test method validator()
     * Asserts an exception
     */
    @Test(expected = javax.faces.validator.ValidatorException.class)
    public void testValidatorComponent() {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("validatorMessage", "");

        doReturn(hashMap).when(component).getAttributes();

        dateValidator.validate(facesContext, component, now);
    }

}