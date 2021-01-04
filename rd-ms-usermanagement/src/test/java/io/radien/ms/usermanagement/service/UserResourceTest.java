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
package io.radien.ms.usermanagement.service;

import io.radien.ms.usermanagement.client.exceptions.InvalidRequestException;
import io.radien.ms.usermanagement.client.exceptions.NotFoundException;

import io.radien.ms.usermanagement.entities.User;
import org.junit.Before;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Response;
import java.net.MalformedURLException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class UserResourceTest  {

    @InjectMocks
    UserResource userResource;
    @Mock
    UserService userService;

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }
/*
    public void manualTestClient(){
        URI baseURI = URI.create("http://localhost:9080/rd-ms-usermanagement");
        UserResourceClient client = RestClientBuilder.newBuilder().
                baseUri(baseURI).
                build(UserResourceClient.class);
        assertNotNull(client);
        Response result = client.getAll(null, null, null, 1,2,null,false,true);
        assertEquals(Response.Status.OK.getStatusCode(),result.getStatus());
        assertNotNull(result);
    }
*/
    @Test
    public void testGetAll() throws MalformedURLException {
        Response response = userResource.getAll(null,null,null,1,2,null,true,true);
        assertEquals(200,response.getStatus());
    }

    @Test
    public void testGetAllGenericException() throws MalformedURLException {
        when(userService.getAll(null,null,null,1,2,null,true,true))
                .thenThrow(new RuntimeException());
        Response response = userResource.getAll(null,null,null,1,2,null,true,true);
        assertEquals(500,response.getStatus());
    }


    @Test
    public void testGetById404() {
        Response response = userResource.getById(1L);
        assertEquals(404,response.getStatus());
    }

    @Test
    public void testGetById() throws NotFoundException {
        when(userService.get(1L)).thenReturn(new User());
        Response response = userResource.getById(1L);
        assertEquals(200,response.getStatus());
    }

    @Test
    public void testGetByIdGenericException() throws NotFoundException {
        when(userService.get(1L)).thenThrow(new RuntimeException());
        Response response = userResource.getById(1L);
        assertEquals(500,response.getStatus());
    }

    @Test
    public void testUpdateUser() throws InvalidRequestException {
        Response response = userResource.updateUser(1l,new User());
        assertEquals(200,response.getStatus());
    }

    @Test
    public void testUpdateUserNotFoundException() throws NotFoundException, InvalidRequestException {
        User user = new User();
        doThrow(new NotFoundException()).when(userService).update(1L, user);
        Response response = userResource.updateUser(1L, user);
        assertEquals(404,response.getStatus());
    }

    @Test
    public void testUpdateUserInvalidRequestException() throws InvalidRequestException, NotFoundException {
        User user = new User();
        doThrow(new InvalidRequestException()).when(userService).update(1L, user);
        Response response = userResource.updateUser(1L, user);
        assertEquals(400,response.getStatus());
    }

    @Test
    public void testUpdateUserGenericError() throws Exception {
        User u = new User();
        doThrow(new RuntimeException()).when(userService).update(1l,u);
        Response response = userResource.updateUser(1l,u);
        assertEquals(500,response.getStatus());
    }

    @Test
    public void testDelete() {
        Response response = userResource.delete(1l);
        assertEquals(200,response.getStatus());
    }
    @Test
    public void testDeleteGenericError() {
        doThrow(new RuntimeException()).when(userService).delete(1l);
        Response response = userResource.delete(1l);
        assertEquals(500,response.getStatus());
    }

    @Test
    public void testCreate() {
        Response response = userResource.create(new User());
        assertEquals(200,response.getStatus());
    }

    @Test
    public void testCreateInvalid() throws InvalidRequestException {
        User u = new User();
        doThrow(new InvalidRequestException()).when(userService).save(u);
        Response response = userResource.create(u);
        assertEquals(400,response.getStatus());
    }

    @Test
    public void testCreateGenericError() throws InvalidRequestException {
        User u = new User();
        doThrow(new RuntimeException()).when(userService).save(u);
        Response response = userResource.create(u);
        assertEquals(500,response.getStatus());
    }
}
