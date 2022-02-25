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
package io.radien.webapp.authz;

import io.radien.api.model.user.SystemUser;
import io.radien.api.security.TokensPlaceHolder;
import io.radien.api.security.UserSessionEnabled;
import io.radien.api.service.permission.PermissionRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.authz.client.TenantRoleClient;
import io.radien.ms.authz.client.UserClient;
import io.radien.ms.openid.entities.Principal;
import java.util.Optional;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;

import io.radien.webapp.JSFUtil;
import io.radien.webapp.JSFUtilAndFaceContextMessagesTest;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


import static io.radien.api.service.permission.SystemPermissionsEnum.THIRD_PARTY_EMAIL_MANAGEMENT_UPDATE;
import static io.radien.api.service.permission.SystemPermissionsEnum.THIRD_PARTY_PASSWORD_MANAGEMENT_UPDATE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

/**
 * @author Newton Carvalho
 */
public class WebAuthorizationCheckerTest extends JSFUtilAndFaceContextMessagesTest {

    @InjectMocks
    private WebAuthorizationChecker webAuthorizationChecker;

    @Mock
    private HttpServletRequest servletRequest;

    @Mock
    private UserClient userClient;

    @Mock
    private TenantRoleClient tenantRoleClient;

    @Mock
    private TokensPlaceHolder tokensPlaceHolder;

    @Mock
    UserSessionEnabled userSession;

    @Mock
    PermissionRESTServiceAccess permissionRESTServiceAccess;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @BeforeClass
    public static void beforeClass(){
        handleJSFUtilAndFaceContextMessages();
    }

    @AfterClass
    public static void afterClass(){
       destroy();
    }

    /**
     * Test for getting current user id should be a success
     */
    @Test
    public void testGetCurrentUserId() {
        Long userId = 22L;
        when(this.userSession.getUserId()).thenReturn(userId);
        try {
            assertEquals(webAuthorizationChecker.getCurrentUserId(), userId);
        }
        catch (Exception e) {
            fail("unexpected");
        }
    }

    /**
     * Test to retrieve current user id searching by the subject of him
     * This test should be a success
     * @throws SystemException in case of any issue while retrieving any of the user information
     */
    @Test
    public void testGetCurrentUserIdRetrievingViaSub() throws SystemException {
        String sub = "a-b-c-d";
        Long userId = 22L;
        Response response = Response.ok().entity(userId).build();

        when(this.userSession.getUserId()).thenReturn(null);
        when(this.userSession.getUserIdSubject()).thenReturn(sub);
        when(this.webAuthorizationChecker.getUserClient().getUserIdBySub(sub)).
                thenReturn(response);
        try {
            assertEquals(webAuthorizationChecker.getCurrentUserId(), userId);
        }
        catch (Exception e) {
           fail("unexpected");
        }
    }

    /**
     * Tests the returned user information
     */
    @Test
    public void testGetInvokerUser() {
        when(userSession.getUserFirstName()).thenReturn("name");
        when(userSession.getUserLastName()).thenReturn("last-name");
        when(userSession.getPreferredUserName()).thenReturn("user.name");
        when(userSession.getUserIdSubject()).thenReturn("sub-1");
        when(userSession.getEmail()).thenReturn("user.name@gmail.com");

        SystemUser u = this.webAuthorizationChecker.getInvokerUser();

        assertEquals("name", u.getFirstname());
        assertEquals("last-name", u.getLastname());
        assertEquals("user.name", u.getLogon());
        assertEquals("sub-1", u.getSub());
        assertEquals("user.name@gmail.com", u.getUserEmail());
    }

    /**
     * Tests if the current user has a specific role and grant access
     */
    @Test
    public void testHasGrant() {
        Long userId = 22L;
        Long tenantId = null;
        String role1 = "tenant-administrator";
        String role2 = "tenant-extreme";

        when(this.tokensPlaceHolder.getAccessToken()).thenReturn("142517271828");
        when(this.userSession.getUserId()).thenReturn(userId);

        when(this.tenantRoleClient.isRoleExistentForUser(userId, role1, tenantId)).
                thenReturn(Response.ok().entity(Boolean.TRUE).build()).
                thenThrow(new RuntimeException("test"));

        when(this.tenantRoleClient.isRoleExistentForUser(userId, role2, tenantId)).
                thenReturn(Response.ok().entity(Boolean.FALSE).build());

        assertTrue(this.webAuthorizationChecker.hasGrant(tenantId, role1));
        assertFalse(this.webAuthorizationChecker.hasGrant(tenantId, role1));
        assertFalse(this.webAuthorizationChecker.hasGrant(tenantId, role2));
    }

