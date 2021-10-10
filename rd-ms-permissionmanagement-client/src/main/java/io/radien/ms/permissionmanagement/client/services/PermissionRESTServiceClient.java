/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemPermission;
import io.radien.api.service.permission.PermissionRESTServiceAccess;
import io.radien.exception.BadRequestException;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.InternalServerErrorException;
import io.radien.exception.NotFoundException;
import io.radien.exception.SystemException;
import io.radien.exception.TokenExpiredException;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.permissionmanagement.client.entities.Permission;
import io.radien.ms.permissionmanagement.client.util.ClientServiceUtil;
import io.radien.ms.permissionmanagement.client.util.ListPermissionModelMapper;
import io.radien.ms.permissionmanagement.client.util.PermissionModelMapper;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.apache.cxf.bus.extension.ExtensionException;

/**
 * Contract for Rest Service Client regarding Permission domain object
 * @author Newton Carvalho
 */
@RequestScoped
public class PermissionRESTServiceClient extends AuthorizationChecker implements PermissionRESTServiceAccess {

    private static final long serialVersionUID = -717253137740953998L;

    @Inject
    private ClientServiceUtil clientServiceUtil;

    @Inject
    private OAFAccess oaf;

    /**
     * Calls the requester to fetch all permissions if not possible will reload the access token and retry
     * @param search value to be filtered
     * @param pageNo of the information to be checked
     * @param pageSize max page numbers for the necessary requested data
     * @param sortBy list of values to sort request
     * @param isAscending in case of true data will come ascending mode if false descending
     * @return list of permissions
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    @Override
    public Page<? extends SystemPermission> getAll(String search, int pageNo, int pageSize,
                                                                  List<String> sortBy, boolean isAscending) throws SystemException {
        try {
            return getAllRequester(search, pageNo, pageSize, sortBy, isAscending);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getAllRequester(search, pageNo, pageSize, sortBy, isAscending);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Fetches all permissions
     * @param search value to be filtered
     * @param pageNo of the information to be checked
     * @param pageSize max page numbers for the necessary requested data
     * @param sortBy list of values to sort request
     * @param isAscending in case of true data will come ascending mode if false descending
     * @return list of permissions
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private Page<?extends SystemPermission> getAllRequester(String search, int pageNo, int pageSize,
                                                           List<String> sortBy, boolean isAscending) throws SystemException {
        try {
            PermissionResourceClient client = clientServiceUtil.getPermissionResourceClient(oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_PERMISSIONMANAGEMENT));
            Response response = client.getAll(search, pageNo, pageSize, sortBy, isAscending);
            return PermissionModelMapper.mapToPage((InputStream) response.getEntity());
        } catch (ExtensionException | ProcessingException | MalformedURLException e){
            throw new SystemException(e);
        }
    }

    /**
     * Calls the requester to gets permissions in the DB, if not possible will refresh the access token and retry
     * @return list of system permissions in the db
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    @Override
    public List<? extends SystemPermission> getPermissions(String search, int pageNo, int pageSize,
                                                           List<String> sortBy, boolean isAscending) throws SystemException {
        try {
            return getPermissionRequester(search, pageNo, pageSize, sortBy, isAscending);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getPermissionRequester(search, pageNo, pageSize, sortBy, isAscending);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Gets permissions in the DB
     * @return list of system permissions in the db
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private List<? extends SystemPermission> getPermissionRequester(String search, int pageNo, int pageSize,
                                                                   List<String> sortBy, boolean isAscending) throws SystemException {
        try {
            PermissionResourceClient client = clientServiceUtil.getPermissionResourceClient(
                    oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_PERMISSIONMANAGEMENT));

            Response response = client.getAll(search, pageNo, pageSize, sortBy, isAscending);
            return PermissionModelMapper.mapToPage((InputStream) response.getEntity()).getResults();
        } catch (ExtensionException | ProcessingException | MalformedURLException | WebApplicationException e){
            throw new SystemException(e);
        }
    }

    /**
     * Calls the requester to get an permission in the DB searching for the field Id
     * if not possible will refresh the access token and retry
     * @param id to be looked after
     * @return Optional List of Actions
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    public Optional<SystemPermission> getPermissionById(Long id) throws SystemException {
        try {
            return getPermissionByIdRequester(id);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getPermissionByIdRequester(id);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Gets an permission in the DB searching for the field Id
     * @param id to be looked after
     * @return Optional List of Actions
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private Optional<SystemPermission> getPermissionByIdRequester(Long id) throws SystemException {
        try {
            PermissionResourceClient client = clientServiceUtil.getPermissionResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_PERMISSIONMANAGEMENT));

            Response response = client.getById(id);
            SystemPermission permission = PermissionModelMapper.map((InputStream) response.getEntity());
            return Optional.ofNullable(permission);

        } catch (ExtensionException | ProcessingException | MalformedURLException | WebApplicationException e){
            throw new SystemException(e);
        }
    }

    /**
     * Calls the requester to retrieve from DB a collection containing permissions. The retrieval process will be
     * based on action identifier and (plus) a resource identifier. If not possible will refresh the access token ad retry.
     * @param actionId action identifier
     * @param resourceId resource identifier
     * @return a list of permissions found using the action and the resource id
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    public List<? extends SystemPermission> getPermissionByActionAndResource(Long actionId, Long resourceId) throws SystemException {
        try {
            return getPermissionByActionAndResourceRequester(actionId, resourceId);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getPermissionByActionAndResourceRequester(actionId, resourceId);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
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
    private List<? extends SystemPermission> getPermissionByActionAndResourceRequester(Long actionId, Long resourceId) throws SystemException {
        try {
            PermissionResourceClient client = clientServiceUtil.getPermissionResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_PERMISSIONMANAGEMENT));
            Response response = client.getPermissions(null, actionId, resourceId, null,true, true);
            return ListPermissionModelMapper.map((InputStream) response.getEntity());
        }
        catch (ExtensionException | ProcessingException | MalformedURLException | WebApplicationException e){
            throw new SystemException(e);
        }
    }

    /**
     * Calls the requester to get all the permissions in the DB searching for the field Name, if not possible will
     * refresh the access token and retry.
     * @param name to be looked after
     * @return Optional List of Permissions
     * @throws SystemException in case it founds multiple permissions or if URL is malformed
     */
    public Optional<SystemPermission> getPermissionByName(String name) throws SystemException {
        try {
            return getPermissionByNameRequester(name);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getPermissionByNameRequester(name);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Gets all the permissions in the DB searching for the field Name
     * @param name to be looked after
     * @return Optional List of Permissions
     * @throws SystemException in case it founds multiple permissions or if URL is malformed
     */
    private Optional<SystemPermission> getPermissionByNameRequester(String name) throws SystemException {
        try {
            PermissionResourceClient client = clientServiceUtil.getPermissionResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_PERMISSIONMANAGEMENT));

            Response response = client.getPermissions(name,null,null,null,true,true);
            List<? extends SystemPermission> list = ListPermissionModelMapper.map((InputStream) response.getEntity());
            if (list.size() == 1) {
                return Optional.ofNullable(list.get(0));
            } else {
                return Optional.empty();
            }
        } catch (ExtensionException | ProcessingException | MalformedURLException | WebApplicationException e){
            throw new SystemException(e);
        }
    }

