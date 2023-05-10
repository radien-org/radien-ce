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
package io.radien.ms.rolemanagement.client.util;

import io.radien.ms.rolemanagement.client.exception.RoleResponseExceptionMapper;
import io.radien.ms.rolemanagement.client.providers.RoleMessageBodyWriter;
import io.radien.ms.rolemanagement.client.providers.TenantRoleMessageBodyWriter;
import io.radien.ms.rolemanagement.client.providers.TenantRolePermissionMessageBodyWriter;
import io.radien.ms.rolemanagement.client.providers.TenantRoleUserMessageBodyWriter;
import io.radien.ms.rolemanagement.client.services.RoleResourceClient;
import io.radien.ms.rolemanagement.client.services.TenantRolePermissionResourceClient;
import io.radien.ms.rolemanagement.client.services.TenantRoleResourceClient;
import io.radien.ms.rolemanagement.client.services.TenantRoleUserResourceClient;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger log = LoggerFactory.getLogger(ClientServiceUtil.class);
    /**
     * Communication requester constructor for the role side
     * @param urlStr role resource client URL
     * @return a Role Resource Client that can perform multiple requests and with the correct exceptions, mappers and
     * message writers
     * @throws MalformedURLException in case of error in the given URL or communication cannot be performed
     */
    public RoleResourceClient getRoleResourceClient(String urlStr) throws MalformedURLException {
        URL url = new URL(urlStr);
        log.info(urlStr);
        return RestClientBuilder.
                newBuilder()
                .baseUrl(url)
                .register(RoleResponseExceptionMapper.class)
                .register(RoleMessageBodyWriter.class)
                .build(RoleResourceClient.class);
    }

    /**
     * Communication requester constructor for the tenant role side
     * @param urlStr Tenant Role Resource client URL
     * @return a Tenant Role Resource Client that can perform multiple requests and
     * with the correct exceptions, mappers an message writers
     * @throws MalformedURLException in case of error in the given URL or communication cannot be performed
     */
    public TenantRoleResourceClient getTenantResourceClient(String urlStr) throws MalformedURLException {
        URL url = new URL(urlStr);
        log.info(urlStr);
        return RestClientBuilder.
                newBuilder()
                .baseUrl(url)
                .register(RoleResponseExceptionMapper.class)
                .register(TenantRoleMessageBodyWriter.class)
                .build(TenantRoleResourceClient.class);
    }

    /**
     * Communication requester constructor for the tenant role user side
     * @param urlStr Tenant Role Resource client URL
     * @return a Tenant Role Resource Client that can perform multiple requests and
     * with the correct exceptions, mappers an message writers
     * @throws MalformedURLException in case of error in the given URL or communication cannot be performed
     */
    public TenantRoleUserResourceClient getTenantRoleUserResourceClient(String urlStr) throws MalformedURLException {
        URL url = new URL(urlStr);
        log.info(urlStr);
        return RestClientBuilder.
                newBuilder()
                .baseUrl(url)
                .register(RoleResponseExceptionMapper.class)
                .register(TenantRoleUserMessageBodyWriter.class)
                .build(TenantRoleUserResourceClient.class);
    }

    /**
     * Communication requester constructor for the tenant role permission side
     * @param urlStr Tenant Role Resource client URL
     * @return a Tenant Role Permission Resource Client that can perform multiple requests and
     * with the correct exceptions, mappers an message writers
     * @throws MalformedURLException in case of error in the given URL or communication cannot be performed
     */
    public TenantRolePermissionResourceClient getTenantRolePermissionResourceClient(String urlStr) throws MalformedURLException {
        URL url = new URL(urlStr);
        log.info(urlStr);
        return RestClientBuilder.
                newBuilder()
                .baseUrl(url)
                .register(RoleResponseExceptionMapper.class)
                .register(TenantRolePermissionMessageBodyWriter.class)
                .build(TenantRolePermissionResourceClient.class);
    }
}
