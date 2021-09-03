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
package io.radien.persistence.jpa;

import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.SystemException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.radien.api.Model;

/**
 * Generic methods for JPA entities
 *
 * @author Marco Weiland
 */
public class EntityManagerUtil {
	private static final Logger log = LoggerFactory.getLogger(EntityManagerUtil.class);

	/**
	 * Persists a {@link Model} implementation in the target database
	 *
	 * @param entity
	 *                   the model to be persisted
	 * @param em
	 *                   the entityManager that gets injected bia the CDI
	 */
	public static void save(Model entity, EntityManager em) {
		EntityTransaction transaction = em.getTransaction();
		boolean hadPreviousTransaction = true;
		try {
			if (!transaction.isActive()) {
				transaction.begin();
				hadPreviousTransaction = false;
			}
			em.persist(entity);
			if(!hadPreviousTransaction) {
				transaction.commit();
				if(log.isInfoEnabled()){
					log.info(GenericErrorCodeMessage.INFO_ENTITY_SAVED.toString(), entity.getClass().getSimpleName());
				}
			}
		} catch (Exception e) {
			transaction.rollback();
			log.error(GenericErrorCodeMessage.ERROR_SAVING_ENTITY.toString(e.getMessage()));
		}
	}

	/**
	 * h Similar to {@link EntityManagerUtil#save(Model, EntityManager)} but
	 * also updates the object in the database if it still exists
	 *
	 * @param entity
	 *                   The entity to be persisted or updated
	 * @param em
	 *                   the entityManager injected by the CDI
	 */
	//TODO: Check if transactions are working
	public static Long saveOrUpdate(Model entity, EntityManager em) {
		try {
			if (entity.getId() == null) {
				em.persist(entity);
			} else {
				em.merge(entity);
			}

			em.flush();
			return entity.getId();
		} catch (Exception e) {
			log.error(GenericErrorCodeMessage.ERROR_SAVING_ENTITY.toString(e.getMessage()));
			return null;
		}
	}

	public static void saveOrUpdate(List<? extends Model> entities, EntityManager em) {
		EntityTransaction transaction = em.getTransaction();
		try {
			boolean hadTransaction = true;
			if(entities.isEmpty()){
				return;
			}
			if(!transaction.isActive()) {
				transaction.begin();
				hadTransaction = false;
			}
			for(Model entity:entities) {
				if (entity.getId() == null) {
					em.persist(entity);
				} else {
					em.merge(entity);
				}
			}
			if(!hadTransaction) {
				transaction.commit();
			}
			if(entities.size()>1) {
				if(log.isInfoEnabled()){
					log.info(GenericErrorCodeMessage.LIST_ENTITY_SAVED.toString(), entities.get(0).getClass().getSimpleName());
				}
			}else if(entities.size() == 1 && log.isInfoEnabled()){
				log.info(GenericErrorCodeMessage.INFO_ENTITY_SAVED.toString(), entities.get(0).getClass().getSimpleName());
			}
		} catch (Exception e) {
			if (transaction.isActive()) {
				transaction.rollback();
			}
			log.error(GenericErrorCodeMessage.ERROR_SAVING_ENTITY.toString(e.getMessage()));
		}
	}

	public static <T extends Model> T saveOrUpdateAndReturn(Model entity, EntityManager em) {
		EntityTransaction transaction = em.getTransaction();
		try {
			transaction.begin();
			if (entity.getId() == null) {
				em.persist(entity);
			} else {
				entity = em.merge(entity);
			}
			transaction.commit();
			if(log.isInfoEnabled()){
				log.info(GenericErrorCodeMessage.INFO_ENTITY_SAVED.toString(), entity.getClass().getSimpleName());
			}
			return (T) entity;
		} catch (Exception e) {
			if (transaction.isActive()) {
				transaction.rollback();
			}
			log.error(GenericErrorCodeMessage.ERROR_SAVING_ENTITY.toString(e.getMessage()));
			return null;
		}
	}

	/**
	 * Deletes a {@link Model} implementation in the target database
	 *
	 * @param entity
	 *                   The entity to be deleted
	 * @param em
	 *                   the entityManager injected by the CDI
	 */
	public static void delete(Model entity, EntityManager em) {
		if (entity != null && entity.getId() == null) {
			return;
		}
		EntityTransaction transaction = em.getTransaction();
		boolean hadTransaction = true;
		try {
			if(!transaction.isActive()) {
				transaction.begin();
				hadTransaction = false;
			}
			if (!em.contains(entity)) {
				entity = em.merge(entity);
			}
			em.remove(entity);
			if(!hadTransaction) {
				transaction.commit();
			}
		} catch (Exception e) {
			if (transaction.isActive()) {
				transaction.rollback();
			}
			log.error(GenericErrorCodeMessage.ERROR_DELETING_ENTITY.toString(e.getMessage()), e);
		}
		if(entity != null && log.isInfoEnabled()) {
			log.info(GenericErrorCodeMessage.INFO_ENTITY_DELETED.toString(), entity.getClass().getSimpleName());
		}
	}

	/**
	 * Deletes a List of {@link Model} implementation in the target database
	 *
	 * @param entities
	 *                   The entity to be deleted
	 * @param em
	 *                   the entityManager injected by the CDI
	 */
	public static void delete(List<? extends Model> entities, EntityManager em) throws SystemException {
		if (entities == null || entities.isEmpty()) {
			return;
		}
		EntityTransaction transaction = em.getTransaction();
		try {
			boolean hadTransaction = true;
			if(entities.isEmpty()){
				return;
			}
			if(!transaction.isActive()) {
				hadTransaction = false;
				transaction.begin();
			}
			for(Model entity: entities) {
				if(entity.getId() == null && log.isWarnEnabled()){
					log.warn(GenericErrorCodeMessage.ENTITY_ID_NULL.toString(entity.toString()));
					continue;
				}
				if (isDetached(entity,em)) {
					entity = attach(entity,em);
				}
				em.remove(entity);
			}
			if (!hadTransaction) {
				transaction.commit();
			}
		} catch (Exception e) {
			if (transaction.isActive()) {
				transaction.rollback();
			}
			throw new SystemException(GenericErrorCodeMessage.ERROR_DELETING_ENTITY.toString(e.getMessage()));
		}
	}
	public static boolean isDetached(Model entity, EntityManager em) {
		return !em.contains(entity);
	}

	public static Model attach(Model entity, EntityManager em) {
		return em.merge(entity);
	}
}
