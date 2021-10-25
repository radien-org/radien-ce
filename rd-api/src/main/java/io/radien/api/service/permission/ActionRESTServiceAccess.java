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

import io.radien.api.Appframeable;
import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemAction;
import io.radien.exception.SystemException;
import java.util.List;
import java.util.Optional;

/**
 * Contract for Rest Service Client regarding Action domain object
 * @author Newton Carvalho
 */
public interface ActionRESTServiceAccess extends Appframeable {

    /**
     * Fetches all Actions
     * @param search field value to be searched or looked up
     * @param pageNo initial page number
     * @param pageSize max page size
     * @param sortBy sort by filter fields
     * @param isAscending ascending result list or descending
     * @return List of existent Actions
     * @throws SystemException in any other kind of system issue
     */
    public Page<? extends SystemAction> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) throws SystemException;

    /**
     * Retrieves a Action by Id
     * @param id of the system action to be search
     * @return a optional list that might come empty or filled with system action that has the requested id
     * @throws SystemException if there is any error in the token or any other field
     */
    public Optional<SystemAction> getActionById(Long id) throws SystemException;

    /**
     * Retrieves a Action by Name
     * @param name to be searched for
     * @return a optional list that might come empty or filled with system action that has the requested name
     * @throws SystemException if there is any error in the token or any other field
     */
    public Optional<SystemAction> getActionByName(String name) throws SystemException;

    /**
     * Creates given action
     * @param action to be created
     * @return true if action has been created with success or false if not
     * @throws SystemException in case of token expiration or any issue on the application
     */
    public boolean create(SystemAction action) throws SystemException;

    /**
     * Update given action
     * @param action to be updated
     * @return true if action has been updated with success or false if not
     * @throws SystemException in case of token expiration or any issue on the application
     */
    public boolean update(SystemAction action) throws SystemException;

    /**
     * Deletes given action
     * @param actionId to be deleted
     * @return true if action has been deleted with success or false if not
     * @throws SystemException in case of URL specification
     */
    public boolean delete(long actionId) throws SystemException;

    /**
     * Retrieves from DB a collection containing actions. The retrieval process will be
     * based on a list containing identifiers
     * @param ids list containing action identifiers
     * @return a list of actions found using the informed identifiers
     * @throws SystemException in case of any found error
     */
    public List<? extends SystemAction> getActionsByIds(List<Long> ids) throws SystemException;

}
