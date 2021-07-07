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
package io.radien.ms.rolemanagement.services;

import io.radien.api.SystemVariables;
import io.radien.api.model.tenantrole.SystemTenantRolePermission;
import io.radien.api.model.tenantrole.SystemTenantRolePermissionSearchFilter;
import io.radien.api.service.tenantrole.TenantRolePermissionServiceAccess;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.client.entities.TenantRolePermissionSearchFilter;
import io.radien.ms.rolemanagement.entities.TenantRolePermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.CriteriaDelete;
import java.util.List;
import java.util.Optional;

/**
 * Repository (Service access implementation) for managing Tenant Role Permission entities
 * @author Newton Carvalho
 */
@Stateful
public class TenantRolePermissionService implements TenantRolePermissionServiceAccess {

    @PersistenceContext(unitName = "persistenceUnit")
    private EntityManager entityManager;

    private static final Logger log = LoggerFactory.getLogger(TenantRolePermissionService.class);

    /**
     * Gets the System Tenant Role Permission searching by the PK (id).
     * @param tenantRolePermissionId to be searched.
     * @return the system tenant role requested to be found.
     */
    @Override
    public SystemTenantRolePermission get(Long tenantRolePermissionId) {
        return getEntityManager().find(TenantRolePermission.class, tenantRolePermissionId);
    }

    /**
     * Gets all the tenants role permission associations matching the given filter information
     * @param filter information to search
     * @return a list o found system tenant role permission associations
     */
    public List<? extends SystemTenantRolePermission> get(SystemTenantRolePermissionSearchFilter filter) {
        EntityManager em = getEntityManager();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<TenantRolePermission> criteriaQuery = criteriaBuilder.createQuery(TenantRolePermission.class);
        Root<TenantRolePermission> root = criteriaQuery.from(TenantRolePermission.class);

        criteriaQuery.select(root);

        Predicate global = getFilteredPredicate((TenantRolePermissionSearchFilter) filter, criteriaBuilder, root);

        criteriaQuery.where(global);
        TypedQuery<TenantRolePermission> q = em.createQuery(criteriaQuery);

        return q.getResultList();
    }

    /**
     * Is LogicalConjunction represents if you join the fields on the predicates with "or" or "and"
     * the predicate is build with the logic (start,operator,newPredicate)
     * where start represents the already joined predicates
     * operator is "and" or "or"
     * depending on the operator the start may need to be true or false
     * true and predicate1 and predicate2
     * false or predicate1 or predicate2
     * @param filter information to be search
     * @param criteriaBuilder to be used
     * @param tenantRolePermissionRoot table to be search
     * @return a filtered predicate
     */
    private Predicate getFilteredPredicate(TenantRolePermissionSearchFilter filter, CriteriaBuilder criteriaBuilder,
                                           Root<TenantRolePermission> tenantRolePermissionRoot) {
        Predicate global;
        if(filter.isLogicConjunction()) {
            global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        } else {
            global = criteriaBuilder.isFalse(criteriaBuilder.literal(true));
        }

        global = getFieldPredicate(SystemVariables.TENANT_ROLE_ID.getFieldName(), filter.getTenantRoleId(), filter, criteriaBuilder,
                tenantRolePermissionRoot, global);
        global = getFieldPredicate(SystemVariables.PERMISSION_ID.getFieldName(), filter.getPermissionId(), filter, criteriaBuilder,
                tenantRolePermissionRoot, global);

        return global;
    }

    /**
     * Puts the requested fields into a predicate line
     * @param name of the field
     * @param value of the field
     * @param filter complete filter
     * @param criteriaBuilder to be used
     * @param tenantRolePermissionRoot table to be used
     * @param global predicate to be added
     * @return a constructed predicate
     */
    private Predicate getFieldPredicate(String name, Object value,
                                        TenantRolePermissionSearchFilter filter,
                                        CriteriaBuilder criteriaBuilder,
                                        Root<TenantRolePermission> tenantRolePermissionRoot,
                                        Predicate global) {
        if(value != null) {
            Predicate subPredicate;
            if (value instanceof String && !filter.isExact()) {
                subPredicate = criteriaBuilder.like(tenantRolePermissionRoot.get(name),"%"+value+"%");
            }
            else {
                subPredicate = criteriaBuilder.equal(tenantRolePermissionRoot.get(name), value);
            }

            if(filter.isLogicConjunction()) {
                global = criteriaBuilder.and(global, subPredicate);
            } else {
                global = criteriaBuilder.or(global, subPredicate);
            }
        }
        return global;
    }

