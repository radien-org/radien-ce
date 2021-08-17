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

import io.radien.exception.UniquenessConstraintException;
import io.radien.exception.UserNotFoundException;
import io.radien.exception.GenericErrorCodeMessage;

import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.radien.api.SystemVariables;
import io.radien.api.entity.Page;
import io.radien.api.model.user.SystemUser;
import io.radien.api.model.user.SystemUserSearchFilter;
import io.radien.api.service.batch.BatchSummary;
import io.radien.api.service.batch.DataIssue;
import io.radien.api.service.user.UserServiceAccess;
import io.radien.api.util.ModelServiceUtil;

import io.radien.ms.usermanagement.entities.UserEntity;
import io.radien.ms.usermanagement.client.entities.UserSearchFilter;

import javax.ejb.Stateful;
/**
 * User management requests and accesses into the db to gather information or validating
 *
 * @author Nuno Santana
 * @author Bruno Gama
 * @author Marco Weiland
 */
@Stateful
public class UserService extends ModelServiceUtil implements UserServiceAccess {
	private static final long serialVersionUID = -8258219044233966135L;
	private static final Logger log = LoggerFactory.getLogger(UserService.class);


	@PersistenceContext(unitName = "persistenceUnit", type = PersistenceContextType.EXTENDED)
	private transient EntityManager em;

	/**
	 * Requests the user id based on the received user subject
	 * @param userSub to be found
	 * @return the user id
	 */
	@Override
	public Long getUserId(String userSub) {
		if (userSub == null || userSub.trim().length() == 0) {
			throw new IllegalArgumentException(GenericErrorCodeMessage.USER_FIELD_MANDATORY.toString("user subject"));
		}
		String query = "Select u.id From UserEntity u where u.sub = :pUserSub";
		TypedQuery<Long> typedQuery = em.createQuery(query, Long.class);
		typedQuery.setParameter("pUserSub", userSub);
		try {
			return typedQuery.getSingleResult();
		}
		catch (NoResultException e) {
			log.error("No user (id) found for sub {}", userSub);
			return null;
		}
	}

