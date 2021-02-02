package io.radien.ms.permissionmanagement.service;

import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemAction;
import io.radien.api.model.permission.SystemPermission;
import io.radien.api.service.permission.ActionServiceAccess;
import io.radien.api.service.permission.PermissionServiceAccess;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.permissionmanagement.client.entities.ActionSearchFilter;
import io.radien.ms.permissionmanagement.client.entities.ActionType;
import io.radien.ms.permissionmanagement.client.entities.PermissionSearchFilter;
import io.radien.ms.permissionmanagement.legacy.PermissionFactory;
import io.radien.ms.permissionmanagement.model.Action;
import io.radien.ms.permissionmanagement.model.AssociationStatus;
import io.radien.ms.permissionmanagement.model.Permission;
import org.junit.Assert;
import org.junit.Test;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import java.util.List;
import java.util.Properties;

public class PermissionBusinessServiceTest {

    PermissionServiceAccess permissionServiceAccess;
    ActionServiceAccess actionServiceAccess;
    PermissionBusinessService permissionBusinessService;

    SystemPermission permission;
    SystemAction action;

    public PermissionBusinessServiceTest() throws NamingException, UniquenessConstraintException {
        Properties p = new Properties();
        p.put("appframeDatabase", "new://Resource?type=DataSource");
        p.put("appframeDatabase.JdbcDriver", "org.hsqldb.jdbcDriver");
        p.put("appframeDatabase.JdbcUrl", "jdbc:hsqldb:mem:radien-permission-association-with-action");
        p.put("appframeDatabase.userName", "sa");
        p.put("appframeDatabase.password", "");

        final Context context = EJBContainer.createEJBContainer(p).getContext();

        permissionServiceAccess = (PermissionServiceAccess)
                context.lookup("java:global/rd-ms-permissionmanagement//PermissionService");
        actionServiceAccess = (ActionServiceAccess)
                context.lookup("java:global/rd-ms-permissionmanagement//ActionService");
        permissionBusinessService = (PermissionBusinessService)
                context.lookup("java:global/rd-ms-permissionmanagement//PermissionBusinessService");

        List<? extends SystemAction> actions = actionServiceAccess.getActions(new ActionSearchFilter(
                "remove-user-from-tenant", null, true, false));

        if (actions.isEmpty()) {
            action = new Action();
            action.setType(ActionType.READ);
            action.setName("remove-user-from-tenant");
            actionServiceAccess.save(action);
            actions = actionServiceAccess.getActions(new ActionSearchFilter(
                    "remove-user-from-tenant", null, true, false));
        }

        action = actions.get(0);

        List<? extends SystemPermission> permissions = permissionServiceAccess.getPermissions(new PermissionSearchFilter(
                "removing-asset", true, false));

        if (permissions.isEmpty()) {
            permission = new Permission();
            permission.setName("removing-asset");
            permissionServiceAccess.save(permission);
            permissions = permissionServiceAccess.getPermissions(new PermissionSearchFilter(
                    "removing-asset", true, false));
        }

        permission = permissions.get(0);
    }

    @Test
    public void testAssociation() {
        AssociationStatus associationStatus =
                this.permissionBusinessService.associate(permission.getId(), action.getId());
        Assert.assertTrue(associationStatus.isOK());

        List<? extends SystemPermission> list = permissionServiceAccess.getPermissions(new PermissionSearchFilter(
                "removing-asset", true, false));

        SystemPermission sp = list.get(0);

        Assert.assertNotNull(sp);
        Assert.assertNotNull(sp.getAction());
    }

    @Test
    public void testDissociation() {
        AssociationStatus associationStatus =
                this.permissionBusinessService.dissociation(permission.getId());
        Assert.assertTrue(associationStatus.isOK());

        List<? extends SystemPermission> list = permissionServiceAccess.getPermissions(new PermissionSearchFilter(
                "removing-asset", true, false));

        SystemPermission sp = list.get(0);

        Assert.assertNotNull(sp);
        Assert.assertNull(sp.getAction());
    }

}
