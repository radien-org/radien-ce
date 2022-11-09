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
package io.radien.ms.permissionmanagement.datalayer;

import io.radien.api.SystemVariables;
import io.radien.api.service.permission.exception.PermissionIllegalArgumentException;
import io.radien.ms.permissionmanagement.entities.ActionEntity;
import io.radien.ms.permissionmanagement.entities.ResourceEntity;
import io.radien.ms.permissionmanagement.util.EntityManagerHolder;
import java.util.ArrayList;
import java.util.List;

import java.util.Collection;
import java.util.Optional;
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

import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemPermission;
import io.radien.api.model.permission.SystemPermissionSearchFilter;
import io.radien.api.service.permission.PermissionServiceAccess;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.UniquenessConstraintException;

import io.radien.ms.permissionmanagement.client.entities.PermissionSearchFilter;
import io.radien.ms.permissionmanagement.entities.PermissionEntity;

/**
 * Permission DB connection requests
 * All the requests made between the permission entity and the database will be performed in here
 *
 * @author Newton Carvalho
 */
@Stateless
public class PermissionService implements PermissionServiceAccess {

    private static final long serialVersionUID = -5681037691916176572L;
    @Inject
    private EntityManagerHolder holder;

    /**
     * Permission service empty constructor
     */
    public PermissionService() {
        //empty
    }

    /**
     * Gets the System Permission searching by the PK (id).
     * @param permissionId to be searched.
     * @return the system Permission requested to be found.
     */
    @Override
    public SystemPermission get(Long permissionId)  {
        return getEntityManager().find(PermissionEntity.class, permissionId);
    }

    /**
     * Gets a list of System Permissions searching by multiple PK's (id) requested in a list.
     * @param permissionId to be searched.
     * @return the list of system Permissions requested to be found.
     */
    @Override
    public List<SystemPermission> get(List<Long> permissionId) {
        EntityManager em = getEntityManager();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<PermissionEntity> criteriaQuery = criteriaBuilder.createQuery(PermissionEntity.class);
        Root<PermissionEntity> permissionRoot = criteriaQuery.from(PermissionEntity.class);
        criteriaQuery.select(permissionRoot);
        criteriaQuery.where(permissionRoot.get("id").in(permissionId));

        TypedQuery<PermissionEntity> q=em.createQuery(criteriaQuery);

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
        EntityManager em = getEntityManager();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<PermissionEntity> criteriaQuery = criteriaBuilder.createQuery(PermissionEntity.class);
        Root<PermissionEntity> permissionRoot = criteriaQuery.from(PermissionEntity.class);

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

        TypedQuery<PermissionEntity> q=em.createQuery(criteriaQuery);

        q.setFirstResult((pageNo-1) * pageSize);
        q.setMaxResults(pageSize);

        List<? extends SystemPermission> systemPermissions = q.getResultList();

        int totalRecords = Math.toIntExact(getCount(global, permissionRoot));
        int totalPages = totalRecords%pageSize==0 ? totalRecords/pageSize : totalRecords/pageSize+1;

        return new Page<>(systemPermissions, pageNo, totalRecords, totalPages);
    }

    /**
     * Get PermissionsBy unique columns
     * @param filter entity with available filters to search Permission
     */
    @Override
    public List<SystemPermission> getPermissions(SystemPermissionSearchFilter filter) {
        EntityManager em = getEntityManager();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<PermissionEntity> criteriaQuery = criteriaBuilder.createQuery(PermissionEntity.class);
        Root<PermissionEntity> permissionRoot = criteriaQuery.from(PermissionEntity.class);

        criteriaQuery.select(permissionRoot);

        Predicate global = getFilteredPredicate((PermissionSearchFilter) filter, criteriaBuilder, permissionRoot);

        criteriaQuery.where(global);
        TypedQuery<PermissionEntity> q = em.createQuery(criteriaQuery);

        return new ArrayList<>(q.getResultList());
    }

