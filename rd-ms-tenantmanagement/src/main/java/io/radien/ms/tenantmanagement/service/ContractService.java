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

import io.radien.api.model.tenant.SystemContract;

import io.radien.api.service.contract.ContractServiceAccess;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.tenantmanagement.client.exceptions.ErrorCodeMessage;
import io.radien.ms.tenantmanagement.entities.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import java.util.Collection;
import java.util.List;

/**
 * @author Nuno Santana
 */

@Stateful
public class ContractService implements ContractServiceAccess {

    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "tenantPersistenceUnit", type = PersistenceContextType.EXTENDED)
    private EntityManager em;

    private static final Logger log = LoggerFactory.getLogger(ContractService.class);

    /**
     * Gets the System Contract searching by the PK (id).
     *
     * @param contractId to be searched.
     * @return the system contract requested to be found.
     */
    @Override
    public SystemContract get(Long contractId) {
        return em.find(Contract.class, contractId);
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
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Contract> criteriaQuery = criteriaBuilder.createQuery(Contract.class);
        Root<Contract> userRoot = criteriaQuery.from(Contract.class);

        criteriaQuery.select(userRoot);

        Predicate global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        if (name != null) {
            global = criteriaBuilder.and(global,
                    criteriaBuilder.equal(userRoot.get("name"), name));
            criteriaQuery.where(global);
        }

        TypedQuery<Contract> q = em.createQuery(criteriaQuery);
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
        List<Contract> alreadyExistentRecords = searchDuplicatedFields(contract);
        if (alreadyExistentRecords.isEmpty()) {
            em.persist(contract);
        } else {
            throw new UniquenessConstraintException(ErrorCodeMessage.DUPLICATED_FIELD.toString("Name"));
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
        List<Contract> alreadyExistentRecords = searchDuplicatedFields(contract);
        if (alreadyExistentRecords.isEmpty()) {
            em.merge(contract);
        } else {
            throw new UniquenessConstraintException(ErrorCodeMessage.DUPLICATED_FIELD.toString("Name"));
        }
    }

    /**
     * Query to validate if an existent name already exists in the database or not.
     *
     * @param contract user information to look up.
     * @return list of users with duplicated information.
     */
    private List<Contract> searchDuplicatedFields(SystemContract contract) {
        List<Contract> alreadyExistentRecords;
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Contract> criteriaQuery = criteriaBuilder.createQuery(Contract.class);
        Root<Contract> root = criteriaQuery.from(Contract.class);
        criteriaQuery.select(root);
        Predicate global = criteriaBuilder.or(
                criteriaBuilder.equal(root.get("name"), contract.getName()));
        if (contract.getId() != null) {
            global = criteriaBuilder.and(global, criteriaBuilder.notEqual(root.get("id"), contract.getId()));
        }
        criteriaQuery.where(global);
        TypedQuery<Contract> q = em.createQuery(criteriaQuery);
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
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<Contract> criteriaDelete = cb.createCriteriaDelete(Contract.class);
        Root<Contract> userRoot = criteriaDelete.from(Contract.class);

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
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<Contract> criteriaDelete = cb.createCriteriaDelete(Contract.class);
        Root<Contract> userRoot = criteriaDelete.from(Contract.class);

        criteriaDelete.where(userRoot.get("id").in(contractIds));
        em.createQuery(criteriaDelete).executeUpdate();
    }
}