    /**
     * CREATE a Tenant Role Permission association
     * @param tenantRolePermission role association information to be created
     * @throws UniquenessConstraintException in case of duplicated fields or records
     */
    @Override
    public void create(SystemTenantRolePermission tenantRolePermission) throws UniquenessConstraintException {
        EntityManager em = getEntityManager();
        boolean alreadyExistentRecords = isAssociationAlreadyExistent(tenantRolePermission.getPermissionId(),
                tenantRolePermission.getTenantRoleId(), em);
        if (alreadyExistentRecords) {
            throw new UniquenessConstraintException(GenericErrorCodeMessage.
                    DUPLICATED_FIELD.toString("permissionId and roleTenantId"));
        }
        em.persist(tenantRolePermission);
    }

    /**
     * Check if a permission is already assigned/associated with a tenant role
     * @param permissionId Permission identifier
     * @param tenantRoleId TenantRole Identifier
     * @return true if already exists, otherwise returns false
     */
    @Override
    public boolean isAssociationAlreadyExistent(Long permissionId, Long tenantRoleId) {
        return isAssociationAlreadyExistent(permissionId, tenantRoleId, getEntityManager());
    }

    /**
     * Check if a permission is already assigned/associated with a tenant role
     * @param permissionId Role identifier
     * @param tenantRoleId Tenant Identifier
     * @param em already created entity manager (reuse)
     * @return true in case the association exists in the db
     */
    protected boolean isAssociationAlreadyExistent(Long permissionId, Long tenantRoleId, EntityManager em) {
        if (permissionId == null) {
            throw new IllegalArgumentException(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString("permission id"));
        }
        if (tenantRoleId == null) {
            throw new IllegalArgumentException(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString("id"));
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> sc = cb.createQuery(Long.class);
        Root<TenantRolePermission> root = sc.from(TenantRolePermission.class);

        sc.select(cb.count(root)).
                where(
                        cb.equal(root.get(SystemVariables.PERMISSION_ID.getFieldName()),permissionId),
                        cb.equal(root.get(SystemVariables.TENANT_ROLE_ID.getFieldName()),tenantRoleId)
                );
        List<Long> count = em.createQuery(sc).getResultList();
        return !count.isEmpty() && count.get(0) > 0;
    }

    /**
     * Deletes a requested tenant role permission association
     * @param tenantRolePermissionId tenant role permission to be deleted
     * @return true in case of success false in case of any error
     */
    @Override
    public boolean delete(Long tenantRolePermissionId)  {
        if (tenantRolePermissionId == null) {
            throw new IllegalArgumentException(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString("permission id"));
        }
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<TenantRolePermission> criteriaDelete = cb.createCriteriaDelete(TenantRolePermission.class);
        Root<TenantRolePermission> tenantRolePermissionRoot = criteriaDelete.from(TenantRolePermission.class);
        criteriaDelete.where(cb.equal(tenantRolePermissionRoot.get(SystemVariables.ID.getFieldName()),tenantRolePermissionId));
        return em.createQuery(criteriaDelete).executeUpdate() > 0;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * Retrieves strictly the TenantRolePermission id basing on tenantRole and user
     * @param tenantRole tenant identifier
     * @param permission identifier
     * @return TenantRolePermission id
     */
    @Override
    public Optional<Long> getTenantRolePermissionId(Long tenantRole, Long permission) {
        if (tenantRole == null) {
            throw new IllegalArgumentException(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString("tenant role id"));
        }
        if(permission == null) {
            throw new IllegalArgumentException(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString("permission id"));
        }
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
        Root<TenantRolePermission> root = criteriaQuery.from(TenantRolePermission.class);

        criteriaQuery.select(root.get(SystemVariables.ID.getFieldName()));

        criteriaQuery.where(
                cb.equal(root.get(SystemVariables.TENANT_ROLE_ID.getFieldName()),tenantRole),
                cb.equal(root.get(SystemVariables.PERMISSION_ID.getFieldName()),permission)
        );

        TypedQuery<Long> typedQuery = em.createQuery(criteriaQuery);
        List<Long> list = typedQuery.getResultList();
        return !list.isEmpty() ? Optional.of(list.get(0)) : Optional.empty();
    }
}
