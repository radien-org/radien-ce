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
package ${package}.web.impl;

import ${package}.api.model.System${entityResourceName};
import ${package}.api.service.${entityResourceName}RESTServiceAccess;
import ${package}.exception.SystemException;
import ${package}.ms.client.entities.${entityResourceName};
import ${package}.web.impl.AbstractManager;
import ${package}.web.impl.${entityResourceName}DataModel;

import ${package}.webapp.JSFUtil;
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
public class ${entityResourceName}DataModelTest extends AbstractManager {

    @InjectMocks
    private ${entityResourceName}DataModel ${entityResourceName.toLowerCase()}DataModel;
    @Mock
    private LazyDataModel<? extends System${entityResourceName}> lazy${entityResourceName}DataModel;
    @Mock
    private ${entityResourceName}RESTServiceAccess service;
    @Mock
    private FacesContext context;
    @Mock
    private ExternalContext externalContext;
    @Mock
    private SelectEvent<System${entityResourceName}> selectEvent;
    @Mock
    private System${entityResourceName} system${entityResourceName}Mock;
    @Mock
    private Logger log;

    @Mock
    private Application app;

    private System${entityResourceName} system${entityResourceName};


    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
        system${entityResourceName} = new ${entityResourceName}();
        system${entityResourceName}.setId(1L);
        system${entityResourceName}.setName("name-1");

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
        ${entityResourceName.toLowerCase()}DataModel.init();
        assertNotNull(${entityResourceName.toLowerCase()}DataModel.getLazy${entityResourceName}DataModel());
        ${entityResourceName.toLowerCase()}DataModel.setLazy${entityResourceName}DataModel(lazy${entityResourceName}DataModel);
        assertEquals(${entityResourceName.toLowerCase()}DataModel.getLazy${entityResourceName}DataModel(),lazy${entityResourceName}DataModel);
    }

    @Test
    public void onload_test(){
        ${entityResourceName.toLowerCase()}DataModel.onload();
        Object expected = ${entityResourceName.toLowerCase()}DataModel.getLazy${entityResourceName}DataModel();

        ${entityResourceName.toLowerCase()}DataModel.init();
        Object actual = ${entityResourceName.toLowerCase()}DataModel.getLazy${entityResourceName}DataModel();

        assertNotEquals(expected,actual);
    }

    @Test
    public void create${entityResourceName}_save_test() {
        boolean success;
        try{
            ${entityResourceName.toLowerCase()}DataModel.create${entityResourceName}(system${entityResourceName});
            success = true;
        } catch (Exception e){
            success = false;
        }
        assertTrue(success);
        assertEquals("correspondingKeyValue", JSFUtil.getMessage("msg", "save_success"));
    }

    @Test
    public void create${entityResourceName}_saveError_test() throws SystemException {
        boolean success;
        System${entityResourceName} system${entityResourceName}1 = new ${entityResourceName}();
        system${entityResourceName}1.setId(100L);
        when(service.save(any())).thenThrow(RuntimeException.class);
        try{
            ${entityResourceName.toLowerCase()}DataModel.create${entityResourceName}(system${entityResourceName}1);
            success = true;
        } catch (Exception e){
            success = false;
        }
        assertTrue(success);
        assertEquals("correspondingKeyValue", JSFUtil.getMessage("msg", "save_error"));
    }

    @Test
    public void update${entityResourceName}_save_test() {
        boolean success;
        system${entityResourceName}.setId(1L);
        system${entityResourceName}.setName("name-1-updated");
        try{
            ${entityResourceName.toLowerCase()}DataModel.create${entityResourceName}(${entityResourceName.toLowerCase()}DataModel.getSelected${entityResourceName}());
            success = true;
        } catch (Exception e){
            success = false;
        }
        assertTrue(success);
        assertEquals("correspondingKeyValue", JSFUtil.getMessage("msg", "edit_success"));
    }

    @Test
    public void update${entityResourceName}_saveError_test() throws SystemException {
        boolean success;
        System${entityResourceName} system${entityResourceName}1 = new ${entityResourceName}();
        system${entityResourceName}1.setId(2L);
        when(service.save(any())).thenThrow(RuntimeException.class);
        try{
            ${entityResourceName.toLowerCase()}DataModel.update${entityResourceName}(system${entityResourceName}1);
            success = true;
        } catch (Exception e){
            success = false;
        }
        assertTrue(success);
        assertEquals("correspondingKeyValue", JSFUtil.getMessage("msg", "edit_error"));
    }

    @Test
    public void delete${entityResourceName}_success_test() {
        boolean success;
        System${entityResourceName} system${entityResourceName}2 = new ${entityResourceName}();
        system${entityResourceName}2.setId(3L);
        system${entityResourceName}2.setName("name-delete");
        ${entityResourceName.toLowerCase()}DataModel.setSelected${entityResourceName}(system${entityResourceName}2);
        ${entityResourceName.toLowerCase()}DataModel.delete${entityResourceName}();
        try{
            service.delete${entityResourceName}(${entityResourceName.toLowerCase()}DataModel.getSelected${entityResourceName}().getId());
            success = true;
        } catch (Exception e){
            success = false;
        }
        assertTrue(success);
        assertEquals("correspondingKeyValue", JSFUtil.getMessage("msg", "delete_success"));
    }

    @Test
    public void delete${entityResourceName}_error_test(){
        boolean success;
        System${entityResourceName} system${entityResourceName}2 = new ${entityResourceName}();
        system${entityResourceName}2.setId(4L);
        ${entityResourceName.toLowerCase()}DataModel.setSelected${entityResourceName}(system${entityResourceName}2);
        when(service.delete${entityResourceName}(anyLong())).thenThrow(RuntimeException.class);
        try{
            ${entityResourceName.toLowerCase()}DataModel.delete${entityResourceName}();
            success = true;
        } catch (Exception e){
            success = false;
        }
        assertTrue(success);
        assertEquals("correspondingKeyValue", JSFUtil.getMessage("msg", "delete_error"));
    }



    @Test
    public void setService_test(){
        ${entityResourceName.toLowerCase()}DataModel.setService(service);
        ${entityResourceName}RESTServiceAccess serviceObject = ${entityResourceName.toLowerCase()}DataModel.getService();
        assertNotNull(serviceObject);
        assertEquals(serviceObject,service);
    }

    @Test
    public void getService_test(){
        assertNotNull(${entityResourceName.toLowerCase()}DataModel.getService());

        ${entityResourceName.toLowerCase()}DataModel.setService(null);
        assertNull(${entityResourceName.toLowerCase()}DataModel.getService());
    }

    @Test
    public void create${entityResourceName}_test(){
        assertTrue(${entityResourceName.toLowerCase()}DataModel.create${entityResourceName}().contains("pretty"));
    }


    @Test
    public void edit${entityResourceName}_test(){
        ${entityResourceName.toLowerCase()}DataModel.setSelected${entityResourceName}(null);
        assertTrue(${entityResourceName.toLowerCase()}DataModel.edit${entityResourceName}().contains("entities"));

        ${entityResourceName.toLowerCase()}DataModel.setSelected${entityResourceName}(system${entityResourceName});
        assertTrue(${entityResourceName.toLowerCase()}DataModel.edit${entityResourceName}().contains("editEntity"));
    }

    @Test
    public void returnHome_test(){
        Object expected = ${entityResourceName.toLowerCase()}DataModel.getSelected${entityResourceName}();
        assertSame(expected, null);
        assertFalse(${entityResourceName.toLowerCase()}DataModel.returnHome().contains("returnHome"));
    }

    @Test
    public void get${entityResourceName}_test(){
        ${entityResourceName.toLowerCase()}DataModel.set${entityResourceName}(system${entityResourceName});
        assertEquals(${entityResourceName.toLowerCase()}DataModel.get${entityResourceName}(),system${entityResourceName});
    }

    @Test
    public void set${entityResourceName}_test(){
        ${entityResourceName.toLowerCase()}DataModel.set${entityResourceName}(system${entityResourceName});
        assertNotNull(${entityResourceName.toLowerCase()}DataModel.get${entityResourceName}());
    }

    @Test
    public void onRowSelect_test() {
        when(selectEvent.getObject()).thenReturn(system${entityResourceName}Mock);
        when(system${entityResourceName}Mock.getName()).thenReturn("name-1");
        ${entityResourceName.toLowerCase()}DataModel.onRowSelect(selectEvent);
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
