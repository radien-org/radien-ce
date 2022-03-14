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

import io.radien.api.entity.Page;
import io.radien.api.model.i18n.SystemI18NProperty;
import io.radien.api.model.i18n.SystemI18NTranslation;
import io.radien.api.service.ecm.exception.ContentRepositoryNotAvailableException;
import io.radien.exception.SystemException;
import io.radien.ms.ecm.client.entities.i18n.I18NProperty;
import io.radien.ms.ecm.client.entities.i18n.I18NTranslation;
import io.radien.ms.ecm.config.ConfigHandler;
import io.radien.ms.ecm.constants.CmsConstants;
import io.radien.ms.ecm.util.i18n.PropertyMappingUtils;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import javax.jcr.GuestCredentials;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.apache.jackrabbit.commons.cnd.ParseException;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class I18NRepositoryTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    @InjectMocks
    private I18NRepository repository;
    @Mock
    private ConfigHandler configHandler;
    @Spy
    private PropertyMappingUtils utils;

    private static boolean initialized = false;
    private static Session session;
    private static Repository transientRepository;

    @Before
    public void initMocks() throws RepositoryException, NoSuchFieldException, IllegalAccessException {
        if(!initialized) {
            transientRepository = JcrUtils.getRepository();
            session = transientRepository.login(new GuestCredentials());
            initialized = true;
        }
        Field repositoryField = repository.getClass().getSuperclass().getDeclaredField("repository");
        repositoryField.setAccessible(true);
        repositoryField.set(repository, transientRepository);

        when(configHandler.getRootNode())
                .thenReturn("radien");
        when(configHandler.getPropertiesNode())
                .thenReturn("rd_properties");
    }

    @Test
    public void test001InitRoot() throws RepositoryException, IOException {
        Session adminSession = getAdminSession();
        registerNodeTypes(adminSession);
        Node node = adminSession.getRootNode().addNode("radien", JcrConstants.NT_FOLDER);
        adminSession.save();
        assertEquals("/radien", node.getPath());
        assertEquals("/", node.getParent().getPath());
        node = node.addNode("rd_properties", JcrConstants.NT_FOLDER);
        adminSession.save();
        assertEquals("/radien/rd_properties", node.getPath());
        assertEquals("/radien", node.getParent().getPath());
        adminSession.logout();
        restartSession();
    }

    @Test
    public void test002Save() throws Exception {
        SystemI18NProperty property = new I18NProperty();
        property.setKey("testKey");
        property.setApplication("testApp");
        SystemI18NTranslation translation = new I18NTranslation();
        translation.setValue("testValue");
        translation.setLanguage("en");
        property.setTranslations(Collections.singletonList(translation));

        repository.save(property);
        assertTrue(checkExists("testKey", "testApp"));
    }

    @Test
    public void test003GetTranslation() throws SystemException {
        assertEquals("testValue", repository.getTranslation("testKey", "en", "testApp"));
    }

    @Test
    public void test004GetTranslationFallback() throws SystemException {
        assertEquals("testValue", repository.getTranslation("testKey", "en-US", "testApp"));
    }

    @Test
    public void test005GetTranslationNonExistant() throws SystemException {
        assertEquals("notExistant", repository.getTranslation("notExistant", "en", "testApp"));
    }

    @Test
    public void test006Update() throws Exception {
        SystemI18NProperty property = new I18NProperty();
        property.setKey("testKey");
        property.setApplication("testApp");
        SystemI18NTranslation translation = new I18NTranslation();
        translation.setValue("testValue1234567");
        translation.setLanguage("en");
        property.setTranslations(Collections.singletonList(translation));

        repository.save(property);
        assertTrue(checkExists("testKey", "testApp"));
        assertEquals("testValue1234567", repository.getTranslation("testKey", "en", "testApp"));
    }

    @Test
    public void test007DeleteProperty() throws Exception {
        SystemI18NProperty property = new I18NProperty();
        property.setKey("testKey");
        property.setApplication("testApp");
        SystemI18NTranslation translation = new I18NTranslation();
        translation.setValue("testValue1234567");
        translation.setLanguage("en");
        property.setTranslations(Collections.singletonList(translation));

        repository.deleteProperty(property);
        assertFalse(checkExists("testKey", "testApp"));
    }

    @Test
    public void test008FindAllByApplication() throws SystemException {
        SystemI18NProperty property = new I18NProperty();
        property.setKey("testKey");
        property.setApplication("testApp");
        SystemI18NTranslation translation = new I18NTranslation();
        translation.setValue("testValue1234567");
        translation.setLanguage("en");
        property.setTranslations(Collections.singletonList(translation));

        repository.save(property);
        property.setKey("testKey2");
        repository.save(property);
        property.setKey("testKey3");
        repository.save(property);

        List<SystemI18NProperty> propertyList = repository.findAllByApplication("testApp");
        assertFalse(propertyList.isEmpty());
        assertEquals(3, propertyList.size());
    }

    @Test
    public void test009GetAllFirstPage() throws SystemException {
        Page<SystemI18NProperty> results = repository.getAll("testApp", 1, 1, null, true);
        assertEquals(1, results.getCurrentPage());
        assertEquals(1, results.getResults().size());
        assertEquals(3, results.getTotalPages());
        assertEquals(3, results.getTotalResults());
    }

    @Test
    public void test010GetAllLastPage() throws SystemException {
        Page<SystemI18NProperty> results = repository.getAll("testApp", 3, 1, null, true);
        assertEquals(3, results.getCurrentPage());
        assertEquals(1, results.getResults().size());
        assertEquals(3, results.getTotalPages());
        assertEquals(3, results.getTotalResults());
    }

    @Test
    public void test011GetAllSingle() throws SystemException {
        Page<SystemI18NProperty> results = repository.getAll("testApp", 1, 100, null, true);
        assertEquals(1, results.getCurrentPage());
        assertEquals(3, results.getResults().size());
        assertEquals(1, results.getTotalPages());
        assertEquals(3, results.getTotalResults());
    }

    @Test
    public void test012DeleteApplication() throws SystemException {
        repository.deleteApplication("testApp");
        List<SystemI18NProperty> propertyList = repository.findAllByApplication("testApp");
        assertTrue(propertyList.isEmpty());
    }

    @Test(expected = ContentRepositoryNotAvailableException.class)
    public void test013CreateSessionError() throws RepositoryException, ContentRepositoryNotAvailableException {
        repository.createSession(new SimpleCredentials("bananas", "bananas".toCharArray()));
    }

    private boolean checkExists(String key, String application) throws Exception {
        Session adminSession = getAdminSession();
        boolean exists = JcrUtils.getNodeIfExists(String.format("/radien/rd_properties/%s/%s", application, key), adminSession) != null;
        adminSession.logout();
        return exists;
    }

    private Session getAdminSession() throws RepositoryException {
        return transientRepository.login(new SimpleCredentials(CmsConstants.USER_ADMIN, "admin".toCharArray()));
    }

    private void registerNodeTypes(Session session) throws RepositoryException, IOException {
        try(InputStreamReader streamReader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("jcr/oafnodetypes.cnd"))) {
            CndImporter.registerNodeTypes(streamReader, session);
        } catch (ParseException e) {
            throw new IOException();
        }
    }

    private void restartSession() throws RepositoryException {
        session.logout();
        session = transientRepository.login(new GuestCredentials());;
    }
}
