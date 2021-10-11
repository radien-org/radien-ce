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
package ${package}.services;

import java.net.MalformedURLException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;

import io.radien.exception.SystemException;
import ${package}.entities.${resource-name};
import ${package}.util.ClientServiceUtil;
import ${package}.services.${resource-name}RESTServiceClient;
import ${package}.services.${resource-name}ResourceClient;


/**
 *
 * @author Rajesh Gavvala
 * @author Nuno Santana
 */
@Stateless @Default
public class ${resource-name}RESTServiceClient implements ${resource-name}RESTServiceAccess {
	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(${resource-name}RESTServiceClient.class);

    @Inject
    private OAFAccess oaf;
    
    @Inject
    private ClientServiceUtil clientServiceUtil;
    

    public boolean create(System${resource-name} ${resource-name-variable}) throws SystemException {
        ${resource-name}ResourceClient client;
		try {
			client = clientServiceUtil.get${resource-name}ResourceClient(getOAF().getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_${resource-nameInUpperCase}MANAGEMENT));
		} catch (MalformedURLException e) {
			log.error(e.getMessage(),e);
            throw new SystemException(e);
		}        
        try (Response response = client.create((${resource-name}) ${resource-name-variable})) {
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            } else {
                log.error(response.readEntity(String.class));
                return false;
            }
        } catch (ProcessingException e) {
        	log.error(e.getMessage(),e);
            throw new SystemException(e);
        }
    }

	public boolean update(System${resource-name} ${resource-name-variable}) throws SystemException {
		${resource-name}ResourceClient client;
		try {
			client = clientServiceUtil.get${resource-name}ResourceClient(getOAF().getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_${resource-nameInUpperCase}MANAGEMENT));
		} catch (MalformedURLException e) {
			log.error(e.getMessage(),e);
			throw new SystemException(e);
		}
		try (Response response = client.update(${resource-name-variable}.getId(), (${resource-name}) ${resource-name-variable})) {
			if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
				return true;
			} else {
				log.error(response.readEntity(String.class));
				return false;
			}
		} catch (ProcessingException e) {
			log.error(e.getMessage(),e);
			throw new SystemException(e);
		}
    }

	public boolean delete(long ${resource-name-variable}Id) throws SystemException {
		${resource-name}ResourceClient client;
		try {
			client = clientServiceUtil.get${resource-name}ResourceClient(getOAF().getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_${resource-nameInUpperCase}MANAGEMENT));
		} catch (MalformedURLException e) {
			log.error(e.getMessage(),e);
			throw new SystemException(e);
		}
		try (Response response = client.delete(${resource-name-variable}Id)) {
			if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
				return true;
			} else {
				log.error(response.readEntity(String.class));
				return false;
			}
		} catch (ProcessingException e) {
			log.error(e.getMessage(),e);
			throw new SystemException(e);
		}
    }

	@Override
	public OAFAccess getOAF() {
		return oaf;
	}
}
