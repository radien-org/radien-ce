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
import io.radien.api.model.role.SystemRole;
import io.radien.api.service.role.RoleServiceAccess;
import io.radien.api.service.role.exception.RoleNotFoundException;
import io.radien.ms.rolemanagement.client.entities.Role;
import io.radien.ms.rolemanagement.client.entities.RoleSearchFilter;
import io.radien.ms.rolemanagement.client.services.RoleFactory;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
/**
 * @author Bruno Gama
 */
public class RoleBusinessServiceTest {

    @InjectMocks
    @Spy
    RoleBusinessService roleBusinessService;
    @Mock
    RoleServiceAccess roleServiceAccess;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Test
    public void testGetAll() {
        Page<SystemRole> p = new Page<>(new ArrayList<>(),-1,0,0);
        when(roleServiceAccess.getAll(null, 1, 10, null, false)).thenReturn(p);
        Page<? extends SystemRole> result = roleBusinessService.getAll(null, 1,10, null, false);
        assertEquals(p,result);
    }

    @Test
    public void testGetSpecificRoles() {
        List<? extends SystemRole> list = roleBusinessService.getSpecificRoles(new RoleSearchFilter
                ("name", "description", new ArrayList<>(), true, true));
        assertEquals(0,list.size());
    }

    @Test
    public void testGetById() throws RoleNotFoundException {
        SystemRole systemRole = RoleFactory.create("name", "description", 4L);
        when(roleServiceAccess.get(any(Long.class))).thenReturn(systemRole);
        SystemRole result = roleBusinessService.getById(3L);
        assertEquals(systemRole,result);
    }

    @Test
    public void testDelete() {
        Role u = RoleFactory.create("name", "description", 4L);
        u.setId(2L);
        when(roleServiceAccess.delete(anyLong())).thenReturn(true);
        boolean success = false;
        try{
            roleBusinessService.create(u);

            roleBusinessService.delete(2L);
            success = true;
        } catch (Exception e){
            success = false;
        }
        assertTrue(success);
    }

    /**
     * Test for method {@link RoleBusinessService#create(Role)}
     */
    @Test
    public void testCreate() {
        Role u = RoleFactory.create("name", "description", 4L);
        boolean success;
        try{
            roleBusinessService.create(u);
            success = true;
        } catch (Exception e){
            success = false;
        }
        assertTrue(success);
    }

    /**
     * Test for method {@link RoleBusinessService#update(Long, Role)}
     */
    @Test
    public void testUpdate() {
        Role u = RoleFactory.create("name", "description", 4L);
        u.setId(111L);
        boolean success;
        try{
            roleBusinessService.update(1L, u);
            success = true;
        } catch (Exception e){
            success = false;
        }
        assertTrue(success);
    }

    @Test
    public void testGetTotalRecordsCount(){
        boolean success = false;
        Long l = 30L;
        try{
            l = roleBusinessService.getTotalRecordsCount();
            success = true;
        } catch (Exception e){
            success = false;
        }
        assertTrue(success);
        assertEquals(l, (Long) 0L);
    }
}