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

package io.radien.ms.usermanagement.service.jobfair;

import io.radien.api.model.jobshop.SystemJobshopItem;
import io.radien.ms.usermanagement.entities.JobshopItemEntity;
import java.util.List;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Stateful
public class JobshopItemService {
    private static final long serialVersionUID = 4433044708638177868L;

    @PersistenceContext(unitName = "persistenceUnit", type = PersistenceContextType.EXTENDED)
    private EntityManager em;

    public List<? extends SystemJobshopItem> getAll() {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<JobshopItemEntity> criteriaQuery = criteriaBuilder.createQuery(JobshopItemEntity.class);
        Root<JobshopItemEntity> entityRoot = criteriaQuery.from(JobshopItemEntity.class);
        criteriaQuery.select(entityRoot);

        TypedQuery<JobshopItemEntity> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public void save(SystemJobshopItem item) {
        if(item.getId() != null && em.find(JobshopItemEntity.class, item.getId()) != null) {
            em.merge(item);
            if(item.getWeight() == 0) {
                em.remove(em.find(JobshopItemEntity.class, item.getId()));
            }
            return;
        }
        em.persist(item);
    }
}
