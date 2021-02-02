package io.radien.ms.permissionmanagement.service;

import io.radien.api.model.permission.SystemAction;
import io.radien.api.model.permission.SystemPermission;
import io.radien.api.service.permission.ActionServiceAccess;
import io.radien.api.service.permission.PermissionServiceAccess;
import io.radien.exception.ActionNotFoundException;
import io.radien.exception.PermissionNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.permissionmanagement.model.AssociationStatus;
import io.radien.ms.permissionmanagement.model.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;

@Stateless
public class PermissionBusinessService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Inject
    private PermissionServiceAccess permissionServiceAccess;

    @Inject
    private ActionServiceAccess actionServiceAccess;

    public AssociationStatus associate(Long permissionId, Long actionId) {

        SystemPermission sp = null;
        try {
            sp = permissionServiceAccess.get(permissionId);
            if (sp == null) {
                throw new PermissionNotFoundException("Permission not found for " + permissionId);
            }
        } catch (PermissionNotFoundException e) {
            this.log.error("error associating permission with action", e);
            return new AssociationStatus(false, "Permission Not Found");
        }

        SystemAction sa = null;
        try {
            sa = actionServiceAccess.get(actionId);
            if (sa == null) {
                throw new ActionNotFoundException("Action not found for " + actionId);
            }
        }
        catch (ActionNotFoundException a) {
            this.log.error("error associating permission with action", a);
            return new AssociationStatus(false, "Action Not Found");
        }

        try {
            sp.setAction(sa);
            permissionServiceAccess.save(sp);
        } catch (UniquenessConstraintException e) {
            this.log.error("error associating permission with action", e);
            return new AssociationStatus(false, "Error associating Permission and action");
        }

        return new AssociationStatus();
    }

    public AssociationStatus dissociation(long permissionId) {
        SystemPermission sp = null;
        try {
            sp = permissionServiceAccess.get(permissionId);
            if (sp == null) {
                throw new PermissionNotFoundException("Permission not found for " + permissionId);
            }
        } catch (PermissionNotFoundException e) {
            this.log.error("error associating permission with action", e);
            return new AssociationStatus(false, "Permission Not Found");
        }

        try {
            sp.setAction(null);
            permissionServiceAccess.save(sp);
        } catch (UniquenessConstraintException e) {
            this.log.error("error dissociating permission with action", e);
            return new AssociationStatus(false, "Error associating Permission and action");
        }

        return new AssociationStatus();
    }
}
