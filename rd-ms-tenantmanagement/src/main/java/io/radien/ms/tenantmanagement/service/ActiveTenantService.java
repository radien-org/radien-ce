/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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
package io.radien.ms.tenantmanagement.service;

import static io.radien.api.SystemVariables.ID;
import static io.radien.api.SystemVariables.TENANT_ID;
import static io.radien.api.SystemVariables.TENANT_NAME;
import static io.radien.api.SystemVariables.USER_ID;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import io.radien.api.entity.Page;
import io.radien.api.model.tenant.SystemActiveTenant;
import io.radien.api.model.tenant.SystemActiveTenantSearchFilter;
import io.radien.api.service.tenant.ActiveTenantServiceAccess;
import io.radien.exception.ActiveTenantException;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.ms.tenantmanagement.client.entities.ActiveTenantSearchFilter;
import io.radien.ms.tenantmanagement.entities.ActiveTenantEntity;

/**
 * Active Tenant requests to be performed into the DB and actions to take place
 * @author Bruno Gama
 */
@Stateful
public class ActiveTenantService implements ActiveTenantServiceAccess {

    private static final long serialVersionUID = -1100263816862655992L;

    @Inject
    private EntityManagerHolder emh;

    /**
     * Gets the System Active Tenant searching by the PK (id).
     *
     * @param id to be searched.
     * @return the system active tenant requested to be found.
     */
    @Override
    public SystemActiveTenant get(Long id) {
        return emh.getEm().find(ActiveTenantEntity.class, id);
    }

    /**
     * Gets the System Active Tenant searching by the user and tenant id
     *
     * @param userId to be searched.
     * @param tenantId to be searched
     * @return the system active tenant requested to be found.
     */
    @Override
    public List<? extends SystemActiveTenant> getByUserAndTenant(Long userId, Long tenantId) {
        EntityManager em = emh.getEm();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ActiveTenantEntity> criteriaQuery = criteriaBuilder.createQuery(ActiveTenantEntity.class);
        Root<ActiveTenantEntity> root = criteriaQuery.from(ActiveTenantEntity.class);

        criteriaQuery.select(root);

        criteriaQuery.where(criteriaBuilder.and(criteriaBuilder.equal(root.get(USER_ID.getFieldName()), userId),
                criteriaBuilder.equal(root.get(TENANT_ID.getFieldName()), tenantId)));
        TypedQuery<ActiveTenantEntity> q = em.createQuery(criteriaQuery);

        return q.getResultList();
    }

