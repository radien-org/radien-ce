/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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

package io.radien.ms.ecm.client.services;

import io.radien.ms.ecm.client.entities.I18NProperty;

import java.util.List;

/**
 * System I18N property service interface
 */
public interface SystemI18NPropertyService {

    /**
     * System I18N property localized message getter
     * @param messageCode to be found
     * @return the string value of the localized message
     */
    String getLocalizedMessage(String messageCode);

    /**
     * System I18N property save method
     * @param property to be saved
     * @return the I18N Property
     */
    I18NProperty save(I18NProperty property);

    /**
     * System I18N property list save methods
     * @param propertyList list of I18N property to be stored
     * @return the list of the saved I18N properties
     */
    List<I18NProperty> save(List<I18NProperty> propertyList);

    /**
     * System I18N property delete method
     * @param property to be deleted
     */
    void delete(I18NProperty property);

    /**
     * System I18N property get all key methods
     * @return a list of all the I18N keys
     */
    List<String> getKeys();

    /**
     * System I18N property get all the I18N properties method
     * @return a list of all the existent I18N properties
     */
    List<I18NProperty> getAll();

    /**
     * System I18N get object by key
     * @param key to be found
     * @return the requested I18N Property
     */
    I18NProperty getByKey(String key);

    /**
     * System I18N property initialization
     */
    void initializeProperties();
}
