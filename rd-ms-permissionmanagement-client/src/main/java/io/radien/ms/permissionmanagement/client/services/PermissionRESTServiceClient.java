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

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.exception.SystemException;
import io.radien.ms.permissionmanagement.client.entities.Permission;
import io.radien.ms.permissionmanagement.client.util.ListPermissionModelMapper;
import io.radien.ms.permissionmanagement.client.util.PermissionModelMapper;
import io.radien.ms.permissionmanagement.client.util.PermissionPageModelMapper;
import org.apache.cxf.bus.extension.ExtensionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.radien.api.model.permission.SystemPermission;
import io.radien.api.service.permission.PermissionRESTServiceAccess;

import io.radien.ms.permissionmanagement.client.util.ClientServiceUtil;

/**
 * @author Newton Carvalho
 * Contract for Rest Service Client regarding Permission domain object
 */
@RequestScoped
public class PermissionRESTServiceClient implements PermissionRESTServiceAccess {

    private static final long serialVersionUID = -717253137740953998L;

    private static final Logger log = LoggerFactory.getLogger(PermissionRESTServiceClient.class);

    @Inject
    private ClientServiceUtil clientServiceUtil;

    @Inject
    private OAFAccess oaf;

    /**
     * Gets all permissions in the DB
     *
     * @return a list of system permissions in the db
     * @throws MalformedURLException in case of malformed url
     * @throws SystemException any issue when obtaining the records
     */
    @Override
    public List<? extends SystemPermission> getAll() throws SystemException {
        try {
            PermissionResourceClient client = clientServiceUtil.getPermissionResourceClient(oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_PERMISSIONMANAGEMENT));

            Response response = client.getAll(null, 1, 100, null, true);
            return PermissionPageModelMapper.map((InputStream) response.getEntity()).getResults();
        } catch (ExtensionException | ProcessingException | MalformedURLException e){
            throw new SystemException(e);
        }
    }

    /**
     * Gets permissions in the DB
     *
     * @return list of system permissions in the db
     * @throws SystemException any issue when obtaining the records
     */
    @Override
    public List<? extends SystemPermission> getPermissions(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) throws SystemException {
        try {
            PermissionResourceClient client = clientServiceUtil.getPermissionResourceClient(oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_PERMISSIONMANAGEMENT));

            Response response = client.getAll(search, pageNo, pageSize, sortBy, isAscending);
            return PermissionPageModelMapper.map((InputStream) response.getEntity()).getResults();
        } catch (ExtensionException | ProcessingException | MalformedURLException e){
            log.error(e.getMessage(),e);
            throw new SystemException(e);
        }
    }

    /**
     * Gets an permission in the DB searching for the field Id
     *
     * @param id to be looked after
     * @return Optional List of Actions
     * @throws SystemException in case it founds multiple actions or if URL is malformed
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
     * Retrieves from DB a collection containing permissions. The retrieval process will be
     * based on action identifier and (plus) a resource identifier
     * @param actionId action identifier
     * @param resourceId resource identifier
     * @return a list of permissions found using the action and the resource id
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    public List<? extends SystemPermission> getPermissionByActionAndResource(Long actionId, Long resourceId) throws SystemException {
        try {
            PermissionResourceClient client = clientServiceUtil.getPermissionResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_PERMISSIONMANAGEMENT));
            Response response = client.getPermissions(null, actionId, resourceId, true, true);
            return ListPermissionModelMapper.map((InputStream) response.getEntity());
        }
        catch (ExtensionException | ProcessingException | MalformedURLException e){
            log.error(e.getMessage(),e);
            throw new SystemException(e);
        }
    }

    /**
     * Gets all the permissions in the DB searching for the field Name
     *
     * @param name to be looked after
     * @return Optional List of Permissions
     * @throws SystemException in case it founds multiple permissions or if URL is malformed
     */
    public Optional<SystemPermission> getPermissionByName(String name) throws SystemException {
        try {
            PermissionResourceClient client = clientServiceUtil.getPermissionResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_PERMISSIONMANAGEMENT));

            Response response = client.getPermissions(name,null,null,true,true);
            List<? extends SystemPermission> list = ListPermissionModelMapper.map((InputStream) response.getEntity());
            if (list.size() == 1) {
                return Optional.ofNullable(list.get(0));
            } else {
                return Optional.empty();
            }
        } catch (ExtensionException | ProcessingException | MalformedURLException e){
            throw new SystemException(e);
        }
    }

    /**
     * Requests to delete a permission
     *
     * @param permissionId to be deleted
     * @return true in case of success
     */
    @Override
    public boolean delete(long permissionId) throws SystemException {
        PermissionResourceClient client;
        try {
            client = clientServiceUtil.getPermissionResourceClient(oaf.getProperty
                    (OAFProperties.SYSTEM_MS_ENDPOINT_PERMISSIONMANAGEMENT));

            Response response = client.delete(permissionId);
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            }
        } catch (ProcessingException | MalformedURLException e) {
            log.error(e.getMessage(),e);
            throw new SystemException(e);
        }
        return false;
    }

    /**
     * Requests a permission creation
     *
     * @param permission information to be created
     * @return true in case of success
     */
    @Override
    public boolean create(SystemPermission permission) throws SystemException {
        PermissionResourceClient client;
        try {
            client = clientServiceUtil.getPermissionResourceClient(oaf.getProperty
                    (OAFProperties.SYSTEM_MS_ENDPOINT_PERMISSIONMANAGEMENT));

            Response response = client.save((Permission) permission);
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            }
        } catch (MalformedURLException | ProcessingException e) {
            log.error(e.getMessage(),e);
            throw new SystemException(e);
        }
        return false;
    }

    /**
     * Request to validate if the permission exists in the DB
     *
     * @param permissionId to be searched
     * @return true in case of success
     */
    @Override
    public boolean isPermissionExistent(Long permissionId, String permissionName) throws SystemException {
        PermissionResourceClient client;
        try {
            client = clientServiceUtil.getPermissionResourceClient(oaf.getProperty
                    (OAFProperties.SYSTEM_MS_ENDPOINT_PERMISSIONMANAGEMENT));

            Response response = client.exists(permissionId, permissionName);
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            }
        } catch (MalformedURLException | ProcessingException e) {
            log.error(e.getMessage(),e);
            throw new SystemException(e);
        }
        return false;
    }

    @Override
    public OAFAccess getOAF() {
        return oaf;
    }
}
