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

import io.radien.api.model.linked.authorization.SystemLinkedAuthorization;
import io.radien.api.model.role.SystemRole;
import io.radien.api.service.linked.authorization.LinkedAuthorizationServiceAccess;
import io.radien.api.service.role.RoleServiceAccess;
import io.radien.exception.LinkedAuthorizationNotFoundException;
import io.radien.exception.RoleNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.entities.LinkedAuthorization;
import io.radien.ms.rolemanagement.entities.Role;
import org.junit.Test;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * @author Bruno Gama
 */
public class LinkedAuthorizationServiceIntegratedTest {
    Properties p;
    LinkedAuthorizationServiceAccess linkedAuthorizationServiceAccess;
    RoleServiceAccess roleServiceAccess;
    SystemLinkedAuthorization systemLinkedAuthorization;

    public LinkedAuthorizationServiceIntegratedTest() throws Exception {
        p = new Properties();
        p.put("appframeDatabase", "new://Resource?type=DataSource");
        p.put("appframeDatabase.JdbcDriver", "org.hsqldb.jdbcDriver");
        p.put("appframeDatabase.JdbcUrl", "jdbc:hsqldb:mem:radien");
        p.put("appframeDatabase.userName", "sa");
        p.put("appframeDatabase.password", "");

        final Context context = EJBContainer.createEJBContainer(p).getContext();

        linkedAuthorizationServiceAccess = (LinkedAuthorizationServiceAccess) context.lookup("java:global/rd-ms-rolemanagement//LinkedAuthorizationService");
        roleServiceAccess = (RoleServiceAccess) context.lookup("java:global/rd-ms-rolemanagement//RoleService");
    }

    @Test
    public void testIntegrationAmongEntities() throws RoleNotFoundException, UniquenessConstraintException, LinkedAuthorizationNotFoundException {
        SystemRole main = new Role();
        main.setName("admin");
        main.setDescription("Main Administrator Role");
        roleServiceAccess.save(main);

        SystemRole tenantAdmin = new Role();
        tenantAdmin.setName("tenant-admin");
        tenantAdmin.setDescription("Tenant Administrator Role");
        roleServiceAccess.save(tenantAdmin);

        SystemRole userAdmin = new Role();
        userAdmin.setName("user-admin");
        userAdmin.setDescription("User Administrator Role");
        roleServiceAccess.save(userAdmin);

        Long permissionIdForMainRole = 1000L;
        Long permissionIdForTenantAdminRole = 1001L;

        Long idUser1 = 222L;
        Long idUser2 = 232L;
        Long idUser3 = 332L;

        SystemLinkedAuthorization linkedAuthorization = new LinkedAuthorization();
        linkedAuthorization.setUserId(idUser1);
        linkedAuthorization.setRoleId(main.getId());
        linkedAuthorization.setPermissionId(permissionIdForMainRole);

        linkedAuthorizationServiceAccess.save(linkedAuthorization);

        // Creating for permissionIdForTenantAdminRole (Tenant admin role)
        linkedAuthorization = new LinkedAuthorization();
        linkedAuthorization.setUserId(idUser1);
        linkedAuthorization.setRoleId(tenantAdmin.getId());
        linkedAuthorization.setTenantId(1111L);
        linkedAuthorization.setPermissionId(permissionIdForTenantAdminRole);
        linkedAuthorizationServiceAccess.save(linkedAuthorization);

        // Checking For "Main Role"
        boolean exists = linkedAuthorizationServiceAccess.isRoleExistentForUser(idUser1, null,
                main.getName());
        assertTrue(exists);

        Long tenantId = 1000L;
        exists = linkedAuthorizationServiceAccess.isRoleExistentForUser(idUser1, tenantId,
                main.getName());
        assertFalse(exists);

        exists = linkedAuthorizationServiceAccess.isRoleExistentForUser(idUser2, tenantId,
                main.getName());
        assertFalse(exists);

        exists = linkedAuthorizationServiceAccess.isRoleExistentForUser(idUser3, tenantId,
                main.getName());
        assertFalse(exists);

        // Checking for "Tenant Admin"
        exists = linkedAuthorizationServiceAccess.isRoleExistentForUser(idUser1, null,
                tenantAdmin.getName());
        assertTrue(exists);

        exists = linkedAuthorizationServiceAccess.isRoleExistentForUser(idUser1, 1111L,
                tenantAdmin.getName());
        assertTrue(exists);
    }

}