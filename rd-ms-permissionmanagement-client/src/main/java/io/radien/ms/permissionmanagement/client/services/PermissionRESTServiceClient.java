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
package io.radien.ms.permissionmanagement.client.services;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;

import io.radien.api.Configurable;
import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.model.permission.SystemAction;
import io.radien.ms.permissionmanagement.client.entities.Permission;
import io.radien.ms.permissionmanagement.client.util.ActionModelMapper;
import io.radien.ms.permissionmanagement.client.util.ListPermissionModelMapper;
import io.radien.ms.permissionmanagement.client.util.PermissionModelMapper;
import org.apache.cxf.bus.extension.ExtensionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.radien.api.model.permission.SystemPermission;
import io.radien.api.service.permission.PermissionRESTServiceAccess;

import io.radien.exception.SystemException;
import io.radien.ms.permissionmanagement.client.util.ClientServiceUtil;

/**
 * @author Newton Carvalho
 * Contract for Rest Service Client regarding Permission domain object
 */
@Stateless @Default
public class PermissionRESTServiceClient implements PermissionRESTServiceAccess {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(PermissionRESTServiceClient.class);

    @Inject
    private OAFAccess oaf;
    
    @Inject
    private ClientServiceUtil clientServiceUtil;

    /**
     * Gets an permission in the DB searching for the field Id
     *
     * @param id to be looked after
     * @return Optional List of Actions
     * @throws Exception in case it founds multiple actions or if URL is malformed
     */
    public Optional<SystemPermission> getPermissionById(Long id) throws SystemException {
        try {
            PermissionResourceClient client = clientServiceUtil.getPermissionResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_PERMISSIONMANAGEMENT));

            Response response = client.getById(id);
            SystemPermission permission = PermissionModelMapper.map((InputStream) response.getEntity());
            return Optional.ofNullable(permission);

        } catch (ExtensionException | ProcessingException | MalformedURLException e){
            log.error(e.getMessage(),e);
            throw new SystemException(e);
        }
    }

    /**
     * Gets all the permissions in the DB searching for the field Name
     *
     * @param name to be looked after
     * @return Optional List of Permissions
     * @throws Exception in case it founds multiple permissions or if URL is malformed
     */
    public Optional<SystemPermission> getPermissionByName(String name) throws SystemException {
        try {
            PermissionResourceClient client = clientServiceUtil.getPermissionResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_PERMISSIONMANAGEMENT));

            Response response = client.getPermissions(name,true,true);
            List<? extends SystemPermission> list = ListPermissionModelMapper.map((InputStream) response.getEntity());
            if (list.size() == 1) {
                return Optional.ofNullable(list.get(0));
            } else {
                return Optional.empty();
            }
        } catch (ExtensionException | ProcessingException | MalformedURLException e){
            log.error(e.getMessage(),e);
            throw new SystemException(e);
        }
    }

    /**
     * Creates given permission
     * @param permission to be created
     * @return true if permission has been created with success or false if not
     * @throws MalformedURLException in case of URL specification
     */
    public boolean create(SystemPermission permission) throws SystemException {
    	PermissionResourceClient client;
		try {
			client = clientServiceUtil.getPermissionResourceClient(oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_PERMISSIONMANAGEMENT));
		} catch (MalformedURLException e) {
			log.error(e.getMessage(),e);
            throw new SystemException(e);
		}
        try (Response response = client.save((Permission)permission)) {
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            } else {
                log.error(response.readEntity(String.class));
                return false;
            }
        } catch (ProcessingException e) {
        	log.error(e.getMessage(),e);
            throw new SystemException(e);
        }
    }

    @Override
    public OAFAccess getOAF() {
        return oaf;
    }
}
