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
package io.radien.webapp;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Abstract class to be extended by Any Unit Test class that need to deal
 * with (or mock, or emulate) JSF related components like {@link FacesContext},
 * {@link JSFUtil}, {@link ExternalContext} and so on.
 */
public abstract class AbstractBaseJsfTester {


    protected FacesContext facesContext;

    private static MockedStatic<FacesContext> facesContextMockedStatic;
    private static MockedStatic<JSFUtil> jsfUtilMockedStatic;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @BeforeClass
    public static void beforeClass(){
        facesContextMockedStatic = Mockito.mockStatic(FacesContext.class);
        jsfUtilMockedStatic = Mockito.mockStatic(JSFUtil.class);
    }
    @AfterClass
    public static final void destroy(){
        if(facesContextMockedStatic!=null) {
            facesContextMockedStatic.close();
        }
        if(jsfUtilMockedStatic!=null) {
            jsfUtilMockedStatic.close();
        }
    }

    /**
     * Method for preparing all the mocks
     */
    @Before
    public void before() {

        facesContext = mock(FacesContext.class);
        when(FacesContext.getCurrentInstance()).thenReturn(facesContext);

        ExternalContext externalContext = mock(ExternalContext.class);
        when(facesContext.getExternalContext())
                .thenReturn(externalContext);

        Flash flash = mock(Flash.class);
        when(externalContext.getFlash()).thenReturn(flash);

        when(JSFUtil.getFacesContext()).thenReturn(facesContext);
        when(JSFUtil.getExternalContext()).thenReturn(externalContext);
        when(JSFUtil.getMessage(anyString())).thenAnswer(i -> i.getArguments()[0]);
    }
}
