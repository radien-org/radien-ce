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
package io.radien.api.service.role.exception;

import javax.ws.rs.core.Response;

/**
 * Exception to express inconsistencies regarding Tenant Role domain object.
 * In this case, the purpose is to warn that a Tenant Role could not
 * be found.
 * @author Newton Carvalho
 */
public class TenantRoleNotFoundException extends RoleException {

    /**
     * Tenant Role exception constructor by a given message
     *
     * @param message to create the tenant role exception with
     */
    public TenantRoleNotFoundException(String message) {
        super(message, Response.Status.NOT_FOUND);
    }
}
