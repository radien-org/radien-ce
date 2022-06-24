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

package io.radien.ms.usermanagement.service.jobfair;

import io.radien.api.SystemVariables;
import io.radien.api.entity.Page;
import io.radien.api.model.jobshop.SystemStudentIdentity;
import io.radien.api.service.jobshop.StudentIdentityServiceAccess;
import io.radien.ms.usermanagement.entities.StudentIdentityEntity;
import java.util.List;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Stateful
public class StudentIdentityService implements StudentIdentityServiceAccess {
    private static final long serialVersionUID = 4433044708638177868L;

    @PersistenceContext(unitName = "persistenceUnit", type = PersistenceContextType.EXTENDED)
    private transient EntityManager em;

    @Override
    public Page<SystemStudentIdentity> getAll(String nameFilter,
                                              int pageNo, int pageSize) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<StudentIdentityEntity> criteriaQuery = criteriaBuilder.createQuery(StudentIdentityEntity.class);
        Root<StudentIdentityEntity> entityRoot = criteriaQuery.from(StudentIdentityEntity.class);
        criteriaQuery.select(entityRoot);

        Predicate global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        if(nameFilter != null) {
             global = criteriaBuilder.and(
                    criteriaBuilder.equal(entityRoot.get(SystemVariables.NAME.getFieldName()), nameFilter)
            );
            criteriaQuery.where(global);
        }

        TypedQuery<StudentIdentityEntity> query = em.createQuery(criteriaQuery);
        query.setFirstResult((pageNo - 1) * pageSize);
        query.setMaxResults(pageSize);

        List<? extends StudentIdentityEntity> resultsList = query.getResultList();
        int totalRecords = Math.toIntExact(getCount(global, entityRoot));
        int totalPages = totalRecords % pageSize == 0 ? totalRecords / pageSize : totalRecords / pageSize + 1;

        return new Page<>(resultsList, pageNo, totalRecords, totalPages);
    }

    @Override
    public void save(SystemStudentIdentity identity) {
        if(identity.getId() != null && em.find(StudentIdentityEntity.class, identity.getId()) != null) {
            em.merge(identity);
            return;
        }
        em.persist(identity);
    }

    private long getCount(Predicate global, Root<StudentIdentityEntity> entityRoot) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        criteriaQuery.where(global);
        criteriaQuery.select(criteriaBuilder.count(entityRoot));
        TypedQuery<Long> q= em.createQuery(criteriaQuery);

        return q.getSingleResult();
    }

}
