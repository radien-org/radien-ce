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
import java.net.URL;
import java.util.Locale;
import java.util.Properties;
import org.junit.Before;
import org.junit.Test;
import org.keycloak.models.KeycloakSession;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Test suit
 * SPIThemesResourceProvider
 */
public class SPIThemesResourceProviderTest {
    private static final Logger log = LoggerFactory.getLogger(SPIThemesResourceProviderTest.class);

    @InjectMocks
    @Spy
    SPIThemesResourceProvider spiThemesResourceProvider;
    @Mock
    KeycloakSession keycloakSession;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        spiThemesResourceProvider = new SPIThemesResourceProvider(keycloakSession);
    }

    @Test
    public void testGetMessage() throws Exception {
        int responseCode = urlOpenConnection(SPIPropertiesProvider.CMS_API_MESSAGES.getDefaultValue());
        if(responseCode != 200){
            assertEquals(new Properties(), spiThemesResourceProvider.getMessages("", new Locale("en" )));
        }

        if(responseCode == 200){
            spiThemesResourceProvider.getMessages("", new Locale("en" ));
        }

    }

    @Test
    public void testGetTemplate(){
        assertNull(spiThemesResourceProvider.getTemplate( "theme/radien/login" ));
    }

    @Test
    public void testGetResourceAsStream(){
        assertNull(spiThemesResourceProvider.getResourceAsStream( "theme/radien/login" ));
    }

    private int urlOpenConnection(String urlToValidate) {
        HttpURLConnection httpURLConnection = null;
        int responseCode = 0;

        try {
            URL url = new URL(urlToValidate);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            responseCode = httpURLConnection.getResponseCode();
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        return responseCode;
    }


}