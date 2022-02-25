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
package io.radien.webapp.contract;

import io.radien.api.model.tenant.SystemContract;
import io.radien.api.service.tenant.ContractRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.permissionmanagement.client.entities.Action;
import io.radien.ms.tenantmanagement.client.entities.Contract;
import io.radien.ms.tenantmanagement.client.services.ContractFactory;
import io.radien.webapp.DataModelEnum;
import io.radien.webapp.JSFUtil;
import io.radien.webapp.JSFUtilAndFaceContextMessagesTest;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

/**
 * Class that aggregates UnitTest cases for ContractManager
 *
 * @author Rajesh Gavvala
 */
public class ContractManagerTest extends JSFUtilAndFaceContextMessagesTest {

    @InjectMocks
    private ContractManager contractManager;

    @Mock
    private ContractRESTServiceAccess service;

    private SystemContract systemContract;

    private static String CONTRACT_PAGE = "contract";

    Date ct_date_start;
    Date ct_date_end;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @BeforeClass
    public static void beforeClass(){
        handleJSFUtilAndFaceContextMessages();
    }
    @AfterClass
    public static final void afterClass(){
        destroy();
    }
    /**
     * Constructs mock object
     */
    @Before
    public void before(){
        systemContract = new Contract();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowEnd = LocalDateTime.of(2100, 10, 10, 00, 00,00);
        systemContract.setName("myContract");
        systemContract.setStart(now);
        systemContract.setEnd(nowEnd);
        systemContract.setCreateUser(1L);

        ZonedDateTime zdt_start = now.atZone(ZoneId.systemDefault());
        ZonedDateTime zdt_end = nowEnd.atZone(ZoneId.systemDefault());
        ct_date_start = Date.from(zdt_start.toInstant());
        ct_date_end = Date.from(zdt_end.toInstant());

        contractManager.setContractStartDate(ct_date_start);
        contractManager.setContractEndDate(ct_date_end);
    }

    @Test
    public void testSave() throws SystemException {
        doReturn(true).when(service).create(systemContract);
        String expected = contractManager.save(systemContract);
        assertEquals(expected, CONTRACT_PAGE);
    }

    @Test
    public void testUpdate() throws SystemException {
        doReturn(true).when(service).create(systemContract);
        systemContract.setId(2L);
        String expected = contractManager.save(systemContract);
        assertEquals(expected, CONTRACT_PAGE);
    }

    @Test
    public void testEdit() throws SystemException {
        doReturn(true).when(service).create(systemContract);
        systemContract.setId(2L);
        systemContract.setName("editMyContract");
        String expected = contractManager.edit(systemContract);
        assertEquals(expected, CONTRACT_PAGE);
    }

    @Test
    public void testGetContractAndSetContract(){
        contractManager.setContract(systemContract);
        assertEquals(systemContract, contractManager.getContract());
    }

    @Test
    public void testGetContractStartAndEndDates(){
        assertEquals(ct_date_start, contractManager.getContractStartDate());
        assertEquals(ct_date_end, contractManager.getContractEndDate());
    }

}