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
package io.rd.web.impl;

import io.rd.api.model.SystemDemo;
import io.rd.api.service.DemoRESTServiceAccess;
import io.rd.exception.SystemException;
import io.rd.ms.client.entities.Demo;

import io.rd.webapp.JSFDemoUtil;
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
@PrepareForTest({FacesContext.class, JSFDemoUtil.class})
public class DemoDataModelTest extends AbstractManager {

    @InjectMocks
    private DemoDataModel demoDataModel;
    @Mock
    private LazyDataModel<? extends SystemDemo> lazyDemoDataModel;
    @Mock
    private DemoRESTServiceAccess service;
    @Mock
    private FacesContext context;
    @Mock
    private ExternalContext externalContext;
    @Mock
    private SelectEvent<SystemDemo> selectEvent;
    @Mock
    private SystemDemo systemDemoMock;
    @Mock
    private Logger log;

    @Mock
    private Application app;

    private SystemDemo systemDemo;


    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
        systemDemo = new Demo();
        systemDemo.setId(1L);
        systemDemo.setName("name-1");

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
        demoDataModel.init();
        assertNotNull(demoDataModel.getLazyDemoDataModel());
        demoDataModel.setLazyDemoDataModel(lazyDemoDataModel);
        assertEquals(demoDataModel.getLazyDemoDataModel(),lazyDemoDataModel);
    }

    @Test
    public void onload_test(){
        demoDataModel.onload();
        Object expected = demoDataModel.getLazyDemoDataModel();

        demoDataModel.init();
        Object actual = demoDataModel.getLazyDemoDataModel();

        assertNotEquals(expected,actual);
    }

    @Test
    public void createDemo_save_test() {
        boolean success;
        try{
            demoDataModel.createDemo(systemDemo);
            success = true;
        } catch (Exception e){
            success = false;
        }
        assertTrue(success);
        assertEquals("correspondingKeyValue", JSFDemoUtil.getMessage("msg", "save_success"));
    }

    @Test
    public void createDemo_saveError_test() throws SystemException {
        boolean success;
        SystemDemo systemDemo1 = new Demo();
        systemDemo1.setId(100L);
        when(service.save(any())).thenThrow(RuntimeException.class);
        try{
            demoDataModel.createDemo(systemDemo1);
            success = true;
        } catch (Exception e){
            success = false;
        }
        assertTrue(success);
        assertEquals("correspondingKeyValue", JSFDemoUtil.getMessage("msg", "save_error"));
    }

    @Test
    public void updateDemo_save_test() {
        boolean success;
        systemDemo.setId(1L);
        systemDemo.setName("name-1-updated");
        try{
            demoDataModel.createDemo(demoDataModel.getSelectedDemo());
            success = true;
        } catch (Exception e){
            success = false;
        }
        assertTrue(success);
        assertEquals("correspondingKeyValue", JSFDemoUtil.getMessage("msg", "edit_success"));
    }

    @Test
    public void updateDemo_saveError_test() throws SystemException {
        boolean success;
        SystemDemo systemDemo1 = new Demo();
        systemDemo1.setId(2L);
        when(service.save(any())).thenThrow(RuntimeException.class);
        try{
            demoDataModel.updateDemo(systemDemo1);
            success = true;
        } catch (Exception e){
            success = false;
        }
        assertTrue(success);
        assertEquals("correspondingKeyValue", JSFDemoUtil.getMessage("msg", "edit_error"));
    }

    @Test
    public void deleteDemo_success_test() {
        boolean success;
        SystemDemo systemDemo2 = new Demo();
        systemDemo2.setId(3L);
        systemDemo2.setName("name-delete");
        demoDataModel.setSelectedDemo(systemDemo2);
        demoDataModel.deleteDemo();
        try{
            service.deleteDemo(demoDataModel.getSelectedDemo().getId());
            success = true;
        } catch (Exception e){
            success = false;
        }
        assertTrue(success);
        assertEquals("correspondingKeyValue", JSFDemoUtil.getMessage("msg", "delete_success"));
    }

    @Test
    public void deleteDemo_error_test(){
        boolean success;
        SystemDemo systemDemo2 = new Demo();
        systemDemo2.setId(4L);
        demoDataModel.setSelectedDemo(systemDemo2);
        when(service.deleteDemo(anyLong())).thenThrow(RuntimeException.class);
        try{
            demoDataModel.deleteDemo();
            success = true;
        } catch (Exception e){
            success = false;
        }
        assertTrue(success);
        assertEquals("correspondingKeyValue", JSFDemoUtil.getMessage("msg", "delete_error"));
    }



    @Test
    public void setService_test(){
        demoDataModel.setService(service);
        DemoRESTServiceAccess serviceObject = demoDataModel.getService();
        assertNotNull(serviceObject);
        assertEquals(serviceObject,service);
    }

    @Test
    public void getService_test(){
        assertNotNull(demoDataModel.getService());

        demoDataModel.setService(null);
        assertNull(demoDataModel.getService());
    }

    @Test
    public void createDemo_test(){
        assertTrue(demoDataModel.createDemo().contains("pretty"));
    }


    @Test
    public void editDemo_test(){
        demoDataModel.setSelectedDemo(null);
        assertTrue(demoDataModel.editDemo().contains("entities"));

        demoDataModel.setSelectedDemo(systemDemo);
        assertTrue(demoDataModel.editDemo().contains("editEntity"));
    }

    @Test
    public void returnHome_test(){
        Object expected = demoDataModel.getSelectedDemo();
        assertSame(null, expected);
        assertFalse(demoDataModel.returnHome().contains("returnHome"));
    }

    @Test
    public void getDemo_test(){
        demoDataModel.setDemo(systemDemo);
        assertEquals(demoDataModel.getDemo(),systemDemo);
    }

    @Test
    public void setDemo_test(){
        demoDataModel.setDemo(systemDemo);
        assertNotNull(demoDataModel.getDemo());
    }

    @Test
    public void onRowSelect_test() {
        when(selectEvent.getObject()).thenReturn(systemDemoMock);
        when(systemDemoMock.getName()).thenReturn("name-1");
        demoDataModel.onRowSelect(selectEvent);
        FacesMessage msg = new FacesMessage("entity_selected", String.valueOf(selectEvent.getObject()));
        assertEquals("correspondingKeyValue", JSFDemoUtil.getMessage("msg", String.format("{0}",msg.getDetail())));
    }

    @Test
    public void getLog_test(){
        Logger logger = getLog();
        log = LoggerFactory.getLogger(AbstractManager.class);
        assertEquals(logger,log);
    }
}
