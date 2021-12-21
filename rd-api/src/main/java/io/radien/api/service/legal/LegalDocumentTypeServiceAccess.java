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
import io.radien.api.model.legal.SystemLegalDocumentTypeSearchFilter;
import io.radien.api.service.ServiceAccess;
import io.radien.exception.LegalDocumentTypeNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import java.util.List;

/**
 * Basis contract for a Data service to handle
 * {@link io.radien.api.model.legal.SystemLegalDocumentType} domain objects
 *
 * @author Newton Carvalho
 */
public interface LegalDocumentTypeServiceAccess extends ServiceAccess {

    /**
     * Gets the System LegalDocumentType searching by the PK (id).
     * @param legalDocumentTypeId to be searched.
     * @return the system LegalDocumentType requested to be found.
     * @throws LegalDocumentTypeNotFoundException in case the requested legalDocumentType could not be found
     */
    SystemLegalDocumentType get(Long legalDocumentTypeId) throws LegalDocumentTypeNotFoundException;

    /**
     * Gets all the LegalDocumentTypes into a pagination mode.
     * Can be filtered by name LegalDocumentType.
     * @param search name description for some legalDocumentType
     * @param pageNo of the requested information. Where the LegalDocumentType is.
     * @param pageSize total number of pages returned in the request.
     * @param sortBy sort filter criteria.
     * @param isAscending ascending filter criteria.
     * @return a page of system LegalDocumentTypes.
     */
    Page<SystemLegalDocumentType> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending);

    /**
     * Get LegalDocumentTypesBy unique columns
     * @param filter entity with available filters to search LegalDocumentType
     * @return a list of found legalDocumentTypes that match the given search filter
     */
    List<? extends SystemLegalDocumentType> getLegalDocumentTypes(SystemLegalDocumentTypeSearchFilter filter);

    /**
     * Creates the requested LegalDocumentType information into the DB.
     * @param legalDocumentType to be added/inserted
     * @throws UniquenessConstraintException in case of duplicated name (or combination of name and tenant)
     */
    void create(SystemLegalDocumentType legalDocumentType) throws UniquenessConstraintException;

    /**
     * Updates the requested LegalDocumentType information into the DB.
     * @param legalDocumentType to be updated
     * @throws UniquenessConstraintException in case of duplicated name (or combination of name and tenant)
     * @throws LegalDocumentTypeNotFoundException in case of not existent legalDocumentType for the give id
     */
    void update(SystemLegalDocumentType legalDocumentType) throws UniquenessConstraintException, LegalDocumentTypeNotFoundException;

    /**
     * Deletes a unique LegalDocumentType selected by his id.
     * @param legalDocumentTypeId to be deleted.
     * @throws LegalDocumentTypeNotFoundException in case of not existent legalDocumentType for the give id
     */
    void delete(Long legalDocumentTypeId) throws LegalDocumentTypeNotFoundException;

}
