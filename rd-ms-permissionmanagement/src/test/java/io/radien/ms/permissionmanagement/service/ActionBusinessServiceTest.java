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
import io.radien.api.model.permission.SystemAction;
import io.radien.api.service.permission.ActionServiceAccess;
import io.radien.api.service.permission.exception.ActionNotFoundException;
import io.radien.exception.BadRequestException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.permissionmanagement.client.entities.Action;
import io.radien.ms.permissionmanagement.client.entities.ActionSearchFilter;
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

public class ActionBusinessServiceTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    @InjectMocks
    private ActionBusinessService businessService;
    @Mock
    private ActionServiceAccess actionService;

    @Test
    public void testGet() {
        when(actionService.get(anyLong())).thenReturn(new Action());
        assertNotNull(businessService.get(1L));
    }

    @Test(expected = ActionNotFoundException.class)
    public void testGetNotFound() {
        when(actionService.get(anyLong())).thenReturn(null);
        businessService.get(1L);
    }

    @Test
    public void testGetList() {
        when(actionService.get(anyList())).thenReturn(Collections.singletonList(new Action()));
        assertFalse(businessService.get(Arrays.asList(1L, 2L)).isEmpty());
    }

    @Test(expected = BadRequestException.class)
    public void testGetListBadRequest() {
        businessService.get(new ArrayList<>());
    }

    @Test(expected = ActionNotFoundException.class)
    public void testGetListNotFound() {
        when(actionService.get(anyList())).thenReturn(new ArrayList<>());
        businessService.get(Collections.singletonList(1L));
    }

    @Test
    public void testGetAll() {
        Page<SystemAction> result = new Page<>(new ArrayList<>(), 1, 1, 1);
        when(actionService.getAll(anyString(), anyInt(), anyInt(), anyList(), anyBoolean())).thenReturn(result);
        assertEquals(result, businessService.getAll("", 1, 1, new ArrayList<>(), false));
    }

    @Test
    public void testGetFiltered() {
        when(actionService.getActions(any())).thenReturn(new ArrayList<>());
        assertTrue(businessService.getFiltered(new ActionSearchFilter()).isEmpty());
    }

    @Test
    public void testCreate() throws UniquenessConstraintException {
        Action action = new Action();
        action.setName("name");
        businessService.create(action);
        verify(actionService).create(any());
    }

    @Test(expected = BadRequestException.class)
    public void testCreateBadRequest() {
        businessService.create(new Action());
    }

    @Test(expected = BadRequestException.class)
    public void testCreateDuplicateData() throws UniquenessConstraintException {
        doThrow(new UniquenessConstraintException("error")).when(actionService).create(any());
        Action action = new Action();
        action.setName("test");
        businessService.create(action);
    }

    @Test
    public void testUpdate() throws UniquenessConstraintException {
        Action action = new Action();
        action.setName("name");
        businessService.update(2L, action);
        verify(actionService).update(any());
    }

    @Test(expected = BadRequestException.class)
    public void testUpdateBadRequest() {
        businessService.update(2L, new Action());
    }

    @Test(expected = BadRequestException.class)
    public void testUpdateDuplicateData() throws UniquenessConstraintException {
        doThrow(new UniquenessConstraintException("error")).when(actionService).update(any());
        Action action = new Action();
        action.setName("test");
        businessService.update(2L, action);
    }

    @Test
    public void testDelete() {
        when(actionService.delete(anyLong())).thenReturn(true);
        businessService.delete(1L);
        verify(actionService).delete(1L);
    }

    @Test(expected = ActionNotFoundException.class)
    public void testDeleteNotFound() {
        when(actionService.delete(anyLong())).thenReturn(false);
        businessService.delete(1L);
    }

    @Test
    public void testDeleteList() {
        when(actionService.delete(anyList())).thenReturn(true);
        businessService.delete(Collections.singletonList(1L));
        verify(actionService).delete(anyList());
    }

    @Test(expected = ActionNotFoundException.class)
    public void testDeleteListNotFound() {
        when(actionService.delete(anyList())).thenReturn(false);
        businessService.delete(Collections.singletonList(1L));
    }

}