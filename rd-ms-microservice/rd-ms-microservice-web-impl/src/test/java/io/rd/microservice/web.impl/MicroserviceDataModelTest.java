/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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
package io.rd.microservice.web.impl;

import io.rd.microservice.api.model.SystemMicroservice;
import io.rd.microservice.api.service.MicroserviceRESTServiceAccess;
import io.rd.microservice.exception.SystemException;
import io.rd.microservice.ms.client.entities.Microservice;
import io.rd.microservice.web.impl.AbstractManager;
import io.rd.microservice.web.impl.MicroserviceDataModel;

import io.rd.microservice.webapp.JSFUtil;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.mock.web.MockHttpServletRequest;

import com.sun.faces.config.InitFacesContext;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;


import java.util.Collections;
import java.util.Enumeration;
import java.util.ResourceBundle;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
/**
 *
 * @author Rajesh Gavvala
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.management.*")
@PrepareForTest({FacesContext.class, JSFUtil.class})
public class MicroserviceDataModelTest extends AbstractManager {

    @InjectMocks
    private MicroserviceDataModel microserviceDataModel;
    @Mock
    private LazyDataModel<? extends SystemMicroservice> lazyMicroserviceDataModel;
    @Mock
    private MicroserviceRESTServiceAccess service;
    @Mock
    private FacesContext context;
    @Mock
    private ExternalContext externalContext;
    @Mock
    private SelectEvent<SystemMicroservice> selectEvent;
    @Mock
    private SystemMicroservice systemMicroserviceMock;
    @Mock
    private Logger log;

    @Mock
    private Application app;

    private SystemMicroservice systemMicroservice;


    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
        systemMicroservice = new Microservice();
        systemMicroservice.setId(1L);
        systemMicroservice.setName("name-1");

        ServletContext sc = mock(ServletContext.class);
        new FakeContext(sc);

        ResourceBundle resourceBundle = new ResourceBundle() {
            @Override
            protected Object handleGetObject(@NotNull String key) {
                return "correspondingKeyValue";
            }

            @Override
            public Enumeration<String> getKeys() {
                return Collections.emptyEnumeration();
            }
        };

        HttpServletRequest request = new MockHttpServletRequest();

        assertEquals(context, FacesContext.getCurrentInstance());
        Mockito.doReturn(this.app).when(this.context).getApplication();
        Mockito.doReturn(this.externalContext).when(this.context).getExternalContext();
        Mockito.doReturn(request).when(this.externalContext).getRequest();

        Mockito.doNothing().when(context).addMessage(isA(String.class), isA(FacesMessage.class));
        Mockito.doReturn(resourceBundle).when(this.app).getResourceBundle(this.context, "msg");
    }

    private class FakeContext extends InitFacesContext {

        FakeContext(ServletContext sc) {
            super(sc);
            setCurrentInstance(context);
        }
    }

    @Test
    public void init_test(){
        microserviceDataModel.init();
        assertNotNull(microserviceDataModel.getLazyMicroserviceDataModel());
        microserviceDataModel.setLazyMicroserviceDataModel(lazyMicroserviceDataModel);
        assertEquals(microserviceDataModel.getLazyMicroserviceDataModel(),lazyMicroserviceDataModel);
    }

    @Test
    public void onload_test(){
        microserviceDataModel.onload();
        Object expected = microserviceDataModel.getLazyMicroserviceDataModel();

        microserviceDataModel.init();
        Object actual = microserviceDataModel.getLazyMicroserviceDataModel();

        assertNotEquals(expected,actual);
    }

    @Test
    public void createMicroservice_save_test() {
        boolean success;
        try{
            microserviceDataModel.createMicroservice(systemMicroservice);
            success = true;
        } catch (Exception e){
            success = false;
        }
        assertTrue(success);
        assertEquals("correspondingKeyValue", JSFUtil.getMessage("msg", "save_success"));
    }

    @Test
    public void createMicroservice_saveError_test() throws SystemException {
        boolean success;
        SystemMicroservice systemMicroservice1 = new Microservice();
        systemMicroservice1.setId(100L);
        when(service.save(any())).thenThrow(RuntimeException.class);
        try{
            microserviceDataModel.createMicroservice(systemMicroservice1);
            success = true;
        } catch (Exception e){
            success = false;
        }
        assertTrue(success);
        assertEquals("correspondingKeyValue", JSFUtil.getMessage("msg", "save_error"));
    }

    @Test
    public void updateMicroservice_save_test() {
        boolean success;
        systemMicroservice.setId(1L);
        systemMicroservice.setName("name-1-updated");
        try{
            microserviceDataModel.createMicroservice(microserviceDataModel.getSelectedMicroservice());
            success = true;
        } catch (Exception e){
            success = false;
        }
        assertTrue(success);
        assertEquals("correspondingKeyValue", JSFUtil.getMessage("msg", "edit_success"));
    }

    @Test
    public void updateMicroservice_saveError_test() throws SystemException {
        boolean success;
        SystemMicroservice systemMicroservice1 = new Microservice();
        systemMicroservice1.setId(2L);
        when(service.save(any())).thenThrow(RuntimeException.class);
        try{
            microserviceDataModel.updateMicroservice(systemMicroservice1);
            success = true;
        } catch (Exception e){
            success = false;
        }
        assertTrue(success);
        assertEquals("correspondingKeyValue", JSFUtil.getMessage("msg", "edit_error"));
    }

    @Test
    public void deleteMicroservice_success_test() {
        boolean success;
        SystemMicroservice systemMicroservice2 = new Microservice();
        systemMicroservice2.setId(3L);
        systemMicroservice2.setName("name-delete");
        microserviceDataModel.setSelectedMicroservice(systemMicroservice2);
        microserviceDataModel.deleteMicroservice();
        try{
            service.deleteMicroservice(microserviceDataModel.getSelectedMicroservice().getId());
            success = true;
        } catch (Exception e){
            success = false;
        }
        assertTrue(success);
        assertEquals("correspondingKeyValue", JSFUtil.getMessage("msg", "delete_success"));
    }

    @Test
    public void deleteMicroservice_error_test(){
        boolean success;
        SystemMicroservice systemMicroservice2 = new Microservice();
        systemMicroservice2.setId(4L);
        microserviceDataModel.setSelectedMicroservice(systemMicroservice2);
        when(service.deleteMicroservice(anyLong())).thenThrow(RuntimeException.class);
        try{
            microserviceDataModel.deleteMicroservice();
            success = true;
        } catch (Exception e){
            success = false;
        }
        assertTrue(success);
        assertEquals("correspondingKeyValue", JSFUtil.getMessage("msg", "delete_error"));
    }



    @Test
    public void setService_test(){
        microserviceDataModel.setService(service);
        MicroserviceRESTServiceAccess serviceObject = microserviceDataModel.getService();
        assertNotNull(serviceObject);
        assertEquals(serviceObject,service);
    }

    @Test
    public void getService_test(){
        assertNotNull(microserviceDataModel.getService());

        microserviceDataModel.setService(null);
        assertNull(microserviceDataModel.getService());
    }

    @Test
    public void createMicroservice_test(){
        assertTrue(microserviceDataModel.createMicroservice().contains("pretty"));
    }


    @Test
    public void editMicroservice_test(){
        microserviceDataModel.setSelectedMicroservice(null);
        assertTrue(microserviceDataModel.editMicroservice().contains("entities"));

        microserviceDataModel.setSelectedMicroservice(systemMicroservice);
        assertTrue(microserviceDataModel.editMicroservice().contains("editEntity"));
    }

    @Test
    public void returnHome_test(){
        Object expected = microserviceDataModel.getSelectedMicroservice();
        assertSame(expected, null);
        assertFalse(microserviceDataModel.returnHome().contains("returnHome"));
    }

    @Test
    public void getMicroservice_test(){
        microserviceDataModel.setMicroservice(systemMicroservice);
        assertEquals(microserviceDataModel.getMicroservice(),systemMicroservice);
    }

    @Test
    public void setMicroservice_test(){
        microserviceDataModel.setMicroservice(systemMicroservice);
        assertNotNull(microserviceDataModel.getMicroservice());
    }

    @Test
    public void onRowSelect_test() {
        when(selectEvent.getObject()).thenReturn(systemMicroserviceMock);
        when(systemMicroserviceMock.getName()).thenReturn("name-1");
        microserviceDataModel.onRowSelect(selectEvent);
        FacesMessage msg = new FacesMessage("entity_selected", String.valueOf(selectEvent.getObject()));
        assertEquals("correspondingKeyValue", JSFUtil.getMessage("msg", String.format("{0}",msg.getDetail())));
    }

    @Test
    public void getLog_test(){
        Logger logger = getLog();
        log = LoggerFactory.getLogger(AbstractManager.class);
        assertEquals(logger,log);
    }
}
