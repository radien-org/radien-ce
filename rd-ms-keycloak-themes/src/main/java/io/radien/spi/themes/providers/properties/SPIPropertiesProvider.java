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
package io.radien.spi.themes.providers.properties;

/**
 * Implements enum constants for the
 * SPIPropertiesProvider
 *
 * @author Rajesh Gavvala
 */
public enum SPIPropertiesProvider implements SPIProperties {
    CMS_API_MESSAGES("cms.api.messages", "http://host.docker.internal:8083/cms/i18n/messages"),
    SPI_ROOT_THEME("spi.root.theme", "theme/");

    private final String propertyKey;
    private final String defaultValue;

    /**
     * Constructs SPIPropertiesProvider object
     * @param propertyKey to be called
     * @param defaultValue to be called
     */
    SPIPropertiesProvider(String propertyKey, String defaultValue) {
        this.propertyKey = propertyKey;
        this.defaultValue = defaultValue;
    }

    /**
     * Gets the requested property key
     * @return the property key value as a string
     */
    @Override
    public String getPropertyKey() {
        return propertyKey;
    }

    /**
     * Gets the default value of requested property key
     * @return the property key value as a string
     */
    @Override
    public String getDefaultValue() {
        return defaultValue;
    }
}
