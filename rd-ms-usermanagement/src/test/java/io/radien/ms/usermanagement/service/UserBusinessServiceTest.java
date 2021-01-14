package io.radien.ms.usermanagement.service;

import io.radien.api.entity.Page;
import io.radien.api.model.user.SystemUser;
import io.radien.api.service.user.UserServiceAccess;
import io.radien.exception.UniquenessConstraintException;
import io.radien.exception.UserNotFoundException;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.ms.usermanagement.client.entities.UserSearchFilter;
import io.radien.ms.usermanagement.client.services.UserFactory;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class UserBusinessServiceTest extends TestCase {

    @InjectMocks
    UserBusinessService userBusinessService;

    @Mock
    UserServiceAccess userServiceAccess;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAll() {
        String search = "";
        Page<SystemUser> p = new Page<>(new ArrayList<>(),-1,0,0);
        when(userServiceAccess.getAll(search,1,2,null,true)).thenReturn(p);
        Page<? extends SystemUser> result = userBusinessService.getAll(search,1,2,null,true);
        assertEquals(p,result);
    }

    @Test
    public void testGetUsers() {
        UserSearchFilter filter = new UserSearchFilter();
        when(userServiceAccess.getUsers(filter)).thenReturn(new ArrayList<>());
        List<? extends SystemUser> results = userBusinessService.getUsers(filter);
        assertEquals(0,results.size());
    }

    @Test
    public void testGet() throws UserNotFoundException {
        SystemUser user = UserFactory.create("a","b","l","s","e",1L);
        List<Long> listIds = Collections.singletonList(2L);
        List<SystemUser> listUsers = Collections.singletonList(user);
        when(userServiceAccess.get(2L)).thenReturn(user);
        when(userServiceAccess.get(listIds)).thenReturn(listUsers);
        SystemUser result = userBusinessService.get(2L);
        List<SystemUser> results = userBusinessService.get(listIds);
        assertEquals(user,result);
        assertEquals(listUsers,results);
    }

    @Test
    public void testDelete() {
       userBusinessService.delete(1l);
    }

    @Test
    public void testSave() throws UniquenessConstraintException, UserNotFoundException {
        User u = UserFactory.create("a","b","l","s","e",1L);
        doThrow(new UserNotFoundException("")).when(userServiceAccess).save(u);
        boolean success = false;
        try{
            userBusinessService.save(u);
        } catch (UserNotFoundException e){
            success = true;
        }
        assertTrue(success);
    }
}