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
import io.radien.exception.SystemException;
import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.ms.tenantmanagement.client.util.ClientServiceUtil;
import io.radien.ms.tenantmanagement.client.util.TenantModelMapper;
import org.apache.cxf.bus.extension.ExtensionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

/**
 * @author Santana
 */
@Stateless
@Default
public class TenantRESTServiceClient implements TenantRESTServiceAccess {
	private static final long serialVersionUID = 4007939167636938896L;

	private static final Logger log = LoggerFactory.getLogger(TenantRESTServiceClient.class);
    
    @Inject
    private ClientServiceUtil clientServiceUtil;

    @Inject
    private OAFAccess oafAccess;
    
    /**
     * Gets the contract in the DB searching for the field Name
     *
     * @param id to be looked after
     * @return Optional Contract
     */
    @Override
    public Optional<SystemTenant> getTenantById(Long id) throws MalformedURLException, ParseException, ProcessingException, ExtensionException {
        try {
            TenantResourceClient client = clientServiceUtil.getTenantResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT));
            Response response = client.getById(id);
            return Optional.of(TenantModelMapper.map((InputStream) response.getEntity()));
        }
        catch (ExtensionException | ProcessingException | MalformedURLException es){
            throw es;
        }
    }
    /**
     * Gets the contract in the DB searching for the field Name
     *
     * @param name to be looked after
     * @return Optional Contract
     */
    @Override
    public List<? extends SystemTenant> getTenantByName(String name) throws MalformedURLException, ParseException, ProcessingException, ExtensionException {
        try {
            TenantResourceClient client = clientServiceUtil.getTenantResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT));

            Response response = client.get(name, null, true, true);
            return TenantModelMapper.mapList((InputStream) response.getEntity());
        }
        catch (ExtensionException | ProcessingException | MalformedURLException | ParseException es ){
            throw es;
        }
    }

    /**
     * Gets all the tenants in the DB
     * @param pageNo to be show the information
     * @param pageSize number of max pages of information
     * @return list of system tenants
     * @throws MalformedURLException if URL is malformed
     * @throws ParseException in case of any issue parsing the json response
     */
    @Override
    public Page<? extends SystemTenant> getAll(int pageNo, int pageSize) throws SystemException {
        try {
            TenantResourceClient client = clientServiceUtil.getTenantResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT));
            Response response = client.getAll(pageNo, pageSize);
            return TenantModelMapper.mapToPage((InputStream) response.getEntity());
        } catch (ExtensionException | ProcessingException | MalformedURLException e){
            throw new SystemException(e);
        }
    }

    /**
     * Creates given Tenant
     * @param tenant to be created
     * @return true if user has been created with success or false if not
     * @throws MalformedURLException in case of URL specification
     */
    @Override
    public boolean create(SystemTenant tenant) throws MalformedURLException {
        TenantResourceClient client = clientServiceUtil.getTenantResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT));
        try (Response response = client.create((Tenant) tenant)) {
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            } else {
                log.error(response.readEntity(String.class));
                return false;
            }
        } catch (ProcessingException pe) {
            throw pe;
        }
    }

    @Override
    public boolean delete(long contractId) throws MalformedURLException {
        TenantResourceClient client = clientServiceUtil.getTenantResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT));
        try (Response response = client.delete(contractId)) {
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            } else {
                log.error(response.readEntity(String.class));
                return false;
            }
        } catch (ProcessingException pe) {
            throw pe;
        }
    }

    @Override
    public boolean update(SystemTenant tenant) throws MalformedURLException {
        TenantResourceClient client = clientServiceUtil.getTenantResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT));
        try (Response response = client.update(tenant.getId(),(Tenant) tenant)) {
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            } else {
                log.error(response.readEntity(String.class));
                return false;
            }
        } catch (ProcessingException pe) {
            throw pe;
        }
    }

    /**
     * Sends a request to the tenant client to validate if a specific tenant exists
     * @param tenantId to be found
     * @return true in case of success response
     * @throws MalformedURLException in case of any error in the specified url
     */
    @Override
    public boolean isTenantExistent(Long tenantId) throws MalformedURLException {
        TenantResourceClient client;
        try {
            client = clientServiceUtil.
                    getTenantResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT));
        } catch(MalformedURLException e) {
            throw new MalformedURLException();
        }

        try {
            Response response = client.exists(tenantId);
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            }
        } catch(ProcessingException e) {
            throw new ProcessingException(e);
        }
        return false;
    }

    /**
     * Will calculate how many records are existent in the db
     * @return the count of existent tenants.
     */
    public Long getTotalRecordsCount() throws SystemException {
        try {
            TenantResourceClient client = clientServiceUtil.getTenantResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT));

            Response response = client.getTotalRecordsCount();
            return Long.parseLong(response.readEntity(String.class));

        } catch (ExtensionException | ProcessingException | MalformedURLException e){
            throw new SystemException(e);
        }
    }
}
