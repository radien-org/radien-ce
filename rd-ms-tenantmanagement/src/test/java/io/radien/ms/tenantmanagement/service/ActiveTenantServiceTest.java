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

import io.radien.api.entity.Page;
import io.radien.api.model.tenant.SystemActiveTenant;
import io.radien.api.service.tenant.ActiveTenantServiceAccess;
import io.radien.exception.ActiveTenantException;
import io.radien.exception.NotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.tenantmanagement.client.entities.ActiveTenantSearchFilter;
import io.radien.ms.tenantmanagement.entities.ActiveTenant;
import org.junit.Test;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Bruno Gama
 */
public class ActiveTenantServiceTest {

    Properties p;
    ActiveTenantServiceAccess activeTenantServiceAccess;
    SystemActiveTenant systemActiveTenant = null;

    public ActiveTenantServiceTest() throws Exception {
        p = new Properties();
        p.put("appframeDatabase", "new://Resource?type=DataSource");
        p.put("appframeDatabase.JdbcDriver", "org.hsqldb.jdbcDriver");
        p.put("appframeDatabase.JdbcUrl", "jdbc:hsqldb:mem:radienTest");
        p.put("appframeDatabase.userName", "sa");
        p.put("appframeDatabase.password", "");
        p.put("openejb.deployments.classpath.include",".*");
        p.put("openejb.deployments.classpath.exclude",".*rd-ms-usermanagement-client.*");


        final Context context = EJBContainer.createEJBContainer(p).getContext();

        activeTenantServiceAccess = (ActiveTenantServiceAccess) context.lookup("java:global/rd-ms-tenantmanagement//ActiveTenantService");

        ActiveTenantSearchFilter filter = new ActiveTenantSearchFilter(2L, 2L, null,false, false);
        List<? extends SystemActiveTenant> roots = activeTenantServiceAccess.get(filter);
        if (roots.isEmpty()) {
            systemActiveTenant = new ActiveTenant();
            systemActiveTenant.setUserId(2L);
            systemActiveTenant.setTenantId(2L);
            activeTenantServiceAccess.create(systemActiveTenant);
        }
        else {
            systemActiveTenant = roots.get(0);
        }
    }

    /**
     * Add active tenant test.
     * Will create and save the active tenant.
     * Expected result: Success.
     * Tested methods: void save(Active Tenant active tenant)
     *
     * @throws UniquenessConstraintException in case of requested action is not well constructed
     */
    @Test
    public void testCreate() throws UniquenessConstraintException, ActiveTenantException {
        SystemActiveTenant result = createActiveTenant(300L);
        assertNotNull(result);
    }

    /**
     * Gets active tenant using the PK (id).
     * Will create a new active tenant, save it into the DB and retrieve the specific active tenant using the ID.
     * Expected result: will return the correct inserted active tenant.
     * Tested methods: SystemActiveTenant get(Long activeTenantId)
     *
     * @throws UniquenessConstraintException in case of requested action is not well constructed
     */
    @Test
    public void testGetById() throws UniquenessConstraintException, ActiveTenantException {
        SystemActiveTenant c = new ActiveTenant(new io.radien.ms.tenantmanagement.client.entities.ActiveTenant(500L, 2L, 2L, null, false));
        activeTenantServiceAccess.create(c);
        SystemActiveTenant result = activeTenantServiceAccess.get(c.getId());
        assertNotNull(result);
        assertEquals(c.getUserId(), result.getUserId());
    }

    /**
     * Deletes inserted active tenant using the PK (id).
     * Will create a new active tenant, save it into the DB and delete it after using the specific ID.
     * Expected result: will return null when retrieving the active tenant.
     * Tested methods: void delete(Long activeTenantId)
     *
     * @throws UniquenessConstraintException in case of requested action is not well constructed
     */
    @Test
    public void testDeleteById() throws UniquenessConstraintException, ActiveTenantException {
        SystemActiveTenant activeTenant = createActiveTenant(4L);
        SystemActiveTenant result = activeTenantServiceAccess.get(activeTenant.getId());
        assertNotNull(result);
        assertEquals(activeTenant.getUserId(), result.getUserId());
        activeTenantServiceAccess.delete(activeTenant.getId());
        result = activeTenantServiceAccess.get(activeTenant.getId());
        assertNull(result);
    }

    /**
     * Deletes inserted active tenant using the PK (id).
     * Will create a new active tenant, save it into the DB and delete it after using the specific ID.
     * Expected result: will return null when retrieving the active tenant.
     * Tested methods: void delete(Long activeTenantId)
     *
     * @throws UniquenessConstraintException in case of requested action is not well constructed
     */
    @Test
    public void testDeleteByListId() throws UniquenessConstraintException, ActiveTenantException {
        SystemActiveTenant activeTenant = createActiveTenant(5L);
        SystemActiveTenant result = activeTenantServiceAccess.get(activeTenant.getId());
        assertNotNull(result);
        assertEquals(activeTenant.getUserId(), result.getUserId());
        activeTenantServiceAccess.delete(Collections.singletonList(activeTenant.getId()));
        result = activeTenantServiceAccess.get(activeTenant.getId());
        assertNull(result);
    }

