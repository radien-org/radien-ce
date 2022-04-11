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
import io.radien.api.model.docmanagement.mixindefinition.SystemMixinDefinition;
import io.radien.api.service.docmanagement.mixindefinition.MixinDefinitionDataAccessLayer;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.doctypemanagement.entities.MixinDefinitionEntity;
import java.util.List;
import java.util.stream.Collectors;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Stateful
public class MixinDefinitionDataLayer implements MixinDefinitionDataAccessLayer {

	@PersistenceContext(unitName = "persistenceUnit", type = PersistenceContextType.EXTENDED)
	private transient EntityManager em;

	@Override
	public SystemMixinDefinition<Long> get(Long id) {
		return em.find(MixinDefinitionEntity.class, id);
	}

	@Override
	public Page<SystemMixinDefinition<Long>> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<MixinDefinitionEntity> criteriaQuery = criteriaBuilder.createQuery(MixinDefinitionEntity.class);
		Root<MixinDefinitionEntity> mixinDefinitionEntityRoot = criteriaQuery.from(MixinDefinitionEntity.class);

		criteriaQuery.select(mixinDefinitionEntityRoot);
		Predicate global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));
		if(search != null) {
			global = criteriaBuilder.and(
						criteriaBuilder.like(mixinDefinitionEntityRoot.get(SystemVariables.NAME.getFieldName()), search)
					);
			criteriaQuery.where(global);
		}
		if(sortBy != null && !sortBy.isEmpty()){
			List<Order> orders;
			if(isAscending){
				orders = sortBy.stream().map(i->criteriaBuilder.asc(mixinDefinitionEntityRoot.get(i))).collect( Collectors.toList());
			} else {
				orders = sortBy.stream().map(i->criteriaBuilder.desc(mixinDefinitionEntityRoot.get(i))).collect(Collectors.toList());
			}
			criteriaQuery.orderBy(orders);
		}

		TypedQuery<MixinDefinitionEntity> query = em.createQuery(criteriaQuery);
		query.setFirstResult((pageNo-1) * pageSize);
		query.setMaxResults(pageSize);

		List<? extends SystemMixinDefinition<Long>> systemRoles = query.getResultList();
		int totalRecords = Math.toIntExact(getCount(global, mixinDefinitionEntityRoot));
		int totalPages = totalRecords%pageSize==0 ? totalRecords/pageSize : totalRecords/pageSize+1;

		return new Page<>(systemRoles, pageNo, totalRecords, totalPages);
	}

	@Override
	public void save(SystemMixinDefinition<Long> mixinDefinition) throws UniquenessConstraintException {
		List<MixinDefinitionEntity> alreadyExistentRecords = searchDuplicatedName(mixinDefinition);
		validateUniquenessRecords(alreadyExistentRecords, "Name");
		if(existsDuplicateAssociations(mixinDefinition)) {
			throw new UniquenessConstraintException(GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Property Definitions"));
		}

		if(mixinDefinition.getId() == null) {
			em.persist(mixinDefinition);
		} else {
			em.merge(mixinDefinition);
		}
	}

	private void validateUniquenessRecords(List<MixinDefinitionEntity> alreadyExistentRecords, String field) throws UniquenessConstraintException {
		if(!alreadyExistentRecords.isEmpty()) {
			throw new UniquenessConstraintException(GenericErrorCodeMessage.DUPLICATED_FIELD.toString(field));
		}
	}

	@Override
	public void delete(Long id) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<MixinDefinitionEntity> criteriaDelete = cb.createCriteriaDelete(MixinDefinitionEntity.class);
		Root<MixinDefinitionEntity> entityRoot = criteriaDelete.from(MixinDefinitionEntity.class);

		criteriaDelete.where(cb.equal(entityRoot.get("id"), id));
		em.createQuery(criteriaDelete).executeUpdate();
	}

	private long getCount(Predicate global, Root<MixinDefinitionEntity> entityRoot) {
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
		return getCount(criteriaBuilder.isTrue(criteriaBuilder.literal(true)), criteriaBuilder.createQuery(Long.class).from(MixinDefinitionEntity.class));
	}

	private List<MixinDefinitionEntity> searchDuplicatedName(SystemMixinDefinition<Long> mixinDefinition) {
		List<MixinDefinitionEntity> alreadyExistentRecords;
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<MixinDefinitionEntity> criteriaQuery = criteriaBuilder.createQuery(MixinDefinitionEntity.class);
		Root<MixinDefinitionEntity> entityRoot = criteriaQuery.from(MixinDefinitionEntity.class);
		criteriaQuery.select(entityRoot);
		Predicate global = criteriaBuilder.equal(entityRoot.get(SystemVariables.NAME.getFieldName()), mixinDefinition.getName());
		if(mixinDefinition.getId() != null) {
			global = criteriaBuilder.and(
					global,
					criteriaBuilder.notEqual(entityRoot.get(SystemVariables.ID.getFieldName()), mixinDefinition.getId())
			);
		}
		criteriaQuery.where(global);
		TypedQuery<MixinDefinitionEntity> query = em.createQuery(criteriaQuery);
		alreadyExistentRecords = query.getResultList();
		return alreadyExistentRecords;
	}

	private boolean existsDuplicateAssociations(SystemMixinDefinition<Long> mixinDefinition) {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
		Root<MixinDefinitionEntity> entityRoot = criteriaQuery.from(MixinDefinitionEntity.class);
		criteriaQuery.select(criteriaBuilder.construct(
				Tuple.class,
				entityRoot.get("id"),
				criteriaBuilder.function("group_concat", String.class, entityRoot.get("propertyDefinitions"))
		));
		Predicate global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));
		if(mixinDefinition.getId() != null) {
			global = criteriaBuilder.and(
					global,
					criteriaBuilder.notEqual(entityRoot.get(SystemVariables.ID.getFieldName()), mixinDefinition.getId())
			);
		}
		criteriaQuery.where(global)
				.groupBy(entityRoot.get("id"));
		TypedQuery<Tuple> query = em.createQuery(criteriaQuery);
		List<Tuple> resultList = query.getResultList();
		return resultList.stream().anyMatch(r -> r.get(1, String.class)
				.equals(mixinDefinition.getPropertyDefinitions().stream().map(String::valueOf).collect(Collectors.joining(","))));
	}
}
