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
package io.radien.ms.usermanagement.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
/**
 * Class that aggregates UnitTest cases for ConfigInfo
 *
 * @author Rajesh Gavvala
 */
public class ConfigInfoTest {
    @InjectMocks
    private ConfigInfo configInfo;

    /**
     * Prepares require objects when requires to invoke
     */
    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Tests init method and no exception
     * is thrown
     */
    @Test(expected = Test.None.class)
    public void initTest(){
        configInfo.init();
    }
}