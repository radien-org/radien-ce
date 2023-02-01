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
package io.radien.api.service.user;

import io.radien.api.model.user.SystemUserPasswordChanging;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import io.radien.api.Appframeable;
import io.radien.api.entity.Page;
import io.radien.api.model.user.SystemUser;
import io.radien.exception.SystemException;

/**
 * User REST Service Access interface for future requests
 *
 * @author Marco Weiland
 */
public interface UserRESTServiceAccess extends Appframeable{

    /**
     * Gets the requested user searching for his subject
     * @param sub to be searched
     * @return a optional of system user if found
     * @throws SystemException in case of token expiration or any issue on the application
     */
	public Optional<SystemUser> getUserBySub(String sub) throws SystemException;

    /**
     * Gets the requested user searching for his logon
     * @param logon to be searched
     * @return a optional of system user if found
     * @throws SystemException in case of token expiration or any issue on the application
     */
	public Optional<SystemUser> getUserByLogon(String logon) throws SystemException;

    /**
     * Gets the requested user searching for his id
     * @param id to be searched
     * @return a optional of system user if found
     * @throws SystemException in case of token expiration or any issue on the application
     */
	public Optional<SystemUser> getUserById(Long id) throws SystemException;

    /**
     * Gets the requested users searching for a list of ids
     * @param ids to be searched
     * @return a list containing system users
     * @throws SystemException in case of token expiration or any issue on the application
     */
    public List<? extends SystemUser> getUsersByIds(Collection<Long> ids) throws SystemException;

    /**
     * Creates given user
     * @param user to be created
     * @return true if user has been created with success or false if not
     * @throws SystemException in case of token expiration or any issue on the application
     */
    Optional<SystemUser> getCurrentUserInSession() throws SystemException;

    /**
     * Creates given user
     * @param user to be created
     * @param skipKeycloak boolean value that will indicate if creation should also be performed in the keycloak
     *                     idp or not
     * @return true if user has been created with success or false if not
     * @throws SystemException in case of token expiration or any issue on the application
     */
    public boolean create(SystemUser user,boolean skipKeycloak) throws SystemException;

    /**
     * Returns all the existent System Users into a pagination format
     * @param search in case there should only be returned a specific type of users
     * @param pageNo where the user currently is
     * @param pageSize number of records to be show by page
     * @param sortBy any specific column
     * @param isAscending true in case records should be filter in ascending order
     * @return a page of all the requested system users
     * @throws MalformedURLException in case of any issue while attempting communication with the client side
     */
    public Page<? extends SystemUser> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) throws MalformedURLException;

    /**
     * Send the update password email to the active/requested user
     * @param id user id to be validated his information and the email sent
     * @return true in case of success
     */
    public boolean sendUpdatePasswordEmail(long id);

    /**
     * Updates user email and sends email for verification
     * @param userId to be set
     * @param user object contains an email
     * @return true in case of success
     * @throws SystemException in case of token expiration or any issue on the application
     */
    public boolean updateEmailAndExecuteActionEmailVerify(long userId, SystemUser user, boolean emailVerify) throws SystemException;

    /**
     * Deletes the requested user from the db
     * @param id of the user to be deleted
     * @return true in case of deletion
     */
    public boolean deleteUser(long id);

    /**
     * Updates the given user information, will validate by the given user id since that one cannot change
     * @param user information to be updated
     * @return true in case of success
     */
    public boolean updateUser(SystemUser user);

    /**
     * Updates the active token for the active user session
     * @return true in case of success
     * @throws SystemException in case of token expiration or any issue on the application
     */
    public boolean refreshToken() throws SystemException;

    /**
     * Changes user password
     * @param sub OpenId user identifier (subject)
     * @param change pojo/bean containing credential information (Not plain text, data encoded on base64)
     * @return true if changing process is concluded with success.
     * @throws SystemException in case of any issue regarding communication with User endpoint
     */
    boolean updatePassword(String sub, SystemUserPasswordChanging change) throws SystemException;

    /**
    * Checks if the user is locked
     * @param id of the user to be deleted
     * @return true if user is locked
     * @throws SystemException
     */
    boolean isProcessingLocked(long id) throws SystemException;
}
