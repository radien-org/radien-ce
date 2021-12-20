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
package io.radien.api.service.legal;

import io.radien.api.entity.Page;
import io.radien.api.model.legal.SystemLegalDocumentType;
import io.radien.exception.SystemException;
import java.util.List;
import java.util.Optional;

/**
 * Rest service client responsible to Deal with LegalDocumentType domain
 * @author Newton Carvalho
 */
public interface LegalDocumentTypeRESTServiceAccess {

    /**
     * Retrieves a page object containing LegalDocumentTypes that matches search parameter.
     * In case of omitted (empty) search parameter retrieves ALL LegalDocumentTypes
     * @param search search parameter for matching LegalDocumentTypes (optional).
     * @param pageNo page number
     * @param pageSize page size
     * @param sortBy Sorting fields
     * @param isAscending Defines if ascending or descending in relation of sorting fields
     * @return Page containing SystemLegalDocumentType instances
     * @throws SystemException in case of any error
     */
    Page<? extends SystemLegalDocumentType> getAll(String search, int pageNo, int pageSize,
                                                   List<String> sortBy, boolean isAscending) throws SystemException;

    /**
     * Finds all LegalDocumentTypes that matches a search filter
     * @param name LegalDocumentType name
     * @param tenantId search property regarding tenant identifier
     * @param toBeShown flag for searching based on toBeShown field
     * @param toBeAccepted flag for searching based on toBeAccepted field
     * @param ids LegalDocumentType ids to be found
     * @param isExact indicates if the match is for approximated value or not
     * @param isLogicalConjunction specifies if the parameters will be unified by AND (true) or OR (false)
     * @return List containing SystemLegalDocumentType instances
     * @throws SystemException in case of any error
     */
    List<? extends SystemLegalDocumentType> getLegalDocumentTypes(String name, Long tenantId,
                                   Boolean toBeShown, Boolean toBeAccepted, List<Long> ids,
                                   boolean isExact, boolean isLogicalConjunction) throws SystemException;

    /**
     * Retrieves an LegalDocumentType by its identifier
     * @param id LegalDocumentType identifier
     * @return an Optional containing Legal Document Type (if exists), otherwise an empty one.
     * @throws SystemException in case of any error
     */
    Optional<SystemLegalDocumentType> getById(Long id) throws SystemException;

    /**
     * Deletes an LegalDocumentType by its identifier
     * @param id LegalDocumentType identifier
     * @return Boolean indicating if operation was concluded successfully
     * @throws SystemException in case of any error
     */
    Boolean delete(long id) throws SystemException;

    /**
     * Creates a LegalDocumentType
     * @param legalDocumentType LegalDocumentType to be created
     * @return Boolean indicating if operation was concluded successfully
     * @throws SystemException in case of any error
     */
    Boolean create(SystemLegalDocumentType legalDocumentType) throws SystemException;

    /**
     * Updates a LegalDocumentType
     * @param legalDocumentType LegalDocumentType to be updated
     * @return Boolean indicating if operation was concluded successfully
     * @throws SystemException in case of any error
     */
    Boolean update(SystemLegalDocumentType legalDocumentType) throws SystemException;

}
