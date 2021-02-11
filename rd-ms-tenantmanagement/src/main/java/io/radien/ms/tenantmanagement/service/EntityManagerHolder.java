package io.radien.ms.tenantmanagement.service;

import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

@Stateful
public class EntityManagerHolder {

    @PersistenceContext(unitName = "tenantPersistenceUnit", type = PersistenceContextType.EXTENDED)
    private EntityManager em;

    public EntityManager getEm() {
        return em;
    }
}
