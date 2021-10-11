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
 * See the License for the specific language governing actions and
 * limitations under the License.
 */
package io.radien.ms.permissionmanagement.client.services;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemAction;
import io.radien.api.service.permission.ActionRESTServiceAccess;
import io.radien.exception.BadRequestException;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.InternalServerErrorException;
import io.radien.exception.NotFoundException;
import io.radien.exception.SystemException;
import io.radien.exception.TokenExpiredException;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.permissionmanagement.client.entities.Action;
import io.radien.ms.permissionmanagement.client.util.ActionModelMapper;
import io.radien.ms.permissionmanagement.client.util.ClientServiceUtil;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import org.apache.cxf.bus.extension.ExtensionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation for Rest Service Client regarding Action domain object
 * @author Newton Carvalho
 */
@RequestScoped
@Default
public class ActionRESTServiceClient extends AuthorizationChecker implements ActionRESTServiceAccess {

	private static final long serialVersionUID = 4416175112376323622L;

	private static final Logger log = LoggerFactory.getLogger(ActionRESTServiceClient.class);

    @Inject
    private OAFAccess oaf;

    @Inject
    private ClientServiceUtil clientServiceUtil;

    /**
     * Programmatically (via RestClientBuilder) creates an instance of a Action Rest Client
     * @return Instance of {@link ActionResourceClient} (Rest client)
     * @throws SystemException in case of any issue regarding url
     */
    private ActionResourceClient getActionResourceClient() throws SystemException {
        try {
            return clientServiceUtil.getActionResourceClient(oaf.getProperty
                    (OAFProperties.SYSTEM_MS_ENDPOINT_PERMISSIONMANAGEMENT));
        } catch (MalformedURLException m) {
            throw new SystemException(m);
        }
    }

