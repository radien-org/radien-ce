/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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
package ${package}.ms.service.services;


import java.util.List;
import java.util.stream.Collectors;

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

import ${package}.ms.service.entities.${entityResourceName};
import ${package}.api.entity.Page;
import ${package}.api.model.System${entityResourceName};
import ${package}.api.service.${entityResourceName}ServiceAccess;
import ${package}.exception.${entityResourceName}NotFoundException;



/**
 * @author Nuno Santana
 * @author Bruno Gama
 * @author Marco Weiland
 */
@Stateful
public class ${entityResourceName}Service implements ${entityResourceName}ServiceAccess{
	private static final long serialVersionUID = 6812608123262000069L;
	private static final Logger log = LoggerFactory.getLogger(${entityResourceName}Service.class);

	@PersistenceContext(unitName = "persistenceUnit", type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	/**
	 * Gets the System ${entityResourceName} searching by the PK (id).
	 * @param ${entityResourceName.toLowerCase()}Id to be searched.
	 * @return the system ${entityResourceName.toLowerCase()} requested to be found.
	 * @throws ${entityResourceName}NotFoundException if ${entityResourceName.toLowerCase()} can not be found will return NotFoundException
	 */
	@Override
	public System${entityResourceName} get(Long ${entityResourceName.toLowerCase()}Id) throws ${entityResourceName}NotFoundException {
		System${entityResourceName} result = em.find(${entityResourceName}.class, ${entityResourceName.toLowerCase()}Id);
		if(result == null){
			throw new ${entityResourceName}NotFoundException(${entityResourceName.toLowerCase()}Id.toString());
		}
		return result;
	}

	/**
	 * Gets all the ${entityResourceName.toLowerCase()}s into a pagination mode.
	 * Can be filtered by logon or ${entityResourceName.toLowerCase()} email.
	 * @param search specific logon or ${entityResourceName.toLowerCase()} email
	 * @param pageNo of the requested information. Where the ${entityResourceName.toLowerCase()} is.
	 * @param pageSize total number of pages returned in the request.
	 * @param sortBy sort filter criteria.
	 * @param isAscending ascending filter criteria.
	 * @return a page of system ${entityResourceName.toLowerCase()}s.
	 */
	@Override
	public Page<System${entityResourceName}> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<${entityResourceName}> criteriaQuery = criteriaBuilder.createQuery(${entityResourceName}.class);
		Root<${entityResourceName}> ${entityResourceName.toLowerCase()}Root = criteriaQuery.from(${entityResourceName}.class);

		criteriaQuery.select(${entityResourceName.toLowerCase()}Root);

		Predicate global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));


		if(sortBy != null && !sortBy.isEmpty()){
			List<Order> orders;
			if(isAscending){
				orders = sortBy.stream().map(i->criteriaBuilder.asc(${entityResourceName.toLowerCase()}Root.get(i))).collect(Collectors.toList());
			} else {
				orders = sortBy.stream().map(i->criteriaBuilder.desc(${entityResourceName.toLowerCase()}Root.get(i))).collect(Collectors.toList());
			}
			criteriaQuery.orderBy(orders);
		}

		TypedQuery<${entityResourceName}> q=em.createQuery(criteriaQuery);

		q.setFirstResult((pageNo-1) * pageSize);
		q.setMaxResults(pageSize);

		List<? extends System${entityResourceName}> system${entityResourceName}s = q.getResultList();

		int totalRecords = Math.toIntExact(getCount(global, ${entityResourceName.toLowerCase()}Root));
		int totalPages = totalRecords%pageSize==0 ? totalRecords/pageSize : totalRecords/pageSize+1;

		return new Page<System${entityResourceName}>(system${entityResourceName}s, pageNo, totalRecords, totalPages);
	}

	/**
	 * Count the number of ${entityResourceName.toLowerCase()}s existent in the DB.
	 * @return the count of ${entityResourceName.toLowerCase()}s
	 */
	private long getCount(Predicate global, Root<${entityResourceName}> ${entityResourceName.toLowerCase()}Root) {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

		criteriaQuery.where(global);

		criteriaQuery.select(criteriaBuilder.count(${entityResourceName.toLowerCase()}Root));

		TypedQuery<Long> q=em.createQuery(criteriaQuery);
		return q.getSingleResult();
	}

	/**
	 * Saves or updates the requested and given ${entityResourceName.toLowerCase()} information into the DB.
	 * @param ${entityResourceName.toLowerCase()} to be added/inserted or updated
	 * @throws ${entityResourceName}NotFoundException in case the ${entityResourceName.toLowerCase()} does not exist in the DB, or cannot be found.
	 */
	@Override
	public void save(System${entityResourceName} ${entityResourceName.toLowerCase()}) throws ${entityResourceName}NotFoundException {
		if(${entityResourceName.toLowerCase()}.getId() == null) {
			em.persist(${entityResourceName.toLowerCase()});
		} else {
			em.merge(${entityResourceName.toLowerCase()});
		}
	}

	/**
	 * Deletes a unique ${entityResourceName.toLowerCase()} selected by his id.
	 * @param ${entityResourceName.toLowerCase()}Id to be deleted.
	 */
	@Override
	public void delete(Long ${entityResourceName.toLowerCase()}Id) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<${entityResourceName}> criteriaDelete = cb.createCriteriaDelete(${entityResourceName}.class);
		Root<${entityResourceName}> ${entityResourceName.toLowerCase()}Root = criteriaDelete.from(${entityResourceName}.class);

		criteriaDelete.where(cb.equal(${entityResourceName.toLowerCase()}Root.get("id"),${entityResourceName.toLowerCase()}Id));
		em.createQuery(criteriaDelete).executeUpdate();
	}

}