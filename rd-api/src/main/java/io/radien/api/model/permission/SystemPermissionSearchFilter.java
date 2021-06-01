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
package io.radien.api.model.permission;

/**
 * Contract description for permissions search filter
 *
 * @author Newton Carvalho
 */
public interface SystemPermissionSearchFilter {

    /**
     * System permission search filter name getter
     * @return the system permission search filter name
     */
    String getName();

    /**
     * System permission search filter name setter
     * @param name to be set
     */
    void setName(String name);

    /**
     * System permission search filter action id getter
     * @return the system permission search filter action id
     */
    Long getActionId();

    /**
     * System permission search filter action id setter
     * @param actionId to be set
     */
    void setActionId(Long actionId);

    /**
     * System permission search filter resource id getter
     * @return the system permission search filter resource id
     */
    Long getResourceId();

    /**
     * System permission search filter resource id setter
     * @param resourceId to be set
     */
    void setResourceId(Long resourceId);

    /**
     * System permission search filter is exact search getter
     * @return the system permission search filter is exact value
     */
    boolean isExact();

    /**
     * System permission search filter is exact setter
     * @param exact if true the search needs to be exactly as the given parameters
     */
    void setExact(boolean exact);

    /**
     * System Permission search filter is logical conjunction getter
     * @return the logical conjunction value if true is an and if false is a or
     */
    boolean isLogicConjunction();

    /**
     * System Permission search filter logical conjunction setter
     * @param logicConjunction the logical conjunction value if true is an and if false is a or
     */
    void setLogicConjunction(boolean logicConjunction);
}
