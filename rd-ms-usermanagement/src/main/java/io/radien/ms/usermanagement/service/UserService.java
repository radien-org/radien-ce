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
import io.radien.api.model.user.SystemUserSearchFilter;
import io.radien.api.service.user.UserServiceAccess;
import io.radien.exception.UniquenessConstraintException;
import io.radien.exception.UserNotFoundException;
import io.radien.ms.usermanagement.client.exceptions.ErrorCodeMessage;
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
	 * @return a page of system users.
	 */
	@Override
	public Page<SystemUser> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
		Root<User> userRoot = criteriaQuery.from(User.class);

		criteriaQuery.select(userRoot);

		Predicate global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));
		if(search!= null) {
			global = criteriaBuilder.and(criteriaBuilder.or(criteriaBuilder.like(userRoot.get("logon"), search), criteriaBuilder.like(userRoot.get("userEmail"), search)));
			criteriaQuery.where(global);
		}

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

		q.setFirstResult((pageNo-1) * pageSize);
		q.setMaxResults(pageSize);

		List<? extends SystemUser> systemUsers = q.getResultList();

		int totalRecords = Math.toIntExact(getCount(global, userRoot));
		int totalPages = totalRecords%pageSize==0 ? totalRecords/pageSize : totalRecords/pageSize+1;

		return new Page<SystemUser>(systemUsers, pageNo, totalRecords, totalPages);
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
	 * Saves or updates the requested and given user information into the DB.
	 * @param user to be added/inserted or updated
	 * @throws UserNotFoundException in case the user does not exist in the DB, or cannot be found.
	 * @throws UniquenessConstraintException in case of duplicated email/duplicated logon
	 */
	@Override
	public void save(SystemUser user) throws UserNotFoundException, UniquenessConstraintException {
		List<User> alreadyExistentRecords = searchDuplicatedEmailOrLogon(user);

		if(user.getId() == null) {
			if(alreadyExistentRecords.isEmpty()) {
				em.persist(user);
			} else {
				validateUniquenessRecords(alreadyExistentRecords, user);
			}
		} else {
			validateUniquenessRecords(alreadyExistentRecords, user);

			em.merge(user);
		}
	}

	/**
	 * Query to validate if an existent email address or logon already exists in the database or not.
	 * @param user user information to look up.
	 * @return list of users with duplicated information.
	 */
	//TODO: validate the subject also
	private List<User> searchDuplicatedEmailOrLogon(SystemUser user) {
		List<User> alreadyExistentRecords;
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
		Root<User> userRoot = criteriaQuery.from(User.class);
		criteriaQuery.select(userRoot);
		Predicate global = criteriaBuilder.or(
				criteriaBuilder.equal(userRoot.get("logon"), user.getLogon()),
				criteriaBuilder.equal(userRoot.get("userEmail"), user.getUserEmail()));
		if(user.getId()!= null) {
			global=criteriaBuilder.and(global, criteriaBuilder.notEqual(userRoot.get("id"), user.getId()));
		}
		criteriaQuery.where(global);
		TypedQuery<User> q = em.createQuery(criteriaQuery);
		alreadyExistentRecords = q.getResultList();
		return alreadyExistentRecords;
	}

	/**
	 * When updating the user information this method will validate if the unique values maintain as unique.
	 * Will search for the user email and logon, given in the information to be updated, to see if they are not already in the DB in another user.
	 * @param alreadyExistentRecords list of duplicated user information
	 * @param newUserInformation user information to update into the requested one
	 * @throws UniquenessConstraintException in case of requested information to be updated already exists in the DB
	 */
	private void validateUniquenessRecords(List<User> alreadyExistentRecords, SystemUser newUserInformation) throws UniquenessConstraintException {
		if (alreadyExistentRecords.size() == 2) {
			throw new UniquenessConstraintException(ErrorCodeMessage.DUPLICATED_FIELD.toString("Email Address and Logon"));
		} else if(!alreadyExistentRecords.isEmpty()) {
			boolean isSameUserEmail = alreadyExistentRecords.get(0).getUserEmail().equals(newUserInformation.getUserEmail());
			boolean isSameLogon = alreadyExistentRecords.get(0).getLogon().equals(newUserInformation.getLogon());

			if(!isSameUserEmail && isSameLogon) {
				throw new UniquenessConstraintException(ErrorCodeMessage.DUPLICATED_FIELD.toString("Logon"));
			} else if(isSameUserEmail && !isSameLogon) {
				throw new UniquenessConstraintException(ErrorCodeMessage.DUPLICATED_FIELD.toString("Email Address"));
			} else if(isSameUserEmail && isSameLogon) {
				throw new UniquenessConstraintException(ErrorCodeMessage.DUPLICATED_FIELD.toString("Email Address and Logon"));
			}
		}
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

	/**
	 * Get UsersBy unique columns
	 * @param filter entity with available filters to search user
	 */
	@Override
	public List<? extends SystemUser> getUsersBy(SystemUserSearchFilter filter) {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
		Root<User> userRoot = criteriaQuery.from(User.class);

		criteriaQuery.select(userRoot);

		Predicate global = getFilteredPredicate(filter, criteriaBuilder, userRoot);

		criteriaQuery.where(global);
		TypedQuery<User> q=em.createQuery(criteriaQuery);

		return q.getResultList();
	}

	private Predicate getFilteredPredicate(SystemUserSearchFilter filter, CriteriaBuilder criteriaBuilder, Root<User> userRoot) {
		Predicate global;

		// is LogicalConjunction represents if you join the fields on the predicates with "or" or "and"
		// the predicate is build with the logic (start,operator,newPredicate)
		// where start represents the already joined predicates
		// operator is "and" or "or"
		// depending on the operator the start may need to be true or false
		// true and predicate1 and predicate2
		// false or predicate1 or predicate2
		if(filter.isLogicConjunction()) {
			global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));
		} else {
			global = criteriaBuilder.isFalse(criteriaBuilder.literal(true));
		}

		global = getFieldPredicate("sub", filter.getSub(), filter, criteriaBuilder, userRoot, global);
		global = getFieldPredicate("userEmail", filter.getEmail(), filter, criteriaBuilder, userRoot, global);
		global = getFieldPredicate("logon", filter.getLogon(), filter, criteriaBuilder, userRoot, global);

		return global;
	}

	private Predicate getFieldPredicate(String name, String value, SystemUserSearchFilter filter, CriteriaBuilder criteriaBuilder, Root<User> userRoot, Predicate global) {
		if(value != null) {
			Predicate subPredicate;
			if (filter.isExact()) {
				subPredicate = criteriaBuilder.equal(userRoot.get(name), value);
			} else {
				subPredicate = criteriaBuilder.like(userRoot.get(name),"%"+value+"%");
			}

			if(filter.isLogicConjunction()) {
				global = criteriaBuilder.and(global, subPredicate);
			} else {
				global = criteriaBuilder.or(global, subPredicate);
			}
		}
		return global;
	}
}
