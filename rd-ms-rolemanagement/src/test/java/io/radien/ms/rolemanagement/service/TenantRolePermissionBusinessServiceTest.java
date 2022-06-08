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
package io.radien.ms.rolemanagement.service;

import io.radien.api.entity.Page;
import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.service.permission.PermissionRESTServiceAccess;
import io.radien.api.service.role.exception.RoleException;
import io.radien.api.service.role.exception.RoleNotFoundException;
import io.radien.api.service.tenantrole.TenantRolePermissionServiceAccess;
import io.radien.exception.BadRequestException;
import io.radien.exception.InvalidArgumentException;
import io.radien.exception.SystemException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.permissionmanagement.client.entities.Permission;
import io.radien.ms.rolemanagement.client.entities.TenantRole;
import io.radien.ms.rolemanagement.client.entities.TenantRolePermissionSearchFilter;
import io.radien.ms.rolemanagement.entities.TenantRolePermissionEntity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * TenantRolePermissionBusinessService requests test
 * {@link TenantRolePermissionBusinessService}
 *
 * @author Newton Carvalho
 */
public class TenantRolePermissionBusinessServiceTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @InjectMocks
    TenantRolePermissionBusinessService target;

    @Mock
    PermissionRESTServiceAccess permissionClient;

    @Mock
    TenantRoleBusinessService tenantRoleServiceAccess;

    @Mock
    TenantRolePermissionServiceAccess tenantRolePermissionServiceAccess;

    @Test
    public void testGetAll() {
        when(tenantRolePermissionServiceAccess.getAll(anyLong(), anyLong(), anyInt(), anyInt(), anyList(), anyBoolean()))
                .thenReturn(new Page<>(new ArrayList<>(), 1, 1, 1));

        assertNotNull(target.getAll(1L, 1L, 1, 1, new ArrayList<>(), false));
    }

    @Test
    public void testGetFiltered() {
        assertTrue(target.getFiltered(new TenantRolePermissionSearchFilter()).isEmpty());
    }

    @Test
    public void testGetById() {
        when(tenantRolePermissionServiceAccess.get(anyLong()))
                .thenReturn(new TenantRolePermissionEntity());
        assertNotNull(target.getById(1L));
    }

    @Test(expected = RoleNotFoundException.class)
    public void testGetByIdNotFound() {
        when(tenantRolePermissionServiceAccess.get(anyLong()))
                .thenReturn(null);
        target.getById(1L);
    }

    @Test
    public void testDelete() {
        when(tenantRolePermissionServiceAccess.delete(anyLong()))
                .thenReturn(true);
        target.delete(1L);
        verify(tenantRolePermissionServiceAccess).delete(anyLong());
    }

    @Test(expected = RoleNotFoundException.class)
    public void testDeleteNotFound() {
        when(tenantRolePermissionServiceAccess.delete(anyLong()))
                .thenReturn(false);
        target.delete(1L);
    }

    @Test
    public void testAssignPermission() throws SystemException, InvalidArgumentException, UniquenessConstraintException {
        TenantRolePermissionEntity entity = new TenantRolePermissionEntity();
        entity.setPermissionId(1L);
        entity.setTenantRoleId(1L);
        when(permissionClient.isPermissionExistent(1L, null)).thenReturn(true);
        when(tenantRolePermissionServiceAccess.isAssociationAlreadyExistent(anyLong(), anyLong())).thenReturn(false);

        target.assignPermission(entity);
        verify(tenantRolePermissionServiceAccess).create(entity);
    }

    @Test(expected = RoleException.class)
    public void testAssignPermissionAlreadyExistent() throws SystemException, InvalidArgumentException {
        TenantRolePermissionEntity entity = new TenantRolePermissionEntity();
        entity.setPermissionId(1L);
        entity.setTenantRoleId(1L);
        when(permissionClient.isPermissionExistent(1L, null)).thenReturn(true);
        when(tenantRolePermissionServiceAccess.isAssociationAlreadyExistent(anyLong(), anyLong())).thenReturn(true);
        SystemTenantRole tenantRole = new TenantRole();
        tenantRole.setTenantId(1L);
        tenantRole.setRoleId(1L);
        when(tenantRoleServiceAccess.getById(1L)).thenReturn(tenantRole);


        target.assignPermission(entity);
    }

    @Test(expected = BadRequestException.class)
    public void testAssignPermissionBadRequest() throws SystemException, InvalidArgumentException {
        TenantRolePermissionEntity entity = new TenantRolePermissionEntity();
        entity.setPermissionId(1L);
        entity.setTenantRoleId(1L);
        when(permissionClient.isPermissionExistent(1L, null)).thenReturn(true);
        when(tenantRolePermissionServiceAccess.isAssociationAlreadyExistent(anyLong(), anyLong()))
                .thenThrow(new InvalidArgumentException("error"));

        target.assignPermission(entity);
    }

    @Test(expected = BadRequestException.class)
    public void testAssignPermissionInvalidParametersPermissionId()  {
        TenantRolePermissionEntity entity = new TenantRolePermissionEntity();
        entity.setPermissionId(null);
        entity.setTenantRoleId(1L);

        target.assignPermission(entity);
    }

    @Test(expected = BadRequestException.class)
    public void testAssignPermissionInvalidParametersTenantRoleId()  {
        TenantRolePermissionEntity entity = new TenantRolePermissionEntity();
        entity.setPermissionId(1L);
        entity.setTenantRoleId(null);

        target.assignPermission(entity);
    }

    @Test(expected = BadRequestException.class)
    public void testAssignPermissionInvalidParametersPermissionNotExistent() throws SystemException {
        TenantRolePermissionEntity entity = new TenantRolePermissionEntity();
        entity.setPermissionId(1L);
        entity.setTenantRoleId(1L);
        when(permissionClient.isPermissionExistent(1L, null)).thenReturn(false);


        target.assignPermission(entity);
    }

    @Test(expected = RoleException.class)
    public void testAssignPermissionInvalidParametersPermissionNotExistentSystemException() throws SystemException {
        TenantRolePermissionEntity entity = new TenantRolePermissionEntity();
        entity.setPermissionId(1L);
        entity.setTenantRoleId(1L);
        when(permissionClient.isPermissionExistent(1L, null))
                .thenThrow(new SystemException("error"));


        target.assignPermission(entity);
    }

    @Test
    public void testUpdate() throws SystemException, InvalidArgumentException, UniquenessConstraintException {
        TenantRolePermissionEntity entity = new TenantRolePermissionEntity();
        entity.setPermissionId(1L);
        entity.setTenantRoleId(1L);
        when(permissionClient.isPermissionExistent(1L, null)).thenReturn(true);
        when(tenantRolePermissionServiceAccess.isAssociationAlreadyExistent(anyLong(), anyLong())).thenReturn(false);

        target.update(entity);
        verify(tenantRolePermissionServiceAccess).update(entity);
    }

    @Test(expected = RoleException.class)
    public void testUpdateAlreadyExistent() throws SystemException {
        TenantRolePermissionEntity entity = new TenantRolePermissionEntity();
        entity.setPermissionId(1L);
        entity.setTenantRoleId(1L);
        SystemTenantRole tenantRole = new TenantRole();
        tenantRole.setTenantId(1L);
        tenantRole.setRoleId(1L);
        when(tenantRoleServiceAccess.getById(1L)).thenReturn(tenantRole);
        when(permissionClient.isPermissionExistent(1L, null)).thenReturn(true);
        when(tenantRolePermissionServiceAccess.isAssociationAlreadyExistent(any(), any(), any())).thenReturn(true);

        target.update(entity);
    }

    @Test(expected = BadRequestException.class)
    public void testUpdateInvalidArgument() throws SystemException, InvalidArgumentException, UniquenessConstraintException {
        TenantRolePermissionEntity entity = new TenantRolePermissionEntity();
        entity.setPermissionId(1L);
        entity.setTenantRoleId(1L);
        when(permissionClient.isPermissionExistent(1L, null)).thenReturn(true);
        when(tenantRolePermissionServiceAccess.isAssociationAlreadyExistent(anyLong(), anyLong(), any())).thenReturn(false);
        doThrow(new InvalidArgumentException("error"))
                .when(tenantRolePermissionServiceAccess).update(any());

        target.update(entity);
    }

    @Test(expected = RoleException.class)
    public void testUpdateDuplicatedData() throws SystemException, InvalidArgumentException, UniquenessConstraintException {
        TenantRolePermissionEntity entity = new TenantRolePermissionEntity();
        entity.setPermissionId(1L);
        entity.setTenantRoleId(1L);
        when(permissionClient.isPermissionExistent(1L, null)).thenReturn(true);
        when(tenantRolePermissionServiceAccess.isAssociationAlreadyExistent(anyLong(), anyLong(), any())).thenReturn(false);
        doThrow(new UniquenessConstraintException("error"))
                .when(tenantRolePermissionServiceAccess).update(any());

        target.update(entity);
    }

    @Test
    public void testUnAssignPermission() throws InvalidArgumentException {
        when(tenantRoleServiceAccess.getIdByTenantRole(anyLong(), anyLong()))
                .thenReturn(1L);
        when(tenantRolePermissionServiceAccess.getTenantRolePermissionId(eq(1L), anyLong()))
                .thenReturn(Optional.of(1L));

        target.unAssignPermission(1L, 1L, 1L);
        verify(tenantRolePermissionServiceAccess).delete(1L);
    }

    @Test(expected = BadRequestException.class)
    public void testUnAssignPermissionMissingParameter() {
        target.unAssignPermission(null, 1L, 1L);
    }

    @Test(expected = RoleException.class)
    public void testUnAssignPermissionMissingTenantRolePermission() throws InvalidArgumentException {
        when(tenantRoleServiceAccess.getIdByTenantRole(anyLong(), anyLong()))
                .thenReturn(1L);
        when(tenantRolePermissionServiceAccess.getTenantRolePermissionId(eq(1L), anyLong()))
                .thenReturn(Optional.empty());

        target.unAssignPermission(1L, 1L, 1L);
    }

    @Test
    public void testGetPermissions() throws InvalidArgumentException {
        when(tenantRolePermissionServiceAccess.getPermissions(anyLong(), anyLong(), anyLong()))
                .thenReturn(Arrays.asList(1L, 2L));
        assertTrue(target.getPermissions(1L, 1L, 1L).isEmpty());
    }

    @Test(expected = BadRequestException.class)
    public void testGetPermissionsInvalidArgument() throws InvalidArgumentException {
        when(tenantRolePermissionServiceAccess.getPermissions(any(), anyLong(), anyLong()))
                .thenThrow(new InvalidArgumentException("error"));
        target.getPermissions(null, 1L, 1L);
    }

    @Test(expected = BadRequestException.class)
    public void testGetPermissionsRequestError() throws InvalidArgumentException, SystemException {
        when(tenantRolePermissionServiceAccess.getPermissions(anyLong(), anyLong(), anyLong()))
                .thenReturn(Arrays.asList(1L, 2L));
        when(permissionClient.getPermissionsByIds(any()))
                .thenThrow(new SystemException("error"));
        target.getPermissions(1L, 1L, 1L);
    }

}
