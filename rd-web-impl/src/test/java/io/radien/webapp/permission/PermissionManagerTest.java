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
 * See the License for the specific language governing actions and
 * limitations under the License.
 */
package io.radien.webapp.permission;

import io.radien.exception.SystemException;

import io.radien.api.model.permission.SystemAction;
import io.radien.api.model.permission.SystemPermission;
import io.radien.api.model.permission.SystemResource;
import io.radien.api.service.permission.ActionRESTServiceAccess;
import io.radien.api.service.permission.PermissionRESTServiceAccess;
import io.radien.api.service.permission.ResourceRESTServiceAccess;

import io.radien.ms.permissionmanagement.client.entities.Action;
import io.radien.ms.permissionmanagement.client.entities.Permission;
import io.radien.ms.permissionmanagement.client.entities.Resource;

import io.radien.webapp.DataModelEnum;
import io.radien.webapp.JSFUtil;
import io.radien.webapp.JSFUtilAndFaceContextMessagesTest;

import java.util.Optional;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
/**
 * Class that aggregates UnitTest cases for PermissionManager
 *
 * @author Rajesh Gavvala
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({JSFUtil.class, FacesContext.class, ExternalContext.class})
public class PermissionManagerTest extends JSFUtilAndFaceContextMessagesTest {
    @InjectMocks
    private PermissionManager permissionManager;

    @Mock
    private PermissionRESTServiceAccess permissionRESTServiceAccess;

    @Mock
    private ActionRESTServiceAccess actionRESTServiceAccess;

    @Mock
    private ResourceRESTServiceAccess resourceRESTServiceAccess;

    private SystemPermission permission;
    private SystemAction selectedAction;
    private SystemResource selectedResource;

    Optional<SystemAction> optionalSystemAction;
    Optional<SystemResource> optionalSystemResource;

    /**
     * Constructs mock object
     */
    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
        handleJSFUtilAndFaceContextMessages();

        permission = new Permission();
        selectedAction = new Action();
        selectedResource = new Resource();

        permission.setId(1L);

        selectedAction.setId(1L);
        selectedAction.setName("action1");

        selectedResource.setId(1L);
        selectedResource.setName("resource1");

        optionalSystemAction = Optional.ofNullable(selectedAction);
        optionalSystemResource = Optional.ofNullable(selectedResource);

        permissionManager.setSelectedAction(selectedAction);
        permissionManager.setSelectedResource(selectedResource);

    }

    @Test
    public void testSave() throws SystemException {
        doReturn(true).when(permissionRESTServiceAccess).create(permission);
        String expected = permissionManager.save(permission);
        assertEquals(expected, DataModelEnum.PERMISSION_DETAIL_PAGE.getValue());
    }

    @Test(expected = Exception.class)
    public void testSaveException() throws SystemException {
        doThrow(SystemException.class).when(permissionRESTServiceAccess).create(permission);
        permissionManager.save(permission);
    }

    @Test
    public void testEdit() throws SystemException {
        permission.setActionId(1L);
        permission.setResourceId(1L);
        permissionManager.setPermission(permission);

        doReturn(optionalSystemAction).when(actionRESTServiceAccess).getActionById(selectedAction.getId());
        doReturn(optionalSystemResource).when(resourceRESTServiceAccess).getResourceById(selectedResource.getId());

        String expected = permissionManager.edit(permission);
        assertEquals(expected, DataModelEnum.PERMISSION_DETAIL_PAGE.getValue());
    }

    @Test(expected = Exception.class)
    public void testEditException() throws SystemException {
        permission.setActionId(1L);
        permission.setResourceId(1L);
        permissionManager.setPermission(permission);

        doThrow(SystemException.class).when(actionRESTServiceAccess).getActionById(permission.getActionId());
        permissionManager.edit(permission);

        doThrow(SystemException.class).when(resourceRESTServiceAccess).getResourceById(permission.getResourceId());
        permissionManager.edit(permission);
    }

    @Test
    public void testGetAndSetPermission(){
        permissionManager.setPermission(permission);
        assertEquals(permission, permissionManager.getPermission());
    }

    @Test
    public void testGetSelectedAction(){
        assertEquals(selectedAction, permissionManager.getSelectedAction());
    }

    @Test
    public void testGetSelectedResource(){
        assertEquals(selectedResource, permissionManager.getSelectedResource());
    }

}