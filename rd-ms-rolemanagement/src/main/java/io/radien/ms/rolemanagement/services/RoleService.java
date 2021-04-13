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
import io.radien.api.service.role.RoleServiceAccess;
import io.radien.exception.RoleNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.client.exception.RoleErrorCodeMessage;
import io.radien.ms.rolemanagement.entities.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Bruno Gama
 */
@Stateful
public class RoleService implements RoleServiceAccess {
    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "rolePersistenceUnit")
    private EntityManager entityManager;

    private static final Logger log = LoggerFactory.getLogger(RoleService.class);

    /**
     * Gets all the role into a pagination mode.
     * @param search name description for some role
     * @param pageNo of the requested information. Where the role is.
     * @param pageSize total number of pages returned in the request.
     * @param sortBy sort filter criteria.
     * @param isAscending ascending filter criteria.
     * @return a page of system roles.
     */
    @Override
    public Page<SystemRole> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) {

        log.info("Going to create a new pagination!");

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Role> criteriaQuery = criteriaBuilder.createQuery(Role.class);
        Root<Role> roleRoot = criteriaQuery.from(Role.class);

        criteriaQuery.select(roleRoot);
        Predicate global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        if(search!= null) {
            global = criteriaBuilder.and(criteriaBuilder.like(roleRoot.get("name"), search));
            criteriaQuery.where(global);
        }
        if(sortBy != null && !sortBy.isEmpty()){
            List<Order> orders;
            if(isAscending){
                orders = sortBy.stream().map(i->criteriaBuilder.asc(roleRoot.get(i))).collect(Collectors.toList());
            } else {
                orders = sortBy.stream().map(i->criteriaBuilder.desc(roleRoot.get(i))).collect(Collectors.toList());
            }
            criteriaQuery.orderBy(orders);
        }

        TypedQuery<Role> q= entityManager.createQuery(criteriaQuery);

        q.setFirstResult((pageNo-1) * pageSize);
        q.setMaxResults(pageSize);

        List<? extends SystemRole> systemRoles = q.getResultList();

        int totalRecords = Math.toIntExact(getCount(global, roleRoot));
        int totalPages = totalRecords%pageSize==0 ? totalRecords/pageSize : totalRecords/pageSize+1;

        log.info("New pagination ready to be showed!");

        return new Page<SystemRole>(systemRoles, pageNo, totalRecords, totalPages);
    }

    /**
     * Get Roles by specific columns already given to be searched.
     *
     * @param filter entity with available filters to search role.
     */
    @Override
    public List<? extends SystemRole> getSpecificRoles(SystemRoleSearchFilter filter) {

        log.info("Ready to search by a specific role!");

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Role> criteriaQuery = criteriaBuilder.createQuery(Role.class);
        Root<Role> roleRoot = criteriaQuery.from(Role.class);

        criteriaQuery.select(roleRoot);

        Predicate global = getFilteredPredicate(filter, criteriaBuilder, roleRoot);

        criteriaQuery.where(global);
        TypedQuery<Role> q= entityManager.createQuery(criteriaQuery);

        log.info("Specific role information prepared to be showed!");

        return q.getResultList();
    }

    /**
     * Creates the query predicate to search for hte specific information.
     * is LogicalConjunction represents if you join the fields on the predicates with "or" or "and"
     * the predicate is build with the logic (start,operator,newPredicate)
     * where start represents the already joined predicates
     * operator is "and" or "or"
     * depending on the operator the start may need to be true or false
     * true and predicate1 and predicate2
     * false or predicate1 or predicate2
     * @param filter complete information to be filtered.
     * @param criteriaBuilder query to be worked on.
     * @param roleRoot table to search the information.
     * @return a completed predicate to be used in the search criteria.
     */
    private Predicate getFilteredPredicate(SystemRoleSearchFilter filter, CriteriaBuilder criteriaBuilder, Root<Role> roleRoot) {
        Predicate global;
        if(filter.isLogicConjunction()) {
            global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        } else {
            global = criteriaBuilder.isFalse(criteriaBuilder.literal(true));
        }

        global = getFieldPredicate("name", filter.getName(), filter, criteriaBuilder, roleRoot, global);
        global = getFieldPredicate("description", filter.getDescription(), filter, criteriaBuilder, roleRoot, global);

        return global;
    }

    /**
     * Creates the where clause in the query predicate.
     *
     * @param name string of the value name
     * @param value value to be searched for
     * @param filter complete information to be filtered.
     * @param criteriaBuilder query to be worked on.
     * @param userRoot table to search the information.
     * @param global global predicate to be used.
     * @return a where predicate to be used in the search criteria.
     */
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
     * Count the number of roles existent in the DB.
     * @return the count of users
     */
    private long getCount(Predicate global, Root<Role> userRoot) {

        log.info("Going to count the existent records.");

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        criteriaQuery.where(global);

        criteriaQuery.select(criteriaBuilder.count(userRoot));

        TypedQuery<Long> q= entityManager.createQuery(criteriaQuery);

        return q.getSingleResult();
    }

    /**
     * Count the number of all the roles existent in the DB.
     * @return the count of roles
     */
    @Override
    public long getTotalRecordsCount() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        return getCount(criteriaBuilder.isTrue(criteriaBuilder.literal(true)), criteriaBuilder.createQuery(Long.class).from(Role.class));
    }

    /**
     * Gets the System Role searched by the PK (id).
     * @param roleId to be searched.
     * @return the system role requested to be found.
     * @throws RoleNotFoundException if role can not be found will return NotFoundException
     */
    @Override
    public SystemRole get(Long roleId) throws RoleNotFoundException {
        if(entityManager.find(Role.class, roleId) == null) {
            throw new RoleNotFoundException(RoleErrorCodeMessage.RESOURCE_NOT_FOUND.toString());
        }

        log.info("I found the record!");

        return entityManager.find(Role.class, roleId);
    }

    /**
     * Saves or updates an record based on the given information.
     * @param role information to create/update.
     * @throws RoleNotFoundException in case of update if we cannot find the specific role.
     * @throws UniquenessConstraintException in case of update if any new given information already exists and
     * cannot be duplicated
     */
    @Override
    public void save(SystemRole role) throws RoleNotFoundException, UniquenessConstraintException {

        log.info("New record information to be used!");

        List<Role> alreadyExistentRecords = searchDuplicatedName(role);

        if(role.getId() == null) {
            if(alreadyExistentRecords.isEmpty()) {
                log.info("It's a creation!");
                entityManager.persist(role);
            } else {
                log.info("No id has been given, but there is already another record with some of the given information");
                validateUniquenessRecords(alreadyExistentRecords, role);
            }
        } else {
            log.info("We are going to update");
            //Validate if record exists
            get(role.getId());
            validateUniquenessRecords(alreadyExistentRecords, role);

            entityManager.merge(role);
        }
    }

    /**
     * When updating the role information this method will validate if the unique values maintain as unique.
     * Will search for the role name, given in the information to be updated, to see if they are not already
     * in the DB in another role.
     * @param alreadyExistentRecords list of duplicated role information
     * @param newRoleInformation role information to update into the requested one
     * @throws UniquenessConstraintException in case of requested information to be updated already exists in the DB
     */
    private void validateUniquenessRecords(List<Role> alreadyExistentRecords, SystemRole newRoleInformation) throws UniquenessConstraintException {
        log.info("Validating duplicated information that must be unique");
        if(!alreadyExistentRecords.isEmpty()) {
            boolean isSameName = alreadyExistentRecords.get(0).getName().equals(newRoleInformation.getName());

            if(isSameName) {
                throw new UniquenessConstraintException(RoleErrorCodeMessage.DUPLICATED_FIELD.toString("Name"));
            }
        }
    }

    /**
     * Query to validate if an existent name already exists in the database or not.
     * @param role role information to look up.
     * @return list of roles with duplicated information.
     */
    private List<Role> searchDuplicatedName(SystemRole role) {

        log.info("Going to search for duplicated names");

        List<Role> alreadyExistentRecords;
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Role> criteriaQuery = criteriaBuilder.createQuery(Role.class);
        Root<Role> userRoot = criteriaQuery.from(Role.class);
        criteriaQuery.select(userRoot);
        Predicate global = criteriaBuilder.equal(userRoot.get("name"), role.getName());
        if(role.getId()!= null) {
            global=criteriaBuilder.and(global, criteriaBuilder.notEqual(userRoot.get("id"), role.getId()));
        }
        criteriaQuery.where(global);
        TypedQuery<Role> q = entityManager.createQuery(criteriaQuery);
        alreadyExistentRecords = q.getResultList();
        return alreadyExistentRecords;
    }

    /**
     * Deletes a role record by a given id.
     * @param roleId to be deleted
     */
    @Override
    public void delete(Long roleId) {

        log.info("I'm going to delete the following record!");

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<Role> criteriaDelete = cb.createCriteriaDelete(Role.class);
        Root<Role> roleRoot = criteriaDelete.from(Role.class);

        criteriaDelete.where(cb.equal(roleRoot.get("id"),roleId));
        entityManager.createQuery(criteriaDelete).executeUpdate();
    }

    /**
     * Validates if a certain specified role is existent
     * @param roleId to be found
     * @param name to be found
     * @return true if it exists.
     */
    public boolean checkIfRolesExist(Long roleId, String name) {
        if(roleId != null || name != null) {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
            Root<Role> contractRoot = criteriaQuery.from(Role.class);
            criteriaQuery.select(criteriaBuilder.count(contractRoot));

            if (roleId != null) {
                criteriaQuery.where(criteriaBuilder.equal(contractRoot.get("id"), roleId));
            } else if (name != null) {
                criteriaQuery.where(criteriaBuilder.equal(contractRoot.get("name"), name));
            }

            Long size = entityManager.createQuery(criteriaQuery).getSingleResult();

            return size != 0;
        }
        return false;
    }
}
