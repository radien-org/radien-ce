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
package io.rd.ms.service.services;

import io.rd.api.entity.Page;
import io.rd.api.model.SystemDemo;
import io.rd.api.service.DemoServiceAccess;
import io.rd.exception.DemoNotFoundException;
import io.rd.ms.service.entities.Demo;
import io.rd.ms.service.legacy.DemoFactory;

import org.junit.Before;
import org.junit.Test;

import javax.ejb.embeddable.EJBContainer;

import javax.naming.Context;
import javax.naming.NamingException;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;

public class DemoServiceTest {
    DemoServiceAccess demoServiceAccess;
    SystemDemo systemDemo;
    Demo demo;

    public DemoServiceTest() throws NamingException {
        Properties p = new Properties();
        p.put("openejb.deployments.classpath.include",".*");
        p.put("openejb.deployments.classpath.exclude",".*rd-ms-demo/rd-ms-demo-client.*");
        final Context context = EJBContainer.createEJBContainer(p).getContext();
        demoServiceAccess = (DemoServiceAccess) context.lookup("java:global/rd-ms-demo-service//DemoService");
    }

    @Before
    public void before() throws DemoNotFoundException {
        Page<? extends SystemDemo> pageSystemDemo = demoServiceAccess.getAll(null, 1, 10,
                null, true);
        if(pageSystemDemo.getTotalResults()>0) {
            systemDemo = pageSystemDemo.getResults().get(0);
        } else {
            systemDemo = DemoFactory.create("name");
            demoServiceAccess.save(systemDemo);
        }
    }

    @Test
    public void save_update_test() throws DemoNotFoundException {
        Demo demo1 = (Demo) demoServiceAccess.get(systemDemo.getId());
        demoServiceAccess.save(demo1);
    }

    @Test
    public void delete_test() throws DemoNotFoundException {
        SystemDemo result = demoServiceAccess.get(systemDemo.getId());
        assertNotNull(result);
        assertEquals(systemDemo.getName(), result.getName());
        demoServiceAccess.delete(systemDemo.getId());
        boolean success = false;
        try {
            result = demoServiceAccess.get(systemDemo.getId());
        } catch (DemoNotFoundException e){
            success = true;
        }
        assertTrue(success);
    }

    @Test
    public void getAll_sort_test() throws DemoNotFoundException {
        Long size = 5L;
        listSystemDemoSave(size);

        List<String> orderBy = new ArrayList<>();
        orderBy.add("name");

        Page<? extends SystemDemo> pageSystemDemo = demoServiceAccess.getAll(null, 1, 10,
                orderBy, true);

        assertTrue(pageSystemDemo.getTotalResults()>0);

        assertEquals("a-0",pageSystemDemo.getResults().get(0).getName());

        pageSystemDemo = demoServiceAccess.getAll(null, 1, 10, orderBy, false);
        assertTrue(pageSystemDemo.getTotalResults()>=size);
        assertEquals("a-4",pageSystemDemo.getResults().get(1).getName());
        assertEquals("name",pageSystemDemo.getResults().get(0).getName());
    }

    private void listSystemDemoSave(Long size) throws DemoNotFoundException {
        for (int i = 0; i < size; i++) {
            SystemDemo systemDemo1 = DemoFactory.create("a-"+i);
            demoServiceAccess.save(systemDemo1);
        }
    }

}
