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
package io.radien.webapp.user;

import io.radien.api.OAFAccess;
import io.radien.api.model.permission.SystemAction;
import io.radien.api.model.permission.SystemPermission;
import io.radien.api.model.permission.SystemResource;
import io.radien.api.model.user.SystemUser;
import io.radien.api.security.UserSessionEnabled;
import io.radien.api.service.permission.ActionRESTServiceAccess;
import io.radien.api.service.permission.PermissionRESTServiceAccess;
import io.radien.api.service.permission.ResourceRESTServiceAccess;
import io.radien.api.service.permission.SystemActionsEnum;
import io.radien.api.service.permission.SystemPermissionsEnum;
import io.radien.api.service.permission.SystemResourcesEnum;
import io.radien.api.service.tenantrole.TenantRoleUserRESTServiceAccess;
import io.radien.api.service.user.UserRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.permissionmanagement.client.entities.Action;
import io.radien.ms.permissionmanagement.client.entities.Permission;
import io.radien.ms.permissionmanagement.client.entities.Resource;
import io.radien.ms.tenantmanagement.client.entities.ActiveTenant;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.webapp.DataModelEnum;
import io.radien.webapp.JSFUtil;
import io.radien.webapp.activeTenant.ActiveTenantDataModelManager;
import io.radien.webapp.authz.WebAuthorizationChecker;
import io.radien.webapp.tenantrole.LazyTenantingUserDataModel;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.radien.webapp.DataModelEnum.ACTION_NOT_FOUND_MESSAGE;
import static io.radien.webapp.DataModelEnum.PERMISSION_NOT_FOUND_FOR_ACTION_RESOURCE_MESSAGE;
import static io.radien.webapp.DataModelEnum.RESOURCE_NOT_FOUND_MESSAGE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.nullable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserDataModelTest {

    @InjectMocks
    private UserDataModel userDataModel;

    @Mock
    private WebAuthorizationChecker webAuthorizationChecker;

    @Mock
    private UserRESTServiceAccess userRESTServiceAccess;


    @Mock
    private ActionRESTServiceAccess actionRESTServiceAccess;


    @Mock
    private ResourceRESTServiceAccess resourceRESTServiceAccess;


    @Mock
    private PermissionRESTServiceAccess permissionRESTServiceAccess;

    @Mock
    private ActiveTenantDataModelManager activeTenantDataModelManager;

    @Mock
    private UserSessionEnabled userSessionEnabled;

    @Mock
    private OAFAccess oafAccess;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    FacesContext facesContext;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        ExternalContext externalContext = Mockito.mock(ExternalContext.class);
        facesContext = Mockito.mock(FacesContext.class);
        Flash flash = Mockito.mock(Flash.class);
        Mockito.when(facesContext.getExternalContext()).thenReturn(externalContext);
        Mockito.when(externalContext.getFlash()).thenReturn(flash);
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
        when(activeTenantDataModelManager.isTenantActive()).thenReturn(true);
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
     * Test getter and setter methods regarding allowedToResetPassword
     */
    @Test
    public void testGetterSetterAllowedToResetPassword() {
        this.userDataModel.setAllowedToResetPassword(true);
        assertTrue(this.userDataModel.isAllowedToResetPassword());
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

    /**
     * Test for method {@link UserDataModel#findUserToAssociate()}
     * Gets the user selected from the DataGrid
     * @throws SystemException in case of any error with UserRestService client
     */
    @Test
    public void testFindUserToAssociateFromDataGrid() throws SystemException {
        User selectedUser = new User(); selectedUser.setLogon("a.b"); selectedUser.setId(1L);
        userDataModel.setSelectedUser(selectedUser);
        assertEquals(selectedUser, userDataModel.getSelectedUser());
        assertEquals(selectedUser, userDataModel.findUserToAssociate());
        userDataModel.findUserToAssociate();
    }

    /**
     * Test for method {@link UserDataModel#findUserToAssociate()}
     * Gets the user newly created
     * @throws SystemException in case of any error with UserRestService client
     */
    @Test
    public void testFindUserToAssociateUserNewlyCreated() throws SystemException {
        User user = new User(); user.setLogon("a.b");
        userDataModel.setUser(user);
        assertEquals(user, userDataModel.getUser());
        when(userRESTServiceAccess.getUserByLogon(user.getLogon())).thenReturn(Optional.of(user));
        assertEquals(user, userDataModel.findUserToAssociate());
        userDataModel.findUserToAssociate();
    }

    /**
     * Test for method {@link UserDataModel#findUserToAssociate()}
     * Situation where newly user could not be found
     * @throws SystemException in case of any error with UserRestService client
     */
    @Test
    public void testFindUserToAssociateUserWhenNewlyUserCouldNotBeFound() throws SystemException {
        User user = new User(); user.setLogon("a.b");
        userDataModel.setUser(user);
        when(userRESTServiceAccess.getUserByLogon(user.getLogon())).thenReturn(Optional.empty());
        assertNull(userDataModel.findUserToAssociate());
    }

    /**
     * Test for method {@link UserDataModel#findUserToAssociate()}
     * Situation where newly user could not be found
     * @throws SystemException in case of any error with UserRestService client
     */
    @Test
    public void testFindUserToAssociateUserWhenNoUsersAvailable() throws SystemException {
        userDataModel.setSelectedUser(null);
        userDataModel.setUser(null);
        assertNull(userDataModel.findUserToAssociate());

        userDataModel.setSelectedUser(new User());
        userDataModel.setUser(new User());
        assertNull(userDataModel.findUserToAssociate());
    }

    /**
     * Test for method {@link UserDataModel#prepareTenantAssociation()}
     */
    @Test
    public void testPrepareTenantAssociation() {
        assertEquals(DataModelEnum.USER_ASSIGNING_TENANT_ASSOCIATION_PATH.getValue(), userDataModel.prepareTenantAssociation());
    }

    /**
     * Test for method {@link UserDataModel#getHasUserAdministratorRoleAccess()}
     */
    @Test
    public void testGetHasUserAdministratorRoleAccess() {
        userDataModel.setHasUserAdministratorRoleAccess(true);
        assertTrue(userDataModel.getHasUserAdministratorRoleAccess());
    }

    /**
     * Test for method {@link UserDataModel#getLazyUserDataModel()} ()}
     */
    @Test
    public void testGetLazyUserDataModel() {
        userDataModel.setService(mock(UserRESTServiceAccess.class));
        TenantRoleUserRESTServiceAccess tenantRoleUserRESTServiceAccess = mock(TenantRoleUserRESTServiceAccess.class);
        LazyDataModel<? extends SystemUser> lazyUserDataModel =
                new LazyTenantingUserDataModel(tenantRoleUserRESTServiceAccess, userDataModel.getService());
        userDataModel.setLazyUserDataModel(lazyUserDataModel);
        assertEquals(lazyUserDataModel, userDataModel.getLazyUserDataModel());
    }

    /**
     * Test for method {@link UserDataModel#init()}
     */
    @Test
    public void testInit() {
        when(webAuthorizationChecker.hasUserAdministratorRoleAccess()).thenReturn(Boolean.TRUE);
        when(webAuthorizationChecker.hasTenantAdministratorRoleAccess()).thenReturn(Boolean.TRUE);

        ActiveTenant activeTenant = new ActiveTenant(); activeTenant.setTenantId(1L);
        when(activeTenantDataModelManager.getActiveTenant()).thenReturn(activeTenant);
        when(activeTenantDataModelManager.isTenantActive()).thenReturn(Boolean.TRUE);

        userDataModel.init();

        assertEquals(activeTenant.getTenantId(), ((LazyTenantingUserDataModel) userDataModel.
                getLazyUserDataModel()).getTenantId());
    }

    /**
     * Test for method {@link UserDataModel#init()} when exception occurs
     */
    @Test
    public void testInitWhenExceptionOccurs() {
        when(activeTenantDataModelManager.isTenantActive()).thenReturn(Boolean.TRUE);
        when(webAuthorizationChecker.hasUserAdministratorRoleAccess()).thenThrow(new RuntimeException("error"));
        userDataModel.init();

        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_ERROR, captured.getSeverity());
        assertEquals(DataModelEnum.GENERIC_ERROR_MESSAGE.getValue(), captured.getSummary());
    }

    /**
     * Test for method {@link UserDataModel#init()} when no active tenant is available
     */
    @Test
    public void testInitWhenNoActiveTenant() {
        when(activeTenantDataModelManager.getActiveTenant()).thenReturn(null);
        assertNull(userDataModel.getLazyUserDataModel());
    }

    /**
     * Test for method {@link UserDataModel#userProfile()}
     */
    @Test
    public void testUserProfile() {
        userDataModel.setSelectedUser(new User());
        assertEquals(DataModelEnum.USERS_PROFILE_PATH.getValue(), userDataModel.userProfile());
        userDataModel.setSelectedUser(null);
        assertEquals(DataModelEnum.USERS_PATH.getValue(), userDataModel.userProfile());
    }

    /**
     * Test for method {@link UserDataModel#returnToDataTableRecords()} ()}
     */
    @Test
    public void testReturnHome() {
        assertEquals(DataModelEnum.USERS_PATH.getValue(), userDataModel.returnToDataTableRecords());
        assertNull(userDataModel.getSelectedUser());
        assertNotNull(userDataModel.getUser());
    }

    /**
     * Test for method {@link UserDataModel#createRecord()} ()}
     */
    @Test
    public void testCreateRecord() {
        assertEquals(DataModelEnum.USER_PATH.getValue(), userDataModel.createRecord());
        assertNotNull(userDataModel.getUser());
        assertTrue(userDataModel.getUser().isEnabled());
    }

   /**
     * Test for method {@link UserDataModel#editRecord()}
     */
    @Test
    public void testEditRecord() {
        userDataModel.setSelectedUser(new User());
        assertEquals(DataModelEnum.USER_PATH.getValue(), userDataModel.editRecord());
        userDataModel.setSelectedUser(null);
        assertEquals(DataModelEnum.USERS_PATH.getValue(), userDataModel.editRecord());
    }

    /**
     * Test for method {@link UserDataModel#deleteRecord()}
     */
    @Test
    public void testDeleteRecord(){
        userDataModel.setSelectedUser(new User());
        assertEquals(DataModelEnum.USER_DELETE_PATH.getValue(), userDataModel.deleteRecord());
        userDataModel.setSelectedUser(null);
        assertEquals(DataModelEnum.USERS_PATH.getValue(), userDataModel.deleteRecord());
    }

    /**
     * Test for method {@link UserDataModel#onRowSelect(SelectEvent)}
     */
    @Test
    public void testOnRowSelect() {
        Long userId = 1L;
        User user = new User(); user.setId(userId);
        SelectEvent<SystemUser> event = mock(SelectEvent.class);
        when(event.getObject()).thenReturn(user);
        LazyTenantingUserDataModel LazyTenantingUserDataModel = mock(LazyTenantingUserDataModel.class);
        userDataModel.setLazyUserDataModel(LazyTenantingUserDataModel);

        userDataModel.onRowSelect(event);

        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_INFO, captured.getSeverity());
        assertEquals(DataModelEnum.USERS_SELECTED_MESSAGE.getValue(), captured.getSummary());
    }

    /**
     * Test for method {@link UserDataModel#sendUpdatePasswordEmail()}
     */
    @Test
    public void testSendUpdatePasswordEmail() {
        User user = new User(); user.setId(1L);
        userDataModel.setSelectedUser(user);
        when(userRESTServiceAccess.sendUpdatePasswordEmail(user.getId())).thenReturn(true);
        userDataModel.sendUpdatePasswordEmail();

        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_INFO, captured.getSeverity());
        assertEquals(DataModelEnum.USER_SEND_UPDATE_PASSWORD_EMAIL_SUCCESS.getValue(), captured.getSummary());
    }

    /**
     * Test for method {@link UserDataModel#getResetPasswordMessage()}
     */
    @Test
    public void testGetResetPasswordMessage() {
        User user = new User(); user.setLogon("login.test");
        userDataModel.setSelectedUser(user);
        String expectedMessage = MessageFormat.format(JSFUtil.getMessage(
                DataModelEnum.USER_RESET_PASSWORD_CONFIRM_MESSAGE.getValue()), user.getLogon());
        assertEquals(expectedMessage, userDataModel.getResetPasswordMessage());
        userDataModel.setSelectedUser(null);
        expectedMessage = MessageFormat.format(JSFUtil.getMessage(
                DataModelEnum.USER_RESET_PASSWORD_CONFIRM_MESSAGE.getValue()), "");
        assertEquals(expectedMessage, userDataModel.getResetPasswordMessage());
    }

    /**
     * Test for method {@link UserDataModel#sendUpdatePasswordEmail()}
     */
    @Test
    public void testSendUpdatePasswordEmailWhenExceptionOccurs() {
        User user = new User(); user.setId(1L);
        userDataModel.setSelectedUser(user);
        when(userRESTServiceAccess.sendUpdatePasswordEmail(user.getId())).thenThrow(new RuntimeException("error"));
        userDataModel.sendUpdatePasswordEmail();

        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_ERROR, captured.getSeverity());
        assertEquals(DataModelEnum.USER_SEND_UPDATE_PASSWORD_EMAIL_ERROR.getValue(), captured.getSummary());
    }

    /**
     * Test for method {@link UserDataModel#deleteUser()}
     */
    @Test
    public void testDeleteUser() {
        User user = new User(); user.setId(1L);
        userDataModel.setSelectedUser(user);
        when(userRESTServiceAccess.deleteUser(user.getId())).thenReturn(true);
        userDataModel.deleteUser();

        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_INFO, captured.getSeverity());
        assertEquals(DataModelEnum.DELETE_SUCCESS.getValue(), captured.getSummary());
    }

    /**
     * Test for method {@link UserDataModel#sendUpdatePasswordEmail()}
     */
    @Test
    public void testDeleteUserWhenExceptionOccurs() {
        User user = new User(); user.setId(1L);
        userDataModel.setSelectedUser(user);
        when(userRESTServiceAccess.deleteUser(user.getId())).thenThrow(new RuntimeException("error"));
        userDataModel.deleteUser();

        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_ERROR, captured.getSeverity());
        assertEquals(DataModelEnum.DELETE_ERROR.getValue(), captured.getSummary());
    }


    /**
     * Test for method {@link UserDataModel#deleteUser()}
     * corresponding to the case of user creation
     */
    @Test
    public void testSaveUser() {
        Long currentLoggedUserId = 1111111L;
        when(userSessionEnabled.getUserId()).thenReturn(currentLoggedUserId);

        User user = new User();
        userDataModel.setSelectedUser(user);
        when(userRESTServiceAccess.updateUser(user)).thenReturn(true);
        userDataModel.updateUser(user);
        assertEquals(currentLoggedUserId, user.getCreateUser());

        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_INFO, captured.getSeverity());
        assertEquals(DataModelEnum.SAVE_SUCCESS_MESSAGE.getValue(), captured.getSummary());
    }

    /**
     * Test for method {@link UserDataModel#deleteUser()}
     * corresponding to the case of user edit/update
     */
    @Test
    public void testEditUpdateUser() {
        Long currentLoggedUserId = 1111111L;
        when(userSessionEnabled.getUserId()).thenReturn(currentLoggedUserId);

        User user = new User(); user.setId(1L);
        userDataModel.setSelectedUser(user);
        when(userRESTServiceAccess.updateUser(user)).thenReturn(true);
        userDataModel.updateUser(user);
        assertEquals(currentLoggedUserId, user.getLastUpdateUser());

        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_INFO, captured.getSeverity());
        assertEquals(DataModelEnum.EDIT_SUCCESS_MESSAGE.getValue(), captured.getSummary());
    }

    /**
     * Test for method {@link UserDataModel#sendUpdatePasswordEmail()}
     * when exception occurs for insert operation
     */
    @Test
    public void testSaveUserWhenExceptionOccurs() {
        Long currentLoggedUserId = 1111111L;
        User user = new User();
        when(userSessionEnabled.getUserId()).thenReturn(currentLoggedUserId);
        when(userRESTServiceAccess.updateUser(user)).thenThrow(new RuntimeException("error"));
        userDataModel.updateUser(user);

        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_ERROR, captured.getSeverity());
        assertEquals(DataModelEnum.SAVE_ERROR_MESSAGE.getValue(), captured.getSummary());
    }

    /**
     * Test for method {@link UserDataModel#sendUpdatePasswordEmail()}
     * when exception occurs for edit/update operation
     */
    @Test
    public void testEditUserWhenExceptionOccurs() {
        Long currentLoggedUserId = 1111111L;
        User user = new User(); user.setId(111L);
        when(userSessionEnabled.getUserId()).thenReturn(currentLoggedUserId);
        when(userRESTServiceAccess.updateUser(user)).thenThrow(new RuntimeException("error"));
        userDataModel.updateUser(user);

        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_ERROR, captured.getSeverity());
        assertEquals(DataModelEnum.EDIT_ERROR_MESSAGE.getValue(), captured.getSummary());
    }

    @Test
    public void testUserRoles(){
        SystemUser user = new User(); user.setId(2L);
        userDataModel.setSelectedUser(user);
        assertEquals(DataModelEnum.USERS_ROLES_PATH.getValue(), userDataModel.userRoles());

        userDataModel.setSelectedUser(null);
        assertEquals(DataModelEnum.USERS_PATH.getValue(), userDataModel.userRoles());

    }

    @Test
    public void testUserUnAssign(){
        SystemUser user = new User(); user.setId(2L);
        userDataModel.setSelectedUser(user);
        assertEquals(DataModelEnum.USER_UN_ASSIGN_PATH.getValue(), userDataModel.userUnAssign());

        userDataModel.setSelectedUser(null);
        assertEquals(DataModelEnum.USERS_PATH.getValue(), userDataModel.userUnAssign());

    }

    /**
     * Test for method {@link UserDataModel#getPermissionToResetPassword()} when Resource could not be found
     * @throws SystemException thrown when errors occurs during Rest client executions
     */
    @Test
    public void testGetPermissionToResetPasswordWhenResourceNotFound() throws SystemException {
        when(resourceRESTServiceAccess.getResourceByName(SystemResourcesEnum.THIRD_PARTY_PASSWORD.getResourceName())).
                thenReturn(Optional.empty());
        String expectedMsgError = "Message(s): " + MessageFormat.format(JSFUtil.getMessage(
                RESOURCE_NOT_FOUND_MESSAGE.getValue()), SystemResourcesEnum.THIRD_PARTY_PASSWORD.getResourceName());
        SystemException outcomeException = assertThrows(SystemException.class, () -> userDataModel.getPermissionToResetPassword());
        assertEquals(expectedMsgError, outcomeException.getMessage());
    }

    /**
     * Test for method {@link UserDataModel#getPermissionToResetPassword()} when Action could not be found
     * @throws SystemException thrown when errors occurs during Rest client executions
     */
    @Test
    public void testGetPermissionToResetPasswordWhenActionNotFound() throws SystemException {
        when(resourceRESTServiceAccess.getResourceByName(any())).thenReturn(Optional.of(new Resource()));
        when(actionRESTServiceAccess.getActionByName(SystemActionsEnum.ACTION_UPDATE.getActionName())).
                thenReturn(Optional.empty());
        String expectedMsgError = "Message(s): " + MessageFormat.format(JSFUtil.getMessage(
                ACTION_NOT_FOUND_MESSAGE.getValue()), SystemActionsEnum.ACTION_UPDATE.getActionName());
        SystemException outcomeException = assertThrows(SystemException.class, () -> userDataModel.getPermissionToResetPassword());
        assertEquals(expectedMsgError, outcomeException.getMessage());
    }

    /**
     * Test for method {@link UserDataModel#getPermissionToResetPassword()} when the Permission in self
     * could not be found
     * @throws SystemException thrown when errors occurs during Rest client executions
     */
    @Test
    public void testGetPermissionToResetPasswordWhenPermissionNotFound() throws SystemException {
        SystemResource resource = new Resource(); resource.setId(1L);
        when(resourceRESTServiceAccess.getResourceByName(any())).thenReturn(Optional.of(resource));
        SystemAction action = new Action(); action.setId(2L);
        when(actionRESTServiceAccess.getActionByName(any())).thenReturn(Optional.of(action));
        String expectedMsgError = "Message(s): " + MessageFormat.format(JSFUtil.getMessage(
                PERMISSION_NOT_FOUND_FOR_ACTION_RESOURCE_MESSAGE.getValue()),
                SystemActionsEnum.ACTION_UPDATE.getActionName(),
                SystemResourcesEnum.THIRD_PARTY_PASSWORD.getResourceName());
        SystemException outcomeException = assertThrows(SystemException.class, () -> userDataModel.getPermissionToResetPassword());
        assertEquals(expectedMsgError, outcomeException.getMessage());
    }

    /**
     * Test for method {@link UserDataModel#getPermissionToResetPassword()}
     * @throws SystemException thrown when errors occurs during Rest client executions
     */
    @Test
    public void testGetPermissionToResetPassword() throws SystemException {
        SystemAction action = new Action();
        action.setName(SystemActionsEnum.ACTION_UPDATE.getActionName());
        action.setId(1L);

        SystemResource resource = new Resource();
        resource.setName(SystemResourcesEnum.THIRD_PARTY_PASSWORD.getResourceName());
        resource.setId(1L);

        SystemPermission permission = new Permission();
        permission.setName(SystemPermissionsEnum.THIRD_PARTY_PASSWORD_MANAGEMENT_UPDATE.getPermissionName());
        permission.setId(11111L);
        permission.setActionId(action.getId());
        permission.setResourceId(resource.getId());

        when(actionRESTServiceAccess.getActionByName(SystemActionsEnum.ACTION_UPDATE.getActionName())).
                thenReturn(Optional.of(action));
        when(resourceRESTServiceAccess.getResourceByName(SystemResourcesEnum.THIRD_PARTY_PASSWORD.getResourceName())).
                thenReturn(Optional.of(resource));

        List<SystemPermission> returnList = Collections.singletonList(permission);

        doReturn(returnList).when(permissionRESTServiceAccess).getPermissionByActionAndResource(action.getId(),
                resource.getId());

        assertEquals(permission, userDataModel.getPermissionToResetPassword());
    }

    /**
     * Test to validate if the current user has permission to reset password
     * @throws SystemException in case of any issue while validating or obtaining permission
     */
    @Test
    public void testHasPermissionToResetPassword() throws SystemException {
        Long tenantId = 1L;

        SystemAction action = new Action();
        action.setName(SystemActionsEnum.ACTION_UPDATE.getActionName());
        action.setId(1L);

        SystemResource resource = new Resource();
        resource.setName(SystemResourcesEnum.THIRD_PARTY_PASSWORD.getResourceName());
        resource.setId(1L);

        SystemPermission permission = new Permission();
        permission.setName(SystemPermissionsEnum.THIRD_PARTY_PASSWORD_MANAGEMENT_UPDATE.getPermissionName());
        permission.setId(11111L);
        permission.setActionId(action.getId());
        permission.setResourceId(resource.getId());

        when(actionRESTServiceAccess.getActionByName(
                SystemActionsEnum.ACTION_UPDATE.getActionName())).thenReturn(Optional.of(action));
        when(resourceRESTServiceAccess.getResourceByName(SystemResourcesEnum.THIRD_PARTY_PASSWORD.getResourceName())).
                thenReturn(Optional.of(resource));

        List<SystemPermission> returnList = Collections.singletonList(permission);

        doReturn(returnList).when(permissionRESTServiceAccess).getPermissionByActionAndResource(action.getId(),
                resource.getId());

        when(webAuthorizationChecker.hasGrant(permission.getId(), tenantId)).thenReturn(Boolean.TRUE);

        assertTrue(userDataModel.hasPermissionToResetPassword(tenantId));
    }
}
