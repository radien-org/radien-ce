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
 * @author Newton Carvalho
 * Contract description for permissions search filter
 */
public interface SystemPermissionSearchFilter {

    void setName(String name);

    String getName();

    void setActionId(Long actionId);

    Long getActionId();

    void setResourceId(Long resourceId);

    Long getResourceId();

    boolean isExact();

    void setExact(boolean exact);

    boolean isLogicConjunction();

    void setLogicConjunction(boolean logicConjunction);
}
