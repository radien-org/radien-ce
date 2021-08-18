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
package io.radien.ms.rolemanagement.services;

import io.radien.api.SystemVariables;
import io.radien.ms.rolemanagement.entities.TenantRoleEntity;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Abstract class that allows a common sharing (in therms of methods)
 * between TenantRoleService, TenantRolePermissionService and TenantRoleUserService
 */
public abstract class AbstractTenantRoleDomainService {

    /**
     * Gets all the tenant role ids for the following parameters.
     * @param tenant search param that corresponds to the TenantRole.tenantId (Optional)
     * @param role search param that corresponds to the TenantRole.roleId (Optional)
     * @return a list containing ids
     */
    protected List<Long> getTenantRoleIds(EntityManager em, Long tenant, Long role) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<TenantRoleEntity> root = criteriaQuery.from(TenantRoleEntity.class);

        List<Predicate> predicates = new ArrayList<>();
        if (tenant != null) {
            predicates.add(criteriaBuilder.equal(root.get(SystemVariables.TENANT_ID.getFieldName()), tenant));
        }
        if (role != null) {
            predicates.add(criteriaBuilder.equal(root.get(SystemVariables.ROLE_ID.getFieldName()), role));
        }
        criteriaQuery.select(root.get(SystemVariables.ID.getFieldName())).
                where(predicates.toArray(new Predicate[]{}));
        TypedQuery<Long> typedQuery = em.createQuery(criteriaQuery);
        return typedQuery.getResultList();
    }
}
