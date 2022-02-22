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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import javax.ws.rs.InternalServerErrorException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.models.KeycloakSession;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doReturn;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SPIThemesResourceProvider.class, URL.class })
public class SPIThemesResourceProviderTest {
    private static final Logger log = LoggerFactory.getLogger(SPIThemesResourceProviderTest.class);

    @InjectMocks
    SPIThemesResourceProvider spiThemesResourceProvider;
    @Mock
    KeycloakSession keycloakSession;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        new SPIThemesResourceProvider(keycloakSession);
    }

    @Test
    public void testGetMessage() throws Exception {
        String content = "{\n" +
                "  \"title\": \"Title\"\n" +
                "}";
        InputStream inputStream = new ByteArrayInputStream(content.getBytes());
        HttpURLConnection urlConnection = PowerMockito.mock(HttpURLConnection.class);
        URL finalUrl = PowerMockito.mock(URL.class);


        PowerMockito.whenNew(URL.class).withArguments(anyString()).thenReturn(finalUrl);
        PowerMockito.when(finalUrl.openConnection()).thenReturn(urlConnection);
        PowerMockito.when(urlConnection.getInputStream()).thenReturn(inputStream);
        PowerMockito.when(urlConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);

        assertEquals(1,spiThemesResourceProvider.getMessages("",new Locale("en")).size());
    }

    @Test
    public void testGetMessageNoContent() throws Exception {
        String content = "{\n" +
                "  \"title\": \"Title\"\n" +
                "}";
        InputStream inputStream = new ByteArrayInputStream(content.getBytes());
        HttpURLConnection urlConnection = PowerMockito.mock(HttpURLConnection.class);
        URL finalUrl = PowerMockito.mock(URL.class);


        PowerMockito.whenNew(URL.class).withArguments(anyString()).thenReturn(finalUrl);
        PowerMockito.when(finalUrl.openConnection()).thenReturn(urlConnection);
        PowerMockito.when(urlConnection.getInputStream()).thenReturn(inputStream);
        PowerMockito.when(urlConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_NO_CONTENT);

        assertEquals(0,spiThemesResourceProvider.getMessages("",new Locale("en")).size());

    }

    @Test
    public void testGetTemplate(){
        assertNull(spiThemesResourceProvider.getTemplate( "theme/radien/login" ));
    }

    @Test
    public void testGetResourceAsStream(){
        assertNull(spiThemesResourceProvider.getResourceAsStream( "theme/radien/login" ));
    }

}