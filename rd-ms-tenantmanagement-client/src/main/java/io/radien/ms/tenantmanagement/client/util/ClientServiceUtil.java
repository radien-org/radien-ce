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
package io.radien.ms.tenantmanagement.client.util;

import io.radien.ms.tenantmanagement.client.ResponseExceptionMapper;
import io.radien.ms.tenantmanagement.client.providers.ContractMessageBodyWriter;
import io.radien.ms.tenantmanagement.client.providers.TenantMessageBodyWriter;
import io.radien.ms.tenantmanagement.client.services.ContractResourceClient;
import io.radien.ms.tenantmanagement.client.services.TenantResourceClient;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Bruno Gama
 */
@RequestScoped
@Named("ContractClientServiceUtil")
public class ClientServiceUtil {

    /**
     * Gets a Rest Client for Contracts
     * @param urlStr url of rest endpoint
     * @return a client from the contract
     * @throws MalformedURLException in case of any url issue
     */
    public ContractResourceClient getContractResourceClient(String urlStr) throws MalformedURLException {
        URL url = new URL(urlStr);
        return RestClientBuilder.
                newBuilder()
                .baseUrl(url)
                .register(ResponseExceptionMapper.class)
                .register(ContractMessageBodyWriter.class)
                .build(ContractResourceClient.class);
    }

    public TenantResourceClient getTenantResourceClient(String urlStr) throws MalformedURLException {
        URL url = new URL(urlStr);
        return RestClientBuilder.
                newBuilder()
                .baseUrl(url)
                .register(ResponseExceptionMapper.class)
                .register(TenantMessageBodyWriter.class)
                .build(TenantResourceClient.class);
    }
}
