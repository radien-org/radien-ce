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
import io.radien.api.model.linked.authorization.SystemLinkedAuthorization;
import io.radien.api.model.linked.authorization.SystemLinkedAuthorizationSearchFilter;
import io.radien.api.model.role.SystemRole;
import io.radien.api.service.linked.authorization.LinkedAuthorizationServiceAccess;
import io.radien.exception.LinkedAuthorizationNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.client.entities.LinkedAuthorizationSearchFilter;
import io.radien.ms.rolemanagement.client.exception.LinkedAuthorizationErrorCodeMessage;
import io.radien.ms.rolemanagement.entities.LinkedAuthorization;
import io.radien.ms.rolemanagement.entities.Role;
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
import java.util.Collection;
import java.util.List;

/**
 * The Linked Authorization Service will perform the necessary requests and validations for each request in the db
 *
 * @author Bruno Gama
 */
@Stateful
public class LinkedAuthorizationService implements LinkedAuthorizationServiceAccess {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(LinkedAuthorizationService.class);

    @PersistenceContext(unitName = "persistenceUnit")
    private EntityManager entityManager;

    /**
     * Gets the System Linked Authorization searched by the PK (id).
     * @param associationId to be searched.
     * @return the system Linked Authorization requested to be found.
     * @throws LinkedAuthorizationNotFoundException if Linked Authorization can not be found will return NotFoundException
     */
    @Override
    public SystemLinkedAuthorization getAssociationById(Long associationId) throws LinkedAuthorizationNotFoundException {
        if(entityManager.find(LinkedAuthorization.class, associationId) == null) {
            throw new LinkedAuthorizationNotFoundException(LinkedAuthorizationErrorCodeMessage.RESOURCE_NOT_FOUND.toString());
        }

        log.info("I found the record!");
        return entityManager.find(LinkedAuthorization.class, associationId);
    }

