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
package io.radien.spi.themes;

import java.net.URL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.keycloak.models.KeycloakSession;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test suit
 * SPIThemesResourceProvider
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ URL.class })
class SPIThemesResourceProviderTest {
    @InjectMocks
    SPIThemesResourceProvider spiThemesResourceProvider;
    @Mock
    KeycloakSession keycloakSession;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        new SPIThemesResourceProvider(keycloakSession);
    }

    @Test
    void testGetTemplate(){
        assertNull(spiThemesResourceProvider.getTemplate("theme/test/login"));
    }

    @Test
    void testGetResourceAsStream(){
        assertNull(spiThemesResourceProvider.getResourceAsStream("theme/test/login"));
    }

}