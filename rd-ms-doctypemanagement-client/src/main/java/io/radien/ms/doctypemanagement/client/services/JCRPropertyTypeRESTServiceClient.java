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
import io.radien.api.model.docmanagement.propertytype.SystemJCRPropertyType;
import io.radien.api.service.docmanagement.propertytype.JCRPropertyTypeRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.doctypemanagement.client.JCRPropertyTypeResponseExceptionMapper;
import io.radien.ms.doctypemanagement.client.entities.JCRPropertyType;
import io.radien.ms.doctypemanagement.client.util.JCRPropertyTypeClientServiceUtil;
import io.radien.ms.doctypemanagement.client.util.JCRPropertyTypeModelMapper;
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
@RegisterProvider(JCRPropertyTypeResponseExceptionMapper.class)
public class JCRPropertyTypeRESTServiceClient extends AuthorizationChecker implements JCRPropertyTypeRESTServiceAccess {
    private static final Logger log = LoggerFactory.getLogger(JCRPropertyTypeRESTServiceClient.class);

    @Inject
    private OAFAccess oaf;

    @Inject
    private JCRPropertyTypeClientServiceUtil clientService;


    @Override
    public Page<? extends SystemJCRPropertyType> getAll(String search, int pageNo, int pageSize, List<String> sortBy,
                                                 boolean isAscending) throws MalformedURLException, SystemException {
        return get(() -> {
            try {
                PropertyTypeResourceClient client = clientService.getJCRPropertyTypeResourceClient(getOAF()
                        .getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_DOCTYPEMANAGEMENT));
                Response response = client.getAll(search, pageNo, pageSize, sortBy, isAscending);
                return JCRPropertyTypeModelMapper.mapToPage((InputStream) response.getEntity());
            } catch (ExtensionException | ProcessingException | MalformedURLException e){
                throw new SystemException(e);
            }
        });
    }

    @Override
    public Optional<SystemJCRPropertyType> getJCRPropertyTypeById(Long id) throws SystemException {
        return get(() -> {
            try {
                PropertyTypeResourceClient client = clientService.getJCRPropertyTypeResourceClient(getOAF()
                        .getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_DOCTYPEMANAGEMENT));
                Response response = client.getById(id);
                return Optional.of(JCRPropertyTypeModelMapper.map((InputStream) response.getEntity()));
            } catch (ExtensionException|ProcessingException | MalformedURLException e){
                throw new SystemException(e);
            }
        });
    }

    @Override
    public boolean save(SystemJCRPropertyType propertyType) throws SystemException {
        return get(() -> {
            try {
            PropertyTypeResourceClient client = clientService.getJCRPropertyTypeResourceClient(getOAF()
                        .getProperty( OAFProperties.SYSTEM_MS_ENDPOINT_DOCTYPEMANAGEMENT));
                Response response = client.save((JCRPropertyType) propertyType);
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
    public boolean deleteJCRPropertyType(long id) throws SystemException {
        return get(() -> {
            try {
            PropertyTypeResourceClient client = clientService.getJCRPropertyTypeResourceClient(getOAF()
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
                PropertyTypeResourceClient client = clientService.getJCRPropertyTypeResourceClient(getOAF()
                        .getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_DOCTYPEMANAGEMENT));

                Response response = client.getTotalRecordsCount();
                return Long.parseLong(response.readEntity(String.class));

            } catch (ExtensionException | ProcessingException | MalformedURLException e){
                throw new SystemException(e);
            }
        });
    }

    /**
     * OAF jcrpropertytype getter
     * @return the active jcrpropertytype oaf
     */
    @Override
    public OAFAccess getOAF() {
        return oaf;
    }
}
