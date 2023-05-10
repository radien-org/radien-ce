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

import io.radien.spi.themes.exception.InvalidResponseException;
import io.radien.spi.themes.providers.themes.SPIThemesResourceProvider;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import junit.framework.TestCase;
import org.keycloak.models.KeycloakSession;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SPIThemesResourceProviderTest extends TestCase {

    private SPIThemesResourceProvider target;

    public void setUp() throws Exception {
        KeycloakSession mockSession = mock(KeycloakSession.class);
        target = new SPIThemesResourceProvider(mockSession);
    }

    public void testGetTemplate() {
        assertNull(target.getTemplate( "theme/radien/login" ));
    }

    public void testGetResourceAsStream() {
        assertNull(target.getResourceAsStream( "theme/radien/login" ));
    }

    public void testGetMessageIOException() throws IOException {
        Properties result = target.getMessages("", Locale.ENGLISH);
        assertEquals(0, result.size());
    }

    public void testRetrievePropertiesFromRemote() throws IOException, InvalidResponseException {
        String content = "{\n" +
                "  \"title\": \"Title\"\n" +
                "}";
        InputStream inputStream = new ByteArrayInputStream(content.getBytes());
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        when(mockConnection.getResponseCode()).thenReturn(200);
        when(mockConnection.getInputStream()).thenReturn(inputStream);
        Map<String, String> resultMap = target.retrievePropertiesFromRemote(mockConnection, Locale.ENGLISH);
        assertTrue(resultMap.containsKey("title"));

    }

    public void testRetrievePropertiesFromRemoteError() throws IOException, InvalidResponseException {
        String error = "Sample error message";
        InputStream inputStream = new ByteArrayInputStream(error.getBytes());
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        when(mockConnection.getResponseCode()).thenReturn(500);
        when(mockConnection.getErrorStream()).thenReturn(inputStream);
        assertThrows(InvalidResponseException.class,
                () -> target.retrievePropertiesFromRemote(mockConnection, Locale.ENGLISH));
    }

    public void testReadFullyAsString() throws IOException {
        String content = "{\n" +
                "  \"title\": \"Title\"\n" +
                "}";
        InputStream inputStream = new ByteArrayInputStream(content.getBytes());
        String result = target.readFullyAsString(inputStream, "UTF-8");
        assertTrue(result.contains("Title"));
    }
}