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
package io.radien.ms.rolemanagement.client.entities;

import io.radien.api.security.TokensPlaceHolder;
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

/**
 * This interface is intended for generating or propagating HTTP headers. It is
 * invoked by the MP Rest Client implementation before invoking any entity
 * providers on the outbound processing chain. It contains a single method,
 * update which takes parameters of headers passed in from the
 * incoming JAX-RS request (if applicable, if not, this will be an empty map)
 * and a read-only map of headers specified by ClientHeaderParam or
 * HeaderParam annotations on the client interface.
 *
 *  @author Bruno Gama
 */
@Default
@Named("roleGlobalHeaders")
public class GlobalHeaders implements ClientHeadersFactory {

    @Inject
    private TokensPlaceHolder tokensPlaceHolder;

    /**
     * Updates the HTTP headers to send to the remote service. Note that providers
     * on the outbound processing chain could further update the headers.
     *
     * @param incomingHeaders - the map of headers from the inbound JAX-RS request. This will
     * be an empty map if the associated client interface is not part of a JAX-RS request.
     * @param outgoingHeaders - the read-only map of header parameters specified on the
     * client interface.
     * @return a map of HTTP headers to merge with the clientOutgoingHeaders to be sent to
     * the remote service.
     */
    @Override
    public MultivaluedMap<String, String> update(MultivaluedMap<String, String> incomingHeaders, MultivaluedMap<String, String> outgoingHeaders) {
        //TODO: Understand why standard injection is not working
        if (tokensPlaceHolder == null) {
            tokensPlaceHolder = CDI.current().select(TokensPlaceHolder.class).get();
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
