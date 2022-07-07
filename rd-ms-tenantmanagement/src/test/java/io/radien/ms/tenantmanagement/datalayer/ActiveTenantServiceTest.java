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
package io.radien.ms.tenantmanagement.datalayer;

import io.radien.api.SystemVariables;
import io.radien.api.entity.Page;
import io.radien.api.model.tenant.SystemActiveTenant;
import io.radien.api.service.tenant.ActiveTenantServiceAccess;
import io.radien.api.service.tenant.exception.ActiveTenantException;
import io.radien.api.service.tenant.exception.ActiveTenantNotFoundException;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.NotFoundException;
import io.radien.exception.SystemException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.tenantmanagement.client.entities.ActiveTenantSearchFilter;
import io.radien.ms.tenantmanagement.entities.ActiveTenantEntity;
import org.junit.Test;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
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

        ActiveTenantSearchFilter filter = new ActiveTenantSearchFilter(2L, 2L, false);
        List<? extends SystemActiveTenant> roots = activeTenantServiceAccess.get(filter);
        if (roots.isEmpty()) {
            systemActiveTenant = new ActiveTenantEntity();
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
    public void testCreate() throws UniquenessConstraintException, ActiveTenantException, SystemException {
        SystemActiveTenant result = createActiveTenant(300L, 300L, 300L);
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
    public void testGetById() throws UniquenessConstraintException, SystemException {
        SystemActiveTenant c = new ActiveTenantEntity(new io.radien.ms.tenantmanagement.client.entities.ActiveTenant(500L, 2L, 8L));
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
    public void testDeleteById() throws UniquenessConstraintException, ActiveTenantException, SystemException {
        SystemActiveTenant activeTenant = createActiveTenant(4L, 4L, 4L);
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
    public void testDeleteByListId() throws UniquenessConstraintException, ActiveTenantException, SystemException {
        SystemActiveTenant activeTenant = createActiveTenant(5L, 5L, 5L);
        SystemActiveTenant result = activeTenantServiceAccess.get(activeTenant.getId());
        assertNotNull(result);
        assertEquals(activeTenant.getUserId(), result.getUserId());
        activeTenantServiceAccess.delete(Collections.singletonList(activeTenant.getId()));
        result = activeTenantServiceAccess.get(activeTenant.getId());
        assertNull(result);
    }

    /**
     * Deletes active tenants using tenant (id) and user (id) as parameter
     * Will create a new active tenant, save it into the DB and delete it after using the specific
     * tenant and user Ids.
     * Expected result: will return null when retrieving the active tenant.
     * Tested methods: delete(Long tenantId, Long userId)
     *
     * @throws UniquenessConstraintException in case of requested action is not well constructed
     */
    @Test
    public void testDeleteByUserIdAndTenantId() throws UniquenessConstraintException, ActiveTenantException, SystemException {
        Long tenantId = 1111L, userId = 222L;
        SystemActiveTenant activeTenant = createActiveTenant(tenantId, userId);
        SystemActiveTenant result = activeTenantServiceAccess.get(activeTenant.getId());
        assertNotNull(result);

        assertEquals(userId, activeTenant.getUserId());
        assertEquals(tenantId, activeTenant.getTenantId());
        assertEquals(activeTenant.getUserId(), result.getUserId());
        assertEquals(activeTenant.getTenantId(), result.getTenantId());

        // Now try to remove correctly
        assertTrue(activeTenantServiceAccess.delete(tenantId, userId));
        result = activeTenantServiceAccess.get(activeTenant.getId());
        assertNull(result);

        // Testing with non existent values
        assertFalse(activeTenantServiceAccess.delete(100000L, 1000000L));
    }

    /**
     * Try to Delete active tenants without informing tenant (id) and user (id) as parameters
     * Will raise exception EJBException informing the issue
     * Expected result: will return null when retrieving the active tenant.
     * Tested methods: delete(Long tenantId, Long userId)
     */
    @Test
    public void testDeleteByUserAndTenantWithoutInformingThem() {
        Long tenantId = null, userId = null;
        SystemException e = assertThrows(SystemException.class, () ->
                activeTenantServiceAccess.delete(tenantId, userId));
        assertEquals("Message(s): " + GenericErrorCodeMessage.ACTIVE_TENANT_DELETE_WITHOUT_TENANT_AND_USER.toString(),
                e.getMessage());
    }

    /**
     * Test updates the active tenant information.
     * @throws Exception in case of active tenant to be updated not found
     */
    @Test
    public void testUpdateSuccess() throws Exception {
        SystemActiveTenant c1 = createActiveTenant(6L, 6L, 6L);
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
    private SystemActiveTenant createActiveTenant(Long id, Long tenantId, Long userId) throws UniquenessConstraintException, ActiveTenantException, SystemException {
        SystemActiveTenant activeTenant = new ActiveTenantEntity();
        activeTenant.setId(id);
        activeTenant.setUserId(userId);
        activeTenant.setTenantId(tenantId);
        activeTenantServiceAccess.create(activeTenant);
        return activeTenant;
    }

    /**
     * Test of creation of active tenants (using tenant id and user id as parameters)
     * @param tenantId tenant identifier
     * @param userId user identifier
     * @return system active tenant
     * @throws UniquenessConstraintException in case of duplicates
     * @throws ActiveTenantException in case of any issue in the data
     */
    private SystemActiveTenant createActiveTenant(Long tenantId, Long userId) throws UniquenessConstraintException, ActiveTenantException, SystemException {
        SystemActiveTenant activeTenant = new ActiveTenantEntity();
        activeTenant.setTenantId(tenantId);
        activeTenant.setUserId(userId);
        activeTenantServiceAccess.create(activeTenant);
        return activeTenant;
    }

    /**
     * Test of get all the active tenants
     * @throws UniquenessConstraintException in case of duplicates
     * @throws ActiveTenantException in case of any issue in the data
     */
    @Test
    public void testGetAll() throws UniquenessConstraintException, ActiveTenantException, SystemException {
        Long tenantId = 1111111L;
        Long userId1 = 1111111L, userId2 = 1111112L;

        SystemActiveTenant c = new ActiveTenantEntity();
        c.setUserId(userId1);
        c.setTenantId(tenantId);
        activeTenantServiceAccess.create(c);

        Page<SystemActiveTenant> result = activeTenantServiceAccess.getAll(tenantId, userId1,1,10,
                Collections.singletonList(SystemVariables.USER_ID.getFieldName()),false);
        assertNotNull(result);
        assertEquals(1, result.getTotalResults());

        SystemActiveTenant c2 = new ActiveTenantEntity();
        c2.setId(1000L);
        c2.setUserId(userId2);
        c2.setTenantId(tenantId);
        activeTenantServiceAccess.create(c2);

        result = activeTenantServiceAccess.getAll(null, userId2,1,10,
                Collections.singletonList(SystemVariables.USER_ID.getFieldName()),false);
        assertNotNull(result);
        assertEquals(1, result.getTotalResults());

        result = activeTenantServiceAccess.getAll(tenantId,null,1,10,
                Collections.singletonList(SystemVariables.USER_ID.getFieldName()),false);
        assertNotNull(result);
        assertEquals(2, result.getTotalResults());
    }

    /**
     * Test of get specific active tenant method
     * @throws UniquenessConstraintException in case of duplicates
     * @throws ActiveTenantException in case of any issue in the data
     */
    @Test
    public void testGet() throws UniquenessConstraintException, ActiveTenantException, SystemException {
        SystemActiveTenant c = new ActiveTenantEntity(new io.radien.ms.tenantmanagement.client.entities.ActiveTenant(8L, 2L, 3L));
        activeTenantServiceAccess.create(c);
        ActiveTenantSearchFilter filter = new ActiveTenantSearchFilter(2L, null, false);
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
    public void testGetIsLogicConjunction() throws UniquenessConstraintException, ActiveTenantException, SystemException {
        SystemActiveTenant c = new ActiveTenantEntity(new io.radien.ms.tenantmanagement.client.entities.ActiveTenant(9L, 2L, 9L));
        activeTenantServiceAccess.create(c);
        ActiveTenantSearchFilter filter = new ActiveTenantSearchFilter(2L, null, true);
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
    public void testExists() throws UniquenessConstraintException, NotFoundException, ActiveTenantException, SystemException {
        SystemActiveTenant c = new ActiveTenantEntity(new io.radien.ms.tenantmanagement.client.entities.ActiveTenant(10L, 2L, 12L));
        activeTenantServiceAccess.create(c);
        assertTrue(activeTenantServiceAccess.exists(2L, 12L));
    }

    /**
     * Test to retrieve all the active tenants
     * @throws UniquenessConstraintException in case of duplicates
     * @throws ActiveTenantException in case of any issue in the data
     */
    @Test
    public void testRetrieveAllPossibleTenants() throws UniquenessConstraintException, ActiveTenantException, SystemException {
        SystemActiveTenant activeTenant = new ActiveTenantEntity();
        activeTenant.setUserId(4L);
        activeTenant.setTenantId(2L);
        activeTenantServiceAccess.create(activeTenant);

        SystemActiveTenant activeTenant2 = new ActiveTenantEntity();
        activeTenant2.setUserId(4L);
        activeTenant2.setTenantId(3L);
        activeTenantServiceAccess.create(activeTenant2);

        Page<SystemActiveTenant> page = activeTenantServiceAccess.getAll(null, null,1,100,null,false);
        assertTrue(page.getTotalResults()>2);
    }

    /**
     * Try to update an ActiveTenant that does not exist
     * @throws UniquenessConstraintException in case of duplicates
     * @throws ActiveTenantException in case of any issue in the data
     * @throws ActiveTenantNotFoundException to indicate that no ActiveTenant could be found
     */
    @Test(expected = ActiveTenantException.class)
    public void testUpdateNotExistent() throws ActiveTenantNotFoundException, UniquenessConstraintException, ActiveTenantException, SystemException {
        SystemActiveTenant activeTenant = new ActiveTenantEntity();
        activeTenant.setId(11111111111L);
        activeTenant.setUserId(4L);
        activeTenant.setTenantId(2L);
        activeTenantServiceAccess.update(activeTenant);
    }

    /**
     * Try to create an Active without informing Tenant Id
     * @throws UniquenessConstraintException in case of duplicates
     * @throws ActiveTenantException in case of any issue in the data
     */
    @Test(expected = SystemException.class)
    public void testCreateWithNoTenantId() throws UniquenessConstraintException, ActiveTenantException, SystemException {
        SystemActiveTenant activeTenant = new ActiveTenantEntity();
        activeTenant.setUserId(1L);
        activeTenantServiceAccess.create(activeTenant);
    }


    /**
     * Try to create an Active without informing User Id
     * @throws UniquenessConstraintException in case of duplicates
     * @throws ActiveTenantException in case of any issue in the data
     */
    @Test(expected = SystemException.class)
    public void testCreateWithNoUserId() throws UniquenessConstraintException, ActiveTenantException, SystemException {
        SystemActiveTenant activeTenant = new ActiveTenantEntity();
        activeTenant.setTenantId(1L);
        activeTenantServiceAccess.create(activeTenant);
    }


    /**
     * Try to update an Active without informing Tenant Id
     * @throws UniquenessConstraintException in case of duplicates
     * @throws ActiveTenantException in case of any issue in the data
     * @throws ActiveTenantNotFoundException to indicate that no ActiveTenant could be found
     */
    @Test(expected = SystemException.class)
    public void testUpdateWithNoTenantId() throws ActiveTenantNotFoundException, UniquenessConstraintException, ActiveTenantException, SystemException {
        SystemActiveTenant activeTenant = new ActiveTenantEntity();
        activeTenant.setUserId(1L);
        activeTenantServiceAccess.update(activeTenant);
    }

    /**
     * Try to update an Active without informing User Id
     * @throws UniquenessConstraintException in case of duplicates
     * @throws ActiveTenantException in case of any issue in the data
     * @throws ActiveTenantNotFoundException to indicate that no ActiveTenant could be found
     */
    @Test(expected = SystemException.class)
    public void testUpdateWithNoUserId() throws ActiveTenantNotFoundException, UniquenessConstraintException, ActiveTenantException, SystemException {
        SystemActiveTenant activeTenant = new ActiveTenantEntity();
        activeTenant.setTenantId(1L);
        activeTenantServiceAccess.update(activeTenant);
    }

}