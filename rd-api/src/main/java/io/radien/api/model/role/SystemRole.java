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
package io.radien.api.model.role;

import io.radien.api.Model;

import java.util.Date;

/**
 * Class that represents and application Role
 *
 * @author Bruno Gama
 */
public interface SystemRole extends Model {

    /**
     * System Role name getter
     * @return the system role name
     */
    public String getName();

    /**
     * System Role name setter
     * @param name to be set
     */
    public void setName(String name);

    /**
     * System Role description getter
     * @return the system role description
     */
    public String getDescription();

    /**
     * System Role description setter
     * @param description to be set
     */
    public void setDescription(String description);

    /**
     * System Role termination date getter
     * @return the system role termination date
     */
    public Date getTerminationDate();

    /**
     * System role termination date setter
     * @param terminationDate to be set
     */
    public void setTerminationDate(Date terminationDate);
}
