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
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.service.tenant.TenantRESTServiceAccess;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.SystemException;
import io.radien.exception.TokenExpiredException;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.ms.tenantmanagement.client.exceptions.InternalServerErrorException;
import io.radien.ms.tenantmanagement.client.util.ClientServiceUtil;
import io.radien.ms.tenantmanagement.client.util.TenantModelMapper;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import org.apache.cxf.bus.extension.ExtensionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contract REST service requests to the client
 *
 * @author Santana
 */
@RequestScoped
public class TenantRESTServiceClient extends AuthorizationChecker implements TenantRESTServiceAccess {
	private static final long serialVersionUID = 4007939167636938896L;

	private static final Logger log = LoggerFactory.getLogger(TenantRESTServiceClient.class);
    
    @Inject
    private ClientServiceUtil clientServiceUtil;

    @Inject
    private OAFAccess oafAccess;
    
    /**
     * Gets requester to get the tenant in the DB searching for the field Id
     *
     * @param id to be looked after
     * @return Optional list of tenant
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    @Override
    public Optional<SystemTenant> getTenantById(Long id) throws SystemException {
        try {
            return getSystemTenant(id);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getSystemTenant(id);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }

    }

    /**
     * Gets the tenant in the DB searching for the field Id
     * @param id to be looked after
     * @return Optional list of tenant
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private Optional<SystemTenant> getSystemTenant(Long id) throws SystemException {
        try {
            TenantResourceClient client = clientServiceUtil.getTenantResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT));
            Response response = client.getById(id);
            return Optional.of(TenantModelMapper.map((InputStream) response.getEntity()));
        }  catch (ExtensionException | ProcessingException | MalformedURLException| ParseException es){
            throw new SystemException(es.getMessage());
        }
    }

    /**
     * Gets the requester to get the tenant in the DB searching for the field Name
     * @param name to be looked after
     * @return Optional list of tenant
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    @Override
    public List<? extends SystemTenant> getTenantByName(String name) throws SystemException {
        try {
            return getTenantsByName(name);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getTenantsByName(name);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Gets the tenant in the DB searching for the field Name
     * @param name to be looked after
     * @return Optional list of tenant
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private List<? extends Tenant> getTenantsByName(String name) throws SystemException {
        try {
            TenantResourceClient client = clientServiceUtil.getTenantResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT));
            Response response = client.get(name, null, null, true, true);
            return TenantModelMapper.mapList((InputStream) response.getEntity());
        }
        catch (ExtensionException | ProcessingException | MalformedURLException | ParseException es ){
            throw new SystemException(es.getMessage());
        }
    }

    /**
     * Gets the tenants in the DB searching based in a given list of ids.
     * To do that invokes the core method counterpart and handles TokenExpiration error.
     * @param ids to be looked after
     * @return list containing tenants
     * @throws SystemException in case of token expiration or any issue on the application
     */
    @Override
    public List<? extends SystemTenant> getTenantsByIds(List<Long> ids) throws SystemException {
        try {
            return getSystemTenants(ids);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getSystemTenants(ids);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Core method that gets the tenants in the DB searching based in a given list of ids
     * @param ids to be looked after
     * @return list containing tenants
     * @throws SystemException in case of token expiration or any issue on the application
     */
    private List<? extends SystemTenant> getSystemTenants(List<Long> ids) throws SystemException {
        try {
            TenantResourceClient client = clientServiceUtil.getTenantResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT));
            Response response = client.get(null, null, ids, true, true);
            return TenantModelMapper.mapList((InputStream) response.getEntity());
        }
        catch (ExtensionException | ProcessingException | MalformedURLException| ParseException |
                InternalServerErrorException es){
            throw new SystemException(es.getMessage());
        }
    }

