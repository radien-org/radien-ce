/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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

import io.radien.api.SystemVariables;
import io.radien.api.entity.Page;
import io.radien.api.model.${entityResourceName.toLowerCase()}.System${entityResourceName};
import io.radien.api.service.${entityResourceName.toLowerCase()}.${entityResourceName}ServiceAccess;

import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.UniquenessConstraintException;
import io.radien.exception.${entityResourceName.toLowerCase()}.${entityResourceName}NotFoundException;

import ${package}.ms.service.entities.${entityResourceName}Entity;

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
 * ${entityResourceName} Service class that will access and request
 * all the information and logics from the db
 *
 * @author Rajesh Gavvala
 */
@Stateful
public class ${entityResourceName}Service implements ${entityResourceName}ServiceAccess {
	private static final Logger log = LoggerFactory.getLogger( ${entityResourceName}Service.class);

	@PersistenceContext(unitName = "persistenceUnit", type = PersistenceContextType.EXTENDED)
	private EntityManager em;


	/**
	 * Saves or updates the requested and given demo information into the DB.
	 * @param ${entityResourceName.toLowerCase()}Id to be added/inserted or updated
	 * @throws ${entityResourceName}NotFoundException in case the demo does not exist in the DB, or cannot be found.
	 */
	@Override
	public System${entityResourceName} get(Long ${entityResourceName.toLowerCase()}Id) throws ${entityResourceName}NotFoundException {
		System${entityResourceName} result = em.find(${entityResourceName}Entity.class, ${entityResourceName.toLowerCase()}Id);
		if(result == null){
			throw new ${entityResourceName}NotFoundException(${entityResourceName.toLowerCase()}Id.toString());
		}
		return result;
	}

