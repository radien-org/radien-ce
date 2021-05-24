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
 */
package io.radien.webapp.user;

import io.radien.api.model.user.SystemUser;
import io.radien.api.service.user.UserRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.webapp.authz.WebAuthorizationChecker;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.lang.reflect.Method;
import java.util.*;

public class UserDataModelTest {

    @InjectMocks
    private UserDataModel userDataModel;

    @Mock
    private WebAuthorizationChecker webAuthorizationChecker;

    @Mock
    private UserRESTServiceAccess userRESTServiceAccess;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        ExternalContext externalContext = Mockito.mock(ExternalContext.class);
        FacesContext facesContext = Mockito.mock(FacesContext.class);
        Mockito.when(facesContext.getExternalContext()).thenReturn(externalContext);
        try {
            Method setter = FacesContext.class.getDeclaredMethod("setCurrentInstance",
                    new Class[] { FacesContext.class });
            setter.setAccessible(true);
            setter.invoke(null, new Object[] { facesContext });
        } catch (Exception e) {
            logger.error("Error setting mocked FacesContext instance", e);
        }
    }

    /**
     * Test the method isTenantAssociationProcessAllowed()
     * Check if the tenant association process is allowed to be started from
     * the following perspective:
     * <ul>
     *     <li>Current logged user has the right roles (the one who will start/trigger the process)</li>
     *     <li>There is a user available to be associated with a Tenant (i.e a newly created user
     *     or a previously selected one from the data grid)</li>
     * </ul>
     *
     * @return true if the process can be handled/started, false otherwise
     * Success Case
     */
    @Test
    public void testIsTenantAssociationProcessAllowedSuccessCase() throws SystemException {
        SystemUser user = new User(); user.setId(1L); user.setLogon("a.bdd");
        SystemUser selectedUser = new User(); selectedUser.setId(3L); selectedUser.setLogon("a.b");

        doReturn(Boolean.TRUE).when(webAuthorizationChecker).hasTenantAdministratorRoleAccess();
        doReturn(Optional.of(user)).when(userRESTServiceAccess).getUserByLogon(any());

        // Call on load to check permission
        this.userDataModel.onload();

        // Set user
        this.userDataModel.setUser(user);

        Boolean expected = this.userDataModel.isTenantAssociationProcessAllowed();
        assertTrue(expected);

        // Set selectedUser
        this.userDataModel.setSelectedUser(selectedUser);

        expected = this.userDataModel.isTenantAssociationProcessAllowed();
        assertTrue(expected);
    }

    /**
     * Test the method isTenantAssociationProcessAllowed()
     * Failure Case - No role available
     */
    @Test
    public void testIsTenantAssociationProcessAllowedFailNoRoleAvailable() throws SystemException {
        doReturn(Boolean.FALSE).when(webAuthorizationChecker).hasTenantAdministratorRoleAccess();

        // Call on load to check permission
        this.userDataModel.onload();

        Boolean expected = this.userDataModel.isTenantAssociationProcessAllowed();
        assertFalse(expected);
    }

    /**
     * Test the method isTenantAssociationProcessAllowed()
     * Failure Case - No user available
     */
    @Test
    public void testIsTenantAssociationProcessAllowedFailNoUserAvailable() throws SystemException {
        doReturn(Boolean.TRUE).when(webAuthorizationChecker).hasTenantAdministratorRoleAccess();

        // Call on load to check permission
        this.userDataModel.onload();

        Boolean expected = this.userDataModel.isTenantAssociationProcessAllowed();
        assertFalse(expected);
    }

    /**
     * Test the method isTenantAssociationProcessAllowed()
     * Failure Case - No found user available
     */
    @Test
    public void testIsTenantAssociationProcessAllowedFailNoFoundUserAvailable() throws SystemException {
        SystemUser user = new User(); user.setId(1L); user.setLogon("a.bdd");

        doReturn(Boolean.TRUE).when(webAuthorizationChecker).hasTenantAdministratorRoleAccess();
        doReturn(Optional.empty()).when(userRESTServiceAccess).getUserByLogon(user.getLogon());

        // Call on load to check permission
        this.userDataModel.onload();

        // Set user
        this.userDataModel.setUser(user);

        Boolean expected = this.userDataModel.isTenantAssociationProcessAllowed();
        assertFalse(expected);
    }

    /**
     * Test the method isTenantAssociationProcessAllowed()
     * Failure Case - No found user available (Raise exception during user retrieval process)
     */
    @Test
    public void testIsTenantAssociationProcessAllowedFailException() throws SystemException {
        SystemUser user = new User(); user.setId(1L); user.setLogon("a.bdd");

        doReturn(Boolean.TRUE).when(webAuthorizationChecker).hasTenantAdministratorRoleAccess();
        doThrow(new SystemException("test")).when(userRESTServiceAccess).getUserByLogon(user.getLogon());

        // Call on load to check permission
        this.userDataModel.onload();

        // Set user
        this.userDataModel.setUser(user);

        Boolean expected = this.userDataModel.isTenantAssociationProcessAllowed();
        assertFalse(expected);
    }

    /**
     * Test getter and setter methods regarding hasTenantAdministratorRoles
     */
    @Test
    public void testGetterSetterForHasTenantAdministratorRoles() {
        this.userDataModel.setHasTenantAdministratorRoleAccess(true);
        assertTrue(this.userDataModel.isHasTenantAdministratorRoleAccess());
    }


    /**
     * Test getter and setter methods regarding userForTenantAssociation
     */
    @Test
    public void testGetterSetterForUserForTenantAssociation() {
        SystemUser user = new User(); user.setId(8L);
        this.userDataModel.setUserForTenantAssociation(user);
        assertEquals(this.userDataModel.getUserForTenantAssociation(), user);
    }
}
