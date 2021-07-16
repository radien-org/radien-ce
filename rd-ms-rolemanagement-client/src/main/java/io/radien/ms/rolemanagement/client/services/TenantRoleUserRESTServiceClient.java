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
package io.radien.ms.rolemanagement.client.services;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.entity.Page;
import io.radien.api.model.tenantrole.SystemTenantRoleUser;
import io.radien.api.service.tenantrole.TenantRoleUserRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.exception.TokenExpiredException;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.rolemanagement.client.exception.InternalServerErrorException;
import io.radien.ms.rolemanagement.client.util.ClientServiceUtil;
import io.radien.ms.rolemanagement.client.util.TenantRoleUserModelMapper;
import org.apache.cxf.bus.extension.ExtensionException;

import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.net.MalformedURLException;

import static io.radien.exception.GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN;

/**
 * Tenant Role REST Service Client
 *
 * It means that the server will have a RESTful web service which would provide the required
 * functionality to the client. The client send's a request to the web service on the server.
 * The server would either reject the request or comply and provide an adequate response to the
 * client.
 *
 * This Rest service client will be responsible to Deal with TenantRole endpoint
 *
 * @author Newton Carvalho
 */
@Stateless
@Default
public class TenantRoleUserRESTServiceClient extends AuthorizationChecker implements TenantRoleUserRESTServiceAccess {

    private static final long serialVersionUID = -3294029074149507760L;

    @Inject
    private OAFAccess oaf;

    @Inject
    private ClientServiceUtil clientServiceUtil;

    /**
     * Under a pagination approach, retrieves the Users associations that exist
     * for a TenantRole
     * (Invokes the core method counterpart and handles TokenExpiration error)
     * @param tenantId tenant identifier for a TenantRole (Acting as filter)
     * @param roleId role identifier for a TenantRole (Acting as filter)
     * @param pageNo page number
     * @param pageSize page size
     * @return Page containing TenantRoleUser instances
     * @throws SystemException in case of any error
     */
    @Override
    public Page<? extends SystemTenantRoleUser> getUsers(Long tenantId, Long roleId, int pageNo, int pageSize) throws SystemException {
        try {
            return getUsersCore(tenantId, roleId, pageNo, pageSize);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getUsersCore(tenantId, roleId, pageNo, pageSize);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Core method that Retrieves TenantRoleUser associations using pagination approach
     * @param tenantId tenant identifier for a TenantRole (Acting as filter)
     * @param roleId role identifier for a TenantRole (Acting as filter)
     * @param pageNo page number
     * @param pageSize page size
     * @return Page containing TenantRole user associations (Chunk/Portion compatible
     * with parameter Page number and Page size)
     * @throws SystemException in case of any error
     */
    protected Page<? extends SystemTenantRoleUser> getUsersCore(Long tenantId, Long roleId, int pageNo, int pageSize) throws SystemException {
        try {
            TenantRoleUserResourceClient client = clientServiceUtil.getTenantRoleUserResourceClient(oaf.
                    getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT));
            Response response = client.getAll(tenantId, roleId, pageNo, pageSize);
            return TenantRoleUserModelMapper.mapToPage((InputStream) response.getEntity());
        }
        catch (TokenExpiredException t) {
            throw t;
        }
        catch (ExtensionException | ProcessingException | MalformedURLException |
                InternalServerErrorException e){
            throw new SystemException(e);
        }
    }
}
