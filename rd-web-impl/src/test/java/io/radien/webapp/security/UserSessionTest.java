///*
// * Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.
// * <p>
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// * <p>
// * http://www.apache.org/licenses/LICENSE-2.0
// * <p>
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package io.radien.webapp.security;
//
//import io.radien.api.model.user.SystemUser;
//import io.radien.api.service.user.UserRESTServiceAccess;
//import io.radien.ms.usermanagement.client.entities.User;
//import jdk.nashorn.internal.runtime.options.Option;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.InjectMocks;
//import org.mockito.MockitoAnnotations;
//
//import javax.inject.Inject;
//
//import java.util.Optional;
//
//import static org.mockito.Mockito.when;
//
///**
// * User Session validation test case scenarios
// * {@link io.radien.webapp.security.UserSession}
// *
// * @author Bruno Gama
// **/
//public class UserSessionTest {
//
//    @InjectMocks
//    UserSession userSession;
//
//    @Inject
//    private UserRESTServiceAccess userClientService;
//
//    private User user;
//
//    @Before
//    public void before(){
//        MockitoAnnotations.initMocks(this);
//
//        user = new User();
//        user.setId(2L);
//        user.setSub("subject");
//        user.setUserEmail("email@email.com");
//        user.setFirstname("Bruno");
//        user.setLastname("Gama");
//        user.setLogon("bgama");
//    }
//
//    @Test
//    public void testLogin() throws Exception {
//        userSession.login(user.getSub(), user.getUserEmail(), user.getLogon(), user.getFirstname(), user.getLastname(), "accessToken", "refrehToken");
//
//        Optional<SystemUser> opt =
//        when(userClientService.getUserBySub(user.getSub())).thenReturn()
//    }
//
//    public void testGetUser() {
//    }
//
//    public void testGetUserId() {
//
//    }
//
//    public void testIsActive() {
//    }
//
//    public void testGetUserIdSubject() {
//    }
//
//    public void testGetEmail() {
//    }
//
//    public void testGetPreferredUserName() {
//    }
//
//    public void testGetUserFirstName() {
//    }
//
//    public void testGetUserLastName() {
//    }
//
//    public void testGetUserFullName() {
//    }
//
//    public void testGetOAF() {
//    }
//
//    public void testGetAccessToken() {
//    }
//
//    public void testSetAccessToken() {
//    }
//
//    public void testGetRefreshToken() {
//    }
//
//    public void testSetRefreshToken() {
//    }
//
//    public void testGetSelectedUser() {
//    }
//
//    public void testSetSelectedUser() {
//    }
//
//    public void testIsValidationTrue() {
//    }
//
//    public void testSetValidationTrue() {
//    }
//}