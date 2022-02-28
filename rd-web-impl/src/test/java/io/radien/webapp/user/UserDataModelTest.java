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
package io.radien.webapp.user;

import io.radien.api.model.tenant.SystemActiveTenant;
import io.radien.api.model.tenantrole.SystemTenantRoleUser;
import io.radien.api.model.user.SystemUser;
import io.radien.api.security.UserSessionEnabled;
import io.radien.api.service.permission.SystemActionsEnum;
import io.radien.api.service.permission.SystemResourcesEnum;
import io.radien.api.service.tenant.ActiveTenantRESTServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleUserRESTServiceAccess;
import io.radien.api.service.user.UserRESTServiceAccess;

import io.radien.exception.SystemException;
import io.radien.ms.rolemanagement.client.entities.TenantRoleUser;
import io.radien.ms.tenantmanagement.client.entities.ActiveTenant;
import io.radien.ms.usermanagement.client.entities.User;

import io.radien.webapp.DataModelEnum;
import io.radien.webapp.JSFUtil;
import io.radien.webapp.JSFUtilAndFaceContextMessagesTest;
import io.radien.webapp.activeTenant.ActiveTenantDataModelManager;
import io.radien.webapp.authz.WebAuthorizationChecker;
import io.radien.webapp.tenantrole.LazyTenantingUserDataModel;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.faces.application.FacesMessage;

import javax.faces.context.FacesContext;

import org.junit.AfterClass;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;


import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;


import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.nullable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
/**
 * Class that aggregates UnitTest cases for UserDataModel
 */
public class UserDataModelTest extends JSFUtilAndFaceContextMessagesTest {

    @InjectMocks
    private UserDataModel userDataModel;

    @Mock
    private WebAuthorizationChecker webAuthorizationChecker;

    @Mock
    private UserRESTServiceAccess userRESTServiceAccess;

    @Mock
    private ActiveTenantDataModelManager activeTenantDataModelManager;
    
    @Mock
    private ActiveTenantRESTServiceAccess activeTenantRESTServiceAccess;
    
    @Mock
    private TenantRoleUserRESTServiceAccess tenantRoleUserRESTServiceAccess;

    @Mock
    private UserSessionEnabled userSessionEnabled;

