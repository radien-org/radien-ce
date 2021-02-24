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
 * See the License for the specific language governing actions and
 * limitations under the License.
 */
package io.radien.ms.permissionmanagement.client.services;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.model.permission.SystemAction;
import io.radien.api.service.permission.ActionRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.permissionmanagement.client.entities.Action;
import io.radien.ms.permissionmanagement.client.util.ActionModelMapper;
import io.radien.ms.permissionmanagement.client.util.ClientServiceUtil;
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
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;

/**
 * @author Newton Carvalho
 * Implementation for Rest Service Client regarding Action domain object
 */
@Stateless @Default
public class ActionRESTServiceClient implements ActionRESTServiceAccess {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(ActionRESTServiceClient.class);

    @Inject
    private OAFAccess oaf;

    @Inject
    private ClientServiceUtil clientServiceUtil;

    /**
     * Gets an Action in the DB searching for the field Id
     * @param id to be looked after
     * @return Optional List of Actions
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    public Optional<SystemAction> getActionById(Long id) throws SystemException {
        try {
            ActionResourceClient client = clientServiceUtil.getActionResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_PERMISSIONMANAGEMENT));

            Response response = client.getById(id);
            SystemAction action = ActionModelMapper.map((InputStream) response.getEntity());
            return Optional.ofNullable(action);

        } catch (ExtensionException | ProcessingException | MalformedURLException e){
            log.error(e.getMessage(),e);
            throw new SystemException(e);
        }
    }

    /**
     * Gets all the actions in the DB searching for the field Name
     *
     * @param name to be looked after
     * @return Optional List of Actions
     * @throws Exception in case it founds multiple actions or if URL is malformed
     */
    public Optional<SystemAction> getActionByName(String name) throws SystemException {
        try {
            ActionResourceClient client = clientServiceUtil.getActionResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_PERMISSIONMANAGEMENT));

            Response response = client.getActions(name,true,true);
            List<? extends SystemAction> list = map((InputStream) response.getEntity());
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
     * Creates given action
     * @param action to be created
     * @return true if action has been created with success or false if not
     * @throws MalformedURLException in case of URL specification
     */
    public boolean create(SystemAction action) throws SystemException {
    	ActionResourceClient client;
		try {
			client = clientServiceUtil.getActionResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_PERMISSIONMANAGEMENT));
		} catch (MalformedURLException e) {
			log.error(e.getMessage(),e);
            throw new SystemException(e);
		}
        try (Response response = client.save((Action)action)) {
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

    protected List<? extends SystemAction> map(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonArray jsonArray = jsonReader.readArray();
            return ActionFactory.convert(jsonArray);
        }
    }

    @Override
    public OAFAccess getOAF() {
        return oaf;
    }
}
