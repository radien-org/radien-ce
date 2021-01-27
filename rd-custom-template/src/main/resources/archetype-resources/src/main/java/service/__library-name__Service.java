package ${package}.service;

import ${package}.model.System${library-name};
import ${package}.model.${library-name};

import javax.ejb.Stateful;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

/**
 * @author Rajesh Gavvala
 *
 */

@Stateful
public class ${library-name}Service {
    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "${persistence-unit-name}", type = PersistenceContextType.EXTENDED)
    private EntityManager em;

    public System${library-name} get(Long id)  {
        return em.find(${library-name}.class, id);
    }

    public void create(${library-name} system${library-name})  {
        em.persist(system${library-name});
    }

    public void update(${library-name} system${library-name})  {
        em.merge(system${library-name});
    }

    public void delete(Long id)  {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<${library-name}> criteriaDelete = cb.createCriteriaDelete(${library-name}.class);
        Root<${library-name}> templateRoot = criteriaDelete.from(${library-name}.class);

        criteriaDelete.where(cb.equal(templateRoot.get("id"),id));
        em.createQuery(criteriaDelete).executeUpdate();
    }


}
