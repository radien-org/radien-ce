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
import io.radien.api.model.linked.authorization.SystemLinkedAuthorization;
import io.radien.api.service.linked.authorization.LinkedAuthorizationRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.exception.TokenExpiredException;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.rolemanagement.client.entities.LinkedAuthorization;
import io.radien.ms.rolemanagement.client.util.ClientServiceUtil;
import io.radien.ms.rolemanagement.client.util.ListLinkedAuthorizationModelMapper;
import org.apache.cxf.bus.extension.ExtensionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.List;

/**
 * Linked Authorization REST Service Client
 *
 * It means that the server will have a RESTful web service which would provide the required
 * functionality to the client. The client send's a request to the web service on the server.
 * The server would either reject the request or comply and provide an adequate response to the
 * client.
 *
 * @author Bruno Gama
 */
@Stateless
@Default
public class LinkedAuthorizationRESTServiceClient extends AuthorizationChecker implements LinkedAuthorizationRESTServiceAccess {

    private static final long serialVersionUID = -7001657359045981674L;

    private static final Logger log = LoggerFactory.getLogger(LinkedAuthorizationRESTServiceClient.class);

    private static final String UNABLE_TO_RECOVER_TOKEN="Unable to recover expiredToken";

    @Inject
    private OAFAccess oaf;

    @Inject
    private ClientServiceUtil linkedAuthorizationServiceUtil;

