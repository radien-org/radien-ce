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

import io.radien.api.model.user.SystemUser;
import io.radien.api.service.user.UserServiceAccess;
import io.radien.exception.SystemException;
import io.radien.exception.UserNotFoundException;
import io.radien.ms.usermanagement.client.entities.Page;
import io.radien.ms.usermanagement.client.exceptions.ErrorCodeMessage;
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
	public SystemUser get(Long userId) throws UserNotFoundException {
		return em.find(User.class, userId);
	}

	/**
	 * Gets a list of System Users searching by multiple PK's (id) requested in a list.
	 * @param userIds to be searched.
	 * @return the list of system users requested to be found.
	 */
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
	 * Can be filtered by sortBy, ascending and subject.
	 * @param pageNumber of the requested information. Where the user is.
	 * @param pageSize total number of pages returned in the request.
	 * @param sortBy sort filter criteria.
	 * @param isAscending ascending filter criteria.
	 * @param subs subject filter criteria.
	 * @return a page of users.
	 */
	public Page<User> getAll(String search,
							 int pageNumber, int pageSize, List<String> sortBy,
							 Boolean isAscending, Boolean isLogicalConjunction) {
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

//		Predicate global = getFilteredPredicate(search, isLogicalConjunction, criteriaBuilder, userRoot);
//		criteriaQuery.where(global);

		criteriaQuery.where(criteriaBuilder.like(userRoot.get("logon"), search ));

		TypedQuery<User> q=em.createQuery(criteriaQuery);
		q.setFirstResult((pageNumber-1) * pageSize);
		q.setMaxResults(pageSize);

//		int totalResults = Math.toIntExact(getCount(global, userRoot));
//		int totalPages = totalResults%pageSize==0 ? totalResults/pageSize : totalResults/pageSize+1;

		return new Page<User>(q.getResultList(), pageNumber, totalResults, totalPages);
	}

	/**
	 * Creates a filtered predicate to be used when searching for specific users.
	 *
	 * @param subs list of subjects to be searched for
	 * @param emails list of emails to be searched for
	 * @param logons list of logons to be searched for
	 * @param isLogicalConjunction boolean value that if true will make the predicate with AND logic, if false will
	 *                             create predicate wit OR logic
	 * @param criteriaBuilder interface that serves as the main factory of criteria queries and criteria query elements
	 * @param userRoot instance created to define a range variable in the FROM clause
	 * @return a completed predicate of where clause
	 */
	private Predicate getFilteredPredicate(String search, Boolean isLogicalConjunction, CriteriaBuilder criteriaBuilder, Root<User> userRoot) {
		Predicate global;
		//TODO: comment what is that for
		if(isLogicalConjunction) {
			global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));
		} else {
			global = criteriaBuilder.isFalse(criteriaBuilder.literal(true));
		}