    /**
     * Gets the requester to get all the tenants into a pagination mode.
     * @param search name description for some tenant (optional)
     * @param pageNo of the requested information. Where the tenant is.
     * @param pageSize total number of pages returned in the request.
     * @param sortBy sort filter criteria.
     * @param isAscending ascending filter criteria.
     * @return a page of system tenants.
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    @Override
    public Page<? extends SystemTenant> getAll(String search,
                                               int pageNo,
                                               int pageSize,
                                               List<String> sortBy,
                                               boolean isAscending) throws SystemException {
        try {
            return getTenantPage(search, pageNo, pageSize, sortBy, isAscending);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getTenantPage(search, pageNo, pageSize, sortBy, isAscending);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Get all the tenants into a pagination mode.
     * @param search name description for some tenant (optional)
     * @param pageNo of the requested information. Where the tenant is.
     * @param pageSize total number of pages returned in the request.
     * @param sortBy sort filter criteria.
     * @param isAscending ascending filter criteria.
     * @return a page of system tenants.
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private Page<Tenant> getTenantPage(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) throws SystemException {
        try {
            TenantResourceClient client = clientServiceUtil.getTenantResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT));
            Response response = client.getAll(search, pageNo, pageSize, sortBy, isAscending);
            return TenantModelMapper.mapToPage((InputStream) response.getEntity());
        } catch (ExtensionException | ProcessingException | MalformedURLException e){
            throw new SystemException(e);
        }
    }

    /**
     * Asks the requester to creat a given Tenant
     * @param tenant to be created
     * @return true if user has been created with success or false if not
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    @Override
    public boolean create(SystemTenant tenant) throws SystemException {
        try {
            return createTenant((Tenant) tenant);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return createTenant((Tenant) tenant);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Creates given Tenant
     * @param tenant to be created
     * @return true if user has been created with success or false if not
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private boolean createTenant(Tenant tenant) throws SystemException {
        TenantResourceClient client;
        try {
            client = clientServiceUtil.getTenantResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT));
        } catch (MalformedURLException malformedURLException){
            throw new SystemException(malformedURLException.getMessage());
        }
        try (Response response = client.create(tenant)) {
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            } else {
                String entity = response.readEntity(String.class);
                log.error(entity);
                return false;
            }
        } catch (ProcessingException pe) {
            throw new SystemException(pe.getMessage());
        }
    }

    /**
     * Asks the requester to delete a given tenant
     * @param tenantId to be deleted
     * @return true if user has been deleted with success or false if not
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    @Override
    public boolean delete(long tenantId) throws SystemException {
        try {
            return deleteRequester(tenantId);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return deleteRequester(tenantId);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Deletes a requester tenant
     * @param tenantId to be deleted
     * @return true if user has been deleted with success or false if not
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private boolean deleteRequester(long tenantId) throws SystemException {
        TenantResourceClient client;
        try {
            client = clientServiceUtil.getTenantResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT));
        } catch (MalformedURLException malformedURLException){
            throw new SystemException(malformedURLException.getMessage());
        }
        try (Response response = client.delete(tenantId)) {
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            } else {
                String entity = response.readEntity(String.class);
                log.error(entity);
                return false;
            }
        } catch (ProcessingException pe) {
            throw new SystemException(pe.getMessage());
        }
    }

    /**
     * Asks the requester to delete a given tenant and all the hierarchy tenants
     * @param id to be deleted
     * @return true if user has been deleted with success or false if not
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    @Override
    public boolean deleteTenantHierarchy(long id) throws SystemException {
        try {
            return delTenantHierarchy(id);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return delTenantHierarchy(id);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Deletes a requester tenant and all the hierarchy tenants
     * @param id to be deleted
     * @return true if user has been deleted with success or false if not
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private boolean delTenantHierarchy(long id) throws SystemException {
        TenantResourceClient client;
        try {
            client = clientServiceUtil.getTenantResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT));
        } catch (MalformedURLException malformedURLException){
            throw new SystemException(malformedURLException.getMessage());
        }
        try (Response response = client.deleteTenantHierarchy(id)) {
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            } else {
                String entity = response.readEntity(String.class);
                log.error(entity);
                return false;
            }
        } catch (ProcessingException pe) {
            throw new SystemException(pe.getMessage());
        }
    }

    /**
     * Asks the requester update a given tenant
     * @param tenant to be updated
     * @return true if user has been updated with success or false if not
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    @Override
    public boolean update(SystemTenant tenant) throws SystemException {
        try {
            return updateTenant(tenant);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return updateTenant(tenant);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Updates a given tenant
     * @param tenant to be updated
     * @return true if user has been updated with success or false if not
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private boolean updateTenant(SystemTenant tenant) throws SystemException {
        TenantResourceClient client;
        try {
            client = clientServiceUtil.getTenantResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT));
        } catch (MalformedURLException malformedURLException){
            throw new SystemException(malformedURLException.getMessage());
        }
        try (Response response = client.update(tenant.getId(),(Tenant) tenant)) {
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            } else {
                String entity = response.readEntity(String.class);
                log.error(entity);
                return false;
            }
        } catch (ProcessingException pe) {
            throw new SystemException(pe.getMessage());
        }
    }

    /**
     * Gets the requester to see if a specific tenant exists
     * @param tenantId to be found
     * @return true in case of success response
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    @Override
    public boolean isTenantExistent(Long tenantId) throws SystemException {
        try {
            return isTenantExistentRequester(tenantId);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return isTenantExistentRequester(tenantId);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Sends a request to the tenant client to validate if a specific tenant exists
     * @param tenantId to be found
     * @return true in case of success response
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private boolean isTenantExistentRequester(Long tenantId) throws SystemException {
        TenantResourceClient client;
        try {
            client = clientServiceUtil.
                    getTenantResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT));
        } catch(MalformedURLException e) {
            throw new SystemException(e.getMessage());
        }

        try {
            Response response = client.exists(tenantId);
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return response.readEntity(Boolean.class);
            }
        } catch(ProcessingException e) {
            throw new SystemException(e.getMessage());
        }
        return false;
    }

}
