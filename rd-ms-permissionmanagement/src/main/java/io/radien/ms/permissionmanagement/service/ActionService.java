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
import io.radien.api.model.permission.SystemAction;
import io.radien.api.model.permission.SystemActionSearchFilter;
import io.radien.api.service.permission.ActionServiceAccess;

import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.permissionmanagement.client.entities.ActionSearchFilter;
import io.radien.ms.permissionmanagement.model.Action;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Action DB connection requests
 * All the requests made between the action entity and the database will be performed in here
 *
 * @author Newton Carvalho
 */
@Stateless
public class ActionService implements ActionServiceAccess {

    @Inject
    private EntityManagerHolder holder;

    /**
     * Count the number of Actions existent in the DB.
     * @return the count of Actions
     */
    private long getCount(Predicate global, Root<Action> actionRoot, CriteriaBuilder cb, EntityManager em) {
        CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);

        criteriaQuery.where(global);

        criteriaQuery.select(cb.count(actionRoot));

        TypedQuery<Long> q=em.createQuery(criteriaQuery);
        return q.getSingleResult();
    }


    /**
     * Gets the System Action searching by the PK (id).
     * @param actionId to be searched.
     * @return the system Action requested to be found.
     */
    @Override
    public SystemAction get(Long actionId)  {
        return getEntityManager().find(Action.class, actionId);
    }

    /**
     * Gets a list of System Actions searching by multiple PK's (id) requested in a list.
     * @param actionId to be searched.
     * @return the list of system Actions requested to be found.
     */
    @Override
    public List<SystemAction> get(List<Long> actionId) {
        ArrayList<SystemAction> results = new ArrayList<>();
        if(actionId == null || actionId.isEmpty()){
            return results;
        }

        EntityManager em = getEntityManager();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Action> criteriaQuery = criteriaBuilder.createQuery(Action.class);
        Root<Action> actionRoot = criteriaQuery.from(Action.class);
        criteriaQuery.select(actionRoot);
        criteriaQuery.where(actionRoot.get("id").in(actionId));

        TypedQuery<Action> q=em.createQuery(criteriaQuery);

        return new ArrayList<>(q.getResultList());
    }

    /**
     * Gets all the Actions into a pagination mode.
     * Can be filtered by name Action.
     * @param search name description for some action
     * @param pageNo of the requested information. Where the Action is.
     * @param pageSize total number of pages returned in the request.
     * @param sortBy sort filter criteria.
     * @param isAscending ascending filter criteria.
     * @return a page of system Actions.
     */
    @Override
    public Page<SystemAction> getAll(String search, int pageNo, int pageSize,
                                     List<String> sortBy,
                                     boolean isAscending) {
        EntityManager em = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Action> criteriaQuery = criteriaBuilder.createQuery(Action.class);
        Root<Action> actionRoot = criteriaQuery.from(Action.class);

        criteriaQuery.select(actionRoot);

        Predicate global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        if(search!= null) {
            global = criteriaBuilder.and(criteriaBuilder.like(actionRoot.get("name"), search));
            criteriaQuery.where(global);
        }

        if(sortBy != null && !sortBy.isEmpty()){
            List<Order> orders;
            if(isAscending){
                orders = sortBy.stream().map(i->criteriaBuilder.asc(actionRoot.get(i))).collect(Collectors.toList());
            } else {
                orders = sortBy.stream().map(i->criteriaBuilder.desc(actionRoot.get(i))).collect(Collectors.toList());
            }
            criteriaQuery.orderBy(orders);
        }

        TypedQuery<Action> q=em.createQuery(criteriaQuery);

        q.setFirstResult((pageNo-1) * pageSize);
        q.setMaxResults(pageSize);

        List<? extends SystemAction> systemActions = q.getResultList();

        int totalRecords = Math.toIntExact(getCount(global, actionRoot, em.getCriteriaBuilder(), em));
        int totalPages = totalRecords%pageSize==0 ? totalRecords/pageSize : totalRecords/pageSize+1;

        return new Page<SystemAction>(systemActions, pageNo, totalRecords, totalPages);
    }

    /**
     * Saves or updates the requested and given Action information into the DB.
     * @param action to be added/inserted or updated
     * @throws UniquenessConstraintException in case of duplicated name
     */
    @Override
    public void save(SystemAction action) throws UniquenessConstraintException {
        EntityManager em = getEntityManager();
        List<Action> alreadyExistentRecords = searchDuplicatedName(action, em);
        if (!alreadyExistentRecords.isEmpty()) {
            throw new UniquenessConstraintException(GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Name"));
        }
        if(action.getId() == null) {
            em.persist(action);
        } else {
            em.merge(action);
        }
    }

    /**
     * Query to validate if an existent name (for action) already exists in the database or not.
     * @param action Action information to look up.
     * @return list of Actions with duplicated information.
     */
    private List<Action> searchDuplicatedName(SystemAction action, EntityManager em) {
        List<Action> alreadyExistentRecords;
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Action> criteriaQuery = criteriaBuilder.createQuery(Action.class);
        Root<Action> actionRoot = criteriaQuery.from(Action.class);
        criteriaQuery.select(actionRoot);
        Predicate global = criteriaBuilder.equal(actionRoot.get("name"), action.getName());
        if(action.getId()!= null) {
            global=criteriaBuilder.and(global, criteriaBuilder.notEqual(actionRoot.get("id"), action.getId()));
        }
        criteriaQuery.where(global);
        TypedQuery<Action> q = em.createQuery(criteriaQuery);
        alreadyExistentRecords = q.getResultList();
        return alreadyExistentRecords;
    }

    /**
     * Deletes a unique Action selected by his id.
     * @param actionId to be deleted.
     */
    @Override
    public void delete(Long actionId) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<Action> criteriaDelete = cb.createCriteriaDelete(Action.class);
        Root<Action> actionRoot = criteriaDelete.from(Action.class);

        criteriaDelete.where(cb.equal(actionRoot.get("id"),actionId));
        em.createQuery(criteriaDelete).executeUpdate();
    }

    /**
     * Deletes a list of Actions selected by his id.
     * @param actionIds to be deleted.
     */
    @Override
    public void delete(Collection<Long> actionIds) {
        if (actionIds == null || actionIds.isEmpty()) {
            return;
        }
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<Action> criteriaDelete = cb.createCriteriaDelete(Action.class);
        Root<Action> actionRoot = criteriaDelete.from(Action.class);

        criteriaDelete.where(actionRoot.get("id").in(actionIds));
        em.createQuery(criteriaDelete).executeUpdate();
    }

    /**
     * Get actions by unique columns
     * @param filter entity with available filters to search Action
     */
    @Override
    public List<? extends SystemAction> getActions(SystemActionSearchFilter filter) {
        EntityManager em = getEntityManager();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Action> criteriaQuery = criteriaBuilder.createQuery(Action.class);
        Root<Action> actionRoot = criteriaQuery.from(Action.class);

        criteriaQuery.select(actionRoot);

        Predicate global = getFilteredPredicate((ActionSearchFilter) filter, criteriaBuilder, actionRoot);

        criteriaQuery.where(global);
        TypedQuery<Action> q=em.createQuery(criteriaQuery);

        return q.getResultList();
    }

    /**
     * Will filter all the fields given in the criteria builder and in the filter and create the
     * where clause for the query
     * @param filter fields to be searched for
     * @param criteriaBuilder database query builder
     * @param actionRoot database table to search the information
     * @return a constructed predicate with the fields needed to be search
     */
    private Predicate getFilteredPredicate(ActionSearchFilter filter,
                                           CriteriaBuilder criteriaBuilder,
                                           Root<Action> actionRoot) {
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

        global = getFieldPredicate("name", filter.getName(), filter, criteriaBuilder, actionRoot, global);

        return global;
    }

    /**
     * Method that will create in the database query where clause each and single search
     * @param name of the field to be search in the query
     * @param value of the field to be search or compared in the query
     * @param filter complete requested filter for further validations
     * @param criteriaBuilder database query builder
     * @param actionRoot database table to search the information
     * @param global complete where clause to be merged into the constructed information
     * @return a constructed predicate with the fields needed to be search
     */
    private Predicate getFieldPredicate(String name, Object value,
                                        ActionSearchFilter filter,
                                        CriteriaBuilder criteriaBuilder,
                                        Root<Action> actionRoot,
                                        Predicate global) {
        if(value != null) {
            Predicate subPredicate;

            if (filter.isExact()) {
                subPredicate = criteriaBuilder.equal(actionRoot.get(name), value);
            } else {
                subPredicate = criteriaBuilder.like(actionRoot.get(name), "%" + value + "%");
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
