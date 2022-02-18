package io.radien.ms.ecm.controller;

import io.radien.api.model.i18n.SystemI18NProperty;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.GenericErrorMessagesToResponseMapper;
import io.radien.ms.ecm.client.entities.i18n.DeletePropertyFilter;
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
        return Response.ok().entity(serviceAccess.getTranslation(key, language, application))
            .build();
    }

    @Override
    public Response findByKeyAndApplication(String key, String application) {
        log.info("Retrieving property for {} {}", key, application);
        return Response.ok().entity(serviceAccess.findByKeyAndApplication(key, application))
                .build();
    }

    @Override
    public Response findAllByApplication(String application) {
        log.info("Retrieving all properties for {}", application);
        return Response.ok().entity(serviceAccess.findAllByApplication(application))
                .build();
    }

    @Override
    public Response deleteProperties(DeletePropertyFilter filter) {
        if(filter.getProperties() != null && !filter.getProperties().isEmpty()) {
            log.info("Deleting {} properties", filter.getProperties().size());
            serviceAccess.deleteProperties(filter.getProperties());
        } else if(!StringUtils.isEmpty(filter.getApplication())) {
            log.info("Deleting app properties for {}", filter.getApplication());
            serviceAccess.deleteApplicationProperties(filter.getApplication());
        } else {
            log.info("Invalid DeletePropertyFilter received");
            return GenericErrorMessagesToResponseMapper.getInvalidRequestResponse(GenericErrorCodeMessage.BAD_REQUEST.toString());
        }
        return Response.ok().build();
    }

    @Override
    public Response saveProperty(SystemI18NProperty property) {
        log.info("Saving new property {} for {}", property.getKey(), property.getApplication());
        serviceAccess.save(property);
        return Response.ok().build();
    }

}
