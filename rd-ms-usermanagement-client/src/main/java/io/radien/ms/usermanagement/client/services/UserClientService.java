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

import io.radien.api.Configurable;
import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.ms.usermanagement.client.entities.Page;
import io.radien.ms.usermanagement.client.exceptions.ErrorCodeMessage;
import io.radien.ms.usermanagement.client.util.ClientServiceUtil;
import io.radien.ms.usermanagement.client.util.PageModelMapper;
import org.apache.cxf.bus.extension.ExtensionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Bruno Gama
 * @author Nuno Santana
 */
@Stateless
public class UserClientService {
    private static final Logger log = LoggerFactory.getLogger(UserClientService.class);


    @Inject
    private Configurable configurable;
    @Inject
    private ClientServiceUtil clientServiceUtil;
    /**
     * Gets all the users in the DB searching for the field Subject
     *
     * @param sub to be looked after
     * @return Optional List of Users
     * @throws Exception in case it founds multiple users or if URL is malformed
     */
    public Optional<User> getUserBySub(String sub) throws Exception {
        try {
            UserResourceClient client = clientServiceUtil.getUserResourceClient(configurable.getProperty(OAFProperties.USER_MANAGEMENT_MS_URL));
            try {
                Response response = client.getAll(Collections.singletonList(sub), null, null, 1, 2, null, null, null);
                Page<User> page = PageModelMapper.map((InputStream) response.getEntity());
                if (page.getTotalResults() == 1) {
                    List<User> users = page.getResults();
                    return Optional.ofNullable(users.get(0));
                } else if (page.getTotalResults() == 0) {
                    return Optional.empty();
                } else {
                    log.error("The subject field should and needs to be unique, this restriction doesn't seem to be met. Please revalidate the sub field.");
                    throw new Exception(ErrorCodeMessage.GENERIC_ERROR.toString());
                }
            } catch (ProcessingException pe) {
                log.error(pe.getMessage(), pe);
                throw pe;
            }
        }        catch (ExtensionException es){
            log.error(es.getMessage(),es);
            throw es;
        }
        //ProcessingException
    }

    /**
     * Creates given user
     * @param user to be created
     * @return true if user has been created with success or false if not
     * @throws MalformedURLException in case of URL specification
     */
    public boolean create(User user) throws MalformedURLException {
        UserResourceClient client = clientServiceUtil.getUserResourceClient(configurable.getProperty(OAFProperties.USER_MANAGEMENT_MS_URL));
        try (Response response = client.create(user)) {
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
