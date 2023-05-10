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

package io.radien.ms.doctypemanagement.datalayer;

import io.radien.api.entity.Page;
import io.radien.api.model.docmanagement.propertydefinition.SystemPropertyDefinition;
import io.radien.api.service.docmanagement.propertydefinition.PropertyDefinitionDataAccessLayer;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.doctypemanagement.client.entities.PropertyDefinition;
import io.radien.ms.doctypemanagement.entities.PropertyDefinitionEntity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PropertyDefinitionDataLayerTest {
    static PropertyDefinitionDataAccessLayer propertyDefinitionDAL;
    static PropertyDefinitionEntity propertyTest;
    static EJBContainer container;

    @BeforeClass
    public static void start() throws Exception {
        Properties p = new Properties();
        p.put("openejb.deployments.classpath.include",".*doctypemanagement.*");
        p.put("openejb.deployments.classpath.exclude",".*client.*");
        container = EJBContainer.createEJBContainer(p);
        final Context context = container.getContext();

        propertyDefinitionDAL = (PropertyDefinitionDataAccessLayer)
                context.lookup("java:global/rd-ms-doctypemanagement//PropertyDefinitionDataLayer");

        Page<? extends SystemPropertyDefinition> propertiesPage =
                propertyDefinitionDAL.getAll(null, 1, 10, null, true);
        if (propertiesPage.getTotalResults() > 0) {
            propertyTest = (PropertyDefinitionEntity) propertiesPage.getResults().get(0);
        } else {
            propertyTest = new PropertyDefinitionEntity(createPropertyDefinition());
            propertyDefinitionDAL.save(propertyTest);
        }
    }

    private static PropertyDefinition createPropertyDefinition() throws UniquenessConstraintException {
        PropertyDefinition result = new PropertyDefinition();
        result.setName("test");
        result.setMandatory(false);
        result.setProtected(false);
        result.setRequiredType(1);
        result.setMultiple(true);

        return result;
    }

    @Before
    public void inject() throws NamingException {
        container.getContext().bind("inject", this);
    }

    @AfterClass
    public static void stop() {
        if (container != null) {
            container.close();
        }
    }

    @Test
    public void testAddAction() {
        SystemPropertyDefinition result = propertyDefinitionDAL.get(propertyTest.getId());
        assertNotNull(result);
    }

    @Test
    public void testGetNotExistentAction() {
        SystemPropertyDefinition result = propertyDefinitionDAL.get(111111111L);
        assertNull(result);
    }

    @Test(expected = UniquenessConstraintException.class)
    public void testAddDuplicatedName() throws UniquenessConstraintException {
        PropertyDefinition propertyDefinition = createPropertyDefinition();
        propertyDefinition.setId(null);
        PropertyDefinitionEntity u = new PropertyDefinitionEntity(propertyDefinition);
        propertyDefinitionDAL.save(u);
    }

    @Test
    public void testGetById() {
        SystemPropertyDefinition result = propertyDefinitionDAL.get(propertyTest.getId());
        assertNotNull(result);
        assertEquals(propertyTest.getName(), result.getName());
    }

    @Test
    public void testDeleteById() {
        SystemPropertyDefinition result = propertyDefinitionDAL.get(propertyTest.getId());
        assertNotNull(result);
        assertEquals(propertyTest.getName(), result.getName());
        propertyDefinitionDAL.delete(propertyTest.getId());
        result = propertyDefinitionDAL.get(propertyTest.getId());
        assertNull(result);
    }

    @Test
    public void testGetNames() {
        List<String> result = propertyDefinitionDAL.getNames(Collections.singletonList(propertyTest.getId()));
        assertEquals(1, result.size());
        assertEquals("test", result.get(0));
    }

    @Test
    public void testGetAllSort() throws UniquenessConstraintException {
        PropertyDefinitionEntity p1 = new PropertyDefinitionEntity(createPropertyDefinition());
        p1.setId(null);
        p1.setName("a");
        propertyDefinitionDAL.save(p1);
        PropertyDefinitionEntity p2 = new PropertyDefinitionEntity(createPropertyDefinition());
        p2.setId(null);
        p2.setName("b");
        propertyDefinitionDAL.save(p2);
        PropertyDefinitionEntity p3 = new PropertyDefinitionEntity(createPropertyDefinition());
        p3.setId(null);
        p3.setName("c");
        propertyDefinitionDAL.save(p3);

        List<String> orderby = new ArrayList<>();
        orderby.add("name");

        Page<? extends SystemPropertyDefinition> propertyPage = propertyDefinitionDAL.getAll(null, 1, 10,
                orderby, true);

        assertTrue(propertyPage.getTotalResults()>=3);

        assertEquals("a", propertyPage.getResults().get(0).getName());

        propertyPage = propertyDefinitionDAL.getAll(null, 1, 10, orderby, false);
        assertTrue(propertyPage.getTotalResults()>=3);
        assertEquals("test",propertyPage.getResults().get(0).getName());

        Page<? extends SystemPropertyDefinition> actionPageWhere = propertyDefinitionDAL.getAll("a", 1, 10, null, true);
        assertEquals(1, actionPageWhere.getTotalResults());

        assertEquals("a",actionPageWhere.getResults().get(0).getName());
    }

}
