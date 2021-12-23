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
 *
 */
package io.radien.ms.legal.services;

import io.radien.api.SystemVariables;
import io.radien.api.entity.Page;
import io.radien.api.model.legal.SystemLegalDocumentType;
import io.radien.api.model.legal.SystemLegalDocumentTypeSearchFilter;
import io.radien.api.service.legal.LegalDocumentTypeServiceAccess;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.LegalDocumentTypeNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.ecm.client.entities.legal.LegalDocumentTypeSearchFilter;
import io.radien.ms.legal.entities.LegalDocumentTypeEntity;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import javax.ejb.EJBException;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.NamingException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

/**
 * Test for class {@link LegalDocumentTypeService}
 * @author Newton Carvalho
 */
public class LegalDocumentTypeServiceTest {
    static Properties p;
    static EJBContainer container;
    static LegalDocumentTypeServiceAccess legalDocumentTypeService;
    
    @BeforeClass
    public static void start() {
        p = new Properties();
        container = EJBContainer.createEJBContainer(p);
    }

    /**
     * Injection method before starting the tests and data preparation
     * @throws NamingException in case of naming injection value exception
     */
    @Before
    public void inject() throws NamingException {
        String lookupString = "java:global/rd-ms-legal-lib//LegalDocumentTypeService";
        legalDocumentTypeService = (LegalDocumentTypeServiceAccess) container.getContext().lookup(lookupString);
    }

    /**
     * Test for method {@link LegalDocumentTypeService#create(SystemLegalDocumentType)}
     * @throws UniquenessConstraintException in case of repeated information
     * @throws LegalDocumentTypeNotFoundException in case of not finding a legal document type
     */
    @Test
    public void testCreate() throws UniquenessConstraintException, LegalDocumentTypeNotFoundException {
        SystemLegalDocumentType legalDocumentType = new LegalDocumentTypeEntity();
        legalDocumentType.setName("ToU");
        legalDocumentType.setTenantId(1L);
        legalDocumentType.setToBeAccepted(true);
        legalDocumentType.setToBeShown(true);
        legalDocumentTypeService.create(legalDocumentType);

        SystemLegalDocumentType legalDocumentType2 = legalDocumentTypeService.
                get(legalDocumentType.getId());
        assertNotNull(legalDocumentType2);
        assertEquals(legalDocumentType.getId(), legalDocumentType2.getId());

        legalDocumentTypeService.delete(legalDocumentType2.getId());
    }

    /**
     * Test for method {@link LegalDocumentTypeService#create(SystemLegalDocumentType)}
     * Scenario: Trying to insert a Legal Document Type containing repeated information,
     * It will trigger {@link UniquenessConstraintException} to be thrown
     * @throws UniquenessConstraintException in case of repeated information
     * @throws LegalDocumentTypeNotFoundException in case of not finding a legal document type
     */
    @Test
    public void testCreateFail() throws UniquenessConstraintException, LegalDocumentTypeNotFoundException {
        SystemLegalDocumentType legalDocumentType = new LegalDocumentTypeEntity();
        legalDocumentType.setName("ToU");
        legalDocumentType.setTenantId(1L);
        legalDocumentType.setToBeAccepted(true);
        legalDocumentType.setToBeShown(true);
        legalDocumentTypeService.create(legalDocumentType);

        final SystemLegalDocumentType legalDocumentType2 = new LegalDocumentTypeEntity();
        legalDocumentType2.setName("ToU");
        legalDocumentType2.setTenantId(1L);
        legalDocumentType2.setToBeAccepted(true);
        legalDocumentType2.setToBeShown(true);

        String expectedErrorMsg = GenericErrorCodeMessage.DUPLICATED_FIELD_COMBINATION.
                toString(SystemVariables.NAME.getFieldName(), SystemVariables.TENANT_ID.getFieldName());

        UniquenessConstraintException u = assertThrows(UniquenessConstraintException.class,
                ()-> legalDocumentTypeService.create(legalDocumentType2));

        assertEquals(expectedErrorMsg, u.getMessage());
        legalDocumentTypeService.delete(legalDocumentType.getId());
    }

    /**
     * Test for method {@link LegalDocumentTypeService#update(SystemLegalDocumentType)}
     * @throws UniquenessConstraintException in case of repeated information
     * @throws LegalDocumentTypeNotFoundException in case of not finding a legal document type
     */
    @Test
    public void testUpdate() throws UniquenessConstraintException, LegalDocumentTypeNotFoundException {
        SystemLegalDocumentType legalDocumentType = new LegalDocumentTypeEntity();
        legalDocumentType.setName("ToU");
        legalDocumentType.setTenantId(1L);
        legalDocumentType.setToBeAccepted(true);
        legalDocumentType.setToBeShown(true);
        legalDocumentTypeService.create(legalDocumentType);

        SystemLegalDocumentType legalDocumentType2 = legalDocumentTypeService.
                get(legalDocumentType.getId());
        assertNotNull(legalDocumentType2);
        assertEquals(legalDocumentType.getId(), legalDocumentType2.getId());

        legalDocumentType2.setToBeAccepted(false);
        legalDocumentType2.setToBeShown(false);
        legalDocumentType2.setName("ToU2");
        legalDocumentType2.setTenantId(2L);

        legalDocumentTypeService.update(legalDocumentType2);
        SystemLegalDocumentType legalDocumentType3 = legalDocumentTypeService.
                get(legalDocumentType.getId());

        assertEquals(legalDocumentType2.getId(), legalDocumentType3.getId());
        assertEquals(legalDocumentType2.getName(), legalDocumentType3.getName());
        assertEquals(legalDocumentType2.getTenantId(), legalDocumentType3.getTenantId());
        assertEquals(legalDocumentType2.isToBeAccepted(), legalDocumentType3.isToBeAccepted());
        assertEquals(legalDocumentType2.isToBeShown(), legalDocumentType3.isToBeShown());

        legalDocumentTypeService.delete(legalDocumentType.getId());
    }

