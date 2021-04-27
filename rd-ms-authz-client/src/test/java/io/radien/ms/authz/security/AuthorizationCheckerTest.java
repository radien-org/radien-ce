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
package io.radien.ms.authz.security;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.security.TokensPlaceHolder;
import io.radien.exception.SystemException;
import io.radien.exception.TokenExpiredException;
import io.radien.ms.authz.client.LinkedAuthorizationClient;
import io.radien.ms.authz.client.UserClient;
import io.radien.ms.authz.client.exception.NotFoundException;
import io.radien.ms.openid.entities.Principal;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

public class AuthorizationCheckerTest {

    @InjectMocks
    private AuthorizationChecker authorizationChecker = new AuthorizationChecker() {};

    @Mock
    private HttpServletRequest servletRequest;

    @Mock
    private UserClient userClient;

    @Mock
    private LinkedAuthorizationClient linkedAuthorizationClient;

    @Mock
    private TokensPlaceHolder tokensPlaceHolder;

    @Mock
    OAFAccess oafAccess;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRefreshToken() {
        String refreshToken = "11122334445566";
        String newAccessToken = "33333333333";

        when(tokensPlaceHolder.getRefreshToken()).thenReturn(refreshToken).
                thenReturn(refreshToken).
                thenReturn(refreshToken);

        when(this.userClient.refreshToken(refreshToken)).
                thenReturn(Response.ok().entity(newAccessToken).build()).
                thenReturn(Response.status(Response.Status.NOT_FOUND).build()).
                thenThrow(new javax.ws.rs.ProcessingException("error"));

        try {
            assertTrue(authorizationChecker.refreshToken());
        } catch (SystemException systemException) {
            fail("unexpected failure");
        }

        try {
            assertFalse(authorizationChecker.refreshToken());
        } catch (SystemException systemException) {
            fail("unexpected failure");
        }

        SystemException se = assertThrows(SystemException.class, () ->
                authorizationChecker.refreshToken());
        assertTrue(se.getMessage().contains(ProcessingException.class.getName()));
    }

    @Test
    public void testHasGrantForRoleReturnTrue() {
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
        when(this.linkedAuthorizationClient.isRoleExistentForUser(userId, roleName, null)).
                thenReturn(Response.ok().entity(Boolean.TRUE).build());
        when(tokensPlaceHolder.getAccessToken()).thenReturn("token-yyz");


        try {
            assertTrue(authorizationChecker.hasGrant(roleName));
        } catch (SystemException systemException) {
            fail("unexpected failure");
        }
    }

    @Test
    public void testHasGrantForRoleWithTenantParamReturnTrue() {
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
        when(this.linkedAuthorizationClient.isRoleExistentForUser(userId, roleName, tenantId)).
                thenReturn(Response.ok().entity(Boolean.TRUE).build());
        when(tokensPlaceHolder.getAccessToken()).thenReturn("token-yyz");

        try {
            assertTrue(authorizationChecker.hasGrant(tenantId, roleName));
        } catch (SystemException systemException) {
            fail("unexpected failure");
        }
    }

    protected void prepareMockParamForRefreshToken(TokensPlaceHolder holder, UserClient uc) {
        String refreshToken = "11122334445566";
        String newAccessToken = "33333333333";

        when(holder.getRefreshToken()).thenReturn(refreshToken).
                thenReturn(refreshToken).
                thenReturn(refreshToken);

        when(uc.refreshToken(refreshToken)).
                thenReturn(Response.ok().entity(newAccessToken).build()).
                thenReturn(Response.ok().entity(newAccessToken).build()).
                thenReturn(Response.ok().entity(newAccessToken).build());
    }

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

        when(servletRequest.getSession()).
                thenReturn(session).
                thenReturn(session);

        when(servletRequest.getSession(false)).
                thenReturn(session).
                thenReturn(session);

        when(session.getAttribute("USER")).
                thenReturn(principal).
                thenReturn(principal);

        when(this.userClient.getUserIdBySub(principal.getSub())).
                thenReturn(Response.ok().entity(userId).build()).
                thenReturn(Response.ok().entity(userId).build());

        prepareMockParamForRefreshToken(tokensPlaceHolder, userClient);

        when(tokensPlaceHolder.getAccessToken()).thenReturn("token-yyz").
                thenReturn("token-yyz");

        when(this.linkedAuthorizationClient.isRoleExistentForUser(userId, roleName, tenantId)).
                thenThrow(new TokenExpiredException()).
                thenReturn(Response.ok().entity(Boolean.TRUE).build()).
                thenThrow(new TokenExpiredException()).
                thenThrow(new TokenExpiredException());

        try {
            assertTrue(authorizationChecker.hasGrant(tenantId, roleName));
        } catch (SystemException systemException) {
            fail("unexpected failure");
        }

        SystemException se = assertThrows(SystemException.class,
                () -> authorizationChecker.hasGrant(tenantId, roleName));

