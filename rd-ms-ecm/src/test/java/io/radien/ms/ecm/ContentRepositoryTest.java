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

package io.radien.ms.ecm;

import io.radien.api.OAFAccess;
import io.radien.api.service.ecm.exception.ContentRepositoryNotAvailableException;
import io.radien.api.service.ecm.model.Folder;
import io.radien.ms.ecm.constants.CmsConstants;
import io.radien.ms.ecm.domain.ContentDataProvider;
import io.radien.ms.ecm.util.ContentMappingUtils;
import java.io.IOException;
import java.lang.reflect.Field;
import javax.jcr.GuestCredentials;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.commons.cnd.ParseException;
import org.eclipse.microprofile.config.Config;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.FieldSetter;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ContentRepositoryTest {
    @InjectMocks
    private ContentRepository contentRepository;
    @Mock
    private Config config;
    @Mock
    private OAFAccess oaf;
    @Mock
    private ContentMappingUtils contentMappingUtils;
    @Mock
    private ContentDataProvider dataProvider;

    private static boolean initialized = false;
    private static Session session;
    private static Repository transientRepository;

    @Before
    public void init() throws RepositoryException, NoSuchFieldException {
        MockitoAnnotations.initMocks(this);
        if(!initialized) {
            transientRepository = JcrUtils.getRepository();
            session = transientRepository.login(new GuestCredentials());
            initialized = true;
        }
        Field repositoryField = contentRepository.getClass().getDeclaredField("repository");
        FieldSetter.setField(contentRepository, repositoryField, transientRepository);

        when(config.getValue(CmsConstants.PropertyKeys.SYSTEM_CMS_CFG_NODE_HTML, String.class))
                .thenReturn("rd_html");
        when(config.getValue(CmsConstants.PropertyKeys.SYSTEM_CMS_CFG_NODE_NOTIFICATION, String.class))
                .thenReturn("rd_notifications");
    }

    @Test
    public void test1RegisterNodeTypes() throws ContentRepositoryNotAvailableException, RepositoryException, ParseException, IOException {
        registerNodeTypes();
        assertNotNull(session.getWorkspace().getNodeTypeManager().getNodeType("rd:NodeType"));
    }

    @Test
    public void test2Save() throws ContentRepositoryNotAvailableException, RepositoryException {
        Folder rootFolder = new Folder("radien");
        rootFolder.setParentPath(contentRepository.getRootNodePath());
        rootFolder.setViewId(rootFolder.getName());

        doCallRealMethod().when(contentMappingUtils).syncNode(any(Node.class), eq(rootFolder), any(Session.class));
        contentRepository.save(rootFolder);
    }

    private void restartSession() throws RepositoryException {
        session.logout();
        session = transientRepository.login(new GuestCredentials());;
    }

    private void registerNodeTypes() throws ContentRepositoryNotAvailableException, RepositoryException {
        contentRepository.registerCNDNodeTypes(CmsConstants.PropertyKeys.OAF_NODE_TYPES);
        restartSession();
    }

}