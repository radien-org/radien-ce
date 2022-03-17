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
package io.radien.ms.doctypemanagement.services;

import io.radien.api.SystemVariables;
import io.radien.api.entity.Page;
import io.radien.api.model.docmanagement.propertytype.SystemJCRPropertyType;
import io.radien.api.service.docmanagement.propertytype.PropertyTypeServiceAccess;

import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.NotFoundException;
import io.radien.exception.UniquenessConstraintException;

import io.radien.ms.doctypemanagement.entities.JCRPropertyTypeEntity;

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
/**
 * JCRPropertyType Service class that will access and request
 * all the information and logics from the db
 *
 * @author Rajesh Gavvala
 */
@Stateful
public class PropertyTypeService implements PropertyTypeServiceAccess {
	private static final Logger log = LoggerFactory.getLogger( PropertyTypeService.class);

	@PersistenceContext(unitName = "persistenceUnit", type = PersistenceContextType.EXTENDED)
	private EntityManager em;


	/**
	 * Saves or updates the requested and given demo information into the DB.
	 * @param jcrpropertytypeId to be added/inserted or updated
	 * @throws NotFoundException in case the demo does not exist in the DB, or cannot be found.
	 */
	@Override
	public SystemJCRPropertyType get(Long jcrpropertytypeId) throws NotFoundException {
		SystemJCRPropertyType result = em.find(JCRPropertyTypeEntity.class, jcrpropertytypeId);
		if(result == null){
			throw new NotFoundException(jcrpropertytypeId.toString());
		}
		return result;
	}

