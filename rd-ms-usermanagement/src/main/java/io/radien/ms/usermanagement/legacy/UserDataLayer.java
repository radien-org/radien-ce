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

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.*;
import javax.persistence.criteria.*;

import io.radien.api.model.user.SystemUser;
import io.radien.api.service.user.UserDataAccessLayer;
import io.radien.exception.UserNotFoundException;
import io.radien.persistence.entities.identity.Identity;
import io.radien.persistence.entities.user.User;
import io.radien.persistence.jpa.EntityManagerUtil;

import java.time.LocalDate;
import java.util.*;

/**
 * The default implementation of the {@link UserDataAccessLayer} interface for
 * oaf applications
 *
 * @author Marco Weiland
 * @author José Rodrigues
 * @author Rafael Fernandes
 * @author João Alves Coelho
 */
@RequestScoped
public class UserDataLayer implements UserDataAccessLayer {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;

	@Inject
	private UserFactory userFactory;

	@Override
	public SystemUser getUserByLoginId(String loginId) throws UserNotFoundException {
		Optional<SystemUser> user = Optional.empty();
		try {
			user = getUserByLogon(loginId);
		} catch (Exception e) {
			throw new UserNotFoundException("User not found using method logon: " + loginId);
		}

		if ( !user.isPresent()) {
			try {
				user = getUserByEmail(loginId);
			} catch (Exception e) {
				throw new UserNotFoundException("User not found using method email: " + loginId);
			}
			if ( !user.isPresent()) {
				throw new UserNotFoundException("The user with loginId " + loginId + " cannot be found");
			}
		}
		return user.get();
	}

	@Override
	public Optional<SystemUser> getUserByLogon(String logon) {
		CriteriaBuilder builder = em.getCriteriaBuilder();

		CriteriaQuery<User> query = builder.createQuery(User.class);
		Root<User> root = query.from(User.class);
		CriteriaQuery<User> select = query.select(root);

		select.where(builder.equal(root.get("logon"), logon));

		TypedQuery<User> typedQuery = em.createQuery(select);

		try {
			return Optional.of(typedQuery.getSingleResult());
		} catch (NonUniqueResultException e) {
			throw new NonUniqueResultException("There is more than one user with the same logon");
		} catch (NoResultException e) {
			return Optional.empty();
		}
	}

