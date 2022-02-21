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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.models.KeycloakSession;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suit
 * SPIThemesResourceProvider
 */
class SPIThemesResourceProviderTest {

    @InjectMocks
    @Spy
    SPIThemesResourceProvider spiThemesResourceProvider;
    @Mock
    KeycloakSession keycloakSession;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        spiThemesResourceProvider = new SPIThemesResourceProvider(keycloakSession);
    }

    @Test
    void testGetMessage() throws Exception {
        class UrlWrapper {

            URL url;

            public UrlWrapper(String spec) throws MalformedURLException {
                url = new URL(spec);
            }

            public URLConnection openConnection() throws IOException {
                return url.openConnection();
            }
        }

        UrlWrapper url = Mockito.mock(UrlWrapper.class);
        HttpURLConnection huc = Mockito.mock(HttpURLConnection.class);
        PowerMockito.when(url.openConnection()).thenReturn(huc);
        assertTrue(url.openConnection() instanceof HttpURLConnection);


        spiThemesResourceProvider.getMessages("messages",new Locale("en") );

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