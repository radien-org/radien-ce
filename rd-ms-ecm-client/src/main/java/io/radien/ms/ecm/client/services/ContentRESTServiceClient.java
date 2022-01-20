/*
 *
 *  * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
 *  * <p>
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  * <p>
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  * <p>
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package io.radien.ms.ecm.client.services;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.service.ecm.ContentRESTServiceAccess;
import io.radien.api.service.ecm.model.ContentType;
import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.exception.SystemException;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.ecm.client.controller.ContentResource;
import io.radien.ms.ecm.client.entities.DeleteContentFilter;
import io.radien.ms.ecm.client.util.ClientServiceUtil;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestScoped
public class ContentRESTServiceClient extends AuthorizationChecker implements ContentRESTServiceAccess {
    private static final Logger log = LoggerFactory.getLogger(ContentRESTServiceClient.class);

    @Inject
    private ClientServiceUtil clientServiceUtil;
    @Inject
    private OAFAccess oaf;

    @Override
    public EnterpriseContent getByViewIdAndLanguage(String viewId, String language) throws SystemException {
        return get(() -> {
            try {
                ContentResource client = getClient();
                Response response = client.getContentByViewIdLanguage(viewId, language);
                return parseResponseForEnterpriseContent(response);
            } catch (IOException | ParseException | java.text.ParseException e) {
                throw new SystemException(MessageFormat.format("Error getting content by view ID and Language {0}, {1}", viewId, language), e);
            }
        });
    }

    @Override
    public EnterpriseContent getFileContent(String jcrAbsolutePath) throws SystemException {
        return get(() -> {
            try {
                ContentResource client = getClient();
                Response response = client.getContentFile(jcrAbsolutePath);
                return parseResponseForEnterpriseContent(response);
            } catch (IOException | ParseException | java.text.ParseException e) {
                throw new SystemException(MessageFormat.format("Error getting file content by Path {0}", jcrAbsolutePath), e);
            }
        });
    }

    @Override
    public List<EnterpriseContent> getFolderContents(String jcrAbsolutePath) throws SystemException {
        return get(() -> {
            try {
                ContentResource client = getClient();
                Response response = client.getFolderContents(jcrAbsolutePath);
                return parseResponseForMultipleEnterpriseContent(response);
            } catch (IOException | ParseException | java.text.ParseException e) {
                throw new SystemException(MessageFormat.format("Error getting folder contents by Path {0}", jcrAbsolutePath), e);
            }
        });
    }

    @Override
    public String getOrCreateDocumentsPath(String jcrRelativePath) throws SystemException {
        return get(() -> {
            try {
                ContentResource client = getClient();
                Response response = client.getOrCreateDocumentsPath(jcrRelativePath);
                String entity = response.readEntity(String.class);
                if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                    return entity;
                } else {
                    log.error(entity);
                    return null;
                }
            } catch (IOException e) {
                throw new SystemException(MessageFormat.format("Error getting or creating documents Path {0}", jcrRelativePath), e);
            }
        });
    }

    @Override
    public boolean saveContent(EnterpriseContent enterpriseContent) throws SystemException {
        return get(() -> {
            try {
                ContentResource client = getClient();
                Response response = client.saveContent(enterpriseContent);
                if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                    return true;
                } else {
                    String entity = response.readEntity(String.class);
                    log.error(entity);
                    return false;
                }
            } catch (IOException e) {
                throw new SystemException("Error saving enterprise content", e);
            }
        });
    }

    @Override
    public boolean deleteContentByPath(String absolutePath) throws SystemException {
        return get(() -> {
            try {
                ContentResource client = getClient();
                Response response = client.deleteContent(new DeleteContentFilter(absolutePath));
                if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                    return true;
                } else {
                    String entity = response.readEntity(String.class);
                    log.error(entity);
                    return false;
                }
            } catch (IOException e) {
                throw new SystemException(MessageFormat.format("Error deleting by path {0}", absolutePath), e);
            }
        });
    }

    @Override
    public boolean deleteContentByViewIDLanguage(String viewId, String language) throws SystemException {
        return get(() -> {
            try {
                ContentResource client = getClient();
                Response response = client.deleteContent(new DeleteContentFilter(viewId, language));
                if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                    return true;
                } else {
                    String entity = response.readEntity(String.class);
                    log.error(entity);
                    return false;
                }
            } catch (IOException e) {
                throw new SystemException(MessageFormat.format("Error deleting by viewID and Language {0} - {1}", viewId, language), e);
            }
        });
    }


    private ContentResource getClient() throws MalformedURLException {
        return clientServiceUtil.getResourceClient(oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ECM));
    }

    private EnterpriseContent parseResponseForEnterpriseContent(Response response) throws IOException, ParseException, java.text.ParseException {
        if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
            EnterpriseContent result = EnterpriseContentMapper.map((InputStream) response.getEntity());
            if (result.getContentType() == ContentType.ERROR) {
                log.error("Result not retrieved correctly from CMS System. Please check CMS logs for the error message");
            }
            return result;
        } else {
            String entity = response.readEntity(String.class);
            log.error(entity);
            return null;
        }
    }

    private List<EnterpriseContent> parseResponseForMultipleEnterpriseContent(Response response) throws IOException, ParseException, java.text.ParseException {
        if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
            return EnterpriseContentMapper.mapArray((InputStream) response.getEntity());
        } else {
            String entity = response.readEntity(String.class);
            log.error(entity);
            return new ArrayList<>();
        }
    }

    @Override
    public OAFAccess getOAF() {
        return oaf;
    }
}
