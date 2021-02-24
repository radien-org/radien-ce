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
package io.radien.ms.tenantmanagement.service;

import io.radien.api.model.tenant.SystemContract;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.service.tenant.ContractServiceAccess;
import io.radien.api.service.tenant.TenantServiceAccess;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.tenantmanagement.client.services.ContractFactory;
import io.radien.ms.tenantmanagement.entities.Contract;
import io.radien.ms.tenantmanagement.entities.Tenant;
import org.junit.Test;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * @author Nuno Santana
 * @author Bruno Gama
 */
public class TenantServiceTest {

    Properties p;
    TenantServiceAccess tenantServiceAccess;

    public TenantServiceTest() throws Exception {
        p = new Properties();
        p.put("appframeDatabase", "new://Resource?type=DataSource");
        p.put("appframeDatabase.JdbcDriver", "org.hsqldb.jdbcDriver");
        p.put("appframeDatabase.JdbcUrl", "jdbc:hsqldb:mem:radienTest");
        p.put("appframeDatabase.userName", "sa");
        p.put("appframeDatabase.password", "");

        final Context context = EJBContainer.createEJBContainer(p).getContext();

        tenantServiceAccess = (TenantServiceAccess) context.lookup("java:global/rd-ms-tenantmanagement//TenantService");

    }

    /**
     * Add tenant test.
     * Will create and save the tenant.
     * Expected result: Success.
     * Tested methods: void save(Tenant tenant)
     *
     * @throws UniquenessConstraintException in case of requested action is not well constructed
     */
    @Test
    public void testCreate() throws UniquenessConstraintException {
        SystemTenant result = createTenant("testCreate");
        assertNotNull(result);
    }

    /**
     * Add tenant test with duplicated name.
     * Will create and save the tenant, with an already existent name.
     * Expected result: Throw treated exception Error 101 Message There is more than one tenant with the same name.
     * Tested methods: void create(Tenant tenant)
     */
    @Test
    public void testAddDuplicatedName() throws UniquenessConstraintException {
        createTenant("testAddDuplicatedName");
        Exception exception = assertThrows(UniquenessConstraintException.class, () -> createTenant("testAddDuplicatedName"));
        String expectedMessage = "{\"code\":101, \"key\":\"error.duplicated.field\", \"message\":\"There is more than" +
                " one resource with the same value for the field: Name\"}";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    /**
     * Update tenant test with duplicated name.
     * Will create and save the tenant, with an already existent name.
     * Expected result: Throw treated exception Error 101 Message There is more than one tenant with the same name.
     * Tested methods: void update(Tenant tenant)
     */
    @Test
    public void testUpdateDuplicated() throws UniquenessConstraintException {
        SystemTenant c = createTenant("testUpdateDuplicated");
        Exception exception = assertThrows(UniquenessConstraintException.class, () -> tenantServiceAccess.update(new Tenant(new io.radien.ms.tenantmanagement.client.entities.Tenant(null,"testUpdateDuplicated"))));
        String expectedMessage = "{\"code\":101, \"key\":\"error.duplicated.field\", \"message\":\"There is more than" +
                " one resource with the same value for the field: Name\"}";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }


    /**
     * Gets tenant using the PK (id).
     * Will create a new tenant, save it into the DB and retrieve the specific tenant using the ID.
     * Expected result: will return the correct inserted tenant.
     * Tested methods: SystemTenant get(Long tenantId)
     *
     * @throws UniquenessConstraintException in case of requested action is not well constructed
     */
    @Test
    public void testGetById() throws  UniquenessConstraintException {
        String name = "testGetById";
        SystemTenant c = new Tenant(new io.radien.ms.tenantmanagement.client.entities.Tenant(1L,name));
        tenantServiceAccess.create(c);
        SystemTenant result = tenantServiceAccess.get(c.getId());
        assertNotNull(result);
        assertEquals(c.getName(), result.getName());
    }

    /**
     * Deletes inserted tenant using the PK (id).
     * Will create a new tenant, save it into the DB and delete it after using the specific ID.
     * Expected result: will return null when retrieving the tenant.
     * Tested methods: void delete(Long tenantId)
     *
     * @throws UniquenessConstraintException in case of requested action is not well constructed
     */
    @Test
    public void testDeleteById() throws UniquenessConstraintException {
        SystemTenant tenant = createTenant("testDeleteById");
        SystemTenant result = tenantServiceAccess.get(tenant.getId());
        assertNotNull(result);
        assertEquals(tenant.getName(), result.getName());
        tenantServiceAccess.delete(tenant.getId());
        result = tenantServiceAccess.get(tenant.getId());
        assertNull(result);
    }

    /**
     * Deletes inserted tenant using the PK (id).
     * Will create a new tenant, save it into the DB and delete it after using the specific ID.
     * Expected result: will return null when retrieving the tenant.
     * Tested methods: void delete(Long tenantId)
     *
     * @throws UniquenessConstraintException in case of requested action is not well constructed
     */
    @Test
    public void testDeleteByListId() throws  UniquenessConstraintException {
        SystemTenant tenant = createTenant("testDeleteByListId");
        SystemTenant result = tenantServiceAccess.get(tenant.getId());
        assertNotNull(result);
        assertEquals(tenant.getName(), result.getName());
        tenantServiceAccess.delete(Collections.singletonList(tenant.getId()));
        result = tenantServiceAccess.get(tenant.getId());
        assertNull(result);
    }

    /**
     * Test updates the tenant information.
     * @throws Exception in case of tenant to be updated not found
     */
    @Test
    public void testUpdateSuccess() throws Exception {
        SystemTenant c1 = createTenant("a<a>2355");
        String name3 = "a<a>99zz";
        c1.setName(name3);
        tenantServiceAccess.update(c1);
        c1 = tenantServiceAccess.get(c1.getId());

        assertEquals(name3, c1.getName());
    }

    private SystemTenant createTenant(String name) throws UniquenessConstraintException {
        SystemTenant tenant = new Tenant(new io.radien.ms.tenantmanagement.client.entities.Tenant(null,name));
        tenantServiceAccess.create(tenant);
        return tenant;
    }

}