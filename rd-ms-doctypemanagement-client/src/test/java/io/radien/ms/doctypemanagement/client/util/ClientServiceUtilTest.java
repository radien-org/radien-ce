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

package io.radien.ms.doctypemanagement.client.util;

import java.net.MalformedURLException;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertTrue;

public class ClientServiceUtilTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @InjectMocks
    private ClientServiceUtil serviceUtil;

    @Test
    public void testGetPropertyDefinitionResourceClient() {
        boolean valid = true;
        try {
            serviceUtil.getPropertyDefinitionClient("http://url.test.pt") ;
        } catch (MalformedURLException e) {
            valid = false;
        }
        assertTrue(valid);
    }

    @Test
    public void testGetMixinDefinitionResourceClient() {
        boolean valid = true;
        try {
            serviceUtil.getMixinDefinitionClient("http://url.test.pt") ;
        } catch (MalformedURLException e) {
            valid = false;
        }
        assertTrue(valid);
    }

    @Test(expected = MalformedURLException.class)
    public void testGetPropertyDefinitionResourceClientException() throws MalformedURLException {
        serviceUtil.getPropertyDefinitionClient("not.a.valid.url") ;
    }

    @Test(expected = MalformedURLException.class)
    public void testGetMixinDefinitionResourceClientException() throws MalformedURLException {
        serviceUtil.getMixinDefinitionClient("not.a.valid.url") ;
    }
}
