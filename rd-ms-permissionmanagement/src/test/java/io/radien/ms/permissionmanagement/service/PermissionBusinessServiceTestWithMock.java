package io.radien.ms.permissionmanagement.service;

import io.radien.api.model.permission.SystemAction;
import io.radien.api.model.permission.SystemPermission;
import io.radien.api.service.permission.ActionServiceAccess;
import io.radien.api.service.permission.PermissionServiceAccess;
import io.radien.exception.ActionNotFoundException;
import io.radien.exception.PermissionNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.permissionmanagement.client.entities.ActionSearchFilter;
import io.radien.ms.permissionmanagement.client.entities.ActionType;
import io.radien.ms.permissionmanagement.client.entities.PermissionSearchFilter;
import io.radien.ms.permissionmanagement.model.Action;
import io.radien.ms.permissionmanagement.model.AssociationStatus;
import io.radien.ms.permissionmanagement.model.Permission;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import java.util.List;
import java.util.Properties;

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

        AssociationStatus status = permissionBusinessService.associate(permission.getId(), action.getId());
        Assert.assertFalse(status.isOK());
        Assert.assertTrue(status.getMessage().contains("persistence layer error"));
    }

    @Test
    public void simulatingExceptionDuringSaveDissociation() throws PermissionNotFoundException, ActionNotFoundException, UniquenessConstraintException {
        Permission permission = new Permission();
        permission.setId(5L);

        Mockito.when(permissionServiceAccess.get(permission.getId())).thenReturn(permission);

        Mockito.doThrow(new RuntimeException("persistence layer error")).
                when(permissionServiceAccess).save(permission);

        AssociationStatus status = permissionBusinessService.dissociation(permission.getId());
        Assert.assertFalse(status.isOK());
        Assert.assertTrue(status.getMessage().contains("persistence layer error"));
    }
}
