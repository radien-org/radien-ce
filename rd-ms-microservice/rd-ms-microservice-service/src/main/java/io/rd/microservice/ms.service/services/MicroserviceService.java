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
package io.rd.microservice.ms.service.services;


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

import io.rd.microservice.ms.service.entities.Microservice;
import io.rd.microservice.api.entity.Page;
import io.rd.microservice.api.model.SystemMicroservice;
import io.rd.microservice.api.service.MicroserviceServiceAccess;
import io.rd.microservice.exception.MicroserviceNotFoundException;



/**
 * @author Nuno Santana
 * @author Bruno Gama
 * @author Marco Weiland
 */
@Stateful
public class MicroserviceService implements MicroserviceServiceAccess{
	private static final long serialVersionUID = 6812608123262000069L;
	private static final Logger log = LoggerFactory.getLogger(MicroserviceService.class);

	@PersistenceContext(unitName = "persistenceUnit", type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	/**
	 * Gets the System Microservice searching by the PK (id).
	 * @param microserviceId to be searched.
	 * @return the system microservice requested to be found.
	 * @throws MicroserviceNotFoundException if microservice can not be found will return NotFoundException
	 */
	@Override
	public SystemMicroservice get(Long microserviceId) throws MicroserviceNotFoundException {
		SystemMicroservice result = em.find(Microservice.class, microserviceId);
		if(result == null){
			throw new MicroserviceNotFoundException(microserviceId.toString());
		}
		return result;
	}

	/**
	 * Gets all the microservices into a pagination mode.
	 * Can be filtered by logon or microservice email.
	 * @param search specific logon or microservice email
	 * @param pageNo of the requested information. Where the microservice is.
	 * @param pageSize total number of pages returned in the request.
	 * @param sortBy sort filter criteria.
	 * @param isAscending ascending filter criteria.
	 * @return a page of system microservices.
	 */
	@Override
	public Page<SystemMicroservice> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Microservice> criteriaQuery = criteriaBuilder.createQuery(Microservice.class);
		Root<Microservice> microserviceRoot = criteriaQuery.from(Microservice.class);

		criteriaQuery.select(microserviceRoot);

		Predicate global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));


		if(sortBy != null && !sortBy.isEmpty()){
			List<Order> orders;
			if(isAscending){
				orders = sortBy.stream().map(i->criteriaBuilder.asc(microserviceRoot.get(i))).collect(Collectors.toList());
			} else {
				orders = sortBy.stream().map(i->criteriaBuilder.desc(microserviceRoot.get(i))).collect(Collectors.toList());
			}
			criteriaQuery.orderBy(orders);
		}

		TypedQuery<Microservice> q=em.createQuery(criteriaQuery);

		q.setFirstResult((pageNo-1) * pageSize);
		q.setMaxResults(pageSize);

		List<? extends SystemMicroservice> systemMicroservices = q.getResultList();

		int totalRecords = Math.toIntExact(getCount(global, microserviceRoot));
		int totalPages = totalRecords%pageSize==0 ? totalRecords/pageSize : totalRecords/pageSize+1;

		return new Page<SystemMicroservice>(systemMicroservices, pageNo, totalRecords, totalPages);
	}

	/**
	 * Count the number of microservices existent in the DB.
	 * @return the count of microservices
	 */
	private long getCount(Predicate global, Root<Microservice> microserviceRoot) {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

		criteriaQuery.where(global);

		criteriaQuery.select(criteriaBuilder.count(microserviceRoot));

		TypedQuery<Long> q=em.createQuery(criteriaQuery);
		return q.getSingleResult();
	}

	/**
	 * Saves or updates the requested and given microservice information into the DB.
	 * @param microservice to be added/inserted or updated
	 * @throws MicroserviceNotFoundException in case the microservice does not exist in the DB, or cannot be found.
	 */
	@Override
	public void save(SystemMicroservice microservice) throws MicroserviceNotFoundException {
		if(microservice.getId() == null) {
			em.persist(microservice);
		} else {
			em.merge(microservice);
		}
	}

	/**
	 * Deletes a unique microservice selected by his id.
	 * @param microserviceId to be deleted.
	 */
	@Override
	public void delete(Long microserviceId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<Microservice> criteriaDelete = cb.createCriteriaDelete(Microservice.class);
		Root<Microservice> microserviceRoot = criteriaDelete.from(Microservice.class);

		criteriaDelete.where(cb.equal(microserviceRoot.get("id"),microserviceId));
		em.createQuery(criteriaDelete).executeUpdate();
	}

}