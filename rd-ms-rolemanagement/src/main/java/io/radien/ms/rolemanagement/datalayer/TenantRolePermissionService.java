/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
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
package io.radien.ms.rolemanagement.datalayer;

import io.radien.api.SystemVariables;
import io.radien.api.entity.Page;
import io.radien.api.model.tenantrole.SystemTenantRolePermission;
import io.radien.api.model.tenantrole.SystemTenantRolePermissionSearchFilter;
import io.radien.api.service.tenantrole.TenantRolePermissionServiceAccess;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.InvalidArgumentException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.client.entities.TenantRolePermissionSearchFilter;
import io.radien.ms.rolemanagement.entities.TenantRoleEntity;
import io.radien.ms.rolemanagement.entities.TenantRolePermissionEntity;
import io.radien.ms.rolemanagement.entities.TenantRoleUserEntity;
import io.radien.ms.rolemanagement.util.ValidationUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Repository (Service access implementation) for managing Tenant Role Permission entities
 * @author Newton Carvalho
 */
@Stateful
public class TenantRolePermissionService implements TenantRolePermissionServiceAccess {

    @PersistenceContext(unitName = "persistenceUnit")
    private transient EntityManager entityManager;

    private static final Logger log = LoggerFactory.getLogger(TenantRolePermissionService.class);

