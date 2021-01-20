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
package io.radien.ms.usermanagement.client.services;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;

import org.apache.cxf.bus.extension.ExtensionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.model.user.SystemUser;
import io.radien.api.service.user.UserRESTServiceAccess;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.ms.usermanagement.client.util.ClientServiceUtil;
import io.radien.ms.usermanagement.client.util.ListUserModelMapper;

/**
 *
 * @author Bruno Gama
 * @author Nuno Santana
 */
@Stateless @Default
public class UserRESTServiceClient implements UserRESTServiceAccess {
	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(UserRESTServiceClient.class);

    @Inject
    private OAFAccess oaf;
    
    @Inject
    private ClientServiceUtil clientServiceUtil;
    
    /**
     * Gets all the users in the DB searching for the field Subject
     *
     * @param sub to be looked after
     * @return Optional List of Users
     * @throws Exception in case it founds multiple users or if URL is malformed
     */
    public Optional<SystemUser> getUserBySub(String sub) throws Exception {
        try {
            UserResourceClient client = clientServiceUtil.getUserResourceClient(getOAF().getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_USERMANAGEMENT));

            Response response = client.getUsers(sub,null,null,true,true);
            List<? extends SystemUser> list = ListUserModelMapper.map((InputStream) response.getEntity());
            if (list.size() == 1) {
                return Optional.ofNullable(list.get(0));
            } else {
                return Optional.empty();
            }
        }        catch (ExtensionException|ProcessingException es){
            log.error(es.getMessage(),es);
            throw es;
        }
    }

    /**
     * Creates given user
     * @param user to be created
     * @return true if user has been created with success or false if not
     * @throws MalformedURLException in case of URL specification
     */
    public boolean create(SystemUser user) throws MalformedURLException {
        UserResourceClient client = clientServiceUtil.getUserResourceClient(getOAF().getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_USERMANAGEMENT));
        try (Response response = client.save((User)user)) {
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
	public OAFAccess getOAF() {
		return oaf;
	}
}
