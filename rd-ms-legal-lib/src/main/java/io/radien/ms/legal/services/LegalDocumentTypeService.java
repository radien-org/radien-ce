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
 *
 */
package io.radien.ms.legal.services;

import io.radien.api.SystemVariables;
import io.radien.api.entity.Page;
import io.radien.api.model.legal.SystemLegalDocumentType;
import io.radien.api.model.legal.SystemLegalDocumentTypeSearchFilter;
import io.radien.api.service.legal.LegalDocumentTypeServiceAccess;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.LegalDocumentTypeNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.legal.entities.LegalDocumentTypeEntity;
import java.util.List;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.radien.api.util.ModelServiceUtil.getCountFromModelService;
import static io.radien.api.util.ModelServiceUtil.getFieldPredicateFromModelService;
import static io.radien.api.util.ModelServiceUtil.getFilteredPredicateFromModelService;
import static io.radien.api.util.ModelServiceUtil.getListOrderSortBy;

/**
 * Data service implementation for {@link LegalDocumentTypeEntity}
 * @author Newton Carvalho
 */
@Stateful
public class LegalDocumentTypeService implements LegalDocumentTypeServiceAccess {

    private static final Logger log = LoggerFactory.getLogger(LegalDocumentTypeService.class);
    private static final long serialVersionUID = 648782616414244197L;


    @PersistenceContext(unitName = "persistenceUnit", type = PersistenceContextType.EXTENDED)
    private transient EntityManager em;

    /**
     * Gets the System LegalDocumentType searching by the PK (id).
     * @param legalDocumentTypeId to be searched.
     * @return the system LegalDocumentType requested to be found.
     * @throws LegalDocumentTypeNotFoundException in case the requested legalDocumentType could not be found
     */
    public SystemLegalDocumentType get(Long legalDocumentTypeId) throws LegalDocumentTypeNotFoundException {
        LegalDocumentTypeEntity result = em.find(LegalDocumentTypeEntity.class, legalDocumentTypeId);
        if(result == null){
            throw new LegalDocumentTypeNotFoundException(legalDocumentTypeId.toString());
        }
        return result;
    }

    /**
     * Gets all the LegalDocumentTypes into a pagination mode.
     * Can be filtered by name LegalDocumentType.
     * @param search name description for some legalDocumentType
     * @param pageNo of the requested information. Where the LegalDocumentType is.
     * @param pageSize total number of pages returned in the request.
     * @param sortBy sort filter criteria.
     * @param isAscending ascending filter criteria.
     * @return a page of system LegalDocumentTypes.
     */
    public Page<SystemLegalDocumentType> getAll(String search, int pageNo, int pageSize, List<String> sortBy,
                                                boolean isAscending) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<LegalDocumentTypeEntity> criteriaQuery = criteriaBuilder.createQuery(LegalDocumentTypeEntity.class);
        Root<LegalDocumentTypeEntity> entityRoot = criteriaQuery.from(LegalDocumentTypeEntity.class);

        criteriaQuery.select(entityRoot);

        Predicate global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        if(search!= null) {
            global = criteriaBuilder.and(criteriaBuilder.like(entityRoot.get(SystemVariables.NAME.getFieldName()), search));
            criteriaQuery.where(global);
        }

        if(sortBy != null && !sortBy.isEmpty()){
            List<Order> orders = getListOrderSortBy(isAscending, entityRoot, criteriaBuilder, sortBy);
            criteriaQuery.orderBy(orders);
        }

        TypedQuery<LegalDocumentTypeEntity> q=em.createQuery(criteriaQuery);

        q.setFirstResult((pageNo-1) * pageSize);
        q.setMaxResults(pageSize);

        List<? extends SystemLegalDocumentType> systemLdtTypes = q.getResultList();

        int totalRecords = Math.toIntExact(getCount(global, entityRoot));
        int totalPages = totalRecords%pageSize==0 ? totalRecords/pageSize : totalRecords/pageSize+1;

