/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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

package io.radien.webapp;

import java.io.Serializable;

import javax.faces.context.ExternalContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import io.radien.api.model.user.SystemUser;
import io.radien.exception.AccountNotValidException;
import io.radien.exception.UserNotFoundException;
import io.radien.util.OAFHttpRequest;

/**
 * Main handler of all application session
 *
 * @author Rafael Fernandes
 */
public interface SessionHandler extends Serializable {
	public static final String ATTR_USER = "_USER";

    /**
     * Get the current http session
     * @param create should session be created or not
     * @return the requested and created http session
     */
    HttpSession getSession(boolean create);

    /**
     * Get the current http session
     * @param create should session be created or not
     * @param request http servlet request endpoint
     * @param response http servlet response endpoint
     * @return the requested and created http session
     */
    HttpSession getSession(boolean create, HttpServletRequest request, HttpServletResponse response);

    /**
     * Get current request
     * @return http servlet request
     */
    HttpServletRequest getRequest();

    /**
     * Get active system user requesting creation
     * @return the active system user
     */
    SystemUser getUser();

    /**
     * Get active system user requesting creation
     * @param request http servlet request endpoint
     * @param response http servlet response endpoint
     * @return the system user requesting session
     * @throws UserNotFoundException in case of user was not beeing able to be found or does not exist
     */
    SystemUser getUser(HttpServletRequest request, HttpServletResponse response) throws UserNotFoundException;

    /**
     * Registration of the system user
     * @param user to be register
     * @throws AccountNotValidException Exception to be thrown when a given system account is not valid
     */
    void register(SystemUser user) throws AccountNotValidException;

    /**
     * Registration of the system user
     * @param req requester endpoint to be registered
     * @param user to be register
     * @throws AccountNotValidException Exception to be thrown when a given system account is not valid
     */
    void register(OAFHttpRequest req, SystemUser user) throws AccountNotValidException;

    /**
     * Update the given system user
     * @param user to be updated
     * @throws AccountNotValidException Exception to be thrown when a given system account is not valid
     */
    void update(SystemUser user) throws AccountNotValidException;

    /**
     * Method to logout the system user
     */
    void logout();

    /**
     * Method to logout the system user in the given endpoints
     * @param request http servlet request endpoint
     * @param response http servlet response endpoint
     */
    void logout(HttpServletRequest request, HttpServletResponse response);

    /**
     * Method to register given user session in given endpoints
     * @param request http servlet request endpoint
     * @param response http servlet response endpoint
     * @param user to be register
     */
    void register(HttpServletRequest request, HttpServletResponse response, SystemUser user);

    /**
     * Method to recover a given user in a specific request enpdpoint
     * @param request http servlet request endpoint
     * @return if found
     */
    SystemUser getUser(HttpServletRequest request);

    /**
     * Delete all the registered cookies from a certain context
     * @param externalContext context for the cookies to be deleted
     */
    void eraseCookies(ExternalContext externalContext);

    /**
     *Delete all the registered cookies from a certain servlet endpoints
     * @param req http servlet request endpoint
     * @param resp http servlet response endpoint
     */
    void eraseCookies(HttpServletRequest req, HttpServletResponse resp);

}
