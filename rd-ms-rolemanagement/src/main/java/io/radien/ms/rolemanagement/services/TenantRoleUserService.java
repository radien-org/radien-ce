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
 * See the License for the specific language governing users and
 * limitations under the License.
 */
package io.radien.ms.rolemanagement.services;

import io.radien.api.SystemVariables;
import io.radien.api.entity.Page;
import io.radien.api.model.tenantrole.SystemTenantRoleUser;
import io.radien.api.model.tenantrole.SystemTenantRoleUserSearchFilter;
import io.radien.api.service.tenantrole.TenantRoleUserServiceAccess;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.entities.TenantRoleUser;
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
 * Repository (Service access implementation) for managing Tenant Role User entities
 * @author Newton Carvalho
 */
@Stateful
public class TenantRoleUserService implements TenantRoleUserServiceAccess {

    @PersistenceContext(unitName = "persistenceUnit")
    private EntityManager entityManager;

    private static final Logger log = LoggerFactory.getLogger(TenantRoleUserService.class);

    /**
     * Gets the System Tenant Role User searching by the PK (id).
     * @param tenantRoleUserId to be searched.
     * @return the system tenant role requested to be found.
     */
    @Override
    public SystemTenantRoleUser get(Long tenantRoleUserId) {
        return getEntityManager().find(TenantRoleUser.class, tenantRoleUserId);
    }