	/**
	 * Gets the System User searching by the PK (id).
	 * @param userId to be searched.
	 * @return the system user requested to be found.
	 * @throws UserNotFoundException if user can not be found will return NotFoundException
	 */
	@Override
	public SystemUser get(Long userId) throws UserNotFoundException {
		UserEntity result = em.find( UserEntity.class, userId);
		if(result == null){
			throw new UserNotFoundException(userId.toString());
		}
		return result;
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
		CriteriaQuery<UserEntity> criteriaQuery = criteriaBuilder.createQuery(UserEntity.class);
		Root<UserEntity> userRoot = criteriaQuery.from(UserEntity.class);
		criteriaQuery.select(userRoot);
		criteriaQuery.where(userRoot.get(SystemVariables.ID.getFieldName()).in(userIds));

		TypedQuery<UserEntity> q=em.createQuery(criteriaQuery);

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
		CriteriaQuery<UserEntity> criteriaQuery = criteriaBuilder.createQuery( UserEntity.class);
		Root<UserEntity> userRoot = criteriaQuery.from(UserEntity.class);

		criteriaQuery.select(userRoot);

		Predicate global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));
		if(search!= null) {
			global = criteriaBuilder.and(criteriaBuilder.or(criteriaBuilder.like(
					userRoot.get(SystemVariables.LOGON.getFieldName()), search),
					criteriaBuilder.like(userRoot.get(SystemVariables.USER_EMAIL.getFieldName()), search)));
			criteriaQuery.where(global);
		}

		if(sortBy != null && !sortBy.isEmpty()){
			List<Order> orders = getListOrderSortBy(isAscending, userRoot, criteriaBuilder, sortBy);
			criteriaQuery.orderBy(orders);
		}

		TypedQuery<UserEntity> q=em.createQuery(criteriaQuery);

		q.setFirstResult((pageNo-1) * pageSize);
		q.setMaxResults(pageSize);

		List<? extends SystemUser> systemUsers = q.getResultList();

		int totalRecords = Math.toIntExact(getCount(global, userRoot));
		int totalPages = totalRecords%pageSize==0 ? totalRecords/pageSize : totalRecords/pageSize+1;

		return new Page<>(systemUsers, pageNo, totalRecords, totalPages);
	}

	/**
	 * Count the number of users existent in the DB.
	 * @return the count of users
	 */
	private long getCount(Predicate global, Root<UserEntity> userRoot) {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		return getCountFromModelService(criteriaBuilder, global, userRoot, em);
	}

	/**
	 * Saves or updates the requested and given user information into the DB.
	 * @param user to be added/inserted or updated
	 * @throws UserNotFoundException in case the user does not exist in the DB, or cannot be found.
	 * @throws UniquenessConstraintException in case of duplicated email/duplicated logon
	 */
	@Override
	public void save(SystemUser user) throws UserNotFoundException, UniquenessConstraintException {
		List<UserEntity> alreadyExistentRecords = searchDuplicatedEmailOrLogon(user);

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
	private List<UserEntity> searchDuplicatedEmailOrLogon(SystemUser user) {
		List<UserEntity> alreadyExistentRecords;
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<UserEntity> criteriaQuery = criteriaBuilder.createQuery(UserEntity.class);
		Root<UserEntity> userRoot = criteriaQuery.from(UserEntity.class);
		criteriaQuery.select(userRoot);
		Predicate global = criteriaBuilder.or(
				criteriaBuilder.equal(userRoot.get(SystemVariables.LOGON.getFieldName()), user.getLogon()),
				criteriaBuilder.equal(userRoot.get(SystemVariables.USER_EMAIL.getFieldName()), user.getUserEmail()));
		if(user.getId()!= null) {
			global=criteriaBuilder.and(global, criteriaBuilder.notEqual(userRoot.get(SystemVariables.ID.getFieldName()), user.getId()));
		}
		criteriaQuery.where(global);
		TypedQuery<UserEntity> q = em.createQuery(criteriaQuery);
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
	private void validateUniquenessRecords(List<UserEntity> alreadyExistentRecords, SystemUser newUserInformation) throws UniquenessConstraintException {
		if (alreadyExistentRecords.size() == 2) {
			throw new UniquenessConstraintException(GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Email Address and Logon"));
		} else if(!alreadyExistentRecords.isEmpty()) {
			SystemUser alreadyExistentRecord = alreadyExistentRecords.get(0);
			boolean isSameUserEmail = alreadyExistentRecord.getUserEmail().equals(newUserInformation.getUserEmail());
			boolean isSameLogon = alreadyExistentRecord.getLogon().equals(newUserInformation.getLogon());


			if(isSameUserEmail && isSameLogon) {
				throw new UniquenessConstraintException(GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Email Address and Logon"));
			} else if(isSameUserEmail) {
				throw new UniquenessConstraintException(GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Email Address"));
			} else if(isSameLogon) {
				throw new UniquenessConstraintException(GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Logon"));
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
		CriteriaDelete<UserEntity> criteriaDelete = cb.createCriteriaDelete( UserEntity.class);
		Root<UserEntity> userRoot = criteriaDelete.from( UserEntity.class);

		criteriaDelete.where(cb.equal(userRoot.get(SystemVariables.ID.getFieldName()),userId));
		em.createQuery(criteriaDelete).executeUpdate();
	}

	/**
	 * Deletes a list of users selected by his id.
	 * @param userIds to be deleted.
	 */
	@Override
	public void delete(Collection<Long> userIds) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<UserEntity> criteriaDelete = cb.createCriteriaDelete( UserEntity.class);
		Root<UserEntity> userRoot = criteriaDelete.from( UserEntity.class);

		criteriaDelete.where(userRoot.get(SystemVariables.ID.getFieldName()).in(userIds));
		em.createQuery(criteriaDelete).executeUpdate();
	}

	/**
	 * Get UsersBy unique columns
	 * @param filter entity with available filters to search user
	 */
	@Override
	public List<? extends SystemUser> getUsers(SystemUserSearchFilter filter) {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<UserEntity> criteriaQuery = criteriaBuilder.createQuery( UserEntity.class);
		Root<UserEntity> userRoot = criteriaQuery.from(UserEntity.class);

		criteriaQuery.select(userRoot);

		Predicate global = getFilteredPredicate((UserSearchFilter) filter, criteriaBuilder, userRoot);

		criteriaQuery.where(global);
		TypedQuery<UserEntity> q=em.createQuery(criteriaQuery);

		return q.getResultList();
	}

	/**
	 * Gets all the users from the db into a list
	 * @return a list of all the existent users
	 */
	@Override
	public List<? extends SystemUser> getUserList() {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<UserEntity> criteriaQuery = criteriaBuilder.createQuery( UserEntity.class);
		Root<UserEntity> userRoot = criteriaQuery.from( UserEntity.class);
		criteriaQuery.select(userRoot);
		TypedQuery<UserEntity> q=em.createQuery(criteriaQuery);
		return q.getResultList();
	}

	/**
	 * Will filter all the fields given in the criteria builder and in the filter and create the
	 * where clause for the query
	 * @param filter fields to be searched for
	 * @param criteriaBuilder database query builder
	 * @param userRoot database table to search the information
	 * @return a constructed predicate with the fields needed to be search
	 */
	private Predicate getFilteredPredicate(UserSearchFilter filter, CriteriaBuilder criteriaBuilder, Root<UserEntity> userRoot) {
		Predicate global;

		boolean isFilterIds = filter.getIds() != null && !filter.getIds().isEmpty();
		List<Long>  filterIds = (List<Long>) filter.getIds();
		global = getFilteredPredicateFromModelService(isFilterIds, filterIds, filter.isLogicConjunction(), criteriaBuilder,userRoot);

		global = getFieldPredicate(SystemVariables.SUB.getFieldName(), filter.getSub(), filter, criteriaBuilder, userRoot, global);
		global = getFieldPredicate(SystemVariables.USER_EMAIL.getFieldName(), filter.getEmail(), filter, criteriaBuilder, userRoot, global);
		global = getFieldPredicate(SystemVariables.LOGON.getFieldName(), filter.getLogon(), filter, criteriaBuilder, userRoot, global);

		return global;
	}

	/**
	 * Method that will create in the database query where clause each and single search
	 * @param name of the field to be search in the query
	 * @param value of the field to be search or compared in the query
	 * @param filter complete requested filter for further validations
	 * @param criteriaBuilder database query builder
	 * @param userRoot database table to search the information
	 * @param global complete where clause to be merged into the constructed information
	 * @return a constructed predicate with the fields needed to be search
	 */
	private Predicate getFieldPredicate(String name, String value, UserSearchFilter filter, CriteriaBuilder criteriaBuilder, Root<UserEntity> userRoot, Predicate global) {
		if(value != null) {
			return getFieldPredicateFromModelService(value, filter.isExact(), filter.isLogicConjunction(), criteriaBuilder, userRoot.get(name), global);
		}
		return global;
	}

	/**
	 * Batch creation method, will delete all the received users from the db
	 * @param users list of users to be deleted
	 * @return a batch summary with a report saying which records have been or not been deleted
	 */
	@Override
	public BatchSummary create(List<? extends SystemUser> users) {
		BatchSummary batchSummary = new BatchSummary(users.size());

		// Retrieve all possible issues
		Map<Integer, DataIssue> issues = retrieveIssues(users);

		// Filter elements ready for insertion
		List<? extends SystemUser> insertion = IntStream.range(0, users.size()).
				filter(index -> !issues.containsKey(index)).
				mapToObj(users::get).collect(Collectors.toList());

		for (SystemUser u: insertion) {
			em.persist(u);
		}
		batchSummary.addNonProcessedItems(issues.values());

		return batchSummary;
	}

	/**
	 * Translates a given list of system users that have been tried to be inserted via batch mode
	 * and will understand individually which one what was the issue for not being inserted in the batch
	 * insertion
	 * @param insertionUsers that have been tried to be created and were not possible
	 * @return a map composed of the user id and the issue faced
	 */
	private Map<Integer, DataIssue> retrieveIssues(
			List<? extends SystemUser> insertionUsers) {

		Map<Integer, DataIssue> issuesByRow = new HashMap<>();

		Set<String> logonsAsParameter = new HashSet<>();
		Set<String> emailsAsParameter = new HashSet<>();
		Set<String> subsAsParameter = new HashSet<>();

		// Searching for repeated elements and gathering params for Query
		for (int index=0; index<insertionUsers.size(); index++) {
			SystemUser u = insertionUsers.get(index);
			populateParametersAndFindIssues(SystemVariables.LOGON.getFieldName(), u.getLogon(), index, issuesByRow, logonsAsParameter);
			populateParametersAndFindIssues(SystemVariables.SUB.getFieldName(), u.getSub(), index, issuesByRow, subsAsParameter);
			populateParametersAndFindIssues(SystemVariables.USER_EMAIL.getFieldName(), u.getUserEmail(), index, issuesByRow, emailsAsParameter);
		}

		// Search for already inserted elements (sub, userEmail and logon) on the database
		Set<String> emailsAlreadyInserted = new HashSet<>(
				retrieveDataFromDB(SystemVariables.USER_EMAIL.getFieldName(), emailsAsParameter));
		Set<String> logonsAlreadyInserted = new HashSet<>(
				retrieveDataFromDB(SystemVariables.LOGON.getFieldName(), logonsAsParameter));
		Set<String> subsAlreadyInserted = new HashSet<>(
				retrieveDataFromDB(SystemVariables.SUB.getFieldName(), subsAsParameter));

		for (int index=0; index<insertionUsers.size(); index++) {
			SystemUser u = insertionUsers.get(index);
			seekForIssue(SystemVariables.LOGON.getFieldName(), u.getLogon(), index, issuesByRow, logonsAlreadyInserted);
			seekForIssue(SystemVariables.USER_EMAIL.getFieldName(),  u.getUserEmail(), index, issuesByRow, emailsAlreadyInserted);
			seekForIssue(SystemVariables.SUB.getFieldName(),  u.getSub(), index, issuesByRow, subsAlreadyInserted);
		}

		return issuesByRow;
	}

	/**
	 * Method to populate new found issues to be validated and/or filtered in the future
	 * @param field to be searched
	 * @param value to be searched
	 * @param index of the hashmap of issues
	 * @param issuesByRow hashmap with all the issue information
	 * @param parameterSet areas to be set
	 */
	private void populateParametersAndFindIssues(String field,
												 String value,
												 int index,
												 Map<Integer, DataIssue> issuesByRow,
												 Set<String> parameterSet) {

		if (!parameterSet.add(value)){
			addNewFoundIssue(index, issuesByRow,
					GenericErrorCodeMessage.DUPLICATED_FIELD.toString(field));
		}
	}

	/**
	 * Method that will try to find specific issue and specific description in the list
	 * @param field to be searched
	 * @param value to be searched
	 * @param index of the hashmap of issues
	 * @param issuesByRow hashmap with all the issue information
	 * @param searchArea possible multiple areas to be search
	 */
	private void seekForIssue(String field,
							  String value,
							  int index,
							  Map<Integer, DataIssue> issuesByRow,
							  Set<String> searchArea) {

		if (searchArea.contains(value)){
			addNewFoundIssue(index, issuesByRow,
					GenericErrorCodeMessage.DUPLICATED_FIELD.toString(field));
		}
	}

	/**
	 * Retrieval method from a given specific field from multiple rows
	 * @param property to be retrieved
	 * @param parameters to be compared
	 * @return list of all the found values
	 */
	private List<String> retrieveDataFromDB(String property, Collection<String> parameters) {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);
		Root<UserEntity> userRoot = criteriaQuery.from( UserEntity.class);

		criteriaQuery.select(userRoot.get(property)).where(userRoot.get(property).in(parameters));
		TypedQuery<String> typedQuery = em.createQuery(criteriaQuery);
		return typedQuery.getResultList();
	}

	/**
	 * Adds reason to the list of reasons
	 * @param index of the hashmap of issues
	 * @param map hashmap with all the issue information
	 * @param issueDescription the issue description
	 */
	private void addNewFoundIssue (Integer index, Map<Integer, DataIssue> map, String issueDescription) {
		DataIssue di = map.get(index);
		if (di == null) {
			di = new DataIssue(index+1L, issueDescription);
			map.put(index, di);
		}
		else {
			di.addReason(issueDescription);
		}
	}

}