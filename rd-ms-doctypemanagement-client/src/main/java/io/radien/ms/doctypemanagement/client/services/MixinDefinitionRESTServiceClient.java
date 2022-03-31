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
import io.radien.api.model.docmanagement.mixindefinition.SystemMixinDefinition;
import io.radien.api.service.docmanagement.mixindefinition.MixinDefinitionRESTServiceAccess;
import io.radien.exception.RestResponseException;
import io.radien.exception.SystemException;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.doctypemanagement.client.PropertyDefinitionResponseExceptionMapper;
import io.radien.ms.doctypemanagement.client.entities.MixinDefinitionDTO;
import io.radien.ms.doctypemanagement.client.util.ClientServiceUtil;
import io.radien.ms.doctypemanagement.client.util.MixinDefinitionModelMapper;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.ParseException;
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
public class MixinDefinitionRESTServiceClient extends AuthorizationChecker implements MixinDefinitionRESTServiceAccess {
    private static final Logger log = LoggerFactory.getLogger(MixinDefinitionRESTServiceClient.class);

    @Inject
    private OAFAccess oaf;

    @Inject
    private ClientServiceUtil clientService;

    @Override
    public Page<? extends SystemMixinDefinition> getAll(String search, int pageNo, int pageSize, List<String> sortBy,
                                                           boolean isAscending) throws SystemException {
        return get(() -> {
            try {
                MixinDefinitionResourceClient client = getClient();
                Response response = client.getAll(search, pageNo, pageSize, sortBy, isAscending);
                if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                    return MixinDefinitionModelMapper.mapToPage((InputStream) response.getEntity());
                } else {
                    throw new RestResponseException(response.readEntity(String.class), response.getStatusInfo().getStatusCode());
                }
            } catch (MalformedURLException e){
                throw new SystemException(e);
            }
        });
    }

    @Override
    public Optional<SystemMixinDefinition> getMixinDefinitionById(Long id) throws SystemException {
        return get(() -> {
            try {
                MixinDefinitionResourceClient client = clientService.getMixinDefinitionClient(getOAF()
                        .getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_DOCTYPEMANAGEMENT));
                Response response = client.getById(id);
                if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                    return Optional.of(MixinDefinitionModelMapper.map((InputStream) response.getEntity()));
                } else {
                    String entity = response.readEntity(String.class);
                    log.error(entity);
                    return Optional.empty();
                }
            } catch (ExtensionException | ProcessingException | MalformedURLException | ParseException e){
                throw new SystemException(e);
            }
        });
    }

    @Override
    public boolean save(SystemMixinDefinition mixinDefinition) throws SystemException {
        return get(() -> {
            try {
            MixinDefinitionResourceClient client = clientService.getMixinDefinitionClient(getOAF()
                        .getProperty( OAFProperties.SYSTEM_MS_ENDPOINT_DOCTYPEMANAGEMENT));
                Response response = client.save((MixinDefinitionDTO) mixinDefinition);
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
    public boolean deleteMixinDefinition(long id) throws SystemException {
        return get(() -> {
            try {
            MixinDefinitionResourceClient client = clientService.getMixinDefinitionClient(getOAF()
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
                MixinDefinitionResourceClient client = clientService.getMixinDefinitionClient(getOAF()
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

    private MixinDefinitionResourceClient getClient() throws MalformedURLException {
        return clientService.getMixinDefinitionClient(
                getOAF().getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_DOCTYPEMANAGEMENT)
        );
    }

    @Override
    public OAFAccess getOAF() {
        return oaf;
    }
}
