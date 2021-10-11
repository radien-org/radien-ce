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
package ${package}.service;

import io.radien.api.model.${resource-name-variable}.System${resource-name};
import io.radien.api.service.${resource-name-variable}.${resource-name}ServiceAccess;

import ${package}.entities.${resource-name};
import ${client-packageName}.services.${resource-name}Factory;

import org.junit.Test;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * @author Rajesh Gavvala
 * @author Nuno Santana
 * @author Bruno Gama
 */
public class ${resource-name}ServiceTest {

    Properties p;
    ${resource-name}ServiceAccess ${resource-name-variable}ServiceAccess;
    System${resource-name} system${resource-name}Test;

    public ${resource-name}ServiceTest() throws Exception {
        p = new Properties();
        p.put("appframeDatabase", "new://Resource?type=DataSource");
        p.put("appframeDatabase.JdbcDriver", "org.hsqldb.jdbcDriver");
        p.put("appframeDatabase.JdbcUrl", "jdbc:hsqldb:mem:radienTest");
        p.put("appframeDatabase.userName", "sa");
        p.put("appframeDatabase.password", "");

        final Context context = EJBContainer.createEJBContainer(p).getContext();
        ${resource-name-variable}ServiceAccess = (${resource-name}ServiceAccess) context.lookup("java:global/${artifactId}//${resource-name}Service");

        String message = "testMessage";
        system${resource-name}Test = new ${resource-name}(${resource-name}Factory.create(message));
        ${resource-name-variable}ServiceAccess.create(system${resource-name}Test);
    }

    @Test
    public void testUpdateSuccess() throws Exception {
        String message1 = "testMessage1";
        System${resource-name} system${resource-name}_1 = new ${resource-name}(${resource-name}Factory.create(message1));
        ${resource-name-variable}ServiceAccess.create(system${resource-name}_1);

        String message3 = "testMessage3";
        system${resource-name}_1.setMessage(message3);
        ${resource-name-variable}ServiceAccess.update(system${resource-name}_1);

        system${resource-name}_1 = ${resource-name-variable}ServiceAccess.get(system${resource-name}_1.getId());
        assertEquals(message3, system${resource-name}_1.getMessage());
    }

}