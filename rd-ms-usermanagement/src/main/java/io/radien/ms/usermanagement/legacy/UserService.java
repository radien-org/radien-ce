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
package io.radien.ms.usermanagement.legacy;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.*;
import javax.persistence.criteria.*;

import io.radien.api.model.user.SystemUser;

import io.radien.ms.usermanagement.client.entities.Page;
import io.radien.ms.usermanagement.client.exceptions.ErrorCodeMessage;
import io.radien.ms.usermanagement.client.exceptions.InvalidRequestException;
import io.radien.ms.usermanagement.client.exceptions.NotFoundException;

import io.radien.persistence.entities.user.User;
import io.radien.persistence.jpa.EntityManagerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
@Stateful
public class UserService {

	private static final long serialVersionUID = 1L;

	@PersistenceContext(unitName = "persistenceUnit", type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	@Inject
	private UserFactory userFactory;

	private static final Logger log = LoggerFactory.getLogger(UserService.class);


	public SystemUser get(Long userId) throws NotFoundException {
		return em.find(User.class, userId);
	}

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

	public Page<User> getAll(int pageNumber, int pageSize, List<String> sortBy, boolean isAscending) {
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

		TypedQuery<User> q=em.createQuery(criteriaQuery);
		q.setFirstResult((pageNumber-1) * pageSize);
		q.setMaxResults(pageSize);
		int totalResults = Math.toIntExact(getCount());
		int totalPages = totalResults%pageSize==0 ? totalResults/pageSize : totalResults/pageSize+1;

		return new Page<User>(q.getResultList(), pageNumber, totalResults, totalPages);
	}

	private long getCount() {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<User> userRoot = criteriaQuery.from(User.class);

		criteriaQuery.select(criteriaBuilder.count(userRoot));

		TypedQuery<Long> q=em.createQuery(criteriaQuery);
		return q.getSingleResult();
	}

	public Long save(User user) throws InvalidRequestException {
		if(getUserByEmail(user.getUserEmail()).isPresent()) {
			log.error(ErrorCodeMessage.DUPLICATED_EMAIL.toString());
			throw new InvalidRequestException(ErrorCodeMessage.DUPLICATED_EMAIL.toString());
		}

		user.setLastUpdate(new Date());
		return EntityManagerUtil.saveOrUpdate(user, em);
	}

	private Optional<SystemUser> getUserByEmail(String email) {
		CriteriaBuilder builder = em.getCriteriaBuilder();

		CriteriaQuery<User> query = builder.createQuery(User.class);
		Root<User> root = query.from(User.class);
		CriteriaQuery<User> select = query.select(root);
		select.where(builder.equal(root.get("userEmail"), email));

		TypedQuery<User> typedQuery = em.createQuery(select);

		try {
			return Optional.of(typedQuery.getSingleResult());
		} catch (NonUniqueResultException e) {
			throw new NonUniqueResultException("There is more than one user with the same Email");
		} catch (NoResultException e) {
			return Optional.empty();
		}
	}

	public void delete(Long userId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<User> criteriaDelete = cb.createCriteriaDelete(User.class);
		Root<User> userRoot = criteriaDelete.from(User.class);

		criteriaDelete.where(cb.equal(userRoot.get("id"),userId));
		em.createQuery(criteriaDelete).executeUpdate();
	}

	public void delete(Collection<Long> userIds) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<User> criteriaDelete = cb.createCriteriaDelete(User.class);
		Root<User> userRoot = criteriaDelete.from(User.class);

		criteriaDelete.where(userRoot.get("id").in(userIds));
		em.createQuery(criteriaDelete).executeUpdate();
	}
}