    /**
     * Gets all the tenant role user associations into a pagination mode.
     * @param pageNo of the requested information. Where the tenant is.
     * @param pageSize total number of pages returned in the request.
     * @return a page containing system tenant role user associations.
     */
    @Override
    public Page<SystemTenantRoleUser> getAll(int pageNo, int pageSize) {
        log.info("Retrieving tenant role user associations using pagination mode");
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TenantRoleUser> criteriaQuery = criteriaBuilder.createQuery(TenantRoleUser.class);
        Root<TenantRoleUser> tenantRoleRoot = criteriaQuery.from(TenantRoleUser.class);

        criteriaQuery.select(tenantRoleRoot);
        Predicate global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));

        TypedQuery<TenantRoleUser> q= entityManager.createQuery(criteriaQuery);

        q.setFirstResult((pageNo-1) * pageSize);
        q.setMaxResults(pageSize);

        List<? extends SystemTenantRoleUser> systemTenantRolesUsers = q.getResultList();

        int totalRecords = Math.toIntExact(getCount(global, tenantRoleRoot, entityManager));
        int totalPages = totalRecords%pageSize==0 ? totalRecords/pageSize : totalRecords/pageSize+1;

        log.info("Pagination ready to be shown");

        return new Page<SystemTenantRoleUser>(systemTenantRolesUsers, pageNo, totalRecords, totalPages);
    }

    /**
     * Count the number of existent associations (Tenant role user) in the DB.
     * @return the count of tenant role user associations
     */
    private long getCount(Predicate global, Root<TenantRoleUser> tenantRoleUserRoot, EntityManager em) {

        log.info("Gathering/counting the associations for tenant role user");
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        criteriaQuery.where(global);

        criteriaQuery.select(criteriaBuilder.count(tenantRoleUserRoot));

        TypedQuery<Long> q= em.createQuery(criteriaQuery);

        return q.getSingleResult();
    }

    /**
     * Gets all the tenants role user associations matching the given filter information
     * @param filter information to search
     * @return a list o found system tenant role user associations
     */
    public List<? extends SystemTenantRoleUser> get(SystemTenantRoleUserSearchFilter filter) {
        EntityManager em = getEntityManager();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<TenantRoleUser> criteriaQuery = criteriaBuilder.createQuery(TenantRoleUser.class);
        Root<TenantRoleUser> root = criteriaQuery.from(TenantRoleUser.class);

        criteriaQuery.select(root);

        Predicate global = getFilteredPredicate(filter, criteriaBuilder, root);

        criteriaQuery.where(global);
        TypedQuery<TenantRoleUser> q = em.createQuery(criteriaQuery);

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
     * @param tenantRoleUserRoot table to be search
     * @return a filtered predicate
     */
    private Predicate getFilteredPredicate(SystemTenantRoleUserSearchFilter filter, CriteriaBuilder criteriaBuilder,
                                           Root<TenantRoleUser> tenantRoleUserRoot) {
        Predicate global;
        if(filter.isLogicConjunction()) {
            global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        } else {
            global = criteriaBuilder.isFalse(criteriaBuilder.literal(true));
        }

        global = getFieldPredicate(SystemVariables.TENANT_ROLE_ID.getFieldName(), filter.getTenantRoleId(), filter, criteriaBuilder,
                tenantRoleUserRoot, global);
        global = getFieldPredicate(SystemVariables.USER_ID.getFieldName(), filter.getUserId(), filter, criteriaBuilder,
                tenantRoleUserRoot, global);

        return global;
    }

    /**
     * Puts the requested fields into a predicate line
     * @param name of the field
     * @param value of the field
     * @param filter complete filter
     * @param criteriaBuilder to be used
     * @param tenantRoleUserRoot table to be used
     * @param global predicate to be added
     * @return a constructed predicate
     */
    private Predicate getFieldPredicate(String name, Object value,
                                        SystemTenantRoleUserSearchFilter filter,
                                        CriteriaBuilder criteriaBuilder,
                                        Root<TenantRoleUser> tenantRoleUserRoot,
                                        Predicate global) {
        if(value != null) {
            Predicate subPredicate;
            if (value instanceof String && !filter.isExact()) {
                subPredicate = criteriaBuilder.like(tenantRoleUserRoot.get(name),"%"+value+"%");
            }
            else {
                subPredicate = criteriaBuilder.equal(tenantRoleUserRoot.get(name), value);
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
     * CREATE a Tenant Role User association
     * @param tenantRoleUser role association information to be created
     * @throws UniquenessConstraintException in case of duplicated fields or records
     */
    @Override
    public void create(SystemTenantRoleUser tenantRoleUser) throws UniquenessConstraintException {
        EntityManager em = getEntityManager();
        boolean alreadyExistentRecords = isAssociationAlreadyExistent(tenantRoleUser.getUserId(),
                tenantRoleUser.getTenantRoleId(), em);
        if (alreadyExistentRecords) {
            throw new UniquenessConstraintException(GenericErrorCodeMessage.
                    DUPLICATED_FIELD.toString("userId and roleTenantId"));
        }
        em.persist(tenantRoleUser);
    }

    /**
     * Check if a user is already assigned/associated with a tenant role
     * @param userId User identifier
     * @param tenantRoleId TenantRole Identifier
     * @return true if already exists, otherwise returns false
     */
    @Override
    public boolean isAssociationAlreadyExistent(Long userId, Long tenantRoleId) {
        return isAssociationAlreadyExistent(userId, tenantRoleId, getEntityManager());
    }

    /**
     * Check if a user is already assigned/associated with a tenant role
     * @param userId Role identifier
     * @param tenantRoleId Tenant Identifier
     * @param em already created entity manager (reuse)
     * @return true in case the association exists in the db
     */
    protected boolean isAssociationAlreadyExistent(Long userId, Long tenantRoleId, EntityManager em) {
        if (userId == null) {
            throw new IllegalArgumentException(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString("user id"));
        }
        if (tenantRoleId == null) {
            throw new IllegalArgumentException(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString("id"));
        }

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> sc = cb.createQuery(Long.class);
        Root<TenantRoleUser> root = sc.from(TenantRoleUser.class);

        sc.select(cb.count(root)).
                where(
                        cb.equal(root.get(SystemVariables.USER_ID.getFieldName()),userId),
                        cb.equal(root.get(SystemVariables.TENANT_ROLE_ID.getFieldName()),tenantRoleId)
                );

        List<Long> count = em.createQuery(sc).getResultList();
        return !count.isEmpty() ? count.get(0) > 0 : false;
    }

    /**
     * Deletes a requested tenant role user association
     * @param tenantRoleUserId tenant role user to be deleted
     * @return true in case of success false in case of any error
     */
    @Override
    public boolean delete(Long tenantRoleUserId)  {
        if (tenantRoleUserId == null) {
            throw new IllegalArgumentException(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString("user id"));
        }
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<TenantRoleUser> criteriaDelete = cb.createCriteriaDelete(TenantRoleUser.class);
        Root<TenantRoleUser> tenantRoleUserRoot = criteriaDelete.from(TenantRoleUser.class);
        criteriaDelete.where(cb.equal(tenantRoleUserRoot.get(SystemVariables.ID.getFieldName()),tenantRoleUserId));
        return em.createQuery(criteriaDelete).executeUpdate() > 0;
    }

    /**
     * Retrieves strictly the TenantRoleUser id basing on tenantRole and user
     * @param tenantRole tenant identifier
     * @param user identifier
     * @return TenantRoleUser id
     */
    @Override
    public Optional<Long> getTenantRoleUserId(Long tenantRole, Long user) {
        if (tenantRole == null) {
            throw new IllegalArgumentException(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString("id"));
        }
        if(user == null) {
            throw new IllegalArgumentException(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString("user id"));
        }

        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> sc = cb.createQuery(Long.class);
        Root<TenantRoleUser> root = sc.from(TenantRoleUser.class);

        sc.select(root.get(SystemVariables.ID.getFieldName())).
                where(
                        cb.equal(root.get(SystemVariables.USER_ID.getFieldName()),user),
                        cb.equal(root.get(SystemVariables.TENANT_ROLE_ID.getFieldName()),tenantRole)
                );

        List<Long> list = em.createQuery(sc).getResultList();
        return !list.isEmpty() ? Optional.of(list.get(0)) : Optional.empty();
    }

    /**
     * Returns the active entity manager being used by the tenant role user service
     * @return the active entity manager
     */
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
