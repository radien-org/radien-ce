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

import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.service.tenant.TenantServiceAccess;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.tenantmanagement.client.exceptions.ErrorCodeMessage;
import io.radien.ms.tenantmanagement.entities.Tenant;
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
 * @author Nuno Santana
 */

@Stateful
public class TenantService implements TenantServiceAccess {

    private static final long serialVersionUID = 1L;

    @Inject
    private EntityManagerHolder emh;

    private static final Logger log = LoggerFactory.getLogger(TenantService.class);

    /**
     * Gets the System Contract searching by the PK (id).
     *
     * @param contractId to be searched.
     * @return the system contract requested to be found.
     */
    @Override
    public SystemTenant get(Long contractId) {
        return emh.getEm().find(Tenant.class, contractId);
    }


    /**
     * Gets all the tenants.
     * Can be filtered by name
     *
     * @param name specific logon or user email
     * @return a List of system contracts.
     */
    @Override
    public List<? extends SystemTenant> get(String name) {
        EntityManager em = emh.getEm();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Tenant> criteriaQuery = criteriaBuilder.createQuery(Tenant.class);
        Root<Tenant> root = criteriaQuery.from(Tenant.class);

        criteriaQuery.select(root);

        Predicate global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        if (name != null) {
            global = criteriaBuilder.and(global,
                    criteriaBuilder.equal(root.get("name"), name));
            criteriaQuery.where(global);
        }

        TypedQuery<Tenant> q = em.createQuery(criteriaQuery);
        return q.getResultList();
    }

    /**
     * Creates the requested and given Contract information into the DB.
     *
     * @param tenant to be added
     * @throws UniquenessConstraintException in case of duplicated email/duplicated logon
     */
    @Override
    public void create(SystemTenant tenant) throws UniquenessConstraintException {
        List<Tenant> alreadyExistentRecords = searchDuplicatedFields(tenant);
        if (alreadyExistentRecords.isEmpty()) {
            emh.getEm().persist(tenant);
        } else {
            throw new UniquenessConstraintException(ErrorCodeMessage.DUPLICATED_FIELD.toString("Name"));
        }
    }

    /**
     * Updates the requested and given Contract information into the DB.
     *
     * @param tenant to be updated
     * @throws UniquenessConstraintException in case of duplicated name
     */
    @Override
    public void update(SystemTenant tenant) throws UniquenessConstraintException {
        List<Tenant> alreadyExistentRecords = searchDuplicatedFields(tenant);
        if (alreadyExistentRecords.isEmpty()) {
            emh.getEm().merge(tenant);
        } else {
            throw new UniquenessConstraintException(ErrorCodeMessage.DUPLICATED_FIELD.toString("Name"));
        }
    }

    /**
     * Query to validate if an existent name already exists in the database or not.
     *
     * @param tenant user information to look up.
     * @return list of users with duplicated information.
     */
    private List<Tenant> searchDuplicatedFields(SystemTenant tenant) {
        EntityManager em = emh.getEm();
        List<Tenant> alreadyExistentRecords;
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Tenant> criteriaQuery = criteriaBuilder.createQuery(Tenant.class);
        Root<Tenant> root = criteriaQuery.from(Tenant.class);
        criteriaQuery.select(root);
        Predicate global = criteriaBuilder.or(
                criteriaBuilder.equal(root.get("name"), tenant.getName()));
        if (tenant.getId() != null) {
            global = criteriaBuilder.and(global, criteriaBuilder.notEqual(root.get("id"), tenant.getId()));
        }
        criteriaQuery.where(global);
        TypedQuery<Tenant> q = em.createQuery(criteriaQuery);
        alreadyExistentRecords = q.getResultList();
        return alreadyExistentRecords;
    }

    /**
     * Deletes a unique contract selected by his id.
     *
     * @param tenantId to be deleted.
     */
    @Override
    public boolean delete(Long tenantId) {
        EntityManager em = emh.getEm();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<Tenant> criteriaDelete = cb.createCriteriaDelete(Tenant.class);
        Root<Tenant> userRoot = criteriaDelete.from(Tenant.class);

        criteriaDelete.where(cb.equal(userRoot.get("id"), tenantId));
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
        CriteriaDelete<Tenant> criteriaDelete = cb.createCriteriaDelete(Tenant.class);
        Root<Tenant> userRoot = criteriaDelete.from(Tenant.class);

        criteriaDelete.where(userRoot.get("id").in(contractIds));
        em.createQuery(criteriaDelete).executeUpdate();
    }
}
