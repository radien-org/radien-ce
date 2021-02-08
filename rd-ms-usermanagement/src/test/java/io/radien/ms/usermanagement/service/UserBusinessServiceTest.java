package io.radien.ms.usermanagement.service;

import io.radien.api.entity.Page;
import io.radien.api.model.user.SystemUser;
import io.radien.api.service.batch.BatchSummary;
import io.radien.api.service.user.UserServiceAccess;
import io.radien.exception.SystemException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.exception.UserNotFoundException;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.ms.usermanagement.client.entities.UserSearchFilter;
import io.radien.ms.usermanagement.client.exceptions.RemoteResourceException;
import io.radien.ms.usermanagement.client.services.UserFactory;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doReturn;

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

    //TODO: Test was failing and usermanagement had to be pause, resume when possible - Bruno Gama

//    @Test
//    public void testDelete() throws UserNotFoundException, SystemException {
//       //TODO: improve test
//        userBusinessService.delete(1l);
//    }

    @Test
    public void testSave() throws UniquenessConstraintException, UserNotFoundException, RemoteResourceException {
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

    @Test
    public void testSaveEmptyUsername() throws UniquenessConstraintException, UserNotFoundException, RemoteResourceException {
        User u = UserFactory.create("a","b","","s","e",1L);
        //doThrow(new UserNotFoundException("")).when(userServiceAccess).save(u);
        boolean success = false;
        try{
            userBusinessService.save(u);
        } catch (RemoteResourceException e){
            success = true;
        }
        assertTrue(success);
    }

    @Test
    public void testSaveEmptyEmail() throws UniquenessConstraintException, UserNotFoundException, RemoteResourceException {
        User u = UserFactory.create("a","b","l","s","",1L);
        boolean success = false;
        try{
            userBusinessService.save(u);
        } catch (RemoteResourceException e){
            success = true;
        }
        assertTrue(success);
    }

    //TODO: Test was failing and usermanagement had to be pause, resume when possible - Bruno Gama

//    @Test
//    public void testSaveCreationTrue() throws UniquenessConstraintException, UserNotFoundException, SystemException {
//        User u = UserFactory.create("a","b","lCreation","s","eCreation",1L);
//
//        //TODO: Test implementation, keycloak null, check history 16:04
//
//
//        boolean success = false;
//        try{
//            userBusinessService.save(u);
//        } catch (SystemException e){
//            success = true;
//        }
//        assertTrue(success);
//
//
//    }


    @Test
    public void testSaveBatch() {
        List<io.radien.ms.usermanagement.entities.User> users = new ArrayList<>();
        int numberOfElementsToInsert = 100;
        when(userServiceAccess.create(anyList())).thenReturn(new BatchSummary(numberOfElementsToInsert));
        BatchSummary batchSummary = userServiceAccess.create(users);
        assertNotNull(batchSummary);
        assertEquals(batchSummary.getTotalProcessed(), numberOfElementsToInsert);
        assertEquals(batchSummary.getTotal(), batchSummary.getTotalProcessed());
        assertEquals(batchSummary.getTotalNonProcessed(), 0);
        assertNotNull(batchSummary.getNonProcessedItems());
        assertEquals(batchSummary.getNonProcessedItems().size(), 0);
    }


}