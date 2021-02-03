package io.radien.ms.permissionmanagement.service;

import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemAction;
import io.radien.api.model.permission.SystemActionSearchFilter;
import io.radien.api.service.permission.ActionServiceAccess;
import io.radien.exception.ActionNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.permissionmanagement.client.entities.ActionType;
import io.radien.ms.permissionmanagement.client.exceptions.ErrorCodeMessage;
import io.radien.ms.permissionmanagement.model.Action;

import javax.ejb.Stateless;
import javax.persistence.*;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class ActionService implements ActionServiceAccess {

    @PersistenceUnit
    private EntityManagerFactory emf;

    /**
     * Count the number of Actions existent in the DB.
     * @return the count of Actions
     */
    private long getCount(Predicate global, Root<Action> actionRoot, CriteriaBuilder cb, EntityManager em) {
        CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);

        criteriaQuery.where(global);

        criteriaQuery.select(cb.count(actionRoot));

        TypedQuery<Long> q=em.createQuery(criteriaQuery);
        return q.getSingleResult();
    }


    /**
     * Gets the System Action searching by the PK (id).
     * @param actionId to be searched.
     * @return the system Action requested to be found.
     */
    @Override
    public SystemAction get(Long actionId)  {
        return getEntityManager().find(Action.class, actionId);
    }

    /**
     * Gets a list of System Actions searching by multiple PK's (id) requested in a list.
     * @param actionId to be searched.
     * @return the list of system Actions requested to be found.
     */
    @Override
    public List<SystemAction> get(List<Long> actionId) {
        ArrayList<SystemAction> results = new ArrayList<>();
        if(actionId == null || actionId.isEmpty()){
            return results;
        }

        EntityManager em = getEntityManager();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Action> criteriaQuery = criteriaBuilder.createQuery(Action.class);
        Root<Action> ActionRoot = criteriaQuery.from(Action.class);
        criteriaQuery.select(ActionRoot);
        criteriaQuery.where(ActionRoot.get("id").in(actionId));

        TypedQuery<Action> q=em.createQuery(criteriaQuery);

        return new ArrayList<>(q.getResultList());
    }

    /**
     * Gets all the Actions into a pagination mode.
     * Can be filtered by name Action.
     * @param search name description for some action
     * @param pageNo of the requested information. Where the Action is.
     * @param pageSize total number of pages returned in the request.
     * @param sortBy sort filter criteria.
     * @param isAscending ascending filter criteria.
     * @return a page of system Actions.
     */
    @Override
    public Page<SystemAction> getAll(String search, int pageNo, int pageSize,
                                     List<String> sortBy,
                                     boolean isAscending) {
        EntityManager em = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Action> criteriaQuery = criteriaBuilder.createQuery(Action.class);
        Root<Action> actionRoot = criteriaQuery.from(Action.class);

        criteriaQuery.select(actionRoot);

        Predicate global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        if(search!= null) {
            global = criteriaBuilder.and(criteriaBuilder.like(actionRoot.get("name"), search));
            criteriaQuery.where(global);
        }

        if(sortBy != null && !sortBy.isEmpty()){
            List<Order> orders;
            if(isAscending){
                orders = sortBy.stream().map(i->criteriaBuilder.asc(actionRoot.get(i))).collect(Collectors.toList());
            } else {
                orders = sortBy.stream().map(i->criteriaBuilder.desc(actionRoot.get(i))).collect(Collectors.toList());
            }
            criteriaQuery.orderBy(orders);
        }

        TypedQuery<Action> q=em.createQuery(criteriaQuery);

        q.setFirstResult((pageNo-1) * pageSize);
        q.setMaxResults(pageSize);

        List<? extends SystemAction> systemActions = q.getResultList();

        int totalRecords = Math.toIntExact(getCount(global, actionRoot, em.getCriteriaBuilder(), em));
        int totalPages = totalRecords%pageSize==0 ? totalRecords/pageSize : totalRecords/pageSize+1;

        return new Page<SystemAction>(systemActions, pageNo, totalRecords, totalPages);
    }

    /**
     * Saves or updates the requested and given Action information into the DB.
     * @param action to be added/inserted or updated
     * @throws UniquenessConstraintException in case of duplicated name
     */
    @Override
    public void save(SystemAction action) throws UniquenessConstraintException {
        List<Action> alreadyExistentRecords = searchDuplicatedName(action);
        EntityManager em = getEntityManager();
        if(action.getId() == null) {
            if(alreadyExistentRecords.isEmpty()) {
                em.persist(action);
            } else {
                validateUniquenessRecords(alreadyExistentRecords, action);
            }
        } else {
            validateUniquenessRecords(alreadyExistentRecords, action);

            em.merge(action);
        }
    }

    /**
     * Query to validate if an existent name (for action) already exists in the database or not.
     * @param action Action information to look up.
     * @return list of Actions with duplicated information.
     */
    private List<Action> searchDuplicatedName(SystemAction action) {
        List<Action> alreadyExistentRecords;
        EntityManager em = getEntityManager();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Action> criteriaQuery = criteriaBuilder.createQuery(Action.class);
        Root<Action> ActionRoot = criteriaQuery.from(Action.class);
        criteriaQuery.select(ActionRoot);
        Predicate global = criteriaBuilder.equal(ActionRoot.get("name"), action.getName());
        if(action.getId()!= null) {
            global=criteriaBuilder.and(global, criteriaBuilder.notEqual(ActionRoot.get("id"), action.getId()));
        }
        criteriaQuery.where(global);
        TypedQuery<Action> q = em.createQuery(criteriaQuery);
        alreadyExistentRecords = q.getResultList();
        return alreadyExistentRecords;
    }

    /**
     * When updating the Action information this method will validate if the unique values maintain as unique.
     * Will search for the Action name, given in the information to be updated, to see if they are not already in the DB in another Action.
     * @param alreadyExistentRecords list of duplicated Action information
     * @param newActionInformation Action information to update into the requested one
     * @throws UniquenessConstraintException in case of requested information to be updated already exists in the DB
     */
    private void validateUniquenessRecords(List<Action> alreadyExistentRecords, SystemAction newActionInformation) throws UniquenessConstraintException {
        if(!alreadyExistentRecords.isEmpty()) {
            boolean isSameName = alreadyExistentRecords.get(0).getName().equals(newActionInformation.getName());
            if(isSameName) {
                throw new UniquenessConstraintException(ErrorCodeMessage.DUPLICATED_FIELD.toString("Name"));
            }
        }
    }

    /**
     * Deletes a unique Action selected by his id.
     * @param actionId to be deleted.
     */
    @Override
    public void delete(Long actionId) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<Action> criteriaDelete = cb.createCriteriaDelete(Action.class);
        Root<Action> ActionRoot = criteriaDelete.from(Action.class);

        criteriaDelete.where(cb.equal(ActionRoot.get("id"),actionId));
        em.createQuery(criteriaDelete).executeUpdate();
    }

    /**
     * Deletes a list of Actions selected by his id.
     * @param actionIds to be deleted.
     */
    @Override
    public void delete(Collection<Long> actionIds) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<Action> criteriaDelete = cb.createCriteriaDelete(Action.class);
        Root<Action> actionRoot = criteriaDelete.from(Action.class);

        criteriaDelete.where(actionRoot.get("id").in(actionIds));
        em.createQuery(criteriaDelete).executeUpdate();
    }

    /**
     * Get actions by unique columns
     * @param filter entity with available filters to search Action
     */
    @Override
    public List<? extends SystemAction> getActions(SystemActionSearchFilter filter) {
        EntityManager em = getEntityManager();
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Action> criteriaQuery = criteriaBuilder.createQuery(Action.class);
        Root<Action> ActionRoot = criteriaQuery.from(Action.class);

        criteriaQuery.select(ActionRoot);

        Predicate global = getFilteredPredicate(filter, criteriaBuilder, ActionRoot);

        criteriaQuery.where(global);
        TypedQuery<Action> q=em.createQuery(criteriaQuery);

        return q.getResultList();
    }

    private Predicate getFilteredPredicate(SystemActionSearchFilter filter,
                                           CriteriaBuilder criteriaBuilder,
                                           Root<Action> actionRoot) {
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

        global = getFieldPredicate("name", filter.getName(), filter, criteriaBuilder, actionRoot, global);
        global = getFieldPredicate("type", filter.getActionType(), filter, criteriaBuilder, actionRoot, global);

        return global;
    }

    private Predicate getFieldPredicate(String name, Object value,
                                        SystemActionSearchFilter filter,
                                        CriteriaBuilder criteriaBuilder,
                                        Root<Action> actionRoot,
                                        Predicate global) {
        if(value != null) {
            Predicate subPredicate;

            if (value instanceof ActionType) {
                subPredicate = criteriaBuilder.equal(actionRoot.get(name), value);
            }
            else {
                if (filter.isExact()) {
                    subPredicate = criteriaBuilder.equal(actionRoot.get(name), value);
                } else {
                    subPredicate = criteriaBuilder.like(actionRoot.get(name), "%" + value + "%");
                }
            }
            if(filter.isLogicConjunction()) {
                global = criteriaBuilder.and(global, subPredicate);
            } else {
                global = criteriaBuilder.or(global, subPredicate);
            }
        }
        return global;
    }
    
    protected EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}