    /**
     * Test for method {@link WebAuthorizationChecker#hasPermissionToResetPassword(Long)} running
     * on successful case (user having access)
     * @throws SystemException thrown in case of errors during communication with the endpoint
     */
    @Test
    public void testHasPermissionToResetPassword() throws SystemException {
        String resourceName = THIRD_PARTY_PASSWORD_MANAGEMENT_UPDATE.getResource().getResourceName();
        String actionName = THIRD_PARTY_PASSWORD_MANAGEMENT_UPDATE.getAction().getActionName();

        boolean result = testHasPermissionToResetPassword(resourceName, actionName, true);
        assertTrue(result);
    }

    /**
     * Test for method {@link WebAuthorizationChecker#hasPermissionToResetPassword(Long)} running
     * on unsuccessful case (user have no access)
     * @throws SystemException thrown in case of errors during communication with the endpoint
     */
    @Test
    public void testHasNoPermissionToResetPassword() throws SystemException {
        String resourceName = THIRD_PARTY_PASSWORD_MANAGEMENT_UPDATE.getResource().getResourceName();
        String actionName = THIRD_PARTY_PASSWORD_MANAGEMENT_UPDATE.getAction().getActionName();

        boolean result = testHasPermissionToResetPassword(resourceName, actionName, false);
        assertFalse(result);
    }

    /**
     * Test for method {@link WebAuthorizationChecker#hasPermissionToResetPassword(Long)} running
     * on unsuccessful case (Permission could not be found)
     * @throws SystemException thrown in case of errors during communication with the endpoint
     */
    @Test
    public void testHasGrantToResetPasswordWhenPermissionNotFound() throws SystemException {
        Long tenantId = 10L;
        String resourceName = THIRD_PARTY_PASSWORD_MANAGEMENT_UPDATE.getResource().getResourceName();
        String actionName = THIRD_PARTY_PASSWORD_MANAGEMENT_UPDATE.getAction().getActionName();
        when(permissionRESTServiceAccess.getIdByResourceAndAction(resourceName, actionName)).
                thenReturn(Optional.empty());
        boolean result = webAuthorizationChecker.hasPermissionToResetPassword(tenantId);
        assertFalse(result);
    }

    /**
     * Test for method {@link WebAuthorizationChecker#hasPermissionToResetPassword(Long)} running
     * on unsuccessful case (Exception occurring during the check process)
     * @throws SystemException thrown in case of errors during communication with the endpoint
     */
    @Test
    public void testHasGrantToResetPasswordWhenExceptionOccurs() throws SystemException {
        Long tenantId = 10L;
        String resourceName = THIRD_PARTY_PASSWORD_MANAGEMENT_UPDATE.getResource().getResourceName();
        String actionName = THIRD_PARTY_PASSWORD_MANAGEMENT_UPDATE.getAction().getActionName();
        when(permissionRESTServiceAccess.getIdByResourceAndAction(resourceName, actionName)).
                thenThrow(new SystemException("error"));
        boolean result = webAuthorizationChecker.hasPermissionToResetPassword(tenantId);
        assertFalse(result);
    }

    /**
     * Test for method {@link WebAuthorizationChecker#hasPermissionToUpdateUserEmail(Long)}
     * on successful case (user having access)
     * @throws SystemException thrown in case of errors during communication with the endpoint
     */
    @Test
    public void testHasPermissionToUpdateUserEmailWithUpdate() throws SystemException {
        String resourceNameUpdate = THIRD_PARTY_EMAIL_MANAGEMENT_UPDATE.getResource().getResourceName();
        String actionNameUpdate = THIRD_PARTY_EMAIL_MANAGEMENT_UPDATE.getAction().getActionName();

        boolean result = testHasPermissionToUpdateUserEmail(resourceNameUpdate, actionNameUpdate, true);
        assertTrue(result);
    }

    /**
     * Test for method {@link WebAuthorizationChecker#hasPermissionToResetPassword(Long)} running
     * on unsuccessful case (user have no access)
     * @throws SystemException thrown in case of errors during communication with the endpoint
     */
    @Test
    public void testHasNoPermissionToUpdateEmail() throws SystemException {
        String resourceNameUpdate = THIRD_PARTY_EMAIL_MANAGEMENT_UPDATE.getResource().getResourceName();
        String actionNameUpdate = THIRD_PARTY_EMAIL_MANAGEMENT_UPDATE.getAction().getActionName();

        boolean result = testHasPermissionToUpdateUserEmail(resourceNameUpdate, actionNameUpdate, false);
        assertFalse(result);
    }

