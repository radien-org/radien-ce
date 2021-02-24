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

package ${package}.service;

import ${package}.entities.Ck;
import io.radien.api.model.${resource-name-variable}.System${resource-name};
import io.radien.api.service.${resource-name-variable}.${resource-name}ServiceAccess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class ${resource-name}Service implements ${resource-name}ServiceAccess {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(${resource-name}Service.class);

    @PersistenceContext(unitName = "${persistence-unit-name}", type = PersistenceContextType.EXTENDED)
    private EntityManager em;

    @Override
    public System${resource-name} get(Long ${resource-name-variable}Id)  {
        return em.find(${resource-name}.class, ${resource-name-variable}Id);
    }

    @Override
    public void create(System${resource-name} ${resource-name-variable})  {
        em.persist(${resource-name-variable});
    }

    @Override
    public void update(System${resource-name} ${resource-name-variable})  {
        em.merge(${resource-name-variable});
    }

    @Override
    public void delete(Long ${resource-name-variable}Id)  {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<${resource-name}> criteriaDelete = cb.createCriteriaDelete(${resource-name}.class);
        Root<${resource-name}> templateRoot = criteriaDelete.from(${resource-name}.class);

        criteriaDelete.where(cb.equal(templateRoot.get("id"),${resource-name-variable}Id));
        em.createQuery(criteriaDelete).executeUpdate();
    }

}
