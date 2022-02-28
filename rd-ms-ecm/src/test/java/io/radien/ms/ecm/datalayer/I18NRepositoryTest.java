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

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.ImmutableMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import io.radien.api.model.i18n.SystemI18NProperty;
import io.radien.api.model.i18n.SystemI18NTranslation;
import io.radien.exception.SystemException;
import io.radien.ms.ecm.client.entities.i18n.I18NProperty;
import io.radien.ms.ecm.client.entities.i18n.I18NTranslation;
import io.radien.ms.ecm.producer.JongoConnectionHandler;
import io.radien.ms.ecm.util.i18n.JongoQueryBuilder;
import java.util.Collections;
import java.util.List;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.powermock.reflect.Whitebox.setInternalState;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(JUnit4.class)
@PrepareForTest(JongoConnectionHandler.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class I18NRepositoryTest {
    @InjectMocks
    private I18NRepository repository;
    @Mock
    private JongoConnectionHandler jongoConnectionHandler;

    private static MongodExecutable mongodExecutable;

    @BeforeClass
    public static void init() throws Exception {
        String ip = "localhost";
        int port = 27018;

        ImmutableMongodConfig mongodConfig = MongodConfig
                .builder()
                .version(Version.Main.DEVELOPMENT)
                .net(new Net(ip, port, Network.localhostIsIPv6()))
                .build();

        MongodStarter starter = MongodStarter.getDefaultInstance();
        mongodExecutable = starter.prepare(mongodConfig);
        mongodExecutable.start();
    }

    @Before
    public void initMocks() throws SystemException {
        setInternalState(jongoConnectionHandler, "mongoDB", "testMongoDB");
        setInternalState(jongoConnectionHandler, "mongoUri", "mongodb://localhost:27018");
        when(jongoConnectionHandler.apply(any(), anyString())).thenCallRealMethod();
    }

    @Test
    public void test001Save() throws Exception {
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
    public void test002GetTranslation() throws SystemException {
        assertEquals("testValue", repository.getTranslation("testKey", "en", "testApp"));
    }

    @Test
    public void test003GetTranslationFallback() throws SystemException {
        assertEquals("testValue", repository.getTranslation("testKey", "en-US", "testApp"));
    }

    @Test
    public void test004GetTranslationNonExistant() throws SystemException {
        assertEquals("notExistant", repository.getTranslation("notExistant", "en", "testApp"));
    }

    @Test
    public void test005Update() throws Exception {
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
    public void test006DeleteProperty() throws Exception {
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
    public void test007FindAllByApplication() throws SystemException {
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
    public void test008DeleteApplication() throws SystemException {
        repository.deleteApplication("testApp");
        List<SystemI18NProperty> propertyList = repository.findAllByApplication("testApp");
        assertTrue(propertyList.isEmpty());
    }

    private boolean checkExists(String key, String application) throws Exception {
        return jongoConnectionHandler.apply(input -> {
            String query = new JongoQueryBuilder()
                    .addEquality("key", key)
                    .addEquality("application", application)
                    .build();
            return input.count(query) > 0;
        }, I18NProperty.class.getSimpleName());
    }

    @AfterClass
    public static void stop() {
        mongodExecutable.stop();
    }
}
