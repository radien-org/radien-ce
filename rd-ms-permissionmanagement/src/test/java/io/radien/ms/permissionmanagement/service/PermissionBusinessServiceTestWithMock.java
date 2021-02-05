package io.radien.ms.permissionmanagement.service;

import io.radien.api.service.permission.ActionServiceAccess;
import io.radien.api.service.permission.PermissionServiceAccess;
import io.radien.exception.ActionNotFoundException;
import io.radien.exception.PermissionNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.permissionmanagement.model.Action;
import io.radien.ms.permissionmanagement.model.Permission;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;

public class PermissionBusinessServiceTestWithMock {

    @Mock
    PermissionServiceAccess permissionServiceAccess;

    @Mock
    ActionServiceAccess actionServiceAccess;

    @InjectMocks
    PermissionBusinessService permissionBusinessService;

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void simulatingExceptionDuringSaveAssociation() throws PermissionNotFoundException, ActionNotFoundException, UniquenessConstraintException {
        Action action = new Action();
        action.setId(2L);

        Permission permission = new Permission();
        permission.setId(5L);

        Mockito.when(permissionServiceAccess.get(permission.getId())).thenReturn(permission);
        Mockito.when(actionServiceAccess.get(action.getId())).thenReturn(action);

        Mockito.doThrow(new RuntimeException("persistence layer error")).
                when(permissionServiceAccess).save(permission);

        Exception exception =
                assertThrows(Exception.class,
                        () -> permissionBusinessService.associate(permission.getId(), action.getId()));
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("persistence layer error"));
    }

    @Test
    public void simulatingExceptionDuringSaveDissociation() throws PermissionNotFoundException, ActionNotFoundException, UniquenessConstraintException {
        Permission permission = new Permission();
        permission.setId(5L);
        Mockito.when(permissionServiceAccess.get(permission.getId())).thenReturn(permission);
        Mockito.doThrow(new RuntimeException("persistence layer error")).
                when(permissionServiceAccess).save(permission);
        Exception exception =
                assertThrows(Exception.class,
                        () -> permissionBusinessService.dissociation(permission.getId()));
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("persistence layer error"));
    }

}
