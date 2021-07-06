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
package io.radien.api.service.tenantrole;

import io.radien.api.entity.Page;
import io.radien.api.model.tenantrole.SystemTenantRoleUser;
import io.radien.exception.SystemException;

/**
 * Rest service client responsible to Deal with TenantRoleUser endpoint
 *
 * @author Newton Carvalho
 */
public interface TenantRoleUserRESTServiceAccess {

    /**
     * Under a pagination approach, retrieves the Users associations that currently exist
     * @param tenantRoleId identifier for a TenantRole (Acting as filter)
     * @param pageNo page number
     * @param pageSize page size
     * @return Page containing TenantRoleUser instances
     * @throws SystemException in case of any error
     */
    Page<? extends SystemTenantRoleUser> getUsers(Long tenantRoleId, int pageNo, int pageSize) throws SystemException;
}
