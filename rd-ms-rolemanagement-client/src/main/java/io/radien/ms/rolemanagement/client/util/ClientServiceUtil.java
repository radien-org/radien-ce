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
package io.radien.ms.rolemanagement.client.util;

import io.radien.ms.rolemanagement.client.exception.LinkedAuthorizationResponseExceptionMapper;
import io.radien.ms.rolemanagement.client.exception.RoleResponseExceptionMapper;
import io.radien.ms.rolemanagement.client.providers.LinkedAuthorizationMessageBodyWriter;
import io.radien.ms.rolemanagement.client.providers.RoleMessageBodyWriter;
import io.radien.ms.rolemanagement.client.services.LinkedAuthorizationResourceClient;
import io.radien.ms.rolemanagement.client.services.RoleResourceClient;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import javax.enterprise.context.RequestScoped;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Constructor for communication with the linked authorization and role clients
 *
 * @author Bruno Gama
 */
@RequestScoped
public class ClientServiceUtil {

    /**
     * Communication requester constructor for the role side
     * @param urlStr role resource client URL
     * @return a Role Resource Client that can perform multiple requests and with the correct exceptions, mappers and
     * message writers
     * @throws MalformedURLException in case of error in the given URL or communication cannot be performed
     */
    public RoleResourceClient getRoleResourceClient(String urlStr) throws MalformedURLException {
        URL url = new URL(urlStr);
        return RestClientBuilder.
                newBuilder()
                .baseUrl(url)
                .register(RoleResponseExceptionMapper.class)
                .register(RoleMessageBodyWriter.class)
                .build(RoleResourceClient.class);
    }

    /**
     * Communication requester constructor for the linked authorization side
     * @param urlStr linked authorization resource client URL
     * @return a Linked Authorization Resource Client that can perform multiple requests and
     * with the correct exceptions, mappers an message writers
     * @throws MalformedURLException in case of error in the given URL or communication cannot be performed
     */
    public LinkedAuthorizationResourceClient getLinkedAuthorizationResourceClient(String urlStr) throws MalformedURLException {
        URL url = new URL(urlStr);
        return RestClientBuilder.
                newBuilder()
                .baseUrl(url)
                .register(LinkedAuthorizationResponseExceptionMapper.class)
                .register(LinkedAuthorizationMessageBodyWriter.class)
                .build(LinkedAuthorizationResourceClient.class);
    }
}
