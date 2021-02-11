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
import java.util.Optional;

import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;

import io.radien.api.model.tenant.SystemContract;
import io.radien.api.service.contract.ContractRESTServiceAccess;
import org.apache.cxf.bus.extension.ExtensionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;

import io.radien.ms.tenantmanagement.client.entities.Contract;
import io.radien.ms.tenantmanagement.client.util.ClientServiceUtil;
import io.radien.ms.tenantmanagement.client.util.ListContractModelMapper;

/**
 * @author Santana
 */
@Stateless @Default
public class ContractRESTServiceClient implements ContractRESTServiceAccess {
	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(ContractRESTServiceClient.class);
    
    @Inject
    private ClientServiceUtil clientServiceUtil;

    @Inject
    private OAFAccess oafAccess;
    
    /**
     * Gets the contract in the DB searching for the field Name
     *
     * @param name to be looked after
     * @return Optional Contract
     */
    @Override
    public Optional<SystemContract> getContractByName(String name) throws MalformedURLException, ParseException, ProcessingException, ExtensionException {
        try {
            ContractResourceClient client = clientServiceUtil.getContractResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_CONTRACTMANAGEMENT));

            Response response = client.get(name);
            List<? extends SystemContract> list = ListContractModelMapper.map((InputStream) response.getEntity());
            if (list.size() == 1) {
                return Optional.ofNullable(list.get(0));
            } else {
                return Optional.empty();
            }
        }        catch (ExtensionException | ProcessingException | MalformedURLException | ParseException es ){
            log.error(es.getMessage(),es);
            throw es;
        }
    }

    @Override
    public List<? extends SystemContract> getAll() throws MalformedURLException, ParseException {
        try {
            ContractResourceClient client = clientServiceUtil.getContractResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_CONTRACTMANAGEMENT));

            Response response = client.get(null);
            return ListContractModelMapper.map((InputStream) response.getEntity());
        }        catch (ExtensionException | ProcessingException | MalformedURLException | ParseException es){
            log.error(es.getMessage(),es);
            throw es;
        }
    }

    /**
     * Creates given Contract
     * @param contract to be created
     * @return true if user has been created with success or false if not
     * @throws MalformedURLException in case of URL specification
     */
    @Override
    public boolean create(SystemContract contract) throws MalformedURLException {
        ContractResourceClient client = clientServiceUtil.getContractResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_CONTRACTMANAGEMENT));
        try (Response response = client.create((Contract)contract)) {
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            } else {
                log.error(response.readEntity(String.class));
                return false;
            }
        } catch (ProcessingException pe) {
            log.error(pe.getMessage(), pe);
            throw pe;
        }
    }

    @Override
    public boolean delete(long contractId) throws MalformedURLException {
        ContractResourceClient client = clientServiceUtil.getContractResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_CONTRACTMANAGEMENT));
        try (Response response = client.delete(contractId)) {
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            } else {
                log.error(response.readEntity(String.class));
                return false;
            }
        } catch (ProcessingException pe) {
            log.error(pe.getMessage(), pe);
            throw pe;
        }
    }

    @Override
    public boolean update(SystemContract contract) throws MalformedURLException {
        ContractResourceClient client = clientServiceUtil.getContractResourceClient(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_CONTRACTMANAGEMENT));
        try (Response response = client.update(contract.getId(),(Contract)contract)) {
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            } else {
                log.error(response.readEntity(String.class));
                return false;
            }
        } catch (ProcessingException pe) {
            log.error(pe.getMessage(), pe);
            throw pe;
        }
    }
}
