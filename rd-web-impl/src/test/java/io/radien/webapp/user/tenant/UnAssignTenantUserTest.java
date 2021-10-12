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
package io.radien.webapp.user.tenant;

import io.radien.api.model.tenant.SystemActiveTenant;
import io.radien.api.model.user.SystemUser;

import io.radien.api.service.tenantrole.TenantRoleUserRESTServiceAccess;
import io.radien.exception.SystemException;

import io.radien.ms.tenantmanagement.client.entities.ActiveTenant;
import io.radien.ms.usermanagement.client.entities.User;

import io.radien.webapp.DataModelEnum;
import io.radien.webapp.JSFUtil;
import io.radien.webapp.JSFUtilAndFaceContextMessagesTest;
import io.radien.webapp.activeTenant.ActiveTenantDataModelManager;

import io.radien.webapp.user.UserDataModel;

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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

/**
 * Class that aggregates UnitTest cases for UnAssignTenantUser
 *
 * @author Rajesh Gavvala
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({JSFUtil.class, FacesContext.class, ExternalContext.class})
public class UnAssignTenantUserTest extends JSFUtilAndFaceContextMessagesTest {

    @InjectMocks
    private UnAssignTenantUser unAssignTenantUser;

    @Mock
    private UserDataModel userDataModel;

    @Mock
    private TenantRoleUserRESTServiceAccess tenantRoleUserRESTServiceAccess;

    @Mock
    private ActiveTenantDataModelManager activeTenantDataModelManager;

    SystemUser systemUser;
    SystemActiveTenant systemActiveTenant;

    /**
     * Prepares require objects when requires to invoke
     */
    @Before
    public void before() {
        MockitoAnnotations.initMocks( this );

        handleJSFUtilAndFaceContextMessages();

        systemUser = new User();
        systemUser.setId(1L);
        doReturn(systemUser).when(userDataModel).getSelectedUser();


        systemActiveTenant = new ActiveTenant();
        systemActiveTenant.setId(1L);
        systemActiveTenant.setUserId(1L);
        doReturn(systemActiveTenant).when(activeTenantDataModelManager).getActiveTenant();
    }


    /**
     * Test method unAssignSelectedTenantUser()
     * Asserts expected to throw exception
     * @throws SystemException if any error
     */
    @Test(expected = Exception.class)
    public void testUnAssignSelectedTenantUserException() throws SystemException {
        doThrow(SystemException.class).when(tenantRoleUserRESTServiceAccess).unAssignUser(anyLong(), any(), anyLong());
        unAssignTenantUser.unAssignSelectedTenantUser();
    }

}