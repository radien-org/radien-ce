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

import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.exception.SystemException;
import io.radien.exception.TenantRoleException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.client.entities.TenantRole;
import io.radien.ms.rolemanagement.client.exception.LinkedAuthorizationErrorCodeMessage;
import org.apache.openejb.mockito.MockitoExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMapOf;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * @author Newton Carvalho
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TenantRoleResourceTest {

    @InjectMocks
    TenantRoleResource tenantRoleResource;

    @Mock
    TenantRoleBusinessService tenantRoleBusinessService;

    @BeforeEach
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Testing getAll
     */
    @Test
    @Order(1)
    public void getAll() {
        Response response = tenantRoleResource.getAll(1,10);
        assertEquals(200,response.getStatus());
    }

    /**
     * Testing getAll with exception during the processing
     */
    @Test
    @Order(2)
    public void getAllWithException() {
        when(tenantRoleResource.getAll(1,10))
                .thenThrow(new RuntimeException());
        Response response = tenantRoleResource.getAll(1,10);
        assertEquals(500,response.getStatus());
    }

    /**
     * Tests response of the get specific association
     */
    @Test
    @Order(3)
    public void testGetSpecific() {
        Response response = tenantRoleResource.getSpecific(2L,2L,true);
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests exception from the get specific association
     */
    @Test
    @Order(4)
    public void testGetSpecificWithException() {
        doThrow(new RuntimeException("error")).when(tenantRoleBusinessService).
                getSpecific(1L, 2L, true);
        Response response = tenantRoleResource.getSpecific(1L,2L,true);
        assertEquals(500,response.getStatus());
    }

    /**
     * Tests response from getById method
     */
    @Test
    @Order(5)
    public void testGetById() {
        Response response = tenantRoleResource.getById(1L);
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests response from getById when exceptions occur during the processing
     */
    @Test
    @Order(6)
    public void testGetByIdWithExceptions() {
        try {
            doThrow(new TenantRoleException("No Tenant Role found for id")).
                    when(tenantRoleBusinessService).getById(1L);
            doThrow(new RuntimeException("error")).
                    when(tenantRoleBusinessService).getById(2L);
        }
        catch (Exception e) {
            fail("unexpected");
        }

        // Association Not Found
        Response response = tenantRoleResource.getById(1L);
        assertEquals(404,response.getStatus());

        // Generic Error
        response = tenantRoleResource.getById(2L);
        assertEquals(500,response.getStatus());
    }

    /**
     * Tests response from delete method
     */
    @Test
    @Order(7)
    public void testDelete() {
        Response response = tenantRoleResource.delete(1L);
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests response from delete method when exceptions occur during the processing
     */
    @Test
    @Order(8)
    public void testDeleteWithException() {
        try {
            when(tenantRoleBusinessService.delete(1L)).
                    thenThrow(new TenantRoleException("Tenant role cannot be deleted"));

            when(tenantRoleBusinessService.delete(2L)).
                    thenThrow(new RuntimeException("unpredictable error"));
        }
        catch (Exception e) {
            fail("unexpected");
        }
        Response response = tenantRoleResource.delete(1L);
        assertEquals(400,response.getStatus());
        response = tenantRoleResource.delete(2L);
        assertEquals(500,response.getStatus());
    }

    /**
     * Tests response from save method
     */
    @Test
    @Order(9)
    public void testSave() {
        Response response = tenantRoleResource.save(new TenantRole());
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests response from save method when exceptions occur during the processing
     */
    @Test
    @Order(10)
    public void testSaveWithException() {
        TenantRole tenantRole = new TenantRole();
        tenantRole.setRoleId(1L); tenantRole.setTenantId(2L);
        try {
            doThrow(new UniquenessConstraintException()).doThrow(new TenantRoleException("Found similar tenant role")).
                    doThrow(new SystemException("Communication breakdown")).when(tenantRoleBusinessService).save(any());
        }
        catch (Exception e) {
            fail("unexpected");
        }
        Response response = tenantRoleResource.save(tenantRole);
        assertEquals(400,response.getStatus());

        response = tenantRoleResource.save(tenantRole);
        assertEquals(400,response.getStatus());

        response = tenantRoleResource.save(tenantRole);
        assertEquals(500,response.getStatus());
    }

    /**
     * Tests response from exists method
     */
    @Test
    @Order(11)
    public void testExists() {
        Response response = tenantRoleResource.exists(1L, 2L);
        assertEquals(200, response.getStatus());
    }

    /**
     * Tests response from exists method when exceptions occur during the processing
     */
    @Test
    @Order(12)
    public void testExistsWithException() {
        doThrow(new RuntimeException("error")).
                when(tenantRoleBusinessService).existsAssociation(1L, 2L);
        Response response = tenantRoleResource.exists(1L, 2L);
        assertEquals(500, response.getStatus());
    }

    /**
     * Tests response from getPermissions method
     */
    @Test
    @Order(13)
    public void testGetPermissions() {
        Response response = tenantRoleResource.getPermissions(1L, 2L, 3L);
        assertEquals(200, response.getStatus());
    }

    /**
     * Tests response from getPermissions method when exceptions occur during the processing
     */
    @Test
    @Order(14)
    public void testGetPermissionsWithException() {
        try {
            doThrow(new RuntimeException("error")).
                    when(tenantRoleBusinessService).getPermissions(1L, 2L, 3L);
        }
        catch (Exception e) {
            fail("unexpected");
        }
        Response response = tenantRoleResource.getPermissions(1L, 2L, 3L);
        assertEquals(500, response.getStatus());
    }

    /**
     * Tests response from getTenants method
     */
    @Test
    @Order(15)
    public void testGetTenants() {
        Response response = tenantRoleResource.getTenants(1L, 2L);
        assertEquals(200, response.getStatus());
    }

    /**
     * Tests response from getTenants method when exceptions occur during the processing
     */
    @Test
    @Order(16)
    public void testGetTenantsWithException() {
        try {
            doThrow(new RuntimeException("error")).
                    when(tenantRoleBusinessService).getTenants(1L, 2L);
        }
        catch (Exception e) {
            fail("unexpected");
        }
        Response response = tenantRoleResource.getTenants(1L, 2L);
        assertEquals(500, response.getStatus());
    }

    /**
     * Tests response from isRoleExistentForUser method
     */
    @Test
    @Order(17)
    public void testIsRoleExistentForUser() {
        Response response = tenantRoleResource.isRoleExistentForUser(1L, "test", 2L);
        assertEquals(200, response.getStatus());
    }

    /**
     * Tests response from isRoleExistentForUser method when exceptions occur
     * during the processing
     */
    @Test
    @Order(18)
    public void testIsRoleExistentForUserWithException() {
        try {
            doThrow(new RuntimeException("error")).
                    when(tenantRoleBusinessService).isRoleExistentForUser(1L, "test", 2L);
        }
        catch (Exception e) {
            fail("unexpected");
        }
        Response response = tenantRoleResource.isRoleExistentForUser(1L, "test", 2L);
        assertEquals(500, response.getStatus());
    }

    /**
     * Tests response from isAnyRoleExistentForUser method
     */
    @Test
    @Order(19)
    public void testIsAnyRoleExistentForUser() {
        Response response = tenantRoleResource.isAnyRoleExistentForUser(1L,
                Arrays.asList("test", "test2"), 2L);
        assertEquals(200, response.getStatus());
    }

    /**
     * Tests response from isAnyRoleExistentForUser method when exceptions occur
     * during the processing
     */
    @Test
    @Order(20)
    public void testIsAnyRoleExistentForUserWithException() {
        try {
            doThrow(new RuntimeException("error")).
                    when(tenantRoleBusinessService).isAnyRoleExistentForUser(1L,
                    Arrays.asList("test", "test2"), 2L);
        }
        catch (Exception e) {
            fail("unexpected");
        }
        Response response = tenantRoleResource.isAnyRoleExistentForUser(1L,
                Arrays.asList("test", "test2"), 2L);
        assertEquals(500, response.getStatus());
    }

    /**
     * Tests response from isPermissionExistentForUser method
     */
    @Test
    @Order(21)
    public void testIsPermissionExistentForUser() {
        Response response = tenantRoleResource.isPermissionExistentForUser(1L,
                2L, 3L);
        assertEquals(200, response.getStatus());
    }

    /**
     * Tests response from isPermissionExistentForUser method when exceptions occur
     * during the processing
     */
    @Test
    @Order(22)
    public void testIsPermissionExistentForUserWithException() {
        try {
            doThrow(new RuntimeException("error")).
                    when(tenantRoleBusinessService).isPermissionExistentForUser(1L,
                    2L, 3L);
        }
        catch (Exception e) {
            fail("unexpected");
        }
        Response response = tenantRoleResource.isPermissionExistentForUser(1L,
                2L, 3L);
        assertEquals(500, response.getStatus());
    }

    /**
     * Tests response from assignUser method
     */
    @Test
    @Order(23)
    public void testAssignUser() {
        Response response = tenantRoleResource.assignUser(1L,
                2L, 3L);
        assertEquals(200, response.getStatus());
    }

    /**
     * Tests response from assignUser method when exceptions occur
     * during the processing
     */
    @Test
    @Order(24)
    public void testAssignUserWithException() {
        try {
            doThrow(new UniquenessConstraintException("error")).
                    doThrow(new TenantRoleException("error")).
                    doThrow(new RuntimeException("error")).
                    when(tenantRoleBusinessService).assignUser(1L,
                    2L, 3L);
        }
        catch (Exception e) {
            fail("unexpected");
        }
        Response response = tenantRoleResource.assignUser(1L,
                2L, 3L);
        assertEquals(400, response.getStatus());

        response = tenantRoleResource.assignUser(1L,
                2L, 3L);
        assertEquals(400, response.getStatus());

        response = tenantRoleResource.assignUser(1L,
                2L, 3L);
        assertEquals(500, response.getStatus());
    }

    /**
     * Tests response from unassignUser method
     */
    @Test
    @Order(25)
    public void testUnAssignUser() {
        Response response = tenantRoleResource.unassignUser(1L,
                2L, 3L);
        assertEquals(200, response.getStatus());
    }

    /**
     * Tests response from unassignUser method when exceptions occur
     * during the processing
     */
    @Test
    @Order(26)
    public void testUnAssignUserWithException() {
        try {
            doThrow(new TenantRoleException("error")).
                    doThrow(new RuntimeException("error")).
                    when(tenantRoleBusinessService).unassignUser(1L,
                    2L, 3L);
        }
        catch (Exception e) {
            fail("unexpected");
        }
        Response response = tenantRoleResource.unassignUser(1L,
                2L, 3L);
        assertEquals(400, response.getStatus());

        response = tenantRoleResource.unassignUser(1L,
                2L, 3L);
        assertEquals(500, response.getStatus());
    }

    /**
     * Tests response from assignPermission method
     */
    @Test
    @Order(27)
    public void testAssignPermission() {
        Response response = tenantRoleResource.assignPermission(1L,
                2L, 3L);
        assertEquals(200, response.getStatus());
    }

    /**
     * Tests response from assignPermission method when exceptions occur
     * during the processing
     */
    @Test
    @Order(28)
    public void testAssignPermissionWithException() {
        try {
            doThrow(new UniquenessConstraintException("error")).
                    doThrow(new TenantRoleException("error")).
                    doThrow(new RuntimeException("error")).
                    when(tenantRoleBusinessService).assignPermission(1L,
                    2L, 3L);
        }
        catch (Exception e) {
            fail("unexpected");
        }
        Response response = tenantRoleResource.assignPermission(1L,
                2L, 3L);
        assertEquals(400, response.getStatus());

        response = tenantRoleResource.assignPermission(1L,
                2L, 3L);
        assertEquals(400, response.getStatus());

        response = tenantRoleResource.assignPermission(1L,
                2L, 3L);
        assertEquals(500, response.getStatus());
    }

    /**
     * Tests response from unassignPermission method
     */
    @Test
    @Order(29)
    public void testUnAssignPermission() {
        Response response = tenantRoleResource.unassignPermission(1L,
                2L, 3L);
        assertEquals(200, response.getStatus());
    }

    /**
     * Tests response from unassignPermission method when exceptions occur
     * during the processing
     */
    @Test
    @Order(30)
    public void testUnAssignPermissionWithException() {
        try {
            doThrow(new TenantRoleException("error")).
                    doThrow(new RuntimeException("error")).
                    when(tenantRoleBusinessService).unassignPermission(1L,
                    2L, 3L);
        }
        catch (Exception e) {
            fail("unexpected");
        }
        Response response = tenantRoleResource.unassignPermission(1L,
                2L, 3L);
        assertEquals(400, response.getStatus());

        response = tenantRoleResource.unassignPermission(1L,
                2L, 3L);
        assertEquals(500, response.getStatus());
    }

}