    /**
     * Retrieve the permission Id using the combination of resource and action as parameters
     * @param resource resource name (Mandatory)
     * @param action action name (Mandatory)
     * @return Optional containing Id (If there is a permission for the informed parameter),
     * otherwise a empty one
     * @throws PermissionIllegalArgumentException in case of parameters not correctly informed
     */
    public Optional<Long> getIdByActionAndResource(String resource, String action) throws PermissionIllegalArgumentException {
        if (resource == null || resource.trim().length() == 0) {
            throw new PermissionIllegalArgumentException(GenericErrorCodeMessage.
                    PERMISSION_PARAMETERS_NOT_INFORMED.toString(SystemVariables.RESOURCE_NAME.getLabel()));
        }
        if (action == null || action.trim().length() == 0) {
            throw new PermissionIllegalArgumentException(GenericErrorCodeMessage.
                    PERMISSION_PARAMETERS_NOT_INFORMED.toString(SystemVariables.ACTION_NAME.getLabel()));
        }

        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<PermissionEntity> permissionRoot = cq.from(PermissionEntity.class);
        Root<ResourceEntity> resourceRoot = cq.from(ResourceEntity.class);
        Root<ActionEntity> actionRoot = cq.from(ActionEntity.class);

        cq.select(permissionRoot.get(SystemVariables.ID.getFieldName())).
                where(
                        cb.equal(permissionRoot.get(SystemVariables.RESOURCE_ID.getFieldName()),
                                resourceRoot.get(SystemVariables.ID.getFieldName())),
                        cb.equal(permissionRoot.get(SystemVariables.ACTION_ID.getFieldName()),
                                actionRoot.get(SystemVariables.ID.getFieldName())),
                        cb.equal(resourceRoot.get(SystemVariables.NAME.getFieldName()), resource),
                        cb.equal(actionRoot.get(SystemVariables.NAME.getFieldName()), action)
                );

        TypedQuery<Long> typedQuery = em.createQuery(cq);
        List<Long> list = typedQuery.getResultList();
        return !list.isEmpty() ? Optional.of(list.get(0)) : Optional.empty();
    }

    /**
     * Creates the requested Permission information into the DB.
     * @param permission to be added/inserted
     * @throws UniquenessConstraintException in case of duplicated name (or combination of action and resource)
     */
    @Override
    public void create(SystemPermission permission) throws UniquenessConstraintException {
        checkUniqueness(permission);
        getEntityManager().persist(permission);
    }

    /**
     * Updates the requested Permission information into the DB.
     * @param permission to be updated
     * @throws UniquenessConstraintException in case of duplicated name (or combination of action and resource)
     */
    @Override
    public void update(SystemPermission permission) throws UniquenessConstraintException {
        checkUniqueness(permission);
        getEntityManager().merge(permission);
    }

    /**
     * Deletes a unique Permission selected by his id.
     * @param permissionId to be deleted.
     */
    @Override
    public boolean delete(Long permissionId) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<PermissionEntity> criteriaDelete = cb.createCriteriaDelete(PermissionEntity.class);
        Root<PermissionEntity> permissionRoot = criteriaDelete.from(PermissionEntity.class);

