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
package io.radien.api.service.permission;

import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemAction;
import io.radien.api.model.permission.SystemActionSearchFilter;
import io.radien.api.service.ServiceAccess;
import io.radien.exception.UniquenessConstraintException;

import java.util.Collection;
import java.util.List;

/**
 * Contract description for the Data Service responsible for handle Actions (CRUD)
 *
 * @author Newton Carvalho
 */
public interface ActionServiceAccess extends ServiceAccess {

    /**
     * Retrieve an Action by an identifier
     * @param actionId action identifier
     * @return the requested system action
     */
    SystemAction get(Long actionId);

    /**
     * Retrieves a collection of Actions by its identifiers
     * @param actionId list of identifiers
     * @return a list of system actions
     */
    List<SystemAction> get(List<Long> actionId);

    /**
     * Retrieves actions using pagination approach
     * @param search values to be search
     * @param pageNo Page number
     * @param pageSize Page size
     * @param sortBy Sorting fields
     * @param isAscending Defines if ascending or descending in relation of sorting fields
     * @return a pagination of system actions
     */
    Page<SystemAction> getAll(String search, int pageNo, int pageSize,
                              List<String> sortBy, boolean isAscending);

    /**
     * Save an action (Create or Update)
     * @param action to be stored/saved
     * @throws UniquenessConstraintException in case the required action already exists or has duplicated information
     */
    void create(SystemAction action) throws UniquenessConstraintException;

    /**
     * Update an action (Create or Update)
     * @param action to be stored/saved
     * @throws UniquenessConstraintException in case the required action already exists or has duplicated information
     */
    void update(SystemAction action) throws UniquenessConstraintException;

    /**
     * Delete an action
     * @param actionId action identifier
     */
    boolean delete(Long actionId);

    /**
     * Deletes a set of actions
     * @param actionIds action identifiers
     */
    boolean delete(Collection<Long> actionIds);

    /**
     * Retrieve Actions using a search filter
     * @param filter with specific fields to find the correct action
     * @return a list of system actions
     */
    List<SystemAction> getActions(SystemActionSearchFilter filter);

}
