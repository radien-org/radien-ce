//package io.radien.ms.usermanagement.service;
//
//import io.radien.exception.SystemException;
//import io.radien.ms.usermanagement.entities.User;
//import junit.framework.TestCase;
//
//import java.util.Optional;
//
//import static org.junit.Assert.*;
//
//public class KeycloakClientTest extends TestCase {
//
//    //TODO: Test was failing and usermanagement had to be pause, resume when possible - Bruno Gama
//
//    public void testKeycloakClient(){
//        User u = new User();
//        u.setLogon("abc");
//        KeycloakClient client = new KeycloakClient()
//                .clientId("admin-cli")
//                .username("adminsantana")
//                .password("NM7uR6ybEx3eu3J")
//                .idpUrl("https://idp-int.radien.io")
//                .tokenPath("/auth/realms/master/protocol/openid-connect/token")
//                .userPath("/auth/admin/realms/radien/users");
//        try {
//            client.login();
//            String id = client.createUser(KeycloakFactory.convertToUserRepresentation(u));
//            System.out.println(id);
//        } catch (SystemException e){
//            e.printStackTrace();
//        }
//    }
//}