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
package io.radien.ms.tenantmanagement.datalayer;

import io.radien.api.entity.Page;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.tenant.SystemTenantSearchFilter;
import io.radien.api.service.tenant.TenantServiceAccess;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.SystemException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.tenantmanagement.client.entities.TenantSearchFilter;
import io.radien.ms.tenantmanagement.client.entities.TenantType;
import io.radien.ms.tenantmanagement.entities.TenantEntity;
import io.radien.ms.tenantmanagement.util.EntityManagerHolder;
import java.util.Objects;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Order;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Tenant requests to be performed into the DB and actions to take place
 * @author Nuno Santana
 */
@Stateful
public class TenantService implements TenantServiceAccess {

    private static final long serialVersionUID = 5367539772479734994L;

    @Inject
    private EntityManagerHolder emh;

    /**
     * Gets the System Contract searching by the PK (id).
     *
     * @param tenantId to be searched.
     * @return the system contract requested to be found.
     */
    @Override
    public SystemTenant get(Long tenantId) {
        return emh.getEm().find(TenantEntity.class, tenantId);
    }

    /**
     * Gets all the tenants into a pagination mode.
     * @param search name description for some tenant
     * @param pageNo of the requested information. Where the tenant is.
     * @param pageSize total number of pages returned in the request.
     * @param sortBy sort filter criteria.
     * @param isAscending ascending filter criteria.
     * @return a page of system tenants.
     */
    @Override
    public Page<SystemTenant> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) {
        EntityManager entityManager = emh.getEm();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TenantEntity> criteriaQuery = criteriaBuilder.createQuery(TenantEntity.class);
        Root<TenantEntity> tenantRoot = criteriaQuery.from(TenantEntity.class);

        criteriaQuery.select(tenantRoot);
        Predicate global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        if(search!= null) {
            global = criteriaBuilder.and(criteriaBuilder.like(tenantRoot.get("name"), search));
            criteriaQuery.where(global);
        }
        if(sortBy != null && !sortBy.isEmpty()){
            List<Order> orders;
            if(isAscending){
                orders = sortBy.stream().map(i->criteriaBuilder.asc(tenantRoot.get(i))).collect(Collectors.toList());
            } else {
                orders = sortBy.stream().map(i->criteriaBuilder.desc(tenantRoot.get(i))).collect(Collectors.toList());
            }
            criteriaQuery.orderBy(orders);
        }
        TypedQuery<TenantEntity> q= entityManager.createQuery(criteriaQuery);

        q.setFirstResult((pageNo-1) * pageSize);
        q.setMaxResults(pageSize);

        List<? extends SystemTenant> systemTenants = q.getResultList();

        int totalRecords = Math.toIntExact(getCount(global, tenantRoot));
        int totalPages = totalRecords%pageSize==0 ? totalRecords/pageSize : totalRecords/pageSize+1;

        return new Page<>(systemTenants, pageNo, totalRecords, totalPages);
    }

    /**
     * Gets all the tenants matching the given filte information
     * @param filter information to search
     * @return a list o found system tenants
     */
    @Override
    public List<? extends SystemTenant> get(SystemTenantSearchFilter filter) {
        EntityManager em = emh.getEm();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<TenantEntity> criteriaQuery = criteriaBuilder.createQuery(TenantEntity.class);
        Root<TenantEntity> root = criteriaQuery.from(TenantEntity.class);

        criteriaQuery.select(root);
        List<Predicate> predicateList = getFilteredPredicate((TenantSearchFilter) filter, criteriaBuilder, root);
        if(!predicateList.isEmpty()) {
            if (((TenantSearchFilter) filter).isLogicConjunction()) {
                criteriaQuery.where(criteriaBuilder.and(predicateList.toArray(new Predicate[]{})));
            } else {
                criteriaQuery.where(criteriaBuilder.or(predicateList.toArray(new Predicate[]{})));
            }
        }
        TypedQuery<TenantEntity> q = em.createQuery(criteriaQuery);

        return q.getResultList();

    }

    /**
     * Creates the requested and given Tenant information into the DB.
     *
     * @param tenant to be added
     * @throws UniquenessConstraintException in case of duplicated email/duplicated logon
     * @throws SystemException in case of invalid data
     */
    @Override
    public void create(SystemTenant tenant) throws UniquenessConstraintException, SystemException {
        validateTenant(tenant);
        List<TenantEntity> alreadyExistentRecords = searchDuplicatedFields(tenant);
        if (alreadyExistentRecords.isEmpty()) {
            emh.getEm().persist(tenant);
        } else {
            throw new UniquenessConstraintException(GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Name"));
        }
    }

    /**
     * Updates the requested and given Contract information into the DB.
     *
     * @param tenant to be updated
     * @throws UniquenessConstraintException in case of duplicated name
     */
    @Override
    public void update(SystemTenant tenant) throws UniquenessConstraintException, SystemException {
        validateTenant(tenant);
        List<TenantEntity> alreadyExistentRecords = searchDuplicatedFields(tenant);
        if (alreadyExistentRecords.isEmpty()) {
            emh.getEm().merge(tenant);
        } else {
            throw new UniquenessConstraintException(GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Name"));
        }
    }

    /**
     * Deletes a unique contract selected by his id.
     *
     * @param tenantId to be deleted.
     */
    @Override
    public boolean delete(Long tenantId) {
        EntityManager em = emh.getEm();
        return delete(tenantId, em);
    }

    /**
     * Deletes a list of contracts selected by his id.
     *
     * @param contractIds to be deleted.
     */
    @Override
    public boolean delete(Collection<Long> contractIds) {
        EntityManager em = emh.getEm();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<TenantEntity> criteriaDelete = cb.createCriteriaDelete(TenantEntity.class);
        Root<TenantEntity> userRoot = criteriaDelete.from(TenantEntity.class);

        criteriaDelete.where(userRoot.get("id").in(contractIds));
        return em.createQuery(criteriaDelete).executeUpdate() > 0;
    }

    /**
     * Requests the DB to delete all the children tenants of the requested tenant
     * @param id to be deleted and all his children
     * @return true if deletion has been a success or false if there was an issue
     */
    public boolean deleteTenantHierarchy(Long id) {
        EntityManager em = emh.getEm();
        return deleteChildren(id, em);
    }

    /**
     * Validates if specific requested Tenant exists
     * @param tenantId to be searched
     * @return response true if it exists
     */
    @Override
    public boolean exists(Long tenantId) {
        EntityManager em = emh.getEm();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<TenantEntity> contractRoot = criteriaQuery.from(TenantEntity.class);

        criteriaQuery.select(criteriaBuilder.count(contractRoot));
        criteriaQuery.where(criteriaBuilder.equal(contractRoot.get("id"), tenantId));

        Long size = em.createQuery(criteriaQuery).getSingleResult();

        return size != 0;
    }

    /**
     * The purpose for this method is to validate a tenant before it being inserted or updated
     * @param tenant Tenant to be checked
     * @throws SystemException in case of any coherence on the data
     */
    private void validateTenant(SystemTenant tenant) throws SystemException {
        if (validateIfFieldsAreEmpty(tenant.getName())) {
            throw new SystemException(GenericErrorCodeMessage.TENANT_FIELD_NOT_INFORMED.toString("name"));
        }

        if (validateIfFieldsAreEmpty(tenant.getTenantKey())) {
            throw new SystemException(GenericErrorCodeMessage.TENANT_FIELD_NOT_INFORMED.toString("tenantKey"));
        }

        if (tenant.getTenantType() == null) {
            throw new SystemException(GenericErrorCodeMessage.TENANT_FIELD_NOT_INFORMED.toString("tenantType"));
        }

        if ((tenant.getTenantEnd() != null && tenant.getTenantStart() != null) && tenant.getTenantEnd().compareTo(tenant.getTenantStart()) <= 0) {
            throw new SystemException(GenericErrorCodeMessage.TENANT_END_DATE_IS_IS_INVALID.toString());
        }

        if (Objects.equals(tenant.getTenantType(), TenantType.ROOT)) {
            validateRootTenant(tenant);
        }

        if(Objects.equals(tenant.getTenantType(), TenantType.CLIENT)) {
            validateClientTenant(tenant);
        }

        if(Objects.equals(tenant.getTenantType(), TenantType.SUB)) {
            validateSubTenant(tenant);
        }
    }

    /**
     * Will validate if given fields are empty or null
     * @param field to be validated
     * @return true in case that given field is null or empty ("")
     */
    private boolean validateIfFieldsAreEmpty(String field) {
        return field == null || field.trim().isEmpty();
    }

    /**
     * Validates rules for the root tenants
     * @param tenant to be validated
     * @throws SystemException in case of any issue in the rules
     */
    private void validateRootTenant(SystemTenant tenant) throws SystemException {
        if (tenant.getParentId() != null) {
            throw new SystemException(GenericErrorCodeMessage.TENANT_ROOT_WITH_PARENT.toString());
        }

        if(tenant.getClientId() != null) {
            throw new SystemException(GenericErrorCodeMessage.TENANT_ROOT_WITH_CLIENT.toString());
        }

        // There must only exist one Root Tenant
        List<? extends SystemTenant> list = this.get(new TenantSearchFilter(null, TenantType.ROOT.getDescription(), null,false, false));
        if ((!list.isEmpty()) && (tenant.getId() == null || list.size() > 1 || !list.get(0).getId().equals(tenant.getId()))) {
                throw new SystemException(GenericErrorCodeMessage.TENANT_ROOT_ALREADY_INSERTED.toString());
        }
    }

    /**
     * Validates rules for the client tenants
     * @param tenant to be validated
     * @throws SystemException in case of any issue in the rules
     */
    private void validateClientTenant(SystemTenant tenant) throws SystemException {
        if(tenant.getParentId() == null) {
            throw new SystemException(GenericErrorCodeMessage.TENANT_PARENT_NOT_INFORMED.toString());
        }

        if(get(tenant.getParentId()) == null) {
            throw new SystemException(GenericErrorCodeMessage.TENANT_PARENT_NOT_FOUND.toString());
        }

        if(get( tenant.getParentId() ).getTenantType().equals(TenantType.SUB)) {
            throw new SystemException(GenericErrorCodeMessage.TENANT_PARENT_TYPE_IS_INVALID.toString());
        }
    }

    /**
     * Validates rules for the sub tenants
     * @param tenant to be validated
     * @throws SystemException in case of any issue in the rules
     */
    private void validateSubTenant(SystemTenant tenant) throws SystemException {
        if(tenant.getParentId() == null){
            throw new SystemException(GenericErrorCodeMessage.TENANT_PARENT_NOT_INFORMED.toString());
        }

        if(tenant.getClientId() == null) {
            throw new SystemException(GenericErrorCodeMessage.TENANT_CLIENT_NOT_INFORMED.toString());
        }

        if(get(tenant.getParentId()) == null) {
            throw new SystemException(GenericErrorCodeMessage.TENANT_PARENT_NOT_FOUND.toString());
        }

        if(get(tenant.getClientId()) == null) {
            throw new SystemException(GenericErrorCodeMessage.TENANT_CLIENT_NOT_FOUND.toString());
        }
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
     * @param tenantRoot table to be search
     * @return a filtered predicate
     */
    private List<Predicate> getFilteredPredicate(TenantSearchFilter filter, CriteriaBuilder criteriaBuilder, Root<TenantEntity> tenantRoot) {
        List<Predicate> list = new ArrayList<>();

        if (filter.getIds() != null && !filter.getIds().isEmpty()) {
            Predicate in = tenantRoot.get("id").in(filter.getIds());
            list.add(in);
        }

        getFieldPredicate("name", filter.getName(), filter, criteriaBuilder, tenantRoot).ifPresent(list::add);
        getFieldPredicate("tenantType", filter.getTenantType(), filter, criteriaBuilder, tenantRoot).ifPresent(list::add);

        return list;
    }

    /**
     * Puts the requested fields into a predicate line
     * @param name of the field
     * @param value of the field
     * @param filter complete filter
     * @param criteriaBuilder to be used
     * @param tenantRoot table to be used
     * @return a constructed predicate
     */
    private Optional<Predicate> getFieldPredicate(String name, Object value, TenantSearchFilter filter, CriteriaBuilder criteriaBuilder, Root<TenantEntity> tenantRoot) {
        if(value != null) {
            Predicate subPredicate;
            if (value instanceof String && !filter.isExact()) {
                subPredicate = criteriaBuilder.like(tenantRoot.get(name),"%"+value+"%");
            }
            else {
                subPredicate = criteriaBuilder.equal(tenantRoot.get(name), value);
            }

            return Optional.of(subPredicate);
        }
        return Optional.empty();
    }

    /**
     * Query to validate if an existent name already exists in the database or not.
     *
     * @param tenant user information to look up.
     * @return list of users with duplicated information.
     */
    private List<TenantEntity> searchDuplicatedFields(SystemTenant tenant) {
        EntityManager em = emh.getEm();
        List<TenantEntity> alreadyExistentRecords;
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<TenantEntity> criteriaQuery = criteriaBuilder.createQuery(TenantEntity.class);
        Root<TenantEntity> root = criteriaQuery.from(TenantEntity.class);
        criteriaQuery.select(root);
        Predicate global =
                criteriaBuilder.equal(root.get("name"), tenant.getName());
        if (tenant.getId() != null) {
            global = criteriaBuilder.and(global, criteriaBuilder.notEqual(root.get("id"), tenant.getId()));
        }
        criteriaQuery.where(global);
        TypedQuery<TenantEntity> q = em.createQuery(criteriaQuery);
        alreadyExistentRecords = q.getResultList();
        return alreadyExistentRecords;
    }

    /**
     * Count the number of tenants existent in the DB.
     * @return the count of tenants
     */
    private long getCount(Predicate global, Root<TenantEntity> userRoot) {
        EntityManager em = emh.getEm();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        criteriaQuery.where(global);

        criteriaQuery.select(criteriaBuilder.count(userRoot));

        TypedQuery<Long> q= em.createQuery(criteriaQuery);

        return q.getSingleResult();
    }

    /**
     * Method to get all the requested tenant children tenants
     * @param tenantId of the parent tenant
     * @return a list of all the tenant children ids
     */
    public List<SystemTenant> getChildren(Long tenantId) {
        EntityManager em = emh.getEm();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<TenantEntity> criteriaQuery = criteriaBuilder.createQuery(TenantEntity.class);
        Root<TenantEntity> tenantRoot = criteriaQuery.from(TenantEntity.class);
        Predicate predicate = criteriaBuilder.equal(tenantRoot.get("parentId"), tenantId);
        criteriaQuery.where(predicate);
        TypedQuery<TenantEntity> q = em.createQuery(criteriaQuery);
        return new ArrayList<>(q.getResultList());
    }

    /**
     * Requests the DB to delete all the children tenants of the requested tenant
     * @param tenantId to be deleted and all his children
     * @param entityManager already created entity manager
     * @return true if deletion has been a success or false if there was an issue
     */
    protected boolean deleteChildren(Long tenantId, EntityManager entityManager) {
        List<SystemTenant> children = getChildren(tenantId);

        if (!children.isEmpty()) {
            for (SystemTenant child: children) {
                deleteChildren(child.getId(), entityManager);
            }
        }

        return delete(tenantId, entityManager);
    }

    /**
     * Method to delete from the db a specific tenant
     * @param tenantId to be deleted
     * @param entityManager already created entity manager
     * @return true if deletion has been a success or false if there was an issue
     */
    protected boolean delete(Long tenantId, EntityManager entityManager) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<TenantEntity> criteriaDelete = cb.createCriteriaDelete(TenantEntity.class);
        Root<TenantEntity> userRoot = criteriaDelete.from(TenantEntity.class);
        criteriaDelete.where(cb.equal(userRoot.get("id"), tenantId));
        int ret = entityManager.createQuery(criteriaDelete).executeUpdate();
        return ret > 0;
    }
}
