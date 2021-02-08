package io.radien.ms.permissionmanagement.service;

import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemAction;
import io.radien.api.model.permission.SystemPermission;
import io.radien.api.service.permission.ActionServiceAccess;
import io.radien.api.service.permission.PermissionServiceAccess;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.permissionmanagement.client.entities.ActionSearchFilter;
import io.radien.ms.permissionmanagement.client.entities.PermissionSearchFilter;
import io.radien.ms.permissionmanagement.model.Action;
import io.radien.ms.permissionmanagement.client.entities.AssociationStatus;
import io.radien.ms.permissionmanagement.model.Permission;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class PermissionBusinessServiceTest {

    PermissionServiceAccess permissionServiceAccess;
    ActionServiceAccess actionServiceAccess;
    PermissionBusinessService permissionBusinessService;

    SystemPermission permission;
    SystemAction action;

    public PermissionBusinessServiceTest() throws NamingException, UniquenessConstraintException {
        final Context context = EJBContainer.createEJBContainer(new Properties()).getContext();

        permissionServiceAccess = (PermissionServiceAccess)
                context.lookup("java:global/rd-ms-permissionmanagement//PermissionService");
        actionServiceAccess = (ActionServiceAccess)
                context.lookup("java:global/rd-ms-permissionmanagement//ActionService");
        permissionBusinessService = (PermissionBusinessService)
                context.lookup("java:global/rd-ms-permissionmanagement//PermissionBusinessService");

    }

    @Before
    public void init() throws UniquenessConstraintException {

        action = new Action();
        action.setName("remove-user-from-tenant");
        actionServiceAccess.save(action);
        List<? extends SystemAction> actions = actionServiceAccess.getActions(new ActionSearchFilter(
                "remove-user-from-tenant", true, false));

        action = actions.get(0);

        permission = new Permission();
        permission.setName("removing-asset");
        permissionServiceAccess.save(permission);

        List<? extends SystemPermission> permissions = permissionServiceAccess.getPermissions(new PermissionSearchFilter(
                "removing-asset", true, false));

        permission = permissions.get(0);
    }

    @After
    public void tear() {

        Page<SystemPermission> pagePermissions = permissionServiceAccess.getAll(null, 1, 1000, null, true);

        if (pagePermissions.getResults().size() > 0) {
            permissionServiceAccess.delete(pagePermissions.getResults().stream().map(p -> p.getId()).collect(Collectors.toList()));
        }

        Page<SystemAction> pageActions = actionServiceAccess.getAll(null, 1, 1000, null, true);

        if (pageActions.getResults().size() > 0) {
            actionServiceAccess.delete(pageActions.getResults().stream().map(a -> a.getId()).collect(Collectors.toList()));
        }
    }

    @Test
    public void testAssociation() throws UniquenessConstraintException {
        AssociationStatus associationStatus =
                this.permissionBusinessService.associate(permission.getId(), action.getId());
        Assert.assertTrue(associationStatus.isOK());

        List<? extends SystemPermission> list = permissionServiceAccess.getPermissions(new PermissionSearchFilter(
                "removing-asset", true, false));

        SystemPermission sp = list.get(0);

        Assert.assertNotNull(sp);
        Assert.assertNotNull(sp.getActionId());
    }

    @Test
    public void testDissociation() throws UniquenessConstraintException {
        AssociationStatus associationStatus =
                this.permissionBusinessService.dissociation(permission.getId());
        Assert.assertTrue(associationStatus.isOK());

        List<? extends SystemPermission> list = permissionServiceAccess.getPermissions(new PermissionSearchFilter(
                "removing-asset", true, false));

        SystemPermission sp = list.get(0);

        Assert.assertNotNull(sp);
        Assert.assertNull(sp.getActionId());
    }

    @Test
    public void testAssociatingNotExistentPermission() throws UniquenessConstraintException {
        AssociationStatus associationStatus =
                this.permissionBusinessService.associate(100000L, action.getId());
        Assert.assertFalse(associationStatus.isOK());
        Assert.assertTrue(associationStatus.getMessage().contains("Permission not found"));
    }

    @Test
    public void testAssociatingNotExistentAction() throws UniquenessConstraintException {
        AssociationStatus associationStatus =
                this.permissionBusinessService.associate(permission.getId(), 111111L);
        Assert.assertFalse(associationStatus.isOK());
        Assert.assertTrue(associationStatus.getMessage().contains("Action not found"));
    }

    @Test
    public void testDissociatingNotExistentPermission() throws UniquenessConstraintException {
        AssociationStatus associationStatus =
                this.permissionBusinessService.dissociation(100000L);
        Assert.assertFalse(associationStatus.isOK());
        Assert.assertTrue(associationStatus.getMessage().contains("Permission not found"));
    }
}
