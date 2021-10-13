/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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
package ${package}.ms.client.services;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.entity.Page;
import io.radien.api.model.${entityResourceName.toLowerCase()}.System${entityResourceName};
import io.radien.api.service.${entityResourceName.toLowerCase()}.${entityResourceName}RESTServiceAccess;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.SystemException;
import io.radien.exception.TokenExpiredException;
import io.radien.ms.authz.security.AuthorizationChecker;
import ${package}.ms.client.${entityResourceName}ResponseExceptionMapper;
import ${package}.ms.client.entities.${entityResourceName};
import ${package}.ms.client.util.${entityResourceName}ClientServiceUtil;
import ${package}.ms.client.util.${entityResourceName}ModelMapper;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import org.apache.cxf.bus.extension.ExtensionException;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * ${entityResourceName} REST Service Client
 *
 * It means that the server will have a RESTful web service which would provide the required
 * functionality to the client. The client send's a request to the web service on the server.
 * The server would either reject the request or comply and provide an adequate response to the
 * client.
 *
 * @author Bruno Gama
 */
@RequestScoped
@RegisterProvider(${entityResourceName}ResponseExceptionMapper.class)
public class ${entityResourceName}RESTServiceClient extends AuthorizationChecker implements ${entityResourceName}RESTServiceAccess {
    private static final Logger log = LoggerFactory.getLogger(${entityResourceName}RESTServiceClient.class);

    @Inject
    private OAFAccess oaf;

    @Inject
    private ${entityResourceName}ClientServiceUtil ${entityResourceName.toLowerCase()}ClientServiceUtil;


