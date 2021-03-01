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

import io.radien.api.entity.Page;
import io.radien.api.model.role.SystemRole;
import io.radien.api.service.role.RoleServiceAccess;
import io.radien.exception.RoleNotFoundException;
import io.radien.ms.rolemanagement.client.entities.Role;
import io.radien.ms.rolemanagement.client.entities.RoleSearchFilter;
import io.radien.ms.rolemanagement.client.services.RoleFactory;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author Bruno Gama
 */
public class RoleBusinessServiceTest extends TestCase {

    @InjectMocks
    @Spy
    RoleBusinessService roleBusinessService;
    @Mock
    RoleServiceAccess roleServiceAccess;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAll() {
        Page<SystemRole> p = new Page<>(new ArrayList<>(),-1,0,0);
        when(roleServiceAccess.getAll(1, 10)).thenReturn(p);
        Page<? extends SystemRole> result = roleBusinessService.getAll(1,10);
        assertEquals(p,result);
    }

    @Test
    public void testGetSpecificRoles() {
        List<? extends SystemRole> list = roleBusinessService.getSpecificRoles(new RoleSearchFilter
                ("name", "description", true, true));
        assertEquals(0,list.size());
    }

    @Test
    public void testGetById() throws RoleNotFoundException {
        SystemRole systemRole = RoleFactory.create("name", "description", 4L);
        when(roleServiceAccess.get(any())).thenReturn(systemRole);
        SystemRole result = roleBusinessService.getById(3L);
        assertEquals(systemRole,result);
    }

    @Test
    public void testDelete() {
        Role u = RoleFactory.create("name", "description", 4L);
        u.setId(2L);
        boolean success = false;
        try{
            roleBusinessService.save(u);

            roleBusinessService.delete(2L);
            success = true;
        } catch (Exception e){
            success = false;
        }
        assertTrue(success);
    }

    @Test
    public void testSave() {
        Role u = RoleFactory.create("name", "description", 4L);
        boolean success = false;
        try{
            roleBusinessService.save(u);
            success = true;
        } catch (Exception e){
            success = false;
        }
        assertTrue(success);
    }

    @Test
    public void testExists() {
        Role u = RoleFactory.create("name", "description", 4L);

        boolean success = false;
        try{
            roleBusinessService.save(u);

            roleBusinessService.exists(u.getId(), u.getName());
            success = true;
        } catch (Exception e){
            success = false;
        }
        assertTrue(success);
    }
}