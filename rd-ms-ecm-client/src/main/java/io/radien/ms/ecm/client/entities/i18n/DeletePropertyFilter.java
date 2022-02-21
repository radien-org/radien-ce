/*
 *
 *  * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
 *  * <p>
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  * <p>
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  * <p>
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package io.radien.ms.ecm.client.entities.i18n;

import io.radien.api.model.i18n.SystemI18NProperty;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class DeletePropertyFilter {
    private List<SystemI18NProperty> properties;
    private String application;

    public DeletePropertyFilter() {
    }

    public DeletePropertyFilter(List<SystemI18NProperty> properties) {
        if(properties == null || properties.isEmpty()) {
            throw new IllegalArgumentException("Value must not be null");
        }
        this.properties = properties;
    }

    public DeletePropertyFilter(String application) {
        if(StringUtils.isEmpty(application)) {
            throw new IllegalArgumentException("Value must not be null");
        }
        this.application = application;
    }

    public List<SystemI18NProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<SystemI18NProperty> properties) {
        if(!StringUtils.isEmpty(application)) {
            throw new IllegalStateException("Application must be null or empty");
        }
        this.properties = properties;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        if(properties != null && !properties.isEmpty()) {
            throw new IllegalStateException("Properties must be null or empty");
        }
        this.application = application;
    }
}
