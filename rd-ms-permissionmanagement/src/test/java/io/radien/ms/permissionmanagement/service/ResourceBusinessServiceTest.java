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
import io.radien.api.model.permission.SystemResource;
import io.radien.api.service.permission.ResourceServiceAccess;
import io.radien.api.service.permission.exception.ResourceNotFoundException;
import io.radien.exception.BadRequestException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.permissionmanagement.client.entities.Resource;
import io.radien.ms.permissionmanagement.client.entities.ResourceSearchFilter;
import io.radien.ms.permissionmanagement.entities.ResourceEntity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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

public class ResourceBusinessServiceTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    @InjectMocks
    private ResourceBusinessService resourceBusinessService;
    @Mock
    private ResourceServiceAccess resourceServiceAccess;

    @Test
    public void testGet() {
        when(resourceServiceAccess.get(anyLong())).thenReturn(new ResourceEntity());
        assertNotNull(resourceBusinessService.get(1L));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetNotFound() {
        when(resourceServiceAccess.get(anyLong())).thenReturn(null);
        resourceBusinessService.get(1L);
    }

    @Test
    public void testGetList() {
        when(resourceServiceAccess.get(anyList()))
                .thenReturn(Collections.singletonList(new ResourceEntity()));
        List<SystemResource> resultList = resourceBusinessService.get(Collections.singletonList(1L));
        assertNotNull(resultList);
        assertFalse(resultList.isEmpty());
    }

    @Test(expected = BadRequestException.class)
    public void testGetListBadRequest() {
        resourceBusinessService.get(new ArrayList<>());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetListNotFound() {
        when(resourceServiceAccess.get(anyList()))
                .thenReturn(new ArrayList<>());
        resourceBusinessService.get(Collections.singletonList(1L));
    }

    @Test
    public void testGetAll() {
        Page<SystemResource> result = new Page<>(new ArrayList<>(), 1, 1, 1);
        when(resourceServiceAccess.getAll(anyString(), anyInt(), anyInt(), anyList(), anyBoolean()))
                .thenReturn(result);
        assertEquals(result, resourceBusinessService.getAll("", 1, 1, new ArrayList<>(), false));
    }

    @Test
    public void testGetFiltered() {
        when(resourceServiceAccess.getResources(any()))
                .thenReturn(new ArrayList<>());
        List<SystemResource> result = resourceBusinessService.getFiltered(new ResourceSearchFilter());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testCreate() throws UniquenessConstraintException {
        Resource resource = new Resource();
        resource.setName("resource");
        resourceBusinessService.create(resource);
        verify(resourceServiceAccess).create(any());
    }

    @Test(expected = BadRequestException.class)
    public void testCreateBadRequest() {
        Resource resource = new Resource();
        resourceBusinessService.create(resource);
    }

    @Test(expected = BadRequestException.class)
    public void testCreateDuplicateData() throws UniquenessConstraintException {
        doThrow(new UniquenessConstraintException("error"))
                .when(resourceServiceAccess).create(any());
        Resource resource = new Resource();
        resource.setName("resource");
        resourceBusinessService.create(resource);
    }

    @Test
    public void testUpdate() throws UniquenessConstraintException {
        Resource resource = new Resource();
        resource.setName("resource");
        resourceBusinessService.update(2L, resource);
        verify(resourceServiceAccess).update(any());
    }

    @Test(expected = BadRequestException.class)
    public void testUpdateBadRequest() {
        Resource resource = new Resource();
        resourceBusinessService.update(2L, resource);
    }

    @Test(expected = BadRequestException.class)
    public void testUpdateDuplicateData() throws UniquenessConstraintException {
        doThrow(new UniquenessConstraintException("error"))
                .when(resourceServiceAccess).update(any());
        Resource resource = new Resource();
        resource.setName("resource");
        resourceBusinessService.update(2L, resource);
    }

    @Test
    public void testDelete() {
        when(resourceServiceAccess.delete(any(Long.class))).thenReturn(true);
        resourceBusinessService.delete(1L);
        verify(resourceServiceAccess).delete(1L);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testDeleteNotFound() {
        when(resourceServiceAccess.delete(any(Long.class))).thenReturn(false);
        resourceBusinessService.delete(1L);
    }

    @Test
    public void testDeleteList() {
        when(resourceServiceAccess.delete(anyList())).thenReturn(true);
        resourceBusinessService.delete(Collections.singletonList(1L));
        verify(resourceServiceAccess).delete(Collections.singletonList(1L));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testDeleteListNotFound() {
        when(resourceServiceAccess.delete(anyList())).thenReturn(false);
        resourceBusinessService.delete(Collections.singletonList(1L));
    }

}