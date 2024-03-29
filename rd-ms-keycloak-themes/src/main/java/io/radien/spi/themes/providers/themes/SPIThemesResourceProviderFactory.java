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
package io.radien.spi.themes.providers.themes;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.theme.ThemeResourceProvider;
import org.keycloak.theme.ThemeResourceProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SPIThemesResourceProviderFactory implements ThemeResourceProviderFactory {
    private static final String PROVIDER_ID = "custom-theme-resource-provider";

    @Override
    public ThemeResourceProvider create(KeycloakSession keycloakSession) {
        return new SPIThemesResourceProvider(keycloakSession);
    }

    @Override
    public void init(Config.Scope scope) {
        //reads configs from the server
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
        // Post factory calls
    }

    @Override
    public void close() {
        // Calls when server shut down
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
