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
package io.radien.ms.permissionmanagement.service;

import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemResource;
import io.radien.api.service.permission.ResourceServiceAccess;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.permissionmanagement.client.entities.ResourceSearchFilter;
import io.radien.ms.permissionmanagement.model.ResourceEntity;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import java.util.Properties;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Resource Service test, to test the crud requests and responses
 * {@link io.radien.ms.permissionmanagement.service.ResourceService}
 *
 * @author Nuno Santana
 * @author Bruno Gama
 */
public class ResourceServiceTest {

    static ResourceServiceAccess resourceServiceAccess;
    static SystemResource resourceTest;
    static EJBContainer container;

    /**
     * Constructor method to prepare all the variables and properties before running the tests
     * @throws Exception in case of any issue
     */
    @BeforeClass
    public static void start() throws Exception {
        Properties p = new Properties();
        p.put("openejb.deployments.classpath.include",".*permission.*");
        p.put("openejb.deployments.classpath.exclude",".*client.*");
        container = EJBContainer.createEJBContainer(p);
        final Context context = container.getContext();

        resourceServiceAccess = (ResourceServiceAccess) 
                context.lookup("java:global/rd-ms-permissionmanagement//ResourceService");

        Page<? extends SystemResource> resourcePage =
                resourceServiceAccess.getAll(null, 1, 10, null, true);
        if (resourcePage.getTotalResults() > 0) {
            resourceTest = resourcePage.getResults().get(0);
        } else {
            resourceTest = createResource("resourceName", 2L);
            resourceServiceAccess.save(resourceTest);
        }
    }

    /**
     * Injection method before starting the tests and data preparation
     * @throws NamingException in case of naming injection value exception
     */
    @Before
    public void inject() throws NamingException {
        container.getContext().bind("inject", this);
    }

    /**
     * Method to stop the container after the testing classes have perform
     */
    @AfterClass
    public static void stop() {
        if (container != null) {
            container.close();
        }
    }

    /**
     * Add Resource test.
     * Will create and save the Resource.
     * Expected result: Success.
     * Tested methods: void save(Resource Resource)
     */
    @Test
    public void testAddResource() {
        SystemResource result = resourceServiceAccess.get(resourceTest.getId());
        assertNotNull(result);
    }

    /**
     * Method to test the action of attempting to retrieve a non existent resource
     */
    @Test
    public void testGetNotExistentResource() {
        SystemResource result = resourceServiceAccess.get(111111111L);
        assertNull(result);
    }

