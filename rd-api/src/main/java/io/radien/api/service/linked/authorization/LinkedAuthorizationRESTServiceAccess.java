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

import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.List;

/**
 * @author Bruno Gama
 */
public interface LinkedAuthorizationRESTServiceAccess extends Appframeable {

    /**
     * Fetches all Linked Authorizations
     * @return List of Linked Authorizations
     */
    public List<? extends SystemLinkedAuthorization> getAll(int pageNo, int pageSize) throws MalformedURLException, ParseException;

    /**
     * Check if a LinkedAuthorization exists for Tenant, User, Permission or Role.
     * @param tenant Tenant identifier
     * @param permission Permission identifier
     * @param role Role identifier
     * @param userId User identifier
     * @return true (if exists some LinkedAuthorization attending the criteria), otherwise false
     */
    boolean checkIfLinkedAuthorizationExists(Long tenant, Long permission, Long role, Long userId);

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
     * @throws MalformedURLException in case of URL specification
     */
    public boolean create(SystemLinkedAuthorization linkedAuthorization) throws SystemException;

    /**
     * Will calculate how many records are existent in the db
     * @return the count of existent linked authorizations.
     */
    public Long getTotalRecordsCount() throws SystemException;

    /**
     * Retrieve all roles related to a specific user.
     * Optionally tenant identifier may be informed to
     * improve the filtering process.
     * @param userId User identifier
     * @param tenantId Tenant Identifier
     */
    public List<String> getRoles(Long userId, Long tenantId) throws SystemException;

    Boolean isRoleExistentForUser(Long userId, Long tenantId, String roleName) throws SystemException;
}
