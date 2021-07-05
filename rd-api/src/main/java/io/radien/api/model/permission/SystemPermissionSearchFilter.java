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

import io.radien.api.search.SystemSearchableByIds;

/**
 * Contract description for permissions search filter
 *
 * @author Newton Carvalho
 */
public interface SystemPermissionSearchFilter extends SystemSearchableByIds {

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
}
