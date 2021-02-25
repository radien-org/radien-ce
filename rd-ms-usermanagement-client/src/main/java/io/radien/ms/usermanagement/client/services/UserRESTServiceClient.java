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
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import javax.json.Json;
import javax.json.JsonArray;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;

import io.radien.api.service.batch.BatchSummary;
import org.apache.cxf.bus.extension.ExtensionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.model.user.SystemUser;
import io.radien.api.service.user.UserRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.ms.usermanagement.client.util.ClientServiceUtil;
import io.radien.ms.usermanagement.client.util.ListUserModelMapper;

import static java.util.stream.Collectors.toList;

/**
 *
 * @author Bruno Gama
 * @author Nuno Santana
 * @author Marco Weiland
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
     * @throws SystemException in case it founds multiple users or if URL is malformed
     */
    public Optional<SystemUser> getUserBySub(String sub) throws SystemException {
        try {
            UserResourceClient client = clientServiceUtil.getUserResourceClient(getOAF().getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_USERMANAGEMENT));

            Response response = client.getUsers(sub,null,null,true,true);
            List<? extends SystemUser> list = ListUserModelMapper.map((InputStream) response.getEntity());
            if (list.size() == 1) {
                return Optional.ofNullable(list.get(0));
            } else {
                return Optional.empty();
            }
        } catch (ExtensionException|ProcessingException | MalformedURLException e){
            log.error(e.getMessage(),e);
            throw new SystemException(e);
        }
    }

    /**
     * Creates given user
     * @param user to be created
     * @return true if user has been created with success or false if not
     * @throws MalformedURLException in case of URL specification
     */
    public boolean create(SystemUser user,boolean skipKeycloak) throws SystemException {
    	UserResourceClient client;
		try {
			client = clientServiceUtil.getUserResourceClient(getOAF().getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_USERMANAGEMENT));
		} catch (MalformedURLException e) {
			log.error(e.getMessage(),e);
            throw new SystemException(e);
		}
		if(skipKeycloak) {
            user.setDelegatedCreation(true);
        }
        try (Response response = client.save((User)user)) {
            if(response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                return true;
            } else {
                log.error(response.readEntity(String.class));
                return false;
            }
        } catch (ProcessingException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public List<? extends SystemUser> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) throws MalformedURLException {
        UserResourceClient client = clientServiceUtil.getUserResourceClient(oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_USERMANAGEMENT));
        Response response = client.getAll(search, pageNo, pageSize, sortBy, isAscending);

        JsonArray jsonArray = (JsonArray) Json.createReader(new StringReader(response.readEntity(String.class))).readObject().get("results");
        List<User> listUsers = new ArrayList<>();
        try{
            if(!jsonArray.isEmpty()){
                for (int i=0; i<jsonArray.size(); i++) {
                    String firstname = jsonArray.getJsonObject(i).getString("firstname");
                    String lastname = jsonArray.getJsonObject(i).getString("lastname");
                    String logon = jsonArray.getJsonObject(i).getString("logon");
                    String userEmail = jsonArray.getJsonObject(i).getString("userEmail");
                    listUsers.add(new User(firstname,lastname,logon,userEmail));
                }
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        return listUsers;
    }

    /**
     * Adds multiple users into the DB.
     *
     * @param systemUsers of users to be added
     * @return returns Optional containing the process summary.<br>
     * There will be a summary when:<br>
     *  If ALL users were added without any issue (Http Status = 200)<br>
     *  If issues regarding some users were found (Http status = 202)<br>
     *  If none users were added, what means that all informed users contained issues (Http status = 400) <br>
     *  In case of 500 error the returned optional will be empty
     * and the found issues as well.
     */
    public Optional<BatchSummary> create(List<SystemUser> systemUsers) throws MalformedURLException {
        UserResourceClient client = clientServiceUtil.getUserResourceClient(oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_USERMANAGEMENT));
        List<User> users = systemUsers.stream().map(User.class::cast).collect(toList());
        try (Response response = client.create(users)) {
            if(response.getStatus() == Response.Status.OK.getStatusCode() ||
                response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
                return Optional.of(response.readEntity(BatchSummary.class));
            } else {
                log.error(response.readEntity(String.class));
                return Optional.empty();
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
