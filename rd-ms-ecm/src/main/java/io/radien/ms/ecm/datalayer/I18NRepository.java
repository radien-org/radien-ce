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

package io.radien.ms.ecm.datalayer;

import com.google.common.collect.Lists;
import io.radien.api.OAFProperties;
import io.radien.api.entity.Page;
import io.radien.api.model.i18n.SystemI18NProperty;
import io.radien.exception.SystemException;
import io.radien.ms.ecm.constants.CmsConstants;

import io.radien.ms.ecm.util.i18n.JCRQueryBuilder;
import io.radien.ms.ecm.util.i18n.PropertyMappingUtils;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.commons.JcrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class I18NRepository extends JCRRepository {
    private static final Logger log = LoggerFactory.getLogger(I18NRepository.class);

    private static final String QUERY_FROM_ALIAS = "node";
    private static final String QUERY_JOIN_ALIAS = "child";
    private static final String FROM_QUERY_EQUALITY = QUERY_FROM_ALIAS + ".[{0}] = ''{1}''";
    private static final String JOIN_QUERY_EQUALITY = QUERY_JOIN_ALIAS + ".[{0}] = ''{1}''";

    @Inject
    private PropertyMappingUtils mappingUtils;

    public String getTranslation(String key, String language, String application) throws IllegalStateException, SystemException {
        Session session = createSession();
        QueryManager queryManager;
        String result = key;
        try {
            queryManager = session.getWorkspace().getQueryManager();
            String queryExpression = new JCRQueryBuilder()
                    .addFrom(CmsConstants.RADIEN_PROPERTY_NODE_TYPE, QUERY_FROM_ALIAS)
                    .addInnerJoin(CmsConstants.RADIEN_TRANSLATION_NODE_TYPE, QUERY_JOIN_ALIAS)
                    .addWhere(MessageFormat.format(FROM_QUERY_EQUALITY, CmsConstants.PROPERTY_KEY, key))
                    .addWhere(MessageFormat.format(FROM_QUERY_EQUALITY, CmsConstants.PROPERTY_APPLICATION, application))
                    .addWhere(MessageFormat.format(JOIN_QUERY_EQUALITY, CmsConstants.PROPERTY_LANGUAGE, language))
                    .build();
            Query query = queryManager.createQuery(queryExpression, Query.JCR_SQL2);
            NodeIterator nodes = query.execute().getNodes();
            while(nodes.hasNext()) {
                if(result.equals(key)) {
                    result = nodes.nextNode()
                            .getNode(language)
                            .getProperty(CmsConstants.PROPERTY_VALUE)
                            .getString();
                } else {
                    log.warn("Multiple nodes found for {} {} {}", key, language, application);
                }
            }
            return result;
        } catch (RepositoryException e) {
            throw new SystemException(MessageFormat.format("Error retrieving translation for {0}-{1} in {2}", application, key, language),
                    e);
        } finally {
            session.logout();
        }
    }

    public void save(SystemI18NProperty entity) throws SystemException {
        Session session = createSession();
        try {
            Node rootNode = session.getRootNode().getNode(getOAF().getProperty(OAFProperties.SYSTEM_CMS_CFG_NODE_ROOT))
                    .getNode(getOAF().getProperty(OAFProperties.SYSTEM_CMS_CFG_NODE_PROPERTIES));
            if(JcrUtils.getNodeIfExists(rootNode, entity.getApplication()) == null) {
                rootNode = rootNode.addNode(entity.getApplication(), JcrConstants.NT_FOLDER);
            } else {
                rootNode = JcrUtils.getNodeIfExists(rootNode, entity.getApplication());
            }
            Node propertyNode = JcrUtils.getNodeIfExists(rootNode, entity.getKey());
            mappingUtils.syncNode(rootNode, propertyNode, entity);

            session.save();
        } catch (RepositoryException e) {
            throw new SystemException(MessageFormat.format("Error saving property {0}", entity.getKey()), e);
        } finally {
            session.logout();
        }
    }

    public void deleteProperty(SystemI18NProperty property) throws SystemException {
        Session session = createSession();
        try {
            String path = calculatePath(property);
            session.removeItem(path);
            session.save();
        } catch (RepositoryException e) {
            throw new SystemException(MessageFormat.format("Error deleting property {0}", property.getKey()), e);
        } finally {
            session.logout();
        }
    }

    public void deleteApplication(String application) throws SystemException {
        Session session = createSession();
        QueryManager queryManager;
        try {
            queryManager = session.getWorkspace().getQueryManager();
            String queryExpression = new JCRQueryBuilder()
                    .addFrom(CmsConstants.RADIEN_PROPERTY_NODE_TYPE, QUERY_FROM_ALIAS)
                    .addWhere(MessageFormat.format(FROM_QUERY_EQUALITY, CmsConstants.PROPERTY_APPLICATION, application))
                    .build();
            Query query = queryManager.createQuery(queryExpression, Query.JCR_SQL2);
            NodeIterator nodes = query.execute().getNodes();
            while(nodes.hasNext()) {
                String path = nodes.nextNode().getPath();
                session.removeItem(path);
                log.info("Property {} deleted", path);
            }
            session.save();
        } catch (RepositoryException e) {
            throw new SystemException(MessageFormat.format("Error deleting properties for {0}", application), e);
        } finally {
            session.logout();
        }
    }

    public List<SystemI18NProperty> findAllByApplication(String application) throws SystemException {
        Session session = createSession();
        QueryManager queryManager;
        List<SystemI18NProperty> resultList = new ArrayList<>();
        try {
            queryManager = session.getWorkspace().getQueryManager();
            String queryExpression = new JCRQueryBuilder()
                    .addFrom(CmsConstants.RADIEN_PROPERTY_NODE_TYPE, QUERY_FROM_ALIAS)
                    .addWhere(MessageFormat.format(FROM_QUERY_EQUALITY, CmsConstants.PROPERTY_APPLICATION, application))
                    .build();
            Query query = queryManager.createQuery(queryExpression, Query.JCR_SQL2);
            for(NodeIterator iterator = query.execute().getNodes(); iterator.hasNext();) {
                resultList.add(mappingUtils.convertPropertyNode(iterator.nextNode()));
            }
            return resultList;
        } catch (RepositoryException e) {
            throw new SystemException(MessageFormat.format("Error retrieving all properties for {0}", application), e);
        } finally {
            session.logout();
        }
    }

    public SystemI18NProperty findByKeyAndApplication(String key, String application) throws IllegalStateException, SystemException {
        Session session = createSession();
        QueryManager queryManager;
        SystemI18NProperty result = null;
        try {
            queryManager = session.getWorkspace().getQueryManager();
            String queryExpression = new JCRQueryBuilder()
                    .addFrom(CmsConstants.RADIEN_PROPERTY_NODE_TYPE, QUERY_FROM_ALIAS)
                    .addWhere(MessageFormat.format(FROM_QUERY_EQUALITY, CmsConstants.PROPERTY_APPLICATION, application))
                    .addWhere(MessageFormat.format(FROM_QUERY_EQUALITY, CmsConstants.PROPERTY_KEY, key))
                    .build();
            Query query = queryManager.createQuery(queryExpression, Query.JCR_SQL2);
            NodeIterator nodes = query.execute().getNodes();
            while(nodes.hasNext()) {
                if(result != null) {
                    log.warn("Multiple nodes found for {} and {}", key, application);
                }
                result = mappingUtils.convertPropertyNode(nodes.nextNode());
            }
            return result;
        } catch (RepositoryException e) {
            throw new SystemException(MessageFormat.format("Error retrieving property {0} for {1}", key, application), e);
        } finally {
            session.logout();
        }
    }

    public Page<SystemI18NProperty> getAll(String application, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) throws SystemException {
        Session session = createSession();
        QueryManager queryManager;
        Page<SystemI18NProperty> page;
        try {
            queryManager = session.getWorkspace().getQueryManager();

            JCRQueryBuilder builder = new JCRQueryBuilder()
                    .addFrom(CmsConstants.RADIEN_PROPERTY_NODE_TYPE, QUERY_FROM_ALIAS);
            if(!StringUtils.isEmpty(application)) {
                builder.addWhere(MessageFormat.format(FROM_QUERY_EQUALITY, CmsConstants.PROPERTY_APPLICATION, application));
            }
            String queryExpression = builder.build();
            Query query = queryManager.createQuery(queryExpression, Query.JCR_SQL2);

            List<Node> nodes = Lists.newArrayList(query.execute().getNodes());
            long totalCount = nodes.size();
            int offset = (pageNo > 0 ? ((pageNo - 1) * pageSize ) : 0);

            List<Node> pageList = nodes.subList(offset, Math.toIntExact(Math.min(offset + pageSize, totalCount)));
            List<SystemI18NProperty> results = new ArrayList<>();

            for(Node nextNode : pageList) {
                results.add(mappingUtils.convertPropertyNode(nextNode));
            }
            page = new Page<>(results, pageNo, Math.toIntExact(totalCount), Math.toIntExact(totalCount % pageSize==0 ?
                    totalCount/pageSize : totalCount/pageSize+1));
            return page;
        } catch (RepositoryException e) {
            throw new SystemException(MessageFormat.format("Error paginating response for {0} {1}", pageNo, pageSize), e);
        } finally {
            session.logout();
        }
    }

    private String calculatePath(SystemI18NProperty property) {
        String rootPath = getOAF().getProperty(OAFProperties.SYSTEM_CMS_CFG_NODE_ROOT);
        String propertiesPath = getOAF().getProperty(OAFProperties.SYSTEM_CMS_CFG_NODE_PROPERTIES);

        return MessageFormat.format("/{0}/{1}/{2}", rootPath, propertiesPath, property.getKey());
    }
}
