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
import io.radien.api.security.TokensPlaceHolder;
import io.radien.api.service.linked.authorization.LinkedAuthorizationRESTServiceAccess;
import io.radien.exception.SystemException;
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
import javax.json.JsonString;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Bruno Gama
 */
@Stateless @Default
public class LinkedAuthorizationRESTServiceClient implements LinkedAuthorizationRESTServiceAccess {

    private static final long serialVersionUID = -7001657359045981674L;

    private static final Logger log = LoggerFactory.getLogger(LinkedAuthorizationRESTServiceClient.class);

    @Inject
    private OAFAccess oaf;

    @Inject
    private ClientServiceUtil linkedAuthorizationServiceUtil;

    /**
     * Check if some LinkedAuthorizationExists for tenant, permission, role and user
     * @param tenant tenant identifier
     * @param permission permission identifier
     * @param role role identifier
     * @param userId user identifier
     * @return true if exists some LinkedAuthorization satisfying the informed parameter
     */
    @Override
    public boolean checkIfLinkedAuthorizationExists(Long tenant, Long permission, Long role, Long userId) {

        try {
            LinkedAuthorizationResourceClient client = linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));

            Response response = client.existsSpecificAssociation(tenant, permission, role, userId, true);
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            }
            log.info("LinkedAuthorizations not found for tenant, permission, role and user. Obtained status= {}", response.getStatusInfo().getStatusCode());
        }
        catch (WebApplicationException e) {
            // In case of 404 status we just want to return false
            if (e.getResponse().getStatus() != Response.Status.NOT_FOUND.getStatusCode()) {
                throw e;
            }
        }
        catch (Exception e) {
            log.info("Error checking if LinkedAuthorizations exists for tenant, permission, role and user");
            throw new ProcessingException(e);
        }
        return false;
    }

    @Override
    public List<? extends SystemLinkedAuthorization> getAll(int pageNo, int pageSize) throws MalformedURLException, ParseException {
        List<? extends SystemLinkedAuthorization> linkedAuthorizationsList = null;
        try {
            LinkedAuthorizationResourceClient client = linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));

            Response response = client.getAllAssociations(pageNo, pageSize);

            JsonArray jsonArray = (JsonArray) Json.createReader(new StringReader(response.readEntity(String.class))).readObject().get("results");
            linkedAuthorizationsList = LinkedAuthorizationFactory.convert(jsonArray);
        }   catch (ExtensionException | ProcessingException | MalformedURLException es){
            throw es;
        }
        return linkedAuthorizationsList;
    }

    /**
     * Will calculate how many records are existent in the db
     * @return the count of existent tenants.
     */
    public Long getTotalRecordsCount() throws SystemException {
        try {
            LinkedAuthorizationResourceClient client = linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));

            Response response = client.getTotalRecordsCount();
            return Long.parseLong(response.readEntity(String.class));

        } catch (ExtensionException | ProcessingException | MalformedURLException e){
            throw new SystemException(e);
        }
    }


    /**
     * Gets all the Linked Authorization in the DB searching for the field role id
     *
     * @param role to be looked after
     * @return list of linked authorizations
     * @throws SystemException in case it founds multiple linked authorizations or if URL is malformed
     */
    @Override
    public List<? extends SystemLinkedAuthorization> getLinkedAuthorizationByRoleId(Long role) throws SystemException {
        try {
            LinkedAuthorizationResourceClient client = linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
            Response response = client.getSpecificAssociation(null, null,role,null,false);
            return ListLinkedAuthorizationModelMapper.map((InputStream) response.getEntity());
        } catch (ExtensionException|ProcessingException | MalformedURLException e){
            throw new SystemException(e);
        }
    }

    /**
     * Creates given linked authorization
     * @param linkedAuthorization to be created
     * @return true if linked authorization has been created with success or false if not
     * @throws MalformedURLException in case of URL specification
     */
    public boolean create(SystemLinkedAuthorization linkedAuthorization) throws SystemException {
        LinkedAuthorizationResourceClient client;
        try {
            client = linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(getOAF().getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
        } catch (MalformedURLException e) {
            throw new SystemException(e);
        }
        try (Response response = client.saveAssociation((LinkedAuthorization) linkedAuthorization)) {
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

    @Override
    public Boolean isRoleExistentForUser(Long userId, Long tenantId, String roleName) throws SystemException {
        try {
            LinkedAuthorizationResourceClient client = linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
            Response response = client.isRoleExistentForUser(userId, roleName, tenantId);
            return response.readEntity(Boolean.class);
        } catch (ExtensionException|ProcessingException | MalformedURLException e){
            throw new SystemException(e);
        }
    }

    @Override
    public OAFAccess getOAF() {
        return oaf;
    }
}
