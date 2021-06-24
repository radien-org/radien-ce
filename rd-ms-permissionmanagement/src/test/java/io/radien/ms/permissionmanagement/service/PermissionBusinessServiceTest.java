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
package io.radien.ms.permissionmanagement.service;

import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemAction;
import io.radien.api.model.permission.SystemPermission;
import io.radien.api.model.permission.SystemResource;
import io.radien.api.service.permission.ActionServiceAccess;
import io.radien.api.service.permission.PermissionServiceAccess;
import io.radien.api.service.permission.ResourceServiceAccess;
import io.radien.exception.PermissionNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.permissionmanagement.client.entities.PermissionSearchFilter;
import io.radien.ms.permissionmanagement.model.Action;
import io.radien.ms.permissionmanagement.client.entities.AssociationStatus;
import io.radien.ms.permissionmanagement.model.Permission;
import io.radien.ms.permissionmanagement.model.Resource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @author Bruno Gama
 */
public class PermissionBusinessServiceTest {

    static PermissionServiceAccess permissionServiceAccess;
    static ActionServiceAccess actionServiceAccess;
    static ResourceServiceAccess resourceServiceAccess;
    static PermissionBusinessService permissionBusinessService;

    static SystemPermission permission;
    static SystemAction action;
    static SystemResource resource;

    static EJBContainer container;

    @BeforeClass
    public static void PermissionBusinessServiceTest() throws NamingException {
        Properties p = new Properties();
        p.put("openejb.deployments.classpath.include",".*permission.*");
        p.put("openejb.deployments.classpath.exclude",".*client.*");
        container = EJBContainer.createEJBContainer(p);
        final Context context = container.getContext();

        permissionServiceAccess = (PermissionServiceAccess)
                context.lookup("java:global/rd-ms-permissionmanagement//PermissionService");
        actionServiceAccess = (ActionServiceAccess)
                context.lookup("java:global/rd-ms-permissionmanagement//ActionService");
        resourceServiceAccess = (ResourceServiceAccess)
                context.lookup("java:global/rd-ms-permissionmanagement//ResourceService");
        permissionBusinessService = (PermissionBusinessService)
                context.lookup("java:global/rd-ms-permissionmanagement//PermissionBusinessService");
    }

    @Before
    public void inject() throws NamingException {
        container.getContext().bind("inject", this);
    }

    @AfterClass
    public static void stop() {
        if (container != null) {
            container.close();
        }
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

        Page<SystemResource> pageResources = resourceServiceAccess.getAll(null, 1, 1000, null, true);

        if (pageResources.getResults().size() > 0) {
            resourceServiceAccess.delete(pageResources.getResults().stream().map(r -> r.getId()).collect(Collectors.toList()));
        }
    }

    @Test
    public void testAssociation() throws UniquenessConstraintException, PermissionNotFoundException {
        resource = new Resource();
        resource.setName("userTestAssociation");
        resourceServiceAccess.save(resource);

        action = new Action();
        action.setName("remove-userTestAssociation");
        actionServiceAccess.save(action);

        permission = new Permission();
        permission.setName("removing-assetTestAssociation");
        permissionServiceAccess.save(permission);

        SystemPermission sp = permissionServiceAccess.get(permission.getId());
        Assert.assertNull(sp.getActionId());

        AssociationStatus associationStatus = this.permissionBusinessService.associate(sp.getId(), action.getId(), resource.getId());
        Assert.assertTrue(associationStatus.isOK());

        SystemPermission sp2 = permissionServiceAccess.get(permission.getId());
        Assert.assertNotNull(sp2.getActionId());
        Assert.assertNotNull(sp2.getResourceId());
    }

    @Test
    public void testDissociation() throws UniquenessConstraintException {
        resource = new Resource();
        resource.setName("userTestDissociation");
        resourceServiceAccess.save(resource);

        action = new Action();
        action.setName("remove-userTestDissociation");
        actionServiceAccess.save(action);

        permission = new Permission();
        permission.setName("removing-assetTestDissociation");
        permissionServiceAccess.save(permission);

        Permission perm = new Permission();
        perm.setName("adding-user");
        perm.setActionId(action.getId());
        perm.setResourceId(resource.getId());
        permissionServiceAccess.save(perm);

        List<? extends SystemPermission> list = permissionServiceAccess.getPermissions(
                new PermissionSearchFilter("adding-user", action.getId(), null, true, false));

        SystemPermission sp = list.get(0);
        Assert.assertNotNull(sp.getActionId());

        AssociationStatus associationStatus = this.permissionBusinessService.dissociation(perm.getId());
        Assert.assertTrue(associationStatus.isOK());

        list = permissionServiceAccess.getPermissions(
                new PermissionSearchFilter("adding-user", null, null,true, false));

        SystemPermission sp2 = list.get(0);
        Assert.assertNull(sp2.getActionId());
        Assert.assertNull(sp2.getResourceId());
    }

