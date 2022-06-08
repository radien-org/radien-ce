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
import io.radien.api.model.role.SystemRoleSearchFilter;
import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.service.role.RoleServiceAccess;
import io.radien.api.service.role.exception.RoleException;
import io.radien.api.service.role.exception.TenantRoleNotFoundException;
import io.radien.api.service.tenantrole.TenantRoleServiceAccess;
import io.radien.exception.BadRequestException;
import io.radien.exception.InvalidArgumentException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.client.entities.TenantRole;
import io.radien.ms.rolemanagement.entities.TenantRoleEntity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertEquals;
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
 * Tenant Role Business Service rest requests and responses into the db access
 * {@link io.radien.ms.rolemanagement.service.TenantRoleBusinessService}
 *
 * @author Newton Carvalho
 */
public class TenantRoleBusinessServiceTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @InjectMocks
    private TenantRoleBusinessService tenantRoleBusinessService;
    @Mock
    private RoleServiceAccess roleServiceAccess;
    @Mock
    private TenantRoleServiceAccess tenantRoleServiceAccess;

    @Test
    public void testGetAll() {
        Page<SystemTenantRole> resultPage = new Page<>();
        resultPage.setResults(Collections.singletonList(new TenantRole()));
        resultPage.setCurrentPage(1);
        resultPage.setTotalResults(1);
        resultPage.setTotalPages(1);
        when(tenantRoleServiceAccess.getAll(anyLong(), anyLong(), anyInt(), anyInt(), anyList(), anyBoolean()))
                .thenReturn(resultPage);

        assertEquals(resultPage, tenantRoleBusinessService.getAll(1L, 1L, 1, 1, new ArrayList<>(), false));
    }

    @Test
    public void testGetFiltered() {
        assertEquals(0, tenantRoleBusinessService.getFiltered(1L, 1L, false).size());
    }

    @Test
    public void testGetById() {
        when(tenantRoleServiceAccess.get(anyLong()))
                .thenReturn(new TenantRole());

        assertNotNull(tenantRoleBusinessService.getById(1L));
    }

    @Test(expected = TenantRoleNotFoundException.class)
    public void testGetByIdNotFound() {
        when(tenantRoleServiceAccess.get(anyLong()))
                .thenReturn(null);
        tenantRoleBusinessService.getById(1L);
    }

    @Test
    public void testGetIdByTenantRole() throws InvalidArgumentException {
        when(tenantRoleServiceAccess.getTenantRoleId(anyLong(), anyLong()))
                .thenReturn(Optional.of(1L));
        assertEquals(Long.valueOf(1), tenantRoleBusinessService.getIdByTenantRole(1L, 1L));
    }

    @Test(expected = TenantRoleNotFoundException.class)
    public void testGetIdByTenantRoleNotFound() {
        tenantRoleBusinessService.getIdByTenantRole(1L, 1L);
    }

    @Test(expected = BadRequestException.class)
    public void testGetIdByTenantRoleInvalidArgument() throws InvalidArgumentException {
        when(tenantRoleServiceAccess.getTenantRoleId(anyLong(), anyLong()))
                .thenThrow(new InvalidArgumentException("error"));
        tenantRoleBusinessService.getIdByTenantRole(1L, 1L);
    }

    @Test
    public void testDelete() throws InvalidArgumentException {
        when(tenantRoleServiceAccess.delete(anyLong()))
                .thenReturn(true);
        tenantRoleBusinessService.delete(1L);
        verify(tenantRoleServiceAccess).delete(anyLong());
    }

    @Test(expected = TenantRoleNotFoundException.class)
    public void testDeleteNotFound() {
        tenantRoleBusinessService.delete(1L);
    }

    @Test(expected = BadRequestException.class)
    public void testDeleteInvalidArgument() throws InvalidArgumentException {
        when(tenantRoleServiceAccess.delete(anyLong()))
                .thenThrow(new InvalidArgumentException("error"));
        tenantRoleBusinessService.delete(1L);
    }

    @Test
    public void testCreate() throws InvalidArgumentException, UniquenessConstraintException {
        tenantRoleBusinessService.create(new TenantRoleEntity());
        verify(tenantRoleServiceAccess).create(any());
    }

    @Test(expected = BadRequestException.class)
    public void testCreateInvalidArgument() throws InvalidArgumentException, UniquenessConstraintException {
        doThrow(new InvalidArgumentException("error"))
                .when(tenantRoleServiceAccess).create(any());
        tenantRoleBusinessService.create(new TenantRoleEntity());
    }

    @Test(expected = RoleException.class)
    public void testCreateDuplicateData() throws InvalidArgumentException, UniquenessConstraintException {
        doThrow(new UniquenessConstraintException("error"))
                .when(tenantRoleServiceAccess).create(any());
        tenantRoleBusinessService.create(new TenantRoleEntity());
    }

    @Test
    public void testUpdate() throws InvalidArgumentException, UniquenessConstraintException {
        tenantRoleBusinessService.update(1L, new TenantRoleEntity());
        verify(tenantRoleServiceAccess).update(any());
    }

    @Test(expected = BadRequestException.class)
    public void testUpdateInvalidArgument() throws InvalidArgumentException, UniquenessConstraintException {
        doThrow(new InvalidArgumentException("error"))
                .when(tenantRoleServiceAccess).update(any());
        tenantRoleBusinessService.update(1L, new TenantRoleEntity());
    }

    @Test(expected = RoleException.class)
    public void testUpdateDuplicateData() throws InvalidArgumentException, UniquenessConstraintException {
        doThrow(new UniquenessConstraintException("error"))
                .when(tenantRoleServiceAccess).update(any());
        tenantRoleBusinessService.update(1L, new TenantRoleEntity());
    }

    @Test
    public void testExistsAssociation() throws InvalidArgumentException {
        when(tenantRoleServiceAccess.isAssociationAlreadyExistent(anyLong(), anyLong()))
                .thenReturn(true);
        assertTrue(tenantRoleBusinessService.existsAssociation(1L, 1L));
    }

    @Test(expected = BadRequestException.class)
    public void testExistsAssociationInvalidArgument() throws InvalidArgumentException {
        when(tenantRoleServiceAccess.isAssociationAlreadyExistent(eq(null), anyLong()))
                .thenThrow(new InvalidArgumentException("error"));
        tenantRoleBusinessService.existsAssociation(1L, null);
    }

    @Test
    public void testIsRoleExistentForUser() throws InvalidArgumentException {
        when(tenantRoleServiceAccess.hasAnyRole(anyLong(), anyList(), anyLong()))
                .thenReturn(true);
        assertTrue(tenantRoleBusinessService.isRoleExistentForUser(1L, "role", 1L));
    }

    @Test(expected = BadRequestException.class)
    public void testIsRoleExistentForUserInvalidArgument() throws InvalidArgumentException {
        when(tenantRoleServiceAccess.hasAnyRole(eq(null), anyList(), anyLong()))
                .thenThrow(new InvalidArgumentException("error"));
        tenantRoleBusinessService.isRoleExistentForUser(null, "role", 1L);
    }

    @Test
    public void testIsAnyRoleExistentForUser() throws InvalidArgumentException {
        when(tenantRoleServiceAccess.hasAnyRole(anyLong(), anyList(), anyLong()))
                .thenReturn(true);
        assertTrue(tenantRoleBusinessService.isAnyRoleExistentForUser(1L, Collections.singletonList("role"), 1L));
    }

    @Test(expected = BadRequestException.class)
    public void testIsAnyRoleExistentForUserBadRequest() {
        tenantRoleBusinessService.isAnyRoleExistentForUser(1L, null, 1L);
    }

    @Test(expected = BadRequestException.class)
    public void testIsAnyRoleExistentForUserInvalidArgument() throws InvalidArgumentException {
        when(tenantRoleServiceAccess.hasAnyRole(eq(null), anyList(), anyLong()))
                .thenThrow(new InvalidArgumentException("error"));
        tenantRoleBusinessService.isAnyRoleExistentForUser(null, Collections.singletonList("role"), 1L);
    }

    @Test
    public void testIsPermissionExistentForUser() throws InvalidArgumentException {
        when(tenantRoleServiceAccess.hasPermission(anyLong(), anyLong(), anyLong()))
                .thenReturn(true);
        assertTrue(tenantRoleBusinessService.isPermissionExistentForUser(1L, 1L, 1L));
    }

    @Test(expected = BadRequestException.class)
    public void testIsPermissionExistentForUserBadRequest() throws InvalidArgumentException {
        when(tenantRoleServiceAccess.hasPermission(eq(null), anyLong(), anyLong()))
                .thenThrow(new InvalidArgumentException("error"));
        tenantRoleBusinessService.isPermissionExistentForUser(null, 1L, 1L);
    }

    @Test
    public void testCount() {
        when(tenantRoleServiceAccess.count())
                .thenReturn(10L);
        assertEquals(10L, tenantRoleBusinessService.count());
    }

    @Test
    public void testGetRolesForUserTenant() throws InvalidArgumentException {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        when(tenantRoleServiceAccess.getRoleIdsForUserTenant(anyLong(), anyLong()))
                .thenReturn(ids);
        when(roleServiceAccess.getSpecificRoles(any(SystemRoleSearchFilter.class)))
                .thenReturn(new ArrayList<>());
        assertTrue(tenantRoleBusinessService.getRolesForUserTenant(1L, 1L).isEmpty());
    }

    @Test
    public void testGetRolesForUserTenantNotFound() throws InvalidArgumentException {
        when(tenantRoleServiceAccess.getRoleIdsForUserTenant(anyLong(), anyLong()))
                .thenReturn(new ArrayList<>());
        assertTrue(tenantRoleBusinessService.getRolesForUserTenant(1L, 1L).isEmpty());
    }

    @Test(expected = BadRequestException.class)
    public void testGetRolesForUserTenantInvalidArgument() throws InvalidArgumentException {
        when(tenantRoleServiceAccess.getRoleIdsForUserTenant(eq(null), anyLong()))
                .thenThrow(new InvalidArgumentException("error"));
        tenantRoleBusinessService.getRolesForUserTenant(null, 1L);
    }

}