        return new Page<>(systemLdtTypes, pageNo, totalRecords, totalPages);
    }

    /**
     * Count the number of legal document types existent in the DB.
     * @return the count of legal document types
     */
    private long getCount(Predicate global, Root<LegalDocumentTypeEntity> root) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        return getCountFromModelService(criteriaBuilder, global, root, em);
    }

    /**
     * Get LegalDocumentTypesBy unique columns
     * @param filter entity with available filters to search LegalDocumentType
     * @return a list of found legalDocumentTypes that match the given search filter
     */
    public List<? extends SystemLegalDocumentType> getLegalDocumentTypes(SystemLegalDocumentTypeSearchFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<LegalDocumentTypeEntity> criteriaQuery = cb.createQuery(LegalDocumentTypeEntity.class);
        Root<LegalDocumentTypeEntity> root = criteriaQuery.from(LegalDocumentTypeEntity.class);

        criteriaQuery.select(root);

        Predicate global = getFilteredPredicate(filter, cb, root);

        criteriaQuery.where(global);
        TypedQuery<LegalDocumentTypeEntity> q = em.createQuery(criteriaQuery);

        return q.getResultList();
    }

    /**
     * Will filter all the fields given in the criteria builder and in the filter and create the
     * where clause for the query
     * @param filter fields to be searched for
     * @param criteriaBuilder database query builder
     * @param userRoot database table to search the information
     * @return a constructed predicate with the fields needed to be search
     */
    protected Predicate getFilteredPredicate(SystemLegalDocumentTypeSearchFilter filter,
                                             CriteriaBuilder criteriaBuilder,
                                             Root<LegalDocumentTypeEntity> userRoot) {
        Predicate global;

        boolean isFilterIds = filter.getIds() != null && !filter.getIds().isEmpty();
        global = getFilteredPredicateFromModelService(isFilterIds, filter.getIds(),
                filter.isLogicConjunction(), criteriaBuilder,userRoot);

        global = getFieldPredicate(SystemVariables.NAME.getFieldName(),
                filter.getName(), filter, criteriaBuilder, userRoot, global);

        global = getFieldPredicate(SystemVariables.TENANT_ID.getFieldName(),
                filter.getTenantId(), filter, criteriaBuilder, userRoot, global);

        global = getFieldPredicate(SystemVariables.TO_BE_SHOWN.getFieldName(),
                filter.isToBeShown(), filter, criteriaBuilder, userRoot, global);

        global = getFieldPredicate(SystemVariables.TO_BE_ACCEPTED.getFieldName(),
                filter.isToBeAccepted(), filter, criteriaBuilder, userRoot, global);

        return global;
    }

    /**
     * Method that will create in the database query where clause each and single search
     * @param name of the field to be search in the query
     * @param value of the field to be search or compared in the query
     * @param filter complete requested filter for further validations
     * @param criteriaBuilder database query builder
     * @param entityRoot database table to search the information
     * @param global complete where clause to be merged into the constructed information
     * @return a constructed predicate with the fields needed to be search
     */
    protected Predicate getFieldPredicate(String name, Object value, SystemLegalDocumentTypeSearchFilter filter,
                                        CriteriaBuilder criteriaBuilder,
                                        Root<LegalDocumentTypeEntity> entityRoot, Predicate global) {
        if(value != null) {
            return getFieldPredicateFromModelService(value, filter.isExact(),
                    filter.isLogicConjunction(), criteriaBuilder, entityRoot.get(name), global);
        }
        return global;
    }

    /**
     * Creates the requested LegalDocumentType information into the DB.
     * @param legalDocumentType to be added/inserted
     * @throws UniquenessConstraintException in case of duplicated name (or combination of name and tenant)
     */
    public void create(SystemLegalDocumentType legalDocumentType) throws UniquenessConstraintException {
        checkUniqueness(legalDocumentType);
        em.persist(legalDocumentType);
    }

    /**
     * Updates the requested LegalDocumentType information into the DB.
     * @param legalDocumentType to be updated
     * @throws UniquenessConstraintException in case of duplicated name (or combination of name and tenant)
     * @throws LegalDocumentTypeNotFoundException in case of not existent legalDocumentType for the give id
     */
    public void update(SystemLegalDocumentType legalDocumentType) throws UniquenessConstraintException,
            LegalDocumentTypeNotFoundException {
        if(em.find(LegalDocumentTypeEntity.class, legalDocumentType.getId()) == null) {
            throw new LegalDocumentTypeNotFoundException(GenericErrorCodeMessage.RESOURCE_NOT_FOUND.toString());
        }
        checkUniqueness(legalDocumentType);
        em.merge(legalDocumentType);
    }

    /**
     * Deletes a unique LegalDocumentType selected by his id.
     * @param legalDocumentTypeId to be deleted.
     */
    public void delete(Long legalDocumentTypeId) throws LegalDocumentTypeNotFoundException {
        if (legalDocumentTypeId == null) {
            throw new IllegalArgumentException(GenericErrorCodeMessage.LEGAL_DOCUMENT_TYPE_FIELD_MANDATORY.
                    toString(SystemVariables.ID.getLabel()));
        }
        if(em.find(LegalDocumentTypeEntity.class, legalDocumentTypeId) == null) {
            throw new LegalDocumentTypeNotFoundException(GenericErrorCodeMessage.RESOURCE_NOT_FOUND.toString());
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<LegalDocumentTypeEntity> criteriaDelete = cb.createCriteriaDelete(LegalDocumentTypeEntity.class);
        Root<LegalDocumentTypeEntity> deleteRoot = criteriaDelete.from(LegalDocumentTypeEntity.class);
        criteriaDelete.where(cb.equal(deleteRoot.get(SystemVariables.ID.getFieldName()),legalDocumentTypeId));
        em.createQuery(criteriaDelete).executeUpdate();
    }


    /**
     * Seek for eventual duplicated information
     * @param legalDocumentType base entity bean to seek for repeated information
     * @throws UniquenessConstraintException thrown in case of repeated information
     */
    protected void checkUniqueness(SystemLegalDocumentType legalDocumentType) throws UniquenessConstraintException{
        boolean alreadyExistentRecords = isAssociationAlreadyExistent(legalDocumentType.getName(),
                legalDocumentType.getTenantId(), legalDocumentType.getId());
        if (alreadyExistentRecords) {
            throw new UniquenessConstraintException(GenericErrorCodeMessage.DUPLICATED_FIELD_COMBINATION.
                    toString(SystemVariables.NAME.getFieldName(), SystemVariables.TENANT_ID.getFieldName()));
        }
    }

    /**
     * Check if there is already a Legal Document Type for a name + tenantId combination
     * @param name Legal Document Type name
     * @param tenantId Tenant Identifier
     * @param id Legal document type identifier
     * @return true if already exists, otherwise returns false
     */
    protected boolean isAssociationAlreadyExistent(String name, Long tenantId, Long id) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> sc = cb.createQuery(Long.class);
        Root<LegalDocumentTypeEntity> root = sc.from(LegalDocumentTypeEntity.class);

        if (id != null) {
            sc.select(cb.count(root)).
                    where(
                            cb.equal(root.get(SystemVariables.NAME.getFieldName()), name),
                            cb.equal(root.get(SystemVariables.TENANT_ID.getFieldName()), tenantId),
                            cb.notEqual(root.get(SystemVariables.ID.getFieldName()), id)
                    );
        }
        else {
            sc.select(cb.count(root)).
                    where(
                            cb.equal(root.get(SystemVariables.NAME.getFieldName()), name),
                            cb.equal(root.get(SystemVariables.TENANT_ID.getFieldName()), tenantId)
                    );
        }
        List<Long> count = em.createQuery(sc).getResultList();
        return count.get(0) > 0;
    }
}
