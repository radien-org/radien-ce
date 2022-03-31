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
import io.radien.api.model.docmanagement.mixindefinition.SystemMixinDefinition;
import io.radien.api.model.docmanagement.propertydefinition.SystemPropertyDefinition;
import io.radien.api.service.docmanagement.mixindefinition.MixinDefinitionDataAccessLayer;
import io.radien.api.service.docmanagement.propertydefinition.PropertyDefinitionDataAccessLayer;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.doctypemanagement.client.entities.MixinDefinitionDTO;
import io.radien.ms.doctypemanagement.client.entities.PropertyDefinition;
import io.radien.ms.doctypemanagement.entities.MixinDefinitionEntity;
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

public class MixinDefinitionDataLayerTest {
    static MixinDefinitionDataAccessLayer mixinDefinitionDAL;
    static PropertyDefinitionDataAccessLayer propertyDefinitionDAL;
    static MixinDefinitionEntity mixinTest;
    static EJBContainer container;

    @BeforeClass
    public static void start() throws Exception {
        Properties p = new Properties();
        p.put("openejb.deployments.classpath.include",".*doctypemanagement.*");
        p.put("openejb.deployments.classpath.exclude",".*client.*");
        container = EJBContainer.createEJBContainer(p);
        final Context context = container.getContext();

        mixinDefinitionDAL = (MixinDefinitionDataAccessLayer)
                context.lookup("java:global/rd-ms-doctypemanagement//MixinDefinitionDataLayer");
        propertyDefinitionDAL = (PropertyDefinitionDataAccessLayer)
                context.lookup("java:global/rd-ms-doctypemanagement//PropertyDefinitionDataLayer");

        Page<? extends SystemMixinDefinition> mixinPage =
                mixinDefinitionDAL.getAll(null, 1, 10, null, true);
        if (mixinPage.getTotalResults() > 0) {
            mixinTest = (MixinDefinitionEntity) mixinPage.getResults().get(0);
        } else {
            mixinTest = new MixinDefinitionEntity(createMixinDefinition());
            mixinDefinitionDAL.save(mixinTest);
        }
    }

    private static MixinDefinitionDTO createMixinDefinition() throws UniquenessConstraintException {
        SystemPropertyDefinition result = new PropertyDefinitionEntity();
        if(propertyDefinitionDAL.getTotalRecordsCount() == 0) {
            result.setName("test");
            result.setMandatory(false);
            result.setProtected(false);
            result.setRequiredType(1);
            result.setMultiple(true);
            propertyDefinitionDAL.save(result);
        }
        MixinDefinitionDTO mixin = new MixinDefinitionDTO();
        mixin.setName("name");
        mixin.setNamespace("rd");
        mixin.setPropertyDefinitions(Collections.singletonList(1L));
        mixin.setAbstract(true);
        mixin.setQueryable(true);
        mixin.setMixin(true);

        return mixin;
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
        SystemMixinDefinition result = mixinDefinitionDAL.get(mixinTest.getId());
        assertNotNull(result);
    }

    @Test
    public void testGetNotExistentAction() {
        SystemMixinDefinition result = mixinDefinitionDAL.get(111111111L);
        assertNull(result);
    }

    @Test(expected = UniquenessConstraintException.class)
    public void testAddDuplicatedName() throws UniquenessConstraintException {
        MixinDefinitionDTO mixinDefinition = createMixinDefinition();
        mixinDefinition.setId(null);
        MixinDefinitionEntity u = new MixinDefinitionEntity(mixinDefinition);
        mixinDefinitionDAL.save(u);
    }

    @Test
    public void testGetById() {
        SystemMixinDefinition result = mixinDefinitionDAL.get(mixinTest.getId());
        assertNotNull(result);
        assertEquals(mixinTest.getName(), result.getName());
    }

    @Test
    public void testDeleteById() {
        SystemMixinDefinition result = mixinDefinitionDAL.get(mixinTest.getId());
        assertNotNull(result);
        assertEquals(mixinTest.getName(), result.getName());
        mixinDefinitionDAL.delete(mixinTest.getId());
        result = mixinDefinitionDAL.get(mixinTest.getId());
        assertNull(result);
    }
    @Test
    public void testGetAllSort() throws UniquenessConstraintException {
        PropertyDefinitionEntity p1 = new PropertyDefinitionEntity((createPropertyDefinition()));
        p1.setId(null);
        p1.setName("a");
        propertyDefinitionDAL.save(p1);
        MixinDefinitionEntity m1 = new MixinDefinitionEntity(createMixinDefinition());
        m1.setId(null);
        m1.setName("a");
        m1.setPropertyDefinitions(Collections.singletonList(2L));
        mixinDefinitionDAL.save(m1);
        PropertyDefinitionEntity p2 = new PropertyDefinitionEntity((createPropertyDefinition()));
        p2.setId(null);
        p2.setName("b");
        propertyDefinitionDAL.save(p2);
        MixinDefinitionEntity m2 = new MixinDefinitionEntity(createMixinDefinition());
        m2.setId(null);
        m2.setName("b");
        m2.setPropertyDefinitions(Collections.singletonList(3L));
        mixinDefinitionDAL.save(m2);
        PropertyDefinitionEntity p3 = new PropertyDefinitionEntity((createPropertyDefinition()));
        p3.setId(null);
        p3.setName("c");
        propertyDefinitionDAL.save(p3);
        MixinDefinitionEntity m3 = new MixinDefinitionEntity(createMixinDefinition());
        m3.setId(null);
        m3.setName("c");
        m3.setPropertyDefinitions(Collections.singletonList(4L));
        mixinDefinitionDAL.save(m3);

        List<String> orderby = new ArrayList<>();
        orderby.add("name");

        Page<? extends SystemMixinDefinition> mixinPage = mixinDefinitionDAL.getAll(null, 1, 10,
                orderby, true);

        assertTrue(mixinPage.getTotalResults()>=3);

        assertEquals("a",mixinPage.getResults().get(0).getName());

        mixinPage = mixinDefinitionDAL.getAll(null, 1, 10, orderby, false);
        assertTrue(mixinPage.getTotalResults()>=3);
        assertEquals("name",mixinPage.getResults().get(0).getName());

        Page<? extends SystemMixinDefinition> actionPageWhere = mixinDefinitionDAL.getAll("a", 1, 10, null, true);
        assertEquals(1, actionPageWhere.getTotalResults());

        assertEquals("a",actionPageWhere.getResults().get(0).getName());
    }

}
