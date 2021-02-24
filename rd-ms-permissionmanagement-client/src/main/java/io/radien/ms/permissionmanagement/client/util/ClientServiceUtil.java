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
package io.radien.ms.permissionmanagement.client.util;

import io.radien.ms.permissionmanagement.client.providers.ActionMessageBodyWriter;
import io.radien.ms.permissionmanagement.client.providers.PermissionMessageBodyWriter;
import io.radien.ms.permissionmanagement.client.providers.ResourceMessageBodyWriter;
import io.radien.ms.permissionmanagement.client.services.ActionResourceClient;
import io.radien.ms.permissionmanagement.client.services.ResourceResourceClient;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import io.radien.ms.permissionmanagement.client.PermissionResponseExceptionMapper;
import io.radien.ms.permissionmanagement.client.services.PermissionResourceClient;

import javax.enterprise.context.RequestScoped;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Bruno Gama
 * Utility class that produces Rest Clients for Permission and Action
 */
@RequestScoped
public class ClientServiceUtil {

    /**
     * Gets a Rest Client for Permission
     * @param urlStr url of rest endpoint
     * @return a client form the permissions
     * @throws MalformedURLException in case of any url issue
     */
    public PermissionResourceClient getPermissionResourceClient(String urlStr) throws MalformedURLException {
        URL url = new URL(urlStr);
        return RestClientBuilder.
                newBuilder()
                .baseUrl(url)
                .register(PermissionResponseExceptionMapper.class)
                .register(PermissionMessageBodyWriter.class)
                .build(PermissionResourceClient.class);
    }

    /**
     * Gets a Rest Client for Action
     * @param urlStr url of rest endpoint
     * @return
     * @throws MalformedURLException
     */
    public ActionResourceClient getActionResourceClient(String urlStr) throws MalformedURLException {
        URL url = new URL(urlStr);
        return RestClientBuilder.
                newBuilder()
                .baseUrl(url)
                .register(PermissionResponseExceptionMapper.class)
                .register(ActionMessageBodyWriter.class)
                .build(ActionResourceClient.class);
    }

    /**
     * Gets a Rest Client for Resource
     * @param urlStr url of rest endpoint
     * @return
     * @throws MalformedURLException
     */
    public ResourceResourceClient getResourceResourceClient(String urlStr) throws MalformedURLException {
        URL url = new URL(urlStr);
        return RestClientBuilder.
                newBuilder()
                .baseUrl(url)
                .register(PermissionResponseExceptionMapper.class)
                .register(ResourceMessageBodyWriter.class)
                .build(ResourceResourceClient.class);
    }
}
