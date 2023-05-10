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

package io.radien.ms.tenantmanagement.service;

import io.radien.api.entity.Page;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.tenant.SystemTenantSearchFilter;
import io.radien.api.service.tenant.TenantServiceAccess;
import io.radien.api.service.tenant.exception.TenantException;
import io.radien.api.service.tenant.exception.TenantNotFoundException;
import io.radien.exception.BadRequestException;
import io.radien.exception.SystemException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.ms.tenantmanagement.client.entities.TenantSearchFilter;
import io.radien.ms.tenantmanagement.client.entities.TenantType;
import io.radien.ms.tenantmanagement.client.services.TenantFactory;
import io.radien.ms.tenantmanagement.entities.TenantEntity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TenantBusinessServiceTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    @InjectMocks
    public TenantBusinessService businessService;
    @Mock
    public TenantServiceAccess tenantService;

    @Test
    public void testGetAll() {
        SystemTenant resultTenant = new Tenant();
        Page<SystemTenant> result = new Page<>(Collections.singletonList(resultTenant), 1, 1, 1);
        when(tenantService.getAll(any(), anyInt(), anyInt(), anyList(), anyBoolean()))
                .thenReturn(result);

        assertEquals(result, businessService.getAll(null, 1, 1, new ArrayList<>(), false));
    }

    @Test
    public void testGetChildren(){
        List<SystemTenant> expectedChildren = new ArrayList<SystemTenant>();
        Long parentId = 2L;

        Tenant t = TenantFactory.create("name","tenantKey", TenantType.ROOT, null, null, null, null, null, null, null, null, parentId, null, null);
        t.setId(1L);
        expectedChildren.add(t);
        t = TenantFactory.create("name","tenantKey2", TenantType.CLIENT, null, null, null, null, null, null, null, null, parentId, null, null);
        t.setId(2L);
        expectedChildren.add(t);

        when(tenantService.getChildren(parentId)).thenReturn(expectedChildren);

        assertEquals(expectedChildren, businessService.getChildren(parentId));
    }

    @Test
    public void testGet() {
        SystemTenant tenant = new Tenant();
        tenant.setId(1L);
        tenant.setName("test tenant");
        when(tenantService.get(1L))
                .thenReturn(tenant);

        assertEquals(tenant, businessService.get(1L));
    }

    @Test(expected = TenantNotFoundException.class)
    public void testGetNotFound() {
        when(tenantService.get(1L))
                .thenReturn(null);

        businessService.get(1L);
    }

    @Test
    public void testGetFiltered() {
        assertEquals(0, businessService.getFiltered(new TenantSearchFilter("", "", new ArrayList<>(), true, true)).size());
    }

    @Test
    public void testCreate() throws SystemException, UniquenessConstraintException {
        businessService.create(new Tenant());
        verify(tenantService).create(any(TenantEntity.class));
    }

    @Test(expected = TenantException.class)
    public void testCreateInvalidData() throws SystemException, UniquenessConstraintException {
        doThrow(new SystemException("error"))
                .when(tenantService).create(any(TenantEntity.class));
        businessService.create(new Tenant());
    }

    @Test(expected = BadRequestException.class)
    public void testCreateDuplicatedData() throws SystemException, UniquenessConstraintException {
        doThrow(new UniquenessConstraintException("error"))
                .when(tenantService).create(any(TenantEntity.class));
        businessService.create(new Tenant());
    }

    @Test
    public void testUpdate() throws SystemException, UniquenessConstraintException {
        businessService.update(1L, new Tenant());
        verify(tenantService).update(any(TenantEntity.class));
    }

    @Test(expected = TenantException.class)
    public void testUpdateInvalidData() throws SystemException, UniquenessConstraintException {
        doThrow(new SystemException("error"))
                .when(tenantService).update(any(TenantEntity.class));
        businessService.update(1L, new Tenant());
    }

    @Test(expected = BadRequestException.class)
    public void testUpdateDuplicatedData() throws SystemException, UniquenessConstraintException {
        doThrow(new UniquenessConstraintException("error"))
                .when(tenantService).update(any(TenantEntity.class));
        businessService.update(1L, new Tenant());
    }

    @Test
    public void testDelete() {
        when(tenantService.delete(anyLong())).thenReturn(true);
        businessService.delete(1L);
        verify(tenantService).delete(1L);
    }

    @Test(expected = TenantNotFoundException.class)
    public void testDeleteNotFound() {
        when(tenantService.delete(anyLong())).thenReturn(false);
        businessService.delete(1L);
    }

    @Test
    public void testDeleteCollection() {
        when(tenantService.delete(anyList())).thenReturn(true);
        businessService.delete(Arrays.asList(1L, 2L, 3L));
        verify(tenantService).delete(Arrays.asList(1L, 2L, 3L));
    }

    @Test(expected = TenantNotFoundException.class)
    public void testDeleteCollectionNotFound() {
        when(tenantService.delete(anyList())).thenReturn(false);
        businessService.delete(Arrays.asList(1L, 2L, 3L));
    }

    @Test
    public void testDeleteHierarchy() {
        when(tenantService.deleteTenantHierarchy(anyLong())).thenReturn(true);
        businessService.deleteTenantHierarchy(1L);
        verify(tenantService).deleteTenantHierarchy(1L);
    }

    @Test(expected = TenantNotFoundException.class)
    public void testDeleteHierarchyNotFound() {
        when(tenantService.deleteTenantHierarchy(anyLong())).thenReturn(false);
        businessService.deleteTenantHierarchy(1L);
    }

    @Test
    public void testExists() {
        when(tenantService.exists(anyLong())).thenReturn(true);
        assertTrue(businessService.exists(1L));
    }
}
