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
package io.radien.api.model.permission;

import io.radien.api.Model;

/**
 * Contract description for Action
 *
 * @author Newton Carvalho
 */
public interface SystemAction extends Model {

    /**
     * System Action getter id
     * @return the system action id
     */
    Long getId();

    /**
     * System Action setter id
     * @param id to be set
     */
    void setId(Long id);

    /**
     * System Action getter name
     * @return the system action name
     */
    String getName();

    /**
     * System Action setter name
     * @param name to be set
     */
    void setName(String name);
}
