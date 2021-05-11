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
package io.radien.ms.rolemanagement.services;

import io.radien.api.entity.Page;
import io.radien.api.model.linked.authorization.SystemLinkedAuthorization;
import io.radien.api.model.linked.authorization.SystemLinkedAuthorizationSearchFilter;
import io.radien.api.service.linked.authorization.LinkedAuthorizationServiceAccess;
import io.radien.exception.LinkedAuthorizationNotFoundException;
import io.radien.exception.UniquenessConstraintException;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

/**
 * Will request the service access to prepare the necessary responses for the certain requests
 *
 * @author Bruno Gama
 */
public class LinkedAuthorizationsService implements Serializable {

    private static final long serialVersionUID = 933659981003498804L;

    @Inject
    private LinkedAuthorizationServiceAccess linkedAuthorizationServiceAccess;

    /**
     * Gets all the Linked Authorization into a pagination mode.
     * @param pageNo of the requested information. Where the user is.
     * @param pageSize total number of pages returned in the request.
     * @return a page of system roles.
     */
    public Page<? extends SystemLinkedAuthorization> getAllAssociations(int pageNo, int pageSize){
        return linkedAuthorizationServiceAccess.getAll(pageNo, pageSize);
    }

    /**
     * Gets the System Linked Authorization searched by the PK (id).
     * @param id to be searched.
     * @return the system Linked Authorization requested to be found.
     * @throws LinkedAuthorizationNotFoundException if Linked Authorization can not be found will return NotFoundException
     */
    public SystemLinkedAuthorization get(long id) throws LinkedAuthorizationNotFoundException {
        return linkedAuthorizationServiceAccess.getAssociationById(id);
    }

    /**
     * Get Linked Authorizations by specific columns already given to be searched.
     * @param filter entity with available filters to search Linked Authorizations.
     * @return a list of Linked Authorizations
     */
    public List<? extends SystemLinkedAuthorization> getSpecificAssociation(SystemLinkedAuthorizationSearchFilter filter) {
        return linkedAuthorizationServiceAccess.getSpecificAssociation(filter);
    }

    /**
     * Saves or updates an record based on the given information.
     * @param association information to create/update.
     * @throws UniquenessConstraintException in case of update if any new given information already exists and
     * cannot be duplicated
     * @throws LinkedAuthorizationNotFoundException in case of update if we cannot find the specific linked authorization.
     */
    public void save(SystemLinkedAuthorization association) throws LinkedAuthorizationNotFoundException, UniquenessConstraintException {
        linkedAuthorizationServiceAccess.save(association);
    }

    /**
     * Deletes a linked authorization record by a given id.
     * @param association to be deleted
     */
    public void deleteAssociation(Long association) throws LinkedAuthorizationNotFoundException {
        linkedAuthorizationServiceAccess.deleteAssociation(association);
    }

}