    /**
     * Requests all Actions if not able then will try to refresh access token and retry
     * @param search field value to be searched or looked up
     * @param pageNo initial page number
     * @param pageSize max page size
     * @param sortBy sort by filter fields
     * @param isAscending ascending result list or descending
     * @return a list of existent system actions
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    @Override
    public Page<? extends SystemAction> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) throws SystemException {
        try {
            return getAllRequest(search, pageNo, pageSize, sortBy, isAscending);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getAllRequest(search, pageNo, pageSize, sortBy, isAscending);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Fetches all Actions
     * @param search field value to be searched or looked up
     * @param pageNo initial page number
     * @param pageSize max page size
     * @param sortBy sort by filter fields
     * @param isAscending ascending result list or descending
     * @return a list of existent system actions
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private Page<? extends SystemAction> getAllRequest(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) throws SystemException {
        try {
            ActionResourceClient client = clientServiceUtil.getActionResourceClient(oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_PERMISSIONMANAGEMENT));
            Response response = client.getAll(search, pageNo, pageSize, sortBy, isAscending);
            return ActionModelMapper.mapToPage((InputStream) response.getEntity());
        }

        catch (ExtensionException | ProcessingException | MalformedURLException e){
            throw new SystemException(e.getMessage());
        }
    }

    /**
     * Calls the get Action in the DB searching for the field Id if he cannot will refresh the access token
     * @param id to be looked after
     * @return Optional List of Actions
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    public Optional<SystemAction> getActionById(Long id) throws SystemException {
        try {
            return getActionByIdRequest(id);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getActionByIdRequest(id);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Gets an Action in the DB searching for the field Id
     * @param id to be looked after
     * @return Optional List of Actions
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private Optional<SystemAction> getActionByIdRequest(Long id) throws SystemException {
        try {
            ActionResourceClient client = clientServiceUtil.getActionResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_PERMISSIONMANAGEMENT));

            Response response = client.getById(id);
            SystemAction action = ActionModelMapper.map((InputStream) response.getEntity());
            return Optional.ofNullable(action);

        } catch (ExtensionException | ProcessingException | MalformedURLException e){
            throw new SystemException(e.getMessage());
        }
    }

    /**
     * Calls the requester to get all the actions in the DB searching for the field Name
     * if he cannot will refresh the access token
     * @param name to be looked after
     * @return Optional List of Actions
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    public Optional<SystemAction> getActionByName(String name) throws SystemException {
        try {
            return getActionByNameRequest(name);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getActionByNameRequest(name);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Gets all the actions in the DB searching for the field Name
     *
     * @param name to be looked after
     * @return Optional List of Actions
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private Optional<SystemAction> getActionByNameRequest(String name) throws SystemException {
        try {
            ActionResourceClient client = clientServiceUtil.getActionResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_PERMISSIONMANAGEMENT));

            Response response = client.getActions(name,null,true,true);
            List<? extends SystemAction> list = map((InputStream) response.getEntity());
            if (list.size() == 1) {
                return Optional.ofNullable(list.get(0));
            } else {
                return Optional.empty();
            }
        } catch (ExtensionException | ProcessingException | MalformedURLException e){
            throw new SystemException(e.getMessage());
        }
    }

    /**
     * Requests to create a given action
     * @param action to be created
     * @return true if action has been created with success or false if not
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    public boolean create(SystemAction action) throws SystemException {
        try {
            return createRequest(action);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return createRequest(action);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Creates given action
     * @param action to be created
     * @return true if action has been created with success or false if not
     * @throws SystemException  in case of any communication or processing issue regarding action rest api
     */
    private boolean createRequest(SystemAction action) throws SystemException{
        ActionResourceClient client = getActionResourceClient();
        try (Response response = client.create((Action)action)) {
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            } else {
                String entity = response.readEntity(String.class);
                log.error(entity);
                return false;
            }
        } catch (ProcessingException | BadRequestException | InternalServerErrorException e) {
            throw new SystemException(e.getMessage());
        }
    }

    /**
     * Requests to update a given action
     * @param action to be updated
     * @return true if action has been updated with success or false if not
     * @throws SystemException in case of any communication or processing issue regarding action rest api
     */
    public boolean update(SystemAction action) throws SystemException {
        try {
            return updateRequest(action);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return updateRequest(action);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Updates a given action
     * @param action to be updated
     * @return true if action has been updated with success or false if not
     * @throws SystemException in case of any communication or processing issue regarding action rest api
     */
    private boolean updateRequest(SystemAction action) throws SystemException{
        ActionResourceClient client = getActionResourceClient();
        try (Response response = client.update(action.getId(), (Action)action)) {
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            } else {
                String entity = response.readEntity(String.class);
                log.error(entity);
                return false;
            }
        } catch (ProcessingException | NotFoundException | BadRequestException | InternalServerErrorException e) {
            throw new SystemException(e.getMessage());
        }
    }

    /**
     * Calls the requester to retrieve from DB a collection containing actions. The retrieval process will be
     * based on a list containing action identifiers. In case of JWT expiration, the process will be attempt once more
     * @param ids list of action identifiers
     * @return a list of actions found (matching the identifiers)
     * @throws SystemException in case of any found error
     */
    public List<? extends SystemAction> getActionsByIds(List<Long> ids) throws SystemException {
        try {
            return getActionsByIdsRequester(ids);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getActionsByIdsRequester(ids);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Retrieves from DB a collection containing actions. The retrieval process will be based on a list of identifiers
     * @param ids list of identifiers
     * @throws SystemException in case of any error
     */
    private List<? extends SystemAction> getActionsByIdsRequester(List<Long> ids) throws SystemException {
        try {
            ActionResourceClient client = clientServiceUtil.getActionResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_PERMISSIONMANAGEMENT));
            Response response = client.getActions(null, ids,true, true);
            return map((InputStream) response.getEntity());
        }
        catch (ExtensionException | ProcessingException | MalformedURLException e){
            throw new SystemException(e);
        }
    }

    /**
     * Requests to delete a given action
     * @param actionId to be delete
     * @return true if action has been deleted with success or false if not
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    public boolean delete(long actionId) throws SystemException {
        try {
            return deleteRequest(actionId);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return deleteRequest(actionId);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Deletes given action
     * @param actionId to be delete
     * @return true if action has been deleted with success or false if not
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private boolean deleteRequest(long actionId) throws SystemException{
        ActionResourceClient client;
        try {
            client = clientServiceUtil.getActionResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_PERMISSIONMANAGEMENT));

            Response response = client.delete(actionId);
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            } else {
                String entity = response.readEntity(String.class);
                log.error(entity);
                return false;
            }
        } catch (ProcessingException | MalformedURLException e) {
            throw new SystemException(e.getMessage());
        }
    }

    /**
     * Mapper for a received input stream into a list of possible system action
     * @param is to be mapped
     * @return a list of system action information
     */
    protected List<? extends SystemAction> map(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonArray jsonArray = jsonReader.readArray();
            return ActionFactory.convert(jsonArray);
        }
    }

    /**
     * OAF action getter
     * @return the active action oaf
     */
    @Override
    public OAFAccess getOAF() {
        return oaf;
    }
}