    /**
     * Add Resource test with duplicated Name.
     * Will create and save the Resource, with an already existent name.
     * Expected result: Throw treated exception Error 103 Message There is more than one Resource with the same Name.
     * Tested methods: void save(Resource Resource)
     */
    @Test
    public void testAddDuplicatedName() throws UniquenessConstraintException {
        ResourceEntity r1 = createResource("resourceNameXXX", 2L);
        resourceServiceAccess.save(r1);

        ResourceEntity r2 = createResource("resourceNameXXX", 2L);
        Exception exception = assertThrows(UniquenessConstraintException.class, () -> resourceServiceAccess.save(r2));
        String expectedMessage = GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Name");
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    /**
     * Gets Resource using the PK (id).
     * Will create a new Resource, save it into the DB and retrieve the specific Resource using the ID.
     * Expected result: will return the correct inserted Resource.
     * Tested methods: SystemResource get(Long ResourceId)
     *
     * @throws UniquenessConstraintException in case of requested resource is not well constructed
     */
    @Test
    public void testGetById() throws UniquenessConstraintException {
        ResourceEntity u = createResource("testGetIdFirstName", 2L);
        resourceServiceAccess.save(u);
        SystemResource result = resourceServiceAccess.get(u.getId());
        assertNotNull(result);
        assertEquals(u.getName(), result.getName());
    }

    /**
     * Gets multiple Resources using a list of PK (id).
     * Will create 2 new Resources, save them into the DB and retrieve the specific Resources using the ID's.
     * Expected result: will return the correct number of inserted Resource.
     * Tested methods: List<SystemResource> get(List<Long> resourceIds)
     *
     * @throws UniquenessConstraintException in case of requested resource is not well constructed
     */
    @Test
    public void testGetByListOfIds() throws UniquenessConstraintException {
        ResourceEntity r1 = createResource("testGetByListOfIdsFirstName1", 2L);
        resourceServiceAccess.save(r1);

        ResourceEntity r2 = createResource("testGetByListOfIdsFirstName2", 2L);
        resourceServiceAccess.save(r2);

        List<Long> ResourceIds = Arrays.asList(r1.getId(), r2.getId());
        List<SystemResource> result = resourceServiceAccess.get(ResourceIds);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    /**
     * Method to test the retrieval of a given empty list of id's, should return empty
     */
    @Test
    public void testGetByEmptyListOfIds() {

        List<SystemResource> result = resourceServiceAccess.get(new ArrayList<>());
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Deletes inserted Resource using the PK (id).
     * Will create a new Resource, save it into the DB and delete it after using the specific ID.
     * Expected result: will return null when retrieving the Resource.
     * Tested methods: void delete(Long ResourceId)
     */
    @Test
    public void testDeleteById() {
        SystemResource result = resourceServiceAccess.get(resourceTest.getId());
        assertNotNull(result);
        assertEquals(resourceTest.getName(), result.getName());
        resourceServiceAccess.delete(resourceTest.getId());
        result = resourceServiceAccess.get(resourceTest.getId());
        assertNull(result);
    }

    /**
     * Deletes multiple Resources using a list of PK (id).
     * Will create 3 new Resources, save them into the DB and delete the specific 2 Resources using the ID's.
     * Expected result: will delete the correct number of inserted Resource and return only one of them (the not deleted
     * one).
     * Tested methods: void delete(Collection<Long> ResourceIds)
     *
     * @throws UniquenessConstraintException in case of requested resource is not well constructed
     */
    @Test
    public void testDeleteByListOfIds() throws UniquenessConstraintException {
        SystemResource r1 = createResource("testDeleteByListOfIdsFirstName1", 2L);
        resourceServiceAccess.save(r1);

        SystemResource r2 = createResource("testDeleteByListOfIdsFirstName2",2L);
        resourceServiceAccess.save(r2);

        SystemResource r3 = createResource("testDeleteByListOfIdsFirstName3",2L);
        resourceServiceAccess.save(r3);

        List<Long> resourceIds = Arrays.asList(r1.getId(), r2.getId());
        resourceServiceAccess.delete(resourceIds);
        List<SystemResource> result = resourceServiceAccess.get(resourceIds);
        assertEquals(0, result.size());
        SystemResource resultExistentOne = resourceServiceAccess.get(r3.getId());
        assertNotNull(result);
        assertEquals(r3.getName(), resultExistentOne.getName());
    }

    /**
     * Method to test the deletion of a given empty list of id's
     */
    @Test
    public void testDeleteByEmptyListOfIds() {
        Collection<Long> resourceIds = null;
        try {
            this.resourceServiceAccess.delete(resourceIds);
            resourceIds = new ArrayList<>();
            this.resourceServiceAccess.delete(resourceIds);
        } catch (Exception e) {
            fail("should not throw an exception");
        }
    }

    /**
     * Test updates the Resource information. 
     * Should be a success in this test case, if the name attribute from the Resource one, 
     * should have been updated to the Resource three information.
     */
    @Test
    public void testUpdateSuccess() throws UniquenessConstraintException {
        SystemResource r1 = createResource("testUpdateResourceName1",2L);
        resourceServiceAccess.save(r1);

        SystemResource r2 = createResource("testUpdateResourceName2",2L);
        resourceServiceAccess.save(r2);

        SystemResource r3 = createResource("testUpdateResourceName1",2L);

        r3.setId(r1.getId());

        resourceServiceAccess.save(r3);

        r1 = resourceServiceAccess.get(r1.getId());

        assertEquals(r1.getName(), r3.getName());
        SystemResource r4 = createResource("testUpdateResourceName4",2L);

        r4.setId(r1.getId());

        resourceServiceAccess.save(r4);

        r1 = resourceServiceAccess.get(r1.getId());

        assertEquals(r1.getName(), r4.getName());

    }

    /**
     * Method to test updating with failure multiple resources
     * @throws Exception to be trow
     */
    @Test
    public void testUpdateFailureMultipleRecords() throws Exception {
        ResourceEntity r1 = createResource("resourceName1", 2L);
        resourceServiceAccess.save(r1);

        ResourceEntity r2 = createResource("resourceName2", 2L);
        resourceServiceAccess.save(r2);

        ResourceEntity r3 = createResource("resourceName3", 2L);
        resourceServiceAccess.save(r3);

        ResourceEntity r4 = createResource("resourceName1", 2L);

        Exception exceptionForRepeatedName = assertThrows(Exception.class, () -> resourceServiceAccess.save(r4));
        String exceptionForRepeatedNameMessage = exceptionForRepeatedName.getMessage();
        String expectedMessage = GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Name");
        assertTrue(exceptionForRepeatedNameMessage.contains(expectedMessage));

    }

    /**
     * Test updates the Resource information. Should be a failure in this test case and no information should be updated,
     * since that we are updating Resource one with the information from Resource three, but using a duplicated email address.
     * @throws UniquenessConstraintException in case of Resource to be updated not found
     */
    @Test
    public void testUpdateFailureDuplicatedName() throws UniquenessConstraintException {
        String expectedMessageName = GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Name");

        ResourceEntity r1 = createResource("resourceNamePerm1", 2L);
        resourceServiceAccess.save(r1);

        ResourceEntity r2 = createResource("resourceNamePerm2", 2L);
        resourceServiceAccess.save(r2);

        ResourceEntity r3 = createResource("resourceNamePerm1", 2L);

        Exception exceptionForFieldName = assertThrows(Exception.class, () -> resourceServiceAccess.save(r3));
        String actualMessage = exceptionForFieldName.getMessage();
        assertTrue(actualMessage.contains(expectedMessageName));

        ResourceEntity r4 = createResource("resourceNamePerm2", 2L);

        Exception exceptionName2 = assertThrows(Exception.class, () -> resourceServiceAccess.save(r4));
        String messageFromException = exceptionName2.getMessage();
        assertTrue(messageFromException.contains(expectedMessageName));
    }

    /**
     * Test to get all the resources but with a specific given sort criteria
     * @throws UniquenessConstraintException in case of one or multiple fields with incorrect or invalid data
     */
    @Test
    public void testGetAllSort() throws UniquenessConstraintException {
        SystemResource r1 = createResource("a", 2L);
        resourceServiceAccess.save(r1);
        SystemResource r2 = createResource("zzz", 2L);
        resourceServiceAccess.save(r2);
        SystemResource r3 = createResource("d", 2L);
        resourceServiceAccess.save(r3);

        List<String> orderby = new ArrayList<>();
        orderby.add("name");

        Page<? extends SystemResource> resourcePage = resourceServiceAccess.getAll(null, 1, 10,
                orderby, true);
  
        assertTrue(resourcePage.getTotalResults()>=3);

        assertEquals("a",resourcePage.getResults().get(0).getName());

        resourcePage = resourceServiceAccess.getAll(null, 1, 10, orderby, false);
        assertTrue(resourcePage.getTotalResults()>=3);
        assertEquals("zzz",resourcePage.getResults().get(0).getName());

        Page<? extends SystemResource> resourcePageWhere = resourceServiceAccess.getAll("a", 1, 10, null, true);
        assertEquals(1, resourcePageWhere.getTotalResults());

        assertEquals("a",resourcePageWhere.getResults().get(0).getName());
    }

    /**
     * Test to try to get resources by id with exact and without exact logical search (and or or)
     * @throws UniquenessConstraintException in case of one or multiple fields with incorrect or invalid data
     */
    @Test
    public void testGetByIsExactOrLogical() throws UniquenessConstraintException {
        SystemResource sr1 = createResource("zz", 1L);
        SystemResource sr2 = createResource("aa", 1L);
        SystemResource sr3 = createResource("aabb", 1L);
        SystemResource sr4 = createResource("aabaco", 1L);
        SystemResource sr5 = createResource("aabaco0", 1L);
        SystemResource sr6 = createResource("ddd", 1L);
        SystemResource sr7 = createResource("xxx", 1L);

        resourceServiceAccess.save(sr1);
        resourceServiceAccess.save(sr2);
        resourceServiceAccess.save(sr3);
        resourceServiceAccess.save(sr4);
        resourceServiceAccess.save(sr5);
        resourceServiceAccess.save(sr6);
        resourceServiceAccess.save(sr7);

        List<? extends SystemResource> resourcesAnd = resourceServiceAccess.getResources(
                new ResourceSearchFilter("zz",null,true,true));
        assertEquals(1, resourcesAnd.size());

        List<? extends SystemResource> resourcesOr = resourceServiceAccess.getResources(
                new ResourceSearchFilter("aa", null,true,false));
        assertEquals(1, resourcesOr.size());

        List<? extends SystemResource> resourcesNotExact = resourceServiceAccess.getResources(
                new ResourceSearchFilter("aa", null,false,true));
        assertEquals(4, resourcesNotExact.size());

        List<? extends SystemResource> resources = resourceServiceAccess.getResources(
                new ResourceSearchFilter("aabac", null,false,false));

        assertEquals(2, resources.size());

        ResourceSearchFilter resourceSearchFilter = new ResourceSearchFilter();
        resourceSearchFilter.setName("aabac");
        resourceSearchFilter.setExact(false);
        resourceSearchFilter.setLogicConjunction(true);
        resources = resourceServiceAccess.getResources(resourceSearchFilter);

        assertEquals(2, resources.size());

        resources = resourceServiceAccess.getResources(
                new ResourceSearchFilter(null, null,false,true));

        assertEquals(8, resources.size());

        resources = resourceServiceAccess.getResources(new ResourceSearchFilter("xxx", null,true,true));

        assertEquals(1, resources.size());

    }

    /**
     * Private method to create resources for testing purposes
     * @param name of the resource
     * @param user to be created for the resource
     * @return the created resource
     */
    private static ResourceEntity createResource(String name, Long user) {
        ResourceEntity r = new ResourceEntity();
        r.setName(name);
        r.setCreateUser(user);
        return r;
    }

    /**
     * Test to try to get resources by filtering them by a list of ids
     * @throws UniquenessConstraintException in case of one or multiple fields with incorrect or invalid data
     */
    @Test
    public void testFilterResourcesByListOfIds() throws UniquenessConstraintException {
        SystemResource testById1 = createResource("resource-dummy-1", 1L);
        SystemResource testById2 = createResource("resource-dummy-2", 1L);
        SystemResource testById3 = createResource("resource-dummy-3", 1L);
        SystemResource testById4 = createResource("resource-dummy-4", 1L);

        resourceServiceAccess.save(testById1);
        resourceServiceAccess.save(testById2);
        resourceServiceAccess.save(testById3);
        resourceServiceAccess.save(testById4);

        List<Long> ids = Arrays.asList(testById1.getId(), testById2.getId(), testById3.getId(), testById4.getId());

        List<? extends SystemResource> filteredResources = resourceServiceAccess.getResources(
                new ResourceSearchFilter(null, ids, true, true));
        assertEquals(4, filteredResources.size());
    }

}
