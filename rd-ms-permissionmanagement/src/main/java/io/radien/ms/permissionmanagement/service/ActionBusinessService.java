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

package io.radien.ms.permissionmanagement.service;

import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemAction;
import io.radien.api.model.permission.SystemActionSearchFilter;
import io.radien.api.service.permission.ActionServiceAccess;
import io.radien.api.service.permission.exception.ActionNotFoundException;
import io.radien.exception.BadRequestException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.permissionmanagement.client.entities.Action;
import io.radien.ms.permissionmanagement.entities.ActionEntity;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.hsqldb.lib.StringUtil;

@Stateless
public class ActionBusinessService {
    @Inject
    private ActionServiceAccess actionService;

    /**
     * Looks for the Action corresponding to the actionId
     * @param actionId to look for
     * @return corresponding action
     * @throws ActionNotFoundException if action is not found
     */
    public SystemAction get(Long actionId) {
        SystemAction result = actionService.get(actionId);
        if(result == null) {
            throw new ActionNotFoundException(MessageFormat.format("No action found for id {0}", actionId));
        }
        return result;
    }

    /**
     * Retrieves actions matching with given ids
     * @param actionIds list of ids
     * @return list of actions matching given ids
     * @throws ActionNotFoundException if no actions match the given ids
     * @throws BadRequestException if input parameter is not valid
     */
    public List<SystemAction> get(List<Long> actionIds) {
        if(actionIds == null || actionIds.isEmpty()) {
            throw new BadRequestException("Mandatory parameter missing");
        }
        List<SystemAction> results = actionService.get(actionIds);
        if(results.isEmpty()) {
            throw new ActionNotFoundException("No actions found for the passed IDs");
        }
        return results;
    }

    /**
     * Returns a page of System Actions
     * @param search name filter for actions
     * @param pageNo page number
     * @param pageSize page size
     * @param sortBy sort parameters
     * @param isAscending ascending or descending order
     * @return page of System Actions matching the given parameters
     */
    public Page<SystemAction> getAll(String search, int pageNo, int pageSize,
                                     List<String> sortBy, boolean isAscending) {
        return actionService.getAll(search, pageNo, pageSize, sortBy, isAscending);
    }

    public List<SystemAction> getFiltered(SystemActionSearchFilter searchFilter) {
        return actionService.getActions(searchFilter);
    }

    /**
     * Persists given action into database
     * @param action value to be persisted
     * @throws BadRequestException if action name is missing or duplicate value already exists
     */
    public void create(Action action) {
        if(StringUtil.isEmpty(action.getName())) {
            throw new BadRequestException("Mandatory parameter missing");
        }
        try {
            actionService.create(new ActionEntity(action));
        } catch (UniquenessConstraintException e) {
            throw new BadRequestException("Duplicated action already exists");
        }
    }

    /**
     * Updates given action to datasource
     * @param id updated if for action
     * @param action action to be updated
     * @throws BadRequestException if action name is missing or duplicate value already exists
     */
    public void update(Long id, Action action) {
        if(id == null || action == null || action.getName() == null) {
            throw new BadRequestException("Mandatory parameter missing");
        }
        try {
            action.setId(id);
            actionService.update(action);
        } catch (UniquenessConstraintException e) {
            throw new BadRequestException("Duplicated action already exists");
        }
    }

    /**
     * Deletes action matching given id
     * @param actionId id to delete
     * @throws ActionNotFoundException if no action was found for given id
     */
    public void delete(Long actionId) {
        if(!actionService.delete(actionId)) {
            throw new ActionNotFoundException(MessageFormat.format("No action found for id {0}", actionId));
        }
    }

    /**
     * Deletes action matching given ids
     * @param actionIds ids to delete
     * @throws ActionNotFoundException if no action was found for given ids
     */
    public void delete(Collection<Long> actionIds) {
        if(!actionService.delete(actionIds)) {
            throw new ActionNotFoundException("No action found for id given id list");
        }
    }
}
