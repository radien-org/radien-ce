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
package io.radien.ms.tenantmanagement.client.entities;

import io.radien.api.security.TokensPlaceHolder;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import static org.mockito.Mockito.when;

/**
 * @author Bruno Gama
 */
public class GlobalHeadersTest extends TestCase {

    @InjectMocks
    GlobalHeaders target;

    @Mock
    TokensPlaceHolder tokensPlaceHolder;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testUpdate() {
        when(tokensPlaceHolder.getAccessToken()).thenReturn("Batata");
        MultivaluedMap<String, String> incomingHeaders = new MultivaluedHashMap<>();
        MultivaluedMap<String, String> outgoingHeaders = new MultivaluedHashMap<>();
        MultivaluedMap<String, String> result = target.update(incomingHeaders,outgoingHeaders);
        assertEquals("Bearer Batata",result.getFirst("Authorization"));
    }
}