/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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
package io.radien.ms.tenantmanagement.client.services;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.List;

import javax.enterprise.context.RequestScoped;

import javax.inject.Inject;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;

import io.radien.api.entity.Page;
import io.radien.api.model.tenant.SystemContract;
import io.radien.api.service.tenant.ContractRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.exception.TokenExpiredException;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.tenantmanagement.client.entities.Contract;
import io.radien.ms.tenantmanagement.client.util.ContractModelMapper;
import io.radien.ms.tenantmanagement.client.util.ListContractModelMapper;
import org.apache.cxf.bus.extension.ExtensionException;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;

import io.radien.ms.tenantmanagement.client.util.ClientServiceUtil;

/**
 * Contract REST service requests to the client
 *
 * @author Santana
 */
@RequestScoped
public class ContractRESTServiceClient extends AuthorizationChecker implements ContractRESTServiceAccess {
    private static final long serialVersionUID = 7576466262027147334L;

    private static final String UNABLE_TO_RECOVER_TOKEN = "Unable to recover expiredToken";
    @Inject
    private ClientServiceUtil clientServiceUtil;

    @Inject
    private OAFAccess oafAccess;

    /**
     * Calls the requester to the contract client requesting all the contracts with a specific name
     * if not able will refresh the access token and retry
     * @param name of the contract to be retrieved
     * @return true in case of success response
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    @Override
    public List<? extends SystemContract> getContractByName(String name) throws SystemException {
        try {
            return getContractByNameRequest(name);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getContractByNameRequest(name);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(UNABLE_TO_RECOVER_TOKEN);
            }
        }
    }

    /**
     * Send a request to the contract client requesting all the contracts with a specific name
     * @param name of the contract to be retrieved
     * @return true in case of success response
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private List<? extends SystemContract> getContractByNameRequest(String name) throws SystemException {
        try {
            ContractResourceClient client = clientServiceUtil.getContractResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT));

            Response response = client.get(name);
            return ListContractModelMapper.map((InputStream) response.getEntity());
        } catch (ExtensionException | ProcessingException | MalformedURLException | ParseException e){
            throw new SystemException(e.getMessage());
        }
    }

    /**
     * Calls the requester to make a creation request to the contract client if not possible will refresh the
     * access token and retry
     * @param systemContract to be created
     * @return true in case of success response
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    @Override
    public boolean create(SystemContract systemContract) throws SystemException {
        try {
            return createRequester(systemContract);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return createRequester(systemContract);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(UNABLE_TO_RECOVER_TOKEN);
            }
        }
    }

    /**
     * Sends a creation request to the contract client
     * @param contract to be created
     * @return true in case of success response
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private boolean createRequester(SystemContract contract) throws SystemException {
        ContractResourceClient client;
        try {
            client = clientServiceUtil.
                    getContractResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT));
        } catch(MalformedURLException e) {
            throw new SystemException(e.getMessage());
        }

        try (Response response = client.create((Contract) contract)){
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            }
        } catch(ProcessingException e) {
            throw new SystemException(e.getMessage());
        }
        return false;
    }

    /**
     * Calls requester to the contract client an update if not possible will refresh token and retry
     * @param contractId to be updated
     * @param systemContract with the information to be update
     * @return true in case of success response
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    @Override
    public boolean update(Long contractId, SystemContract systemContract) throws SystemException {
        try {
            return updateRequester(contractId, systemContract);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return updateRequester(contractId, systemContract);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(UNABLE_TO_RECOVER_TOKEN);
            }
        }
    }

    /**
     * Requests to the contract client an update
     * @param contractId to be updated
     * @param systemContract with the information to be update
     * @return true in case of success response
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private boolean updateRequester(Long contractId, SystemContract systemContract) throws SystemException {
        ContractResourceClient client;
        try {
            client = clientServiceUtil.
                    getContractResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT));
        } catch(MalformedURLException e) {
            throw new SystemException(e.getMessage());
        }

        try {
            Response response = client.update(contractId, (Contract) systemContract);
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            }
        } catch(ProcessingException e) {
            throw new SystemException(e.getMessage());
        }
        return false;
    }

    /**
     * Calls the requester to get all the contracts existent in the data base into a paginated mode
     * if not possible will refresh the access token and retry
     * @param pageNo of the data to be visualized
     * @param pageSize is the max size of pages regarding the existent data to be checked
     * @return a list of System contracts to be used
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    @Override
    public Page<? extends SystemContract> getAll(int pageNo, int pageSize) throws SystemException {
        try {
            return getAlRequester(pageNo, pageSize);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getAlRequester(pageNo, pageSize);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(UNABLE_TO_RECOVER_TOKEN);
            }
        }
    }

    /**
     * Retrieves all the contracts existent in the data base into a paginated mode
     * @param pageNo of the data to be visualized
     * @param pageSize is the max size of pages regarding the existent data to be checked
     * @return a list of System contracts to be used
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private Page<? extends SystemContract> getAlRequester(int pageNo, int pageSize) throws SystemException {
        try {
            ContractResourceClient client = clientServiceUtil.getContractResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT));
            Response response = client.getAll(pageNo, pageSize);
            return ContractModelMapper.mapToPage((InputStream) response.getEntity());
        } catch (ExtensionException | ProcessingException | MalformedURLException e){
            throw new SystemException(e.getMessage());
        }
    }

    /**
     * Calls the requester to calculate how many records are existent in the db if not possible will refresh the
     * access token and retry
     * @return the count of existent contracts.
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    public Long getTotalRecordsCount() throws SystemException {
        try {
            return getTotalRecordsCountRequester();
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return getTotalRecordsCountRequester();
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(UNABLE_TO_RECOVER_TOKEN);
            }
        }
    }

    /**
     * Will calculate how many records are existent in the db
     * @return the count of existent contracts.
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private Long getTotalRecordsCountRequester() throws SystemException {
        try {
            ContractResourceClient client = clientServiceUtil.getContractResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT));

            Response response = client.getTotalRecordsCount();
            return Long.parseLong(response.readEntity(String.class));

        } catch (ExtensionException | ProcessingException | MalformedURLException e){
            throw new SystemException(e);
        }
    }

    /**
     * Will call the requester to request to the contract client to validate if a specific contract exists
     * if not possible will refresh the access token and retry
     * @param contractId to be found
     * @return true in case of success response
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    @Override
    public boolean isContractExistent(Long contractId) throws SystemException {
        try {
            return isContractExistentRequester(contractId);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return isContractExistentRequester(contractId);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(UNABLE_TO_RECOVER_TOKEN);
            }
        }
    }

    /**
     * Sends a request to the contract client to validate if a specific contract exists
     * @param contractId to be found
     * @return true in case of success response
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private boolean isContractExistentRequester(Long contractId) throws SystemException {
        ContractResourceClient client;
        try {
            client = clientServiceUtil.
                    getContractResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT));
            Response response = client.exists(contractId);
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            }
        } catch(ProcessingException | MalformedURLException e) {
            throw new SystemException(e.getMessage());
        }
        return false;
    }

    /**
     * Will call the requester to delete a specific contract if not possible will refresh the access token and retry
     * @param contractId to be found
     * @return true in case of success response
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    @Override
    public boolean delete(Long contractId) throws SystemException {
        try {
            return deleteRequest(contractId);
        } catch (TokenExpiredException expiredException) {
            refreshToken();
            try{
                return deleteRequest(contractId);
            } catch (TokenExpiredException expiredException1){
                throw new SystemException(UNABLE_TO_RECOVER_TOKEN);
            }
        }
    }

    /**
     * Sends a request to delete a specific contract if not possible will refresh the access token and retry
     * @param contractId to be found
     * @return true in case of success response
     * @throws SystemException in case it founds multiple actions or if URL is malformed
     */
    private boolean deleteRequest(Long contractId) throws SystemException {
        ContractResourceClient client;
        try {
            client = clientServiceUtil.
                    getContractResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT));
            Response response = client.delete(contractId);
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            }
        } catch(ProcessingException | MalformedURLException e) {
            throw new SystemException(e.getMessage());
        }
        return false;
    }

}