    @Test
    public void testAssociatingNotExistentPermission() throws UniquenessConstraintException {
        resource = new Resource();
        resource.setName("userTestAssociatingNotExistentPermission");
        resourceServiceAccess.save(resource);

        action = new Action();
        action.setName("remove-userTestAssociatingNotExistentPermission");
        actionServiceAccess.save(action);

        permission = new Permission();
        permission.setName("removing-assetTestAssociatingNotExistentPermission");
        permissionServiceAccess.save(permission);

        AssociationStatus associationStatus =
                this.permissionBusinessService.associate(100000L, action.getId(), resource.getId());
        Assert.assertFalse(associationStatus.isOK());
        Assert.assertTrue(associationStatus.getMessage().contains("Permission not found"));
    }

    @Test
    public void testAssociatingNotExistentAction() throws UniquenessConstraintException {
        resource = new Resource();
        resource.setName("userTestAssociatingNotExistentAction");
        resourceServiceAccess.save(resource);

        action = new Action();
        action.setName("remove-userTestAssociatingNotExistentAction");
        actionServiceAccess.save(action);

        permission = new Permission();
        permission.setName("removing-assetTestAssociatingNotExistentAction");
        permissionServiceAccess.save(permission);

        AssociationStatus associationStatus =
                this.permissionBusinessService.associate(permission.getId(), 111111L, resource.getId());
        Assert.assertFalse(associationStatus.isOK());
        Assert.assertTrue(associationStatus.getMessage().contains("Action not found"));
    }

    @Test
    public void testAssociatingNotExistentResource() throws UniquenessConstraintException {
        resource = new Resource();
        resource.setName("userTestAssociatingNotExistentResource");
        resourceServiceAccess.save(resource);

        action = new Action();
        action.setName("remove-userTestAssociatingNotExistentResource");
        actionServiceAccess.save(action);

        permission = new Permission();
        permission.setName("removing-assetTestAssociatingNotExistentResource");
        permissionServiceAccess.save(permission);

        AssociationStatus associationStatus =
                this.permissionBusinessService.associate(permission.getId(), action.getId(), 11111L);
        Assert.assertFalse(associationStatus.isOK());
        Assert.assertTrue(associationStatus.getMessage().contains("Resource not found"));
    }

    @Test
    public void testAssociatingWithNullAsPermissionId() throws UniquenessConstraintException {
        AssociationStatus associationStatus =
                this.permissionBusinessService.associate(null, null, null);
        Assert.assertFalse(associationStatus.isOK());
        Assert.assertTrue(associationStatus.getMessage().contains("Permission Id not informed"));
    }

    @Test
    public void testAssociatingWithNullAsActionId() throws UniquenessConstraintException {
        AssociationStatus associationStatus =
                this.permissionBusinessService.associate(1L, null, null);
        Assert.assertFalse(associationStatus.isOK());
        Assert.assertTrue(associationStatus.getMessage().contains("Action Id not informed"));
    }

    @Test
    public void testAssociatingWithNullAsResourceId() throws UniquenessConstraintException {
        resource = new Resource();
        resource.setName("user");
        resourceServiceAccess.save(resource);

        action = new Action();
        action.setName("remove-user");
        actionServiceAccess.save(action);

        permission = new Permission();
        permission.setName("removing-asset");
        permissionServiceAccess.save(permission);

        AssociationStatus associationStatus =
                this.permissionBusinessService.associate(1L, 2L, null);
        Assert.assertFalse(associationStatus.isOK());
        Assert.assertTrue(associationStatus.getMessage().contains("Resource Id not informed"));
    }

    @Test
    public void testDissociatingNotExistentPermission() throws UniquenessConstraintException {
        AssociationStatus associationStatus =
                this.permissionBusinessService.dissociation(100000L);
        Assert.assertFalse(associationStatus.isOK());
        Assert.assertTrue(associationStatus.getMessage().contains("Permission not found"));
    }

    @Test
    public void testDissociatingWithNullAsPermissionId() throws UniquenessConstraintException {
        AssociationStatus associationStatus =
                this.permissionBusinessService.dissociation(null);
        Assert.assertFalse(associationStatus.isOK());
        Assert.assertTrue(associationStatus.getMessage().contains("Permission Id not informed"));
    }
}
