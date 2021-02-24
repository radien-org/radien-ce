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
package io.radien.ms.ecm.resource;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.ms.ecm.client.entities.I18NProperty;
import io.radien.ms.ecm.client.services.I18NPropertyResourceClient;
import io.radien.ms.ecm.client.services.SystemI18NPropertyService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author andresousa
 */

@Path("i18n")
@RequestScoped
public class I18NPropertyResource implements I18NPropertyResourceClient {
    @Inject
    private SystemI18NPropertyService propertyManager;
    @Inject
    private OAFAccess oafAccess;

    @Override
    public Response getMessage(String msg) {
        return Response.ok(propertyManager.getLocalizedMessage(msg)).build();
    }

    @Override
    public Response getProperty(String key) {
        return Response.ok(propertyManager.getByKey(key)).build();
    }

    @Override
    public Response add(I18NProperty property) {
        I18NProperty saved = propertyManager.save(property);
        return Response.created(UriBuilder.fromPath(String.valueOf(saved.getKey())).build())
                .build();
    }

    @Override
    public Response addAll(List<I18NProperty> propertyList) {
        List<I18NProperty> savedList = propertyManager.save(propertyList);
        return Response.created(
                UriBuilder.fromPath(savedList.stream().map(I18NProperty::getKey)
                        .collect(Collectors.joining(";"))).build()).build();
    }

    @Override
    public Response getKeys() {
        return Response.ok(propertyManager.getAll().stream().map(I18NProperty::getKey).collect(Collectors.toList())).build();
    }

    @Override
    public Response getProperties() { return Response.ok(propertyManager.getAll()).build(); }

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
