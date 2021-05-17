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
package ${package}.ms.service.services;

import ${package}.api.entity.Page;
import ${package}.api.model.System${entityResourceName};
import ${package}.api.service.${entityResourceName}ServiceAccess;
import ${package}.exception.${entityResourceName}NotFoundException;
import ${package}.ms.service.entities.${entityResourceName};
import ${package}.ms.service.legacy.${entityResourceName}Factory;

import org.junit.Before;
import org.junit.Test;

import javax.ejb.embeddable.EJBContainer;

import javax.naming.Context;
import javax.naming.NamingException;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;

public class ${entityResourceName}ServiceTest {
    ${entityResourceName}ServiceAccess ${entityResourceName.toLowerCase()}ServiceAccess;
    System${entityResourceName} system${entityResourceName};
    ${entityResourceName} ${entityResourceName.toLowerCase()};

    public ${entityResourceName}ServiceTest() throws NamingException {
        Properties p = new Properties();
        p.put("openejb.deployments.classpath.include",".*");
        p.put("openejb.deployments.classpath.exclude",".*rd-ms-${entityResourceName.toLowerCase()}/rd-ms-${entityResourceName.toLowerCase()}-client.*");
        final Context context = EJBContainer.createEJBContainer(p).getContext();
        ${entityResourceName.toLowerCase()}ServiceAccess = (${entityResourceName}ServiceAccess) context.lookup("java:global/rd-ms-${entityResourceName.toLowerCase()}-service//${entityResourceName}Service");
    }

    @Before
    public void before() throws ${entityResourceName}NotFoundException {
        Page<? extends System${entityResourceName}> pageSystem${entityResourceName} = ${entityResourceName.toLowerCase()}ServiceAccess.getAll(null, 1, 10,
                null, true);
        if(pageSystem${entityResourceName}.getTotalResults()>0) {
            system${entityResourceName} = pageSystem${entityResourceName}.getResults().get(0);
        } else {
            system${entityResourceName} = ${entityResourceName}Factory.create("name");
            ${entityResourceName.toLowerCase()}ServiceAccess.save(system${entityResourceName});
        }
    }

    @Test
    public void save_update_test() throws ${entityResourceName}NotFoundException {
        ${entityResourceName} ${entityResourceName.toLowerCase()}1 = (${entityResourceName}) ${entityResourceName.toLowerCase()}ServiceAccess.get(system${entityResourceName}.getId());
        ${entityResourceName.toLowerCase()}ServiceAccess.save(${entityResourceName.toLowerCase()}1);
    }

    @Test
    public void delete_test() throws ${entityResourceName}NotFoundException {
        System${entityResourceName} result = ${entityResourceName.toLowerCase()}ServiceAccess.get(system${entityResourceName}.getId());
        assertNotNull(result);
        assertEquals(system${entityResourceName}.getName(), result.getName());
        ${entityResourceName.toLowerCase()}ServiceAccess.delete(system${entityResourceName}.getId());
        boolean success = false;
        try {
            result = ${entityResourceName.toLowerCase()}ServiceAccess.get(system${entityResourceName}.getId());
        } catch (${entityResourceName}NotFoundException e){
            success = true;
        }
        assertTrue(success);
    }

    @Test
    public void getAll_sort_test() throws ${entityResourceName}NotFoundException {
        Long size = 5L;
        listSystem${entityResourceName}Save(size);

        List<String> orderBy = new ArrayList<>();
        orderBy.add("name");

        Page<? extends System${entityResourceName}> pageSystem${entityResourceName} = ${entityResourceName.toLowerCase()}ServiceAccess.getAll(null, 1, 10,
                orderBy, true);

        assertTrue(pageSystem${entityResourceName}.getTotalResults()>0);

        assertEquals("a-0",pageSystem${entityResourceName}.getResults().get(0).getName());

        pageSystem${entityResourceName} = ${entityResourceName.toLowerCase()}ServiceAccess.getAll(null, 1, 10, orderBy, false);
        assertTrue(pageSystem${entityResourceName}.getTotalResults()>=size);
        assertEquals("a-4",pageSystem${entityResourceName}.getResults().get(1).getName());
        assertEquals("name",pageSystem${entityResourceName}.getResults().get(0).getName());
    }

    private void listSystem${entityResourceName}Save(Long size) throws ${entityResourceName}NotFoundException {
        for (int i = 0; i < size; i++) {
            System${entityResourceName} system${entityResourceName}1 = ${entityResourceName}Factory.create("a-"+i);
            ${entityResourceName.toLowerCase()}ServiceAccess.save(system${entityResourceName}1);
        }
    }

}
