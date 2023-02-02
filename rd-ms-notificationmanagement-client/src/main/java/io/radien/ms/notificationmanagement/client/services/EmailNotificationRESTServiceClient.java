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
 * See the License for the specific language governing actions and
 * limitations under the License.
 */
package io.radien.ms.notificationmanagement.client.services;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.service.notification.email.EmailNotificationRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.notificationmanagement.client.util.ClientServiceUtil;
import java.net.MalformedURLException;
import java.util.Map;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

@RequestScoped
@Default
public class EmailNotificationRESTServiceClient extends AuthorizationChecker implements EmailNotificationRESTServiceAccess {

	private static final long serialVersionUID = 4416175112376323622L;

    @Inject
    private OAFAccess oaf;

    @Inject
    private ClientServiceUtil clientServiceUtil;

    private EmailNotificationResourceClient getResourceClient() throws MalformedURLException {
        return clientServiceUtil.getEmailNotificationResourceClient(
            oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_NOTIFICATIONMANAGEMENT)
        );
    }

    @Override
    public boolean notify(String email, String notificationViewId, String language, Map<String, String> arguments) throws SystemException {
        try {
            EmailNotificationResourceClient client = getResourceClient();
            Response response = client.notify(email, notificationViewId, language, arguments);
            return response.getStatus() == 200;
        } catch (MalformedURLException e){
            throw new SystemException(e.getMessage());
        }
    }

    /**
     * OAF action getter
     * @return the active action oaf
     */
    @Override
    public OAFAccess getOAF() {
        return oaf;
    }
}