    /**
     * Test for method {@link LegalDocumentTypeService#update(SystemLegalDocumentType)}
     * Scenario: Fail when trying to update with repeated information (from the perspective of other
     * Legal Document Type instances)
     * @throws UniquenessConstraintException in case of repeated information
     * @throws LegalDocumentTypeNotFoundException in case of not finding a legal document type
     */
    @Test
    public void testUpdateFail() throws UniquenessConstraintException, LegalDocumentTypeNotFoundException {
        SystemLegalDocumentType legalDocumentType = new LegalDocumentTypeEntity();
        legalDocumentType.setName("ToU");
        legalDocumentType.setTenantId(1L);
        legalDocumentTypeService.create(legalDocumentType);

        SystemLegalDocumentType legalDocumentType2 = new LegalDocumentTypeEntity();
        legalDocumentType2.setName("TaC");
        legalDocumentType2.setTenantId(2L);
        legalDocumentTypeService.create(legalDocumentType2);

        SystemLegalDocumentType legalDocumentType3 = new LegalDocumentTypeEntity();
        legalDocumentType3.setId(legalDocumentType2.getId());
        legalDocumentType3.setName("ToU");
        legalDocumentType3.setTenantId(1L);

        String expectedErrorMsg = GenericErrorCodeMessage.DUPLICATED_FIELD_COMBINATION.
                toString(SystemVariables.NAME.getFieldName(), SystemVariables.TENANT_ID.getFieldName());

        UniquenessConstraintException u = assertThrows(UniquenessConstraintException.class,
                ()-> legalDocumentTypeService.update(legalDocumentType3));

        assertEquals(expectedErrorMsg, u.getMessage());
        legalDocumentTypeService.delete(legalDocumentType.getId());
        legalDocumentTypeService.delete(legalDocumentType2.getId());
    }

    /**
     * Test for method {@link LegalDocumentTypeService#update(SystemLegalDocumentType)}
     * Scenario: Fail when trying to update a resource (LegalDocumentType instance) that does not exist
     */
    @Test
    public void testUpdateFailWhenNotExist() {
        String expectedErrorMsg = GenericErrorCodeMessage.RESOURCE_NOT_FOUND.toString();

        SystemLegalDocumentType legalDocumentType = new LegalDocumentTypeEntity();
        legalDocumentType.setName("ToU");
        legalDocumentType.setTenantId(1L);
        legalDocumentType.setId(111111111L);

        LegalDocumentTypeNotFoundException u = assertThrows(LegalDocumentTypeNotFoundException.class,
                ()-> legalDocumentTypeService.update(legalDocumentType));

        assertEquals(expectedErrorMsg, u.getMessage());
    }

    /**
     * Test for method {@link LegalDocumentTypeService#delete(Long)}
     * Scenario: Fail when trying to delete a resource (Legal Document Type instance) due not informing an Id
     */
    @Test
    public void testDeleteWithNoId() {
        String expectedErrorMsg = GenericErrorCodeMessage.LEGAL_DOCUMENT_TYPE_FIELD_MANDATORY.
                toString(SystemVariables.ID.getLabel());
        Long id = null;
        EJBException u = assertThrows(EJBException.class,  ()-> legalDocumentTypeService.delete(id));
        assertNotNull(u.getCause());
        assertEquals(expectedErrorMsg, u.getCause().getMessage());
    }

    /**
     * Test for method {@link LegalDocumentTypeService#delete(Long)}
     * Scenario: Fail when trying to delete a resource (Legal Document Type instance) that does not exist
     */
    @Test
    public void testDeleteWithNotExistentId() {
        String expectedErrorMsg = GenericErrorCodeMessage.RESOURCE_NOT_FOUND.toString();
        Long id = 111111111111L;
        LegalDocumentTypeNotFoundException u = assertThrows(LegalDocumentTypeNotFoundException.class,
                ()-> legalDocumentTypeService.delete(id));
        assertEquals(expectedErrorMsg, u.getMessage());
    }