    @Override
    public Page<? extends System${entityResourceName}> getAll(String search, int pageNo, int pageSize, List<String> sortBy,
                                                 boolean isAscending) throws MalformedURLException, SystemException {
        try {
            return getAllRequester(search, pageNo, pageSize, sortBy, isAscending);
        } catch (TokenExpiredException | SystemException expiredException) {
            refreshToken();
            try{
                return getAllRequester(search, pageNo, pageSize, sortBy, isAscending);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    private Page<? extends System${entityResourceName}> getAllRequester(String search, int pageNo, int pageSize,
                                                           List<String> sortBy, boolean isAscending) throws SystemException {
        try {
            ${entityResourceName}ResourceClient client = ${entityResourceName.toLowerCase()}ClientServiceUtil.get${entityResourceName}ResourceClient(getOAF()
                    .getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_${entityResourceName.toUpperCase()}MANAGEMENT));
            Response response = client.getAll(search, pageNo, pageSize, sortBy, isAscending);
            return ${entityResourceName}ModelMapper.mapToPage((InputStream) response.getEntity());
        } catch (ExtensionException | ProcessingException | MalformedURLException e){
            throw new SystemException(e);
        }
    }

    @Override
    public Optional<System${entityResourceName}> get${entityResourceName}ById(Long id) throws SystemException {
        try {
            return get${entityResourceName}ByIdRequester(id);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return get${entityResourceName}ByIdRequester(id);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    private Optional<System${entityResourceName}> get${entityResourceName}ByIdRequester(Long id) throws SystemException {
        try {
            ${entityResourceName}ResourceClient client = ${entityResourceName.toLowerCase()}ClientServiceUtil.get${entityResourceName}ResourceClient(getOAF()
                    .getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_${entityResourceName.toUpperCase()}MANAGEMENT));
            Response response = client.getById(id);
            return Optional.of(${entityResourceName}ModelMapper.map((InputStream) response.getEntity()));
        } catch (ExtensionException|ProcessingException | MalformedURLException e){
            throw new SystemException(e);
        }
    }

    @Override
    public boolean save(System${entityResourceName} ${entityResourceName.toLowerCase()}) throws SystemException {
        try {
            return saveRequester(${entityResourceName.toLowerCase()});
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return saveRequester(${entityResourceName.toLowerCase()});
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    private boolean saveRequester(System${entityResourceName} ${entityResourceName.toLowerCase()}) throws SystemException {
        ${entityResourceName}ResourceClient client;
        try {
            client = ${entityResourceName.toLowerCase()}ClientServiceUtil.get${entityResourceName}ResourceClient(getOAF()
                    .getProperty( OAFProperties.SYSTEM_MS_ENDPOINT_${entityResourceName.toUpperCase()}MANAGEMENT));
        } catch (MalformedURLException e) {
            throw new SystemException(e);
        }
        try (Response response = client.save((${entityResourceName}) ${entityResourceName.toLowerCase()})) {
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            } else {
                String entity = response.readEntity(String.class);
                log.error(entity);
                return false;
            }
        } catch (ProcessingException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public boolean delete${entityResourceName}(long ${entityResourceName.toLowerCase()}Id) throws SystemException {
        try {
            return deleteRequester(${entityResourceName.toLowerCase()}Id);
        } catch (TokenExpiredException | SystemException expiredException) {
            refreshToken();
            try{
                return deleteRequester(${entityResourceName.toLowerCase()}Id);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Deletes given ${entityResourceName.toLowerCase()}
     * @param ${entityResourceName.toLowerCase()}Id to be deleted
     * @return true if ${entityResourceName.toLowerCase()} has been deleted with success or false if not
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private boolean deleteRequester(long ${entityResourceName.toLowerCase()}Id) throws SystemException {
        ${entityResourceName}ResourceClient client;
        try {
            client = ${entityResourceName.toLowerCase()}ClientServiceUtil.get${entityResourceName}ResourceClient(getOAF()
                    .getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_${entityResourceName.toUpperCase()}MANAGEMENT));
        } catch (MalformedURLException e) {
            throw new SystemException(e);
        }
        try (Response response = client.delete(${entityResourceName.toLowerCase()}Id)) {
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            } else {
                String entity = response.readEntity(String.class);
                log.error(entity);
                return false;
            }
        } catch (ProcessingException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public boolean update${entityResourceName}(System${entityResourceName} ${entityResourceName.toLowerCase()}) {
        try {
            return update${entityResourceName}Requester(${entityResourceName.toLowerCase()});
        } catch (TokenExpiredException e) {
            try {
                refreshToken();
                return update${entityResourceName}Requester(${entityResourceName.toLowerCase()});
            } catch (SystemException | TokenExpiredException tokenExpiredException) {
                log.error(tokenExpiredException.getMessage(), tokenExpiredException);
            }
        }
        return false;
    }

    /**
     * Updates the given user information, will validate by the given user id since that one cannot change
     * @param ${entityResourceName.toLowerCase()} information to be updated
     * @return true in case of success
     * @throws TokenExpiredException in case of any issue while attempting communication with the client side
     */
    private boolean update${entityResourceName}Requester(System${entityResourceName} ${entityResourceName.toLowerCase()}) throws TokenExpiredException {
        try {
            ${entityResourceName}ResourceClient client = ${entityResourceName.toLowerCase()}ClientServiceUtil.get${entityResourceName}ResourceClient(getOAF()
                    .getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_${entityResourceName.toUpperCase()}MANAGEMENT));
            Response response = client.save((${entityResourceName}) ${entityResourceName.toLowerCase()} );
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            } else {
                String entity = response.readEntity(String.class);
                log.error(entity);
                return false;
            }
        } catch (MalformedURLException e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }


    public Long getTotalRecordsCount() throws SystemException {
        try {
            return getTotalRecordsCountRequester();
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getTotalRecordsCountRequester();
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(GenericErrorCodeMessage.EXPIRED_ACCESS_TOKEN.toString());
            }
        }
    }

    /**
     * Will calculate how many records are existent in the db
     * @return the count of existent ${entityResourceName.toLowerCase()}s.
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private Long getTotalRecordsCountRequester() throws SystemException {
        try {
            ${entityResourceName}ResourceClient client = ${entityResourceName.toLowerCase()}ClientServiceUtil.get${entityResourceName}ResourceClient(getOAF()
                    .getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_${entityResourceName.toUpperCase()}MANAGEMENT));

            Response response = client.getTotalRecordsCount();
            return Long.parseLong(response.readEntity(String.class));

        } catch (ExtensionException | ProcessingException | MalformedURLException e){
            throw new SystemException(e);
        }
    }

    /**
     * OAF ${entityResourceName.toLowerCase()} getter
     * @return the active ${entityResourceName.toLowerCase()} oaf
     */
    @Override
    public OAFAccess getOAF() {
        return oaf;
    }
}
