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
package io.radien.api.service.linked.authorization;

import io.radien.api.Appframeable;
import io.radien.api.model.linked.authorization.SystemLinkedAuthorization;
import io.radien.exception.SystemException;

import java.net.MalformedURLException;
import java.util.List;

/**
 * Linked authorization rest service access interface for requests
 *
 * @author Bruno Gama
 */
public interface LinkedAuthorizationRESTServiceAccess extends Appframeable {

    /**
     * Calls the requester expecting to receive all the existent linked authorizations,
     * if not possible will refresh the access token and retry
     * @param pageNo of the information to be seen
     * @param pageSize number of records to be showed
     * @return a list of System Linked Authorizations
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    public List<? extends SystemLinkedAuthorization> getAll(int pageNo, int pageSize) throws SystemException;

    /**
     * Check if a LinkedAuthorization exists for Tenant, User, Permission or Role.
     * @param tenant Tenant identifier
     * @param permission Permission identifier
     * @param role Role identifier
     * @param userId User identifier
     * @return true (if exists some LinkedAuthorization attending the criteria), otherwise false
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    boolean checkIfLinkedAuthorizationExists(Long tenant, Long permission, Long role, Long userId) throws SystemException;

    /**
     * Requests find of linked authorization by role id
     * @param roleId to be found
     * @return a list of system linked authorizations
     * @throws SystemException in case of any issue
     */
    public List<? extends SystemLinkedAuthorization> getLinkedAuthorizationByRoleId(Long roleId) throws SystemException;

    /**
     * Creates given linked authorization
     * @param linkedAuthorization to be created
     * @return true if linked authorization has been created with success or false if not
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    public boolean create(SystemLinkedAuthorization linkedAuthorization) throws SystemException;

    /**
     * Will calculate how many records are existent in the db
     * @return the count of existent linked authorizations.
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    public Long getTotalRecordsCount() throws SystemException;

    /**
     * Check if the informed user has a role (Optionally - under a specific tenant)
     * @param userId corresponds to the user identifier
     * @param tenantId corresponds to a tenant identifier (Optional parameter)
     * @param roleName corresponds to the role name
     * @return true if the specified role does exist for the specific user
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    Boolean isRoleExistentForUser(Long userId, Long tenantId, String roleName) throws SystemException;

    /**
     * Requests find of linked authorization by user id
     * @param userId to be found
     * @return a list of system linked authorizations
     * @throws SystemException in case of any issue
     */
    List<? extends SystemLinkedAuthorization> getSpecificAssociationByUserId(Long userId) throws SystemException;

    /**
     * Will request delete ALL Linked Authorizations that exist in the DB for the following
     * parameters (tenant and user).
     * @param tenantId Tenant identifier
     * @param userId User identifier
     * @return SystemException in case of error
     */
    boolean deleteAssociations(Long tenantId, Long userId) throws SystemException;
}