    /**
     * Calls requester to validate if some LinkedAuthorizationExists for tenant, permission, role and user exists
     * if not possible will refresh the access token and retry
     * @param tenant tenant identifier
     * @param permission permission identifier
     * @param role role identifier
     * @param userId user identifier
     * @return true if exists some LinkedAuthorization satisfying the informed parameter
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    @Override
    public boolean checkIfLinkedAuthorizationExists(Long tenant, Long permission, Long role, Long userId) throws SystemException {
        try {
            return checkIfLinkedAuthorizationExistsRequester(tenant, permission, role, userId);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return checkIfLinkedAuthorizationExistsRequester(tenant, permission, role, userId);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(UNABLE_TO_RECOVER_TOKEN);
            }
        }
    }

    /**
     * Validates if some LinkedAuthorizationExists for tenant, permission, role and user exists
     * @param tenant tenant identifier
     * @param permission permission identifier
     * @param role role identifier
     * @param userId user identifier
     * @return true if exists some LinkedAuthorization satisfying the informed parameter
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private boolean checkIfLinkedAuthorizationExistsRequester(Long tenant, Long permission, Long role, Long userId)
            throws SystemException {
        try {
            LinkedAuthorizationResourceClient client = linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));

            Response response = client.existsSpecificAssociation(tenant, permission, role, userId, true);
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            }
            log.info("LinkedAuthorizations not found for tenant, permission, role and user. Obtained status= {}",
                    response.getStatusInfo().getStatusCode());
        }
        catch (WebApplicationException e) {
            // In case of 404 status we just want to return false
            if (e.getResponse().getStatus() != Response.Status.NOT_FOUND.getStatusCode()) {
                throw e;
            }
        }
        catch (ExtensionException | ProcessingException | MalformedURLException e) {
            throw new SystemException(e);
        }
        return false;
    }

    /**
     * Calls the requester expecting to receive all the existent linked authorizations,
     * if not possible will refresh the access token and retry
     * @param pageNo of the information to be seen
     * @param pageSize number of records to be showed
     * @return a list of System Linked Authorizations
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    @Override
    public List<? extends SystemLinkedAuthorization> getAll(int pageNo, int pageSize) throws SystemException {
        try {
            return getAllRequester(pageNo, pageSize);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getAllRequester(pageNo, pageSize);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(UNABLE_TO_RECOVER_TOKEN);
            }
        }
    }

    /**
     * Will get all the existent linked authorizations,
     * @param pageNo of the information to be seen
     * @param pageSize number of records to be showed
     * @return a list of System Linked Authorizations
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private List<? extends SystemLinkedAuthorization> getAllRequester(int pageNo, int pageSize) throws SystemException {
        List<? extends SystemLinkedAuthorization> linkedAuthorizationsList = null;
        try {
            LinkedAuthorizationResourceClient client = linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));

            Response response = client.getAllAssociations(pageNo, pageSize);

            try (JsonReader jsonReader =Json.createReader(new StringReader(response.readEntity(String.class)))){
                JsonArray jsonArray = (JsonArray) jsonReader.readObject().get("results");
                linkedAuthorizationsList = LinkedAuthorizationFactory.convert(jsonArray);
            }
        }   catch (ExtensionException | ProcessingException | MalformedURLException es){
                throw new SystemException(es);
        }
        return linkedAuthorizationsList;
    }

    /**
     * Will request to calculate how many records are existent in the db
     * @return the count of existent tenants.
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
                throw new SystemException(UNABLE_TO_RECOVER_TOKEN);
            }
        }
    }

    /**
     * Will calculate how many records are existent in the db
     * @return the count of existent tenants.
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private Long getTotalRecordsCountRequester() throws SystemException {
        try {
            LinkedAuthorizationResourceClient client = linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));

            Response response = client.getTotalRecordsCount();
            return Long.parseLong(response.readEntity(String.class));

        } catch (ExtensionException | ProcessingException | MalformedURLException e){
            throw new SystemException(e.getMessage());
        }
    }


    /**
     * Will request to get all the Linked Authorization in the DB searching for the field role id if not possible will
     * refresh the access token and retry
     * @param role to be looked after
     * @return list of linked authorizations
     * @throws SystemException in case it founds multiple linked authorizations or if URL is malformed
     */
    @Override
    public List<? extends SystemLinkedAuthorization> getLinkedAuthorizationByRoleId(Long role) throws SystemException {
        try {
            return getLInkedAuthorizationRoleIdRequester(role);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getLInkedAuthorizationRoleIdRequester(role);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(UNABLE_TO_RECOVER_TOKEN);
            }
        }
    }

    /**
     * Gets all the Linked Authorization in the DB searching for the field role id
     * @param role to be looked after
     * @return list of linked authorizations
     * @throws SystemException in case it founds multiple linked authorizations or if URL is malformed
     */
    private List<? extends SystemLinkedAuthorization> getLInkedAuthorizationRoleIdRequester(Long role) throws SystemException {
        try {
            LinkedAuthorizationResourceClient client = linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
            Response response = client.getSpecificAssociation(null, null,role,null,false);
            return ListLinkedAuthorizationModelMapper.map((InputStream) response.getEntity());
        } catch (ExtensionException|ProcessingException | MalformedURLException e){
            throw new SystemException(e.getMessage());
        }
    }

    /**
     * Creates a request to create given linked authorization, if not possible will refresh the access token and retry
     * @param linkedAuthorization to be created
     * @return true if linked authorization has been created with success or false if not
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    public boolean create(SystemLinkedAuthorization linkedAuthorization) throws SystemException {
        try {
            return createRequester(linkedAuthorization);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return createRequester(linkedAuthorization);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(UNABLE_TO_RECOVER_TOKEN);
            }
        }
    }

    /**
     * Creates given linked authorization
     * @param linkedAuthorization to be created
     * @return true if linked authorization has been created with success or false if not
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private boolean createRequester(SystemLinkedAuthorization linkedAuthorization) throws SystemException {
        LinkedAuthorizationResourceClient client;
        try {
            client = linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(getOAF().getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
            Response response = client.saveAssociation((LinkedAuthorization) linkedAuthorization);
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            } else {
                String responseMessage = response.readEntity(String.class);
                log.error(responseMessage);
                return false;
            }
        } catch (ProcessingException | MalformedURLException e) {
            throw new SystemException(e.getMessage());
        }
    }

    /**
     * Creates a request to check if a given linked authorization exists,
     * if not possible will refresh the access token and retry
     * @param userId corresponds to the user identifier
     * @param tenantId corresponds to a tenant identifier (Optional parameter)
     * @param roleName corresponds to the role name
     * @return true if record has been found
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    @Override
    public Boolean isRoleExistentForUser(Long userId, Long tenantId, String roleName) throws SystemException {
        try {
            return isRoleExistentForUserRequester(userId, tenantId, roleName);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return isRoleExistentForUserRequester(userId, tenantId, roleName);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(UNABLE_TO_RECOVER_TOKEN);
            }
        }
    }

    /**
     * Checks if a given linked authorization exists or not
     * @param userId user id composing the linked authorization
     * @param tenantId tenant id composing the linked authorization
     * @param roleName role id composing the linked authorization
     * @return true if record has been found
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private Boolean isRoleExistentForUserRequester(Long userId, Long tenantId, String roleName) throws SystemException {
        try {
            LinkedAuthorizationResourceClient client = linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
            Response response = client.isRoleExistentForUser(userId, roleName, tenantId);
            return response.readEntity(Boolean.class);
        } catch (ExtensionException | ProcessingException | MalformedURLException e){
            throw new SystemException(e.getMessage());
        }
    }

    /**
     * Will request to get all the Linked Authorization in the DB searching for the field user id if not possible will
     * refresh the access token and retry
     * @param userId to be looked after
     * @return list of linked authorizations
     * @throws SystemException in case it founds multiple linked authorizations or if URL is malformed
     */
    @Override
    public List<? extends SystemLinkedAuthorization> getSpecificAssociationByUserId(Long userId) throws SystemException {
        try {
            return getSpecificAssociationByUserIdRequester(userId);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getSpecificAssociationByUserIdRequester(userId);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(UNABLE_TO_RECOVER_TOKEN);
            }
        }
    }

    /**
     * Gets all the Linked Authorization in the DB searching for the field user id
     * @param userId to be looked after
     * @return list of linked authorizations
     * @throws SystemException in case it founds multiple linked authorizations or if URL is malformed
     */
    private List<? extends SystemLinkedAuthorization> getSpecificAssociationByUserIdRequester(Long userId) throws SystemException {
        try {
            LinkedAuthorizationResourceClient client = linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
            Response response = client.getSpecificAssociation(null, null,null,userId,false);
            return ListLinkedAuthorizationModelMapper.map((InputStream) response.getEntity());
        } catch (ExtensionException|ProcessingException | MalformedURLException e){
            throw new SystemException(e.getMessage());
        }
    }

    /**
     * Will request delete ALL Linked Authorizations that exist in the DB for the following
     * parameters (tenant and user).
     * In case of JWT expiration will refresh the access token and retry
     * the operation
     * @param tenantId Tenant identifier
     * @param userId User identifier
     * @return SystemException in case of error
     */
    @Override
    public boolean deleteAssociations(Long tenantId, Long userId) throws SystemException {
        try {
            return dissociateTenantUserRequester(tenantId, userId);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return dissociateTenantUserRequester(tenantId, userId);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(UNABLE_TO_RECOVER_TOKEN);
            }
        }
    }

    /**
     * Delete ALL Linked Authorizations that exist in the DB for the following
     * parameters (tenant and user).
     * @param tenantId Tenant identifier
     * @param userId User identifier
     * @return SystemException in case of error
     */
    private boolean dissociateTenantUserRequester(Long tenantId, Long userId) throws SystemException {
        try {
            LinkedAuthorizationResourceClient client = linkedAuthorizationServiceUtil.
                    getLinkedAuthorizationResourceClient(getOAF().getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
            Response response = client.deleteAssociations(tenantId, userId);
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            } else {
                String responseMessage = response.readEntity(String.class);
                log.error(responseMessage);
                return false;
            }
        } catch (ExtensionException | ProcessingException | MalformedURLException e) {
            throw new SystemException(e.getMessage());
        }
    }

    /**
     * OAF linked authorization getter
     * @return the active linked authorization oaf
     */
    @Override
    public OAFAccess getOAF() {
        return oaf;
    }
}
