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
 *
 */

package io.radien.ms.ticketmanagement.service;

import io.radien.api.SystemVariables;
import io.radien.api.entity.Page;
import io.radien.api.model.ticket.SystemTicket;
import io.radien.api.model.ticket.SystemTicketSearchFilter;
import io.radien.api.service.ticket.TicketServiceAccess;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.TicketException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.ticketmanagement.entities.TicketEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.CriteriaDelete;

import java.util.List;
import java.util.stream.Collectors;

@Stateful
public class TicketService implements TicketServiceAccess {

    private static final long serialVersionUID = 6159752149574674022L;

    @PersistenceContext(unitName = "persistenceUnit")
    private EntityManager entityManager;

    private static final Logger log = LoggerFactory.getLogger(TicketService.class);

    @Override
    public void create(SystemTicket ticket) throws TicketException, UniquenessConstraintException {
        validateEmptyToken(ticket);
        List<TicketEntity> alreadyExistentRecords = searchDuplicatedFields(ticket);
        if (alreadyExistentRecords.isEmpty()) {
            entityManager.persist(ticket);
        } else {
            throw new UniquenessConstraintException(GenericErrorCodeMessage.DUPLICATED_FIELD.toString(SystemVariables.TOKEN.getFieldName()));
        }
    }

    @Override
    public SystemTicket get(Long ticketId) {
        return entityManager.find(TicketEntity.class, ticketId);
    }

    @Override
    public SystemTicket getByToken(String ticketUuid) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TicketEntity> criteriaQuery = criteriaBuilder.createQuery(TicketEntity.class);
        Root<TicketEntity> ticketRoot = criteriaQuery.from(TicketEntity.class);

        criteriaQuery.select(ticketRoot);
        criteriaQuery.where(criteriaBuilder.equal(ticketRoot.get("token"), ticketUuid));

        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    @Override
    public Page<SystemTicket> getAll(SystemTicketSearchFilter filter, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) {
        log.info("Going to create a new pagination!");
        EntityManager entityManager = this.entityManager;
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TicketEntity> criteriaQuery = criteriaBuilder.createQuery(TicketEntity.class);
        Root<TicketEntity> ticketRoot = criteriaQuery.from(TicketEntity.class);

        criteriaQuery.select(ticketRoot);
        Predicate global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        if(filter!= null) {
            if(filter.isLogicalConjunction()) {
                global = criteriaBuilder.and(criteriaBuilder.like(ticketRoot.get(SystemVariables.USER_ID.getFieldName()), filter.getUserId().toString()));
                criteriaQuery.where(global);
            }
        }
        if(sortBy != null && !sortBy.isEmpty()){
            List<Order> orders;
            if(isAscending){
                orders = sortBy.stream().map(i->criteriaBuilder.asc(ticketRoot.get(i))).collect(Collectors.toList());
            } else {
                orders = sortBy.stream().map(i->criteriaBuilder.desc(ticketRoot.get(i))).collect(Collectors.toList());
            }
            criteriaQuery.orderBy(orders);
        }
        TypedQuery<TicketEntity> q= entityManager.createQuery(criteriaQuery);

        q.setFirstResult((pageNo-1) * pageSize);
        q.setMaxResults(pageSize);

        List<? extends SystemTicket> systemTickets = q.getResultList();

        int totalRecords = Math.toIntExact(getCount(global, ticketRoot));
        int totalPages = totalRecords%pageSize==0 ? totalRecords/pageSize : totalRecords/pageSize+1;

        log.info("New pagination ready to be showed!");

        return new Page<>(systemTickets, pageNo, totalRecords, totalPages);
    }

    @Override
    public void update(SystemTicket ticket) throws TicketException, UniquenessConstraintException {
        validateEmptyToken(ticket);
        List<TicketEntity> alreadyExistentRecords = searchDuplicatedFields(ticket);
        if (alreadyExistentRecords.isEmpty()) {
            entityManager.merge(ticket);
        } else {
            throw new UniquenessConstraintException(GenericErrorCodeMessage.DUPLICATED_FIELD.toString(SystemVariables.TOKEN.getFieldName()));
        }

    }


    @Override
    public boolean delete(Long tenantId) {
        return delete(tenantId, entityManager);
    }


    private void validateEmptyToken(SystemTicket ticket) throws TicketException {
        if (validateIfFieldsAreEmpty(ticket.getToken())) {
            throw new TicketException(GenericErrorCodeMessage.TICKET_FIELD_NOT_PROVIDED.toString(SystemVariables.TOKEN.getFieldName()));
        }
    }

    private boolean validateIfFieldsAreEmpty(String field) {
        return field == null || field.trim().isEmpty();
    }

    protected boolean delete(Long ticketId, EntityManager entityManager) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<TicketEntity> criteriaDelete = cb.createCriteriaDelete(TicketEntity.class);
        Root<TicketEntity> userRoot = criteriaDelete.from(TicketEntity.class);
        criteriaDelete.where(cb.equal(userRoot.get(SystemVariables.ID.getFieldName()), ticketId));
        int ret = entityManager.createQuery(criteriaDelete).executeUpdate();
        return ret > 0;
    }

    private long getCount(Predicate global, Root<TicketEntity> userRoot) {

        log.info("Going to count the existent records.");
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        criteriaQuery.where(global);

        criteriaQuery.select(criteriaBuilder.count(userRoot));

        TypedQuery<Long> q= entityManager.createQuery(criteriaQuery);

        return q.getSingleResult();
    }

    private List<TicketEntity> searchDuplicatedFields(SystemTicket ticket) {
        List<TicketEntity> alreadyExistentRecords;
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TicketEntity> criteriaQuery = criteriaBuilder.createQuery(TicketEntity.class);
        Root<TicketEntity> root = criteriaQuery.from(TicketEntity.class);
        criteriaQuery.select(root);
        Predicate global =
                criteriaBuilder.equal(root.get(SystemVariables.TOKEN.getFieldName()), ticket.getToken());
        if (ticket.getId() != null) {
            global = criteriaBuilder.and(global, criteriaBuilder.notEqual(root.get(SystemVariables.ID.getFieldName()), ticket.getId()));
        }
        criteriaQuery.where(global);
        TypedQuery<TicketEntity> q = entityManager.createQuery(criteriaQuery);
        alreadyExistentRecords = q.getResultList();
        return alreadyExistentRecords;
    }

}
