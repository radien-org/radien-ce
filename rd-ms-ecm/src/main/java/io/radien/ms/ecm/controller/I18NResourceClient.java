package io.radien.ms.ecm.controller;

import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.GenericErrorMessagesToResponseMapper;
import io.radien.ms.ecm.client.entities.i18n.DeletePropertyFilter;
import io.radien.ms.ecm.client.entities.i18n.I18NProperty;
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
        return Response.ok().entity(serviceAccess.getTranslation(key, language, application))
            .build();
    }

    @Override
    public Response getProperty(String key, String application) {
        return Response.ok().entity(serviceAccess.findByKeyAndApplication(key, application))
                .build();
    }

    @Override
    public Response deleteProperties(DeletePropertyFilter filter) {
        if(filter.getProperties() != null && !filter.getProperties().isEmpty()) {
            serviceAccess.deleteProperties(filter.getProperties());
        } else if(!StringUtils.isEmpty(filter.getApplication())) {
            serviceAccess.deleteApplicationProperties(filter.getApplication());
        } else {
            return GenericErrorMessagesToResponseMapper.getInvalidRequestResponse(GenericErrorCodeMessage.BAD_REQUEST.toString());
        }
        return Response.ok().build();
    }

    @Override
    public Response saveProperty(I18NProperty property) {
        serviceAccess.save(property);
        return Response.ok().build();
    }

}
