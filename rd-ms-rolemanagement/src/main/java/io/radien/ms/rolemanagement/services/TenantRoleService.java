/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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
import io.radien.api.model.permission.SystemPermission;
import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.model.tenantrole.SystemTenantRoleSearchFilter;
import io.radien.api.service.tenantrole.TenantRoleServiceAccess;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.PermissionNotFoundException;
import io.radien.exception.TenantRoleException;
import io.radien.exception.TenantRoleNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.client.entities.TenantRoleSearchFilter;
import io.radien.ms.rolemanagement.entities.RoleEntity;
import io.radien.ms.rolemanagement.entities.TenantRoleEntity;
import io.radien.ms.rolemanagement.entities.TenantRolePermissionEntity;
import io.radien.ms.rolemanagement.entities.TenantRoleUserEntity;
import java.util.Collection;
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
        return getEntityManager().find(TenantRoleEntity.class, tenantRoleId);
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
        EntityManager em = getEntityManager();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<TenantRoleEntity> criteriaQuery = criteriaBuilder.createQuery(TenantRoleEntity.class);
        Root<TenantRoleEntity> tenantRoleRoot = criteriaQuery.from(TenantRoleEntity.class);

        criteriaQuery.select(tenantRoleRoot);
        Predicate global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));

        TypedQuery<TenantRoleEntity> q= em.createQuery(criteriaQuery);

        q.setFirstResult((pageNo-1) * pageSize);
        q.setMaxResults(pageSize);

        List<? extends SystemTenantRole> systemTenantRoles = q.getResultList();

        int totalRecords = Math.toIntExact(getCount(global, tenantRoleRoot, em));
        int totalPages = totalRecords%pageSize==0 ? totalRecords/pageSize : totalRecords/pageSize+1;

        log.info("Pagination ready to be shown");

        return new Page<SystemTenantRole>(systemTenantRoles, pageNo, totalRecords, totalPages);
    }

    @Override
    public long count(){
        EntityManager em = getEntityManager();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<TenantRoleEntity> criteriaQuery = criteriaBuilder.createQuery(TenantRoleEntity.class);
        Root<TenantRoleEntity> tenantRoleRoot = criteriaQuery.from(TenantRoleEntity.class);
        return getCount(null,tenantRoleRoot,em);
    }

    /**
     * Count the number of existent associations (Tenant role) in the DB.
     * @return the count of tenant role associations
     */
    private long getCount(Predicate global, Root<TenantRoleEntity> tenantRoleRoot, EntityManager em) {
        log.info("Gathering/counting the associations");
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        if(global != null) {
            criteriaQuery.where(global);
        }
        criteriaQuery.select(criteriaBuilder.count(tenantRoleRoot));

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
        CriteriaQuery<TenantRoleEntity> criteriaQuery = criteriaBuilder.createQuery(TenantRoleEntity.class);
        Root<TenantRoleEntity> root = criteriaQuery.from(TenantRoleEntity.class);

        criteriaQuery.select(root);

        Predicate global = getFilteredPredicate((TenantRoleSearchFilter) filter, criteriaBuilder, root);

        criteriaQuery.where(global);
        TypedQuery<TenantRoleEntity> q = em.createQuery(criteriaQuery);

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
                                           Root<TenantRoleEntity> tenantRoleRoot) {
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
                                        CriteriaBuilder criteriaBuilder, Root<TenantRoleEntity> tenantRoleRoot,
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
     * Creates a Tenant Role association
     * @param tenantRole role association information to be created
     * @throws UniquenessConstraintException in case of duplicated combination of tenant and role
     */
    @Override
    public void create(SystemTenantRole tenantRole) throws UniquenessConstraintException {
        EntityManager em = getEntityManager();
        checkUniqueness(tenantRole, em);
        em.persist(tenantRole);
    }

    /**
     * Updates a Tenant Role association
     * @param tenantRole role association information to be updated
     * @throws UniquenessConstraintException in case of duplicated combination of tenant and role
     * @throws TenantRoleNotFoundException in case of not existent tenantRole for the give id
     */
    @Override
    public void update(SystemTenantRole tenantRole) throws UniquenessConstraintException, TenantRoleNotFoundException {
        EntityManager em = getEntityManager();
        if(em.find(TenantRoleEntity.class, tenantRole.getId()) == null) {
            throw new TenantRoleNotFoundException(GenericErrorCodeMessage.RESOURCE_NOT_FOUND.toString());
        }
        checkUniqueness(tenantRole, em);
        em.merge(tenantRole);
    }

    /**
     * Seek for eventual duplicated information
     * @param tenantRole base entity bean to seek for repeated information
     * @param em entity manager
     * @throws UniquenessConstraintException thrown in case of repeated information
     */
    private void checkUniqueness(SystemTenantRole tenantRole, EntityManager em) throws UniquenessConstraintException{
        boolean alreadyExistentRecords = isAssociationAlreadyExistent(tenantRole.getRoleId(),
                tenantRole.getTenantId(), tenantRole.getId(), em);
        if (alreadyExistentRecords) {
            throw new UniquenessConstraintException(GenericErrorCodeMessage.DUPLICATED_FIELD.toString("roleId and TenantId"));
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
            throw new IllegalArgumentException(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString(SystemVariables.ROLE_ID.getLabel()));
        }
        if (tenantId == null) {
            throw new IllegalArgumentException(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString(SystemVariables.TENANT_ID.getLabel()));
        }

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> sc = cb.createQuery(Long.class);
        Root<TenantRoleEntity> root = sc.from(TenantRoleEntity.class);

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
        return !count.isEmpty() && count.get(0) > 0;
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
            throw new IllegalArgumentException(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString(SystemVariables.ID.getLabel()));
        }

        EntityManager em = getEntityManager();

        // Check if we have some user associated
        if (hasUsersAssociated(tenantRoleId, em)) {
            throw new TenantRoleException(GenericErrorCodeMessage.TENANT_ROLE_USERS_ASSOCIATED_WITH_TENANT_ROLE.toString() + tenantRoleId);
        }

        // Check if we have some permission associated
        if (hasPermissionsAssociated(tenantRoleId, em)) {
            throw new TenantRoleException(GenericErrorCodeMessage.TENANT_ROLE_PERMISSIONS_ASSOCIATED_WITH_TENANT_ROLE.toString() + tenantRoleId);
        }

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<TenantRoleEntity> criteriaDelete = cb.createCriteriaDelete(TenantRoleEntity.class);
        Root<TenantRoleEntity> tenantRoleRoot = criteriaDelete.from(TenantRoleEntity.class);
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
        Root<TenantRoleUserEntity> root = sc.from(TenantRoleUserEntity.class);

        sc.select(cb.count(root));

        sc.where(cb.equal(root.get(SystemVariables.TENANT_ROLE_ID.getFieldName()),tenantRoleId));

        List<Long> count = em.createQuery(sc).getResultList();
        return !count.isEmpty() && count.get(0) > 0;
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
        Root<TenantRolePermissionEntity> root = sc.from(TenantRolePermissionEntity.class);

        sc.select(cb.count(root));

        sc.where(cb.equal(root.get(SystemVariables.TENANT_ROLE_ID.getFieldName()),tenantRoleId));
        List<Long> count = em.createQuery(sc).getResultList();
        return !count.isEmpty() && count.get(0) > 0;
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
            throw new IllegalArgumentException(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString(SystemVariables.TENANT_ID.getLabel()));
        }
        if (roleId == null) {
            throw new IllegalArgumentException(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString(SystemVariables.ROLE_ID.getLabel()));
        }
        EntityManager em = getEntityManager();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<TenantRoleEntity> tenantRoleRoot = cq.from(TenantRoleEntity.class);
        Root<TenantRolePermissionEntity> tenantRolePermissionRoot = cq.from(TenantRolePermissionEntity.class);

        cq.select(tenantRolePermissionRoot.get(SystemVariables.PERMISSION_ID.getFieldName()));

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(tenantRoleRoot.get(SystemVariables.TENANT_ID.getFieldName()), tenantId));
        predicates.add(cb.equal(tenantRoleRoot.get(SystemVariables.ROLE_ID.getFieldName()), roleId));
        predicates.add(cb.equal(tenantRoleRoot.get(SystemVariables.ID.getFieldName()), tenantRolePermissionRoot.get(SystemVariables.TENANT_ROLE_ID.getFieldName())));
        if (userId != null) {
            Root<TenantRoleUserEntity> tenantRoleUserRoot = cq.from(TenantRoleUserEntity.class);
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
            throw new IllegalArgumentException(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString(SystemVariables.USER_ID.getLabel()));
        }

        EntityManager em = getEntityManager();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<TenantRoleEntity> tenantRoleRoot = cq.from(TenantRoleEntity.class);
        Root<TenantRoleUserEntity> tenantRoleUserRoot = cq.from(TenantRoleUserEntity.class);

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
     * Retrieves the existent Roles for a User of a specific Tenant
     * @param userId User identifier
     * @param tenantId Tenant identifier
     * @return List containing role ids
     */
    @Override
    public List<Long> getRoleIdsForUserTenant(Long userId, Long tenantId) {
        if (userId == null || tenantId == null) {
            throw new IllegalArgumentException(GenericErrorCodeMessage.INFO_TENANT_AND_USER_ID.toString());
        }

        EntityManager em = getEntityManager();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<TenantRoleEntity> tenantRoleRoot = cq.from(TenantRoleEntity.class);
        Root<TenantRoleUserEntity> tenantRoleUserRoot = cq.from(TenantRoleUserEntity.class);

        cq.select(tenantRoleRoot.get(SystemVariables.ROLE_ID.getFieldName()));
        cq.distinct(true);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(tenantRoleRoot.get(SystemVariables.ID.getFieldName()),
                tenantRoleUserRoot.get(SystemVariables.TENANT_ROLE_ID.getFieldName())));
        predicates.add(cb.equal(tenantRoleRoot.get(SystemVariables.TENANT_ID.getFieldName()), tenantId));
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
            throw new IllegalArgumentException(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString(SystemVariables.USER_ID.getLabel()));
        }

        if (roleNames == null || roleNames.isEmpty()) {
            throw new IllegalArgumentException(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString(SystemVariables.ROLE_NAME.getLabel()));
        }

        EntityManager em = getEntityManager();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<RoleEntity> roleRoot = cq.from(RoleEntity.class);
        Root<TenantRoleEntity> tenantRoleRoot = cq.from(TenantRoleEntity.class);
        Root<TenantRoleUserEntity> tenantRoleUserRoot = cq.from(TenantRoleUserEntity.class);

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
        return !count.isEmpty() && count.get(0) > 0;
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
        Root<TenantRoleEntity> tenantRoleRoot = cq.from(TenantRoleEntity.class);
        Root<TenantRolePermissionEntity> tenantRolePermissionRoot = cq.from(TenantRolePermissionEntity.class);
        Root<TenantRoleUserEntity> tenantRoleUserRoot = cq.from(TenantRoleUserEntity.class);

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
        return !count.isEmpty() && count.get(0) > 0;
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
            throw new IllegalArgumentException(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString(SystemVariables.TENANT_ID.getLabel()));
        }
        if(role == null) {
            throw new IllegalArgumentException(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString(SystemVariables.ROLE_ID.getLabel()));
        }
        EntityManager em = getEntityManager();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<TenantRoleEntity> tenantRoleRoot = criteriaQuery.from(TenantRoleEntity.class);

        criteriaQuery.select(tenantRoleRoot.get(SystemVariables.ID.getFieldName())).
                where(
                        criteriaBuilder.equal(tenantRoleRoot.get(SystemVariables.TENANT_ID.getFieldName()), tenant),
                        criteriaBuilder.equal(tenantRoleRoot.get(SystemVariables.ROLE_ID.getFieldName()), role)
                );

        TypedQuery<Long> typedQuery = em.createQuery(criteriaQuery);
        List<Long> list = typedQuery.getResultList();
        return !list.isEmpty() ? Optional.of(list.get(0)) : Optional.empty();
    }

    /**
     * Retrieves strictly the TenantRole ids based on tenant and roleIds
     * @param tenantId Tenant id
     * @param roleIds Collection Role ids
     * @return List of TenantRole ids
     */
    @Override
    public List<Long> getTenantRoleIds(Long tenantId, Collection<Long> roleIds){
        EntityManager em = getEntityManager();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<TenantRoleEntity> tenantRoleRoot = criteriaQuery.from(TenantRoleEntity.class);

        criteriaQuery.select(tenantRoleRoot.get(SystemVariables.ID.getFieldName())).
                where(
                        criteriaBuilder.equal(tenantRoleRoot.get(SystemVariables.TENANT_ID.getFieldName()), tenantId),
                        tenantRoleRoot.get(SystemVariables.ROLE_ID.getFieldName()).in(roleIds)
                );

        TypedQuery<Long> typedQuery = em.createQuery(criteriaQuery);
        return typedQuery.getResultList();
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }
}
