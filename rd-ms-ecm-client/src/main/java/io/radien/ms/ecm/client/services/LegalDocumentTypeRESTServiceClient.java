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
package io.radien.ms.ecm.client.services;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.entity.Page;
import io.radien.api.model.legal.SystemLegalDocumentType;
import io.radien.api.service.legal.LegalDocumentTypeRESTServiceAccess;
import io.radien.exception.BadRequestException;
import io.radien.exception.InternalServerErrorException;
import io.radien.exception.NotFoundException;
import io.radien.exception.SystemException;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.ecm.client.entities.legal.LegalDocumentType;
import io.radien.ms.ecm.client.util.ClientServiceUtil;
import io.radien.ms.ecm.client.util.LegalDocumentTypeMapper;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import org.apache.cxf.bus.extension.ExtensionException;

/**
 * Legal Document Type REST Service Client
 *
 * It means that the server will have a RESTful web service which would provide the required
 * functionality to the client. The client send's a request to the web service on the server.
 * The server would either reject the request or comply and provide an adequate response to the
 * client.
 *
 * This Rest service client will be responsible to Deal with Legal Document Type domain
 *
 * @author Newton Carvalho
 */
@ApplicationScoped
public class LegalDocumentTypeRESTServiceClient extends AuthorizationChecker
        implements LegalDocumentTypeRESTServiceAccess {

    private static final long serialVersionUID = 1245575026189286433L;

    @Inject
    private OAFAccess oaf;

    @Inject
    private ClientServiceUtil clientServiceUtil;

    /**
     * Retrieves a page object containing LegalDocumentTypes that matches search parameter.
     * In case of omitted (empty) search parameter retrieves ALL LegalDocumentTypes
     *
     * (To do this, invokes the core method counterpart and handles TokenExpiration error)
     *
     * @param search search parameter for matching LegalDocumentTypes (optional).
     * @param pageNo page number
     * @param pageSize page size
     * @param sortBy Sorting fields
     * @param isAscending Defines if ascending or descending in relation of sorting fields
     * @return Page containing SystemLegalDocumentType instances
     * @throws SystemException in case of any error
     */
    public Page<? extends SystemLegalDocumentType> getAll(String search, int pageNo, int pageSize,
                                                   List<String> sortBy, boolean isAscending) throws SystemException {
        return get(() -> this.getAllCore(search, pageNo, pageSize, sortBy, isAscending));
    }

    /**
     * Core method that retrieves a page object containing LegalDocumentTypes that matches search parameter.
     * @param search search parameter for matching LegalDocumentTypes (optional).
     * @param pageNo page number
     * @param pageSize page size
     * @param sortBy Sorting fields
     * @param isAscending Defines if ascending or descending in relation of sorting fields
     * @return Page containing SystemLegalDocumentType instances
     * @throws SystemException in case of any error
     */
    protected Page<? extends SystemLegalDocumentType> getAllCore(String search, int pageNo, int pageSize,
                                                   List<String> sortBy, boolean isAscending) throws SystemException {
        LegalDocumentTypeResourceClient client = getClient();
        try (Response response = client.getAll(search, pageNo, pageSize, sortBy, isAscending)) {
            return LegalDocumentTypeMapper.mapToPage((InputStream) response.getEntity());
        }
        catch (ExtensionException | ProcessingException | InternalServerErrorException e) {
            throw new SystemException(e);
        }
    }

    /**
     * Finds all LegalDocumentTypes that matches a search filter.
     *
     * (To do this, it invokes the core method counterpart and handles TokenExpiration error).
     *
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
    public List<? extends SystemLegalDocumentType> getLegalDocumentTypes(String name, Long tenantId,
                                                                         Boolean toBeShown, Boolean toBeAccepted, List<Long> ids,
                                                                         boolean isExact, boolean isLogicalConjunction) throws SystemException {
        return get(() -> this.getLegalDocumentTypesCore(name, tenantId, toBeShown, toBeAccepted, ids, isExact, isLogicalConjunction));
    }

    /**
     * Core method that Finds all LegalDocumentTypes that matches a search filter.
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
    protected List<? extends SystemLegalDocumentType> getLegalDocumentTypesCore(String name, Long tenantId,
                                                                         Boolean toBeShown, Boolean toBeAccepted, List<Long> ids,
                                                                         boolean isExact, boolean isLogicalConjunction) throws SystemException {
        LegalDocumentTypeResourceClient client = getClient();
        try (Response response = client.getLegalDocumentTypes(name, tenantId, toBeShown, toBeAccepted, ids, isExact, isLogicalConjunction)) {
            return LegalDocumentTypeMapper.mapToList((InputStream) response.getEntity());
        }
        catch (ExtensionException | ProcessingException | InternalServerErrorException e) {
            throw new SystemException(e);
        }
    }

    /**
     * Retrieves an LegalDocumentType by its identifier.
     *
     * (To do this, it invokes the core method counterpart and handles TokenExpiration error).
     *
     * @param id LegalDocumentType identifier
     * @return an Optional containing Legal Document Type (if exists), otherwise an empty one.
     * @throws SystemException in case of any error
     */
    public Optional<SystemLegalDocumentType> getById(Long id) throws SystemException {
        return get(this::getByIdCore, id);
    }

    /**
     * Core method that retrieves a LegalDocumentType by its identifier
     * @param id LegalDocumentType identifier
     * @return an Optional containing Legal Document Type (if exists), otherwise an empty one.
     * @throws SystemException in case of any error
     */
    protected Optional<SystemLegalDocumentType> getByIdCore(Long id) throws SystemException {
        LegalDocumentTypeResourceClient client = getClient();
        try (Response response = client.getById(id)){
            return Optional.of(LegalDocumentTypeMapper.map((InputStream) response.getEntity()));
        }
        catch (NotFoundException n) {
            return Optional.empty();
        }
        catch (ExtensionException | ProcessingException | BadRequestException | InternalServerErrorException e) {
            throw new SystemException(e);
        }
    }

    /**
     * Deletes an LegalDocumentType by its identifier.
     *
     * (To do this, it invokes the core method counterpart and handles TokenExpiration error).
     *
     * @param id LegalDocumentType identifier
     * @return Boolean indicating if operation was concluded successfully
     * @throws SystemException in case of any error
     */
    public Boolean delete(long id) throws SystemException {
        return get(this::deleteCore, id);
    }

    /**
     * Core method that Deletes an LegalDocumentType by its identifier.
     * @param id LegalDocumentType identifier
     * @return Boolean indicating if operation was concluded successfully
     * @throws SystemException in case of any error
     */
    protected Boolean deleteCore(long id) throws SystemException{
        LegalDocumentTypeResourceClient client = getClient();
        try (Response response = client.delete(id)){
            return response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL;
        }
        catch (ExtensionException | ProcessingException | BadRequestException | NotFoundException | InternalServerErrorException e) {
            throw new SystemException(e);
        }
    }

    /**
     * Creates a LegalDocumentType.
     *
     * (To do this, it invokes the core method counterpart and handles TokenExpiration error).
     *
     * @param legalDocumentType LegalDocumentType to be created
     * @return Boolean indicating if operation was concluded successfully
     * @throws SystemException in case of any error
     */
    public Boolean create(SystemLegalDocumentType legalDocumentType) throws SystemException {
        return get(this::createCore, legalDocumentType);
    }

    /**
     * Core method that creates a LegalDocumentType.
     * @param legalDocumentType LegalDocumentType to be created
     * @return Boolean indicating if operation was concluded successfully
     * @throws SystemException in case of any error
     */
    protected Boolean createCore(SystemLegalDocumentType legalDocumentType) throws SystemException {
        LegalDocumentTypeResourceClient client = getClient();
        try (Response response = client.create((LegalDocumentType) legalDocumentType)){
            return response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL;
        }
        catch (ExtensionException | ProcessingException | BadRequestException | InternalServerErrorException e) {
            throw new SystemException(e);
        }
    }

    /**
     * Updates a LegalDocumentType.
     *
     * (To do this, it invokes the core method counterpart and handles TokenExpiration error).
     *
     * @param legalDocumentType LegalDocumentType to be updated
     * @return Boolean indicating if operation was concluded successfully
     * @throws SystemException in case of any error
     */
    public Boolean update(SystemLegalDocumentType legalDocumentType) throws SystemException {
        return get(this::updateCore, legalDocumentType);
    }

    /**
     * Core method that updates a LegalDocumentType.
     * @param legalDocumentType LegalDocumentType to be updated
     * @return Boolean indicating if operation was concluded successfully
     * @throws SystemException in case of any error
     */
    protected Boolean updateCore(SystemLegalDocumentType legalDocumentType) throws SystemException {
        LegalDocumentTypeResourceClient client = getClient();
        try (Response response = client.update(legalDocumentType.getId(), (LegalDocumentType) legalDocumentType)){
            return response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL;
        }
        catch (ExtensionException | ProcessingException | BadRequestException | NotFoundException | InternalServerErrorException e) {
            throw new SystemException(e);
        }
    }

    /**
     * Assemblies a LegalDocumentType REST Client using Rest client builder API,
     * using as parameter the URL informed via {@link OAFProperties#CMS_MS_URL}
     * @return instance of {@link LegalDocumentTypeResourceClient} REST Client
     * @throws SystemException in case of malformed url
     */
    private LegalDocumentTypeResourceClient getClient() throws SystemException {
        try {
            return clientServiceUtil.getLegalDocumentTypeClient(oaf.getProperty(
                    OAFProperties.SYSTEM_MS_ENDPOINT_ECM));
        } catch (MalformedURLException e) {
            throw new SystemException(e);
        }
    }

}