    static FacesContext facesContext;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @BeforeClass
    public static void beforeClass(){
        facesContext = getFacesContext();
    }
    @AfterClass
    public static final void afterClass(){
        destroy();

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
     * Success Case
     */
    @Test
    public void testIsTenantAssociationProcessAllowedSuccessCase() throws SystemException {

        when(activeTenantDataModelManager.isTenantActive()).thenReturn(true);
        SystemUser user = new User(); user.setId(1L); user.setLogon("a.bdd");
        SystemUser selectedUser = new User(); selectedUser.setId(3L); selectedUser.setLogon("a.b");

        doReturn(Boolean.TRUE).when(webAuthorizationChecker).hasPermissionAccess(anyString(), anyString(), anyLong());
        doReturn(Optional.of(user)).when(userRESTServiceAccess).getUserByLogon(any());

        // Call on load to check permission
        this.userDataModel.onload();

        // Set user
        this.userDataModel.setUser(user);

        boolean expected = this.userDataModel.isTenantAssociationProcessAllowed();
        assertFalse(expected);

        // Set selectedUser
        this.userDataModel.setSelectedUser(selectedUser);

        expected = this.userDataModel.isTenantAssociationProcessAllowed();
        assertFalse(expected);
    }

    /**
     * Test the method isTenantAssociationProcessAllowed()
     * Failure Case - No role available
     */
    @Test
    public void testIsTenantAssociationProcessAllowedFailNoRoleAvailable() {
        doReturn(Boolean.FALSE).when(webAuthorizationChecker).hasPermissionAccess(SystemResourcesEnum.TENANT_ROLE_USER.getResourceName(),
                SystemActionsEnum.ACTION_CREATE.getActionName(), null);

        // Call on load to check permission
        this.userDataModel.onload();

        boolean expected = this.userDataModel.isTenantAssociationProcessAllowed();
        assertFalse(expected);
    }

    /**
     * Test the method isTenantAssociationProcessAllowed()
     * Failure Case - No user available
     */
    @Test
    public void testIsTenantAssociationProcessAllowedFailNoUserAvailable() {

       userDataModel.setAllowedAssociateTenantAndUser(true);

        boolean expected = this.userDataModel.isTenantAssociationProcessAllowed();
        assertFalse(expected);
    }

    /**
     * Test the method isTenantAssociationProcessAllowed()
     * Success Case - User found available
     */
    @Test
    public void testIsTenantAssociationProcessAllowedFoundUserAvailable() throws SystemException {
        SystemUser user = new User(); user.setId(1L); user.setLogon("a.bdd");
        Optional<SystemUser> optionalSystemUser = Optional.of(user);
        userDataModel.setAllowedAssociateTenantAndUser(true);

        doReturn(Boolean.TRUE).when(webAuthorizationChecker).hasPermissionAccess(
                SystemResourcesEnum.TENANT_ROLE_USER.getResourceName(),
                SystemActionsEnum.ACTION_CREATE.getActionName(), null);
        doReturn(optionalSystemUser).when(userRESTServiceAccess).getUserByLogon(user.getLogon());

        // Call on load to check permission
        this.userDataModel.onload();

        // Set user
        this.userDataModel.setUser(user);

        boolean expected = this.userDataModel.isTenantAssociationProcessAllowed();
        assertTrue(expected);
    }

    /**
     * Test the method isTenantAssociationProcessAllowed()
     * Failure Case - No found user available (Raise exception during user retrieval process)
     */
    @Test
    public void testIsTenantAssociationProcessAllowedFailException() throws SystemException {
        SystemUser user = new User(); user.setId(1L); user.setLogon("a.bdd");
        userDataModel.setAllowedAssociateTenantAndUser(true);

        doReturn(Boolean.FALSE).when(webAuthorizationChecker).hasPermissionAccess(
                SystemResourcesEnum.TENANT_ROLE_USER.getResourceName(),
                SystemActionsEnum.ACTION_CREATE.getActionName(), null);
        doThrow(new SystemException("test")).when(userRESTServiceAccess).getUserByLogon(user.getLogon());

        // Call on load to check permission
        this.userDataModel.onload();

        // Set user
        this.userDataModel.setUser(user);

        assertFalse(userDataModel.isTenantAssociationProcessAllowed());
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
        this.userDataModel.setAllowedAssociateTenantAndUser(true);
        assertTrue(this.userDataModel.isAllowedAssociateTenantAndUser());
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
     * Test for method {@link UserDataModel#isAllowedCreateUser()}
     */
    @Test
    public void testAllowedCreate() {
        userDataModel.setAllowedCreateUser(true);
        assertTrue(userDataModel.isAllowedCreateUser());
    }

    /**
     * Test for method {@link UserDataModel#isAllowedReadUser()}
     */
    @Test
    public void testAllowedRead() {
        userDataModel.setAllowedReadUser(true);
        assertTrue(userDataModel.isAllowedReadUser());
    }
    /**
     * Test for method {@link UserDataModel#isAllowedUpdateUser()}
     */
    @Test
    public void testAllowedUpdate() {
        userDataModel.setAllowedUpdateUser(true);
        assertTrue(userDataModel.isAllowedUpdateUser());
    }
    /**
     * Test for method {@link UserDataModel#isAllowedDeleteUser()}
     */
    @Test
    public void testAllowedDelete() {
        userDataModel.setAllowedDeleteUser(true);
        assertTrue(userDataModel.isAllowedDeleteUser());
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
        userDataModel.setAllowedAssociateTenantAndUser(true);
        when(webAuthorizationChecker.hasPermissionAccess(SystemResourcesEnum.USER.getResourceName(), SystemActionsEnum.ACTION_READ.getActionName(), null)).thenReturn(Boolean.TRUE);
        when(webAuthorizationChecker.hasPermissionAccess(SystemResourcesEnum.TENANT_ROLE_USER.getResourceName(),
                SystemActionsEnum.ACTION_CREATE.getActionName(), null)).thenReturn(Boolean.TRUE);
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
//    @Test
//    public void testInitExceptionOccurs() {
//        when(activeTenantDataModelManager.isTenantActive()).thenReturn(Boolean.TRUE);
//        when(webAuthorizationChecker.hasPermissionAccess(any(),any(),any())).thenThrow(new RuntimeException("error"));
//        userDataModel.init();
//
//        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
//        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());
//
//        FacesMessage captured = facesMessageCaptor.getValue();
//        assertEquals(FacesMessage.SEVERITY_ERROR, captured.getSeverity());
//        assertEquals(DataModelEnum.GENERIC_ERROR_MESSAGE.getValue(), captured.getSummary());
//    }

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
        assertNotNull(userDataModel.getSelectedUser());
    }

    /**
     * Test for method {@link UserDataModel#sendUpdatePasswordEmail()}
     */
//    @Test
//    public void testSendUpdatePasswordEmail() {
//        User user = new User();
//        user.setId( 1L );
//        userDataModel.setSelectedUser( user );
//        when( userRESTServiceAccess.sendUpdatePasswordEmail( user.getId() ) ).thenReturn( true );
//        userDataModel.sendUpdatePasswordEmail();
//
//        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass( FacesMessage.class );
//        verify( facesContext ).addMessage( nullable( String.class ), facesMessageCaptor.capture() );
//
//        FacesMessage captured = facesMessageCaptor.getValue();
//        assertEquals( FacesMessage.SEVERITY_INFO, captured.getSeverity() );
//        assertEquals( DataModelEnum.SENT_UPDATE_PASSWORD_EMAIL_SUCCESS.getValue(), captured.getSummary() );
//
//        userDataModel.setSelectedUser(null);
//        userDataModel.sendUpdatePasswordEmail();
//    }

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
//    @Test
//    public void testSendUpdatePasswordEmailWhenExceptionOccurs() {
//        User user = new User(); user.setId(1L);
//        userDataModel.setSelectedUser(user);
//        when(userRESTServiceAccess.sendUpdatePasswordEmail(user.getId())).thenThrow(new RuntimeException("error"));
//        userDataModel.sendUpdatePasswordEmail();
//
//        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
//        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());
//
//        FacesMessage captured = facesMessageCaptor.getValue();
//        assertEquals(FacesMessage.SEVERITY_ERROR, captured.getSeverity());
//        assertEquals(DataModelEnum.SENT_UPDATE_PASSWORD_EMAIL_ERROR.getValue(), captured.getSummary());
//    }

    /**
     * Test for method {@link UserDataModel#deleteUser()}
     * @throws SystemException in case of any issue regarding REST API communication
     */
    @Test
    public void testDeleteUser() throws SystemException {
        User user = new User(); user.setId(1L);
        userDataModel.setSelectedUser(user);
        when(userRESTServiceAccess.deleteUser(user.getId())).thenReturn(true);
        
        List<? extends SystemTenantRoleUser> tenantRoleUsers = getTenantRoleUsersForDeleteUser(user.getId());
        doReturn(tenantRoleUsers).when(tenantRoleUserRESTServiceAccess).getTenantRoleUsers(null, user.getId(), false);
        when(tenantRoleUserRESTServiceAccess.delete(anyLong())).thenReturn(true);

        List<? extends SystemActiveTenant> systemActiveTenants = getActiveTenantForDeleteUser(user.getId());
        doReturn(systemActiveTenants).when(activeTenantRESTServiceAccess).getActiveTenantByFilter(user.getId(), null);
        when(activeTenantRESTServiceAccess.delete(anyLong())).thenReturn(true);
        
        userDataModel.deleteUser();
        assertNotNull(userDataModel.getSelectedUser());

        userDataModel.setSelectedUser(null);
        userDataModel.deleteUser();
        assertNull(userDataModel.getSelectedUser());
    }

    private List<? extends SystemTenantRoleUser> getTenantRoleUsersForDeleteUser(Long userId) {
        List<SystemTenantRoleUser> result = new ArrayList<>();
        
        SystemTenantRoleUser stru = new TenantRoleUser();
        stru.setId(1L);
        stru.setTenantRoleId(1L);
        stru.setUserId(userId);
        
        result.add(stru);
        
        return result;
    }

    private List<? extends SystemActiveTenant> getActiveTenantForDeleteUser(Long userId) {
        List<SystemActiveTenant> result = new ArrayList<>();
        
        SystemActiveTenant sat = new ActiveTenant();
        sat.setId(1L);
        sat.setTenantId(2L);
        sat.setUserId(userId);
        
        result.add(sat);
        
        return result;
    }

    /**
     * Test for method {@link UserDataModel#deleteUser()}
     */
//    @Test
//    public void testDeleteUserWhenExceptionOccurs() {
//        User user = new User(); user.setId(1L);
//        userDataModel.setSelectedUser(user);
//        when(userRESTServiceAccess.deleteUser(user.getId())).thenThrow(new RuntimeException("error"));
//        userDataModel.deleteUser();
//
//        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
//        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());
//
//        FacesMessage captured = facesMessageCaptor.getValue();
//        assertEquals(FacesMessage.SEVERITY_ERROR, captured.getSeverity());
//        assertEquals(DataModelEnum.DELETE_ERROR.getValue(), captured.getSummary());
//    }


    /**
     * Test for method {@link UserDataModel#saveUser(SystemUser)}
     * corresponding to the case of user creation
     * @throws SystemException thrown in any case of error
     */
//    @Test
//    public void testSaveCreateUser() throws SystemException {
//        User user = new User();
//        userDataModel.setSelectedUser(user);
//        when(userRESTServiceAccess.create(user, user.isDelegatedCreation())).thenReturn(true);
//        userDataModel.saveUser(user);
//
//        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
//        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());
//        FacesMessage captured = facesMessageCaptor.getValue();
//        assertEquals(FacesMessage.SEVERITY_INFO, captured.getSeverity());
//    }

    /**
     * Test for method {@link UserDataModel#saveUser(SystemUser)}
     * corresponding to the case of user updating
     */
    @Test
    public void testSaveUpdateUser() {
        Long currentLoggedUserId = 1111111L;
        when(userSessionEnabled.getUserId()).thenReturn(currentLoggedUserId);

        User user = new User();
        userDataModel.setSelectedUser(user);
        when(userRESTServiceAccess.updateUser(user)).thenReturn(true);
        userDataModel.saveUser(user);
        assertEquals(currentLoggedUserId, user.getCreateUser());
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
        userDataModel.saveUser(user);
        assertEquals(currentLoggedUserId, user.getLastUpdateUser());

        userDataModel.saveUser(null);
    }

    /**
     * Test for method {@link UserDataModel#saveUser(SystemUser)}
     * when exception occurs for insert operation
     * @throws SystemException for any caught error during creation proccess
     */
//    @Test
//    public void testSaveCreateUserWhenExceptionOccurs() throws SystemException {
//        Long currentLoggedUserId = 1111111L;
//        User user = new User();
//        when(userSessionEnabled.getUserId()).thenReturn(currentLoggedUserId);
//        when(userRESTServiceAccess.create(user, user.isDelegatedCreation())).thenThrow(new RuntimeException("error"));
//        userDataModel.saveUser(user);
//
//        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
//        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());
//
//        FacesMessage captured = facesMessageCaptor.getValue();
//        assertEquals(FacesMessage.SEVERITY_ERROR, captured.getSeverity());
//        assertEquals(DataModelEnum.SAVE_ERROR_MESSAGE.getValue(), captured.getSummary());
//    }

    /**
     * Test for method {@link UserDataModel#saveUser(SystemUser)}
     * when exception occurs for insert operation
     */
//    @Test
//    public void testSaveUpdateUserWhenExceptionOccurs() {
//        Long currentLoggedUserId = 1111111L;
//        User user = new User(); user.setId(1L);
//        when(userSessionEnabled.getUserId()).thenReturn(currentLoggedUserId);
//        when(userRESTServiceAccess.updateUser(user)).thenThrow(new RuntimeException("error"));
//        userDataModel.saveUser(user);
//
//        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
//        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());
//
//        FacesMessage captured = facesMessageCaptor.getValue();
//        assertEquals(FacesMessage.SEVERITY_ERROR, captured.getSeverity());
//        assertEquals(DataModelEnum.EDIT_ERROR_MESSAGE.getValue(), captured.getSummary());
//    }

    /**
     * Test for method {@link UserDataModel#saveUser(SystemUser)}
     * when exception occurs for edit/update operation
     */
//    @Test
//    public void testEditUserWhenExceptionOccurs() {
//        Long currentLoggedUserId = 1111111L;
//        User user = new User(); user.setId(111L);
//        when(userSessionEnabled.getUserId()).thenReturn(currentLoggedUserId);
//        when(userRESTServiceAccess.updateUser(user)).thenThrow(new RuntimeException("error"));
//        userDataModel.saveUser(user);
//
//        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
//        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());
//
//        FacesMessage captured = facesMessageCaptor.getValue();
//        assertEquals(FacesMessage.SEVERITY_ERROR, captured.getSeverity());
//        assertEquals(DataModelEnum.EDIT_ERROR_MESSAGE.getValue(), captured.getSummary());
//    }

    /**
     * Test for method
     * {@link UserDataModel#userRoles()}
     */
    @Test
    public void testUserRoles(){
        SystemUser user = new User(); user.setId(2L);
        userDataModel.setSelectedUser(user);
        assertEquals(DataModelEnum.USERS_ROLES_PATH.getValue(), userDataModel.userRoles());

        userDataModel.setSelectedUser(null);
        assertEquals(DataModelEnum.USERS_PATH.getValue(), userDataModel.userRoles());

    }

    /**
     * Test for method
     * {@link UserDataModel#userUnAssign()}
     */
    @Test
    public void testUserUnAssign(){
        SystemUser user = new User(); user.setId(2L);
        userDataModel.setSelectedUser(user);
        assertEquals(DataModelEnum.USER_UN_ASSIGN_PATH.getValue(), userDataModel.userUnAssign());

        userDataModel.setSelectedUser(null);
        assertEquals(DataModelEnum.USERS_PATH.getValue(), userDataModel.userUnAssign());

    }

    /**
     * Test for method
     * {@link UserDataModel#userEmailUpdate()}
     */
    @Test
    public void testUserEmailUpdate(){
        SystemUser user = new User(); user.setId(2L);
        userDataModel.setSelectedUser(user);
        assertEquals(DataModelEnum.USER_EMAIL_UPDATE.getValue(), userDataModel.userEmailUpdate());

        userDataModel.setSelectedUser(null);
        assertEquals(DataModelEnum.USERS_PATH.getValue(), userDataModel.userEmailUpdate());
    }

    /**
     * Test for method {@link UserDataModel#updateUserEmailAndExecuteActionEmailVerify()}
     */
//    @Test
//    public void testSendUpdateUserEmailVerify() throws SystemException {
//        User user = new User(); user.setId(1L);
//        userDataModel.setSelectedUser(user);
//        SystemUser updateEmail = new User();
//        updateEmail.setUserEmail("myemail@email.com");
//        userDataModel.setUpdateEmail(updateEmail);
//        when(userRESTServiceAccess.updateEmailAndExecuteActionEmailVerify(user.getId(), updateEmail, true)).thenReturn(true);
//        userDataModel.updateUserEmailAndExecuteActionEmailVerify();
//
//        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class );
//        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture() );
//
//        FacesMessage captured = facesMessageCaptor.getValue();
//        assertEquals(FacesMessage.SEVERITY_INFO, captured.getSeverity());
//        assertEquals(DataModelEnum.SENT_UPDATED_EMAIL_VERIFY_SUCCESS.getValue(), captured.getSummary());
//
//    }

    /**
     * Test for method {@link UserDataModel#updateUserEmailAndExecuteActionEmailVerify()}
     */
    @Test
    public void testSendUpdateEmailVerifyWithVariousUserOptions() {
        User user = new User(); user.setId(1L); userDataModel.setSelectedUser(user);
        SystemUser updateEmailNull = new User();
        userDataModel.setUpdateEmail(updateEmailNull);
        userDataModel.updateUserEmailAndExecuteActionEmailVerify();
        assertNotNull(userDataModel.getSelectedUser().getId());

        User userNull = new User(); userDataModel.setSelectedUser(user);
        userDataModel.setSelectedUser(userNull);
        userDataModel.updateUserEmailAndExecuteActionEmailVerify();
        assertNull(userDataModel.getSelectedUser().getId());

    }

    /**
     * Test for method {@link UserDataModel#updateUserEmailAndExecuteActionEmailVerify()}
     */
//    @Test
//    public void testSendUpdateEmailVerifyWhenExceptionOccurs() throws SystemException {
//        User user = new User(); user.setId(1L);
//        userDataModel.setSelectedUser(user);
//        userDataModel.setSelectedUser(user);
//        SystemUser updateEmail = new User();
//        updateEmail.setUserEmail("myemail@email.com");
//        userDataModel.setUpdateEmail(updateEmail);
//        when(userRESTServiceAccess.updateEmailAndExecuteActionEmailVerify(user.getId(), updateEmail, true)).thenThrow(new RuntimeException("error"));
//        userDataModel.updateUserEmailAndExecuteActionEmailVerify();
//
//        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
//        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());
//
//        FacesMessage captured = facesMessageCaptor.getValue();
//        assertEquals( FacesMessage.SEVERITY_ERROR, captured.getSeverity() );
//        assertEquals( DataModelEnum.SENT_UPDATED_EMAIL_VERIFY_ERROR.getValue(), captured.getSummary() );
//    }

    /**
     * Test for method
     * {@link UserDataModel#isAllowedToUpdateUserEmail()}
     * {@link UserDataModel#setAllowedToUpdateUserEmail(boolean)}
     */
    @Test
    public void testGetterSetterAllowedToUpdateUserEmail() {
        this.userDataModel.setAllowedToUpdateUserEmail(true);
        assertTrue(this.userDataModel.isAllowedToUpdateUserEmail());
    }

    /**
     * Test for method
     * {@link UserDataModel#getUpdateEmail()}
     * {@link UserDataModel#setUpdateEmail(SystemUser)}
     */
    @Test
    public void testGetterSetterUpdateEmail() {
        SystemUser systemUser = new User();
        systemUser.setUserEmail("email@email.com");
        this.userDataModel.setUpdateEmail(systemUser);
        assertEquals(systemUser, userDataModel.getUpdateEmail());
    }
}
