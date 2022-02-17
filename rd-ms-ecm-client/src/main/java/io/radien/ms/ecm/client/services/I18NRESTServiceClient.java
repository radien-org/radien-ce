/*
 *
 *  * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
 *  * <p>
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  * <p>
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  * <p>
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package io.radien.ms.ecm.client.services;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.model.i18n.SystemI18NProperty;
import io.radien.api.service.i18n.I18NRESTServiceAccess;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.SystemException;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.ecm.client.controller.I18NResource;
import io.radien.ms.ecm.client.util.ClientServiceUtil;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestScoped
public class I18NRESTServiceClient extends AuthorizationChecker implements I18NRESTServiceAccess {
    private static final Logger log = LoggerFactory.getLogger(I18NRESTServiceClient.class);

    @Inject
    private ClientServiceUtil clientServiceUtil;
    @Inject
    private OAFAccess oaf;

    @Override
    public String getTranslation(String key, String language, String application) throws SystemException {
        return get(() -> {
            try {
                I18NResource client = getClient();
                Response response = client.getMessage(key, application, language);
                String entity = response.readEntity(String.class);
                if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                    return entity;
                } else {
                    log.error(entity);
                    return null;
                }
            } catch (IOException e) {
                throw new SystemException(GenericErrorCodeMessage.ERROR_RETRIEVING_TRANSLATION.toString(key, language), e);
            }
        });
    }

    @Override
    public boolean save(SystemI18NProperty property) throws SystemException{
        return get(() -> {
            try {
                I18NResource client = getClient();
                Response response = client.saveProperty(property);
                if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                    return true;
                } else {
                    String entity = response.readEntity(String.class);
                    log.error(entity);
                    return false;
                }
            } catch (IOException e) {
                throw new SystemException(GenericErrorCodeMessage.ERROR_SAVING_I18N_PROPERTY.toString(), e);
            }
        });
    }

    @Override
    public SystemI18NProperty findByKeyAndApplication(String key, String application) throws SystemException{
        return get(() -> {
            try {
                I18NResource client = getClient();
                Response response = client.findByKeyAndApplication(key, application);
                if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                    return I18NPropertyMapper.map((InputStream) response.getEntity());
                } else {
                    String entity = response.readEntity(String.class);
                    log.error(entity);
                    return null;
                }
            } catch (IOException | ParseException | java.text.ParseException e) {
                throw new SystemException(GenericErrorCodeMessage.ERROR_RETRIEVING_I18N_PROPERTY.toString(key), e);
            }
        });
    }

    @Override
    public List<SystemI18NProperty> findAllByApplication(String application) throws SystemException{
        return get(() -> {
            try {
                I18NResource client = getClient();
                Response response = client.findAllByApplication(application);
                if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                    return I18NPropertyMapper.mapArray((InputStream) response.getEntity());
                } else {
                    String entity = response.readEntity(String.class);
                    log.error(entity);
                    return new ArrayList<>();
                }
            } catch (IOException | ParseException | java.text.ParseException e) {
                throw new SystemException(GenericErrorCodeMessage.ERROR_RETRIEVING_I18N_APPLICATION_PROPERTIES.toString(application), e);
            }
        });
    }

    private I18NResource getClient() throws MalformedURLException {
        return clientServiceUtil.getI18NResourceClient(oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ECM));
    }

    @Override
    public OAFAccess getOAF() {
        return oaf;
    }
}
