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
package io.radien.ms.tenantmanagement.service;

import io.radien.api.entity.Page;
import io.radien.api.model.tenant.SystemContract;

import io.radien.api.service.tenant.ContractServiceAccess;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.tenantmanagement.entities.ContractEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import java.util.Collection;
import java.util.List;

/**
 * Contract requests to be performed into the DB and actions to take place
 * @author Nuno Santana
 */
@Stateful
public class ContractService implements ContractServiceAccess {

    private static final long serialVersionUID = 6720615849486414582L;

    @Inject
    private EntityManagerHolder emh;

    private static final Logger log = LoggerFactory.getLogger(ContractService.class);

    /**
     * Gets the System Contract searching by the PK (id).
     *
     * @param contractId to be searched.
     * @return the system contract requested to be found.
     */
    @Override
    public SystemContract get(Long contractId) {
        return emh.getEm().find(ContractEntity.class, contractId);
    }

    /**
     * Gets all the contracts into a pagination mode.
     * @param pageNo of the requested information. Where the user is.
     * @param pageSize total number of pages returned in the request.
     * @return a page of system contracts.
     */
    @Override
    public Page<SystemContract> getAll(int pageNo, int pageSize) {

        log.info("Going to create a new pagination!");
        EntityManager entityManager = emh.getEm();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ContractEntity> criteriaQuery = criteriaBuilder.createQuery(ContractEntity.class);
        Root<ContractEntity> contractRoot = criteriaQuery.from(ContractEntity.class);

        criteriaQuery.select(contractRoot);
        Predicate global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));

        TypedQuery<ContractEntity> q= entityManager.createQuery(criteriaQuery);

        q.setFirstResult((pageNo-1) * pageSize);
        q.setMaxResults(pageSize);

        List<? extends SystemContract> systemContracts= q.getResultList();

        int totalRecords = Math.toIntExact(getCount(global, contractRoot));
        int totalPages = totalRecords%pageSize==0 ? totalRecords/pageSize : totalRecords/pageSize+1;

        log.info("New pagination ready to be showed!");

        return new Page<SystemContract>(systemContracts, pageNo, totalRecords, totalPages);
    }

    /**
     * Count the number of contracts existent in the DB.
     * @return the count of contracts
     */
    private long getCount(Predicate global, Root<ContractEntity> userRoot) {

        log.info("Going to count the existent records.");
        EntityManager em = emh.getEm();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        criteriaQuery.where(global);

        criteriaQuery.select(criteriaBuilder.count(userRoot));

        TypedQuery<Long> q= em.createQuery(criteriaQuery);

        return q.getSingleResult();
    }

    /**
     * Count the number of all the contracts existent in the DB.
     * @return the count of contracts
     */
    @Override
    public long getTotalRecordsCount() {
        CriteriaBuilder criteriaBuilder = emh.getEm().getCriteriaBuilder();
        return getCount(criteriaBuilder.isTrue(criteriaBuilder.literal(true)), criteriaBuilder.createQuery(Long.class).from(ContractEntity.class));
    }

    /**
     * Gets all the contracts.
     * Can be filtered by name
     *
     * @param name specific logon or user email
     * @return a List of system contracts.
     */
    @Override
    public List<? extends SystemContract> get(String name) {
        EntityManager em = emh.getEm();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ContractEntity> criteriaQuery = criteriaBuilder.createQuery(ContractEntity.class);
        Root<ContractEntity> userRoot = criteriaQuery.from(ContractEntity.class);

        criteriaQuery.select(userRoot);

        Predicate global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        if (name != null) {
            global = criteriaBuilder.and(global,
                    criteriaBuilder.equal(userRoot.get("name"), name));
            criteriaQuery.where(global);
        }

        TypedQuery<ContractEntity> q = em.createQuery(criteriaQuery);
        return q.getResultList();
    }

    /**
     * Creates the requested and given Contract information into the DB.
     *
     * @param contract to be added
     * @throws UniquenessConstraintException in case of duplicated email/duplicated logon
     */
    @Override
    public void create(SystemContract contract) throws UniquenessConstraintException {
        List<ContractEntity> alreadyExistentRecords = searchDuplicatedFields(contract);
        if (alreadyExistentRecords.isEmpty()) {
            emh.getEm().persist(contract);
        } else {
            throw new UniquenessConstraintException(GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Name"));
        }
    }

    /**
     * Updates the requested and given Contract information into the DB.
     *
     * @param contract to be updated
     * @throws UniquenessConstraintException in case of duplicated name
     */
    @Override
    public void update(SystemContract contract) throws UniquenessConstraintException {
        List<ContractEntity> alreadyExistentRecords = searchDuplicatedFields(contract);
        if (alreadyExistentRecords.isEmpty()) {
            emh.getEm().merge(contract);
        } else {
            throw new UniquenessConstraintException(GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Name"));
        }
    }

    /**
     * Query to validate if an existent name already exists in the database or not.
     *
     * @param contract user information to look up.
     * @return list of users with duplicated information.
     */
    private List<ContractEntity> searchDuplicatedFields(SystemContract contract) {
        EntityManager em = emh.getEm();
        List<ContractEntity> alreadyExistentRecords;
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<ContractEntity> criteriaQuery = criteriaBuilder.createQuery(ContractEntity.class);
        Root<ContractEntity> root = criteriaQuery.from(ContractEntity.class);
        criteriaQuery.select(root);
        Predicate global = criteriaBuilder.or(
                criteriaBuilder.equal(root.get("name"), contract.getName()));
        if (contract.getId() != null) {
            global = criteriaBuilder.and(global, criteriaBuilder.notEqual(root.get("id"), contract.getId()));
        }
        criteriaQuery.where(global);
        TypedQuery<ContractEntity> q = em.createQuery(criteriaQuery);
        alreadyExistentRecords = q.getResultList();
        return alreadyExistentRecords;
    }

    /**
     * Deletes a unique contract selected by his id.
     *
     * @param contractId to be deleted.
     */
    @Override
    public boolean delete(Long contractId) {
        EntityManager em = emh.getEm();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<ContractEntity> criteriaDelete = cb.createCriteriaDelete(ContractEntity.class);
        Root<ContractEntity> userRoot = criteriaDelete.from(ContractEntity.class);

        criteriaDelete.where(cb.equal(userRoot.get("id"), contractId));
        int ret = em.createQuery(criteriaDelete).executeUpdate();
        return ret > 0;
    }

    /**
     * Deletes a list of contracts selected by his id.
     *
     * @param contractIds to be deleted.
     */
    @Override
    public void delete(Collection<Long> contractIds) {
        EntityManager em = emh.getEm();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<ContractEntity> criteriaDelete = cb.createCriteriaDelete(ContractEntity.class);
        Root<ContractEntity> userRoot = criteriaDelete.from(ContractEntity.class);

        criteriaDelete.where(userRoot.get("id").in(contractIds));
        em.createQuery(criteriaDelete).executeUpdate();
    }

    /**
     * Validates if specific requested Contract exists
     * @param contractId to be searched
     * @return response true if it exists
     */
    @Override
    public boolean exists(Long contractId) {
        EntityManager em = emh.getEm();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<ContractEntity> contractRoot = criteriaQuery.from(ContractEntity.class);

        criteriaQuery.select(criteriaBuilder.count(contractRoot));
        criteriaQuery.where(criteriaBuilder.equal(contractRoot.get("id"), contractId));

        Long size = em.createQuery(criteriaQuery).getSingleResult();

        return size != 0;
    }
}
