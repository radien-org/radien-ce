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

    private final String BASE_MSG = "%s not found for %n";

    public AssociationStatus associate(Long permissionId, Long actionId) throws UniquenessConstraintException{
        try {
            SystemPermission sp = permissionServiceAccess.get(permissionId);
            if (sp == null) {
                throw new PermissionNotFoundException(String.format(BASE_MSG, "Permission", permissionId));
            }

            SystemAction sa = actionServiceAccess.get(actionId);
            if (sa == null) {
                throw new ActionNotFoundException(String.format(BASE_MSG, "Action", actionId));
            }

            sp.setAction(sa);
            permissionServiceAccess.save(sp);
        }
        catch (ActionNotFoundException | PermissionNotFoundException e) {
            this.log.error("error associating permission with action", e);
            return new AssociationStatus(false, e.getMessage());
        }
        return new AssociationStatus();
    }

    public AssociationStatus dissociation(Long permissionId) throws UniquenessConstraintException {
        try {
            SystemPermission sp = permissionServiceAccess.get(permissionId);
            if (sp == null) {
                throw new PermissionNotFoundException(String.format(BASE_MSG, "Permission", permissionId));
            }
            sp.setAction(null);
            permissionServiceAccess.save(sp);
        } catch (PermissionNotFoundException e) {
            this.log.error("error associating permission with action", e);
            return new AssociationStatus(false, e.getMessage());
        }
        return new AssociationStatus();
    }
}
