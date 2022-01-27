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

package io.radien.ms.ecm.client.entities;

import org.apache.commons.lang3.StringUtils;

public class DeleteContentFilter {
    private String viewId;
    private String language;
    private String absoluteJcrPath;

    public DeleteContentFilter() {
    }

    public DeleteContentFilter(String viewId, String language) {
        if(StringUtils.isEmpty(viewId) || StringUtils.isEmpty(language)) {
            throw new IllegalArgumentException("Values must not be null");
        }
        this.viewId = viewId;
        this.language = language;
    }

    public DeleteContentFilter(String absoluteJcrPath) {
        if(StringUtils.isEmpty(absoluteJcrPath)) {
            throw new IllegalArgumentException("Value must not be null");
        }
        this.absoluteJcrPath = absoluteJcrPath;
    }

    public String getViewId() {
        return viewId;
    }

    public void setViewId(String viewId) {
        if(!StringUtils.isEmpty(absoluteJcrPath)) {
            throw new IllegalStateException("AbsoluteJcrPath must be null or empty");
        }
        this.viewId = viewId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        if(!StringUtils.isEmpty(absoluteJcrPath)) {
            throw new IllegalStateException("AbsoluteJcrPath must be null or empty");
        }
        this.language = language;
    }

    public String getAbsoluteJcrPath() {
        return absoluteJcrPath;
    }

    public void setAbsoluteJcrPath(String absoluteJcrPath) {
        if(!StringUtils.isEmpty(viewId) || !StringUtils.isEmpty(language)) {
            throw new IllegalStateException("ViewID and Language must be null or empty");
        }
        this.absoluteJcrPath = absoluteJcrPath;
    }
}
