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

import java.util.ArrayList;
import java.util.List;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Order;

import io.radien.api.SystemVariables;
import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemPermission;
import io.radien.api.model.permission.SystemPermissionSearchFilter;
import io.radien.api.service.permission.PermissionServiceAccess;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.UniquenessConstraintException;

import io.radien.ms.permissionmanagement.client.entities.PermissionSearchFilter;
import io.radien.ms.permissionmanagement.model.Permission;
import io.radien.persistencelib.PredicateMapper;

/**
 * Permission DB connection requests
 * All the requests made between the permission entity and the database will be performed in here
 *
 * @author Newton Carvalho
 */
@Stateless
public class PermissionService implements PermissionServiceAccess {

    @Inject
    private EntityManagerHolder holder;

    /**
     * Permission service empty constructor
     */
    public PermissionService() {
        //empty
    }

    /**
     * Count the number of Permissions existent in the DB.
     * @return the count of Permissions
     */
    private long getCount(Predicate global, Root<Permission> permissionRoot, EntityManager em) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        criteriaQuery.where(global);

        criteriaQuery.select(criteriaBuilder.count(permissionRoot));

        TypedQuery<Long> q=em.createQuery(criteriaQuery);
        return q.getSingleResult();
    }


    /**
     * Gets the System Permission searching by the PK (id).
     * @param permissionId to be searched.
     * @return the system Permission requested to be found.
     */
    @Override
    public SystemPermission get(Long permissionId)  {
        return getEntityManager().find(Permission.class, permissionId);
    }

    /**
     * Gets a list of System Permissions searching by multiple PK's (id) requested in a list.
     * @param permissionId to be searched.
     * @return the list of system Permissions requested to be found.
     */
    @Override
    public List<SystemPermission> get(List<Long> permissionId) {
        ArrayList<SystemPermission> results = new ArrayList<>();
        if(permissionId == null || permissionId.isEmpty()){
            return results;
        }

        EntityManager em = getEntityManager();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Permission> criteriaQuery = criteriaBuilder.createQuery(Permission.class);
        Root<Permission> permissionRoot = criteriaQuery.from(Permission.class);
        criteriaQuery.select(permissionRoot);
        criteriaQuery.where(permissionRoot.get(SystemVariables.ID.getFieldName()).in(permissionId));

        TypedQuery<Permission> q=em.createQuery(criteriaQuery);

        return new ArrayList<>(q.getResultList());
    }

    /**
     * Gets all the Permissions into a pagination mode.
     * Can be filtered by name Permission.
     * @param search name description for some permission
     * @param pageNo of the requested information. Where the Permission is.
     * @param pageSize total number of pages returned in the request.
     * @param sortBy sort filter criteria.
     * @param isAscending ascending filter criteria.
     * @return a page of system Permissions.
     */
    @Override
    public Page<SystemPermission> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) {
        EntityManager em = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Permission> criteriaQuery = criteriaBuilder.createQuery(Permission.class);
        Root<Permission> permissionRoot = criteriaQuery.from(Permission.class);

        criteriaQuery.select(permissionRoot);

        Predicate global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        if(search!= null) {
            global = criteriaBuilder.and(criteriaBuilder.like(permissionRoot.get(SystemVariables.NAME.getFieldName()), search));
            criteriaQuery.where(global);
        }

        if(sortBy != null && !sortBy.isEmpty()){
            List<Order> orders;
            if(isAscending){
                orders = sortBy.stream().map(i->criteriaBuilder.asc(permissionRoot.get(i))).collect(Collectors.toList());
            } else {
                orders = sortBy.stream().map(i->criteriaBuilder.desc(permissionRoot.get(i))).collect(Collectors.toList());
            }
            criteriaQuery.orderBy(orders);
        }

        TypedQuery<Permission> q=em.createQuery(criteriaQuery);

        q.setFirstResult((pageNo-1) * pageSize);
        q.setMaxResults(pageSize);

        List<? extends SystemPermission> systemPermissions = q.getResultList();

        int totalRecords = Math.toIntExact(getCount(global, permissionRoot, em));
        int totalPages = totalRecords%pageSize==0 ? totalRecords/pageSize : totalRecords/pageSize+1;

        return new Page<SystemPermission>(systemPermissions, pageNo, totalRecords, totalPages);
    }

    /**
     * Saves or updates the requested and given Permission information into the DB.
     * @param permission to be added/inserted or updated
     * @throws UniquenessConstraintException in case of duplicated name
     */
    @Override
    public void save(SystemPermission permission) throws UniquenessConstraintException {
        EntityManager em = getEntityManager();
        List<Permission> alreadyExistentRecords = searchDuplicatedName(permission, em);
        if (!alreadyExistentRecords.isEmpty()) {
            throw new UniquenessConstraintException(GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Name"));
        }
        boolean existsForActionAndResource = existsForResourceAndAction(permission, em);
        if (existsForActionAndResource) {
            throw new UniquenessConstraintException(GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Action and Resource"));
        }
        if (permission.getId() == null) {
            em.persist(permission);
        } else {
            em.merge(permission);
        }
    }

    /**
     * Query to validate if an existent name (for permission) already exists in the database or not.
     * @param permission Permission information to look up.
     * @return list of Permissions with duplicated information.
     */
    private List<Permission> searchDuplicatedName(SystemPermission permission, EntityManager em) {
        List<Permission> alreadyExistentRecords;
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Permission> criteriaQuery = criteriaBuilder.createQuery(Permission.class);
        Root<Permission> permissionRoot = criteriaQuery.from(Permission.class);
        criteriaQuery.select(permissionRoot);
        Predicate global = criteriaBuilder.equal(permissionRoot.get(SystemVariables.NAME.getFieldName()), permission.getName());
        if(permission.getId()!= null) {
            global=criteriaBuilder.and(global, criteriaBuilder.notEqual(permissionRoot.get(SystemVariables.ID.getFieldName()), permission.getId()));
        }
        criteriaQuery.where(global);
        TypedQuery<Permission> q = em.createQuery(criteriaQuery);
        alreadyExistentRecords = q.getResultList();
        return alreadyExistentRecords;
    }

    /**
     * Deletes a unique Permission selected by his id.
     * @param permissionId to be deleted.
     */
    @Override
    public void delete(Long permissionId) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<Permission> criteriaDelete = cb.createCriteriaDelete(Permission.class);
        Root<Permission> permissionRoot = criteriaDelete.from(Permission.class);

        criteriaDelete.where(cb.equal(permissionRoot.get(SystemVariables.ID.getFieldName()),permissionId));
        em.createQuery(criteriaDelete).executeUpdate();
    }

    /**
     * Deletes a list of Permissions selected by his id.
     * @param permissionIds to be deleted.
     */
    @Override
    public void delete(Collection<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return;
        }
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<Permission> criteriaDelete = cb.createCriteriaDelete(Permission.class);
        Root<Permission> permissionRoot = criteriaDelete.from(Permission.class);

        criteriaDelete.where(permissionRoot.get(SystemVariables.ID.getFieldName()).in(permissionIds));
        em.createQuery(criteriaDelete).executeUpdate();
    }

    /**
     * Get PermissionsBy unique columns
     * @param filter entity with available filters to search Permission
     */
    @Override
    public List<? extends SystemPermission> getPermissions(SystemPermissionSearchFilter filter) {
        EntityManager em = getEntityManager();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Permission> criteriaQuery = criteriaBuilder.createQuery(Permission.class);
        Root<Permission> permissionRoot = criteriaQuery.from(Permission.class);

        criteriaQuery.select(permissionRoot);

        Predicate global = getFilteredPredicate((PermissionSearchFilter) filter, criteriaBuilder, permissionRoot);

        criteriaQuery.where(global);
        TypedQuery<Permission> q=em.createQuery(criteriaQuery);

        return q.getResultList();
    }

    /**
     * Will filter all the fields given in the criteria builder and in the filter and create the
     * where clause for the query
     * @param filter fields to be searched for
     * @param criteriaBuilder database query builder
     * @param permissionRoot database table to search the information
     * @return a constructed predicate with the fields needed to be search
     */
    private Predicate getFilteredPredicate(PermissionSearchFilter filter,
                                           CriteriaBuilder criteriaBuilder, Root<Permission> permissionRoot) {
        Predicate global;

        // is LogicalConjunction represents if you join the fields on the predicates with "or" or "and"
        // the predicate is build with the logic (start,operator,newPredicate)
        // where start represents the already joined predicates
        // operator is "and" or "or"
        // depending on the operator the start may need to be true or false
        // true and predicate1 and predicate2
        // false or predicate1 or predicate2
        if(filter.isLogicConjunction()) {
            global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        } else {
            global = criteriaBuilder.isFalse(criteriaBuilder.literal(true));
        }

        if (filter.getIds() != null && !filter.getIds().isEmpty()) {
            Predicate in = permissionRoot.get(SystemVariables.ID.getFieldName()).in(filter.getIds());
            if(filter.isLogicConjunction()) {
                global = criteriaBuilder.and(global, in);
            } else {
                global = criteriaBuilder.or(global, in);
            }
        }

        global = PredicateMapper.getFieldPredicate(SystemVariables.NAME.getFieldName(), filter.getName(), filter.isExact(), filter.isLogicConjunction(), criteriaBuilder, permissionRoot, global);
        global = PredicateMapper.getFieldPredicate(SystemVariables.ACTION_ID.getFieldName(), filter.getActionId(),
                filter.isExact(), filter.isLogicConjunction(), criteriaBuilder, permissionRoot, global);
        global = PredicateMapper.getFieldPredicate(SystemVariables.RESOURCE_ID.getFieldName(), filter.getResourceId(),
                filter.isExact(), filter.isLogicConjunction(), criteriaBuilder, permissionRoot, global);
        return global;
    }

    /**
     * Entity manager getter
     * @return the correct requested entity manager
     */
    protected EntityManager getEntityManager() {
        return this.holder.getEntityManager();
    }

    /**
     * Verifies if some Permission exists for a referred Id (or alternatively for a name)
     * @param permissionId Identifier to guide the search be searched (Primary parameter)
     * @param permissionName Permission name, an alternative parameter that will be used ONLY
     * if Id is omitted
     * @return response true if it exists
     */
    @Override
    public boolean exists(Long permissionId, String permissionName) {
        if (permissionId == null && permissionName == null) {
            return false;
        }

        EntityManager em = getEntityManager();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Permission> permissionRoot = criteriaQuery.from(Permission.class);

        criteriaQuery.select(criteriaBuilder.count(permissionRoot));
        if (permissionId !=  null) {
            criteriaQuery.where(criteriaBuilder.equal(permissionRoot.get(SystemVariables.ID.getFieldName()), permissionId));
        }
        else {
            criteriaQuery.where(criteriaBuilder.equal(permissionRoot.get(SystemVariables.NAME.getFieldName()), permissionName));
        }

        Long size = em.createQuery(criteriaQuery).getSingleResult();

        return size != 0;
    }

    /**
     * Retrieves a SystemPermission taking in account Resource and Action
     * @param action Action name
     * @param resource Resource Name
     * @return a SystemPermission linked with Resource and Action
     */
    @Override
    public SystemPermission getPermissionByActionAndResourceNames(String action, String resource) {
        String query = "Select p From Permission p, Resource r, Action a " +
                "Where p.actionId = a.id and p.resourceId = r.id and r.name = :rName and a.name= :aName";
        return getEntityManager().createQuery(query, Permission.class).
                setParameter("rName", resource).
                setParameter("aName", action).
                getResultStream().findFirst().orElse(null);
    }

    /**
     * Validator to see if given and request resource and action do exist and belong into one given permission
     * @param p system permission previously constructed to be gathered the action and the resource
     * @param em entity management table to be validated
     * @return true if resource and action do exist
     */
    protected boolean existsForResourceAndAction(SystemPermission p, EntityManager em) {
        if (p.getResourceId() == null || p.getActionId() == null) {
            return false;
        }

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Permission> permissionRoot = criteriaQuery.from(Permission.class);
        criteriaQuery.select(criteriaBuilder.count(permissionRoot));

        Predicate global = null;

        global = criteriaBuilder.equal(permissionRoot.get(SystemVariables.RESOURCE_ID.getFieldName()), p.getResourceId());
        global=criteriaBuilder.and(global, criteriaBuilder.equal(permissionRoot.get(SystemVariables.ACTION_ID.getFieldName()), p.getActionId()));

        if(p.getId() != null) {
            global=criteriaBuilder.and(global, criteriaBuilder.notEqual(permissionRoot.get(SystemVariables.ID.getFieldName()), p.getId()));
        }
        criteriaQuery.where(global);

        Long size = em.createQuery(criteriaQuery).getSingleResult();

        return size != 0;
    }

    /**
     * Count the number of all the permissions existent in the DB.
     * @return the count of permissions
     */
    @Override
    public long getTotalRecordsCount() {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        return getCount(criteriaBuilder.isTrue(criteriaBuilder.literal(true)), criteriaBuilder.createQuery(Long.class).from(Permission.class), getEntityManager());
    }
}
