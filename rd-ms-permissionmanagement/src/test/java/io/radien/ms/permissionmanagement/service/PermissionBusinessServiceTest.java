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

package io.radien.ms.permissionmanagement.service;

import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemPermission;
import io.radien.api.model.permission.SystemPermissionSearchFilter;
import io.radien.api.service.permission.PermissionServiceAccess;
import io.radien.api.service.permission.exception.PermissionIllegalArgumentException;
import io.radien.api.service.permission.exception.PermissionNotFoundException;
import io.radien.exception.BadRequestException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.permissionmanagement.client.entities.Permission;
import io.radien.ms.permissionmanagement.client.entities.PermissionSearchFilter;
import io.radien.ms.permissionmanagement.entities.PermissionEntity;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PermissionBusinessServiceTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    @InjectMocks
    public PermissionBusinessService businessService;
    @Mock
    public PermissionServiceAccess permissionService;

    @Test
    public void testGet() {
        when(permissionService.get(anyLong())).thenReturn(new PermissionEntity());
        assertNotNull(businessService.get(1L));
    }

    @Test(expected = PermissionNotFoundException.class)
    public void testGetNotFound() {
        when(permissionService.get(anyLong())).thenReturn(null);
        businessService.get(1L);
    }

    @Test
    public void testGetList() {
        when(permissionService.get(anyList())).thenReturn(Collections.singletonList(new PermissionEntity()));
        assertNotNull(businessService.get(Collections.singletonList(1L)));
    }

    @Test(expected = BadRequestException.class)
    public void testGetListBadRequest() {
        businessService.get(new ArrayList<>());
    }

    @Test(expected = PermissionNotFoundException.class)
    public void testGetListNotFound() {
        when(permissionService.get(anyList())).thenReturn(new ArrayList<>());
        businessService.get(Collections.singletonList(1L));
    }

    @Test
    public void testGetAll() {
        Page<SystemPermission> resultPage = new Page<>(new ArrayList<>(), 1, 1, 1);
        when(permissionService.getAll(anyString(), anyInt(), anyInt(), anyList(), anyBoolean()))
                .thenReturn(resultPage);
        assertEquals(resultPage, businessService.getAll("", 1, 1, new ArrayList<>(), false));
    }

    @Test
    public void testGetFiltered() {
        List<SystemPermission> resultList = Arrays.asList(new PermissionEntity(), new PermissionEntity());
        when(permissionService.getPermissions(any(SystemPermissionSearchFilter.class)))
                .thenReturn(resultList);
        assertEquals(resultList, businessService.getFiltered(new PermissionSearchFilter()));
    }

    @Test
    public void testGetIdByActionAndResource() throws PermissionIllegalArgumentException {
        when(permissionService.getIdByActionAndResource(anyString(), anyString()))
                .thenReturn(Optional.of(1L));
        assertEquals(Long.valueOf(1), businessService.getIdByActionAndResource("", ""));
    }

    @Test(expected = PermissionNotFoundException.class)
    public void testGetIdByActionAndResourceNotFound() throws PermissionIllegalArgumentException {
        when(permissionService.getIdByActionAndResource(anyString(), anyString()))
                .thenReturn(Optional.empty());
        businessService.getIdByActionAndResource("", "");
    }

    @Test(expected = BadRequestException.class)
    public void testGetIdByActionAndResourceBadRequest() throws PermissionIllegalArgumentException {
        when(permissionService.getIdByActionAndResource(anyString(), anyString()))
                .thenThrow(new PermissionIllegalArgumentException("error"));
        businessService.getIdByActionAndResource("", "");
    }

    @Test
    public void testCreate() throws UniquenessConstraintException {
        businessService.create(new Permission());
        verify(permissionService).create(any());
    }

    @Test(expected = BadRequestException.class)
    public void testCreateDuplicate() throws UniquenessConstraintException {
        doThrow(new UniquenessConstraintException("error"))
                .when(permissionService).create(any());
        businessService.create(new Permission());
    }

    @Test
    public void testUpdate() throws UniquenessConstraintException {
        businessService.update(2L, new Permission());
        verify(permissionService).update(any());
    }

    @Test(expected = BadRequestException.class)
    public void testUpdateDuplicate() throws UniquenessConstraintException {
        doThrow(new UniquenessConstraintException("error"))
                .when(permissionService).update(any());
        businessService.update(2L, new Permission());
    }

    @Test
    public void testDelete() {
        when(permissionService.delete(anyLong())).thenReturn(true);
        businessService.delete(1L);
        verify(permissionService).delete(1L);
    }

    @Test(expected = PermissionNotFoundException.class)
    public void testDeleteNotFound() {
        when(permissionService.delete(anyLong())).thenReturn(false);
        businessService.delete(1L);
    }

    @Test
    public void testDeleteList() {
        when(permissionService.delete(anyList())).thenReturn(true);
        businessService.delete(Collections.singletonList(1L));
        verify(permissionService).delete(anyList());
    }

    @Test(expected = PermissionNotFoundException.class)
    public void testDeleteListNotFound() {
        when(permissionService.delete(anyList())).thenReturn(false);
        businessService.delete(Collections.singletonList(1L));
    }

    @Test
    public void testExists() {
        when(permissionService.exists(anyLong(), anyString())).thenReturn(true);
        businessService.exists(1L, "");
        verify(permissionService).exists(1L, "");
    }

    @Test(expected = BadRequestException.class)
    public void testExistsBadRequest() {
        businessService.exists(null, null);
    }

    @Test
    public void testGetPermissionByActionNameAndResourceName() {
        when(permissionService.getPermissionByActionAndResourceNames(anyString(), anyString()))
                .thenReturn(new PermissionEntity());
        assertNotNull(businessService.getPermissionByActionNameAndResourceName("name", "name"));
    }

    @Test(expected = PermissionNotFoundException.class)
    public void testGetPermissionByActionNameAndResourceNameNotFound() {
        when(permissionService.getPermissionByActionAndResourceNames(anyString(), anyString()))
                .thenReturn(null);
        businessService.getPermissionByActionNameAndResourceName("name", "name");
    }

    @Test(expected = BadRequestException.class)
    public void testGetPermissionByActionNameAndResourceNameBadRequest() {
        businessService.getPermissionByActionNameAndResourceName("", "name");
    }

    @Test
    public void testCount() {
        when(permissionService.getTotalRecordsCount()).thenReturn(10L);
        assertEquals(10L, businessService.getCount());
    }
}