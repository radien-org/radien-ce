package io.radien.ms.openid.security;

import io.radien.api.security.TokensPlaceHolder;
import io.radien.exception.SystemException;
import io.radien.ms.openid.client.LinkedAuthorizationClient;
import io.radien.ms.openid.client.UserClient;
import io.radien.ms.openid.client.exception.NotFoundException;
import io.radien.ms.openid.entities.Principal;
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

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
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
}