        assertTrue(se.getMessage().contains(TokenExpiredException.class.getName()));
    }

    @Test
    public void testHasGrantForRoleReturnFalse() {
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
        when(this.linkedAuthorizationClient.isRoleExistentForUser(userId, roleName, null)).
                thenReturn(Response.ok().entity(Boolean.FALSE).build());
        when(tokensPlaceHolder.getAccessToken()).thenReturn("token-yyz");


        try {
            assertFalse(authorizationChecker.hasGrant(roleName));
        } catch (SystemException systemException) {
            fail("unexpected failure");
        }
    }

    @Test
    public void testHasGrantForRoleWithTenantParamReturnFalse() {
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
        when(this.linkedAuthorizationClient.isRoleExistentForUser(userId, roleName, tenantId)).
                thenReturn(Response.ok().entity(Boolean.FALSE).build());
        when(tokensPlaceHolder.getAccessToken()).thenReturn("token-yyz");

        try {
            assertFalse(authorizationChecker.hasGrant(tenantId, roleName));
        } catch (SystemException systemException) {
            fail("unexpected failure");
        }
    }

    @Test
    public void testHasGrantForRoleWithTenantParamReturnFalse2() {
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
        when(this.linkedAuthorizationClient.isRoleExistentForUser(userId, roleName, tenantId)).
                thenReturn(Response.status(300).build());
        when(tokensPlaceHolder.getAccessToken()).thenReturn("token-yyz");

        try {
            assertFalse(authorizationChecker.hasGrant(tenantId, roleName));
        } catch (SystemException systemException) {
            fail("unexpected failure");
        }
    }

    @Test
    public void testFailureWhenRetrievingUserId() {
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

        when(tokensPlaceHolder.getAccessToken()).thenReturn("token-yyz");
        when(this.userClient.getUserIdBySub(principal.getSub())).
                thenThrow(NotFoundException.class).
                thenThrow(RuntimeException.class);

        assertThrows(SystemException.class, () -> authorizationChecker.hasGrant(tenantId, roleName));
        assertThrows(SystemException.class, () -> authorizationChecker.hasGrant(tenantId, roleName));
    }

    @Test
    public void testTokenExpiredWhenRetrievingUserId() throws SystemException {
        Long userId = 1001L;
        String roleName = "admin";

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
                thenReturn(Response.ok().entity(userId).build()).
                thenThrow(TokenExpiredException.class);

        this.prepareMockParamForRefreshToken(tokensPlaceHolder, this.userClient);

        assertEquals(userId, authorizationChecker.getCurrentUserIdBySub(principal.getSub()));

        SystemException se = assertThrows(SystemException.class,
                () -> authorizationChecker.getCurrentUserIdBySub(principal.getSub()));

        assertTrue(se.getMessage().contains(TokenExpiredException.class.getName()));
    }

    @Test
    public void testFailureWhenNoInvokerUserIsPresent() {
        Long userId = 1001L;
        Long tenantId = 1111L;
        String roleName = "admin";

        HttpSession session = Mockito.mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);

        when(tokensPlaceHolder.getAccessToken()).thenReturn("token-yyz");

        assertThrows(SystemException.class, () -> authorizationChecker.hasGrant(tenantId, roleName));
    }

    @Test
    public void testGrantForPermissionExists() {
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
        when(this.linkedAuthorizationClient.existsSpecificAssociation(tenantId, permissionId, null, userId, true)).
                thenReturn(Response.ok().build());
        when(tokensPlaceHolder.getAccessToken()).thenReturn("token-yyz");

        try {
            assertTrue(authorizationChecker.hasGrant(permissionId, tenantId));
        } catch (SystemException systemException) {
            fail("unexpected failure");
        }
    }

    @Test
    public void testGrantForPermissionNotExist() {
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
        when(this.linkedAuthorizationClient.existsSpecificAssociation(tenantId, permissionId, null, userId, true)).
                thenThrow(NotFoundException.class);
        when(tokensPlaceHolder.getAccessToken()).thenReturn("token-yyz");

        try {
            assertFalse(authorizationChecker.hasGrant(permissionId, tenantId));
        } catch (SystemException systemException) {
            fail("unexpected failure");
        }
    }

    @Test
    public void testFailureWhenCheckingGrantForPermission() {
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
        when(this.linkedAuthorizationClient.existsSpecificAssociation(tenantId, permissionId, null, userId, true)).
                thenThrow(RuntimeException.class);
        when(tokensPlaceHolder.getAccessToken()).thenReturn("token-yyz");

        assertThrows(SystemException.class, () -> authorizationChecker.hasGrant(permissionId, tenantId));
    }

    @Test
    public void testTokenExpiredExceptionWhenCheckingGrantForPermission() {
        Long userId = 1001L;
        Long tenantId = 22L;
        Long permissionId = 1L;

        HttpSession session = Mockito.mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);

        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");

        when(servletRequest.getSession()).
                thenReturn(session).
                thenReturn(session);

        when(servletRequest.getSession(false)).
                thenReturn(session).
                thenReturn(session);

        when(session.getAttribute("USER")).thenReturn(principal).
                thenReturn(principal);

        when(this.userClient.getUserIdBySub(principal.getSub())).
                thenReturn(Response.ok().entity(userId).build()).
                thenReturn(Response.ok().entity(userId).build());

        when(this.linkedAuthorizationClient.existsSpecificAssociation(tenantId,
                permissionId, null, userId, true)).
                thenThrow(TokenExpiredException.class).
                thenReturn(Response.ok().build()).
                thenThrow(TokenExpiredException.class).
                thenThrow(TokenExpiredException.class);

        when(tokensPlaceHolder.getAccessToken()).thenReturn("token-yyz").
                thenReturn("token-yyz");

        this.prepareMockParamForRefreshToken(tokensPlaceHolder, userClient);

        try {
            assertTrue(authorizationChecker.hasGrant(permissionId, tenantId));
        } catch (SystemException systemException) {
            fail("unexpected failure");
        }
        SystemException se = assertThrows(SystemException.class,
                () -> authorizationChecker.hasGrant(permissionId, tenantId));
        assertTrue(se.getMessage().contains(TokenExpiredException.class.getName()));
    }

    @Test
    public void testTokensPlaceHolderEmpty() {

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
        when(this.linkedAuthorizationClient.isRoleExistentForUser(userId, roleName, null)).
                thenReturn(Response.ok().entity(Boolean.TRUE).build());

        try {
            assertTrue(authorizationChecker.hasGrant(roleName));
        } catch (SystemException systemException) {
            fail("unexpected failure");
        }
    }

    @Test
    public void testTokensPlaceHolderEmpty2() {

        Long userId = 1001L;
        String roleName = "admin";

        HttpSession session = Mockito.mock(HttpSession.class);
        session.setAttribute("accessToken", "57689405040406094950606050606060nnnc");

        when(servletRequest.getHeader(HttpHeaders.AUTHORIZATION)).
                thenReturn("Bearer 57689405040406094950606050606060nnnc");

        Principal principal = new Principal();
        principal.setSub("aaa-bbb-ccc-ddd");

        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getSession(false)).thenReturn(session);
        when(session.getAttribute("USER")).thenReturn(principal);

        when(this.userClient.getUserIdBySub(principal.getSub())).thenReturn(Response.ok().entity(userId).build());

        when(this.linkedAuthorizationClient.isRoleExistentForUser(userId, roleName, null)).
                thenReturn(Response.ok().entity(Boolean.TRUE).build());

        try {
            assertTrue(authorizationChecker.hasGrant(roleName));
        } catch (SystemException systemException) {
            fail("unexpected failure");
        }
    }

    @Test
    public void testGetUserClientWithError() throws SystemException {
        AuthorizationChecker spied = Mockito.spy(AuthorizationChecker.class);

        RestClientBuilder builder = mock(RestClientBuilder.class);
        doThrow(new RuntimeException()).when(builder).build(UserClient.class);
        doReturn(builder).when(spied).getRestClientBuilder();

        OAFAccess oaf = mock(OAFAccess.class);
        doReturn("http://localhost:8080").when(oaf).
                getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_USERMANAGEMENT);
        doReturn(oaf).when(spied).getOafAccess();

        assertThrows(SystemException.class, () -> spied.getUserClient());
    }

    @Test
    public void testGetLinkedAuthorizationClientWithError() throws SystemException {
        AuthorizationChecker spied = Mockito.spy(AuthorizationChecker.class);

        RestClientBuilder builder = mock(RestClientBuilder.class);
        doThrow(new RuntimeException()).when(builder).build(LinkedAuthorizationClient.class);
        doReturn(builder).when(spied).getRestClientBuilder();

        OAFAccess oaf = mock(OAFAccess.class);
        doReturn("http://localhost:8080").when(oaf).
                getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT);
        doReturn(oaf).when(spied).getOafAccess();

        assertThrows(SystemException.class, () -> spied.getLinkedAuthorizationClient());
    }

    @Test
    public void testGetRestClientBuilder() throws SystemException {
        AuthorizationChecker spied = Mockito.spy(AuthorizationChecker.class);
        RestClientBuilder rcb = spied.getRestClientBuilder();
        assertNotNull(rcb);
    }

    @Test
    public void testRefreshTokenFalseResponse() throws SystemException {
        when(userClient.refreshToken(any())).thenReturn(Response.notModified().entity("test").build());

        assertFalse(authorizationChecker.refreshToken());
    }

    @Test
    public void testRefreshTokenException() throws SystemException {
        when(userClient.refreshToken(any())).thenThrow(new TokenExpiredException());

        boolean success =false;
        try {
            authorizationChecker.refreshToken();
        } catch (SystemException systemException) {
            success = true;
        }

        assertTrue(success);
    }
}
