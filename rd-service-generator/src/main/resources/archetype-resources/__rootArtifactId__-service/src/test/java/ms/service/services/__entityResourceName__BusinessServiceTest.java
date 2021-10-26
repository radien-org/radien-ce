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
package ${package}.ms.service.services;

import ${package}.api.entity.Page;
import ${package}.api.model.System${entityResourceName};
import ${package}.api.service.${entityResourceName}ServiceAccess;
import ${package}.exception.${entityResourceName}NotFoundException;
import ${package}.ms.client.entities.${entityResourceName};
import ${package}.ms.client.exceptions.RemoteResourceException;
import ${package}.ms.client.services.${entityResourceName}Factory;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class ${entityResourceName}BusinessServiceTest {

    @InjectMocks
    ${entityResourceName}BusinessService ${entityResourceName.toLowerCase()}BusinessService;
    @Mock
    ${entityResourceName}ServiceAccess ${entityResourceName.toLowerCase()}ServiceAccess;

    System${entityResourceName} system${entityResourceName};

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
        system${entityResourceName} = ${entityResourceName}Factory.create("a");
    }

    @Test
    public void Save_test() throws ${entityResourceName}NotFoundException {
        boolean success;
        try{
            ${entityResourceName.toLowerCase()}BusinessService.save((${entityResourceName}) system${entityResourceName});
            success = true;
        } catch (${entityResourceName}NotFoundException | RemoteResourceException e){
            success = false;
        }
        assertTrue(success);
    }

    @Test
    public void Save_exception_test() throws ${entityResourceName}NotFoundException {
        doThrow(new ${entityResourceName}NotFoundException("")).when(${entityResourceName.toLowerCase()}ServiceAccess).save(system${entityResourceName});
        boolean success = false;
        try{
            ${entityResourceName.toLowerCase()}BusinessService.save((${entityResourceName}) system${entityResourceName});
        } catch (${entityResourceName}NotFoundException | RemoteResourceException e){
            success = true;
        }
        assertTrue(success);
    }

    @Test
    public void get_test() throws ${entityResourceName}NotFoundException {
        when(${entityResourceName.toLowerCase()}ServiceAccess.get(2L)).thenReturn(system${entityResourceName});
        System${entityResourceName} result = ${entityResourceName.toLowerCase()}BusinessService.get(2L);
        assertEquals(system${entityResourceName},result);

    }

    @Test
    public void delete_test() throws ${entityResourceName}NotFoundException {
        when(${entityResourceName.toLowerCase()}ServiceAccess.get((Long) any())).thenReturn(system${entityResourceName});
        boolean success = false;
        try{
            ${entityResourceName.toLowerCase()}BusinessService.delete(1l);
        } catch (Exception e){
            success = true;
        }
        assertFalse(success);
    }

    @Test
    public void getAll_test() {
        String search = "";
        Page<System${entityResourceName}> p = new Page<>(new ArrayList<>(),-1,0,0);
        when(${entityResourceName.toLowerCase()}ServiceAccess.getAll(search,1,2,null,true)).thenReturn(p);
        Page<? extends System${entityResourceName}> result = ${entityResourceName.toLowerCase()}BusinessService.getAll(search,1,2,null,true);
        assertEquals(p,result);
    }
}
