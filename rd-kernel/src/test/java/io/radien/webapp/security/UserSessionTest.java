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
package io.radien.webapp.security;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.kernel.messages.SystemMessages;
import io.radien.api.model.user.SystemUser;
import io.radien.ms.usermanagement.client.entities.User;

import io.radien.api.service.user.UserRESTServiceAccess;

import io.radien.exception.SystemException;

import io.radien.ms.usermanagement.client.services.UserFactory;

import io.radien.webapp.JSFUtil;

import java.io.IOException;

import java.util.Objects;
import java.util.Optional;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.http.HttpSession;
import org.eclipse.microprofile.config.Config;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
/**
 * Class that aggregates UnitTest cases for UserSession
 *
 */


public class UserSessionTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @InjectMocks
    private UserSession userSession;

    @Mock
    private OAFAccess oaf;

    @Mock
    private UserRESTServiceAccess userRESTServiceAccess;

    @Mock
    private Config config;

    private static FacesContext facesContext;

    SystemUser user;

    /**
     * Prepares mock objects
     */
    @BeforeClass
    public static void setUp() {

        Mockito.mockStatic(FacesContext.class);
        Mockito.mockStatic(JSFUtil.class);
        Mockito.mockStatic(UserFactory.class);
        facesContext = mock(FacesContext.class);

        initialize();
    }

    private static void initialize() {
        ExternalContext externalContext = mock(ExternalContext.class);
        Flash flash = mock(Flash.class);
        when(FacesContext.getCurrentInstance()).thenReturn(facesContext);
        when(facesContext.getExternalContext()).thenReturn(externalContext);
        when(externalContext.getFlash()).thenReturn(flash);
        when(JSFUtil.getFacesContext()).thenReturn(facesContext);
        when(JSFUtil.getExternalContext()).thenReturn(externalContext);
        when(JSFUtil.getMessage(anyString())).thenAnswer(i -> i.getArguments()[0]);
    }

    @Before
    public void setUpEach(){
        user = new User();
        user.setId(1L);
        user.setUserEmail("myemail@email.com");
        user.setSub("abcd-1234");
        user.setEnabled(true);
        user.setFirstname("my");
        user.setLastname("email");
        user.setMobileNumber("12345678");

        userSession.setUser(user);
        userSession.setLanguage("Portuguese");

    }

    /**
     * Test method for{@link UserSession #login(String, String, String, String, String, String, String)}
     * for an existing user
     * @throws Exception if any error
     */
    @Test
    public void testLoginExistingUser() throws Exception {
        Optional<SystemUser> optionalSystemUser = Optional.of(mock(SystemUser.class));
        when(userRESTServiceAccess.getUserBySub(anyString())).thenReturn(optionalSystemUser);
        userSession.login("sub", "myemail@email.com", "myname", "name",
                "my", "951", "acc-token","re-token" );

        assertNotNull(userSession.getUser());

    }

    /**
     * Test method for {@link UserSession #login(String, String, String, String, String, String, String)}
     * assuming user is not exists
     * @throws Exception if any error
     */
    @Test
    public void testLoginNotExistedAnUser() throws Exception {
        when(UserFactory.create(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyLong(), anyBoolean())).thenReturn((User) user );
        userSession.login("sub", "myemail@email.com", "myname", "name",
                "my", "951", "acc-token","re-token" );

        assertNotNull(userSession.getUser());
    }

    /**
     * Test method for {@link UserSession #login(String, String, String, String, String, String, String)}
     * assuming user is null
     * @throws Exception if any error
     */
    @Test
    public void testLoginNullUser() throws Exception {
        when(UserFactory.create(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyLong(), anyBoolean())).thenReturn(null);
        userSession.login("sub", "myemail@email.com", "myname", "name",
                "my", "951", "acc-token","re-token" );

        assertNull(userSession.getUser());
    }

    /**
     * Test method for {@link UserSession #login(String, String, String, String, String, String, String)}
     * asserts exception
     * @throws Exception if any error
     */
    @Test(expected = Exception.class)
    public void testLoginWithException() throws Exception {
        when(userRESTServiceAccess.getUserBySub(anyString())).thenThrow(SystemException.class);
        userSession.login("sub", "myemail@email.com", "myname", "name",
                "my", "951", "acc-token","re-token" );

    }

    /**
     * Test method for {@link UserSession#getUser()}
     */
    @Test
    public void testGetUser(){
        assertNotNull(userSession.getUser());
    }

    /**
     * Test method for
     * {@link UserSession#getUserId()}
     */
    @Test
    public void testGetUserId(){
        assertNotNull(userSession.getUserId());
    }

    /**
     * Test method for
     * {@link UserSession#getUserId()}
     */
    @Test
    public void testGetUserIdSubject(){
        assertNotNull(userSession.getUserIdSubject());
    }

    /**
     * Test method for
     * {@link UserSession#getEmail()}
     */
    @Test
    public void testGetEmail(){
        assertNotNull(userSession.getEmail());
    }

    /**
     * Test method for
     * {@link UserSession#getUserFirstName()}
     */
    @Test
    public void testGetUserFirstName(){
        assertNotNull(userSession.getUserFirstName());
    }

    /**
     * Test method for
     * {@link UserSession#getMobileNumber()}
     */
    @Test
    public void testGetMobileNumber(){
        assertNotNull(userSession.getMobileNumber());
    }

    /**
     * Test method for
     * {@link UserSession#getUserLastName()}
     */
    @Test
    public void testGetUserLastName(){
        assertNotNull(userSession.getUserLastName());
    }

    /**
     * Test method for
     * {@link UserSession#getUserFullName()}
     */
    @Test
    public void testGetUserFullName(){
        assertNotNull(userSession.getUserFullName());
    }

    /**
     * Test method for
     * {@link UserSession#getLanguage()}
     */
    @Test
    public void testGetLanguage(){
        assertNotNull(userSession.getLanguage());
    }

    /**
     * Test method for
     * {@link UserSession#getPreferredUserName()}
     */
    @Test
    public void testGetPreferredUserName(){
        assertNotNull(userSession.getPreferredUserName());

        SystemUser user1 = new User();
        user.setId(1L);
        user.setLogon("my-logOn");
        userSession.setUser(user1);
        assertNull(userSession.getPreferredUserName());

        userSession.setUser(user);
        assertNotNull(userSession.getPreferredUserName());
    }

    /**
     * Test method for
     * {@link UserSession#isActive()}
     */
    @Test
    public void testIsActive(){
        assertTrue(userSession.isActive());

        userSession.setUser(null);
        assertFalse(userSession.isActive());
    }

    /**
     * Test method for
     * {@link UserSession#getOAF()}
     */
    @Test
    public void testGetOAF(){
        assertNotNull(userSession.getOAF());
    }

    /**
     * Test method for
     * {@link UserSession#getAccessToken()}
     * {@link UserSession#setAccessToken(String)}
     */
    @Test
    public void testGetterSetterAccessToken(){
        userSession.setAccessToken("access-token");
        assertEquals("access-token", userSession.getAccessToken());
    }

    /**
     * Test method for
     * {@link UserSession#getRefreshToken()}
     * {@link UserSession#setRefreshToken(String)}
     */
    @Test
    public void testGetterSetterRefreshToken(){
        userSession.setRefreshToken("refresh-token");
        assertEquals("refresh-token", userSession.getRefreshToken());
    }

    /**
     * Test method for
     * {@link UserSession#getUser()}
     * {@link UserSession#setUser(SystemUser)}
     */
    @Test
    public void testGetterSetterUser(){
        userSession.setUser(new User());
        assertNotNull(userSession.getUser());
    }

    /**
     * Test for method {@link UserSession#logout()}
     * @throws ServletException in case of i/o error during request.logout
     * @throws IOException in case of i/o error when performing sendRedirect
     */
    @Test
    public void testLogout() throws ServletException, IOException {
        SystemUser user = mock(SystemUser.class);
        when(user.isEnabled()).thenReturn(Boolean.TRUE);
        userSession.setUser(user);

        String host = oaf.getProperty(OAFProperties.SYS_HOSTNAME);
        String context = oaf.getProperty(OAFProperties.SYS_APPLICATION_CONTEXT);

        when(oaf.getProperty(OAFProperties.SYS_HOSTNAME)).
                thenReturn(SystemMessages.KERNEL_PROPERTY_UNAVAILABLE.message() +
                        OAFProperties.SYS_HOSTNAME.propKey());

        when(oaf.getProperty(OAFProperties.SYS_APPLICATION_CONTEXT)).
                thenReturn(SystemMessages.KERNEL_PROPERTY_UNAVAILABLE.message() +
                        OAFProperties.SYS_APPLICATION_CONTEXT.propKey());

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getScheme()).thenReturn("https");
        when(request.getServerName()).thenReturn("localhost");
        when(request.getServerPort()).thenReturn(8443);
        when(request.getContextPath()).thenReturn("/web");

        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

        initialize();
        when(Objects.requireNonNull(JSFUtil.getExternalContext()).getRequest()).thenReturn(request);
        when(JSFUtil.getExternalContext().getResponse()).thenReturn(response);
        doNothing().when(request).logout();
        doNothing().when(session).invalidate();

        when(config.getValue("IDP_LOGOUT_URL_PATTERN", String.class)).thenReturn("https://domain.io");
        when(config.getValue("SCRIPT_CLIENT_ID_VALUE", String.class)).thenReturn("realmtest");

        when(request.toString()).thenReturn("https://localhost:8443/web/public/index.oaf");
        when(request.getServletPath()).thenReturn("/public/index.oaf");
        doNothing().when(response).sendRedirect(anyString());

        Assert.assertTrue(userSession.logout());
    }

    /**
     * Test for method {@link UserSession#logout()}
     * Scenario: External context could not be loaded
     * @throws ServletException in case of i/o error during request.logout
     * @throws IOException in case of i/o error when performing sendRedirect
     */
    @Test
    public void testLogoutWhenExternalContextNull() throws ServletException, IOException {
        SystemUser user = mock(SystemUser.class);
        when(user.isEnabled()).thenReturn(Boolean.TRUE);
        userSession.setUser(user);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        when(JSFUtil.getExternalContext()).thenReturn(null);
        doNothing().when(request).logout();
        doNothing().when(session).invalidate();
        assertFalse(userSession.logout());
    }

    /**
     * Test for method {@link UserSession#logout()} when there is no user active
     */
    @Test
    public void testLogoutWhenNoUserActive() {
        SystemUser user = mock(SystemUser.class);
        when(user.isEnabled()).thenReturn(Boolean.FALSE);
        userSession.setUser(user);
        Assert.assertFalse(userSession.logout());
    }

    /**
     * Test for method {@link UserSession#logout()}
     * @throws ServletException in case of i/o error during request.logout
     */
    @Test
    public void testLogoutWhenExceptionOccurs() throws ServletException {
        SystemUser user = mock(SystemUser.class);
        when(user.isEnabled()).thenReturn(Boolean.TRUE);
        userSession.setUser(user);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(Objects.requireNonNull(JSFUtil.getExternalContext()).getRequest()).thenReturn(request);
        when(JSFUtil.getExternalContext().getResponse()).thenReturn(response);
        doThrow(new ServletException("error")).when(request).logout();
        Assert.assertFalse(userSession.logout());
    }
}
