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
import io.radien.api.model.tenant.SystemActiveTenant;
import io.radien.api.service.tenant.ActiveTenantServiceAccess;
import io.radien.api.service.tenant.exception.ActiveTenantException;
import io.radien.api.service.tenant.exception.ActiveTenantNotFoundException;
import io.radien.exception.BadRequestException;
import io.radien.exception.SystemException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.tenantmanagement.client.entities.ActiveTenant;
import io.radien.ms.tenantmanagement.client.entities.ActiveTenantSearchFilter;
import io.radien.ms.tenantmanagement.entities.ActiveTenantEntity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ActiveTenantBusinessServiceTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    @InjectMocks
    public ActiveTenantBusinessService businessService;
    @Mock
    public ActiveTenantServiceAccess activeTenantService;

    @Test
    public void testGetAll() {
        SystemActiveTenant activeTenantResult = new ActiveTenant();
        Page<SystemActiveTenant> result = new Page<>(Collections.singletonList(activeTenantResult), 1, 1, 1);
        when(activeTenantService.getAll(anyLong(), anyLong(),  anyInt(), anyInt(), anyList(), anyBoolean()))
                .thenReturn(result);

        assertEquals(result, businessService.getAll(1L, 1L, 1, 1, new ArrayList<>(), false));
    }

    @Test
    public void testGet() {
        SystemActiveTenant activeTenant = new ActiveTenant();
        activeTenant.setId(1L);
        when(activeTenantService.get(1L))
                .thenReturn(activeTenant);

        assertEquals(activeTenant, businessService.get(1L));
    }

    @Test(expected = ActiveTenantNotFoundException.class)
    public void testGetNotFound() {
        when(activeTenantService.get(1L))
                .thenReturn(null);

        businessService.get(1L);
    }

    @Test
    public void testGetFiltered() {
        assertEquals(0, businessService.getFiltered(new ActiveTenantSearchFilter(1L, 1L, true)).size());
    }

    @Test
    public void testCreate() throws SystemException, UniquenessConstraintException {
        businessService.create(new ActiveTenant());
        verify(activeTenantService).create(any(ActiveTenantEntity.class));
    }

    @Test(expected = ActiveTenantNotFoundException.class)
    public void testCreateInvalidData() throws SystemException, UniquenessConstraintException {
        doThrow(new SystemException("error"))
                .when(activeTenantService).create(any(ActiveTenantEntity.class));
        businessService.create(new ActiveTenant());
    }

    @Test(expected = BadRequestException.class)
    public void testCreateDuplicatedData() throws SystemException, UniquenessConstraintException {
        doThrow(new UniquenessConstraintException("error"))
                .when(activeTenantService).create(any(ActiveTenantEntity.class));
        businessService.create(new ActiveTenant());
    }

    @Test
    public void testUpdate() throws SystemException, UniquenessConstraintException, ActiveTenantException {
        businessService.update(1L, new ActiveTenantEntity());
        verify(activeTenantService).update(any(ActiveTenantEntity.class));
    }

    @Test(expected = ActiveTenantNotFoundException.class)
    public void testUpdateInvalidData() throws SystemException, UniquenessConstraintException, ActiveTenantException {
        doThrow(new ActiveTenantException("error"))
                .when(activeTenantService).update(any(ActiveTenantEntity.class));
        businessService.update(1L, new ActiveTenant());
    }

    @Test(expected = BadRequestException.class)
    public void testUpdateDuplicatedData() throws SystemException, UniquenessConstraintException, ActiveTenantException {
        doThrow(new UniquenessConstraintException("error"))
                .when(activeTenantService).update(any(ActiveTenantEntity.class));
        businessService.update(1L, new ActiveTenant());
    }

    @Test
    public void testDelete() {
        when(activeTenantService.delete(anyLong())).thenReturn(true);
        businessService.delete(1L);
        verify(activeTenantService).delete(1L);
    }

    @Test(expected = ActiveTenantNotFoundException.class)
    public void testDeleteNotFound() {
        when(activeTenantService.delete(anyLong())).thenReturn(false);
        businessService.delete(1L);
    }

    @Test
    public void testDeleteIds() throws SystemException {
        when(activeTenantService.delete(anyLong(), anyLong())).thenReturn(true);
        businessService.delete(1L, 1L);
        verify(activeTenantService).delete(1L, 1L);
    }

    @Test(expected = ActiveTenantNotFoundException.class)
    public void testDeleteIdsNotFound() throws SystemException {
        when(activeTenantService.delete(anyLong(), anyLong())).thenReturn(false);
        businessService.delete(1L, 1L);
    }

    @Test(expected = BadRequestException.class)
    public void testDeleteIdsBadRequest() throws SystemException {
        when(activeTenantService.delete(anyLong(), anyLong()))
                .thenThrow(new SystemException("error"));
        businessService.delete(1L, 1L);
    }

    @Test
    public void testDeleteCollection() {
        when(activeTenantService.delete(anyList())).thenReturn(true);
        businessService.delete(Arrays.asList(1L, 2L, 3L));
        verify(activeTenantService).delete(Arrays.asList(1L, 2L, 3L));
    }

    @Test(expected = ActiveTenantNotFoundException.class)
    public void testDeleteCollectionNotFound() {
        when(activeTenantService.delete(anyList())).thenReturn(false);
        businessService.delete(Arrays.asList(1L, 2L, 3L));
    }

    @Test
    public void testExists() {
        when(activeTenantService.exists(anyLong(), anyLong())).thenReturn(true);
        assertTrue(businessService.exists(1L, 1L));
    }
}