    /**
     * Test for method {@link WebAuthorizationChecker#hasPermissionToResetPassword(Long)} running
     * on unsuccessful case (Permission could not be found)
     * @throws SystemException thrown in case of errors during communication with the endpoint
     */
    @Test
    public void testHasPermissionToUpdateUserEmailWhenPermissionNotFound() throws SystemException {
        Long tenantId = 10L;
        String resourceName = THIRD_PARTY_EMAIL_MANAGEMENT_UPDATE.getResource().getResourceName();
        String actionName = THIRD_PARTY_EMAIL_MANAGEMENT_UPDATE.getAction().getActionName();
        when(permissionRESTServiceAccess.getIdByResourceAndAction(resourceName, actionName)).
                thenReturn(Optional.empty());
        boolean result = webAuthorizationChecker.hasPermissionToUpdateUserEmail(tenantId);
        assertFalse(result);
    }

    /**
     * Test for method {@link WebAuthorizationChecker#hasPermissionToResetPassword(Long)} running
     * on unsuccessful case (Exception occurring during the check process)
     * @throws SystemException thrown in case of errors during communication with the endpoint
     */
    @Test
    public void testHasPermissionToUpdateUserEmailWhenExceptionOccurs() throws SystemException {
        Long tenantId = 10L;
        String resourceName = THIRD_PARTY_EMAIL_MANAGEMENT_UPDATE.getResource().getResourceName();
        String actionName = THIRD_PARTY_EMAIL_MANAGEMENT_UPDATE.getAction().getActionName();
        when(permissionRESTServiceAccess.getIdByResourceAndAction(resourceName, actionName)).
                thenThrow(new SystemException("error"));
        boolean result = webAuthorizationChecker.hasPermissionToUpdateUserEmail(tenantId);
        assertFalse(result);
    }

    private boolean testHasPermissionToResetPassword(String resourceName, String actionName, boolean response) throws SystemException {
        Long permissionId = 222L;
        Long tenantId = 10L;
        Long userId = 1111L;
        when(this.userSession.getUserId()).thenReturn(userId);
        when(this.userSession.isActive()).thenReturn(Boolean.TRUE);
        when(permissionRESTServiceAccess.getIdByResourceAndAction(resourceName, actionName)).
                then(i -> Optional.of(permissionId));
        doReturn("token-yyz").when(tokensPlaceHolder).getAccessToken();
        Response expectedResponse = Response.ok().entity(response).build();
        doReturn(expectedResponse).when(tenantRoleClient).isPermissionExistentForUser(
                userId, permissionId, tenantId);

        when(userSession.isActive()).thenReturn(true);

        return webAuthorizationChecker.hasPermissionToResetPassword(tenantId);
    }

    private boolean testHasPermissionToUpdateUserEmail(String resourceName, String actionName, boolean response) throws SystemException {
        Long permissionId = 222L;
        Long tenantId = 10L;
        Long userId = 1111L;
        when(this.userSession.getUserId()).thenReturn(userId);
        when(this.userSession.isActive()).thenReturn(Boolean.TRUE);
        when(permissionRESTServiceAccess.getIdByResourceAndAction(resourceName, actionName)).
                then(i -> Optional.of(permissionId));
        doReturn("token-yyz").when(tokensPlaceHolder).getAccessToken();
        Response expectedResponse = Response.ok().entity(response).build();
        doReturn(expectedResponse).when(tenantRoleClient).isPermissionExistentForUser(
                userId, permissionId, tenantId);
        when(userSession.isActive()).thenReturn(true);

       return webAuthorizationChecker.hasPermissionToUpdateUserEmail(tenantId);
    }
    @Test
    public void redirectOnMissingPermissionTest(){
        assertTrue(webAuthorizationChecker.redirectOnMissingPermission("Employee","Read",null,"Dest"));
    }

    @Test
    public void redirectOnMissingPermissionTestActive(){
        when(userSession.isActive()).thenReturn(true);
        assertTrue(webAuthorizationChecker.redirectOnMissingPermission("Employee","Read",null,"Dest"));
    }

    @Test
    public void redirectOnMissingPermissionTestActiveAndWithPermission() throws SystemException {
        when(userSession.isActive()).thenReturn(true);

        HttpSession session = Mockito.mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);

        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(principal);

        when(permissionRESTServiceAccess.
                getIdByResourceAndAction(any(),any())).thenReturn(Optional.of(1l));

        doReturn("token-yyz").when(tokensPlaceHolder).getAccessToken();
        Response expectedResponse = Response.ok().entity(true).build();
        doReturn(expectedResponse).when(tenantRoleClient).isPermissionExistentForUser(any(), any(), any());

        assertFalse(webAuthorizationChecker.redirectOnMissingPermission("Employee","Read",null,"Dest"));
    }




}