    /**
     * Gets all the active tenants into a pagination mode.
     * @param search name description for some active tenants
     * @param pageNo of the requested information. Where the active tenant is.
     * @param pageSize total number of pages returned in the request.
     * @param sortBy sort filter criteria.
     * @param isAscending ascending filter criteria.
     * @return a page of system active tenants.
     */
    @Override
    public Page<SystemActiveTenant> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) {
        EntityManager entityManager = emh.getEm();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ActiveTenantEntity> criteriaQuery = criteriaBuilder.createQuery(ActiveTenantEntity.class);
        Root<ActiveTenantEntity> activeTenantRoot = criteriaQuery.from(ActiveTenantEntity.class);

        criteriaQuery.select(activeTenantRoot);
        Predicate global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        if(search!= null) {
            global = criteriaBuilder.and(criteriaBuilder.like(activeTenantRoot.get(TENANT_NAME.getFieldName()), search));
            criteriaQuery.where(global);
        }
        if(sortBy != null && !sortBy.isEmpty()){
            List<Order> orders;
            if(isAscending){
                orders = sortBy.stream().map(i->criteriaBuilder.asc(activeTenantRoot.get(i))).collect(Collectors.toList());
            } else {
                orders = sortBy.stream().map(i->criteriaBuilder.desc(activeTenantRoot.get(i))).collect(Collectors.toList());
            }
            criteriaQuery.orderBy(orders);
        }
        TypedQuery<ActiveTenantEntity> q= entityManager.createQuery(criteriaQuery);

        q.setFirstResult((pageNo-1) * pageSize);
        q.setMaxResults(pageSize);

        List<? extends SystemActiveTenant> systemActiveTenants = q.getResultList();

        int totalRecords = Math.toIntExact(getCount(global, activeTenantRoot));
        int totalPages = totalRecords%pageSize==0 ? totalRecords/pageSize : totalRecords/pageSize+1;

        return new Page<SystemActiveTenant>(systemActiveTenants, pageNo, totalRecords, totalPages);
    }

    /**
     * Gets all the active tenants matching the given filter information
     * @param filter information to search
     * @return a list o found system active tenants
     */
    @Override
    public List<? extends SystemActiveTenant> get(SystemActiveTenantSearchFilter filter) {
        EntityManager em = emh.getEm();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ActiveTenantEntity> criteriaQuery = criteriaBuilder.createQuery(ActiveTenantEntity.class);
        Root<ActiveTenantEntity> root = criteriaQuery.from(ActiveTenantEntity.class);

        criteriaQuery.select(root);

        List<Predicate> predicateList = getFilteredPredicate((ActiveTenantSearchFilter) filter, criteriaBuilder, root);
        if(!predicateList.isEmpty()) {
            Predicate[] predicateArray = predicateList.toArray(new Predicate[]{});
            if(((ActiveTenantSearchFilter)filter).isLogicConjunction()) {
                criteriaQuery.where(criteriaBuilder.and(predicateArray));
            } else {
                criteriaQuery.where(criteriaBuilder.or(predicateArray));
            }
        }
        TypedQuery<ActiveTenantEntity> q = em.createQuery(criteriaQuery);

        return q.getResultList();

    }

    /**
     * Is LogicalConjunction represents if you join the fields on the predicates with "or" or "and"
     * the predicate is build with the logic (start,operator,newPredicate)
     * where start represents the already joined predicates
     * operator is "and" or "or"
     * depending on the operator the start may need to be true or false
     * true and predicate1 and predicate2
     * false or predicate1 or predicate2
     * @param filter information to be search
     * @param criteriaBuilder to be used
     * @param activeTenantRoot table to be search
     * @return a filtered predicate
     */
    private List<Predicate> getFilteredPredicate(ActiveTenantSearchFilter filter, CriteriaBuilder criteriaBuilder, Root<ActiveTenantEntity> activeTenantRoot) {
        List<Predicate> list =new ArrayList<>();
        getFieldPredicate(USER_ID.getFieldName(), filter.getUserId(), filter, criteriaBuilder, activeTenantRoot).ifPresent(list::add);
        getFieldPredicate(TENANT_ID.getFieldName(), filter.getTenantId(), filter, criteriaBuilder, activeTenantRoot).ifPresent(list::add);
        return list;
    }

    /**
     * Puts the requested fields into a predicate line
     * @param name of the field
     * @param value of the field
     * @param filter complete filter
     * @param criteriaBuilder to be used
     * @param activeTenantRoot table to be used
     * @return a constructed predicate
     */
    private Optional<Predicate> getFieldPredicate(String name, Object value, ActiveTenantSearchFilter filter, CriteriaBuilder criteriaBuilder, Root<ActiveTenantEntity> activeTenantRoot) {
        if(value != null) {
            Predicate subPredicate = criteriaBuilder.equal(activeTenantRoot.get(name), value);
            if (value instanceof String && !filter.isExact()) {
                subPredicate = criteriaBuilder.like(activeTenantRoot.get(name),"%"+value+"%");
            }
            return Optional.ofNullable(subPredicate);
        }
        return Optional.empty();
    }

    /**
     * Creates the requested and given active tenant information into the DB.
     *
     * @param activeTenant to be added
     */
    @Override
    public void create(SystemActiveTenant activeTenant) throws ActiveTenantException {
        emh.getEm().persist(activeTenant);
    }

    /**
     * Updates the requested and given Active tenant information into the DB.
     *
     * @param activeTenant to be updated
     */
    @Override
    public void update(SystemActiveTenant activeTenant) throws ActiveTenantException {
        emh.getEm().merge(activeTenant);
    }

    /**
     * Deletes a unique active tenant selected by his id.
     *
     * @param activeTenantId to be deleted.
     */
    @Override
    public boolean delete(Long activeTenantId) {
        EntityManager em = emh.getEm();
        return delete(activeTenantId, em);
    }

    /**
     * Delete ActiveTenants that exist for following parameters
     * @param tenantId tenant identifier
     * @param userId user identifier
     * @return true in case of success (records founds and removed), otherwise false
     */
    public boolean delete(Long tenantId, Long userId) {
        if (tenantId == null && userId == null) {
            throw new IllegalArgumentException(GenericErrorCodeMessage.
                    ACTIVE_TENANT_DELETE_WITHOUT_TENANT_AND_USER.toString());
        }
        EntityManager em = emh.getEm();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<ActiveTenantEntity> criteriaDelete = cb.createCriteriaDelete(ActiveTenantEntity.class);
        Root<ActiveTenantEntity> activeTenantRoot = criteriaDelete.from(ActiveTenantEntity.class);

        List<Predicate> predicates = new ArrayList<>();
        if (tenantId != null) {
            predicates.add(cb.equal(activeTenantRoot.get(TENANT_ID.getFieldName()), tenantId));
        }
        if (userId != null) {
            predicates.add(cb.equal(activeTenantRoot.get(USER_ID.getFieldName()), userId));
        }
        criteriaDelete.where(cb.and(predicates.toArray(new Predicate[0])));
        return em.createQuery(criteriaDelete).executeUpdate() > 0;
    }

    /**
     * Deletes a list of active tenants selected by his id.
     *
     * @param activeTenantId to be deleted.
     */
    @Override
    public void delete(Collection<Long> activeTenantId) {
        EntityManager em = emh.getEm();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<ActiveTenantEntity> criteriaDelete = cb.createCriteriaDelete(ActiveTenantEntity.class);
        Root<ActiveTenantEntity> userRoot = criteriaDelete.from(ActiveTenantEntity.class);

        criteriaDelete.where(userRoot.get(ID.getFieldName()).in(activeTenantId));
        em.createQuery(criteriaDelete).executeUpdate();
    }

    /**
     * Validates if specific requested Active Tenant exists
     * @param userId to be searched
     * @param tenantId to be search
     * @return response true if it exists
     */
    @Override
    public boolean exists(Long userId, Long tenantId) {
        EntityManager em = emh.getEm();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<ActiveTenantEntity> contractRoot = criteriaQuery.from(ActiveTenantEntity.class);

        criteriaQuery.select(criteriaBuilder.count(contractRoot));

        criteriaQuery.where(criteriaBuilder.and(criteriaBuilder.equal(contractRoot.get(USER_ID.getFieldName()), userId),
                criteriaBuilder.equal(contractRoot.get(TENANT_ID.getFieldName()), tenantId)));

        Long size = em.createQuery(criteriaQuery).getSingleResult();

        return size != 0;
    }

    /**
     * Count the number of active tenants existent in the DB.
     * @return the count of active tenants
     */
    private long getCount(Predicate global, Root<ActiveTenantEntity> userRoot) {
        EntityManager em = emh.getEm();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        criteriaQuery.where(global);

        criteriaQuery.select(criteriaBuilder.count(userRoot));

        TypedQuery<Long> q= em.createQuery(criteriaQuery);

        return q.getSingleResult();
    }

    /**
     * Method to delete from the db a specific active tenant
     * @param activeTenant to be deleted
     * @param entityManager already created entity manager
     * @return true if deletion has been a success or false if there was an issue
     */
    protected boolean delete(Long activeTenant, EntityManager entityManager) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<ActiveTenantEntity> criteriaDelete = cb.createCriteriaDelete(ActiveTenantEntity.class);
        Root<ActiveTenantEntity> userRoot = criteriaDelete.from(ActiveTenantEntity.class);
        criteriaDelete.where(cb.equal(userRoot.get(ID.getFieldName()), activeTenant));
        int ret = entityManager.createQuery(criteriaDelete).executeUpdate();
        return ret > 0;
    }
}