	@Override
	public Page<System${entityResourceName}> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) {
		log.info("Going to create the new pagination!");

		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<${entityResourceName}Entity> criteriaQuery = criteriaBuilder.createQuery(${entityResourceName}Entity.class);
		Root<${entityResourceName}Entity> ${entityResourceName.toLowerCase()}EntityRoot = criteriaQuery.from(${entityResourceName}Entity.class);

		criteriaQuery.select(${entityResourceName.toLowerCase()}EntityRoot);
		Predicate global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));
		if(search!= null) {
			global = criteriaBuilder.and(criteriaBuilder.like(${entityResourceName.toLowerCase()}EntityRoot.get(SystemVariables.NAME.getFieldName()), search));
			criteriaQuery.where(global);
		}
		if(sortBy != null && !sortBy.isEmpty()){
			List<Order> orders;
			if(isAscending){
				orders = sortBy.stream().map(i->criteriaBuilder.asc(${entityResourceName.toLowerCase()}EntityRoot.get(i))).collect( Collectors.toList());
			} else {
				orders = sortBy.stream().map(i->criteriaBuilder.desc(${entityResourceName.toLowerCase()}EntityRoot.get(i))).collect(Collectors.toList());
			}
			criteriaQuery.orderBy(orders);
		}

		TypedQuery<${entityResourceName}Entity> q= em.createQuery(criteriaQuery);

		q.setFirstResult((pageNo-1) * pageSize);
		q.setMaxResults(pageSize);

		List<? extends System${entityResourceName}> systemRoles = q.getResultList();

		int totalRecords = Math.toIntExact(getCount(global, ${entityResourceName.toLowerCase()}EntityRoot));
		int totalPages = totalRecords%pageSize==0 ? totalRecords/pageSize : totalRecords/pageSize+1;

		log.info("New pagination ready to be showed!");

		return new Page<>(systemRoles, pageNo, totalRecords, totalPages);
	}

	/**
	 * Saves or updates the requested and given user information into the DB.
	 * @param ${entityResourceName.toLowerCase()} to be added/inserted or updated
	 * @throws ${entityResourceName}NotFoundException in case the user does not exist in the DB, or cannot be found.
	 * @throws UniquenessConstraintException in case of duplicated email/duplicated logon
	 */
	@Override
	public void save(System${entityResourceName} ${entityResourceName.toLowerCase()}) throws ${entityResourceName}NotFoundException, UniquenessConstraintException {
		List<${entityResourceName}Entity> alreadyExistentRecords = searchDuplicatedName(${entityResourceName.toLowerCase()});

		if(${entityResourceName.toLowerCase()}.getId() == null) {
			if(alreadyExistentRecords.isEmpty()) {
				em.persist(${entityResourceName.toLowerCase()});
			} else {
				validateUniquenessRecords(alreadyExistentRecords, ${entityResourceName.toLowerCase()});
			}
		} else {
			validateUniquenessRecords(alreadyExistentRecords, ${entityResourceName.toLowerCase()});

			em.merge(${entityResourceName.toLowerCase()});
		}
	}

	/**
	 * Query to validate if an existent email address or logon already exists in the database or not.
	 * @param ${entityResourceName.toLowerCase()} user information to look up.
	 * @return list of users with duplicated information.
	 */
	private List<${entityResourceName}Entity> searchDuplicatedName(System${entityResourceName} ${entityResourceName.toLowerCase()}) {
		List<${entityResourceName}Entity> alreadyExistentRecords;
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<${entityResourceName}Entity> criteriaQuery = criteriaBuilder.createQuery(${entityResourceName}Entity.class);
		Root<${entityResourceName}Entity> ${entityResourceName.toLowerCase()}EntityRoot = criteriaQuery.from(${entityResourceName}Entity.class);
		criteriaQuery.select(${entityResourceName.toLowerCase()}EntityRoot);
		Predicate global = criteriaBuilder.or(
				criteriaBuilder.equal(${entityResourceName.toLowerCase()}EntityRoot.get(SystemVariables.NAME.getFieldName()), ${entityResourceName.toLowerCase()}.getName()));
		if(${entityResourceName.toLowerCase()}.getId()!= null) {
			global=criteriaBuilder.and(global, criteriaBuilder.notEqual(${entityResourceName.toLowerCase()}EntityRoot.get(SystemVariables.ID.getFieldName()), ${entityResourceName.toLowerCase()}.getId()));
		}
		criteriaQuery.where(global);
		TypedQuery<${entityResourceName}Entity> q = em.createQuery(criteriaQuery);
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
	private void validateUniquenessRecords(List<${entityResourceName}Entity> alreadyExistentRecords, System${entityResourceName} newUserInformation) throws UniquenessConstraintException {
		if (alreadyExistentRecords.size() == 2) {
			throw new UniquenessConstraintException( GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Name"));
		} else if(!alreadyExistentRecords.isEmpty()) {
			System${entityResourceName} alreadyExistentRecord = alreadyExistentRecords.get(0);
			boolean isSameName = alreadyExistentRecord.getName().equals(newUserInformation.getName());

			if(isSameName) {
				throw new UniquenessConstraintException(GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Name"));
			}
		}
	}

	/**
	 * Deletes a unique demo selected by his id.
	 * @param ${entityResourceName.toLowerCase()}Id to be deleted.
	 */
	@Override
	public void delete(Long ${entityResourceName.toLowerCase()}Id) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<${entityResourceName}Entity> criteriaDelete = cb.createCriteriaDelete(${entityResourceName}Entity.class);
		Root<${entityResourceName}Entity> ${entityResourceName.toLowerCase()}Root = criteriaDelete.from(${entityResourceName}Entity.class);

		criteriaDelete.where(cb.equal(${entityResourceName.toLowerCase()}Root.get("id"), ${entityResourceName.toLowerCase()}Id));
		em.createQuery(criteriaDelete).executeUpdate();
	}

	/**
	 * Count the number of ${entityResourceName.toLowerCase()}s existent in the DB.
	 * @return the count of users
	 */
	private long getCount(Predicate global, Root<${entityResourceName}Entity> ${entityResourceName.toLowerCase()}EntityRoot) {
		log.info("Going to count the existent records in the DB.");

		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		criteriaQuery.where(global);
		criteriaQuery.select(criteriaBuilder.count(${entityResourceName.toLowerCase()}EntityRoot));
		TypedQuery<Long> q= em.createQuery(criteriaQuery);

		return q.getSingleResult();
	}

	/**
	 * Count the number of all the ${entityResourceName.toLowerCase()}s existent in the DB.
	 * @return the count of ${entityResourceName.toLowerCase()}s
	 */
	@Override
	public long getTotalRecordsCount() {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		return getCount(criteriaBuilder.isTrue(criteriaBuilder.literal(true)), criteriaBuilder.createQuery(Long.class).from(${entityResourceName}Entity.class));
	}
}
