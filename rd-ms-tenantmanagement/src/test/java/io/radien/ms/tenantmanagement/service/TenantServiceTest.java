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
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.service.tenant.TenantServiceAccess;
import io.radien.exception.NotFoundException;
import io.radien.exception.TenantException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.tenantmanagement.client.entities.TenantSearchFilter;
import io.radien.ms.tenantmanagement.client.entities.TenantType;
import io.radien.ms.tenantmanagement.entities.Tenant;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * @author Nuno Santana
 * @author Bruno Gama
 */
public class TenantServiceTest {

    Properties p;
    TenantServiceAccess tenantServiceAccess;
    SystemTenant rootTenant = null;

    public TenantServiceTest() throws Exception {
        p = new Properties();
        p.put("appframeDatabase", "new://Resource?type=DataSource");
        p.put("appframeDatabase.JdbcDriver", "org.hsqldb.jdbcDriver");
        p.put("appframeDatabase.JdbcUrl", "jdbc:hsqldb:mem:radienTest");
        p.put("appframeDatabase.userName", "sa");
        p.put("appframeDatabase.password", "");
        p.put("openejb.deployments.classpath.include",".*");
        p.put("openejb.deployments.classpath.exclude",".*rd-ms-usermanagement-client.*");


        final Context context = EJBContainer.createEJBContainer(p).getContext();

        tenantServiceAccess = (TenantServiceAccess) context.lookup("java:global/rd-ms-tenantmanagement//TenantService");

        TenantSearchFilter filter = new TenantSearchFilter("rootName", null, false, false);
        List<? extends SystemTenant> roots = tenantServiceAccess.get(filter);
        if (roots.isEmpty()) {
            rootTenant = new Tenant();
            rootTenant.setName("rootName");
            rootTenant.setKey("rd");
            rootTenant.setType(TenantType.ROOT_TENANT);
            tenantServiceAccess.create(rootTenant);
        }
        else {
            rootTenant = roots.get(0);
        }
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
    public void testCreate() throws UniquenessConstraintException, TenantException {
        SystemTenant result = createTenant("testCreate");
        assertNotNull(result);
    }


    /**
     * Add second root tenant. Should throw exception.
     */
    @Test
    public void testCreateDoubleRootException() {
        SystemTenant tenant = new Tenant();
        tenant.setName("nameCreation");
        tenant.setType(TenantType.ROOT_TENANT);
        tenant.setStart(LocalDate.now());
        tenant.setKey(RandomStringUtils.randomAlphabetic(4));
        Exception exception = assertThrows(TenantException.class, () -> tenantServiceAccess.create(tenant));
        String expectedMessage = "{\"code\":109, \"key\":\"error.tenant.root.already.inserted\"," +
                " \"message\":\"There must be only one Root Tenant.\"}";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    /**
     * Add tenant test with duplicated name.
     * Will create and save the tenant, with an already existent name.
     * Expected result: Throw treated exception Error 101 Message There is more than one tenant with the same name.
     * Tested methods: void create(Tenant tenant)
     */
    @Test
    public void testAddDuplicatedName() throws UniquenessConstraintException, TenantException {
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
    public void testUpdateDuplicated() throws UniquenessConstraintException, TenantException {
        SystemTenant c = createTenant("testUpdateDuplicated");
        Exception exception = assertThrows(UniquenessConstraintException.class, () ->
                tenantServiceAccess.update(new Tenant(new io.radien.ms.tenantmanagement.client.entities.Tenant(
                        null,"testUpdateDuplicated", "key-x", TenantType.CLIENT_TENANT, null, null,
                        null, null, null,null, null,
                        null, rootTenant.getId(), null))));
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
    public void testGetById() throws UniquenessConstraintException, TenantException {
        String name = "testGetById";
        SystemTenant c = new Tenant(new io.radien.ms.tenantmanagement.client.entities.Tenant(
                11111L,name, RandomStringUtils.randomAlphabetic(4), TenantType.CLIENT_TENANT, null, null,
                null, null, null,null, null,
                null, rootTenant.getId(), null));
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
    public void testDeleteById() throws UniquenessConstraintException, TenantException {
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
    public void testDeleteByListId() throws UniquenessConstraintException, TenantException {
        SystemTenant tenant = createTenant("testDeleteByListId");
        SystemTenant result = tenantServiceAccess.get(tenant.getId());
        assertNotNull(result);
        assertEquals(tenant.getName(), result.getName());
        tenantServiceAccess.delete(Collections.singletonList(tenant.getId()));
        result = tenantServiceAccess.get(tenant.getId());
        assertNull(result);
    }

    /**
     * Deletes inserted tenant using the PK (id) and if exists the under the parent of tenants.
     * Will create a new tenant, save it into the DB and delete it after using the specific ID.
     * Expected result: will return null when retrieving the tenant and under sub tenants.
     * Tested methods: void deleteTenantHierarchy(Long tenantId)
     *
     * @throws UniquenessConstraintException in case of requested action is not well constructed
     */
    @Test
    public void testDeleteTenantHierarchyById() throws UniquenessConstraintException, TenantException {
        SystemTenant tenant = createTenant("testDeleteById");
        SystemTenant result = tenantServiceAccess.get(tenant.getId());
        assertNotNull(result);
        assertEquals(tenant.getName(), result.getName());
        tenantServiceAccess.deleteTenantHierarchy(tenant.getId());
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

    /**
     * Test of creation of tenants
     * @param name of the tenant to create
     * @return system tenant
     * @throws UniquenessConstraintException in case of duplicates
     * @throws TenantException in case of any issue in the data
     */
    private SystemTenant createTenant(String name) throws UniquenessConstraintException, TenantException {
        SystemTenant tenant = new Tenant();
        tenant.setName(name);
        tenant.setType(TenantType.CLIENT_TENANT);
        tenant.setParentId(rootTenant.getId());
        tenant.setKey(RandomStringUtils.randomAlphabetic(4));
        tenantServiceAccess.create(tenant);
        return tenant;
    }

    /**
     * Test of get all the tenants
     * @throws UniquenessConstraintException in case of duplicates
     * @throws TenantException in case of any issue in the data
     */
    @Test
    public void testGetAll() throws UniquenessConstraintException, TenantException {
        String name = "testGetAll";
        SystemTenant c = new Tenant();
        c.setId(100L);
        c.setName(name);
        c.setType(TenantType.CLIENT_TENANT);
        c.setParentId(rootTenant.getId());
        c.setKey(RandomStringUtils.randomAlphabetic(4));
        tenantServiceAccess.create(c);
        Page<SystemTenant> result = tenantServiceAccess.getAll(null,1,10,null,false);
        assertNotNull(result);
    }

    @Test
    public void testGetAllSearchNotNullSort() throws UniquenessConstraintException, TenantException {
        String name = "testGetAll2";
        SystemTenant c = new Tenant();
        c.setId(102L);
        c.setName(name);
        c.setType(TenantType.CLIENT_TENANT);
        c.setParentId(rootTenant.getId());
        c.setKey(RandomStringUtils.randomAlphabetic(4));
        tenantServiceAccess.create(c);

        List<String> sortBy = new ArrayList<>();
        sortBy.add("name");

        Page<SystemTenant> result = tenantServiceAccess.getAll("testGetAll2",1,10,sortBy,false);
        assertNotNull(result);

        Page<SystemTenant> result2 = tenantServiceAccess.getAll("testGetAll2",1,10,sortBy,true);
        assertNotNull(result2);
    }

    /**
     * Test of get specific tenant method
     * @throws UniquenessConstraintException in case of duplicates
     * @throws TenantException in case of any issue in the data
     */
    @Test
    public void testGet() throws UniquenessConstraintException, TenantException {
        String name = "testGet";
        SystemTenant c = new Tenant(new io.radien.ms.tenantmanagement.client.entities.Tenant(
                200L,name, RandomStringUtils.randomAlphabetic(4), TenantType.CLIENT_TENANT, null, null,
                null, null, null,null, null,
                null, rootTenant.getId(), null));
        tenantServiceAccess.create(c);
        TenantSearchFilter filter = new TenantSearchFilter(name, null, false, false);
        List<? extends SystemTenant> result = tenantServiceAccess.get(filter);
        assertNotNull(result);
        assertEquals((Long) 200L, result.get(0).getId());
    }

    /**
     * Test of get specific tenant method with logical conjunction
     * @throws UniquenessConstraintException in case of duplicates
     * @throws TenantException in case of any issue in the data
     */
    @Test
    public void testGetIsLogicConjunction() throws UniquenessConstraintException, TenantException {
        String name = "testGetIsLogicalConjunction";
        SystemTenant c = new Tenant(new io.radien.ms.tenantmanagement.client.entities.Tenant(
                923L,name, RandomStringUtils.randomAlphabetic(4), TenantType.CLIENT_TENANT, null, null,
                null, null, null,null, null,
                null, rootTenant.getId(), null));
        tenantServiceAccess.create(c);
        TenantSearchFilter filter = new TenantSearchFilter(name, null, false, true);
        List<? extends SystemTenant> result = tenantServiceAccess.get(filter);
        assertNotNull(result);
        assertEquals((Long) 923L, result.get(0).getId());
    }

    /**
     * Will test the validation of a specific tenant exists
     * @throws UniquenessConstraintException in case of duplicates
     * @throws NotFoundException in case of tenant not existing
     * @throws TenantException in case of any issue in the data
     */
    @Test
    public void testExists() throws UniquenessConstraintException, NotFoundException, TenantException {
        String name = "testExists";
        SystemTenant c = new Tenant(new io.radien.ms.tenantmanagement.client.entities.Tenant(
                300L,name, RandomStringUtils.randomAlphabetic(4), TenantType.CLIENT_TENANT, null, null,
                null, null, null,null, null,
                null, rootTenant.getId(), null));
        tenantServiceAccess.create(c);
        assertTrue(tenantServiceAccess.exists(300L));
    }

    /**
     * Tests adding a root tenant
     */
    @Test
    public void testAddRootTenant() {
        Tenant tenant = new Tenant();
        tenant.setType(TenantType.ROOT_TENANT);
        tenant.setKey("key1");
        tenant.setName("radien-default");
        tenant.setStart(LocalDate.now());
        tenant.setParentId(2L);
        tenant.setEnd(LocalDate.now().plus(3, ChronoUnit.YEARS));
        assertThrows(TenantException.class, ()->tenantServiceAccess.create(tenant));
    }

    /**
     * Test to add a sub tenant
     * @throws UniquenessConstraintException in case of duplicates
     * @throws TenantException in case of any issue in the data
     */
    @Test
    public void testAddSubTenant() throws UniquenessConstraintException, TenantException {
        Tenant tenant = new Tenant();
        tenant.setType(TenantType.CLIENT_TENANT);
        tenant.setKey("keyClient");
        tenant.setParentId(rootTenant.getId());
        tenant.setName("volkswagen-accountancy-client");
        tenant.setStart(LocalDate.now());
        tenant.setEnd(LocalDate.now().plus(3, ChronoUnit.YEARS));

        tenantServiceAccess.create(tenant);

        Tenant tenantSub = new Tenant();
        tenantSub.setType(TenantType.SUB_TENANT);
        tenantSub.setKey("key111");
        tenantSub.setParentId(tenant.getId());
        tenantSub.setClientId(tenant.getId());
        tenantSub.setName("volkswagen-accountancy");
        tenantSub.setStart(LocalDate.now());
        tenantSub.setEnd(LocalDate.now().plus(3, ChronoUnit.YEARS));

        tenantServiceAccess.create(tenantSub);

        SystemTenant systemTenant = tenantServiceAccess.get(tenantSub.getId());
        assertNotNull(systemTenant);
        assertEquals(systemTenant.getType(), TenantType.SUB_TENANT);

        TenantSearchFilter filter = new TenantSearchFilter("volkswagen-accountancy", null, false, false);
        List<? extends SystemTenant> list =
                tenantServiceAccess.get(filter);
        assertNotNull(list);
        assertFalse(list.isEmpty());
    }

    /**
     * Test to validate the creation of a root tenant under a client tenant
     * @throws UniquenessConstraintException in case of duplicates
     * @throws TenantException in case of any issue in the data
     */
    @Test
    public void rootUnderClientException() throws UniquenessConstraintException, TenantException {
        Tenant tenant = new Tenant();
        tenant.setType(TenantType.CLIENT_TENANT);
        tenant.setKey("keyClient1");
        tenant.setParentId(rootTenant.getId());
        tenant.setName("volkswagen-accountancy-client1");
        tenant.setStart(LocalDate.now());
        tenant.setEnd(LocalDate.now().plus(3, ChronoUnit.YEARS));

        tenantServiceAccess.create(tenant);

        Tenant tenantRoot = new Tenant();
        tenantRoot.setType(TenantType.ROOT_TENANT);
        tenantRoot.setKey("keyRoot1");
        tenantRoot.setParentId(tenant.getId());
        tenantRoot.setName("volkswagen-accountancy-root1");
        tenantRoot.setStart(LocalDate.now());
        tenantRoot.setEnd(LocalDate.now().plus(3, ChronoUnit.YEARS));

        TenantException e = assertThrows(TenantException.class, ()->tenantServiceAccess.create(tenantRoot));
        assertEquals("{\"code\":110, \"key\":\"error.tenant.root.with.parent\", \"message\":\"Tenant root cannot have parent associated.\"}", e.getMessage());

        Tenant tenantRoot2 = new Tenant();
        tenantRoot2.setType(TenantType.ROOT_TENANT);
        tenantRoot2.setKey("keyRoot1");
        tenantRoot2.setClientId(tenant.getId());
        tenantRoot2.setName("volkswagen-accountancy-root1");
        tenantRoot2.setStart(LocalDate.now());
        tenantRoot2.setEnd(LocalDate.now().plus(3, ChronoUnit.YEARS));

        TenantException e2 = assertThrows(TenantException.class, ()->tenantServiceAccess.create(tenantRoot2));
        assertEquals("{\"code\":111, \"key\":\"error.tenant.root.with.client\", \"message\":\"Tenant root cannot have client associated.\"}", e2.getMessage());
    }

    /**
     * Tests the creation of a client tenant under a sub tenant
     * @throws UniquenessConstraintException in case of duplicates
     * @throws TenantException in case of any issue in the data
     */
    @Test
    public void clientUnderSubException() throws UniquenessConstraintException, TenantException {
        Tenant tenant = new Tenant();
        tenant.setType(TenantType.SUB_TENANT);
        tenant.setKey("keySub1");
        tenant.setParentId(rootTenant.getId());
        tenant.setClientId(rootTenant.getId());
        tenant.setName("clientUnderSubException1");
        tenant.setStart(LocalDate.now());
        tenant.setEnd(LocalDate.now().plus(3, ChronoUnit.YEARS));

        tenantServiceAccess.create(tenant);

        Tenant tenantRoot = new Tenant();
        tenantRoot.setType(TenantType.CLIENT_TENANT);
        tenantRoot.setKey("keyRoot1");
        tenantRoot.setParentId(tenant.getId());
        tenantRoot.setName("volkswagen-accountancy-root1");
        tenantRoot.setStart(LocalDate.now());
        tenantRoot.setEnd(LocalDate.now().plus(3, ChronoUnit.YEARS));

        TenantException e = assertThrows(TenantException.class, ()->tenantServiceAccess.create(tenantRoot));
        assertEquals("{\"code\":107, \"key\":\"error.tenant.parent.type.invalid\", \"message\":\"Parent type is invalid.\"}", e.getMessage());
    }

    /**
     * Tests the sub tenant rule of non parent
     */
    @Test
    public void subTenantRuleValidationNoParent() {
        Tenant tenant = new Tenant();
        tenant.setType(TenantType.SUB_TENANT);
        tenant.setKey("keySub1");
        tenant.setClientId(rootTenant.getId());
        tenant.setName("volkswagen-accountancy-Sub1");
        tenant.setStart(LocalDate.now());
        tenant.setEnd(LocalDate.now().plus(3, ChronoUnit.YEARS));

        TenantException e = assertThrows(TenantException.class, ()->tenantServiceAccess.create(tenant));
        assertEquals("{\"code\":103, \"key\":\"error.tenant.parent.not.informed\", \"message\":\"Parent information not informed.\"}", e.getMessage());
    }

    /**
     * Tests the sub tenant rule of non client
     */
    @Test
    public void subTenantRuleValidationNoClient() {
        Tenant tenant = new Tenant();
        tenant.setType(TenantType.SUB_TENANT);
        tenant.setKey("keySub1");
        tenant.setParentId(rootTenant.getId());
        tenant.setName("volkswagen-accountancy-Sub1");
        tenant.setStart(LocalDate.now());
        tenant.setEnd(LocalDate.now().plus(3, ChronoUnit.YEARS));

        TenantException e = assertThrows(TenantException.class, ()->tenantServiceAccess.create(tenant));
        assertEquals("{\"code\":104, \"key\":\"error.tenant.client.not.informed\", \"message\":\"Client information not informed.\"}", e.getMessage());
    }

    /**
     * Tests the sub tenant rule of not found parent
     */
    @Test
    public void subTenantRuleValidationNotFoundParent() {
        Tenant tenant = new Tenant();
        tenant.setType(TenantType.SUB_TENANT);
        tenant.setKey("keySub1");
        tenant.setClientId(rootTenant.getId());
        tenant.setParentId(555L);
        tenant.setName("volkswagen-accountancy-Sub1");
        tenant.setStart(LocalDate.now());
        tenant.setEnd(LocalDate.now().plus(3, ChronoUnit.YEARS));

        TenantException e = assertThrows(TenantException.class, ()->tenantServiceAccess.create(tenant));
        assertEquals("{\"code\":105, \"key\":\"error.tenant.parent.not.found\", \"message\":\"Parent registry not found.\"}", e.getMessage());
    }

    /**
     * Tests the sub tenant rule of not found client
     */
    @Test
    public void subTenantRuleValidationNotFountClient() {
        Tenant tenant = new Tenant();
        tenant.setType(TenantType.SUB_TENANT);
        tenant.setKey("keySub1");
        tenant.setParentId(rootTenant.getId());
        tenant.setClientId(200L);
        tenant.setName("volkswagen-accountancy-Sub1");
        tenant.setStart(LocalDate.now());
        tenant.setEnd(LocalDate.now().plus(3, ChronoUnit.YEARS));

        TenantException e = assertThrows(TenantException.class, ()->tenantServiceAccess.create(tenant));
        assertEquals("{\"code\":106, \"key\":\"error.tenant.client.not.found\", \"message\":\"Client registry not found.\"}", e.getMessage());
    }

    /**
     * Test to retrieve all the tenants
     * @throws UniquenessConstraintException in case of duplicates
     * @throws TenantException in case of any issue in the data
     */
    @Test
    public void testRetrieveAllPossibleTenants() throws UniquenessConstraintException, TenantException {

        SystemTenant tenant = new Tenant();
        tenant.setType(TenantType.CLIENT_TENANT);
        tenant.setKey(RandomStringUtils.randomAlphabetic(4));
        tenant.setName("volkswagen-marketing");
        tenant.setStart(LocalDate.now());
        tenant.setParentId(rootTenant.getId());
        tenantServiceAccess.create(tenant);

        tenant = new Tenant();
        tenant.setType(TenantType.CLIENT_TENANT);
        tenant.setKey(RandomStringUtils.randomAlphabetic(4));
        tenant.setName("volkswagen-human-resources");
        tenant.setStart(LocalDate.now());
        tenant.setEnd(LocalDate.now().plus(10, ChronoUnit.YEARS));
        tenant.setParentId(rootTenant.getId());

        tenantServiceAccess.create(tenant);

        // Page size = 100 -> Overkill!!
        Page<SystemTenant> page = tenantServiceAccess.getAll(null,1,100,null,false);
    }

    /**
     * Tests to validate tenant rules
     */
    @Test
    public void creatingInvalidTenant() {
        SystemTenant tenant = new Tenant();
        TenantException e = assertThrows(TenantException.class, ()->tenantServiceAccess.create(tenant));
        assertEquals("{\"code\":102, \"key\":\"error.tenant.field.not.informed\", \"message\":\"Tenant name was not informed.\"}", e.getMessage());

        tenant.setName("test");
        e = assertThrows(TenantException.class, ()->tenantServiceAccess.create(tenant));
        assertEquals("{\"code\":102, \"key\":\"error.tenant.field.not.informed\", \"message\":\"Tenant key was not informed.\"}", e.getMessage());

        tenant.setKey("key-1");
        e = assertThrows(TenantException.class, ()->tenantServiceAccess.create(tenant));
        assertEquals("{\"code\":102, \"key\":\"error.tenant.field.not.informed\", \"message\":\"Tenant type was not informed.\"}", e.getMessage());

        tenant.setType(TenantType.CLIENT_TENANT);
        tenant.setEnd(LocalDate.now().minus(3, ChronoUnit.MONTHS));
        tenant.setStart(LocalDate.now());

        e = assertThrows(TenantException.class, ()->tenantServiceAccess.create(tenant));
        assertEquals("{\"code\":108, \"key\":\"error.tenant.end.date.invalid\", \"message\":\"End date is invalid.\"}", e.getMessage());

        tenant.setEnd(LocalDate.now().plus(3, ChronoUnit.MONTHS));

        e = assertThrows(TenantException.class, ()->tenantServiceAccess.create(tenant));
        assertEquals("{\"code\":103, \"key\":\"error.tenant.parent.not.informed\", \"message\":\"Parent information not informed.\"}", e.getMessage());

        tenant.setParentId(111L);
        e = assertThrows(TenantException.class, ()->tenantServiceAccess.create(tenant));
        assertEquals("{\"code\":105, \"key\":\"error.tenant.parent.not.found\", \"message\":\"Parent registry not found.\"}", e.getMessage());

        SystemTenant newTenantRoot = new Tenant();
        newTenantRoot.setType(TenantType.ROOT_TENANT);
        newTenantRoot.setName("root");
        newTenantRoot.setKey("root-1");
        newTenantRoot.setStart(LocalDate.now());
        newTenantRoot.setEnd(LocalDate.now().plus(5, ChronoUnit.MONTHS));
        newTenantRoot.setParentId(111L);

        e = assertThrows(TenantException.class, ()->tenantServiceAccess.create(newTenantRoot));
        assertEquals("{\"code\":110, \"key\":\"error.tenant.root.with.parent\", \"message\":\"Tenant root cannot have parent associated.\"}", e.getMessage());

        newTenantRoot.setParentId(null);
    }
}