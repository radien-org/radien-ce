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
package io.radien.ms.permissionmanagement.service;

import io.radien.api.SystemVariables;
import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemResource;
import io.radien.api.model.permission.SystemResourceSearchFilter;
import io.radien.api.service.permission.ResourceServiceAccess;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.ResourceNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.permissionmanagement.client.entities.ResourceSearchFilter;
import io.radien.ms.permissionmanagement.model.ResourceEntity;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.CriteriaDelete;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Resource DB connection requests
 * All the requests made between the resource entity and the database will be performed in here
 *
 * @author Newton Carvalho
 */
@Stateless
public class ResourceService implements ResourceServiceAccess {

    @Inject
    private EntityManagerHolder holder;

    /**
     * Count the number of Resources existent in the DB.
     * @return the count of Resources
     */
    private long getCount(Predicate global, Root<ResourceEntity> resourceRoot, CriteriaBuilder cb, EntityManager em) {
        CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);

        criteriaQuery.where(global);

        criteriaQuery.select(cb.count(resourceRoot));

        TypedQuery<Long> q=em.createQuery(criteriaQuery);
        return q.getSingleResult();
    }


    /**
     * Gets the System Resource searching by the PK (id).
     * @param resourceId to be searched.
     * @return the system Resource requested to be found.
     */
    @Override
    public SystemResource get(Long resourceId)  {
        return getEntityManager().find(ResourceEntity.class, resourceId);
    }

    /**
     * Gets a list of System Resources searching by multiple PK's (id) requested in a list.
     * @param resourceId to be searched.
     * @return the list of system Resources requested to be found.
     */
    @Override
    public List<SystemResource> get(List<Long> resourceId) {
        ArrayList<SystemResource> results = new ArrayList<>();
        if(resourceId == null || resourceId.isEmpty()){
            return results;
        }

        EntityManager em = getEntityManager();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ResourceEntity> criteriaQuery = criteriaBuilder.createQuery(ResourceEntity.class);
        Root<ResourceEntity> resourceRoot = criteriaQuery.from(ResourceEntity.class);
        criteriaQuery.select(resourceRoot);
        criteriaQuery.where(resourceRoot.get("id").in(resourceId));

        TypedQuery<ResourceEntity> q=em.createQuery(criteriaQuery);

        return new ArrayList<>(q.getResultList());
    }

    /**
     * Gets all the Resources into a pagination mode.
     * Can be filtered by name Resource.
     * @param search name description for some resource
     * @param pageNo of the requested information. Where the Resource is.
     * @param pageSize total number of pages returned in the request.
     * @param sortBy sort filter criteria.
     * @param isAscending ascending filter criteria.
     * @return a page of system Resources.
     */
    @Override
    public Page<SystemResource> getAll(String search, int pageNo, int pageSize,
                                     List<String> sortBy,
                                     boolean isAscending) {
        EntityManager em = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ResourceEntity> criteriaQuery = criteriaBuilder.createQuery(ResourceEntity.class);
        Root<ResourceEntity> resourceRoot = criteriaQuery.from(ResourceEntity.class);

        criteriaQuery.select(resourceRoot);

        Predicate global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        if(search!= null) {
            global = criteriaBuilder.and(criteriaBuilder.like(resourceRoot.get("name"), search));
            criteriaQuery.where(global);
        }

        if(sortBy != null && !sortBy.isEmpty()){
            List<Order> orders;
            if(isAscending){
                orders = sortBy.stream().map(i->criteriaBuilder.asc(resourceRoot.get(i))).collect(Collectors.toList());
            } else {
                orders = sortBy.stream().map(i->criteriaBuilder.desc(resourceRoot.get(i))).collect(Collectors.toList());
            }
            criteriaQuery.orderBy(orders);
        }

        TypedQuery<ResourceEntity> q=em.createQuery(criteriaQuery);

        q.setFirstResult((pageNo - 1) * pageSize);
        q.setMaxResults(pageSize);

        List<? extends SystemResource> systemResources = q.getResultList();

        int totalRecords = Math.toIntExact(getCount(global, resourceRoot, em.getCriteriaBuilder(), em));
        int totalPages = totalRecords%pageSize==0 ? totalRecords/pageSize : totalRecords/pageSize+1;

        return new Page<SystemResource>(systemResources, pageNo, totalRecords, totalPages);
    }

    /**
     * Create a resource
     * @param resource to be created
     * @throws UniquenessConstraintException in case of duplicated information
     */
    @Override
    public void create(SystemResource resource) throws UniquenessConstraintException {
        EntityManager em = getEntityManager();
        checkUniqueness(resource, em);
        em.persist(resource);
    }

    /**
     * Update a resource
     * @param resource to be updated
     * @throws ResourceNotFoundException in case of not existent resource
     * @throws UniquenessConstraintException in case of duplicated information
     */
    @Override
    public void update(SystemResource resource) throws ResourceNotFoundException, UniquenessConstraintException {
        EntityManager em = getEntityManager();
        if(em.find(ResourceEntity.class, resource.getId()) == null) {
            throw new ResourceNotFoundException(GenericErrorCodeMessage.RESOURCE_NOT_FOUND.toString());
        }
        checkUniqueness(resource, em);
        em.merge(resource);
    }

    /**
     * Seek for eventual duplicated information
     * @param resource base entity bean to seek for repeated information
     * @param em entity manager
     * @throws UniquenessConstraintException thrown in case of repeated information
     */
    private void checkUniqueness(SystemResource resource, EntityManager em) throws UniquenessConstraintException {
        List<ResourceEntity> alreadyExistentRecords = searchDuplicatedName(resource, em);
        if (!alreadyExistentRecords.isEmpty()) {
            throw new UniquenessConstraintException(GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Name"));
        }
    }

    /**
     * Query to validate if an existent name (for resource) already exists in the database or not.
     * @param resource Resource information to look up.
     * @return list of Resources with duplicated information.
     */
    private List<ResourceEntity> searchDuplicatedName(SystemResource resource, EntityManager em) {
        List<ResourceEntity> alreadyExistentRecords;
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ResourceEntity> criteriaQuery = criteriaBuilder.createQuery(ResourceEntity.class);
        Root<ResourceEntity> resourceRoot = criteriaQuery.from(ResourceEntity.class);
        criteriaQuery.select(resourceRoot);
        Predicate global = criteriaBuilder.equal(resourceRoot.get("name"), resource.getName());
        if(resource.getId()!= null) {
            global=criteriaBuilder.and(global, criteriaBuilder.notEqual(resourceRoot.get("id"), resource.getId()));
        }
        criteriaQuery.where(global);
        TypedQuery<ResourceEntity> q = em.createQuery(criteriaQuery);
        alreadyExistentRecords = q.getResultList();
        return alreadyExistentRecords;
    }

    /**
     * Deletes a unique Resource selected by his id.
     * @param resourceId to be deleted.
     */
    @Override
    public void delete(Long resourceId) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<ResourceEntity> criteriaDelete = cb.createCriteriaDelete(ResourceEntity.class);
        Root<ResourceEntity> resourceRoot = criteriaDelete.from(ResourceEntity.class);

        criteriaDelete.where(cb.equal(resourceRoot.get("id"),resourceId));
        em.createQuery(criteriaDelete).executeUpdate();
    }

    /**
     * Deletes a list of Resources selected by his id.
     * @param resourceIds to be deleted.
     */
    @Override
    public void delete(Collection<Long> resourceIds) {
        if (resourceIds == null || resourceIds.isEmpty()) {
            return;
        }
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<ResourceEntity> criteriaDelete = cb.createCriteriaDelete(ResourceEntity.class);
        Root<ResourceEntity> resourceRoot = criteriaDelete.from(ResourceEntity.class);

        criteriaDelete.where(resourceRoot.get("id").in(resourceIds));
        em.createQuery(criteriaDelete).executeUpdate();
    }

    /**
     * Get resources by unique columns
     * @param filter entity with available filters to search Resource
     */
    @Override
    public List<? extends SystemResource> getResources(SystemResourceSearchFilter filter) {
        EntityManager em = getEntityManager();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ResourceEntity> criteriaQuery = criteriaBuilder.createQuery(ResourceEntity.class);
        Root<ResourceEntity> resourceRoot = criteriaQuery.from(ResourceEntity.class);

        criteriaQuery.select(resourceRoot);

        Predicate global = getFilteredPredicate((ResourceSearchFilter) filter, criteriaBuilder, resourceRoot);

        criteriaQuery.where(global);
        TypedQuery<ResourceEntity> q=em.createQuery(criteriaQuery);

        return q.getResultList();
    }

    /**
     * Will filter all the fields given in the criteria builder and in the filter and create the
     * where clause for the query
     * @param filter fields to be searched for
     * @param criteriaBuilder database query builder
     * @param resourceRoot database table to search the information
     * @return a constructed predicate with the fields needed to be search
     */
    private Predicate getFilteredPredicate(ResourceSearchFilter filter,
                                           CriteriaBuilder criteriaBuilder,
                                           Root<ResourceEntity> resourceRoot) {
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
            Predicate in = resourceRoot.get(SystemVariables.ID.getFieldName()).in(filter.getIds());
            if(filter.isLogicConjunction()) {
                global = criteriaBuilder.and(global, in);
            } else {
                global = criteriaBuilder.or(global, in);
            }
        }

        global = getFieldPredicate("name", filter.getName(), filter, criteriaBuilder, resourceRoot, global);

        return global;
    }

    /**
     * Method that will create in the database query where clause each and single search
     * @param name of the field to be search in the query
     * @param value of the field to be search or compared in the query
     * @param filter complete requested filter for further validations
     * @param criteriaBuilder database query builder
     * @param resourceRoot database table to search the information
     * @param global complete where clause to be merged into the constructed information
     * @return a constructed predicate with the fields needed to be search
     */
    private Predicate getFieldPredicate(String name, Object value,
                                        ResourceSearchFilter filter,
                                        CriteriaBuilder criteriaBuilder,
                                        Root<ResourceEntity> resourceRoot,
                                        Predicate global) {
        if(value != null) {
            Predicate subPredicate;

            if (filter.isExact()) {
                subPredicate = criteriaBuilder.equal(resourceRoot.get(name), value);
            } else {
                subPredicate = criteriaBuilder.like(resourceRoot.get(name), "%" + value + "%");
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
     * Entity manager getter
     * @return the correct requested entity manager
     */
    protected EntityManager getEntityManager() {
        return holder.getEntityManager();
    }
}