    /**
     * Test for method {@link LegalDocumentTypeService#get(Long)}
     * Scenario: Fail when trying to get a resource (Legal Document Type instance) that does not exist
     * @throws LegalDocumentTypeNotFoundException in case of not finding a legal document type
     */
    @Test(expected = LegalDocumentTypeNotFoundException.class)
    public void testGetWithNotExistentId() throws LegalDocumentTypeNotFoundException {
        Long id = 111111111111L;
        legalDocumentTypeService.get(id);
    }

    /**
     * Test for method {@link LegalDocumentTypeService#getAll(String, int, int, List, boolean)}
     * @throws UniquenessConstraintException in case of repeated information
     * @throws LegalDocumentTypeNotFoundException in case of not finding a legal document type
     */
    @Test
    public void testGetAll() throws UniquenessConstraintException, LegalDocumentTypeNotFoundException {
        SystemLegalDocumentType legalDocumentType = new LegalDocumentTypeEntity();
        legalDocumentType.setName("ToU");
        legalDocumentType.setTenantId(1L);
        legalDocumentTypeService.create(legalDocumentType);

        SystemLegalDocumentType legalDocumentType2 = new LegalDocumentTypeEntity();
        legalDocumentType2.setName("TaC");
        legalDocumentType2.setTenantId(2L);
        legalDocumentTypeService.create(legalDocumentType2);

        SystemLegalDocumentType legalDocumentType3 = new LegalDocumentTypeEntity();
        legalDocumentType3.setName("ToU2");
        legalDocumentType3.setTenantId(1L);
        legalDocumentTypeService.create(legalDocumentType3);

        SystemLegalDocumentType legalDocumentType4 = new LegalDocumentTypeEntity();
        legalDocumentType4.setName("Awr");
        legalDocumentType4.setTenantId(1L);
        legalDocumentTypeService.create(legalDocumentType4);

        Page page  = legalDocumentTypeService.getAll("T%", 1, 1,
                Arrays.asList(SystemVariables.ID.getFieldName(), SystemVariables.NAME.getFieldName()), true);

        assertEquals(3, page.getTotalPages());
        assertEquals(1, page.getCurrentPage());
        assertEquals(3, page.getTotalResults());

        page  = legalDocumentTypeService.getAll(null, 1, 1, null, false);
        assertEquals(4, page.getTotalPages());

        page  = legalDocumentTypeService.getAll("ZZ", 1, 1, null, false);
        assertEquals(0, page.getTotalPages());

        legalDocumentTypeService.delete(legalDocumentType.getId());
        legalDocumentTypeService.delete(legalDocumentType2.getId());
        legalDocumentTypeService.delete(legalDocumentType3.getId());
        legalDocumentTypeService.delete(legalDocumentType4.getId());
    }

    /**
     * Test for method {@link LegalDocumentTypeService#getLegalDocumentTypes(SystemLegalDocumentTypeSearchFilter)}
     * @throws UniquenessConstraintException in case of repeated information
     * @throws LegalDocumentTypeNotFoundException in case of not finding a legal document type
     */
    @Test
    public void testGetByFilter() throws UniquenessConstraintException, LegalDocumentTypeNotFoundException {
        SystemLegalDocumentType legalDocumentType = new LegalDocumentTypeEntity();
        legalDocumentType.setName("ToU");
        legalDocumentType.setTenantId(1L);
        legalDocumentType.setToBeShown(false);
        legalDocumentType.setToBeAccepted(false);
        legalDocumentTypeService.create(legalDocumentType);

        SystemLegalDocumentType legalDocumentType2 = new LegalDocumentTypeEntity();
        legalDocumentType2.setName("TaC");
        legalDocumentType2.setTenantId(2L);
        legalDocumentTypeService.create(legalDocumentType2);

        SystemLegalDocumentType legalDocumentType3 = new LegalDocumentTypeEntity();
        legalDocumentType3.setName("ToU2");
        legalDocumentType3.setTenantId(1L);
        legalDocumentType3.setToBeShown(false);
        legalDocumentType3.setToBeAccepted(false);
        legalDocumentTypeService.create(legalDocumentType3);

        LegalDocumentTypeSearchFilter filter = new LegalDocumentTypeSearchFilter();
        filter.setName("ToU");
        filter.setExact(false);
        filter.setExact(false);
        filter.setLogicConjunction(true);
        List<? extends SystemLegalDocumentType> list = legalDocumentTypeService.getLegalDocumentTypes(filter);
        assertEquals(2, list.size());

        filter = new LegalDocumentTypeSearchFilter();
        filter.setToBeAccepted(null);
        filter.setToBeAccepted(null);
        filter.setIds(Collections.singletonList(legalDocumentType.getId()));
        list = legalDocumentTypeService.getLegalDocumentTypes(filter);
        assertEquals(1, list.size());

        legalDocumentTypeService.delete(legalDocumentType.getId());
        legalDocumentTypeService.delete(legalDocumentType2.getId());
        legalDocumentTypeService.delete(legalDocumentType3.getId());
    }


    /**
     * Method to stop the container after the testing classes have perform
     */
    @AfterClass
    public static void stop() {
        if (container != null) {
            container.close();
        }
    }
}
