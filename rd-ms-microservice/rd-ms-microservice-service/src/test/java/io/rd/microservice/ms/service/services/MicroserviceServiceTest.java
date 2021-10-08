/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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
package io.rd.microservice.ms.service.services;

import io.rd.microservice.api.entity.Page;
import io.rd.microservice.api.model.SystemMicroservice;
import io.rd.microservice.api.service.MicroserviceServiceAccess;
import io.rd.microservice.exception.MicroserviceNotFoundException;
import io.rd.microservice.ms.service.entities.Microservice;
import io.rd.microservice.ms.service.legacy.MicroserviceFactory;

import org.junit.Before;
import org.junit.Test;

import javax.ejb.embeddable.EJBContainer;

import javax.naming.Context;
import javax.naming.NamingException;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;

public class MicroserviceServiceTest {
    MicroserviceServiceAccess microserviceServiceAccess;
    SystemMicroservice systemMicroservice;
    Microservice microservice;

    public MicroserviceServiceTest() throws NamingException {
        Properties p = new Properties();
        p.put("openejb.deployments.classpath.include",".*");
        p.put("openejb.deployments.classpath.exclude",".*rd-ms-microservice/rd-ms-microservice-client.*");
        final Context context = EJBContainer.createEJBContainer(p).getContext();
        microserviceServiceAccess = (MicroserviceServiceAccess) context.lookup("java:global/rd-ms-microservice-service//MicroserviceService");
    }

    @Before
    public void before() throws MicroserviceNotFoundException {
        Page<? extends SystemMicroservice> pageSystemMicroservice = microserviceServiceAccess.getAll(null, 1, 10,
                null, true);
        if(pageSystemMicroservice.getTotalResults()>0) {
            systemMicroservice = pageSystemMicroservice.getResults().get(0);
        } else {
            systemMicroservice = MicroserviceFactory.create("name");
            microserviceServiceAccess.save(systemMicroservice);
        }
    }

    @Test
    public void save_update_test() throws MicroserviceNotFoundException {
        Microservice microservice1 = (Microservice) microserviceServiceAccess.get(systemMicroservice.getId());
        microserviceServiceAccess.save(microservice1);
    }

    @Test
    public void delete_test() throws MicroserviceNotFoundException {
        SystemMicroservice result = microserviceServiceAccess.get(systemMicroservice.getId());
        assertNotNull(result);
        assertEquals(systemMicroservice.getName(), result.getName());
        microserviceServiceAccess.delete(systemMicroservice.getId());
        boolean success = false;
        try {
            result = microserviceServiceAccess.get(systemMicroservice.getId());
        } catch (MicroserviceNotFoundException e){
            success = true;
        }
        assertTrue(success);
    }

    @Test
    public void getAll_sort_test() throws MicroserviceNotFoundException {
        Long size = 5L;
        listSystemMicroserviceSave(size);

        List<String> orderBy = new ArrayList<>();
        orderBy.add("name");

        Page<? extends SystemMicroservice> pageSystemMicroservice = microserviceServiceAccess.getAll(null, 1, 10,
                orderBy, true);

        assertTrue(pageSystemMicroservice.getTotalResults()>0);

        assertEquals("a-0",pageSystemMicroservice.getResults().get(0).getName());

        pageSystemMicroservice = microserviceServiceAccess.getAll(null, 1, 10, orderBy, false);
        assertTrue(pageSystemMicroservice.getTotalResults()>=size);
        assertEquals("a-4",pageSystemMicroservice.getResults().get(1).getName());
        assertEquals("name",pageSystemMicroservice.getResults().get(0).getName());
    }

    private void listSystemMicroserviceSave(Long size) throws MicroserviceNotFoundException {
        for (int i = 0; i < size; i++) {
            SystemMicroservice systemMicroservice1 = MicroserviceFactory.create("a-"+i);
            microserviceServiceAccess.save(systemMicroservice1);
        }
    }

}