    /**
     * Gets all the Linked Authorization into a pagination mode.
     * @param pageNo of the requested information. Where the user is.
     * @param pageSize total number of pages returned in the request.
     * @return a page of system roles.
     */
    @Override
    public Page<SystemLinkedAuthorization> getAll(int pageNo, int pageSize) {
        log.info("Going to create a new pagination!");

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<LinkedAuthorization> criteriaQuery = criteriaBuilder.createQuery(LinkedAuthorization.class);
        Root<LinkedAuthorization> tenancyCtrlRoot = criteriaQuery.from(LinkedAuthorization.class);

        criteriaQuery.select(tenancyCtrlRoot);
        Predicate global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));

        TypedQuery<LinkedAuthorization> q= entityManager.createQuery(criteriaQuery);

        q.setFirstResult((pageNo-1) * pageSize);
        q.setMaxResults(pageSize);

        List<? extends LinkedAuthorization> systemTenancyAssociations = q.getResultList();

        int totalRecords = Math.toIntExact(getTenancyAssociationCount(global, tenancyCtrlRoot));
        int totalPages = totalRecords%pageSize==0 ? totalRecords/pageSize : totalRecords/pageSize+1;

        log.info("New pagination ready to be showed!");

        return new Page<SystemLinkedAuthorization>(systemTenancyAssociations, pageNo, totalRecords, totalPages);
    }

    /**
     * Count the number of Linked Authorizations existent in the DB.
     * @param global predicate
     * @param tenancyCtrlRoot root list of the Linked authorization
     * @return the count of users
     */
    private long getTenancyAssociationCount(Predicate global, Root<LinkedAuthorization> tenancyCtrlRoot) {

        log.info("Going to count the existent records.");

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        criteriaQuery.where(global);

        criteriaQuery.select(criteriaBuilder.count(tenancyCtrlRoot));

        TypedQuery<Long> q= entityManager.createQuery(criteriaQuery);

        return q.getSingleResult();
    }

    /**
     * Get Linked Authorizations by specific columns already given to be searched.
     * @param filter entity with available filters to search Linked Authorizations.
     * @return a list of Linked Authorizations
     */
    @Override
    public List<? extends SystemLinkedAuthorization> getSpecificAssociation(SystemLinkedAuthorizationSearchFilter filter) {
        log.info("Ready to search by a specific role!");

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<LinkedAuthorization> criteriaQuery = criteriaBuilder.createQuery(LinkedAuthorization.class);
        Root<LinkedAuthorization> tenancyRoot = criteriaQuery.from(LinkedAuthorization.class);

        criteriaQuery.select(tenancyRoot);

        Predicate global = getFilteredPredicate(filter, criteriaBuilder, tenancyRoot);

        criteriaQuery.where(global);
        TypedQuery<LinkedAuthorization> q= entityManager.createQuery(criteriaQuery);

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
    private Predicate getFilteredPredicate(SystemLinkedAuthorizationSearchFilter filter, CriteriaBuilder criteriaBuilder, Root<LinkedAuthorization> roleRoot) {
        Predicate global;
        if(filter.isLogicConjunction()) {
            global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        } else {
            global = criteriaBuilder.isFalse(criteriaBuilder.literal(true));
        }

        global = getFieldPredicate("tenantId", filter.getTenantId(), filter, criteriaBuilder, roleRoot, global);
        global = getFieldPredicate("permissionId", filter.getPermissionId(), filter, criteriaBuilder, roleRoot, global);
        global = getFieldPredicate("roleId", filter.getRoleId(), filter, criteriaBuilder, roleRoot, global);
        global = getFieldPredicate("userId", filter.getUserId(), filter, criteriaBuilder, roleRoot, global);

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
    private Predicate getFieldPredicate(String name, Long value, SystemLinkedAuthorizationSearchFilter filter, CriteriaBuilder criteriaBuilder, Root<LinkedAuthorization> userRoot, Predicate global) {
        if(value != null) {
            Predicate subPredicate;

            subPredicate = criteriaBuilder.equal(userRoot.get(name), value);

            if(filter.isLogicConjunction()) {
                global = criteriaBuilder.and(global, subPredicate);
            } else {
                global = criteriaBuilder.or(global, subPredicate);
            }
        }
        return global;
    }

    /**
     * Deletes a linked authorization record by a given id.
     * @param associationId to be deleted
     */
    @Override
    public void deleteAssociation(Long associationId) {

        log.info("I'm going to delete the following record!");

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<LinkedAuthorization> criteriaDelete = cb.createCriteriaDelete(LinkedAuthorization.class);
        Root<LinkedAuthorization> associationRoot = criteriaDelete.from(LinkedAuthorization.class);

        criteriaDelete.where(cb.equal(associationRoot.get("id"),associationId));
        entityManager.createQuery(criteriaDelete).executeUpdate();
    }

    /**
     * Deletes a list of linked authorization associations taking in consideration
     * a set of ids
     * @param ids to be deleted.
     */
    @Override
    public void deleteAssociations(Collection<Long> ids) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<LinkedAuthorization> criteriaDelete = cb.createCriteriaDelete(LinkedAuthorization.class);
        Root<LinkedAuthorization> linkedAuthorizationRoot = criteriaDelete.from(LinkedAuthorization.class);

        criteriaDelete.where(linkedAuthorizationRoot.get("id").in(ids));
        entityManager.createQuery(criteriaDelete).executeUpdate();
    }

    /**
     * Delete linked authorization taking in consideration the following parameters
     * @param tenantId Tenant identifier
     * @param userId User identifier
     * @return true in case of success (elements found and deleted), otherwise false
     */
    @Override
    public boolean deleteAssociations(Long tenantId, Long userId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<LinkedAuthorization> criteriaDelete = cb.createCriteriaDelete(LinkedAuthorization.class);
        Root<LinkedAuthorization> linkedAuthorizationRoot = criteriaDelete.from(LinkedAuthorization.class);

        criteriaDelete.where(
                cb.equal(linkedAuthorizationRoot.get("tenantId"), tenantId),
                cb.equal(linkedAuthorizationRoot.get("userId"), userId)
        );

        return entityManager.createQuery(criteriaDelete).executeUpdate() > 0;
    }

    /**
     * Saves or updates an record based on the given information.
     * @param association information to create/update.
     * @throws UniquenessConstraintException in case of update if any new given information already exists and
     * cannot be duplicated
     * @throws LinkedAuthorizationNotFoundException in case of update if we cannot find the specific linked authorization.
     */
    @Override
    public void save(SystemLinkedAuthorization association) throws UniquenessConstraintException, LinkedAuthorizationNotFoundException {
        log.info("New record information to be used!");

        List<LinkedAuthorization> alreadyExistentAssociation = searchForAssociationToUpdate(association);

        if(association.getId() == null) {
            if(alreadyExistentAssociation.isEmpty()) {
                log.info("It's a creation!");

                entityManager.persist(association);
            } else {
                log.info("No id has been given, but there is already another record with some of the given information");
                throw new UniquenessConstraintException(LinkedAuthorizationErrorCodeMessage.DUPLICATED_FIELD.toString("Tenant Id, Permission Id, Role Id and User Id"));
            }
        } else {
            log.info("We are going to update");
            if(!alreadyExistentAssociation.isEmpty()) {
                log.info("An id has been given, but there is already another record with some of the given information");
                throw new UniquenessConstraintException(LinkedAuthorizationErrorCodeMessage.DUPLICATED_FIELD.toString("Tenant Id, Permission Id, Role Id and User Id"));
            } else {
                getAssociationById(association.getId());

                entityManager.merge(association);
            }
        }
    }

    /**
     * Query to validate if an existent association already exists in the database or not.
     * @param association linked authorization information to look up.
     * @return list of roles with duplicated information.
     */
    private List<LinkedAuthorization> searchForAssociationToUpdate(SystemLinkedAuthorization association) {

        log.info("Going to search for duplicated names");

        List<LinkedAuthorization> alreadyExistentRecords;
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<LinkedAuthorization> criteriaQuery = criteriaBuilder.createQuery(LinkedAuthorization.class);
        Root<LinkedAuthorization> tenancyRoot = criteriaQuery.from(LinkedAuthorization.class);
        criteriaQuery.select(tenancyRoot);
        Predicate global = criteriaBuilder.and(criteriaBuilder.equal(tenancyRoot.get("tenantId"), association.getTenantId()),
                criteriaBuilder.equal(tenancyRoot.get("permissionId"), association.getPermissionId()),
                criteriaBuilder.equal(tenancyRoot.get("roleId"), association.getRoleId()),
                criteriaBuilder.equal(tenancyRoot.get("userId"), association.getUserId()));
        if(association.getId()!= null) {
            global=criteriaBuilder.and(global, criteriaBuilder.notEqual(tenancyRoot.get("id"), association.getId()));
        }
        criteriaQuery.where(global);
        TypedQuery<LinkedAuthorization> q = entityManager.createQuery(criteriaQuery);
        alreadyExistentRecords = q.getResultList();
        return alreadyExistentRecords;
    }

    /**
     * Verifies if exist LinkedAuthorizations for a specific Filter
     * @param filter contains the criteria that satisfies the search process
     * @return true (If finds some LinkedAuthorization for the informed filter), otherwise false
     */
    @Override
    public boolean exists(SystemLinkedAuthorizationSearchFilter filter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<LinkedAuthorization> linkedAuthorizationRoot = criteriaQuery.from(LinkedAuthorization.class);
        criteriaQuery.select(criteriaBuilder.count(linkedAuthorizationRoot));

        Predicate global;
        if(filter.isLogicConjunction()) {
            global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        } else {
            global = criteriaBuilder.isFalse(criteriaBuilder.literal(true));
        }

        global = getFieldPredicate("tenantId", filter.getTenantId(), filter, criteriaBuilder, linkedAuthorizationRoot, global);
        global = getFieldPredicate("permissionId", filter.getPermissionId(), filter, criteriaBuilder, linkedAuthorizationRoot, global);
        global = getFieldPredicate("roleId", filter.getRoleId(), filter, criteriaBuilder, linkedAuthorizationRoot, global);
        global = getFieldPredicate("userId", filter.getUserId(), filter, criteriaBuilder, linkedAuthorizationRoot, global);

        criteriaQuery.where(global);

        Long size = entityManager.createQuery(criteriaQuery).getSingleResult();

        return size != 0;
    }

    /**
     * Validates if the given role exists for the given user
     * @param userId to be validated
     * @param tenantId to be checked
     * @param roleName to see if it exists
     * @return true in case the correct user in the specific tenant has the required role
     */
    @Override
    public boolean isRoleExistentForUser(Long userId, Long tenantId, String roleName) {
        if (userId == null) {
            throw new IllegalArgumentException("User Id is mandatory");
        }
        if (roleName == null) {
            throw new IllegalArgumentException("Role Name is mandatory");
        }
        String query = "Select count(lka) From LinkedAuthorization lka, Role r " +
                "where lka.userId = :pUserId and lka.roleId = r.id " +
                "and upper(r.name) = :pRoleName";
        if (tenantId != null) {
            query += " and lka.tenantId = :pTenantId";
        }
        TypedQuery<Long> typedQuery = entityManager.createQuery(query, Long.class);
        typedQuery.setParameter("pUserId", userId);
        typedQuery.setParameter("pRoleName", roleName.toUpperCase());
        if (tenantId != null) {
            typedQuery.setParameter("pTenantId", tenantId);
        }
        return typedQuery.getSingleResult() > 0;
    }

    /**
     * Checks if a given role name in multiple ones exists for a specific user and tenant
     * @param userId of the current user
     * @param tenantId where the action is requesting validation
     * @param roleNames needed fo active permission access
     * @return true in case of existence
     */
    @Override
    public boolean checkPermissions(Long userId, Long tenantId, List<String> roleNames) {
        CriteriaBuilder criteriaBuilderRole = entityManager.getCriteriaBuilder();
        CriteriaQuery<Role> criteriaQueryRole = criteriaBuilderRole.createQuery(Role.class);
        Root<Role> roleRoot = criteriaQueryRole.from(Role.class);

        criteriaQueryRole.select(roleRoot);
        Predicate globalRole = criteriaBuilderRole.isFalse(criteriaBuilderRole.literal(true));

        for(String s : roleNames) {
            globalRole = criteriaBuilderRole.or(globalRole, criteriaBuilderRole.equal(roleRoot.get("name"), s));
        }

        criteriaQueryRole.where(globalRole);

        TypedQuery<Role> q= entityManager.createQuery(criteriaQueryRole);
        List<? extends SystemRole> listOfRoles = q.getResultList();

        for(SystemRole sRole : listOfRoles) {
            SystemLinkedAuthorizationSearchFilter filter = new LinkedAuthorizationSearchFilter(tenantId, null, sRole.getId(), userId, true);

            if(exists(filter)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Retrieves all the roles based on the user id and tenant id
     * @param userId of the current requested user
     * @param tenantId of the current requested tenant
     * @return a list of system roles
     */
    @Override
    public List<? extends SystemRole> getRolesByUserAndTenant(Long userId, Long tenantId) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId is mandatory");
        }
        String query = "Select distinct r From LinkedAuthorization lka, Role r " +
                "where lka.userId = :pUserId and lka.roleId = r.id";
        if (tenantId != null) {
            query += " and lka.tenantId = :pTenantId";
        }
        TypedQuery<Role> typedQuery = entityManager.createQuery(query, Role.class);
        typedQuery.setParameter("pUserId", userId);
        if (tenantId != null) {
            typedQuery.setParameter("pTenantId", tenantId);
        }
        return typedQuery.getResultList();
    }

    /**
     * Count the number of all the contracts existent in the DB.
     * @return the count of users
     */
    @Override
    public long getTotalRecordsCount() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        return getCount(criteriaBuilder.isTrue(criteriaBuilder.literal(true)), criteriaBuilder.createQuery(Long.class).from(LinkedAuthorization.class));
    }

    /**
     * Count the number of tenants existent in the DB.
     * @return the count of users
     */
    private long getCount(Predicate global, Root<LinkedAuthorization> userRoot) {
        log.info("Going to count the existent records.");
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        criteriaQuery.where(global);

        criteriaQuery.select(criteriaBuilder.count(userRoot));

        TypedQuery<Long> q= entityManager.createQuery(criteriaQuery);

        return q.getSingleResult();
    }
}
