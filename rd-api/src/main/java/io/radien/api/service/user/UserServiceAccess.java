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

import io.radien.exception.SystemException;
import java.util.Collection;
import java.util.List;

import io.radien.api.entity.Page;
import io.radien.api.model.user.SystemUser;
import io.radien.api.model.user.SystemUserSearchFilter;
import io.radien.api.service.ServiceAccess;
import io.radien.api.service.batch.BatchSummary;
import io.radien.exception.UniquenessConstraintException;
import io.radien.exception.UserNotFoundException;

/**
 * User Service access interface class and all the possible requests
 *
 * @author Marco Weiland
 */
public interface UserServiceAccess extends ServiceAccess {

    /**
     * Requests the user id based on the received user subject
     * @param userSub to be found
     * @return the user id
     */
    Long getUserId(String userSub);

    /**
     * Requests the user information based on the user id
     * @param userId to be found
     * @return the user object
     * @throws UserNotFoundException in case no user is found with the given id
     */
    public SystemUser get(Long userId);

    /**
     * Requests a list of users based on a list of user id's
     * @param userId to be found
     * @return a list of users
     */
    public List<SystemUser> get(List<Long> userId);

    /**
     * Returns all the requested and existent users in the db into a pagination mode
     * @param search field to be looked up for
     * @param pageNo where te user is
     * @param pageSize number of records per page
     * @param sortBy any type of column or field
     * @param isAscending if in case of true the records will come in ascending sorted
     * @return a page of requested users
     */
    public Page<SystemUser> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending);

    /**
     * CREATE a User association
     * @param user information to be created
     * @throws UniquenessConstraintException in case of duplicated fields or records
     */
    void create(SystemUser user) throws UniquenessConstraintException;

    /**
     * UPDATE a User association
     * @param user to be updated
     * @throws UniquenessConstraintException in case of duplicated fields or records
     * @throws UserNotFoundException in case of not existing a User for an id
     */
    void update(SystemUser user) throws UniquenessConstraintException, SystemException;

    /**
     * Deletes a requested user based on the received id
     * @param userId to be deleted
     */
    public void delete(Long userId);

    /**
     * Deletes a collection of users based on the received collection of ids
     * @param userIds to be deleted
     */
    public void delete(Collection<Long> userIds);

    /**
     * Gets a list of users based on a filtered information
     * @param filter with the information for the records to be returned
     * @return a list of matching criteria users
     */
    public List<? extends SystemUser> getUsers(SystemUserSearchFilter filter);

    /**
     * Gets all the users from the db into a list
     * @return a list of all the existent users
     */
    public List<? extends SystemUser> getUserList();

    /**
     * Batch creation method, will delete all the received users from the db
     * @param users list of users to be deleted
     * @return a batch summary with a report saying which records have been or not been deleted
     */
    public BatchSummary create(List<? extends SystemUser> users);

    Long count();
}
