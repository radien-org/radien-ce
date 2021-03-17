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
public class RoleRESTServiceClient implements RoleRESTServiceAccess {
	private static final long serialVersionUID = 2781374814532388090L;

	private static final Logger log = LoggerFactory.getLogger(RoleRESTServiceClient.class);

    @Inject
    private OAFAccess oaf;

    @Inject
    private ClientServiceUtil clientServiceUtil;

    /**
     * Gets all the roles in the DB
     * @param pageNo to be show the information
     * @param pageSize number of max pages of information
     * @return list of system roles
     * @throws MalformedURLException if URL is malformed
     */
    @Override
    public Page<? extends SystemRole> getAll(int pageNo, int pageSize) throws SystemException {
        try {
            RoleResourceClient client = clientServiceUtil.getRoleResourceClient(getOAF().getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
            Response response = client.getAll(pageNo, pageSize);
            return RoleModelMapper.mapToPage((InputStream) response.getEntity());
        } catch (ExtensionException | ProcessingException | MalformedURLException e){
            throw new SystemException(e);
        }
    }

    /**
     * Gets a role from the DB searching  by its Id
     * @param id of the role to be retrieved
     * @return Optional containing (or not) one role
     * @throws Exception in case of any trouble during the retrieving process
     */
    public Optional<SystemRole> getRoleById(Long id) throws SystemException {
        try {
            RoleResourceClient client = clientServiceUtil.getRoleResourceClient(getOAF().getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
            Response response = client.getById(id);
            return Optional.of(RoleModelMapper.map((InputStream) response.getEntity()));
        } catch (ExtensionException|ProcessingException | MalformedURLException e){
            throw new SystemException(e);
        }
    }

    /**
     * Gets all the roles in the DB searching for the field description
     *
     * @param description to be looked after
     * @return list of roles
     * @throws SystemException in case it founds multiple roles or if URL is malformed
     */
    public List<? extends SystemRole> getRolesByDescription(String description) throws SystemException {
        try {
            RoleResourceClient client = clientServiceUtil.getRoleResourceClient(getOAF().getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));

            Response response = client.getSpecificRoles(null, description,false,false);
            return ListRoleModelMapper.map((InputStream) response.getEntity());
        } catch (ExtensionException|ProcessingException | MalformedURLException e){
            throw new SystemException(e);
        }
    }

    /**
     * Creates given role
     * @param role to be created
     * @return true if user has been created with success or false if not
     * @throws MalformedURLException in case of URL specification
     */
    public boolean create(SystemRole role) throws SystemException {
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
     * Will calculate how many records are existent in the db
     * @return the count of existent roles.
     */
    public Long getTotalRecordsCount() throws SystemException {
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
