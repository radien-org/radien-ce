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
