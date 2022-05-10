/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
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

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.entity.Page;
import io.radien.api.model.jobshop.SystemJobshopItem;
import io.radien.api.model.jobshop.SystemStudentIdentity;
import io.radien.api.service.user.JobShopRESTServiceAccess;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.SystemException;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.usermanagement.client.UserResponseExceptionMapper;
import io.radien.ms.usermanagement.client.entities.JobshopItem;
import io.radien.ms.usermanagement.client.entities.StudentIdentity;
import io.radien.ms.usermanagement.client.util.ClientServiceUtil;
import io.radien.ms.usermanagement.client.util.JobshopModelMapper;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;

/**
 * User management service client for Rest Service Client regarding user domain object
 *
 * @author Bruno Gama
 * @author Nuno Santana
 * @author Marco Weiland
 */
@RequestScoped
@RegisterProvider(UserResponseExceptionMapper.class)
public class JobshopRESTServiceClient extends AuthorizationChecker implements JobShopRESTServiceAccess {
    private static final long serialVersionUID = 7871265016216355739L;

    private static final Logger log = LoggerFactory.getLogger(JobshopRESTServiceClient.class);

    @Inject
    private OAFAccess oaf;

    @Inject
    private ClientServiceUtil clientServiceUtil;

    private JobshopResourceClient getClient() throws MalformedURLException {
        return clientServiceUtil.getStudentIdentityResourceClient(getOAF().getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_USERMANAGEMENT));
    }

    @Override
    public OAFAccess getOAF() {
        return getOafAccess();
    }


    @Override
    public Page<? extends SystemStudentIdentity> getAllStudent(String nameFilter, int pageNo, int pageSize) throws SystemException {
        return get(() -> {
            try {
                JobshopResourceClient client = getClient();
                Response response = client.getAllIdentities(nameFilter, pageNo, pageSize);
                return JobshopModelMapper.mapStudentIdentityToPage((InputStream) response.getEntity());
            } catch (IOException e) {
                throw new SystemException(GenericErrorCodeMessage.ERROR_RETRIEVING_TICKETS.toString(), e);
            }
        });
    }

    @Override
    public List<? extends SystemJobshopItem> getAllItem() throws SystemException {
        return get(() -> {
            try {
                JobshopResourceClient client = getClient();
                return JobshopModelMapper.mapJobshopItemList((InputStream) client.getAllItems().getEntity());
            } catch (IOException e) {
                throw new SystemException(GenericErrorCodeMessage.ERROR_CREATING_TICKET.toString(), e);
            }
        });
    }

    @Override
    public boolean create(SystemStudentIdentity student) throws SystemException {
        return get(() -> {
            try {
                JobshopResourceClient client = getClient();
                Response response = client.saveIdentity((StudentIdentity) student);
                return checkBooleanResponse(response);
            } catch (IOException e) {
                throw new SystemException(GenericErrorCodeMessage.ERROR_CREATING_TICKET.toString(), e);
            }
        });
    }

    @Override
    public boolean create(SystemJobshopItem item) throws SystemException {
        return get(() -> {
            try {
                JobshopResourceClient client = getClient();
                Response response = client.saveItem((JobshopItem) item);
                return checkBooleanResponse(response);
            } catch (IOException e) {
                throw new SystemException(GenericErrorCodeMessage.ERROR_CREATING_TICKET.toString(), e);
            }
        });
    }

    private Boolean checkBooleanResponse(Response response) {
        if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
            return true;
        } else {
            String entity = response.readEntity(String.class);
            log.error(entity);
            return false;
        }
    }
}
