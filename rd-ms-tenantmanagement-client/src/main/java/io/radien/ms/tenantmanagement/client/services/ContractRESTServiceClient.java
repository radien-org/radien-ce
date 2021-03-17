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
import io.radien.ms.tenantmanagement.client.entities.Contract;
import io.radien.ms.tenantmanagement.client.util.ContractModelMapper;
import io.radien.ms.tenantmanagement.client.util.ListContractModelMapper;
import org.apache.cxf.bus.extension.ExtensionException;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;

import io.radien.ms.tenantmanagement.client.util.ClientServiceUtil;

/**
 * @author Santana
 */
@RequestScoped
public class ContractRESTServiceClient implements ContractRESTServiceAccess {
    private static final long serialVersionUID = 7576466262027147334L;

    @Inject
    private ClientServiceUtil clientServiceUtil;

    @Inject
    private OAFAccess oafAccess;

    /**
     * Send a request to the contract client requesting all the contracts with a specific name
     * @param name of the contract to be retrieved
     * @return true in case of success response
     * @throws Exception in case of any issue
     */
    @Override
    public List<? extends SystemContract> getContractByName(String name) throws Exception {
        try {
            ContractResourceClient client = clientServiceUtil.getContractResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT));

            Response response = client.get(name);
            return ListContractModelMapper.map((InputStream) response.getEntity());
        } catch (ExtensionException |ProcessingException | MalformedURLException e){
            throw new SystemException(e);
        }
    }

    /**
     * Sends a creation request to the contract client
     * @param systemContract to be created
     * @return true in case of success response
     * @throws MalformedURLException in case of any error in the specified url
     */
    @Override
    public boolean create(SystemContract systemContract) throws MalformedURLException {
        ContractResourceClient client;
        try {
            client = clientServiceUtil.
                    getContractResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT));
        } catch(MalformedURLException e) {
            throw new MalformedURLException();
        }

        try {
            Response response = client.create((Contract) systemContract);
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            }
        } catch(ProcessingException e) {
            throw new ProcessingException(e);
        }
        return false;
    }

    /**
     * Sends a request to the contract client to delete a specific record
     * @param contractId id of the contract to be deleted
     * @return true in case of success response
     * @throws MalformedURLException in case of any error in the specified url
     */
    @Override
    public boolean delete(long contractId) throws MalformedURLException {
        ContractResourceClient client;
        try {
            client = clientServiceUtil.
                    getContractResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT));
        } catch(MalformedURLException e) {
            throw new MalformedURLException();
        }

        try {
            Response response = client.delete(contractId);
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            }
        } catch(ProcessingException e) {
            throw new ProcessingException(e);
        }
        return false;
    }

    /**
     * Requests to the contract client an update
     * @param contractId to be updated
     * @param systemContract with the information to be update
     * @return true in case of success response
     * @throws MalformedURLException in case of any error in the specified url
     */
    @Override
    public boolean update(Long contractId, SystemContract systemContract) throws MalformedURLException {
        ContractResourceClient client;
        try {
            client = clientServiceUtil.
                    getContractResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT));
        } catch(MalformedURLException e) {
            throw new MalformedURLException();
        }

        try {
            Response response = client.update(contractId, (Contract) systemContract);
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            }
        } catch(ProcessingException e) {
            throw new ProcessingException(e);
        }
        return false;
    }

    /**
     * Retrieves all the contracts existent in the data base into a paginated mode
     * @param pageNo of the data to be visualized
     * @param pageSize is the max size of pages regarding the existent data to be checked
     * @return a list of System contracts to be used
     * @throws MalformedURLException in case of URL specification
     * @throws ParseException in case of parsing or constructing the response
     */
    @Override
    public Page<? extends SystemContract> getAll(int pageNo, int pageSize) throws SystemException, MalformedURLException {
        try {
            ContractResourceClient client = clientServiceUtil.getContractResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT));
            Response response = client.getAll(pageNo, pageSize);
            return ContractModelMapper.mapToPage((InputStream) response.getEntity());
        } catch (ExtensionException | ProcessingException | MalformedURLException e){
            throw new SystemException(e);
        }
    }

    /**
     * Will calculate how many records are existent in the db
     * @return the count of existent contracts.
     */
    public Long getTotalRecordsCount() throws SystemException {
        try {
            ContractResourceClient client = clientServiceUtil.getContractResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT));

            Response response = client.getTotalRecordsCount();
            return Long.parseLong(response.readEntity(String.class));

        } catch (ExtensionException | ProcessingException | MalformedURLException e){
            throw new SystemException(e);
        }
    }

    /**
     * Sends a request to the contract client to validdate if a specific contract exists
     * @param contractId to be found
     * @return true in case of success response
     * @throws MalformedURLException in case of any error in the specified url
     */
    @Override
    public boolean isContractExistent(Long contractId) throws MalformedURLException {
        ContractResourceClient client;
        try {
            client = clientServiceUtil.
                    getContractResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT));
        } catch(MalformedURLException e) {
            throw new MalformedURLException();
        }

        try {
            Response response = client.exists(contractId);
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            }
        } catch(ProcessingException e) {
            throw new ProcessingException(e);
        }
        return false;
    }
}
