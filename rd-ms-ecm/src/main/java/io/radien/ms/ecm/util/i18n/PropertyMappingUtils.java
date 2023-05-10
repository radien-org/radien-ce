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

package io.radien.ms.ecm.util.i18n;

import io.radien.api.model.i18n.SystemI18NProperty;
import io.radien.api.model.i18n.SystemI18NTranslation;
import io.radien.exception.SystemException;
import io.radien.ms.ecm.client.entities.i18n.I18NProperty;
import io.radien.ms.ecm.client.entities.i18n.I18NTranslation;
import io.radien.ms.ecm.constants.CmsConstants;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class PropertyMappingUtils implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(PropertyMappingUtils.class);
    private static final long serialVersionUID = -5336357595791422077L;

    public SystemI18NProperty convertPropertyNode(Node node) throws RepositoryException, SystemException {
        if(!node.getPrimaryNodeType().isNodeType(CmsConstants.RADIEN_PROPERTY_NODE_TYPE)) {
            throw new SystemException("Node is not " + CmsConstants.RADIEN_PROPERTY_NODE_TYPE);
        }
        SystemI18NProperty property = new I18NProperty();
        property.setApplication(node.getProperty(CmsConstants.PROPERTY_APPLICATION).getString());
        property.setKey(node.getProperty(CmsConstants.PROPERTY_KEY).getString());
        property.setTranslations(convertTranslationNodes(node.getNodes()));

        return property;
    }

    public void syncNode(Node rootNode, Node propertyNode, SystemI18NProperty property) throws RepositoryException {
        if(propertyNode == null) {
            log.info("Adding new node for property {}-{}", property.getApplication(), property.getKey());
            propertyNode = rootNode.addNode(property.getKey(), CmsConstants.RADIEN_PROPERTY_NODE_TYPE);
        } else {
            log.info("Updating property {}-{}", property.getApplication(), property.getKey());
            for(NodeIterator iter = propertyNode.getNodes(); iter.hasNext();) {
                iter.nextNode().remove();
            }
        }
        propertyNode.setProperty(CmsConstants.PROPERTY_KEY, property.getKey());
        propertyNode.setProperty(CmsConstants.PROPERTY_APPLICATION, property.getApplication());
        for (SystemI18NTranslation t : property.getTranslations()) {
            Node translation = propertyNode.addNode(t.getLanguage(), CmsConstants.RADIEN_TRANSLATION_NODE_TYPE);
            translation.setProperty(CmsConstants.PROPERTY_LANGUAGE, t.getLanguage());
            translation.setProperty(CmsConstants.PROPERTY_VALUE, t.getValue());
        }
    }

    private List<SystemI18NTranslation> convertTranslationNodes(NodeIterator nodes) throws RepositoryException {
        List<SystemI18NTranslation> translationList = new ArrayList<>();
        for(NodeIterator it = nodes; it.hasNext(); ) {
            Node translationNode = it.nextNode();
            SystemI18NTranslation translation = new I18NTranslation();
            translation.setLanguage(translationNode.getProperty(CmsConstants.PROPERTY_LANGUAGE).getString());
            translation.setValue(translationNode.getProperty(CmsConstants.PROPERTY_VALUE).getString());
            translationList.add(translation);
        }
        return translationList;
    }
}
