/*
 * Copyright (c) 2016-present radien.io & its legal owners. All rights reserved.
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
import io.radien.exception.ActionNotFoundException;
import io.radien.exception.UniquenessConstraintException;

import java.util.Collection;
import java.util.List;

/**
 * @author Newton Carvalho
 * Contract description for the Data Service responsible for handle Actions (CRUD)
 */
public interface ActionServiceAccess extends ServiceAccess {

    /**
     * Retrieve an Action by an identifier
     * @param actionId action identifier
     * @return
     */
    SystemAction get(Long actionId);

    /**
     * Retrieves a collection of Actions by its identifiers
     * @param actionId list of identifiers
     * @return
     */
    List<SystemAction> get(List<Long> actionId);

    /**
     * Retrieves actions using pagination approach
     * @param search
     * @param pageNo Page number
     * @param pageSize Page size
     * @param sortBy Sorting fields
     * @param isAscending Defines if ascending or descending in relation of sorting fields
     * @return
     */
    Page<SystemAction> getAll(String search, int pageNo, int pageSize,
                              List<String> sortBy, boolean isAscending);

    /**
     * Save an action (Create or Update)
     * @param action
     * @throws UniquenessConstraintException
     */
    void save(SystemAction action) throws UniquenessConstraintException;

    /**
     * Delete an action
     * @param actionId action identifier
     */
    void delete(Long actionId);

    /**
     * Deletes a set of actions
     * @param actionIds action identifiers
     */
    void delete(Collection<Long> actionIds);

    /**
     * Retrieve Actions using a search filter
     * @param filter
     * @return
     */
    List<? extends SystemAction> getActions(SystemActionSearchFilter filter);

}
