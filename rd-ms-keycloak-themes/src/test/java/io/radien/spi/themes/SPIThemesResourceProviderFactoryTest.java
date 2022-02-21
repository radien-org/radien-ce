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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.models.KeycloakSession;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test suit
 * SPIThemesResourceProviderFactory
 */
class SPIThemesResourceProviderFactoryTest {
    SPIThemesResourceProviderFactory spiThemesResourceProviderFactory;
    @Mock
    SPIThemesResourceProvider spiThemesResourceProvider;
    @Mock
    KeycloakSession keycloakSession;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.initMocks(this);
        spiThemesResourceProviderFactory = new SPIThemesResourceProviderFactory();
    }

    @Test
    void testCreate() {
        Assertions.assertNotEquals(spiThemesResourceProvider, spiThemesResourceProviderFactory.create(keycloakSession));
    }

    @Test
    void testGetId() {
        Assertions.assertEquals("", spiThemesResourceProviderFactory.getId());
    }
}