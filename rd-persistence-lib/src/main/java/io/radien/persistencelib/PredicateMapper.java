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
 */

package io.radien.persistencelib;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * @author Bruno Gama
 **/
public class PredicateMapper {

    /**
     * Will filter all the fields given in the criteria builder and in the filter and create the
     * where clause for the query
     * @param name field to be searched for
     * @param isExact should the search be for the exact value or not
     * @param isLogicalConjunction should the conjunction field be a and or a or
     * @param criteriaBuilder database query builder
     * @param root database table to search the information
     * @return a constructed predicate with the fields needed to be search
     */
    public static <T> Predicate getFilteredSingleNamePredicate(String name, boolean isExact, boolean isLogicalConjunction,
                                           CriteriaBuilder criteriaBuilder,
                                           Root<T> root) {
        Predicate global;

        // is LogicalConjunction represents if you join the fields on the predicates with "or" or "and"
        // the predicate is build with the logic (start,operator,newPredicate)
        // where start represents the already joined predicates
        // operator is "and" or "or"
        // depending on the operator the start may need to be true or false
        // true and predicate1 and predicate2
        // false or predicate1 or predicate2
        if(isLogicalConjunction) {
            global = criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        } else {
            global = criteriaBuilder.isFalse(criteriaBuilder.literal(true));
        }

        global = PredicateMapper.getFieldPredicate("name", name, isExact, isLogicalConjunction, criteriaBuilder, root, global);

        return global;
    }


    /**
     * Method that will create in the database query where clause each and single search
     * @param name of the field to be search in the query
     * @param value of the field to be search or compared in the query
     * @param isExact should the search be for the exact value or not
     * @param isLogicalConjunction should the conjunction field be a and or a or
     * @param criteriaBuilder database query builder
     * @param root database table to search the information
     * @param global complete where clause to be merged into the constructed information
     * @return a constructed predicate with the fields needed to be search
     */
    public static <T> Predicate getFieldPredicate(String name, Object value, boolean isExact, boolean isLogicalConjunction, CriteriaBuilder criteriaBuilder, Root<T> root, Predicate global) {
        if(value != null) {
            Predicate subPredicate;
            if (value instanceof String && !isExact) {
                subPredicate = criteriaBuilder.like(root.get(name),"%"+value+"%");
            } else {
                subPredicate = criteriaBuilder.equal(root.get(name), value);
            }

            if(isLogicalConjunction) {
                global = criteriaBuilder.and(global, subPredicate);
            } else {
                global = criteriaBuilder.or(global, subPredicate);
            }
        }
        return global;
    }
}