        criteriaDelete.where(cb.equal(permissionRoot.get("id"),permissionId));
        return em.createQuery(criteriaDelete).executeUpdate() > 0;
    }

    /**
     * Deletes a list of Permissions selected by his id.
     * @param permissionIds to be deleted.
     */
    @Override
    public boolean delete(Collection<Long> permissionIds) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<PermissionEntity> criteriaDelete = cb.createCriteriaDelete(PermissionEntity.class);
        Root<PermissionEntity> permissionRoot = criteriaDelete.from(PermissionEntity.class);

        criteriaDelete.where(permissionRoot.get("id").in(permissionIds));
        return em.createQuery(criteriaDelete).executeUpdate() > 0;
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
        EntityManager em = getEntityManager();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<PermissionEntity> permissionRoot = criteriaQuery.from(PermissionEntity.class);

        criteriaQuery.select(criteriaBuilder.count(permissionRoot));
        if (permissionId !=  null) {
            criteriaQuery.where(criteriaBuilder.equal(permissionRoot.get("id"), permissionId));
        } else {
            criteriaQuery.where(criteriaBuilder.equal(permissionRoot.get("name"), permissionName));
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
        String query = "Select p From PermissionEntity p, ResourceEntity r, ActionEntity a " +
                "Where p.actionId = a.id and p.resourceId = r.id and r.name = :rName and a.name= :aName";
        return getEntityManager().createQuery(query, PermissionEntity.class).
                setParameter("rName", resource).
                setParameter("aName", action).
                getResultStream().findFirst().orElse(null);
    }

    /**
     * Count the number of all the permissions existent in the DB.
     * @return the count of permissions
     */
    @Override
    public long getTotalRecordsCount() {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        return getCount(criteriaBuilder.isTrue(criteriaBuilder.literal(true)), criteriaBuilder.createQuery(Long.class).from(PermissionEntity.class));
    }

    /**
     * Count the number of Permissions existent in the DB.
     * @return the count of Permissions
     */
    private long getCount(Predicate global, Root<PermissionEntity> permissionRoot) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        criteriaQuery.where(global);

        criteriaQuery.select(criteriaBuilder.count(permissionRoot));

        TypedQuery<Long> q = getEntityManager().createQuery(criteriaQuery);
        return q.getSingleResult();
    }

    /**
     * Seek for eventual duplicated information
     * @param permission base entity bean to seek for repeated information
     * @throws UniquenessConstraintException thrown in case of repeated information
     */
    private void checkUniqueness(SystemPermission permission) throws UniquenessConstraintException{
        List<PermissionEntity> alreadyExistentRecords = searchDuplicatedName(permission);
        if (!alreadyExistentRecords.isEmpty()) {
            throw new UniquenessConstraintException(GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Name"));
        }
        boolean existsForActionAndResource = existsForResourceAndAction(permission);
        if (existsForActionAndResource) {
            throw new UniquenessConstraintException(GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Action and Resource"));
        }
    }

    /**
     * Query to validate if an existent name (for permission) already exists in the database or not.
     * @param permission Permission information to look up.
     * @return list of Permissions with duplicated information.
     */
    private List<PermissionEntity> searchDuplicatedName(SystemPermission permission) {
        List<PermissionEntity> alreadyExistentRecords;
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<PermissionEntity> criteriaQuery = criteriaBuilder.createQuery(PermissionEntity.class);
        Root<PermissionEntity> permissionRoot = criteriaQuery.from(PermissionEntity.class);
        criteriaQuery.select(permissionRoot);
        Predicate global = criteriaBuilder.equal(permissionRoot.get("name"), permission.getName());
        if(permission.getId()!= null) {
            global=criteriaBuilder.and(global, criteriaBuilder.notEqual(permissionRoot.get("id"), permission.getId()));
        }
        criteriaQuery.where(global);
        TypedQuery<PermissionEntity> q = getEntityManager().createQuery(criteriaQuery);
        alreadyExistentRecords = q.getResultList();
        return alreadyExistentRecords;
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
                                           CriteriaBuilder criteriaBuilder, Root<PermissionEntity> permissionRoot) {
        Predicate global;
        if(filter.isLogicConjunction()) {
            global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        } else {
            global = criteriaBuilder.isFalse(criteriaBuilder.literal(true));
        }

        if (filter.getIds() != null && !filter.getIds().isEmpty()) {
            Predicate in = permissionRoot.get("id").in(filter.getIds());
            if(filter.isLogicConjunction()) {
                global = criteriaBuilder.and(global, in);
            } else {
                global = criteriaBuilder.or(global, in);
            }
        }

        global = getFieldPredicate("name", filter.getName(), filter, criteriaBuilder, permissionRoot, global);
        global = getFieldPredicate("actionId", filter.getActionId(),
                filter, criteriaBuilder, permissionRoot, global);
        global = getFieldPredicate("resourceId", filter.getResourceId(),
                filter, criteriaBuilder, permissionRoot, global);
        return global;
    }

    private Predicate getFieldPredicate(String name, Object value, PermissionSearchFilter filter,
                                        CriteriaBuilder criteriaBuilder, Root<PermissionEntity> permissionRoot,
                                        Predicate global) {
        if(value != null) {
            Predicate subPredicate;
            if (value instanceof String && !filter.isExact()) {
                subPredicate = criteriaBuilder.like(permissionRoot.get(name),"%"+value+"%");
            }
            else {
                subPredicate = criteriaBuilder.equal(permissionRoot.get(name), value);
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
     * Validator to see if given and request resource and action do exist and belong into one given permission
     * @param p system permission previously constructed to be gathered the action and the resource
     * @param em entity management table to be validated
     * @return true if resource and action do exist
     */
    protected boolean existsForResourceAndAction(SystemPermission p) {
        if (p.getResourceId() == null || p.getActionId() == null) {
            return false;
        }
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<PermissionEntity> permissionRoot = criteriaQuery.from(PermissionEntity.class);
        criteriaQuery.select(criteriaBuilder.count(permissionRoot));

        Predicate global = criteriaBuilder.equal(permissionRoot.get("resourceId"), p.getResourceId());
        global=criteriaBuilder.and(global, criteriaBuilder.equal(permissionRoot.get("actionId"), p.getActionId()));

        if(p.getId() != null) {
            global=criteriaBuilder.and(global, criteriaBuilder.notEqual(permissionRoot.get("id"), p.getId()));
        }
        criteriaQuery.where(global);
        Long size = getEntityManager().createQuery(criteriaQuery).getSingleResult();
        return size != 0;
    }

    /**
     * Entity manager getter
     * @return the correct requested entity manager
     */
    protected EntityManager getEntityManager() {
        return this.holder.getEntityManager();
    }
}
