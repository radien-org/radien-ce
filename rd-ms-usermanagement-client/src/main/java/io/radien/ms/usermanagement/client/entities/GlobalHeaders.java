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
package io.radien.ms.usermanagement.client.entities;

import io.radien.api.security.TokensPlaceHolder;
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

@Default @Named
public class GlobalHeaders implements ClientHeadersFactory {
    @Inject
    private TokensPlaceHolder tokensPlaceHolder;

    @Override
    public MultivaluedMap<String, String> update(MultivaluedMap<String, String> incomingHeaders, MultivaluedMap<String, String> outgoingHeaders) {
        //TODO: Understand why standard injection is not working
        tokensPlaceHolder =  CDI.current().select(TokensPlaceHolder.class).get();
        MultivaluedMap<String, String> result = new MultivaluedHashMap<>();

        if(tokensPlaceHolder.getAccessToken() != null) {
            result.add(HttpHeaders.AUTHORIZATION, "Bearer " + tokensPlaceHolder.getAccessToken());
        }
        result.putAll(incomingHeaders);
        result.putAll(outgoingHeaders);

        return result;
    }
}
