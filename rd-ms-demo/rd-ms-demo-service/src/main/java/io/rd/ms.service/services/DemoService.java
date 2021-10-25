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
package io.rd.ms.service.services;


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

import io.rd.ms.service.entities.Demo;
import io.rd.api.entity.Page;
import io.rd.api.model.SystemDemo;
import io.rd.api.service.DemoServiceAccess;
import io.rd.exception.DemoNotFoundException;



/**
 * @author Nuno Santana
 * @author Bruno Gama
 * @author Marco Weiland
 */
@Stateful
public class DemoService implements DemoServiceAccess{
	private static final long serialVersionUID = 6812608123262000069L;
	private static final Logger log = LoggerFactory.getLogger(DemoService.class);

	@PersistenceContext(unitName = "persistenceUnit", type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	/**
	 * Gets the System Demo searching by the PK (id).
	 * @param demoId to be searched.
	 * @return the system demo requested to be found.
	 * @throws DemoNotFoundException if demo can not be found will return NotFoundException
	 */
	@Override
	public SystemDemo get(Long demoId) throws DemoNotFoundException {
		SystemDemo result = em.find(Demo.class, demoId);
		if(result == null){
			throw new DemoNotFoundException(demoId.toString());
		}
		return result;
	}

	/**
	 * Gets all the demos into a pagination mode.
	 * Can be filtered by logon or demo email.
	 * @param search specific logon or demo email
	 * @param pageNo of the requested information. Where the demo is.
	 * @param pageSize total number of pages returned in the request.
	 * @param sortBy sort filter criteria.
	 * @param isAscending ascending filter criteria.
	 * @return a page of system demos.
	 */
	@Override
	public Page<SystemDemo> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Demo> criteriaQuery = criteriaBuilder.createQuery(Demo.class);
		Root<Demo> demoRoot = criteriaQuery.from(Demo.class);

		criteriaQuery.select(demoRoot);

		Predicate global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));


		if(sortBy != null && !sortBy.isEmpty()){
			List<Order> orders;
			if(isAscending){
				orders = sortBy.stream().map(i->criteriaBuilder.asc(demoRoot.get(i))).collect(Collectors.toList());
			} else {
				orders = sortBy.stream().map(i->criteriaBuilder.desc(demoRoot.get(i))).collect(Collectors.toList());
			}
			criteriaQuery.orderBy(orders);
		}

		TypedQuery<Demo> q=em.createQuery(criteriaQuery);

		q.setFirstResult((pageNo-1) * pageSize);
		q.setMaxResults(pageSize);

		List<? extends SystemDemo> systemDemos = q.getResultList();

		int totalRecords = Math.toIntExact(getCount(global, demoRoot));
		int totalPages = totalRecords%pageSize==0 ? totalRecords/pageSize : totalRecords/pageSize+1;

		return new Page<SystemDemo>(systemDemos, pageNo, totalRecords, totalPages);
	}

	/**
	 * Count the number of demos existent in the DB.
	 * @return the count of demos
	 */
	private long getCount(Predicate global, Root<Demo> demoRoot) {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

		criteriaQuery.where(global);

		criteriaQuery.select(criteriaBuilder.count(demoRoot));

		TypedQuery<Long> q=em.createQuery(criteriaQuery);
		return q.getSingleResult();
	}

	/**
	 * Saves or updates the requested and given demo information into the DB.
	 * @param demo to be added/inserted or updated
	 * @throws DemoNotFoundException in case the demo does not exist in the DB, or cannot be found.
	 */
	@Override
	public void save(SystemDemo demo) throws DemoNotFoundException {
		if(demo.getId() == null) {
			em.persist(demo);
		} else {
			em.merge(demo);
		}
	}

	/**
	 * Deletes a unique demo selected by his id.
	 * @param demoId to be deleted.
	 */
	@Override
	public void delete(Long demoId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<Demo> criteriaDelete = cb.createCriteriaDelete(Demo.class);
		Root<Demo> demoRoot = criteriaDelete.from(Demo.class);

		criteriaDelete.where(cb.equal(demoRoot.get("id"),demoId));
		em.createQuery(criteriaDelete).executeUpdate();
	}

}