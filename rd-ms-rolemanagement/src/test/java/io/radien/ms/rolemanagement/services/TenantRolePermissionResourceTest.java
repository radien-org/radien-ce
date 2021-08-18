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

import io.radien.exception.TenantRoleException;
import io.radien.exception.TenantRoleNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.client.entities.TenantRolePermission;
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * TenantRolePermission resource requests test
 * {@link TenantRoleResource}
 *
 * @author Newton Carvalho
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TenantRolePermissionResourceTest {

    @InjectMocks
    TenantRolePermissionResource tenantRolePermissionResource;

    @Mock
    TenantRolePermissionBusinessService tenantRolePermissionBusinessService;

    @BeforeEach
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Tests response from assignPermission method
     */
    @Test
    @Order(1)
    public void testAssignPermission() {
        Response response = tenantRolePermissionResource.assignPermission(new TenantRolePermission());
        assertEquals(200, response.getStatus());
    }

    /**
     * Tests response from assignPermission method when exceptions occur
     * during the processing
     */
    @Test
    @Order(2)
    public void testAssignPermissionWithException() {
        TenantRolePermission tenantRolePermission = new TenantRolePermission();
        try {
            doThrow(new UniquenessConstraintException("error")).
                    doThrow(new TenantRoleException("error")).
                    doThrow(new RuntimeException("error")).
                    when(tenantRolePermissionBusinessService).assignPermission(any());
        }
        catch (Exception e) {
            fail("unexpected");
        }
        Response response = tenantRolePermissionResource.assignPermission(tenantRolePermission);
        assertEquals(400, response.getStatus());

        response = tenantRolePermissionResource.assignPermission(tenantRolePermission);
        assertEquals(400, response.getStatus());

        response = tenantRolePermissionResource.assignPermission(tenantRolePermission);
        assertEquals(500, response.getStatus());
    }

    /**
     * Tests response from unassignPermission method
     */
    @Test
    @Order(3)
    public void testUnAssignPermission() {
        Response response = tenantRolePermissionResource.unAssignPermission(1L,
                2L, 3L);
        assertEquals(200, response.getStatus());
    }

    /**
     * Tests response from unassignPermission method when exceptions occur
     * during the processing
     */
    @Test
    @Order(4)
    public void testUnAssignPermissionWithException() {
        try {
            doThrow(new TenantRoleException("error")).
                    doThrow(new RuntimeException("error")).
                    when(tenantRolePermissionBusinessService).unAssignPermission(1L,
                    2L, 3L);
        }
        catch (Exception e) {
            fail("unexpected");
        }
        Response response = tenantRolePermissionResource.unAssignPermission(1L,
                2L, 3L);
        assertEquals(400, response.getStatus());

        response = tenantRolePermissionResource.unAssignPermission(1L,
                2L, 3L);
        assertEquals(500, response.getStatus());
    }

    /**
     * Tests response from delete method
     */
    @Test
    @Order(5)
    public void testDelete() {
        Response response = tenantRolePermissionResource.delete(1L);
        assertEquals(200,response.getStatus());
    }

    /**
     * Tests response from delete method when exceptions occur during the processing
     */
    @Test
    @Order(6)
    public void testDeleteWithException() {
        try {
            when(tenantRolePermissionBusinessService.delete(1L)).
                    thenThrow(new TenantRoleNotFoundException("Tenant role permission cannot be found"));

            when(tenantRolePermissionBusinessService.delete(2L)).
                    thenThrow(new RuntimeException("unpredictable error"));
        }
        catch (Exception e) {
            fail("unexpected");
        }
        Response response = tenantRolePermissionResource.delete(1L);
        assertEquals(400,response.getStatus());
        response = tenantRolePermissionResource.delete(2L);
        assertEquals(500,response.getStatus());
    }

    /**
     * Tests response from getAll method
     */
    @Test
    @Order(7)
    public void testGetUsers() {
        Response response = tenantRolePermissionResource.getAll(1L,1L,
                2, 3);
        assertEquals(200, response.getStatus());
    }

    /**
     * Tests response from getAll method when exceptions occur
     * during the processing
     */
    @Test
    @Order(8)
    public void testGetAllWithException() {
        doThrow(new RuntimeException("error")).
                when(tenantRolePermissionBusinessService).getAll(1L,1L,
                2, 3);
        Response response = tenantRolePermissionResource.getAll(1L,1L,
                2, 3);
        assertEquals(500, response.getStatus());
    }
}
