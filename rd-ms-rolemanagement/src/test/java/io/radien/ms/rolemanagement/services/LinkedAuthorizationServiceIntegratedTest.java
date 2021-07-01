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

import io.radien.api.entity.Page;
import io.radien.api.model.linked.authorization.SystemLinkedAuthorization;
import io.radien.api.model.role.SystemRole;
import io.radien.api.service.linked.authorization.LinkedAuthorizationServiceAccess;
import io.radien.api.service.role.RoleServiceAccess;
import io.radien.exception.LinkedAuthorizationNotFoundException;
import io.radien.exception.RoleNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.entities.LinkedAuthorization;
import io.radien.ms.rolemanagement.entities.Role;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import java.util.List;
import java.util.Properties;

import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Linked authorization service integrated test to validate connections and requests
 *
 * @author Newton Carvalho
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
public class LinkedAuthorizationServiceIntegratedTest {

    static Properties p;
    static LinkedAuthorizationServiceAccess linkedAuthorizationServiceAccess;
    static RoleServiceAccess roleServiceAccess;
    static EJBContainer container;
    static Context context;

    static final String roleJndiLookupUri = "java:global/rd-ms-rolemanagement//RoleService";
    static final String linkedAuthzJndiLookupUri =
            "java:global/rd-ms-rolemanagement//LinkedAuthorizationService";

    /**
     * Method to initialize db connection
     */
    @BeforeAll
    public static void start() {
        p = new Properties();
        p.put("appframeDatabase", "new://Resource?type=DataSource");
        p.put("appframeDatabase.JdbcDriver", "org.hsqldb.jdbcDriver");
        p.put("appframeDatabase.JdbcUrl", "jdbc:hsqldb:mem:radien");
        p.put("appframeDatabase.userName", "sa");
        p.put("appframeDatabase.password", "");
        p.put("openejb.exclude-include.order", "include-exclude"); // Defines the processing order
        p.put("openejb.deployments.classpath.include", ".*rolemanagement.*");
        p.put("openejb.deployments.classpath.exclude", ".*client.*");
        p.put("openejb.cdi.activated-on-ejb", "false");

        container = EJBContainer.createEJBContainer(p);
        context = container.getContext();
    }

    /**
     * Method to initialize the system variables for testing
     * @throws NamingException in case of naming exception in the injection
     */
    @BeforeEach
    public void setUp() throws NamingException {
        roleServiceAccess = (RoleServiceAccess) context.lookup(roleJndiLookupUri);
        linkedAuthorizationServiceAccess = (LinkedAuthorizationServiceAccess)
                context.lookup(linkedAuthzJndiLookupUri);
    }

    /**
     * Test the integration between multiple services
     * @throws RoleNotFoundException in case requested role could not be found
     * @throws UniquenessConstraintException in case there are duplicated fields or records
     * @throws LinkedAuthorizationNotFoundException in case linked authorization could not be found
     */
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

    /**
     * Test to attempt to connect with the wrong parameters
     */
    @Test
    public void testInvokingWithWrongParameters() {
        assertThrows(Exception.class, () -> linkedAuthorizationServiceAccess.
            getRolesByUserAndTenant(null, null));
        assertThrows(Exception.class, () -> linkedAuthorizationServiceAccess.
                isRoleExistentForUser(null, null, "role-test"));
        assertThrows(Exception.class, () -> linkedAuthorizationServiceAccess.
                isRoleExistentForUser(1L, null, null));
    }

    /**
     * Test to retrieve the roles in all the users
     */
    @Test
    public void testRetrieveRolesPerUser() {
        long idUser1 = 222L;
        List<? extends  SystemRole> roles = linkedAuthorizationServiceAccess.
                getRolesByUserAndTenant(idUser1, null);
        assertEquals(2, roles.size());

        long tenantId = 1111L;
        roles = linkedAuthorizationServiceAccess.getRolesByUserAndTenant(idUser1, tenantId);
        assertEquals(1, roles.size());

        tenantId = 9999L;
        roles = linkedAuthorizationServiceAccess.getRolesByUserAndTenant(idUser1, tenantId);
        assertEquals(0, roles.size());
    }


    /**
     * The idea for this method is to do a "clean up" process, for do not
     * interfere in other tests cases ahead that use LinkedAuthorization
     */
    @AfterAll
    public static void cleanUp() throws LinkedAuthorizationNotFoundException, RoleNotFoundException {
        Page<SystemRole> allRoles = roleServiceAccess.getAll(null,1, 100, null, false);
        for (SystemRole sr: allRoles.getResults()) {
            roleServiceAccess.delete(sr.getId());
        }
        Page<SystemLinkedAuthorization> allLinkedAuths = linkedAuthorizationServiceAccess.getAll(1, 100);
        for (SystemLinkedAuthorization sl: allLinkedAuths.getResults()) {
            linkedAuthorizationServiceAccess.deleteAssociation(sl.getId());
        }
        if (container != null) {
            container.close();
        }
    }
}