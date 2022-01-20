package io.radien.ms.ticketmanagement.service;

import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

@Stateful(name = "entityManagerHolderForTicket")
public class EntityManagerHolder {

    @PersistenceContext(unitName = "persistenceUnit", type = PersistenceContextType.EXTENDED)
    private EntityManager em;

    public EntityManager getEm() {
        return em;
    }
}