    /**
     * Gets all the tenant role permission associations into a pagination mode.
     * @param tenantRoleId search param that corresponds to the TenantRole id (Optional)
     * @param permissionId search param that corresponds to the permission id (Optional)
     * @param pageNo of the requested information. Where the tenant is.
     * @param pageSize total number of pages returned in the request
     * @param sortBy criteria field to be sorted
     * @param isAscending boolean value to show the values ascending or descending way
     * @return a page containing system tenant role permission associations.
     */
    @Override
    public Page<SystemTenantRolePermission> getAll(Long tenantRoleId, Long permissionId, int pageNo, int pageSize,
                                                   List<String> sortBy, boolean isAscending) {
        log.info("Retrieving tenant role permission associations using pagination mode, tenantRole  {} permission {}, page {}, size {}",
                tenantRoleId, permissionId, pageNo, pageSize);

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TenantRolePermissionEntity> criteriaQuery = criteriaBuilder.createQuery(TenantRolePermissionEntity.class);
        Root<TenantRolePermissionEntity> tenantRoleRoot = criteriaQuery.from(TenantRolePermissionEntity.class);

        criteriaQuery.select(tenantRoleRoot);
        Predicate global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));

        if (tenantRoleId != null) {
            global = criteriaBuilder.and(global, criteriaBuilder.equal(
                    tenantRoleRoot.get(SystemVariables.TENANT_ROLE_ID.getFieldName()), tenantRoleId));
        }

        if (permissionId != null) {
            global = criteriaBuilder.and(global, criteriaBuilder.equal(
                    tenantRoleRoot.get(SystemVariables.PERMISSION_ID.getFieldName()), permissionId));
        }

        if (tenantRoleId != null || permissionId != null) {
            criteriaQuery.where(global);
        }

        if(sortBy != null && !sortBy.isEmpty()){
            List<Order> orders = sortBy.stream().map(i-> isAscending ? criteriaBuilder.asc(tenantRoleRoot.get(i)) :
                    criteriaBuilder.desc(tenantRoleRoot.get(i))).collect(Collectors.toList());
            criteriaQuery.orderBy(orders);
        }

        TypedQuery<TenantRolePermissionEntity> q = entityManager.createQuery(criteriaQuery);

        q.setFirstResult((pageNo-1) * pageSize);
        q.setMaxResults(pageSize);

        List<? extends SystemTenantRolePermission> systemTenantRolesPermissions = q.getResultList();

        int totalRecords = Math.toIntExact(getCount(global, tenantRoleRoot));
        int totalPages = totalRecords%pageSize==0 ? totalRecords/pageSize : totalRecords/pageSize+1;

        return new Page<>(systemTenantRolesPermissions, pageNo, totalRecords, totalPages);
    }

    /**
     * Gets the System Tenant Role Permission searching by the PK (id).
     * @param tenantRolePermissionId to be searched.
     * @return the system tenant role requested to be found.
     */
    @Override
    public SystemTenantRolePermission get(Long tenantRolePermissionId) {
        return entityManager.find(TenantRolePermissionEntity.class, tenantRolePermissionId);
    }

    /**
     * Gets all the tenants role permission associations matching the given filter information
     * @param filter information to search
     * @return a list o found system tenant role permission associations
     */
    public List<? extends SystemTenantRolePermission> get(SystemTenantRolePermissionSearchFilter filter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TenantRolePermissionEntity> criteriaQuery = criteriaBuilder.createQuery(TenantRolePermissionEntity.class);
        Root<TenantRolePermissionEntity> root = criteriaQuery.from(TenantRolePermissionEntity.class);

        criteriaQuery.select(root);

        Predicate global = getFilteredPredicate((TenantRolePermissionSearchFilter) filter, criteriaBuilder, root);

        criteriaQuery.where(global);
        TypedQuery<TenantRolePermissionEntity> q = entityManager.createQuery(criteriaQuery);

        return q.getResultList();
    }

    /**
     * Retrieves the Permissions that exists for a Tenant Role Association (Optionally taking in account user)
     * @param tenantId Tenant identifier (Mandatory)
     * @param roleId Role identifier (Mandatory)
     * @param userId User identifier (Optional)
     * @return permission ids
     */
    @Override
    public List<Long> getPermissions(Long tenantId, Long roleId, Long userId) throws InvalidArgumentException {
        ValidationUtil.checkIfMandatoryParametersWereInformed(tenantId, roleId);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
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

        TypedQuery<Long> query = entityManager.createQuery(cq);
        return query.getResultList();
    }

    /**
     * CREATE a Tenant Role Permission association
     * @param tenantRolePermission role association information to be created
     * @throws UniquenessConstraintException in case of duplicated fields or records
     */
    @Override
    public void create(SystemTenantRolePermission tenantRolePermission) throws UniquenessConstraintException {
        checkUniqueness(tenantRolePermission);
        entityManager.persist(tenantRolePermission);
    }

    /**
     * UPDATE a Tenant Role Permission association
     * @param tenantRolePermission role association information to be updated
     * @throws UniquenessConstraintException in case of duplicated fields or records
     * @throws InvalidArgumentException in case of missing parameters
     */
    @Override
    public void update(SystemTenantRolePermission tenantRolePermission) throws UniquenessConstraintException, InvalidArgumentException {
        ValidationUtil.checkIfMandatoryParametersWereInformed(tenantRolePermission.getPermissionId(), tenantRolePermission.getTenantRoleId());
        checkUniqueness(tenantRolePermission);
        entityManager.merge(tenantRolePermission);
    }

    /**
     * Deletes a requested tenant role permission association
     * @param tenantRolePermissionId tenant role permission to be deleted
     * @return true in case of success false in case of any error
     */
    @Override
    public boolean delete(Long tenantRolePermissionId)  {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<TenantRolePermissionEntity> criteriaDelete = cb.createCriteriaDelete(TenantRolePermissionEntity.class);
        Root<TenantRolePermissionEntity> tenantRolePermissionRoot = criteriaDelete.from(TenantRolePermissionEntity.class);
        criteriaDelete.where(cb.equal(tenantRolePermissionRoot.get(SystemVariables.ID.getFieldName()),tenantRolePermissionId));
        return entityManager.createQuery(criteriaDelete).executeUpdate() > 0;
    }

    /**
     * Check if a permission is already assigned/associated with a tenant role
     * @param permissionId Permission identifier
     * @param tenantRoleId TenantRole Identifier
     * @return true if already exists, otherwise returns false
     */
    @Override
    public boolean isAssociationAlreadyExistent(Long permissionId, Long tenantRoleId) throws InvalidArgumentException {
        ValidationUtil.checkIfMandatoryParametersWereInformed(permissionId, tenantRoleId);
        return isAssociationAlreadyExistent(permissionId, tenantRoleId, null);
    }

    /**
     * Check if a permission is already assigned/associated with a tenant role
     * @param permissionId Permission identifier
     * @param tenantRoleId TenantRole Identifier
     * @param id tenantRolePermission identifier
     * @return true in case the association exists in the db
     */
    public boolean isAssociationAlreadyExistent(Long permissionId, Long tenantRoleId, Long id) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> sc = cb.createQuery(Long.class);
        Root<TenantRolePermissionEntity> root = sc.from(TenantRolePermissionEntity.class);

        if (id != null) {
            sc.select(cb.count(root)).
                    where(
                            cb.equal(root.get(SystemVariables.PERMISSION_ID.getFieldName()), permissionId),
                            cb.equal(root.get(SystemVariables.TENANT_ROLE_ID.getFieldName()), tenantRoleId),
                            cb.notEqual(root.get(SystemVariables.ID.getFieldName()), id)
                    );
        }
        else {
            sc.select(cb.count(root)).
                    where(
                            cb.equal(root.get(SystemVariables.PERMISSION_ID.getFieldName()), permissionId),
                            cb.equal(root.get(SystemVariables.TENANT_ROLE_ID.getFieldName()), tenantRoleId)
                    );
        }
        List<Long> count = entityManager.createQuery(sc).getResultList();
        return !count.isEmpty() && count.get(0) > 0;
    }

    /**
     * Retrieves strictly the TenantRolePermission id basing on tenantRole and user
     * @param tenantRole tenant identifier
     * @param permission identifier
     * @return TenantRolePermission id
     * @throws InvalidArgumentException if any parameters are missing
     */
    @Override
    public Optional<Long> getTenantRolePermissionId(Long tenantRole, Long permission) throws InvalidArgumentException {
        ValidationUtil.checkIfMandatoryParametersWereInformed(tenantRole, permission);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
        Root<TenantRolePermissionEntity> root = criteriaQuery.from(TenantRolePermissionEntity.class);

        criteriaQuery.select(root.get(SystemVariables.ID.getFieldName()));

        criteriaQuery.where(
                cb.equal(root.get(SystemVariables.TENANT_ROLE_ID.getFieldName()),tenantRole),
                cb.equal(root.get(SystemVariables.PERMISSION_ID.getFieldName()),permission)
        );

        TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
        List<Long> list = typedQuery.getResultList();
        return !list.isEmpty() ? Optional.of(list.get(0)) : Optional.empty();
    }

    /**
     * Count the number of existent associations (Tenant role user) in the DB.
     * @return the count of tenant role user associations
     */
    private long getCount(Predicate global, Root<TenantRolePermissionEntity> tenantRoleUserRoot) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        criteriaQuery.where(global);

        criteriaQuery.select(criteriaBuilder.count(tenantRoleUserRoot));

        TypedQuery<Long> q = entityManager.createQuery(criteriaQuery);

        return q.getSingleResult();
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
                                           Root<TenantRolePermissionEntity> tenantRolePermissionRoot) {
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
    private Predicate getFieldPredicate(String name, Object value, TenantRolePermissionSearchFilter filter, CriteriaBuilder criteriaBuilder,
                                        Root<TenantRolePermissionEntity> tenantRolePermissionRoot, Predicate global) {
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
     * Seek for eventual duplicated information
     * @param tenantRolePermission base entity bean to seek for repeated information
     * @throws UniquenessConstraintException thrown in case of repeated information
     */
    private void checkUniqueness(SystemTenantRolePermission tenantRolePermission) throws UniquenessConstraintException {
        boolean alreadyExistentRecords = isAssociationAlreadyExistent(tenantRolePermission.getPermissionId(), tenantRolePermission.getTenantRoleId(),
                tenantRolePermission.getId());
        if (alreadyExistentRecords) {
            throw new UniquenessConstraintException(GenericErrorCodeMessage.DUPLICATED_FIELD.
                    toString("permissionId and tenantRoleId"));
        }
    }
}
