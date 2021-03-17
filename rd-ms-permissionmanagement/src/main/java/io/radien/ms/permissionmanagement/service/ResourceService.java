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

import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemResource;
import io.radien.api.model.permission.SystemResourceSearchFilter;
import io.radien.api.service.permission.ResourceServiceAccess;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.permissionmanagement.client.exceptions.ErrorCodeMessage;
import io.radien.ms.permissionmanagement.model.Resource;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
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
    private long getCount(Predicate global, Root<Resource> resourceRoot, CriteriaBuilder cb, EntityManager em) {
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
        return getEntityManager().find(Resource.class, resourceId);
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
        CriteriaQuery<Resource> criteriaQuery = criteriaBuilder.createQuery(Resource.class);
        Root<Resource> resourceRoot = criteriaQuery.from(Resource.class);
        criteriaQuery.select(resourceRoot);
        criteriaQuery.where(resourceRoot.get("id").in(resourceId));

        TypedQuery<Resource> q=em.createQuery(criteriaQuery);

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
        CriteriaQuery<Resource> criteriaQuery = criteriaBuilder.createQuery(Resource.class);
        Root<Resource> resourceRoot = criteriaQuery.from(Resource.class);

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

        TypedQuery<Resource> q=em.createQuery(criteriaQuery);

        q.setFirstResult((pageNo) * pageSize);
        q.setMaxResults(pageSize);

        List<? extends SystemResource> systemResources = q.getResultList();

        int totalRecords = Math.toIntExact(getCount(global, resourceRoot, em.getCriteriaBuilder(), em));
        int totalPages = totalRecords%pageSize==0 ? totalRecords/pageSize : totalRecords/pageSize+1;

        return new Page<SystemResource>(systemResources, pageNo, totalRecords, totalPages);
    }

    /**
     * Saves or updates the requested and given Resource information into the DB.
     * @param resource to be added/inserted or updated
     * @throws UniquenessConstraintException in case of duplicated name
     */
    @Override
    public void save(SystemResource resource) throws UniquenessConstraintException {
        EntityManager em = getEntityManager();
        List<Resource> alreadyExistentRecords = searchDuplicatedName(resource, em);
        if (!alreadyExistentRecords.isEmpty()) {
            throw new UniquenessConstraintException(ErrorCodeMessage.DUPLICATED_FIELD.toString("Name"));
        }
        if(resource.getId() == null) {
            em.persist(resource);
        } else {
            em.merge(resource);
        }
    }

    /**
     * Query to validate if an existent name (for resource) already exists in the database or not.
     * @param resource Resource information to look up.
     * @return list of Resources with duplicated information.
     */
    private List<Resource> searchDuplicatedName(SystemResource resource, EntityManager em) {
        List<Resource> alreadyExistentRecords;
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Resource> criteriaQuery = criteriaBuilder.createQuery(Resource.class);
        Root<Resource> resourceRoot = criteriaQuery.from(Resource.class);
        criteriaQuery.select(resourceRoot);
        Predicate global = criteriaBuilder.equal(resourceRoot.get("name"), resource.getName());
        if(resource.getId()!= null) {
            global=criteriaBuilder.and(global, criteriaBuilder.notEqual(resourceRoot.get("id"), resource.getId()));
        }
        criteriaQuery.where(global);
        TypedQuery<Resource> q = em.createQuery(criteriaQuery);
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
        CriteriaDelete<Resource> criteriaDelete = cb.createCriteriaDelete(Resource.class);
        Root<Resource> resourceRoot = criteriaDelete.from(Resource.class);

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
        CriteriaDelete<Resource> criteriaDelete = cb.createCriteriaDelete(Resource.class);
        Root<Resource> resourceRoot = criteriaDelete.from(Resource.class);

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
        CriteriaQuery<Resource> criteriaQuery = criteriaBuilder.createQuery(Resource.class);
        Root<Resource> resourceRoot = criteriaQuery.from(Resource.class);

        criteriaQuery.select(resourceRoot);

        Predicate global = getFilteredPredicate(filter, criteriaBuilder, resourceRoot);

        criteriaQuery.where(global);
        TypedQuery<Resource> q=em.createQuery(criteriaQuery);

        return q.getResultList();
    }

    /**
     * Count the number of all the resources existent in the DB.
     * @return the count of resources
     */
    @Override
    public long getTotalRecordsCount() {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        return getCount(criteriaBuilder.isTrue(criteriaBuilder.literal(true)), criteriaBuilder.createQuery(Long.class).from(Resource.class), criteriaBuilder, getEntityManager());
    }

    private Predicate getFilteredPredicate(SystemResourceSearchFilter filter,
                                           CriteriaBuilder criteriaBuilder,
                                           Root<Resource> resourceRoot) {
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

        global = getFieldPredicate("name", filter.getName(), filter, criteriaBuilder, resourceRoot, global);

        return global;
    }

    private Predicate getFieldPredicate(String name, Object value,
                                        SystemResourceSearchFilter filter,
                                        CriteriaBuilder criteriaBuilder,
                                        Root<Resource> resourceRoot,
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
    
    protected EntityManager getEntityManager() {
        return holder.getEntityManager();
    }
}
