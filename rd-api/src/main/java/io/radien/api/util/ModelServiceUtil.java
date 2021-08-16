/*
 * Copyright (c) 2006-present openappframe.org & its legal owners. All rights reserved.
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
package io.radien.api.util;

import java.io.Serializable;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Class that holds/manages static methods maintained redundant
 * code blocks of a model service class(s)
 *
 * @author Rajesh Gavvala
 */
public class ModelServiceUtil implements Serializable {
    private static final long serialVersionUID = 3114832435567898715L;

    public static Predicate getFilteredPredicateFromModelService(boolean isFilterIds,
                                                                List<Long>  filterIds, boolean filterIsLogicConjunction,
                                                                CriteriaBuilder criteriaBuilder, Root<?> objectRoot){
        Predicate global;
        // is LogicalConjunction represents if you join the fields on the predicates with "or" or "and"
        // the predicate is build with the logic (start,operator,newPredicate)
        // where start represents the already joined predicates
        // operator is "and" or "or"
        // depending on the operator the start may need to be true or false
        // true and predicate1 and predicate2
        // false or predicate1 or predicate2
        if(filterIsLogicConjunction) {
            global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        } else {
            global = criteriaBuilder.isFalse(criteriaBuilder.literal(true));
        }

        if (isFilterIds) {
            Predicate in = objectRoot.get("id").in(filterIds);
            if(filterIsLogicConjunction) {
                global = criteriaBuilder.and(global, in);
            } else {
                global = criteriaBuilder.or(global, in);
            }
        }

        return global;
    }

    public static long getCountFromModelService(CriteriaBuilder criteriaBuilder, Predicate global, Root<?> userRoot, EntityManager em){
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        criteriaQuery.where(global);

        criteriaQuery.select(criteriaBuilder.count(userRoot));

        TypedQuery<Long> q=em.createQuery(criteriaQuery);
        return q.getSingleResult();
    }

    public static List<Order> getListOrderSortBy(boolean isAscending, Root<?> userRoot,
                                                      CriteriaBuilder criteriaBuilder, List<String> sortBy){
        List<Order> orders;
        if(isAscending){
            orders = sortBy.stream().map(i->criteriaBuilder.asc(userRoot.get(i))).collect(Collectors.toList());
        } else {
            orders = sortBy.stream().map(i->criteriaBuilder.desc(userRoot.get(i))).collect(Collectors.toList());
        }
        return orders;
    }

    public static Predicate getFieldPredicateFromModelService(Object value, boolean filterIsExact, boolean filterIsLogicConjunction,
                                             CriteriaBuilder criteriaBuilder,
                                             Expression<? extends Object> objectExpression, Predicate global){
        Predicate subPredicate;
        if (filterIsExact) {
            subPredicate = criteriaBuilder.equal(objectExpression, value);
        } else {
            subPredicate = criteriaBuilder.like((Expression<String>)  objectExpression, "%" + value + "%");
        }

        if(filterIsLogicConjunction) {
            global = criteriaBuilder.and(global, subPredicate);
        } else {
            global = criteriaBuilder.or(global, subPredicate);
        }
        return global;
    }

}
