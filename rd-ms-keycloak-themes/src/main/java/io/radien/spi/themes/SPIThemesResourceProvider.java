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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import org.keycloak.models.KeycloakSession;
import org.keycloak.theme.ThemeResourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static kong.unirest.HeaderNames.ACCEPT_LANGUAGE;
import static kong.unirest.HeaderNames.CONTENT_TYPE;

public class SPIThemesResourceProvider implements ThemeResourceProvider {
    private static final Logger log = LoggerFactory.getLogger(SPIThemesResourceProvider.class);
    private final String themeRoot;
    private final String resourceRoot;

    private final ClassLoader classLoader;
    private final KeycloakSession keycloakSession;
    private final Properties properties;

    public SPIThemesResourceProvider(KeycloakSession keycloakSession) {
        log.info("Starts: Theme Resource initialization");
        this.keycloakSession = keycloakSession;
        this.classLoader = getClass().getClassLoader();
        this.properties = new Properties();
        this.themeRoot = SPIPropertiesProvider.SPI_ROOT_THEME.getDefaultValue() + keycloakSession.theme() + "/";
        if(log.isInfoEnabled()){
            log.info(String.format("get Theme type: %s", themeRoot));
        }
        this.resourceRoot = themeRoot + "resources/";
    }

    @Override
    public URL getTemplate(String templateName) {
        if(log.isInfoEnabled()){
            log.info(String.format("get template: %s", this.themeRoot + templateName.toLowerCase()));
        }
        return this.classLoader.getResource(this.themeRoot + templateName.toLowerCase() + "/");
    }

    @Override
    public InputStream getResourceAsStream(String resource) {
        String resourceName = String.format("get resource: %s", resourceRoot+resource);
        log.info(resourceName);
        return classLoader.getResourceAsStream(resourceRoot + resource);
    }

    @Override
    public Properties getMessages(String baseBundleName, Locale locale) throws IOException {
        String messageInfo = String.format("Get Messages for %s and %s!", baseBundleName, locale.toLanguageTag());
        log.info(messageInfo);
        String uri = SPIPropertiesProvider.CMS_API_MESSAGES.getDefaultValue();
        URL url = new URL(uri);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty(CONTENT_TYPE, "application/x-www-form-urlencoded");
        con.setRequestProperty(ACCEPT_LANGUAGE, locale.toLanguageTag());
        try{
            int status = con.getResponseCode();
            if(status == 200) {
                String result = readFullyAsString(con.getInputStream(), "UTF-8");
                ObjectMapper mapper = new ObjectMapper();
                Map<String, String> map = mapper.readValue(result, Map.class);
                properties.putAll(map);
            }
        }catch (Exception e){
            log.error("Error in establishing HttpURLConnection:: {}", e.getMessage());
        }
        return properties;
    }

    public String readFullyAsString(InputStream inputStream, String encoding) throws IOException {
        return readFully(inputStream).toString(encoding);
    }

    private ByteArrayOutputStream readFully(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos;
    }

    @Override
    public void close() {
        // Calls when server shut down
    }

}
