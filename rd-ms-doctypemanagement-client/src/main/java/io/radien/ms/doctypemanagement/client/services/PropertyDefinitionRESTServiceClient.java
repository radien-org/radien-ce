/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
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
package io.radien.ms.doctypemanagement.client.services;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.entity.Page;
import io.radien.api.model.docmanagement.propertydefinition.SystemPropertyDefinition;
import io.radien.api.service.docmanagement.propertydefinition.PropertyDefinitionRESTServiceAccess;
import io.radien.exception.RestResponseException;
import io.radien.exception.SystemException;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.doctypemanagement.client.PropertyDefinitionResponseExceptionMapper;
import io.radien.ms.doctypemanagement.client.entities.PropertyDefinition;
import io.radien.ms.doctypemanagement.client.util.ClientServiceUtil;
import io.radien.ms.doctypemanagement.client.util.PropertyDefinitionModelMapper;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import org.apache.cxf.bus.extension.ExtensionException;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestScoped
@RegisterProvider(PropertyDefinitionResponseExceptionMapper.class)
public class PropertyDefinitionRESTServiceClient extends AuthorizationChecker implements PropertyDefinitionRESTServiceAccess {
    private static final Logger log = LoggerFactory.getLogger(PropertyDefinitionRESTServiceClient.class);

    @Inject
    private OAFAccess oaf;

    @Inject
    private ClientServiceUtil clientService;

    @Override
    public Page<? extends SystemPropertyDefinition> getAll(String search, int pageNo, int pageSize, List<String> sortBy,
                                                           boolean isAscending) throws SystemException {
        return get(() -> {
            try {
                PropertyDefinitionResourceClient client = getClient();
                Response response = client.getAll(search, pageNo, pageSize, sortBy, isAscending);
                if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                    return PropertyDefinitionModelMapper.mapToPage((InputStream) response.getEntity());
                } else {
                    throw new RestResponseException(response.readEntity(String.class), response.getStatusInfo().getStatusCode());
                }
            } catch (MalformedURLException e){
                throw new SystemException(e);
            }
        });
    }

    @Override
    public Optional<SystemPropertyDefinition> getPropertyDefinitionById(Long id) throws SystemException {
        return get(() -> {
            try {
                PropertyDefinitionResourceClient client = clientService.getPropertyDefinitionClient(getOAF()
                        .getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_DOCTYPEMANAGEMENT));
                Response response = client.getById(id);
                if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                    return Optional.of(PropertyDefinitionModelMapper.map((InputStream) response.getEntity()));
                } else {
                    String entity = response.readEntity(String.class);
                    log.error(entity);
                    return Optional.empty();
                }
            } catch (ExtensionException|ProcessingException | MalformedURLException e){
                throw new SystemException(e);
            }
        });
    }

    @Override
    public boolean save(SystemPropertyDefinition propertyType) throws SystemException {
        return get(() -> {
            try {
            PropertyDefinitionResourceClient client = clientService.getPropertyDefinitionClient(getOAF()
                        .getProperty( OAFProperties.SYSTEM_MS_ENDPOINT_DOCTYPEMANAGEMENT));
                Response response = client.save((PropertyDefinition) propertyType);
                if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                    return true;
                } else {
                    String entity = response.readEntity(String.class);
                    log.error(entity);
                    return false;
                }
            } catch (ProcessingException | MalformedURLException e) {
                throw new SystemException(e);
            }
        });
    }

    @Override
    public boolean deletePropertyDefinition(long id) throws SystemException {
        return get(() -> {
            try {
            PropertyDefinitionResourceClient client = clientService.getPropertyDefinitionClient(getOAF()
                        .getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_DOCTYPEMANAGEMENT));
                Response response = client.delete(id);
                if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                    return true;
                } else {
                    String entity = response.readEntity(String.class);
                    log.error(entity);
                    return false;
                }
            } catch (ProcessingException | MalformedURLException e) {
                throw new SystemException(e);
            }
        });
    }

    public Long getTotalRecordsCount() throws SystemException {
        return get(() -> {
            try {
                PropertyDefinitionResourceClient client = clientService.getPropertyDefinitionClient(getOAF()
                        .getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_DOCTYPEMANAGEMENT));

                Response response = client.getTotalRecordsCount();
                if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                    return Long.parseLong(response.readEntity(String.class));
                } else {
                    throw new RestResponseException(response.readEntity(String.class), response.getStatusInfo().getStatusCode());
                }
            } catch (ExtensionException | ProcessingException | MalformedURLException e){
                throw new SystemException(e);
            }
        });
    }

    private PropertyDefinitionResourceClient getClient() throws MalformedURLException {
        return clientService.getPropertyDefinitionClient(
                getOAF().getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_DOCTYPEMANAGEMENT)
        );
    }

    @Override
    public OAFAccess getOAF() {
        return oaf;
    }
}
