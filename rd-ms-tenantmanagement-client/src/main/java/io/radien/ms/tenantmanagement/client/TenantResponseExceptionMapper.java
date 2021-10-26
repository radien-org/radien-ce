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
package io.radien.ms.tenantmanagement.client;

import io.radien.exception.ModelResponseExceptionMapper;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
/**
 * Tenant/Contract Exception mapper
 *
 * @author Bruno Gama
 */
@Provider
public class TenantResponseExceptionMapper extends ModelResponseExceptionMapper {

    /**
     * Validates if by a given status code the error message can be handle by the following mapper
     * @param statusCode to be validated
     * @param headers to be passed
     * @return true in case handler can handle exception
     */
    public boolean tenantHandles(int statusCode, MultivaluedMap<String, Object> headers) {
        return handles(statusCode, headers);
    }

    /**
     * Throws the correct permission exception by the given response
     * @param response message to be validated
     * @return a exception
     */
    public Exception tenantToThrowable(Response response) {
        return toThrowable(response);
    }
}