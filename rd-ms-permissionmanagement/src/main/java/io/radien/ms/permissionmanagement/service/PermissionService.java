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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;

import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemPermission;
import io.radien.api.model.permission.SystemPermissionSearchFilter;
import io.radien.api.service.permission.PermissionServiceAccess;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.permissionmanagement.client.exceptions.ErrorCodeMessage;

import io.radien.ms.permissionmanagement.model.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author mawe
 *
 */
@Stateless
public class PermissionService implements PermissionServiceAccess {

    @PersistenceContext(unitName = "permissionPersistenceUnit")
    private EntityManager em;

    public PermissionService() {}

    /**
     * Count the number of Permissions existent in the DB.
     * @return the count of Permissions
     */
    private long getCount(Predicate global, Root<Permission> PermissionRoot) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        criteriaQuery.where(global);

        criteriaQuery.select(criteriaBuilder.count(PermissionRoot));

        TypedQuery<Long> q=em.createQuery(criteriaQuery);
        return q.getSingleResult();
    }


    /**
     * Gets the System Permission searching by the PK (id).
     * @param PermissionId to be searched.
     * @return the system Permission requested to be found.
     */
    @Override
    public SystemPermission get(Long PermissionId)  {
        return em.find(Permission.class, PermissionId);
    }

    /**
     * Gets a list of System Permissions searching by multiple PK's (id) requested in a list.
     * @param PermissionIds to be searched.
     * @return the list of system Permissions requested to be found.
     */
    @Override
    public List<SystemPermission> get(List<Long> PermissionIds) {
        ArrayList<SystemPermission> results = new ArrayList<>();
        if(PermissionIds == null || PermissionIds.isEmpty()){
            return results;
        }

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Permission> criteriaQuery = criteriaBuilder.createQuery(Permission.class);
        Root<Permission> PermissionRoot = criteriaQuery.from(Permission.class);
        criteriaQuery.select(PermissionRoot);
        criteriaQuery.where(PermissionRoot.get("id").in(PermissionIds));

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
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Permission> criteriaQuery = criteriaBuilder.createQuery(Permission.class);
        Root<Permission> permissionRoot = criteriaQuery.from(Permission.class);

        criteriaQuery.select(permissionRoot);

        Predicate global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        if(search!= null) {
            global = criteriaBuilder.and(criteriaBuilder.like(permissionRoot.get("name"), search));
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

        int totalRecords = Math.toIntExact(getCount(global, permissionRoot));
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
        List<Permission> alreadyExistentRecords = searchDuplicatedName(permission);

        if(permission.getId() == null) {
            if(alreadyExistentRecords.isEmpty()) {
                em.persist(permission);
            } else {
                validateUniquenessRecords(alreadyExistentRecords, permission);
            }
        } else {
            validateUniquenessRecords(alreadyExistentRecords, permission);

            em.merge(permission);
        }
    }

    /**
     * Query to validate if an existent name (for permission) already exists in the database or not.
     * @param permission Permission information to look up.
     * @return list of Permissions with duplicated information.
     */
    private List<Permission> searchDuplicatedName(SystemPermission permission) {
        List<Permission> alreadyExistentRecords;
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Permission> criteriaQuery = criteriaBuilder.createQuery(Permission.class);
        Root<Permission> PermissionRoot = criteriaQuery.from(Permission.class);
        criteriaQuery.select(PermissionRoot);
        Predicate global = criteriaBuilder.equal(PermissionRoot.get("name"), permission.getName());
        if(permission.getId()!= null) {
            global=criteriaBuilder.and(global, criteriaBuilder.notEqual(PermissionRoot.get("id"), permission.getId()));
        }
        criteriaQuery.where(global);
        TypedQuery<Permission> q = em.createQuery(criteriaQuery);
        alreadyExistentRecords = q.getResultList();
        return alreadyExistentRecords;
    }

    /**
     * When updating the Permission information this method will validate if the unique values maintain as unique.
     * Will search for the Permission name, given in the information to be updated, to see if they are
     * not already in the DB in another Permission.
     * @param alreadyExistentRecords list of duplicated Permission information
     * @param newPermissionInformation Permission information to update into the requested one
     * @throws UniquenessConstraintException in case of requested information to be updated already exists in the DB
     */
    private void validateUniquenessRecords(List<Permission> alreadyExistentRecords, SystemPermission newPermissionInformation) throws UniquenessConstraintException {
        if(!alreadyExistentRecords.isEmpty()) {
            boolean isSameName = alreadyExistentRecords.get(0).getName().equals(newPermissionInformation.getName());
            if(isSameName) {
                throw new UniquenessConstraintException(ErrorCodeMessage.DUPLICATED_FIELD.toString("Name"));
            }
        }
    }

    /**
     * Deletes a unique Permission selected by his id.
     * @param PermissionId to be deleted.
     */
    @Override
    public void delete(Long PermissionId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<Permission> criteriaDelete = cb.createCriteriaDelete(Permission.class);
        Root<Permission> PermissionRoot = criteriaDelete.from(Permission.class);

        criteriaDelete.where(cb.equal(PermissionRoot.get("id"),PermissionId));
        em.createQuery(criteriaDelete).executeUpdate();
    }

    /**
     * Deletes a list of Permissions selected by his id.
     * @param permissionIds to be deleted.
     */
    @Override
    public void delete(Collection<Long> permissionIds) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<Permission> criteriaDelete = cb.createCriteriaDelete(Permission.class);
        Root<Permission> PermissionRoot = criteriaDelete.from(Permission.class);

        criteriaDelete.where(PermissionRoot.get("id").in(permissionIds));
        em.createQuery(criteriaDelete).executeUpdate();
    }

    /**
     * Get PermissionsBy unique columns
     * @param filter entity with available filters to search Permission
     */
    @Override
    public List<? extends SystemPermission> getPermissions(SystemPermissionSearchFilter filter) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Permission> criteriaQuery = criteriaBuilder.createQuery(Permission.class);
        Root<Permission> PermissionRoot = criteriaQuery.from(Permission.class);

        criteriaQuery.select(PermissionRoot);

        Predicate global = getFilteredPredicate(filter, criteriaBuilder, PermissionRoot);

        criteriaQuery.where(global);
        TypedQuery<Permission> q=em.createQuery(criteriaQuery);

        return q.getResultList();
    }

    private Predicate getFilteredPredicate(SystemPermissionSearchFilter filter, CriteriaBuilder criteriaBuilder, Root<Permission> PermissionRoot) {
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

        global = getFieldPredicate("name", filter.getName(), filter, criteriaBuilder, PermissionRoot, global);

        return global;
    }

    private Predicate getFieldPredicate(String name, String value, SystemPermissionSearchFilter filter, CriteriaBuilder criteriaBuilder, Root<Permission> PermissionRoot, Predicate global) {
        if(value != null) {
            Predicate subPredicate;
            if (filter.isExact()) {
                subPredicate = criteriaBuilder.equal(PermissionRoot.get(name), value);
            } else {
                subPredicate = criteriaBuilder.like(PermissionRoot.get(name),"%"+value+"%");
            }

            if(filter.isLogicConjunction()) {
                global = criteriaBuilder.and(global, subPredicate);
            } else {
                global = criteriaBuilder.or(global, subPredicate);
            }
        }
        return global;
    }

}
