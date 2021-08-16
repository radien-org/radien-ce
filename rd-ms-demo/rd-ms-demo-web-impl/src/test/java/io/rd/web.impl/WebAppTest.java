/*
 * Copyright (c) 2006-present openappframe.org & its legal owners. All rights reserved.
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

package io.rd.web.impl;

import io.rd.api.OAFAccess;
import io.rd.api.OAFProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 *
 * @author Rajesh Gavvala
 */
@RunWith(PowerMockRunner.class)
public class WebAppTest {
    @InjectMocks
    private WebApp webApp;
    @Mock
    private OAFAccess oaf;


    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getOAF_test() {
        assertEquals(oaf, webApp.getOAF());
    }

    @Test
    public void getProperty_test() {
        String expected = webApp.getProperty(OAFProperties.SYS_DEFAULT_LOCALE);
        assertNull(expected);
    }

}
