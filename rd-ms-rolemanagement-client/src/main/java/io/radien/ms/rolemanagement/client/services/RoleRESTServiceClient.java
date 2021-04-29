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
package io.radien.ms.rolemanagement.client.services;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.entity.Page;
import io.radien.api.model.role.SystemRole;
import io.radien.api.service.role.RoleRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.exception.TokenExpiredException;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.rolemanagement.client.entities.Role;
import io.radien.ms.rolemanagement.client.util.ListRoleModelMapper;
import io.radien.ms.rolemanagement.client.util.ClientServiceUtil;
import io.radien.ms.rolemanagement.client.util.RoleModelMapper;
import org.apache.cxf.bus.extension.ExtensionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;

/**
 * @author Bruno Gama
 */
@RequestScoped
@Default
public class RoleRESTServiceClient extends AuthorizationChecker implements RoleRESTServiceAccess {
	private static final long serialVersionUID = 2781374814532388090L;

	private static final Logger log = LoggerFactory.getLogger(RoleRESTServiceClient.class);

    @Inject
    private OAFAccess oaf;

    @Inject
    private ClientServiceUtil clientServiceUtil;

    /**
     * Calls the requester to retrieve a page object containing roles that matches search parameter.
     * In case of omitted (empty) search parameter retrieves ALL roles. If not possible it will reload the
     * access token and retry.
     * @param search search parameter for matching roles (optional).
     * @param pageNo page number where the user is seeing the information.
     * @param pageSize number of roles to be showed in each page.
     * @param sortBy Sorting fields
     * @param isAscending Defines if ascending or descending in relation of sorting fields
     * @return page containing system roles
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    @Override
    public Page<? extends SystemRole> getAll(String search, int pageNo, int pageSize,
                                             List<String> sortBy, boolean isAscending) throws SystemException {
        try {
            return getAllRequester(search, pageNo, pageSize, sortBy, isAscending);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getAllRequester(search, pageNo, pageSize, sortBy, isAscending);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException("Unable to recover expiredToken");
            }
        }
    }

    /**
     * Retrieves a page object containing roles that matches search parameter.
     * In case of omitted (empty) search parameter retrieves ALL roles
     * @param search search parameter for matching roles (optional).
     * @param pageNo page number where the user is seeing the information.
     * @param pageSize number of roles to be showed in each page.
     * @param sortBy Sorting fields
     * @param isAscending Defines if ascending or descending in relation of sorting fields
     * @return page containing system roles
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private Page<? extends SystemRole> getAllRequester(String search, int pageNo, int pageSize,
                                                      List<String> sortBy, boolean isAscending) throws SystemException {
        try {
            RoleResourceClient client = clientServiceUtil.getRoleResourceClient(getOAF().getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
            Response response = client.getAll(search, pageNo, pageSize, sortBy, isAscending);
            return RoleModelMapper.mapToPage((InputStream) response.getEntity());
        } catch (ExtensionException | ProcessingException | MalformedURLException e){
            throw new SystemException(e);
        }
    }

    /**
     * Calls the requester to get a role from the DB searching  by its Id if not possible will refresh the access token
     * and retry
     * @param id of the role to be retrieved
     * @return Optional containing (or not) one role
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    public Optional<SystemRole> getRoleById(Long id) throws SystemException {
        try {
            return getRoleByIdRequester(id);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getRoleByIdRequester(id);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException("Unable to recover expiredToken");
            }
        }
    }

    /**
     * Gets a role from the DB searching  by its Id
     * @param id of the role to be retrieved
     * @return Optional containing (or not) one role
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private Optional<SystemRole> getRoleByIdRequester(Long id) throws SystemException {
        try {
            RoleResourceClient client = clientServiceUtil.getRoleResourceClient(getOAF().getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
            Response response = client.getById(id);
            return Optional.of(RoleModelMapper.map((InputStream) response.getEntity()));
        } catch (ExtensionException|ProcessingException | MalformedURLException e){
            throw new SystemException(e);
        }
    }

    /**
     * Calls the requester to get a role from the DB searching  by its Name if not possible will refresh the access
     * token and retry
     * @param name of the role to be retrieved
     * @return Optional containing (or not) one role
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    public Optional<SystemRole> getRoleByName(String name) throws SystemException {
        try {
            return getRoleByNameRequester(name);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getRoleByNameRequester(name);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException("Unable to recover expiredToken");
            }
        }
    }

    /**
     * Gets a role from the DB searching  by its Name
     * @param name of the role to be retrieved
     * @return Optional containing (or not) one role
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private Optional<SystemRole> getRoleByNameRequester(String name) throws SystemException {
        try {
            RoleResourceClient client = clientServiceUtil.getRoleResourceClient(getOAF().getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
            Response response = client.getSpecificRoles(name, null, true, true);
            List<? extends SystemRole> list = ListRoleModelMapper.map((InputStream) response.getEntity());
            if (list.size() == 1) {
                return Optional.ofNullable(list.get(0));
            } else {
                return Optional.empty();
            }
        } catch (ExtensionException|ProcessingException | MalformedURLException e){
            throw new SystemException(e);
        }
    }

    /**
     * Calls the requester to get all the roles in the DB searching for the field description if not possible will
     * reload the access token and retry
     * @param description to be looked after
     * @return list of roles
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    public List<? extends SystemRole> getRolesByDescription(String description) throws SystemException {
        try {
            return getRolesByDescriptionRequester(description);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getRolesByDescriptionRequester(description);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException("Unable to recover expiredToken");
            }
        }
    }

    /**
     * Gets all the roles in the DB searching for the field description
     * @param description to be looked after
     * @return list of roles
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private List<? extends SystemRole> getRolesByDescriptionRequester(String description) throws SystemException {
        try {
            RoleResourceClient client = clientServiceUtil.getRoleResourceClient(getOAF().getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));

            Response response = client.getSpecificRoles(null, description,false,false);
            return ListRoleModelMapper.map((InputStream) response.getEntity());
        } catch (ExtensionException | ProcessingException | MalformedURLException e){
            throw new SystemException(e);
        }
    }

    /**
     * Calls the requester to create given role if not possible will reload the access token and retry
     * @param role to be created
     * @return true if user has been created with success or false if not
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    public boolean create(SystemRole role) throws SystemException {
        try {
            return createRequester(role);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return createRequester(role);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException("Unable to recover expiredToken");
            }
        }
    }

    /**
     * Creates given role
     * @param role to be created
     * @return true if user has been created with success or false if not
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private boolean createRequester(SystemRole role) throws SystemException {
        RoleResourceClient client;
        try {
            client = clientServiceUtil.getRoleResourceClient(getOAF().getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
        } catch (MalformedURLException e) {
            throw new SystemException(e);
        }
        try (Response response = client.save((Role)role)) {
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            } else {
                log.error(response.readEntity(String.class));
                return false;
            }
        } catch (ProcessingException e) {
            throw new SystemException(e);
        }
    }

    /**
     * Calls the requester to calculate how many records are existent in the db if not possible will reload
     * the access token and retry
     * @return the count of existent roles.
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    public Long getTotalRecordsCount() throws SystemException {
        try {
            return getTotalRecordsCountRequester();
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getTotalRecordsCountRequester();
            } catch (TokenExpiredException expiredException1){
                throw new SystemException("Unable to recover expiredToken");
            }
        }
    }

    /**
     * Will calculate how many records are existent in the db
     * @return the count of existent roles.
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private Long getTotalRecordsCountRequester() throws SystemException {
        try {
            RoleResourceClient client = clientServiceUtil.getRoleResourceClient(getOAF().getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));

            Response response = client.getTotalRecordsCount();
            return Long.parseLong(response.readEntity(String.class));

        } catch (ExtensionException | ProcessingException | MalformedURLException e){
            throw new SystemException(e);
        }
    }

	@Override
	public OAFAccess getOAF() {
		return oaf;
	}
}