	@Override
	public Optional<SystemUser> getUserByEmail(String email) {
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

	@Override
	public SystemUser getUserByLogonOrEmail(String logonOrEmail) throws UserNotFoundException {
		CriteriaBuilder builder = em.getCriteriaBuilder();

		CriteriaQuery<User> query = builder.createQuery(User.class);
		Root<User> root = query.from(User.class);
		CriteriaQuery<User> select = query.select(root);
		Predicate logon = builder.equal(root.get("logon"), logonOrEmail);
		Predicate email = builder.equal(root.get("userEmail"), logonOrEmail);
		Predicate user = builder.or(logon, email);
		select.where(user);

		TypedQuery<User> typedQuery = em.createQuery(select);

		try {
			return typedQuery.getSingleResult();
		} catch (NonUniqueResultException e) {
			throw new NonUniqueResultException("There is more than one user with the same Logon or Email");
		} catch (NoResultException e) {
			throw new UserNotFoundException("There is no user with the Logon or Email " + logonOrEmail);
		}
	}

	@Override
	public SystemUser getUserByEmailAndEnabled(String email, boolean enabled) throws UserNotFoundException {
		CriteriaBuilder builder = em.getCriteriaBuilder();

		CriteriaQuery<User> query = builder.createQuery(User.class);
		Root<User> root = query.from(User.class);
		CriteriaQuery<User> select = query.select(root);
		Predicate emailPredicate = builder.equal(root.get("userEmail"), email);
		Predicate enabledPredicate = builder.equal(root.get("enabled"), enabled);
		select.where(builder.and(emailPredicate, enabledPredicate));

		TypedQuery<User> typedQuery = em.createQuery(select);

		try {
			return typedQuery.getSingleResult();
		} catch (NonUniqueResultException e) {
			throw new NonUniqueResultException("There is more than one user with the same Email");
		} catch (NoResultException e) {
			throw new UserNotFoundException("There is no user with the Email " + email + " and Enabled " + enabled);
		}
	}

	@Override
	public void save(SystemUser systemUser) {
		User user = userFactory.convert(systemUser);
		user.setLastUpdate(new Date());
		EntityManagerUtil.saveOrUpdate(user, em);
	}

	@Override
	public void delete(SystemUser systemUser) {
		User user = userFactory.convert(systemUser);
		EntityManagerUtil.delete(user, em);
	}

	@Override
	public void delete(Collection<SystemUser> user) {
		EntityTransaction transaction = em.getTransaction();
		if(!transaction.isActive()) {
			transaction.begin();
		}
		try {
			for(SystemUser usr:user) {
				User entity =userFactory.convert(usr);

				if (entity != null && entity.getId() == null) {
					continue;
				}
				if (!em.contains(entity)) {
					entity = em.merge(entity);
				}
				em.remove(entity);
			}
		} catch (Exception e) {
			if (transaction.isActive()) {
				transaction.rollback();
			}
			throw e;
		}
	}

	@Override
	public SystemUser get(Long id) {
		return em.find(User.class, id);
	}

	@Override
	public List<SystemUser> get(List<Long> ids) {
		return getUserListById(ids);
	}

	@Override
	public List<SystemUser> getAll() {

		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
		Root<User> userRoot = criteriaQuery.from(User.class);
		criteriaQuery.select(userRoot);

		TypedQuery<User> q=em.createQuery(criteriaQuery);

		return new ArrayList<>(q.getResultList());
	}

	@Override
	public List<SystemUser> get(int pageSize, int pageNumber, String query) {
		if (pageNumber == 0) {
			pageNumber = 1;
		}
		if (pageSize == 0) {
			pageSize = 10;
		}
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

		CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
		Root<User> from = criteriaQuery.from(User.class);
		CriteriaQuery<User> select = criteriaQuery.select(from);

		TypedQuery<User> typedQuery = em.createQuery(select);
		typedQuery.setFirstResult((pageNumber - 1) * pageSize);
		typedQuery.setMaxResults(pageSize);

		return new ArrayList<>(typedQuery.getResultList());
	}

	@Override
	public List<SystemUser> getUsers(boolean registered, Long days) {
		CriteriaBuilder builder = em.getCriteriaBuilder();

		CriteriaQuery<User> criteriaQuery = builder.createQuery(User.class);
		Root<User> root = criteriaQuery.from(User.class);
		CriteriaQuery<User> select = criteriaQuery.select(root);
		Predicate p = builder.equal(root.get("enabled"), registered);

		if(days != null){
			LocalDate daysAgo = LocalDate.now().minusDays(days);
			Join<User, Identity> identityJoin = root.join("identityId", JoinType.INNER);
			Predicate datePredicate = builder.or(
					builder.lessThanOrEqualTo(root.get("registerDate"),java.sql.Date.valueOf(daysAgo)),
					builder.lessThanOrEqualTo(identityJoin.get("createDate"),java.sql.Date.valueOf(daysAgo))
			);
			p = builder.and(p,datePredicate);
		}

		TypedQuery<User> typedQuery = em.createQuery(select.where(p));

		return new ArrayList<>(typedQuery.getResultList());
	}

	@Override
	public List<SystemUser> getUsersById(Collection<Long> ids) {
		return getUserListById(ids);
	}

	private List<SystemUser> getUserListById(Collection <Long> ids) {
		ArrayList<SystemUser> results = new ArrayList<>();
		if(ids == null || ids.isEmpty()){
			return results;
		}

		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
		Root<User> userRoot = criteriaQuery.from(User.class);
		criteriaQuery.select(userRoot);
		criteriaQuery.where(userRoot.get("id").in(ids));

		TypedQuery<User> q=em.createQuery(criteriaQuery);

		return new ArrayList<>(q.getResultList());
	}
}
