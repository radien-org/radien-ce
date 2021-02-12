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

import io.radien.api.model.permission.SystemPermission;
import io.radien.api.service.permission.PermissionServiceAccess;
import io.radien.exception.ActionNotFoundException;
import io.radien.exception.PermissionNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.permissionmanagement.client.entities.AssociationStatus;
import io.radien.ms.permissionmanagement.model.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * @author Newton Carvalho
 * Service facade to perform operations beyond the simple CRUD contract
 */
@Stateless
public class PermissionBusinessService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Inject
    private PermissionServiceAccess permissionServiceAccess;

    @PersistenceUnit
    private EntityManagerFactory emf;

    private final String BASE_MSG = "%s not found for id=%d";

    /***
     * Assign an action to a permission
     * @param permissionId
     * @param actionId
     * @return AssociationStatus describing the status operation (if it was successful or not) and the
     * issue description (in case of unsuccessful operation)
     * @throws UniquenessConstraintException
     */
    public AssociationStatus associate(Long permissionId, Long actionId) throws UniquenessConstraintException{
        if (permissionId == null)
            return new AssociationStatus(false, "Permission Id not informed");

        if (actionId == null)
            return new AssociationStatus(false, "Action Id not informed");

        try {
            SystemPermission sp = permissionServiceAccess.get(permissionId);
            if (sp == null) {
                throw new PermissionNotFoundException(String.format(BASE_MSG, "Permission", permissionId));
            }

            if (!checkIfActionExists(actionId)) {
                throw new ActionNotFoundException(String.format(BASE_MSG, "Action", actionId));
            }
            sp.setActionId(actionId);
            permissionServiceAccess.save(sp);
        }
        catch (ActionNotFoundException | PermissionNotFoundException e) {
            this.log.error("error associating permission with action", e);
            return new AssociationStatus(false, e.getMessage());
        }
        return new AssociationStatus();
    }

    /***
     * UnAssign an action to a permission
     * @param permissionId
     * @return AssociationStatus describing the status operation (if it was successful or not) and the
     * issue description (in case of unsuccessful operation)
     * @throws UniquenessConstraintException
     */
    public AssociationStatus dissociation(Long permissionId) throws UniquenessConstraintException {
        if (permissionId == null)
            return new AssociationStatus(false, "Permission Id not informed");

        try {
            SystemPermission sp = permissionServiceAccess.get(permissionId);
            if (sp == null) {
                throw new PermissionNotFoundException(String.format(BASE_MSG, "Permission", permissionId));
            }
            sp.setActionId(null);
            permissionServiceAccess.save(sp);
        } catch (PermissionNotFoundException e) {
            this.log.error("error associating permission with action", e);
            return new AssociationStatus(false, e.getMessage());
        }
        return new AssociationStatus();
    }

    /**
     * Check if an action exists on the DB
     * @param actionId
     * @return
     */
    protected boolean checkIfActionExists(Long actionId) {
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Action> actionRoot = criteriaQuery.from(Action.class);

        criteriaQuery.select(actionRoot.get("id")).where(criteriaBuilder.equal(actionRoot.get("id"), actionId));

        TypedQuery<Long> typedQuery = em.createQuery(criteriaQuery);
        return typedQuery.getResultStream().findFirst().isPresent();
    }
}