	@Override
	public Page<SystemJCRPropertyType> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) {
		log.info("Going to create the new pagination!");

		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<JCRPropertyTypeEntity> criteriaQuery = criteriaBuilder.createQuery(JCRPropertyTypeEntity.class);
		Root<JCRPropertyTypeEntity> jcrpropertytypeEntityRoot = criteriaQuery.from(JCRPropertyTypeEntity.class);

		criteriaQuery.select(jcrpropertytypeEntityRoot);
		Predicate global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));
		if(search!= null) {
			global = criteriaBuilder.and(criteriaBuilder.like(jcrpropertytypeEntityRoot.get(SystemVariables.NAME.getFieldName()), search));
			criteriaQuery.where(global);
		}
		if(sortBy != null && !sortBy.isEmpty()){
			List<Order> orders;
			if(isAscending){
				orders = sortBy.stream().map(i->criteriaBuilder.asc(jcrpropertytypeEntityRoot.get(i))).collect( Collectors.toList());
			} else {
				orders = sortBy.stream().map(i->criteriaBuilder.desc(jcrpropertytypeEntityRoot.get(i))).collect(Collectors.toList());
			}
			criteriaQuery.orderBy(orders);
		}

		TypedQuery<JCRPropertyTypeEntity> q= em.createQuery(criteriaQuery);

		q.setFirstResult((pageNo-1) * pageSize);
		q.setMaxResults(pageSize);

		List<? extends SystemJCRPropertyType> systemRoles = q.getResultList();

		int totalRecords = Math.toIntExact(getCount(global, jcrpropertytypeEntityRoot));
		int totalPages = totalRecords%pageSize==0 ? totalRecords/pageSize : totalRecords/pageSize+1;

		log.info("New pagination ready to be showed!");

		return new Page<>(systemRoles, pageNo, totalRecords, totalPages);
	}

	/**
	 * Saves or updates the requested and given user information into the DB.
	 * @param jcrpropertytype to be added/inserted or updated
	 * @throws io.radien.exception.NotFoundException in case the user does not exist in the DB, or cannot be found.
	 * @throws UniquenessConstraintException in case of duplicated email/duplicated logon
	 */
	@Override
	public void save(SystemJCRPropertyType jcrpropertytype) throws NotFoundException, UniquenessConstraintException {
		List<JCRPropertyTypeEntity> alreadyExistentRecords = searchDuplicatedName(jcrpropertytype);

		if(jcrpropertytype.getId() == null) {
			if(alreadyExistentRecords.isEmpty()) {
				em.persist(jcrpropertytype);
			} else {
				validateUniquenessRecords(alreadyExistentRecords, jcrpropertytype);
			}
		} else {
			validateUniquenessRecords(alreadyExistentRecords, jcrpropertytype);

			em.merge(jcrpropertytype);
		}
	}

	/**
	 * Query to validate if an existent email address or logon already exists in the database or not.
	 * @param jcrpropertytype user information to look up.
	 * @return list of users with duplicated information.
	 */
	private List<JCRPropertyTypeEntity> searchDuplicatedName(SystemJCRPropertyType jcrpropertytype) {
		List<JCRPropertyTypeEntity> alreadyExistentRecords;
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<JCRPropertyTypeEntity> criteriaQuery = criteriaBuilder.createQuery(JCRPropertyTypeEntity.class);
		Root<JCRPropertyTypeEntity> jcrpropertytypeEntityRoot = criteriaQuery.from(JCRPropertyTypeEntity.class);
		criteriaQuery.select(jcrpropertytypeEntityRoot);
		Predicate global = criteriaBuilder.or(
				criteriaBuilder.equal(jcrpropertytypeEntityRoot.get(SystemVariables.NAME.getFieldName()), jcrpropertytype.getName()));
		if(jcrpropertytype.getId()!= null) {
			global=criteriaBuilder.and(global, criteriaBuilder.notEqual(jcrpropertytypeEntityRoot.get(SystemVariables.ID.getFieldName()), jcrpropertytype.getId()));
		}
		criteriaQuery.where(global);
		TypedQuery<JCRPropertyTypeEntity> q = em.createQuery(criteriaQuery);
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
	private void validateUniquenessRecords(List<JCRPropertyTypeEntity> alreadyExistentRecords, SystemJCRPropertyType newUserInformation) throws UniquenessConstraintException {
		if (alreadyExistentRecords.size() == 2) {
			throw new UniquenessConstraintException( GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Name"));
		} else if(!alreadyExistentRecords.isEmpty()) {
			SystemJCRPropertyType alreadyExistentRecord = alreadyExistentRecords.get(0);
			boolean isSameName = alreadyExistentRecord.getName().equals(newUserInformation.getName());

			if(isSameName) {
				throw new UniquenessConstraintException(GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Name"));
			}
		}
	}

	/**
	 * Deletes a unique demo selected by his id.
	 * @param jcrpropertytypeId to be deleted.
	 */
	@Override
	public void delete(Long jcrpropertytypeId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<JCRPropertyTypeEntity> criteriaDelete = cb.createCriteriaDelete(JCRPropertyTypeEntity.class);
		Root<JCRPropertyTypeEntity> jcrpropertytypeRoot = criteriaDelete.from(JCRPropertyTypeEntity.class);

		criteriaDelete.where(cb.equal(jcrpropertytypeRoot.get("id"), jcrpropertytypeId));
		em.createQuery(criteriaDelete).executeUpdate();
	}

	/**
	 * Count the number of jcrpropertytypes existent in the DB.
	 * @return the count of users
	 */
	private long getCount(Predicate global, Root<JCRPropertyTypeEntity> jcrpropertytypeEntityRoot) {
		log.info("Going to count the existent records in the DB.");

		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		criteriaQuery.where(global);
		criteriaQuery.select(criteriaBuilder.count(jcrpropertytypeEntityRoot));
		TypedQuery<Long> q= em.createQuery(criteriaQuery);

		return q.getSingleResult();
	}

	/**
	 * Count the number of all the jcrpropertytypes existent in the DB.
	 * @return the count of jcrpropertytypes
	 */
	@Override
	public long getTotalRecordsCount() {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		return getCount(criteriaBuilder.isTrue(criteriaBuilder.literal(true)), criteriaBuilder.createQuery(Long.class).from(JCRPropertyTypeEntity.class));
	}
}