//			Predicate subPredicate;
//			if(subs.size()==1){
//				subPredicate = criteriaBuilder.equal(userRoot.get("sub"), subs.get(0));
//			} else {
//				subPredicate = userRoot.get("sub").in(subs);
//			}
//			if(isLogicalConjunction) {
//				global = criteriaBuilder.and(global, subPredicate);
//			} else {
//				global = criteriaBuilder.or(global, subPredicate);
//			}
//
//
//
//			Predicate emailPredicate;
//			if(emails.size()==1){
//				emailPredicate = criteriaBuilder.equal(userRoot.get("userEmail"), emails.get(0));
//			} else {
//				emailPredicate = userRoot.get("userEmail").in(emails);
//			}
//			if(isLogicalConjunction) {
//				global = criteriaBuilder.and(global, emailPredicate);
//			} else {
//				global = criteriaBuilder.or(global, emailPredicate);
//			}
//
//
//
//			Predicate logonPredicate;
//			if(logons.size()==1){
//				logonPredicate = criteriaBuilder.equal(userRoot.get("logon"), logons.get(0));
//			} else {
//				logonPredicate = userRoot.get("logon").in(logons);
//			}
//			if(isLogicalConjunction) {
//				global = criteriaBuilder.and(global, logonPredicate);
//			} else {
//				global = criteriaBuilder.or(global, logonPredicate);
//			}

		return global;
	}

	/**
	 * Count the number of users existent in the DB.
	 * @return the count of users
	 */
	private long getCount(Predicate global, Root<User> userRoot) {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

		criteriaQuery.where(global);

		criteriaQuery.select(criteriaBuilder.count(userRoot));

		TypedQuery<Long> q=em.createQuery(criteriaQuery);
		return q.getSingleResult();
	}

	/**
	 * Saves the requested and given user information into the DB.
	 * @param user to be added/inserted
	 * @throws InvalidRequestException in case of duplicated email/duplicated logon
	 */
	public void save(SystemUser user)  {
//		if(getUserByEmail(user.getUserEmail()).isPresent()) {
//			log.error(ErrorCodeMessage.DUPLICATED_FIELD.toString("Email Address"));
//			throw new InvalidRequestException(ErrorCodeMessage.DUPLICATED_FIELD.toString("Email Address"));
//		}
//		if(getUserByLogon(user.getLogon()).isPresent()) {
//			log.error(ErrorCodeMessage.DUPLICATED_FIELD.toString("Logon"));
//			throw new InvalidRequestException(ErrorCodeMessage.DUPLICATED_FIELD.toString("Logon"));
//		}
//		Optional<String> sub = keycloakService.createUser(user);
//		if(sub.isPresent()){
			user.setLastUpdate(new Date());
			user.setLastUpdateUser(null);

			//TODO: this has to be done somewhere else:
//			user.setSub(sub.get());
			em.persist(user);

//		} else {
//			//TODO: Error handling
//			throw new InvalidRequestException(ErrorCodeMessage.GENERIC_ERROR.toString("Keycloak Integration"));
//		}

	}

	/**
	 * Updates the requested given user with the information given in the new user object information.
	 * @param id of the user to be updated
	 * @param newUserInformation user information to update into the requested one
	 * @throws NotFoundException in case of requested user to be updated cannot be found
	 * @throws InvalidRequestException in case of requested information to be updated already exists in the DB
	 */
	private void update(long id, User newUserInformation) throws UserNotFoundException, InvalidRequestException {
		User dbUser = null;

		dbUser = (User) get(id);
		validateUniquenessRecords(dbUser, newUserInformation);

		dbUser.setFirstname(newUserInformation.getFirstname());
		dbUser.setLastname(newUserInformation.getLastname());
		dbUser.setUserEmail(newUserInformation.getUserEmail());
		dbUser.setLogon(newUserInformation.getLogon());

		em.persist(dbUser);
	}

	/**
	 * When updating the user information this method will validate if the unique values maintain as unique.
	 * Will search for the user email and logon, given in the information to be updated, to see if they are not already in the DB in another user.
	 * @param dbUser to be updated
	 * @param newUserInformation user information to update into the requested one
	 * @throws InvalidRequestException in case of requested information to be updated already exists in the DB
	 */
	private void validateUniquenessRecords(User dbUser, User newUserInformation) throws InvalidRequestException {
		boolean isSameUserEmail = dbUser.getUserEmail().equals(newUserInformation.getUserEmail());
		boolean isSameLogon = dbUser.getLogon().equals(newUserInformation.getLogon());
		Page<User> page;

		if (!isSameUserEmail && !isSameLogon) {
			page = getAll(null, Collections.singletonList(newUserInformation.getUserEmail()), Collections.singletonList(newUserInformation.getLogon()), 1, 2, null, null, false);
		} else if(!isSameUserEmail) {
			page = getAll(null, Collections.singletonList(newUserInformation.getUserEmail()),null, 1, 2, null, null, true);
		} else if(!isSameLogon) {
			page = getAll(null , null ,Collections.singletonList(newUserInformation.getLogon()), 1, 2, null, null, true);
		} else {
			page = new Page<>(null, -1, 0, 0);
		}

		if (page.getTotalResults() == 2) {
			throw new InvalidRequestException(ErrorCodeMessage.DUPLICATED_FIELD.toString("Email address and Logon"));
		} else if(page.getTotalResults() > 0) {
			if(!isSameUserEmail && isSameLogon) {
				throw new InvalidRequestException(ErrorCodeMessage.DUPLICATED_FIELD.toString("Email address"));
			} else if(isSameUserEmail && !isSameLogon) {
				throw new InvalidRequestException(ErrorCodeMessage.DUPLICATED_FIELD.toString("Logon"));
			} else {
				Boolean isEmailDuplicated = newUserInformation.getUserEmail().equals(page.getResults().get(0).getUserEmail());
				Boolean isLogonDuplicated = newUserInformation.getLogon().equals(page.getResults().get(0).getLogon());

				if(isEmailDuplicated && isLogonDuplicated) {
					throw new InvalidRequestException(ErrorCodeMessage.DUPLICATED_FIELD.toString("Email address and Logon"));
				} else if(isEmailDuplicated) {
					throw new InvalidRequestException(ErrorCodeMessage.DUPLICATED_FIELD.toString("Email address"));
				} else if (isLogonDuplicated) {
					throw new InvalidRequestException(ErrorCodeMessage.DUPLICATED_FIELD.toString("Logon"));
				}
			}
		}
	}

	/**
	 * Searches the user by Email.
	 * @param email to be searched.
	 * @return a Optional of System user.
	 */
	private Optional<SystemUser> getUserByEmail(String email) {
		CriteriaBuilder builder = em.getCriteriaBuilder();

		CriteriaQuery<User> query = builder.createQuery(User.class);
		Root<User> root = query.from(User.class);
		CriteriaQuery<User> select = query.select(root);
		select.where(builder.equal(root.get("userEmail"), email));

		TypedQuery<User> typedQuery = em.createQuery(select);

		try {
			return Optional.of(typedQuery.getSingleResult());
		}catch (NoResultException e) {
			return Optional.empty();
		}
	}

	/**
	 * Searches the user by logon.
	 * @param logon to be searched.
	 * @return a Optional of System user.
	 */
	private Optional<SystemUser> getUserByLogon(String logon) {
		CriteriaBuilder builder = em.getCriteriaBuilder();

		CriteriaQuery<User> query = builder.createQuery(User.class);
		Root<User> root = query.from(User.class);
		CriteriaQuery<User> select = query.select(root);
		select.where(builder.equal(root.get("logon"), logon));

		TypedQuery<User> typedQuery = em.createQuery(select);

		try {
			return Optional.of(typedQuery.getSingleResult());
		} catch (NoResultException e) {
			return Optional.empty();
		}
	}

	/**
	 * Deletes a unique user selected by his id.
	 * @param userId to be deleted.
	 */
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
	public void delete(Collection<Long> userIds) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<User> criteriaDelete = cb.createCriteriaDelete(User.class);
		Root<User> userRoot = criteriaDelete.from(User.class);

		criteriaDelete.where(userRoot.get("id").in(userIds));
		em.createQuery(criteriaDelete).executeUpdate();
	}
}
