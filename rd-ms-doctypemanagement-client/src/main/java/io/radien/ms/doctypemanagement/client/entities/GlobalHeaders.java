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
package io.radien.ms.doctypemanagement.client.entities;

import io.radien.api.security.TokensPlaceHolder;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.CDI;

import javax.inject.Inject;
import javax.inject.Named;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;
@Default
@Named("docTypeManagementGlobalHeaders")
public class GlobalHeaders implements ClientHeadersFactory {

    @Inject
    private TokensPlaceHolder tokensPlaceHolder;

    @Override
    public MultivaluedMap<String, String> update(MultivaluedMap<String, String> incomingHeaders, MultivaluedMap<String, String> outgoingHeaders) {
        if (tokensPlaceHolder == null) {
            tokensPlaceHolder =  CDI.current().select(TokensPlaceHolder.class).get();
        }
        MultivaluedMap<String, String> result = new MultivaluedHashMap<>();

        if(tokensPlaceHolder.getAccessToken() != null) {
            result.add(HttpHeaders.AUTHORIZATION, "Bearer " + tokensPlaceHolder.getAccessToken());
        }
        result.putAll(incomingHeaders);
        result.putAll(outgoingHeaders);

        return result;
    }
}
