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
import io.radien.api.service.linked.authorization.LinkedAuthorizationServiceAccess;
import io.radien.exception.LinkedAuthorizationNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.client.exception.LinkedAuthorizationErrorCodeMessage;
import io.radien.ms.rolemanagement.entities.LinkedAuthorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;

/**
 * @author Bruno Gama
 */
@Stateful
public class LinkedAuthorizationService implements LinkedAuthorizationServiceAccess {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(LinkedAuthorizationService.class);

    @PersistenceContext(unitName = "rolePersistenceUnit")
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
                throw new UniquenessConstraintException(LinkedAuthorizationErrorCodeMessage.DUPLICATED_FIELD.toString("Tenant Id, Permission Id and Role Id"));
            }
        } else {
            log.info("We are going to update");
            if(!alreadyExistentAssociation.isEmpty()) {
                log.info("An id has been given, but there is already another record with some of the given information");
                throw new UniquenessConstraintException(LinkedAuthorizationErrorCodeMessage.DUPLICATED_FIELD.toString("Tenant Id, Permission Id and Role Id"));
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
                criteriaBuilder.equal(tenancyRoot.get("roleId"), association.getRoleId()));
        if(association.getId()!= null) {
            global=criteriaBuilder.and(global, criteriaBuilder.notEqual(tenancyRoot.get("id"), association.getId()));
        }
        criteriaQuery.where(global);
        TypedQuery<LinkedAuthorization> q = entityManager.createQuery(criteriaQuery);
        alreadyExistentRecords = q.getResultList();
        return alreadyExistentRecords;
    }
}
