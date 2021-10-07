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
package io.radien.ms.authz.security;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.security.TokensPlaceHolder;
import io.radien.api.service.role.SystemRolesEnum;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.SystemException;
import io.radien.exception.TokenExpiredException;
import io.radien.ms.authz.client.TenantRoleClient;
import io.radien.ms.authz.client.UserClient;
import io.radien.ms.authz.client.exception.NotFoundException;
import io.radien.ms.openid.entities.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class AuthorizationCheckerTest {

    @InjectMocks
    private final AuthorizationChecker authorizationChecker = new AuthorizationChecker() {
        private static final long serialVersionUID = 1916818168942543909L;
    };

    @Mock
    private HttpServletRequest servletRequest;

    @Mock
    private UserClient userClient;

    @Mock
    private TenantRoleClient tenantRoleClient;

    @Mock
    private TokensPlaceHolder tokensPlaceHolder;

    @Mock
    private OAFAccess oafAccess;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test for method {@link AuthorizationChecker#refreshToken()
     * Scenario: refreshToken performs successfully (In other words, a new JWT access token is created)
     * Expected outcome: TRUE
     * @throws SystemException thrown by User RestClient (see {@link UserClient#refreshToken(String)}
     */
    @Test
    public void testRefreshTokenSuccess() throws SystemException{
        String refreshToken = "11122334445566";
        String newAccessToken = "33333333333";
        when(oafAccess.getProperty(any())).thenReturn("x");
        when(tokensPlaceHolder.getRefreshToken()).thenReturn(refreshToken);
        when(this.userClient.refreshToken(refreshToken)).
                thenReturn(Response.ok().entity(newAccessToken).build());
        assertTrue(authorizationChecker.refreshToken());
    }

    /**
     * Tests for method {@link AuthorizationChecker#refreshToken()
     * Scenario: refreshToken performs unsuccessfully (In other words, NO jwt access token is created)
     * Expected outcome: FALSE
     * @throws SystemException thrown by User RestClient (see {@link UserClient#refreshToken(String)}
     */
    @Test
    public void testRefreshTokenUnSuccess() throws SystemException{
        String refreshToken = "1";
        String newAccessToken = "2";
        when(oafAccess.getProperty(any())).thenReturn("x");
        when(tokensPlaceHolder.getRefreshToken()).thenReturn(refreshToken);
        when(this.userClient.refreshToken(refreshToken)).thenReturn(Response.status(300).build());
        assertFalse(authorizationChecker.refreshToken());
    }

    /**
     * Tests for method {@link AuthorizationChecker#refreshToken()
     * Scenario: refreshToken performs unsuccessfully (SystemException occurs in the middle of the process)
     * @throws SystemException thrown by User RestClient (see {@link UserClient#refreshToken(String)}
     */
    @Test
    public void testRefreshTokenForSystemException() throws SystemException{
        String refreshToken = "4";
        String errorMsg = "error doing refreshing";
        when(oafAccess.getProperty(any())).thenReturn("x");
        when(tokensPlaceHolder.getRefreshToken()).thenReturn(refreshToken);
        when(this.userClient.refreshToken(refreshToken)).
                thenThrow(new javax.ws.rs.ProcessingException(errorMsg));
        SystemException se = assertThrows(SystemException.class, authorizationChecker::refreshToken);
        assertTrue(se.getMessage().contains(errorMsg));
    }

    /**
     * Test for method {@link AuthorizationChecker#hasGrant(String)
     * Scenario: User has access to an informed role
     * Expected outcome: TRUE
     * @throws SystemException described on the signature for {@link AuthorizationChecker#hasGrant(String)}
     */
    @Test
    public void testHasGrantForRoleReturnTrue() throws SystemException{
        Long userId = 1001L;
        String roleName = "admin";

        HttpSession session = Mockito.mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);

        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(principal);

        when(this.userClient.getUserIdBySub(principal.getSub())).
                thenReturn(Response.ok().entity(userId).build());
        when(this.tenantRoleClient.isRoleExistentForUser(userId, roleName, null)).
                thenReturn(Response.ok().entity(Boolean.TRUE).build());
        when(tokensPlaceHolder.getAccessToken()).thenReturn("token-yyz");

        assertTrue(authorizationChecker.hasGrant(roleName));
    }

    /**
     * Test for method {@link AuthorizationChecker#hasGrant(Long, String)
     * Scenario: User has access to an informed role in a given tenant (id)
     * Expected outcome: TRUE
     * @throws SystemException described on the signature for {@link AuthorizationChecker#hasGrant(Long, String)}
     */
    @Test
    public void testHasGrantForRoleWithTenantParamReturnTrue() throws SystemException {
        Long userId = 1001L;
        Long tenantId = 1111L;
        String roleName = "admin";

        HttpSession session = Mockito.mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);

        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(principal);

        when(this.userClient.getUserIdBySub(principal.getSub())).
                thenReturn(Response.ok().entity(userId).build());
        when(this.tenantRoleClient.isRoleExistentForUser(userId, roleName, tenantId)).
                thenReturn(Response.ok().entity(Boolean.TRUE).build());
        when(tokensPlaceHolder.getAccessToken()).thenReturn("token-yyz");

        assertTrue(authorizationChecker.hasGrant(tenantId, roleName));
    }

    /**
     * Utility method to reduce cognitive complexity when mocking components for refresh token cases
     * @param holder instance of {@link TokensPlaceHolder}
     * @param uc instance of {@link UserClient}
     */
    protected void prepareMockParamForRefreshToken(TokensPlaceHolder holder, UserClient uc) {
        String refreshToken = "11122334445566";
        String newAccessToken = "33333333333";
        when(holder.getRefreshToken()).thenReturn(refreshToken).thenReturn(refreshToken);
        when(uc.refreshToken(refreshToken)).thenReturn(Response.ok().entity(newAccessToken).build());
    }

    /**
     * Test for method {@link AuthorizationChecker#hasGrant(Long, String)
     * Scenario: While it verifies if user has access to a given role and tenant, the jwt access token expires.
     * Then Refresh token operation is performed and the assessment is successfully concluded
     * Expected outcome: TRUE
     * @throws SystemException described on the signature for {@link AuthorizationChecker#hasGrant(Long, String)}
     */
    @Test
    public void testHasGrantForRoleWithTenantWhenTokenExpires() throws SystemException {
        Long userId = 1001L;
        Long tenantId = 1111L;
        String roleName = "admin";

        HttpSession session = Mockito.mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session).
                thenReturn(session).thenReturn(session).thenReturn(session);

        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(principal);

        when(this.userClient.getUserIdBySub(principal.getSub())).
                thenReturn(Response.ok().entity(userId).build());

        prepareMockParamForRefreshToken(tokensPlaceHolder, userClient);

        when(tokensPlaceHolder.getAccessToken()).thenReturn("token-yyz");

        when(this.tenantRoleClient.isRoleExistentForUser(userId, roleName, tenantId)).
                thenThrow(new TokenExpiredException()).
                thenReturn(Response.ok().entity(Boolean.TRUE).build());

        assertTrue(authorizationChecker.hasGrant(tenantId, roleName));
    }

    /**
     * Test for method {@link AuthorizationChecker#hasGrant(Long, String)
     * Scenario: While it verifies if user has access to a given role and tenant, the jwt access token expires.
     * When trying to perform refresh token operation (due some delay), the new access token expires.
     * Expected outcome: raise TokenExpiredException
     * @throws SystemException described on the signature for {@link AuthorizationChecker#hasGrant(Long, String)}
     */
    @Test(expected = TokenExpiredException.class)
    public void testHasGrantForRoleWithTenantWhenTokenExpiresTwice() throws SystemException {
        Long userId = 1001L;
        Long tenantId = 1111L;
        String roleName = "admin";

        HttpSession session = Mockito.mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session).
                thenReturn(session).thenReturn(session).thenReturn(session);

        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(principal);
        when(this.userClient.getUserIdBySub(principal.getSub())).
                thenReturn(Response.ok().entity(userId).build());
        prepareMockParamForRefreshToken(tokensPlaceHolder, userClient);
        when(tokensPlaceHolder.getAccessToken()).thenReturn("token-yyz");
        when(this.tenantRoleClient.isRoleExistentForUser(userId, roleName, tenantId)).
                thenThrow(new TokenExpiredException()).
                thenThrow(new TokenExpiredException());

        authorizationChecker.hasGrant(tenantId, roleName);
    }

    /**
     * Test for method {@link AuthorizationChecker#hasGrant(Long, String)
     * Scenario: User has NO access to an informed role
     * Expected outcome: FALSE
     * @throws SystemException described on the signature for {@link AuthorizationChecker#hasGrant(Long, String)}
     */
    @Test
    public void testHasGrantForRoleReturnFalse() throws SystemException {
        Long userId = 1001L;
        String roleName = "admin";

        HttpSession session = Mockito.mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);

        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(principal);

        when(this.userClient.getUserIdBySub(principal.getSub())).
                thenReturn(Response.ok().entity(userId).build());
        when(this.tenantRoleClient.isRoleExistentForUser(userId, roleName, null)).
                thenReturn(Response.ok().entity(Boolean.FALSE).build());
        when(tokensPlaceHolder.getAccessToken()).thenReturn("token-yyz");

        assertFalse(authorizationChecker.hasGrant(roleName));
    }

    /**
     * Test for method {@link AuthorizationChecker#hasGrant(Long, String)
     * Scenario: User has NO access to an informed role in a given tenant (id)
     * Expected outcome: FALSE
     * @throws SystemException described on the signature for {@link AuthorizationChecker#hasGrant(Long, String)}
     */
    @Test
    public void testHasGrantForRoleWithTenantParamReturnFalse() throws SystemException {
        Long userId = 1001L;
        Long tenantId = 1111L;
        String roleName = "admin";

        HttpSession session = Mockito.mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);

        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(principal);

        when(this.userClient.getUserIdBySub(principal.getSub())).
                thenReturn(Response.ok().entity(userId).build());
        when(this.tenantRoleClient.isRoleExistentForUser(userId, roleName, tenantId)).
                thenReturn(Response.ok().entity(Boolean.FALSE).build());
        when(tokensPlaceHolder.getAccessToken()).thenReturn("token-yyz");

        assertFalse(authorizationChecker.hasGrant(tenantId, roleName));
    }

    /**
     * Test for method {@link AuthorizationChecker#hasGrant(Long, String)
     * Scenario: User has NO access to an informed role in a given tenant (id).
     * Tenant Role API responds with some status different from success family (2xx)
     * Expected outcome: FALSE
     * @throws SystemException described on the signature for {@link AuthorizationChecker#hasGrant(Long, String)}
     */
    @Test
    public void testHasGrantForRoleWithTenantParamReturnFalseDueHttpStatusNOK() throws SystemException {
        Long userId = 1001L;
        Long tenantId = 1111L;
        String roleName = "admin";

        HttpSession session = Mockito.mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);

        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(principal);

        when(this.userClient.getUserIdBySub(principal.getSub())).
                thenReturn(Response.ok().entity(userId).build());
        when(this.tenantRoleClient.isRoleExistentForUser(userId, roleName, tenantId)).
                thenReturn(Response.status(300).build());
        when(tokensPlaceHolder.getAccessToken()).thenReturn("token-yyz");

        assertFalse(authorizationChecker.hasGrant(tenantId, roleName));
    }


    /**
     * Test for method {@link AuthorizationChecker#hasGrant(Long, String)
     * Scenario: Given a sub, No User could be found in the radien repository.
     * Expected outcome: raise NotFoundException
     * @throws SystemException described on the signature for {@link AuthorizationChecker#hasGrant(Long, String)}
     */
    @Test(expected = NotFoundException.class)
    public void testFailureWhenRetrievingUserId() throws SystemException{
        Long tenantId = 1111L;
        String roleName = "admin";

        HttpSession session = Mockito.mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);

        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(principal);

        when(tokensPlaceHolder.getAccessToken()).thenReturn("token-yyz");
        when(this.userClient.getUserIdBySub(principal.getSub())).
                thenThrow(NotFoundException.class);

        authorizationChecker.hasGrant(tenantId, roleName);
    }

    /**
     * Test for method {@link AuthorizationChecker#getCurrentUserIdBySub(String)}
     * Scenario: Given a sub, tries to retrieve User Id but jwt access token expires.
     * A new access token will be produced (via refresh token operation), the operation
     * will be reattempt and concluded with success.
     * Expected outcome: user id retrieved
     * @throws SystemException described on the signature for {@link AuthorizationChecker#hasGrant(Long, String)}
     */
    @Test
    public void testTokenExpiresWhenRetrievingUserId() throws SystemException {
        Long userId = 1001L;

        HttpSession session = Mockito.mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session).
                thenReturn(session).thenReturn(session);

        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");

        when(servletRequest.getSession()).thenReturn(session).
                thenReturn(session).thenReturn(session);

        when(servletRequest.getSession(false)).thenReturn(session).
                thenReturn(session).thenReturn(session);

        when(session.getAttribute("USER")).thenReturn(principal).
                thenReturn(principal).thenReturn(principal);

        when(tokensPlaceHolder.getAccessToken()).thenReturn("token-yyz").
                thenReturn("token-yyz").thenReturn("token-yyz");

        when(this.userClient.getUserIdBySub(principal.getSub())).
                thenThrow(TokenExpiredException.class).
                thenReturn(Response.ok().entity(userId).build());

        this.prepareMockParamForRefreshToken(tokensPlaceHolder, this.userClient);

        assertEquals(userId, authorizationChecker.getCurrentUserIdBySub(principal.getSub()));
    }

    /**
     * Test for method {@link AuthorizationChecker#getCurrentUserIdBySub(String)}
     * Scenario: Given a sub, tries to retrieve User Id but jwt access token expires.
     * There will be a first attempt to generate a new access token (via refresh token), but suddenly jwt access
     * token expires again.
     * Expected outcome: raise {@link TokenExpiredException}
     * @throws SystemException described on the signature for {@link AuthorizationChecker#hasGrant(Long, String)}
     */
    @Test(expected = TokenExpiredException.class)
    public void testTokenExpiresTwiceWhenRetrievingUserId() throws SystemException {
        HttpSession session = Mockito.mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session).
                thenReturn(session).thenReturn(session);

        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");

        when(servletRequest.getSession()).thenReturn(session).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(principal);
        when(tokensPlaceHolder.getAccessToken()).thenReturn("token-yyz");

        when(this.userClient.getUserIdBySub(principal.getSub())).
                thenThrow(TokenExpiredException.class).
                thenThrow(TokenExpiredException.class);

        this.prepareMockParamForRefreshToken(tokensPlaceHolder, this.userClient);

        authorizationChecker.getCurrentUserIdBySub(principal.getSub());
    }

    /**
     * Test for method {@link AuthorizationChecker#getCurrentUserIdBySub(String)}
     * Scenario: Given a sub, tries to retrieve User Id but SystemException occurs in the middle of the process.
     * Expected outcome: raise {@link SystemException}
     * @throws SystemException described on the signature for {@link AuthorizationChecker#hasGrant(Long, String)}
     */
    @Test
    public void testSystemExceptionWhenRetrievingUserId() throws SystemException {
        AuthorizationChecker authChecker = spy(authorizationChecker);
        HttpSession session = Mockito.mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session).
                thenReturn(session).thenReturn(session);

        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");

        when(servletRequest.getSession()).thenReturn(session).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(principal);
        when(tokensPlaceHolder.getAccessToken()).thenReturn("token-yyz");

        String msgError = "Error trying to obtain user id";
        when(authChecker.getUserClient()).thenThrow(new SystemException(msgError));

        this.prepareMockParamForRefreshToken(tokensPlaceHolder, this.userClient);

        SystemException se = assertThrows(SystemException.class, () ->
                authChecker.getCurrentUserIdBySub(principal.getSub()));
        assertTrue(se.getMessage().contains(msgError));
    }

    /**
     * Test for method {@link AuthorizationChecker#hasGrant(Long, String)}
     * Scenario: No invoker user is present (No current logged user detected).
     * Expected outcome: raise {@link SystemException}
     * @throws SystemException described on the signature for {@link AuthorizationChecker#hasGrant(Long, String)}
     */
    @Test
    public void testFailureWhenNoInvokerUserIsPresent() throws SystemException {
        Long tenantId = 1111L;
        String roleName = "admin";

        HttpSession session = Mockito.mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);

        when(tokensPlaceHolder.getAccessToken()).thenReturn("token-yyz");

        SystemException se = assertThrows(SystemException.class, () ->
                authorizationChecker.hasGrant(tenantId, roleName));
        assertTrue(se.getMessage().contains(GenericErrorCodeMessage.
                NO_CURRENT_USER_AVAILABLE.toString()));
    }

    /**
     * Test for method {@link AuthorizationChecker#hasGrant(Long, Long)}
     * Scenario: User has access to an informed permission (id) in a given tenant (id)
     * Expected outcome: TRUE
     * @throws SystemException described on the signature for {@link AuthorizationChecker#hasGrant(Long, Long)}
     */
    @Test
    public void testGrantForPermissionExists() throws SystemException {
        Long userId = 1001L;
        Long tenantId = 22L;
        Long permissionId = 1L;

        HttpSession session = Mockito.mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);

        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(principal);

        when(this.userClient.getUserIdBySub(principal.getSub())).
                thenReturn(Response.ok().entity(userId).build());
        when(this.tenantRoleClient.isPermissionExistentForUser(userId, permissionId, tenantId)).
                thenReturn(Response.ok(Boolean.TRUE).build());
        when(tokensPlaceHolder.getAccessToken()).thenReturn("token-yyz");

        assertTrue(authorizationChecker.hasGrant(permissionId, tenantId));
    }

    /**
     * Test for method {@link AuthorizationChecker#hasGrant(Long, Long)}
     * Scenario: User has NO access to an informed permission (id) in a given tenant (id)
     * Expected outcome: FALSE
     * @throws SystemException described on the signature for {@link AuthorizationChecker#hasGrant(Long, Long)}
     */
    @Test
    public void testGrantForPermissionNotExist() throws SystemException {
        Long userId = 1001L;
        Long tenantId = 22L;
        Long permissionId = 1L;

        HttpSession session = Mockito.mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);

        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(principal);

        when(this.userClient.getUserIdBySub(principal.getSub())).
                thenReturn(Response.ok().entity(userId).build());
        when(this.tenantRoleClient.isPermissionExistentForUser(userId, permissionId, tenantId)).
                thenReturn(Response.ok(Boolean.FALSE).build());
        when(tokensPlaceHolder.getAccessToken()).thenReturn("token-yyz");

        assertFalse(authorizationChecker.hasGrant(permissionId, tenantId));
    }

    /**
     * Test for method {@link AuthorizationChecker#hasGrant(Long, Long)}
     * Scenario: User has NO access to an informed permission (id) in a given tenant (id).
     * Tenant Role API responds with some status different from success family (2xx)
     * Expected outcome: FALSE
     * @throws SystemException described on the signature for {@link AuthorizationChecker#hasGrant(Long, Long)}
     */
    @Test
    public void testGrantForPermissionWhenResponseHttpStatusNOK() throws SystemException {
        Long userId = 1001L;
        Long tenantId = 22L;
        Long permissionId = 1L;

        HttpSession session = Mockito.mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);

        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(principal);

        when(this.userClient.getUserIdBySub(principal.getSub())).
                thenReturn(Response.ok().entity(userId).build());
        when(this.tenantRoleClient.isPermissionExistentForUser(userId, permissionId, tenantId)).
                thenReturn(Response.status(300).build());
        when(tokensPlaceHolder.getAccessToken()).thenReturn("token-yyz");

        assertFalse(authorizationChecker.hasGrant(permissionId, tenantId));
    }

    /**
     * Test for method {@link AuthorizationChecker#hasGrant(Long, Long)
     * Scenario: While it verifies if user has access to a given permission and tenant, the jwt access token expires.
     * Then Refresh token operation is performed and the assessment is successfully concluded
     * Expected outcome: TRUE
     * @throws SystemException described on the signature for {@link AuthorizationChecker#hasGrant(Long, Long)}
     */
    @Test
    public void testTokenExpiresWhenCheckingGrantForPermission() throws SystemException{
        Long userId = 1001L;
        Long tenantId = 22L;
        Long permissionId = 1L;

        HttpSession session = Mockito.mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);

        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");

        when(servletRequest.getSession()).thenReturn(session);

        when(servletRequest.getSession(false)).thenReturn(session);

        when(session.getAttribute("USER")).thenReturn(principal);

        when(this.userClient.getUserIdBySub(principal.getSub())).
                thenReturn(Response.ok().entity(userId).build());

        when(this.tenantRoleClient.isPermissionExistentForUser(userId, permissionId, tenantId)).
                thenThrow(TokenExpiredException.class).
                thenReturn(Response.ok(Boolean.TRUE).build());

        when(tokensPlaceHolder.getAccessToken()).thenReturn("token-yyz");

        this.prepareMockParamForRefreshToken(tokensPlaceHolder, userClient);

        assertTrue(authorizationChecker.hasGrant(permissionId, tenantId));
    }

    /**
     * Test for method {@link AuthorizationChecker#hasGrant(Long, Long)
     * Scenario: While it verifies if user has access to a given permission and tenant, the jwt access token expires.
     * When trying to perform refresh token operation (due some delay), the new access token expires.
     * Expected outcome: raise SystemException
     */
    @Test
    public void testTokenExpiresTwiceWhenCheckingGrantForPermission() {
        Long userId = 1001L;
        Long tenantId = 22L;
        Long permissionId = 1L;

        HttpSession session = Mockito.mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);

        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");

        when(servletRequest.getSession()).thenReturn(session);

        when(servletRequest.getSession(false)).thenReturn(session);

        when(session.getAttribute("USER")).thenReturn(principal);

        when(this.userClient.getUserIdBySub(principal.getSub())).
                thenReturn(Response.ok().entity(userId).build());

        when(this.tenantRoleClient.isPermissionExistentForUser(userId, permissionId, tenantId)).
                thenThrow(TokenExpiredException.class).
                thenThrow(TokenExpiredException.class);

        when(tokensPlaceHolder.getAccessToken()).thenReturn("token-yyz");

        this.prepareMockParamForRefreshToken(tokensPlaceHolder, userClient);

        SystemException se = assertThrows(SystemException.class,
                () -> authorizationChecker.hasGrant(permissionId, tenantId));
        assertTrue(se.getMessage().contains(TokenExpiredException.class.getName()));
    }

    /**
     * Test scenario where the PlaceHolder is empty. Covers the case where access
     * token information is obtained via http session attribute.
     * This is simulate during {@link AuthorizationChecker#hasGrant(String)}
     * @throws SystemException described on the signature for {@link AuthorizationChecker#hasGrant(String)}
     */
    @Test
    public void testTokensPlaceHolderEmpty() throws SystemException {

        Long userId = 1001L;
        String roleName = "admin";

        HttpSession session = Mockito.mock(HttpSession.class);
        when(session.getAttribute("accessToken")).thenReturn("57689405040406094950606050606060nnnc");

        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(principal);

        when(this.userClient.getUserIdBySub(principal.getSub())).thenReturn(Response.ok().entity(userId).build());
        when(this.tenantRoleClient.isRoleExistentForUser(userId, roleName, null)).
                thenReturn(Response.ok().entity(Boolean.TRUE).build());

        assertTrue(authorizationChecker.hasGrant(roleName));
    }

    /**
     * Test scenario where the PlaceHolder is empty. Covers the case where access
     * token is obtained via header information. This is simulate during
     * {@link AuthorizationChecker#hasGrant(String)}
     * @throws SystemException described on the signature for {@link AuthorizationChecker#hasGrant(String)}
     */
    @Test
    public void testTokensPlaceHolderEmpty2() throws SystemException{

        Long userId = 1001L;
        String roleName = "admin";

        HttpSession session = Mockito.mock(HttpSession.class);
        when(servletRequest.getHeader(HttpHeaders.AUTHORIZATION)).
                thenReturn("Bearer 57689405040406094950606050606060nnnc");

        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(principal);

        when(this.userClient.getUserIdBySub(principal.getSub())).thenReturn(Response.ok().entity(userId).build());

        when(this.tenantRoleClient.isRoleExistentForUser(userId, roleName, null)).
                thenReturn(Response.ok().entity(Boolean.TRUE).build());

        assertTrue(authorizationChecker.hasGrant(roleName));
    }

    /**
     * Test case for the scenario where is not
     * possible to load User Client ({@link UserClient})
     * Expected outcome: raise {@link SystemException}
     */
    @Test
    public void testGetUserClientWithError() {
        AuthorizationChecker spied = Mockito.spy(AuthorizationChecker.class);

        RestClientBuilder builder = mock(RestClientBuilder.class);
        doThrow(new RuntimeException()).when(builder).build(UserClient.class);
        doReturn(builder).when(spied).getRestClientBuilder();

        OAFAccess oaf = mock(OAFAccess.class);
        doReturn("http://localhost:8080").when(oaf).
                getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_USERMANAGEMENT);
        doReturn(oaf).when(spied).getOafAccess();

        assertThrows(SystemException.class, spied::getUserClient);
    }

    /**
     * Test case for the scenario where is not
     * possible to load Tenant Role Client ({@link TenantRoleClient})
     * Expected outcome: raise {@link SystemException}
     */
    @Test
    public void testGetTenantRoleClientWithError() {
        AuthorizationChecker spied = Mockito.spy(AuthorizationChecker.class);

        RestClientBuilder builder = mock(RestClientBuilder.class);
        doThrow(new RuntimeException()).when(builder).build(TenantRoleClient.class);
        doReturn(builder).when(spied).getRestClientBuilder();

        OAFAccess oaf = mock(OAFAccess.class);
        doReturn("http://localhost:8080").when(oaf).
                getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT);
        doReturn(oaf).when(spied).getOafAccess();

        assertThrows(SystemException.class, spied::getTenantRoleClient);
    }

    /**
     * Test case to cover scenario where RestClientBuilder is assembled
     */
    @Test
    public void testGetRestClientBuilder() {
        AuthorizationChecker spied = Mockito.spy(AuthorizationChecker.class);
        RestClientBuilder rcb = spied.getRestClientBuilder();
        assertNotNull(rcb);
    }

    /**
     * Test for method {@link AuthorizationChecker#hasGrantMultipleRoles(List)}
     * Scenario: Given an informed role list, user has access to some of them
     * Expected outcome: TRUE
     * @throws SystemException described on the signature for {@link AuthorizationChecker#hasGrantMultipleRoles(List)}
     */
    @Test
    public void testHasGrantMultipleRolesReturnTrue() throws SystemException {
        Long userId = 1001L;

        List<String> roleList = new ArrayList<>();
        roleList.add(SystemRolesEnum.SYSTEM_ADMINISTRATOR.getRoleName());
        roleList.add(SystemRolesEnum.USER_ADMINISTRATOR.getRoleName());

        HttpSession session = Mockito.mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);

        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(principal);

        when(this.userClient.getUserIdBySub(principal.getSub())).
                thenReturn(Response.ok().entity(userId).build());
        when(this.tenantRoleClient.checkPermissions(userId, roleList, null)).
                thenReturn(Response.ok().entity(Boolean.TRUE).build());
        when(tokensPlaceHolder.getAccessToken()).thenReturn("token-yyz");

        assertTrue(authorizationChecker.hasGrantMultipleRoles(roleList));
    }

    /**
     * Test for method {@link AuthorizationChecker#hasGrantMultipleRoles(List)}
     * Scenario: Given an informed role list, user has access to none of them
     * Expected outcome: FALSE
     * @throws SystemException described on the signature for {@link AuthorizationChecker#hasGrantMultipleRoles(List)}
     */
    @Test
    public void testHasGrantMultipleRolesReturnFalse() throws SystemException {
        Long userId = 1001L;

        List<String> roleList = new ArrayList<>();
        roleList.add(SystemRolesEnum.SYSTEM_ADMINISTRATOR.getRoleName());
        roleList.add(SystemRolesEnum.USER_ADMINISTRATOR.getRoleName());

        HttpSession session = Mockito.mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);

        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(principal);

        when(this.userClient.getUserIdBySub(principal.getSub())).
                thenReturn(Response.ok().entity(userId).build());
        when(this.tenantRoleClient.checkPermissions(userId, roleList, null)).
                thenReturn(Response.ok().entity(Boolean.FALSE).build());
        when(tokensPlaceHolder.getAccessToken()).thenReturn("token-yyz");

        assertFalse(authorizationChecker.hasGrantMultipleRoles(roleList));
    }

    /**
     * Test for method {@link AuthorizationChecker#hasGrantMultipleRoles(Long, List)}
     * Scenario: While it verifies if user has access to some role (in a given list) and tenant, the jwt access token expires.
     * Then Refresh token operation is performed and the assessment is successfully concluded
     * Expected outcome: TRUE
     * @throws SystemException described on the signature for {@link AuthorizationChecker#hasGrantMultipleRoles(Long, List)}
     */
    @Test
    public void testHasGrantMultipleRoleWithTenantWhenTokenExpires() {
        Long userId = 1001L;
        Long tenantId = 1111L;

        List<String> roleList = new ArrayList<>();
        roleList.add(SystemRolesEnum.SYSTEM_ADMINISTRATOR.getRoleName());
        roleList.add(SystemRolesEnum.USER_ADMINISTRATOR.getRoleName());

        HttpSession session = Mockito.mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session).
                thenReturn(session).thenReturn(session).thenReturn(session);

        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(principal);

        when(this.userClient.getUserIdBySub(principal.getSub())).thenReturn(Response.ok().entity(userId).build());

        prepareMockParamForRefreshToken(tokensPlaceHolder, userClient);

        when(tokensPlaceHolder.getAccessToken()).thenReturn("token-yyz");

        when(this.tenantRoleClient.checkPermissions(userId, roleList, tenantId)).
                thenThrow(new TokenExpiredException()).
                thenThrow(new TokenExpiredException());
        SystemException se = assertThrows(SystemException.class,
                () -> authorizationChecker.hasGrantMultipleRoles(tenantId, roleList));
        assertTrue(se.getMessage().contains(TokenExpiredException.class.getName()));
    }

    /**
     * Test for method {@link AuthorizationChecker#hasGrantMultipleRoles(Long, List)}
     * Scenario: While it verifies if user has access to some role (in a given list) and tenant, the jwt access token expires.
     * When trying to perform refresh token operation (due some delay), the new access token expires.
     * Expected outcome: raise {@link SystemException}
     * @throws SystemException described on the signature for {@link AuthorizationChecker#hasGrantMultipleRoles(Long, List)}
     */
    @Test
    public void testHasGrantMultipleRoleWithTenantWhenTokenExpiresTwice() {
        Long userId = 1001L;
        Long tenantId = 1111L;

        List<String> roleList = new ArrayList<>();
        roleList.add(SystemRolesEnum.SYSTEM_ADMINISTRATOR.getRoleName());
        roleList.add(SystemRolesEnum.USER_ADMINISTRATOR.getRoleName());

        HttpSession session = Mockito.mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session).
                thenReturn(session).thenReturn(session).thenReturn(session);

        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(principal);
        when(this.userClient.getUserIdBySub(principal.getSub())).thenReturn(Response.ok().entity(userId).build());

        prepareMockParamForRefreshToken(tokensPlaceHolder, userClient);

        when(tokensPlaceHolder.getAccessToken()).thenReturn("token-yyz").thenReturn("token-yyz");

        when(this.tenantRoleClient.checkPermissions(userId, roleList, tenantId)).
                thenThrow(new TokenExpiredException()).
                thenThrow(new TokenExpiredException());

        SystemException se = assertThrows(SystemException.class,
                () -> authorizationChecker.hasGrantMultipleRoles(tenantId, roleList));
        assertTrue(se.getMessage().contains(TokenExpiredException.class.getName()));
    }

    /**
     * Test for method {@link AuthorizationChecker#hasGrantMultipleRoles(Long, List)}
     * Scenario: User has NO access to any role contained in a list (absolutely none of them)
     * Tenant Role API responds with some status different from success family (2xx)
     * Expected outcome: FALSE
     * @throws SystemException described on the signature for {@link AuthorizationChecker#hasGrant(Long, String)}
     */
    @Test
    public void testHasGrantMultipleRoleWithTenantReturnFalseDueHttpStatusNOK() throws SystemException{
        Long userId = 1001L;
        Long tenantId = 1111L;

        List<String> roleList = new ArrayList<>();
        roleList.add(SystemRolesEnum.SYSTEM_ADMINISTRATOR.getRoleName());
        roleList.add(SystemRolesEnum.USER_ADMINISTRATOR.getRoleName());

        HttpSession session = Mockito.mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session).
                thenReturn(session).thenReturn(session).thenReturn(session);

        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(principal);
        when(this.userClient.getUserIdBySub(principal.getSub())).thenReturn(Response.ok().entity(userId).build());

        prepareMockParamForRefreshToken(tokensPlaceHolder, userClient);

        when(tokensPlaceHolder.getAccessToken()).thenReturn("token-yyz").thenReturn("token-yyz");

        when(this.tenantRoleClient.checkPermissions(userId, roleList, tenantId)).
                thenReturn(Response.status(300).build());

        assertFalse(authorizationChecker.hasGrantMultipleRoles(tenantId, roleList));
    }

}
