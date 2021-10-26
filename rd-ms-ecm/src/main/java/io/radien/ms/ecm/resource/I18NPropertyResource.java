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
package io.radien.ms.ecm.resource;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.ms.ecm.client.entities.I18NProperty;
import io.radien.ms.ecm.client.services.I18NPropertyResourceClient;
import io.radien.ms.ecm.client.services.SystemI18NPropertyService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.List;
import java.util.stream.Collectors;

/**
 * I18N Property Resource resource methods
 *
 * @author andresousa
 */
@Path("i18n")
@RequestScoped
public class I18NPropertyResource implements I18NPropertyResourceClient {

    @Inject
    private SystemI18NPropertyService propertyManager;
    @Inject
    private OAFAccess oafAccess;

    /**
     * Method to retrieve the I18N by a given message
     * @param msg to be found
     * @return a http request response
     */
    @Override
    public Response getMessage(String msg) {
        return Response.ok(propertyManager.getLocalizedMessage(msg)).build();
    }

    /**
     * Method to retrieve the I18N by a given property key
     * @param key to be found
     * @return a http request response
     */
    @Override
    public Response getProperty(String key) {
        return Response.ok(propertyManager.getByKey(key)).build();
    }

    /**
     * Request to create a given I18N property
     * @param property to be created
     * @return a http request response either with success or not depending if the I18N has been created
     */
    @Override
    public Response add(I18NProperty property) {
        I18NProperty saved = propertyManager.save(property);
        return Response.created(UriBuilder.fromPath(String.valueOf(saved.getKey())).build())
                .build();
    }

    /**
     * Request to create a given list of I18N properties
     * @param propertyList of all the I18N that has to be created
     * @return a http request response
     */
    @Override
    public Response addAll(List<I18NProperty> propertyList) {
        List<I18NProperty> savedList = propertyManager.save(propertyList);
        return Response.created(
                UriBuilder.fromPath(savedList.stream().map(I18NProperty::getKey)
                        .collect(Collectors.joining(";"))).build()).build();
    }

    /**
     * Request to retrieve all the I18N existent keys
     * @return a http request response with a list of all the I18N keys
     */
    @Override
    public Response getKeys() {
        return Response.ok(propertyManager.getAll().stream().map(I18NProperty::getKey).collect(Collectors.toList())).build();
    }

    /**
     * Request to retrieve all the I18N existent properties
     * @return a http request response with a list of all the I18N properties
     */
    @Override
    public Response getProperties() { return Response.ok(propertyManager.getAll()).build(); }

    /**
     * Request to initialize the I18N properties if the given secret is the correct one
     * @param secret to be compared, if matched will initialize the properties
     * @return a 200 http message if the property has been initialized with success
     */
    @Override
    public Response initializeProperties(String secret) {
        String correctSecret = oafAccess.getProperty(OAFProperties.SYSTEM_MS_SECRET_ECM);
        if(!correctSecret.equals(secret)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        propertyManager.initializeProperties();
        return Response.ok().build();
    }
}
