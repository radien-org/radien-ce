package io.radien.ms.ticketmanagement.service;

import io.radien.api.entity.Page;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.ticket.SystemTicket;
import io.radien.api.service.ticket.TicketServiceAccess;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.TicketException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.ticketmanagement.entities.TicketEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;
import java.util.stream.Collectors;

@Stateful
public class TicketService implements TicketServiceAccess {

    private static final long serialVersionUID = 6159752149574674022L;

    @Inject
    private EntityManagerHolder emh;

    private static final Logger log = LoggerFactory.getLogger(TicketService.class);

    @Override
    public void create(SystemTicket ticket) throws TicketException, UniquenessConstraintException {
        validateTicket(ticket);
        List<TicketEntity> alreadyExistentRecords = searchDuplicatedFields(ticket);
        if (alreadyExistentRecords.isEmpty()) {
            emh.getEm().persist(ticket);
        } else {
            throw new UniquenessConstraintException(GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Token"));
        }
    }

    @Override
    public SystemTicket get(Long ticketId) {
        return emh.getEm().find(TicketEntity.class, ticketId);
    }

    @Override
    public Page<SystemTicket> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) {
        log.info("Going to create a new pagination!");
        EntityManager entityManager = emh.getEm();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<TicketEntity> criteriaQuery = criteriaBuilder.createQuery(TicketEntity.class);
        Root<TicketEntity> ticketRoot = criteriaQuery.from(TicketEntity.class);

        criteriaQuery.select(ticketRoot);
        Predicate global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        if(search!= null) {
            global = criteriaBuilder.and(criteriaBuilder.like(ticketRoot.get("token"), search));
            criteriaQuery.where(global);
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

        return new Page<SystemTicket>(systemTickets, pageNo, totalRecords, totalPages);
    }

    @Override
    public void update(SystemTicket ticket) throws TicketException, UniquenessConstraintException {
        validateTicket(ticket);
        List<TicketEntity> alreadyExistentRecords = searchDuplicatedFields(ticket);
        if (alreadyExistentRecords.isEmpty()) {
            emh.getEm().merge(ticket);
        } else {
            throw new UniquenessConstraintException(GenericErrorCodeMessage.DUPLICATED_FIELD.toString("Token"));
        }

    }


    @Override
    public boolean delete(Long tenantId) {
        EntityManager em = emh.getEm();
        return delete(tenantId, em);
    }


    private void validateTicket(SystemTicket ticket) throws TicketException {
        if (validateIfFieldsAreEmpty(ticket.getToken())) {
            throw new TicketException(GenericErrorCodeMessage.TICKET_FIELD_NOT_PROVIDED.toString("token"));
        }
    }

    private boolean validateIfFieldsAreEmpty(String field) {
        return field == null || field.trim().isEmpty();
    }

    protected boolean delete(Long ticketId, EntityManager entityManager) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<TicketEntity> criteriaDelete = cb.createCriteriaDelete(TicketEntity.class);
        Root<TicketEntity> userRoot = criteriaDelete.from(TicketEntity.class);
        criteriaDelete.where(cb.equal(userRoot.get("id"), ticketId));
        int ret = entityManager.createQuery(criteriaDelete).executeUpdate();
        return ret > 0;
    }

    private long getCount(Predicate global, Root<TicketEntity> userRoot) {

        log.info("Going to count the existent records.");
        EntityManager em = emh.getEm();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        criteriaQuery.where(global);

        criteriaQuery.select(criteriaBuilder.count(userRoot));

        TypedQuery<Long> q= em.createQuery(criteriaQuery);

        return q.getSingleResult();
    }

    private List<TicketEntity> searchDuplicatedFields(SystemTicket ticket) {
        EntityManager em = emh.getEm();
        List<TicketEntity> alreadyExistentRecords;
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<TicketEntity> criteriaQuery = criteriaBuilder.createQuery(TicketEntity.class);
        Root<TicketEntity> root = criteriaQuery.from(TicketEntity.class);
        criteriaQuery.select(root);
        Predicate global =
                criteriaBuilder.equal(root.get("token"), ticket.getToken());
        if (ticket.getId() != null) {
            global = criteriaBuilder.and(global, criteriaBuilder.notEqual(root.get("id"), ticket.getId()));
        }
        criteriaQuery.where(global);
        TypedQuery<TicketEntity> q = em.createQuery(criteriaQuery);
        alreadyExistentRecords = q.getResultList();
        return alreadyExistentRecords;
    }

}
