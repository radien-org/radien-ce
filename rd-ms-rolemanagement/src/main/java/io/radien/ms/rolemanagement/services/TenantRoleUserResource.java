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
package io.radien.ms.rolemanagement.services;

import io.radien.api.service.tenantrole.TenantRoleUserServiceAccess;
import io.radien.exception.GenericErrorMessagesToResponseMapper;
import io.radien.ms.rolemanagement.client.services.TenantRoleUserResourceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

/**
 * Resource implementation responsible for deal with operations
 * regarding Tenant Role User associations domain object
 * @author Newton Carvalho
 */
@RequestScoped
public class TenantRoleUserResource implements TenantRoleUserResourceClient {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Inject
    private TenantRoleUserServiceAccess tenantRoleUserServiceAccess;

    /**
     * Retrieves TenantRoleUser association using pagination approach
     * (in other words, retrieves the Users associations that exist for a TenantRole)
     * @param tenantRoleId identifier for a TenantRole
     * @param pageNo page number
     * @param pageSize page size
     * @return In case of successful operation returns OK (http status 200)
     * and a Page containing TenantRole associations (Chunk/Portion compatible
     * with parameter Page number and Page size).<br>
     * Otherwise, in case of operational error, returns Internal Server Error (500)
     */
    @Override
    public Response getAll(Long tenantRoleId, int pageNo, int pageSize) {
        log("Retrieving tenant role users");
        try {
            return Response.ok().entity(tenantRoleUserServiceAccess.
                    getAll(tenantRoleId, pageNo, pageSize)).build();
        } catch (Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }

    /**
     * Utility method to log messages (in a cleaner approach)
     * @param msg message to be logged
     * @param params message params
     */
    private void log(String msg, Object... params) {
        if (msg != null) {
            String formattedMsg = params != null ? String.format(msg, params) : msg;
            log.error(formattedMsg);
        }
    }
}