    /**
     * Calls the requester to delete a permission
     * @param permissionId to be deleted
     * @return true in case of success
     * @throws SystemException in case it founds multiple permissions or if URL is malformed
     */
    @Override
    public boolean delete(long permissionId) throws SystemException {
        try {
            return deleteRequester(permissionId);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return deleteRequester(permissionId);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Requests to delete a permission
     * @param permissionId to be deleted
     * @return true in case of success
     * @throws SystemException in case it founds multiple permissions or if URL is malformed
     */
    private boolean deleteRequester(long permissionId) throws SystemException{
        PermissionResourceClient client;
        try {
            client = clientServiceUtil.getPermissionResourceClient(oaf.getProperty
                    (OAFProperties.SYSTEM_MS_ENDPOINT_PERMISSIONMANAGEMENT));

            Response response = client.delete(permissionId);
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            }
        } catch (ProcessingException | MalformedURLException | WebApplicationException e) {
            throw new SystemException(e);
        }
        return false;
    }

    /**
     * Calls the requester for a permission creation if not possible will refresh the access token and retry
     * @param permission information to be created
     * @return true in case of success
     * @throws SystemException in case it founds multiple permissions or if URL is malformed
     */
    @Override
    public boolean create(SystemPermission permission) throws SystemException {
        try {
            return createRequester(permission);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return createRequester(permission);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Requests a permission creation
     * @param permission information to be created
     * @return true in case of success
     * @throws SystemException in case it founds multiple permissions or if URL is malformed
     */
    private boolean createRequester(SystemPermission permission) throws SystemException {
        PermissionResourceClient client;
        try {
            client = clientServiceUtil.getPermissionResourceClient(oaf.getProperty
                    (OAFProperties.SYSTEM_MS_ENDPOINT_PERMISSIONMANAGEMENT));

            Response response = client.save((Permission) permission);
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            }
        } catch (MalformedURLException | ProcessingException | WebApplicationException e) {
            throw new SystemException(e);
        }
        return false;
    }

    /**
     * Calls the requester to validate if the permission exists in the DB if not possible
     * will refresh the access token and retry
     * @param permissionId to be searched
     * @return true in case of success
     * @throws SystemException in case it founds multiple permissions or if URL is malformed
     */
    @Override
    public boolean isPermissionExistent(Long permissionId, String permissionName) throws SystemException {
        try {
            return isPermissionExistentRequester(permissionId, permissionName);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return isPermissionExistentRequester(permissionId, permissionName);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Request to validate if the permission exists in the DB
     * @param permissionId to be searched
     * @return true in case of success
     * @throws SystemException in case it founds multiple permissions or if URL is malformed
     */
    private boolean isPermissionExistentRequester(Long permissionId, String permissionName) throws  SystemException {
        PermissionResourceClient client;
        try {
            client = clientServiceUtil.getPermissionResourceClient(oaf.getProperty
                    (OAFProperties.SYSTEM_MS_ENDPOINT_PERMISSIONMANAGEMENT));

            Response response = client.exists(permissionId, permissionName);
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            }
        } catch (MalformedURLException | ProcessingException | WebApplicationException e) {
            throw new SystemException(e);
        }
        return false;
    }

    /**
     * Calls the requester to calculate how many records are existent in the db if not possible will refresh
     * the access token and retry
     * @return the count of existent permissions.
     * @throws SystemException in case it founds multiple permissions or if URL is malformed
     */
    public Long getTotalRecordsCount() throws SystemException {
        try {
            return getTotalRecordsCountRequester();
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getTotalRecordsCountRequester();
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Will calculate how many records are existent in the db
     * @return the count of existent permissions.
     * @throws SystemException in case it founds multiple permissions or if URL is malformed
     */
    private Long getTotalRecordsCountRequester() throws SystemException {
        try {
            PermissionResourceClient client = clientServiceUtil.getPermissionResourceClient(oaf.getProperty
                    (OAFProperties.SYSTEM_MS_ENDPOINT_PERMISSIONMANAGEMENT));
            Response response = client.getTotalRecordsCount();
            return Long.parseLong(response.readEntity(String.class));

        } catch (ExtensionException | ProcessingException | MalformedURLException e){
            throw new SystemException(e);
        }
    }

    /**
     * Calls the requester to retrieve from DB a collection containing permissions. The retrieval process will be
     * based on a list containing permission identifiers. In case of JWT expiration, the process will be attempt once more
     * @param ids list of permission identifiers
     * @return a list of permissions found (matching the identifiers)
     * @throws SystemException in case of any found error
     */
    public List<? extends SystemPermission> getPermissionsByIds(List<Long> ids) throws SystemException {
        try {
            return getPermissionsByIdsRequester(ids);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getPermissionsByIdsRequester(ids);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Retrieves from DB a collection containing permissions. The retrieval process will be based on a list of idenfitiers
     * @param ids list of idenfitiers
     * @throws SystemException in case of any eorr
     */
    private List<? extends SystemPermission> getPermissionsByIdsRequester(List<Long> ids) throws SystemException {
        try {
            PermissionResourceClient client = clientServiceUtil.getPermissionResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_PERMISSIONMANAGEMENT));
            Response response = client.getPermissions(null, null, null, ids,true, true);
            return ListPermissionModelMapper.map((InputStream) response.getEntity());
        }
        catch (ExtensionException | ProcessingException | MalformedURLException | WebApplicationException e){
            throw new SystemException(e);
        }
    }

    /**
     * Calls a core method to retrieve the permission Id using the combination of resource and action as parameters.
     *  In case of JWT expiration, the process will be attempt once more.
     * @param resource resource name (Mandatory)
     * @param action action name (Mandatory)
     * @return Optional containing Id (If there is a permission for the informed parameter), otherwise a empty one
     * @throws SystemException  in case of not being able to find the information
     */
    public Optional<Long> getIdByResourceAndAction(String resource, String action) throws SystemException {
        try {
            return getIdByResourceAndActionRequester(resource, action);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getIdByResourceAndActionRequester(resource, action);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Core method that retrieves the permission Id using the combination of resource and action as parameters.
     * @param resource resource name (Mandatory)
     * @param action action name (Mandatory)
     * @return Optional containing Id (If there is a permission for the informed parameter), otherwise a empty one
     * @throws SystemException  in case of not being able to find the information
     */
    private Optional<Long> getIdByResourceAndActionRequester(String resource, String action) throws SystemException {
        try {
            PermissionResourceClient client = clientServiceUtil.getPermissionResourceClient(oaf.getProperty
                    (OAFProperties.SYSTEM_MS_ENDPOINT_PERMISSIONMANAGEMENT));
            Response response = client.getIdByResourceAndAction(resource, action);
            return Optional.of(response.readEntity(Long.class));
        }
        catch(NotFoundException nfe) {
            return Optional.empty();
        }
        catch (BadRequestException | InternalServerErrorException | ExtensionException | ProcessingException | MalformedURLException e){
            throw new SystemException(e);
        }
    }

    /**
     * Permission OAF getter
     * @return the permission active oaf session access
     */
    @Override
    public OAFAccess getOAF() {
        return oaf;
    }
}
