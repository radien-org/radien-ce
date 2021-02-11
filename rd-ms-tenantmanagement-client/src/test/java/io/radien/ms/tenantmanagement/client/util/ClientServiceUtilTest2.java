//package io.radien.ms.usermanagement.client.util;
//
//import io.radien.ms.usermanagement.client.UserResponseExceptionMapper;
//import io.radien.ms.usermanagement.client.providers.UserMessageBodyWriter;
//import io.radien.ms.usermanagement.client.services.UserResourceClient;
//import junit.framework.TestCase;
//import org.eclipse.microprofile.rest.client.RestClientBuilder;
//import org.junit.Test;
//
//import java.net.URL;
//
//import static org.junit.Assert.*;
//
//public class ClientServiceUtilTest2 extends TestCase {
//
//    @Test
//    public void testGetUserResourceClient() {
//        URL url = new URL("http://localhost:9080/");
//        return RestClientBuilder.
//                newBuilder()
//                .baseUrl(url)
//                .register(UserResponseExceptionMapper.class)
//                .register(UserMessageBodyWriter.class)
//                .build(UserResourceClient.class);
//    }
//}