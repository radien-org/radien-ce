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

package io.radien.ms.ecm.controller;

import io.radien.api.model.i18n.SystemI18NProperty;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.GenericErrorMessagesToResponseMapper;
import io.radien.exception.SystemException;
import io.radien.ms.ecm.client.entities.i18n.DeletePropertyFilter;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.radien.api.service.i18n.I18NServiceAccess;
import io.radien.ms.ecm.client.controller.I18NResource;

public class I18NResourceClient implements I18NResource {
    private static final Logger log = LoggerFactory.getLogger(I18NResourceClient.class);

    private static final long serialVersionUID = 7726133759137685545L;

    @Inject
    private I18NServiceAccess serviceAccess;

    @Override
    public Response getMessage(String key, String application, String language) {
        log.info("Retrieving translation for {} {} {}", key, application, language);
        try {
            String result = serviceAccess.getTranslation(key, language, application);
            return Response.ok().entity(result)
                    .build();
        } catch (SystemException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }

    }

    @Override
    public Response findByKeyAndApplication(String key, String application) {
        log.info("Retrieving property for {} {}", key, application);
        try {
            SystemI18NProperty property = serviceAccess.findByKeyAndApplication(key, application);
            return Response.ok().entity(property)
                    .build();
        } catch (SystemException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }

    }

    @Override
    public Response findAllByApplication(String application) {
        log.info("Retrieving all properties for {}", application);
        try {
            List<SystemI18NProperty> propertyList = serviceAccess.findAllByApplication(application);
            return Response.ok().entity(propertyList)
                    .build();
        } catch (SystemException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response deleteProperties(DeletePropertyFilter filter) {
        if(filter.getProperties() != null && !filter.getProperties().isEmpty()) {
            try {
                log.info("Deleting {} properties", filter.getProperties().size());
                serviceAccess.deleteProperties(filter.getProperties());
            } catch (SystemException e) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
            }
        } else if(!StringUtils.isEmpty(filter.getApplication())) {
            try {
                log.info("Deleting app properties for {}", filter.getApplication());
                serviceAccess.deleteApplicationProperties(filter.getApplication());
            } catch (SystemException e) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
            }
        } else {
            log.info("Invalid DeletePropertyFilter received");
            return GenericErrorMessagesToResponseMapper.getInvalidRequestResponse(GenericErrorCodeMessage.BAD_REQUEST.toString());
        }
        return Response.ok().build();
    }

    @Override
    public Response saveProperty(SystemI18NProperty property) {
        try {
            log.info("Saving new property {} for {}", property.getKey(), property.getApplication());
            serviceAccess.save(property);
            return Response.ok().build();
        } catch (SystemException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

}
