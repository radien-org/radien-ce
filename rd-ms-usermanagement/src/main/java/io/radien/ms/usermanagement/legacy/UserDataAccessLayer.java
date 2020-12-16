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
package io.radien.ms.usermanagement.legacy;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Interface that defines the user available operations
 *
 *
 * @author Bruno Gama
 */

public interface UserDataAccessLayer extends DataAccessLayer {

	/**
	 * Returns a SystemUser object by the given String identifier (normally
	 * one of the possible configured logon ids)
	 *
	 * @param loginId
	 *                  the user identifier
	 * @return a {@link SystemUser} object
	 */
	SystemUser getUserByLoginId(String loginId) throws UserNotFoundException;

	/**
	 * Returns a SystemUser object by the given String identifier (normally
	 * his/her email address)
	 *
	 * @param logon
	 *                  the user identifier
	 * @return a {@link SystemUser} object
	 */
	Optional<SystemUser> getUserByLogon(String logon);

	/**
	 * Returns a SystemUser object by the given String identifier (normally
	 * his/her email address)
	 *
	 * @param email
	 *                  the user identifier
	 * @return a {@link SystemUser} object
	 */
	Optional<SystemUser> getUserByEmail(String email) ;

	/**
	 * Returns a SystemUser object by the given String identifier (normally
	 * his/her email address)
	 *
	 * @param logonOrEmail
	 *                  the user email or logon
	 * @return a {@link SystemUser} object
	 */
	SystemUser getUserByLogonOrEmail(String logonOrEmail) throws UserNotFoundException;

	/**
	 * Returns a SystemUser object by the given String identifier (normally
	 * his/her email address)
	 *
	 * @param email the user identifier
	 * @param enabled boolean representing if user is enable
	 *
	 * @return a {@link SystemUser} object
	 */
	SystemUser getUserByEmailAndEnabled(String email,boolean enabled) throws UserNotFoundException;

	/**
	 * Persists the given SystemUser object
	 *
	 * @param user
	 *                 {@link SystemUser} the jpa entity to persist
	 */
	void save(SystemUser user);

	/**
	 * Removes the given SystemUser object from de Database
	 *
	 * @param user
	 *                 {@link SystemUser} the jpa entity to persist
	 */
	void delete(SystemUser user);

	/**
	 * Removes the given SystemUser objects from de Database
	 *
	 * @param user
	 *                 {@link List<SystemUser>} the jpa entity to persist
	 */
	void delete(Collection<SystemUser> user);
	/**
	 * Returns a SystemUser object by its DB PK
	 *
	 * @param id
	 *               Long with the PK identifier
	 * @return {@link SystemUser} the jpa entity from the DB
	 */
	SystemUser get(Long id);

	/**
	 * Returns a List of SystemUser object by its DB PK
	 *
	 * @param ids
	 *               List of Long with the PK identifier
	 * @return {@link SystemUser} the jpa entity from the DB
	 */
	List<SystemUser> get(List<Long> ids);

	/**
	 * Returns a List of All Users
	 *
	 * @return {@link SystemUser} the jpa entity from the DB
	 */
	List<SystemUser> getAll();

	/**
	 * Retuns a list of users that can be paginated to perform lazy loading of
	 * data on application datatables
	 *
	 * @param pageSize
	 *                       the number of objects to return per page
	 * @param pageNumber
	 *                       the page number (where the query starts)
	 * @param query
	 *                       the query that will be executed in the database
	 * @return {@link SystemUser} likt with pagination
	 */
	List<SystemUser> get(int pageSize, int pageNumber, String query);

	List<SystemUser> getUsers(boolean registered, Long days);

    List<SystemUser> getUsersById(Collection<Long> ids);
}
