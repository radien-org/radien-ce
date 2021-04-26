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
package io.radien.webapp.authz;

import io.radien.api.model.user.SystemUser;
import io.radien.api.security.TokensPlaceHolder;
import io.radien.api.security.UserSessionEnabled;
import io.radien.exception.SystemException;
import io.radien.ms.authz.client.LinkedAuthorizationClient;
import io.radien.ms.authz.client.UserClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

public class WebAuthorizationCheckerTest {

    @InjectMocks
    private WebAuthorizationChecker webAuthorizationChecker;

    @Mock
    private HttpServletRequest servletRequest;

    @Mock
    private UserClient userClient;

    @Mock
    private LinkedAuthorizationClient linkedAuthorizationClient;

    @Mock
    private TokensPlaceHolder tokensPlaceHolder;

    @Mock
    UserSessionEnabled userSession;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetCurrentUserId() throws SystemException {
        Long userId = 22L;
        when(this.userSession.getUserId()).thenReturn(userId);
        try {
            assertEquals(webAuthorizationChecker.getCurrentUserId(), userId);
        }
        catch (Exception e) {
            fail("unexpected");
        }
    }

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

    @Test
    public void testGetInvokerUser() {
        when(userSession.getUserFirstName()).thenReturn("name");
        when(userSession.getUserLastName()).thenReturn("last-name");
        when(userSession.getPreferredUserName()).thenReturn("user.name");
        when(userSession.getUserIdSubject()).thenReturn("sub-1");
        when(userSession.getEmail()).thenReturn("user.name@gmail.com");

        SystemUser u = this.webAuthorizationChecker.getInvokerUser();

        assertEquals(u.getFirstname(), "name");
        assertEquals(u.getLastname(), "last-name");
        assertEquals(u.getLogon(), "user.name");
        assertEquals(u.getSub(), "sub-1");
        assertEquals(u.getUserEmail(), "user.name@gmail.com");
    }

    @Test
    public void testHasGrant() {
        Long userId = 22L;
        Long tenantId = null;
        String role1 = "tenant-administrator";
        String role2 = "tenant-extreme";

        when(this.tokensPlaceHolder.getAccessToken()).thenReturn("142517271828");
        when(this.userSession.getUserId()).thenReturn(userId);

        when(this.linkedAuthorizationClient.isRoleExistentForUser(userId, role1, tenantId)).
                thenReturn(Response.ok().entity(Boolean.TRUE).build()).
                thenThrow(new RuntimeException("test"));

        when(this.linkedAuthorizationClient.isRoleExistentForUser(userId, role2, tenantId)).
                thenReturn(Response.ok().entity(Boolean.FALSE).build());

        assertTrue(this.webAuthorizationChecker.hasGrant(tenantId, role1));
        assertFalse(this.webAuthorizationChecker.hasGrant(tenantId, role1));
        assertFalse(this.webAuthorizationChecker.hasGrant(tenantId, role2));
    }
}
