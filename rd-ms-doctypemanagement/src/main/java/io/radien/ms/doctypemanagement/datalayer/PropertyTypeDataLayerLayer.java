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
package io.radien.ms.doctypemanagement.datalayer;

import io.radien.api.SystemVariables;
import io.radien.api.entity.Page;
import io.radien.api.model.docmanagement.propertytype.SystemJCRPropertyType;
import io.radien.api.service.docmanagement.propertytype.PropertyTypeDataAccessLayer;

import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.UniquenessConstraintException;

import io.radien.ms.doctypemanagement.entities.PropertyTypeEntity;

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
public class PropertyTypeDataLayerLayer implements PropertyTypeDataAccessLayer {
	private static final Logger log = LoggerFactory.getLogger( PropertyTypeDataLayerLayer.class);

	@PersistenceContext(unitName = "persistenceUnit", type = PersistenceContextType.EXTENDED)
	private EntityManager em;

	@Override
	public SystemJCRPropertyType get(Long id) {
		return em.find(PropertyTypeEntity.class, id);
	}

	@Override
	public Page<SystemJCRPropertyType> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<PropertyTypeEntity> criteriaQuery = criteriaBuilder.createQuery(PropertyTypeEntity.class);
		Root<PropertyTypeEntity> propertyTypeEntityRoot = criteriaQuery.from(PropertyTypeEntity.class);

		criteriaQuery.select(propertyTypeEntityRoot);
		Predicate global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));
		if(search != null) {
			global = criteriaBuilder.and(
						criteriaBuilder.like(propertyTypeEntityRoot.get(SystemVariables.NAME.getFieldName()), search)
					);
			criteriaQuery.where(global);
		}
		if(sortBy != null && !sortBy.isEmpty()){
			List<Order> orders;
			if(isAscending){
				orders = sortBy.stream().map(i->criteriaBuilder.asc(propertyTypeEntityRoot.get(i))).collect( Collectors.toList());
			} else {
				orders = sortBy.stream().map(i->criteriaBuilder.desc(propertyTypeEntityRoot.get(i))).collect(Collectors.toList());
			}
			criteriaQuery.orderBy(orders);
		}

		TypedQuery<PropertyTypeEntity> query = em.createQuery(criteriaQuery);
		query.setFirstResult((pageNo-1) * pageSize);
		query.setMaxResults(pageSize);

		List<? extends SystemJCRPropertyType> systemRoles = query.getResultList();
		int totalRecords = Math.toIntExact(getCount(global, propertyTypeEntityRoot));
		int totalPages = totalRecords%pageSize==0 ? totalRecords/pageSize : totalRecords/pageSize+1;

		return new Page<>(systemRoles, pageNo, totalRecords, totalPages);
	}

	@Override
	public void save(SystemJCRPropertyType propertyType) throws UniquenessConstraintException {
		List<PropertyTypeEntity> alreadyExistentRecords = searchDuplicatedName(propertyType);
		validateUniquenessRecords(alreadyExistentRecords, propertyType);

		if(propertyType.getId() == null) {
			em.persist(propertyType);
		} else {
			em.merge(propertyType);
		}
	}

	private void validateUniquenessRecords(List<PropertyTypeEntity> alreadyExistentRecords, SystemJCRPropertyType newUserInformation) throws UniquenessConstraintException {
		if (alreadyExistentRecords.size() == 2) {
			throw new UniquenessConstraintException(GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Name"));
		} else if(!alreadyExistentRecords.isEmpty()) {
			SystemJCRPropertyType alreadyExistentRecord = alreadyExistentRecords.get(0);
			boolean isSameName = alreadyExistentRecord.getName().equals(newUserInformation.getName());

			if(isSameName) {
				throw new UniquenessConstraintException(GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Name"));
			}
		}
	}

	@Override
	public void delete(Long id) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<PropertyTypeEntity> criteriaDelete = cb.createCriteriaDelete(PropertyTypeEntity.class);
		Root<PropertyTypeEntity> jcrpropertytypeRoot = criteriaDelete.from(PropertyTypeEntity.class);

		criteriaDelete.where(cb.equal(jcrpropertytypeRoot.get("id"), id));
		em.createQuery(criteriaDelete).executeUpdate();
	}

	private long getCount(Predicate global, Root<PropertyTypeEntity> entityRoot) {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		criteriaQuery.where(global);
		criteriaQuery.select(criteriaBuilder.count(entityRoot));
		TypedQuery<Long> q= em.createQuery(criteriaQuery);

		return q.getSingleResult();
	}

	@Override
	public long getTotalRecordsCount() {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		return getCount(criteriaBuilder.isTrue(criteriaBuilder.literal(true)), criteriaBuilder.createQuery(Long.class).from(PropertyTypeEntity.class));
	}

	private List<PropertyTypeEntity> searchDuplicatedName(SystemJCRPropertyType propertyType) {
		List<PropertyTypeEntity> alreadyExistentRecords;
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<PropertyTypeEntity> criteriaQuery = criteriaBuilder.createQuery(PropertyTypeEntity.class);
		Root<PropertyTypeEntity> entityRoot = criteriaQuery.from(PropertyTypeEntity.class);
		criteriaQuery.select(entityRoot);
		Predicate global = criteriaBuilder.equal(entityRoot.get(SystemVariables.NAME.getFieldName()), propertyType.getName());
		if(propertyType.getId()!= null) {
			global = criteriaBuilder.and(
					global,
					criteriaBuilder.notEqual(entityRoot.get(SystemVariables.ID.getFieldName()), propertyType.getId())
			);
		}
		criteriaQuery.where(global);
		TypedQuery<PropertyTypeEntity> query = em.createQuery(criteriaQuery);
		alreadyExistentRecords = query.getResultList();
		return alreadyExistentRecords;
	}
}
