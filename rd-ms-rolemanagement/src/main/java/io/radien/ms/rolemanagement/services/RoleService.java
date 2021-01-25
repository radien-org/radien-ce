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

import io.radien.api.entity.Page;
import io.radien.api.model.role.SystemRole;
import io.radien.api.model.role.SystemRoleSearchFilter;
import io.radien.api.model.user.SystemUser;
import io.radien.api.service.role.RoleServiceAccess;
import io.radien.exception.RoleNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.exception.UserNotFoundException;
import io.radien.ms.rolemanagement.client.exception.RoleErrorCodeMessage;
import io.radien.ms.rolemanagement.entities.Role;

import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Bruno Gama
 */
@Stateful
public class RoleService implements RoleServiceAccess {
    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "rolePersistenceUnit")
    private EntityManager em;

    /**
     * Gets all the users into a pagination mode.
     * @param pageNo of the requested information. Where the user is.
     * @param pageSize total number of pages returned in the request.
     * @return a page of system roles.
     */
    @Override
    public Page<SystemRole> getAll(int pageNo, int pageSize) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Role> criteriaQuery = criteriaBuilder.createQuery(Role.class);
        Root<Role> roleRoot = criteriaQuery.from(Role.class);

        criteriaQuery.select(roleRoot);
        Predicate global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));

        TypedQuery<Role> q=em.createQuery(criteriaQuery);

        q.setFirstResult((pageNo-1) * pageSize);
        q.setMaxResults(pageSize);

        List<? extends SystemRole> systemRoles = q.getResultList();

        int totalRecords = Math.toIntExact(getCount(global, roleRoot));
        int totalPages = totalRecords%pageSize==0 ? totalRecords/pageSize : totalRecords/pageSize+1;

        return new Page<SystemRole>(systemRoles, pageNo, totalRecords, totalPages);
    }

    /**
     * Get Roles by specific columns already given to be searched
     * @param filter entity with available filters to search role
     */
    @Override
    public List<? extends SystemRole> getSpecificRoles(SystemRoleSearchFilter filter) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Role> criteriaQuery = criteriaBuilder.createQuery(Role.class);
        Root<Role> roleRoot = criteriaQuery.from(Role.class);

        criteriaQuery.select(roleRoot);

        Predicate global = getFilteredPredicate(filter, criteriaBuilder, roleRoot);

        criteriaQuery.where(global);
        TypedQuery<Role> q=em.createQuery(criteriaQuery);

        return q.getResultList();
    }

    private Predicate getFilteredPredicate(SystemRoleSearchFilter filter, CriteriaBuilder criteriaBuilder, Root<Role> roleRoot) {
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

        global = getFieldPredicate("name", filter.getName(), filter, criteriaBuilder, roleRoot, global);
        global = getFieldPredicate("description", filter.getDescription(), filter, criteriaBuilder, roleRoot, global);

        return global;
    }

    private Predicate getFieldPredicate(String name, String value, SystemRoleSearchFilter filter, CriteriaBuilder criteriaBuilder, Root<Role> userRoot, Predicate global) {
        if(value != null) {
            Predicate subPredicate;
            if (filter.isExact()) {
                subPredicate = criteriaBuilder.equal(userRoot.get(name), value);
            } else {
                subPredicate = criteriaBuilder.like(userRoot.get(name),"%"+value+"%");
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
     * Count the number of users existent in the DB.
     * @return the count of users
     */
    private long getCount(Predicate global, Root<Role> userRoot) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        criteriaQuery.where(global);

        criteriaQuery.select(criteriaBuilder.count(userRoot));

        TypedQuery<Long> q=em.createQuery(criteriaQuery);
        return q.getSingleResult();
    }

    /**
     * Gets the System Role searched by the PK (id).
     * @param roleId to be searched.
     * @return the system role requested to be found.
     * @throws RoleNotFoundException if role can not be found will return NotFoundException
     */
    @Override
    public SystemRole get(Long roleId) throws RoleNotFoundException {
        if(em.find(Role.class, roleId) == null) {
            throw new RoleNotFoundException(RoleErrorCodeMessage.RESOURCE_NOT_FOUND.toString());
        }
        return em.find(Role.class, roleId);
    }

    @Override
    public void save(SystemRole role) throws RoleNotFoundException, UniquenessConstraintException {
        List<Role> alreadyExistentRecords = searchDuplicatedName(role);

        if(role.getId() == null) {
            if(alreadyExistentRecords.isEmpty()) {
                em.persist(role);
            } else {
                validateUniquenessRecords(alreadyExistentRecords, role);
            }
        } else {
            //Validate if record exists
            get(role.getId());

            validateUniquenessRecords(alreadyExistentRecords, role);

            em.merge(role);
        }
    }

    /**
     * When updating the role information this method will validate if the unique values maintain as unique.
     * Will search for the role name, given in the information to be updated, to see if they are not already in the DB in another user.
     * @param alreadyExistentRecords list of duplicated role information
     * @param newRoleInformation role information to update into the requested one
     * @throws UniquenessConstraintException in case of requested information to be updated already exists in the DB
     */
    private void validateUniquenessRecords(List<Role> alreadyExistentRecords, SystemRole newRoleInformation) throws UniquenessConstraintException {
        if(!alreadyExistentRecords.isEmpty()) {
            boolean isSameUserEmail = alreadyExistentRecords.get(0).getName().equals(newRoleInformation.getName());

            if(isSameUserEmail) {
                throw new UniquenessConstraintException(RoleErrorCodeMessage.DUPLICATED_FIELD.toString("Name"));
            }
        }
    }

    /**
     * Query to validate if an existent email address or logon already exists in the database or not.
     * @param role user information to look up.
     * @return list of users with duplicated information.
     */
    private List<Role> searchDuplicatedName(SystemRole role) {
        List<Role> alreadyExistentRecords;
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Role> criteriaQuery = criteriaBuilder.createQuery(Role.class);
        Root<Role> userRoot = criteriaQuery.from(Role.class);
        criteriaQuery.select(userRoot);
        Predicate global = criteriaBuilder.equal(userRoot.get("name"), role.getName());
        if(role.getId()!= null) {
            global=criteriaBuilder.and(global, criteriaBuilder.notEqual(userRoot.get("id"), role.getId()));
        }
        criteriaQuery.where(global);
        TypedQuery<Role> q = em.createQuery(criteriaQuery);
        alreadyExistentRecords = q.getResultList();
        return alreadyExistentRecords;
    }

    @Override
    public void delete(Long roleId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<Role> criteriaDelete = cb.createCriteriaDelete(Role.class);
        Root<Role> roleRoot = criteriaDelete.from(Role.class);

        criteriaDelete.where(cb.equal(roleRoot.get("id"),roleId));
        em.createQuery(criteriaDelete).executeUpdate();
    }
}
