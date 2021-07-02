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
import io.radien.api.entity.Page;
import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.model.tenantrole.SystemTenantRoleSearchFilter;
import io.radien.api.service.tenantrole.TenantRoleServiceAccess;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.TenantRoleException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.client.entities.TenantRoleSearchFilter;
import io.radien.ms.rolemanagement.entities.Role;
import io.radien.ms.rolemanagement.entities.TenantRole;
import io.radien.ms.rolemanagement.entities.TenantRolePermission;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository (Service access implementation) for managing Tenant Role entities
 * @author Newton Carvalho
 */
@Stateful
public class TenantRoleService implements TenantRoleServiceAccess {

    @PersistenceContext(unitName = "persistenceUnit")
    private EntityManager entityManager;

    private static final Logger log = LoggerFactory.getLogger(TenantRoleService.class);

    /**
     * Gets the System Tenant Role searching by the PK (id).
     * @param tenantRoleId to be searched.
     * @return the system tenant role requested to be found.
     */
    @Override
    public SystemTenantRole get(Long tenantRoleId) {
        return getEntityManager().find(TenantRole.class, tenantRoleId);
    }

    /**
     * Gets all the tenant role associations into a pagination mode.
     * @param pageNo of the requested information. Where the tenant is.
     * @param pageSize total number of pages returned in the request.
     * @return a page containing system tenant role associations.
     */
    @Override
    public Page<SystemTenantRole> getAll(int pageNo, int pageSize) {
        log.info("Retrieving tenant role associations using pagination mode");
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TenantRole> criteriaQuery = criteriaBuilder.createQuery(TenantRole.class);
        Root<TenantRole> tenantRoleRoot = criteriaQuery.from(TenantRole.class);

        criteriaQuery.select(tenantRoleRoot);
        Predicate global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));

        TypedQuery<TenantRole> q= entityManager.createQuery(criteriaQuery);

        q.setFirstResult((pageNo-1) * pageSize);
        q.setMaxResults(pageSize);

        List<? extends SystemTenantRole> systemTenantRoles = q.getResultList();

        int totalRecords = Math.toIntExact(getCount(global, tenantRoleRoot, entityManager));
        int totalPages = totalRecords%pageSize==0 ? totalRecords/pageSize : totalRecords/pageSize+1;

        log.info("Pagination ready to be shown");

        return new Page<SystemTenantRole>(systemTenantRoles, pageNo, totalRecords, totalPages);
    }

    /**
     * Count the number of existent associations (Tenant role) in the DB.
     * @return the count of tenant role associations
     */
    private long getCount(Predicate global, Root<TenantRole> userRoot, EntityManager em) {
        log.info("Gathering/counting the associations");
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.where(global);
        criteriaQuery.select(criteriaBuilder.count(userRoot));

        TypedQuery<Long> q= em.createQuery(criteriaQuery);

        return q.getSingleResult();
    }

    /**
     * Gets all the tenants role associations matching the given filter information
     * @param filter information to search
     * @return a list o found system tenant role associations
     */
    public List<? extends SystemTenantRole> get(SystemTenantRoleSearchFilter filter) {
        EntityManager em = getEntityManager();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<TenantRole> criteriaQuery = criteriaBuilder.createQuery(TenantRole.class);
        Root<TenantRole> root = criteriaQuery.from(TenantRole.class);

        criteriaQuery.select(root);

        Predicate global = getFilteredPredicate((TenantRoleSearchFilter) filter, criteriaBuilder, root);

        criteriaQuery.where(global);
        TypedQuery<TenantRole> q = em.createQuery(criteriaQuery);

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
     * @param tenantRoleRoot table to be search
     * @return a filtered predicate
     */
    private Predicate getFilteredPredicate(TenantRoleSearchFilter filter, CriteriaBuilder criteriaBuilder,
                                           Root<TenantRole> tenantRoleRoot) {
        Predicate global;
        if(filter.isLogicConjunction()) {
            global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        } else {
            global = criteriaBuilder.isFalse(criteriaBuilder.literal(true));
        }

        global = getFieldPredicate(SystemVariables.TENANT_ID.getFieldName(), filter.getTenantId(), filter, criteriaBuilder,
                tenantRoleRoot, global);
        global = getFieldPredicate(SystemVariables.ROLE_ID.getFieldName(), filter.getRoleId(), filter, criteriaBuilder,
                tenantRoleRoot, global);

        return global;
    }

    /**
     * Puts the requested fields into a predicate line
     * @param name of the field
     * @param value of the field
     * @param filter complete filter
     * @param criteriaBuilder to be used
     * @param tenantRoleRoot table to be used
     * @param global predicate to be added
     * @return a constructed predicate
     */
    private Predicate getFieldPredicate(String name, Object value, TenantRoleSearchFilter filter,
                                        CriteriaBuilder criteriaBuilder, Root<TenantRole> tenantRoleRoot,
                                        Predicate global) {
        if(value != null) {
            Predicate subPredicate = criteriaBuilder.equal(tenantRoleRoot.get(name), value);
            if(filter.isLogicConjunction()) {
                global = criteriaBuilder.and(global, subPredicate);
            } else {
                global = criteriaBuilder.or(global, subPredicate);
            }
        }
        return global;
    }

    /**
     * Save or update a Tenant Role association
     * @param tenantRole role association information to be created
     * @throws UniquenessConstraintException to be throw in case of duplication of fields or records
     */
    @Override
    public void save(SystemTenantRole tenantRole) throws UniquenessConstraintException {
        EntityManager em = getEntityManager();
        boolean alreadyExistentRecords = isAssociationAlreadyExistent(tenantRole.getRoleId(),
                tenantRole.getTenantId(), tenantRole.getId(), em);
        if (alreadyExistentRecords) {
            throw new UniquenessConstraintException(GenericErrorCodeMessage.DUPLICATED_FIELD.toString("roleId and TenantId"));
        }
        if (tenantRole.getId() == null) {
            em.persist(tenantRole);
        } else {
            em.merge(tenantRole);
        }
    }

    /**
     * Check if a role is already assigned/associated with a tenant
     * @param roleId Role identifier
     * @param tenantId Tenant Identifier
     * @return true if already exists, otherwise returns false
     */
    @Override
    public boolean isAssociationAlreadyExistent(Long roleId, Long tenantId) {
        return isAssociationAlreadyExistent(roleId, tenantId, null, getEntityManager());
    }

    /**
     * Check if a role is already assigned/associated with a tenant
     * @param roleId Role identifier
     * @param tenantId Tenant Identifier
     * @param currentTenantRoleId Optional parameter. Corresponds to a current tenant role,
     *                            and In case of an update operation this helps to make sure
     *                            that a possible new combination (role+tenant) do not exist for other ids
     * @param em already created entity manager (reuse)
     * @return true in case association exists in the db
     */
    protected boolean isAssociationAlreadyExistent(Long roleId, Long tenantId,
                                                   Long currentTenantRoleId, EntityManager em) {
        if (roleId == null) {
            throw new IllegalArgumentException(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString("role id"));
        }
        if (tenantId == null) {
            throw new IllegalArgumentException(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString("tenant id"));
        }

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> sc = cb.createQuery(Long.class);
        Root<TenantRole> root = sc.from(TenantRole.class);

        sc.select(cb.count(root));

        if (currentTenantRoleId != null) {
            sc.where(
                    cb.equal(root.get(SystemVariables.TENANT_ID.getFieldName()), tenantId),
                    cb.equal(root.get(SystemVariables.ROLE_ID.getFieldName()), roleId),
                    cb.notEqual(root.get(SystemVariables.ID.getFieldName()), currentTenantRoleId)
            );
        }
        else {
            sc.where(
                    cb.equal(root.get(SystemVariables.TENANT_ID.getFieldName()), tenantId),
                    cb.equal(root.get(SystemVariables.ROLE_ID.getFieldName()), roleId)
            );
        }

        List<Long> count = em.createQuery(sc).getResultList();
        return !count.isEmpty() ? count.get(0) > 0 : false;
    }

    /**
     * Deletes a requested tenant role association
     * @param tenantRoleId tenant role to be deleted
     * @return true in case of success, false otherwise
     * @throws TenantRoleException in case of any inconsistency found
     */
    @Override
    public boolean delete(Long tenantRoleId) throws TenantRoleException {
        if (tenantRoleId == null) {
            throw new IllegalArgumentException(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString("id"));
        }

        EntityManager em = getEntityManager();

        // Check if we have some user associated
        if (hasUsersAssociated(tenantRoleId, em)) {
            throw new TenantRoleException("There are users associated with tenant role " + tenantRoleId);
        }

        // Check if we have some permission associated
        if (hasPermissionsAssociated(tenantRoleId, em)) {
            throw new TenantRoleException("There are permissions associated with tenant role " + tenantRoleId);
        }

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<TenantRole> criteriaDelete = cb.createCriteriaDelete(TenantRole.class);
        Root<TenantRole> tenantRoleRoot = criteriaDelete.from(TenantRole.class);
        criteriaDelete.where(cb.equal(tenantRoleRoot.get(SystemVariables.ID.getFieldName()),tenantRoleId));
        return em.createQuery(criteriaDelete).executeUpdate() > 0;
    }

    /**
     * Verifies if there are users linked with the Tenant Role association
     * @param tenantRoleId Tenant Role association identifier
     * @param em Entity manager
     * @return true if exists, otherwise false
     */
    protected boolean hasUsersAssociated(Long tenantRoleId, EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> sc = cb.createQuery(Long.class);
        Root<TenantRoleUser> root = sc.from(TenantRoleUser.class);

        sc.select(cb.count(root));

        sc.where(cb.equal(root.get(SystemVariables.TENANT_ROLE_ID.getFieldName()),tenantRoleId));

        List<Long> count = em.createQuery(sc).getResultList();
        return !count.isEmpty() ? count.get(0) > 0 : false;
    }

    /**
     * Verifies if there are permissions linked with the Tenant Role association
     * @param tenantRoleId Tenant Role association identifier
     * @param em Entity manager
     * @return true if exists, otherwise false
     */
    protected boolean hasPermissionsAssociated(Long tenantRoleId, EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> sc = cb.createQuery(Long.class);
        Root<TenantRolePermission> root = sc.from(TenantRolePermission.class);

        sc.select(cb.count(root));

        sc.where(cb.equal(root.get(SystemVariables.TENANT_ROLE_ID.getFieldName()),tenantRoleId));
        List<Long> count = em.createQuery(sc).getResultList();
        return !count.isEmpty() ? count.get(0) > 0 : false;
    }

    /**
     * Retrieves the Permissions that exists for a Tenant Role Association (Optionally taking in account user)
     * @param tenantId Tenant identifier (Mandatory)
     * @param roleId Role identifier (Mandatory)
     * @param userId User identifier (Optional)
     * @return permission ids
     */
    @Override
    public List<Long> getPermissions(Long tenantId, Long roleId, Long userId) {
        if (tenantId == null) {
            throw new IllegalArgumentException(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString("tenant id"));
        }
        if (roleId == null) {
            throw new IllegalArgumentException(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString("role id"));
        }
        EntityManager em = getEntityManager();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<TenantRole> tenantRoleRoot = cq.from(TenantRole.class);
        Root<TenantRolePermission> tenantRolePermissionRoot = cq.from(TenantRolePermission.class);

        cq.select(tenantRolePermissionRoot.get(SystemVariables.PERMISSION_ID.getFieldName()));

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(tenantRoleRoot.get(SystemVariables.TENANT_ID.getFieldName()), tenantId));
        predicates.add(cb.equal(tenantRoleRoot.get(SystemVariables.ROLE_ID.getFieldName()), roleId));
        predicates.add(cb.equal(tenantRoleRoot.get(SystemVariables.ID.getFieldName()), tenantRolePermissionRoot.get(SystemVariables.TENANT_ROLE_ID.getFieldName())));
        if (userId != null) {
            Root<TenantRoleUser> tenantRoleUserRoot = cq.from(TenantRoleUser.class);
            predicates.add(cb.equal(tenantRoleRoot.get(SystemVariables.ID.getFieldName()), tenantRoleUserRoot.get(SystemVariables.TENANT_ROLE_ID.getFieldName())));
            predicates.add(cb.equal(tenantRoleUserRoot.get(SystemVariables.USER_ID.getFieldName()), userId));
        }

        cq.where(cb.and(predicates.toArray(new Predicate[0])));

        TypedQuery<Long> query = em.createQuery(cq);
        return query.getResultList();
    }

    /**
     * Retrieves the existent Tenants for a User (Optionally for a specific role)
     * @param userId User identifier
     * @param roleId Role identifier (Optional)
     * @return List containing tenant ids
     */
    @Override
    public List<Long> getTenants(Long userId, Long roleId) {
        if (userId == null) {
            throw new IllegalArgumentException(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString("user id"));
        }

        EntityManager em = getEntityManager();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<TenantRole> tenantRoleRoot = cq.from(TenantRole.class);
        Root<TenantRoleUser> tenantRoleUserRoot = cq.from(TenantRoleUser.class);

        cq.select(tenantRoleRoot.get(SystemVariables.TENANT_ID.getFieldName()));
        cq.distinct(true);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(tenantRoleRoot.get(SystemVariables.ID.getFieldName()), tenantRoleUserRoot.get(SystemVariables.TENANT_ROLE_ID.getFieldName())));
        if (roleId != null) {
            predicates.add(cb.equal(tenantRoleRoot.get(SystemVariables.ROLE_ID.getFieldName()), roleId));
        }
        predicates.add(cb.equal(tenantRoleUserRoot.get(SystemVariables.USER_ID.getFieldName()), userId));

        cq.where(cb.and(predicates.toArray(new Predicate[0])));

        TypedQuery<Long> query = em.createQuery(cq);
        return query.getResultList();
    }

    /**
     * Check if a User has some Role (Optionally for a specific Tenant)
     * @param userId User identifier
     * @param roleNames Role name identifier
     * @param tenantId Tenant identifier (Optional)
     * @return true if has some of the informed roles, otherwise fase
     */
    @Override
    public boolean hasAnyRole(Long userId, List<String> roleNames, Long tenantId) {
        if (userId == null) {
            throw new IllegalArgumentException(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString("user id"));
        }

        if (roleNames == null || roleNames.isEmpty()) {
            throw new IllegalArgumentException(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString("role name"));
        }

        EntityManager em = getEntityManager();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Role> roleRoot = cq.from(Role.class);
        Root<TenantRole> tenantRoleRoot = cq.from(TenantRole.class);
        Root<TenantRoleUser> tenantRoleUserRoot = cq.from(TenantRoleUser.class);

        cq.select(cb.count(tenantRoleRoot));

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(roleRoot.get(SystemVariables.NAME.getFieldName()).in(roleNames));
        predicates.add(cb.equal(roleRoot.get(SystemVariables.ID.getFieldName()), tenantRoleRoot.get(SystemVariables.ROLE_ID.getFieldName())));
        predicates.add(cb.equal(tenantRoleRoot.get(SystemVariables.ID.getFieldName()), tenantRoleUserRoot.get(SystemVariables.TENANT_ROLE_ID.getFieldName())));
        if (tenantId != null) {
            predicates.add(cb.equal(tenantRoleRoot.get(SystemVariables.TENANT_ID.getFieldName()), tenantId));
        }
        predicates.add(cb.equal(tenantRoleUserRoot.get(SystemVariables.USER_ID.getFieldName()), userId));

        cq.where(cb.and(predicates.toArray(new Predicate[0])));

        List<Long> count = em.createQuery(cq).getResultList();
        return !count.isEmpty() ? count.get(0) > 0 : false;
    }

    /**
     * Check if a User has a Permission (Optionally for a specific Tenant)
     * @param userId User identifier
     * @param permissionId Permission identifier
     * @param tenantId Tenant identifier (Optional)
     * @return true if has some of the informed roles, otherwise fase
     */
    @Override
    public boolean hasPermission(Long userId, Long permissionId, Long tenantId) {
        EntityManager em = getEntityManager();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<TenantRole> tenantRoleRoot = cq.from(TenantRole.class);
        Root<TenantRolePermission> tenantRolePermissionRoot = cq.from(TenantRolePermission.class);
        Root<TenantRoleUser> tenantRoleUserRoot = cq.from(TenantRoleUser.class);

        cq.select(cb.count(tenantRoleRoot));

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(tenantRoleRoot.get(SystemVariables.ID.getFieldName()), tenantRolePermissionRoot.get(SystemVariables.TENANT_ROLE_ID.getFieldName())));
        predicates.add(cb.equal(tenantRoleRoot.get(SystemVariables.ID.getFieldName()), tenantRoleUserRoot.get(SystemVariables.TENANT_ROLE_ID.getFieldName())));
        predicates.add(cb.equal(tenantRolePermissionRoot.get(SystemVariables.PERMISSION_ID.getFieldName()), permissionId));
        predicates.add(cb.equal(tenantRoleUserRoot.get(SystemVariables.USER_ID.getFieldName()), userId));
        if (tenantId != null) {
            predicates.add(cb.equal(tenantRoleRoot.get(SystemVariables.TENANT_ID.getFieldName()), tenantId));
        }

        cq.where(cb.and(predicates.toArray(new Predicate[0])));

        List<Long> count = em.createQuery(cq).getResultList();
        return !count.isEmpty() ? count.get(0) > 0 : false;
    }

    /**
     * Retrieves strictly the TenantRole id basing on tenant and role
     * @param tenant tenant identifier
     * @param  role role identifier
     * @return TenantRole id
     */
    @Override
    public Optional<Long> getTenantRoleId(Long tenant, Long role) {
        if (tenant == null) {
            throw new IllegalArgumentException(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString("tenant id"));
        }
        if(role == null) {
            throw new IllegalArgumentException(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString("role id"));
        }
        EntityManager em = getEntityManager();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<TenantRole> tenantRoleRoot = criteriaQuery.from(TenantRole.class);

        criteriaQuery.select(tenantRoleRoot.get(SystemVariables.ID.getFieldName())).
                where(
                        criteriaBuilder.equal(tenantRoleRoot.get(SystemVariables.TENANT_ID.getFieldName()), tenant),
                        criteriaBuilder.equal(tenantRoleRoot.get(SystemVariables.ROLE_ID.getFieldName()), role)
                );

        TypedQuery<Long> typedQuery = em.createQuery(criteriaQuery);
        List<Long> list = typedQuery.getResultList();
        return !list.isEmpty() ? Optional.of(list.get(0)) : Optional.empty();
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }
}