    /**
     * Test updates the active tenant information.
     * @throws Exception in case of active tenant to be updated not found
     */
    @Test
    public void testUpdateSuccess() throws Exception {
        SystemActiveTenant c1 = createActiveTenant(6L);
        c1.setUserId(3L);
        activeTenantServiceAccess.update(c1);
        c1 = activeTenantServiceAccess.get(c1.getId());

        assertEquals((Long) 3L, c1.getUserId());
    }

    /**
     * Test of creation of active tenants
     * @param id of the active tenant to create
     * @return system active tenant
     * @throws UniquenessConstraintException in case of duplicates
     * @throws ActiveTenantException in case of any issue in the data
     */
    private SystemActiveTenant createActiveTenant(Long id) throws UniquenessConstraintException, ActiveTenantException {
        SystemActiveTenant activeTenant = new ActiveTenant();
        activeTenant.setId(id);
        activeTenant.setUserId(2L);
        activeTenant.setTenantId(2L);
        activeTenantServiceAccess.create(activeTenant);
        return activeTenant;
    }

    /**
     * Test of get all the active tenants
     * @throws UniquenessConstraintException in case of duplicates
     * @throws ActiveTenantException in case of any issue in the data
     */
    @Test
    public void testGetAll() throws UniquenessConstraintException, ActiveTenantException {
        SystemActiveTenant c = new ActiveTenant();
        c.setId(7L);
        c.setUserId(2L);
        c.setTenantId(2L);
        activeTenantServiceAccess.create(c);
        Page<SystemActiveTenant> result = activeTenantServiceAccess.getAll(null,1,10,null,false);
        assertNotNull(result);
    }

    /**
     * Test of get specific active tenant method
     * @throws UniquenessConstraintException in case of duplicates
     * @throws ActiveTenantException in case of any issue in the data
     */
    @Test
    public void testGet() throws UniquenessConstraintException, ActiveTenantException {
        SystemActiveTenant c = new ActiveTenant(new io.radien.ms.tenantmanagement.client.entities.ActiveTenant(8L, 2L, 2L, null, false));
        activeTenantServiceAccess.create(c);
        ActiveTenantSearchFilter filter = new ActiveTenantSearchFilter(2L, null, null, false, false);
        List<? extends SystemActiveTenant> result = activeTenantServiceAccess.get(filter);
        assertNotNull(result);
        assertEquals((Long) 1L, result.get(0).getId());
    }

    /**
     * Test of get specific active tenant method with logical conjunction
     * @throws UniquenessConstraintException in case of duplicates
     * @throws ActiveTenantException in case of any issue in the data
     */
    @Test
    public void testGetIsLogicConjunction() throws UniquenessConstraintException, ActiveTenantException {
        SystemActiveTenant c = new ActiveTenant(new io.radien.ms.tenantmanagement.client.entities.ActiveTenant(9L, 2L, 2L, null, false));
        activeTenantServiceAccess.create(c);
        ActiveTenantSearchFilter filter = new ActiveTenantSearchFilter(2L, null, null, false, true);
        List<? extends SystemActiveTenant> result = activeTenantServiceAccess.get(filter);
        assertNotNull(result);
        assertEquals((Long) 1L, result.get(0).getId());
    }

    /**
     * Will test the validation of a specific active tenant exists
     * @throws UniquenessConstraintException in case of duplicates
     * @throws NotFoundException in case of active tenant not existing
     * @throws ActiveTenantException in case of any issue in the data
     */
    @Test
    public void testExists() throws UniquenessConstraintException, NotFoundException, ActiveTenantException {
        SystemActiveTenant c = new ActiveTenant(new io.radien.ms.tenantmanagement.client.entities.ActiveTenant(10L, 2L, 2L, null, false));
        activeTenantServiceAccess.create(c);
        assertTrue(activeTenantServiceAccess.exists(10L));
    }

    /**
     * Test to retrieve all the active tenants
     * @throws UniquenessConstraintException in case of duplicates
     * @throws ActiveTenantException in case of any issue in the data
     */
    @Test
    public void testRetrieveAllPossibleTenants() throws UniquenessConstraintException, ActiveTenantException {
        SystemActiveTenant activeTenant = new ActiveTenant();
        activeTenant.setUserId(2L);
        activeTenant.setTenantId(2L);
        activeTenantServiceAccess.create(activeTenant);

        SystemActiveTenant activeTenant2 = new ActiveTenant();
        activeTenant2.setUserId(2L);
        activeTenant2.setTenantId(2L);
        activeTenantServiceAccess.create(activeTenant2);

        Page<SystemActiveTenant> page = activeTenantServiceAccess.getAll(null,1,100,null,false);
        assertTrue(page.getTotalResults()>2);
    }
}