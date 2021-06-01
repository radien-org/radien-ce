/*
 * Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.
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
package io.radien.api.service.ecm.model;

/**
 * System Content Version interface implementation class
 * @author André Sousa <a.sousa@radien.io>
 */
public interface SystemContentVersion extends Comparable<SystemContentVersion> {

    /**
     * System Content Version version getter
     * @return system content version version
     */
    String getVersion();

    /**
     * System Content Version major version getter
     * @return system content version major version
     */
    Long getMajorVersion();

    /**
     * System Content Version major version setter
     * @param majorVersion System Content Version major version
     */
    void setMajorVersion(Long majorVersion);

    /**
     * System Content Version minor version getter
     * @return system content version minor version
     */
    Long getMinorVersion();

    /**
     * System Content Version minor version setter
     * @param minorVersion System Content Version minor version
     */
    void setMinorVersion(Long minorVersion);

    /**
     * System Content Version hot fix version getter
     * @return system content version hot fix version
     */
    Long getHotfixVersion();

    /**
     * System Content Version hot fix version getter
     * @param hotfixVersion System Content Version hot fix version
     */
    void setHotfixVersion(Long hotfixVersion);
}
