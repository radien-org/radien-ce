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
package io.radien.ms.tenantmanagement.client.services;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.entity.Page;
import io.radien.api.model.tenant.SystemActiveTenant;
import io.radien.api.service.tenant.ActiveTenantRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.exception.TokenExpiredException;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.tenantmanagement.client.entities.ActiveTenant;
import io.radien.ms.tenantmanagement.client.util.ActiveTenantModelMapper;
import io.radien.ms.tenantmanagement.client.util.ClientServiceUtil;
import org.apache.cxf.bus.extension.ExtensionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

/**
 * Active Tenant REST service requests to the client
 *
 * @author Bruno Gama
 */
@Stateless
public class ActiveTenantRESTServiceClient extends AuthorizationChecker implements ActiveTenantRESTServiceAccess {
	private static final long serialVersionUID = -4351385833751601092L;

	private static final Logger log = LoggerFactory.getLogger(ActiveTenantRESTServiceClient.class);
    
    @Inject
    private ClientServiceUtil clientServiceUtil;

    @Inject
    private OAFAccess oafAccess;

    private final String unableToRecoverExpiredToken = "Unable to recover expiredToken";


    /**
     * Gets requester to get the active tenant in the DB searching for the field Id
     *
     * @param userId to be looked after
     * @param tenantId to be looked after
     * @return list of active tenant
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    @Override
    public List<? extends SystemActiveTenant> getActiveTenantByUserAndTenant(Long userId, Long tenantId) throws SystemException {
        try {
            return getActiveTenantByUserAndTenantRequester(userId, tenantId);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getActiveTenantByUserAndTenantRequester(userId, tenantId);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(unableToRecoverExpiredToken);
            }
        }
    }

    /**
     * Gets the active tenant in the DB searching for the field Id
     * @param userId to be looked after
     * @param tenantId to be looked after
     * @return list of tenant
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private List<? extends SystemActiveTenant> getActiveTenantByUserAndTenantRequester(Long userId, Long tenantId) throws SystemException {
        try {
            ActiveTenantResourceClient client = clientServiceUtil.getActiveTenantResourceClient(oafAccess.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT));
            Response response = client.getByUserAndTenant(userId, tenantId);
            return ActiveTenantModelMapper.mapList((InputStream) response.getEntity());
        }  catch (ExtensionException | ProcessingException | MalformedURLException| ParseException es){
            throw new SystemException(es.getMessage());
        }
    }

    /**
     * Gets requester to get the active tenant in the DB searching for the field Id
     *
     * @param id to be looked after
     * @return Optional list of active tenant
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    @Override
    public Optional<SystemActiveTenant> getActiveTenantById(Long id) throws SystemException {
        try {
            return getSystemActiveTenant(id);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getSystemActiveTenant(id);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(unableToRecoverExpiredToken);
            }
        }
    }

    /**
     * Gets the requester to get the active tenant in the DB searching for the user
     * @param userId to be looked after
     * @param tenantId to be looked after
     * @param tenantName to be looked after
     * @param isTenantActive to be looked after
     * @return list of active tenant
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    @Override
    public List<? extends SystemActiveTenant> getActiveTenantByFilter(Long userId, Long tenantId,
                                                                      String tenantName, boolean isTenantActive) throws SystemException {
        try {
            return getActiveTenantByFilterRequester(userId, tenantId, tenantName, isTenantActive);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getActiveTenantByFilterRequester(userId, tenantId, tenantName, isTenantActive);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(unableToRecoverExpiredToken);
            }
        }
    }

    /**
     * Gets the active tenant in the DB searching for the user
     * @param userId to be looked after
     * @param tenantId to be looked after
     * @param tenantName to be looked after
     * @param isTenantActive to be looked after
     * @return list of active tenant
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private List<? extends ActiveTenant> getActiveTenantByFilterRequester(Long userId, Long tenantId,
                                                                          String tenantName, boolean isTenantActive) throws SystemException {
        try {
            ActiveTenantResourceClient client = clientServiceUtil.getActiveTenantResourceClient(
                    oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT));
            Response response = client.get(userId, tenantId, tenantName, isTenantActive, false);
            return ActiveTenantModelMapper.mapList((InputStream) response.getEntity());
        }
        catch (ExtensionException | ProcessingException | MalformedURLException | ParseException es ){
            throw new SystemException(es.getMessage());
        }
    }

    /**
     * Gets the active tenant in the DB searching for the field Id
     * @param id to be looked after
     * @return Optional list of tenant
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private Optional<SystemActiveTenant> getSystemActiveTenant(Long id) throws SystemException {
        try {
            ActiveTenantResourceClient client = clientServiceUtil.getActiveTenantResourceClient(oafAccess.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT));
            Response response = client.getById(id);
            return Optional.of(ActiveTenantModelMapper.map((InputStream) response.getEntity()));
        }  catch (ExtensionException | ProcessingException | MalformedURLException| ParseException es){
            throw new SystemException(es.getMessage());
        }
    }

    /**
     * Gets the requester to get all the active tenants into a pagination mode.
     * @param search name description for some active tenant (optional)
     * @param pageNo of the requested information. Where the active tenant is.
     * @param pageSize total number of pages returned in the request.
     * @param sortBy sort filter criteria.
     * @param isAscending ascending filter criteria.
     * @return a page of system active tenants.
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    @Override
    public Page<? extends SystemActiveTenant> getAll(String search,
                                               int pageNo,
                                               int pageSize,
                                               List<String> sortBy,
                                               boolean isAscending) throws SystemException {
        try {
            return getActiveTenantPage(search, pageNo, pageSize, sortBy, isAscending);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getActiveTenantPage(search, pageNo, pageSize, sortBy, isAscending);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(unableToRecoverExpiredToken);
            }
        }
    }

    /**
     * Get all the active tenants into a pagination mode.
     * @param search name description for some active tenant (optional)
     * @param pageNo of the requested information. Where the active tenant is.
     * @param pageSize total number of pages returned in the request.
     * @param sortBy sort filter criteria.
     * @param isAscending ascending filter criteria.
     * @return a page of system active tenants.
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private Page<ActiveTenant> getActiveTenantPage(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) throws SystemException {
        try {
            ActiveTenantResourceClient client = clientServiceUtil.getActiveTenantResourceClient(oafAccess.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT));
            Response response = client.getAll(search, pageNo, pageSize, sortBy, isAscending);
            return ActiveTenantModelMapper.mapToPage((InputStream) response.getEntity());
        } catch (ExtensionException | ProcessingException | MalformedURLException e){
            throw new SystemException(e);
        }
    }

    /**
     * Asks the requester to creat a given active Tenant
     * @param activeTenant to be created
     * @return true if user has been created with success or false if not
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    @Override
    public boolean create(SystemActiveTenant activeTenant) throws SystemException {
        try {
            return createActiveTenant((ActiveTenant) activeTenant);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return createActiveTenant((ActiveTenant) activeTenant);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(unableToRecoverExpiredToken);
            }
        }
    }

    /**
     * Creates given active Tenant
     * @param activeTenant to be created
     * @return true if user has been created with success or false if not
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private boolean createActiveTenant(ActiveTenant activeTenant) throws SystemException {
        ActiveTenantResourceClient client;
        try {
            client = clientServiceUtil.getActiveTenantResourceClient(oafAccess.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT));
        } catch (MalformedURLException malformedURLException){
            throw new SystemException(malformedURLException.getMessage());
        }
        try (Response response = client.create(activeTenant)) {
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            } else {
                String issueMessage = response.readEntity(String.class);
                log.error(issueMessage);
                return false;
            }
        } catch (ProcessingException pe) {
            throw new SystemException(pe.getMessage());
        }
    }

    /**
     * Asks the requester to delete a given active tenant
     * @param activeTenantId to be deleted
     * @return true if user has been deleted with success or false if not
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    @Override
    public boolean delete(long activeTenantId) throws SystemException {
        try {
            return deleteRequester(activeTenantId);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return deleteRequester(activeTenantId);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(unableToRecoverExpiredToken);
            }
        }
    }

    /**
     * Deletes a requester active tenant
     * @param activeTenantId to be deleted
     * @return true if user has been deleted with success or false if not
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private boolean deleteRequester(long activeTenantId) throws SystemException {
        ActiveTenantResourceClient client;
        try {
            client = clientServiceUtil.getActiveTenantResourceClient(oafAccess.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT));
        } catch (MalformedURLException malformedURLException){
            throw new SystemException(malformedURLException.getMessage());
        }
        try (Response response = client.delete(activeTenantId)) {
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            } else {
                String issueMessage = response.readEntity(String.class);
                log.error(issueMessage);
                return false;
            }
        } catch (ProcessingException pe) {
            throw new SystemException(pe.getMessage());
        }
    }

    /**
     * Asks the requester to delete an active tenant taking in consideration
     * @param tenant tenant id
     * @param user user id
     * @return true if user has been deleted with success or false if not
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    @Override
    public boolean deleteByTenantAndUser(long tenant, long user) throws SystemException {
        try {
            return deleteByTenantAndUserRequester(tenant, user);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return deleteByTenantAndUserRequester(tenant, user);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(unableToRecoverExpiredToken);
            }
        }
    }

    /**
     * Deletes active tenant by informed tenant id and user id
     * @param tenant tenant identifier
     * @param user user identifier
     * @return true if user has been deleted with success or false if not
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private boolean deleteByTenantAndUserRequester(long tenant, long user) throws SystemException {
        try {
            ActiveTenantResourceClient client = clientServiceUtil.getActiveTenantResourceClient(oafAccess.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT));
            Response response = client.delete(tenant, user);
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return response.readEntity(Boolean.class);
            }
            String issue = response.readEntity(String.class);
            log.error(issue);
            return false;
        }
        catch (MalformedURLException | ExtensionException | ProcessingException e) {
            throw new SystemException(e);
        }
    }

    /**
     * Asks the requester update a given active tenant
     * @param activeTenant to be updated
     * @return true if user has been updated with success or false if not
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    @Override
    public boolean update(SystemActiveTenant activeTenant) throws SystemException {
        try {
            return updateActiveTenant(activeTenant);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return updateActiveTenant(activeTenant);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(unableToRecoverExpiredToken);
            }
        }
    }

    /**
     * Updates a given active tenant
     * @param activeTenant to be updated
     * @return true if user has been updated with success or false if not
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private boolean updateActiveTenant(SystemActiveTenant activeTenant) throws SystemException {
        ActiveTenantResourceClient client;
        try {
            client = clientServiceUtil.getActiveTenantResourceClient(oafAccess.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT));
        } catch (MalformedURLException malformedURLException){
            throw new SystemException(malformedURLException.getMessage());
        }
        try (Response response = client.update(activeTenant.getId(),(ActiveTenant) activeTenant)) {
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            } else {
                String issueMessage = response.readEntity(String.class);
                log.error(issueMessage);
                return false;
            }
        } catch (ProcessingException pe) {
            throw new SystemException(pe.getMessage());
        }
    }

    /**
     * Gets the requester to see if a specific active tenant exists
     * @param userId to be found
     * @param tenantId to be found
     * @return true in case of success response
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    @Override
    public boolean isActiveTenantExistent(Long userId, Long tenantId) throws SystemException {
        try {
            return isActiveTenantExistentRequester(userId, tenantId);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return isActiveTenantExistentRequester(userId, tenantId);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(unableToRecoverExpiredToken);
            }
        }
    }

    /**
     * Sends a request to the active tenant client to validate if a specific active tenant exists
     * @param userId to be found
     * @param tenantId to be found
     * @return true in case of success response
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private boolean isActiveTenantExistentRequester(Long userId, Long tenantId) throws SystemException {
        ActiveTenantResourceClient client;
        try {
            client = clientServiceUtil.
                    getActiveTenantResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT));
        } catch(MalformedURLException e) {
            throw new SystemException(e.getMessage());
        }

        try {
            Response response = client.exists(userId, tenantId);
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return response.readEntity(Boolean.class);
            }
        } catch(ProcessingException e) {
            throw new SystemException(e.getMessage());
        }
        return false;
    }
}
