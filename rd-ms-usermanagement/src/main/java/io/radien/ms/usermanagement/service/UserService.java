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
package io.radien.ms.usermanagement.service;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.*;
import javax.persistence.criteria.*;

import io.radien.api.entity.Page;
import io.radien.api.model.user.SystemUser;
import io.radien.api.service.user.UserServiceAccess;
import io.radien.exception.UserNotFoundException;
import io.radien.ms.usermanagement.client.exceptions.InvalidRequestException;
import io.radien.ms.usermanagement.client.exceptions.NotFoundException;

import io.radien.ms.usermanagement.entities.User;
import io.radien.ms.usermanagement.legacy.UserFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Nuno Santana
 * @author Bruno Gama
 */

@Stateful
public class UserService implements UserServiceAccess{

	private static final long serialVersionUID = 1L;

	@PersistenceContext(unitName = "userPersistenceUnitLocal", type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	@Inject
	private UserFactory userFactory;

	private static final Logger log = LoggerFactory.getLogger(UserService.class);

	/**
	 * Gets the System User searching by the PK (id).
	 * @param userId to be searched.
	 * @return the system user requested to be found.
	 * @throws NotFoundException if user can not be found will return NotFoundException
	 */
	@Override
	public SystemUser get(Long userId) throws UserNotFoundException {
		return em.find(User.class, userId);
	}

	/**
	 * Gets a list of System Users searching by multiple PK's (id) requested in a list.
	 * @param userIds to be searched.
	 * @return the list of system users requested to be found.
	 */
	@Override
	public List<SystemUser> get(List<Long> userIds) {
		ArrayList<SystemUser> results = new ArrayList<>();
		if(userIds == null || userIds.isEmpty()){
			return results;
		}

		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
		Root<User> userRoot = criteriaQuery.from(User.class);
		criteriaQuery.select(userRoot);
		criteriaQuery.where(userRoot.get("id").in(userIds));

		TypedQuery<User> q=em.createQuery(criteriaQuery);

		return new ArrayList<>(q.getResultList());
	}

	/**
	 * Gets all the users into a pagination mode.
	 * Can be filtered by logon or user email.
	 * @param search specific logon or user email
	 * @param pageNo of the requested information. Where the user is.
	 * @param pageSize total number of pages returned in the request.
	 * @param sortBy sort filter criteria.
	 * @param isAscending ascending filter criteria.
	 * @param isConjunction is search criteria and or or
	 * @return a page of system users.
	 */
	@Override
	public Page<SystemUser> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending, boolean isConjunction) {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
		Root<User> userRoot = criteriaQuery.from(User.class);

		criteriaQuery.select(userRoot);
		if(sortBy != null && !sortBy.isEmpty()){
			List<Order> orders;
			if(isAscending){
				orders = sortBy.stream().map(i->criteriaBuilder.asc(userRoot.get(i))).collect(Collectors.toList());
			} else {
				orders = sortBy.stream().map(i->criteriaBuilder.desc(userRoot.get(i))).collect(Collectors.toList());
			}
			criteriaQuery.orderBy(orders);
		}

		if(isConjunction) {
			criteriaQuery.where(criteriaBuilder.and(criteriaBuilder.like(userRoot.get("logon"), "%" + search + "%"), criteriaBuilder.like(userRoot.get("userEmail"), "%" + search + "%")));
		} else {
			criteriaQuery.where(criteriaBuilder.or(criteriaBuilder.like(userRoot.get("logon"), "%" + search + "%"), criteriaBuilder.like(userRoot.get("userEmail"), "%" + search + "%")));
		}

		TypedQuery<User> q=em.createQuery(criteriaQuery);
		q.setFirstResult((pageNo-1) * pageSize);
		q.setMaxResults(pageSize);

		List<SystemUser> systemUsers = new ArrayList<>();

		for(User u : q.getResultList()) {
			systemUsers.add((SystemUser) u);
		}

		int totalRecords = systemUsers.size();
		int totalPages = totalRecords%pageSize==0 ? totalRecords/pageSize : totalRecords/pageSize+1;

		return new Page<SystemUser>(systemUsers, pageNo, pageSize, totalPages);
	}

	/**
	 * Saves the requested and given user information into the DB.
	 * @param user to be added/inserted
	 * @throws InvalidRequestException in case of duplicated email/duplicated logon
	 */
	//TODO: Error handling
	@Override
	public void save(SystemUser user) {
		user.setLastUpdate(new Date());
		user.setLastUpdateUser(null);

		//TODO: this has to be done somewhere else:
//			user.setSub(sub.get());
		em.persist(user);
	}

	/**
	 * Deletes a unique user selected by his id.
	 * @param userId to be deleted.
	 */
	@Override
	public void delete(Long userId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<User> criteriaDelete = cb.createCriteriaDelete(User.class);
		Root<User> userRoot = criteriaDelete.from(User.class);

		criteriaDelete.where(cb.equal(userRoot.get("id"),userId));
		em.createQuery(criteriaDelete).executeUpdate();
	}

	/**
	 * Deletes a list of users selected by his id.
	 * @param userIds to be deleted.
	 */
	@Override
	public void delete(Collection<Long> userIds) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<User> criteriaDelete = cb.createCriteriaDelete(User.class);
		Root<User> userRoot = criteriaDelete.from(User.class);

		criteriaDelete.where(userRoot.get("id").in(userIds));
		em.createQuery(criteriaDelete).executeUpdate();
	}
}
