/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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
package io.radien.ms.usermanagement.config;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Bruno Gama
 **/
public class KeycloakEmailActionsTest {

    @Test
    public void testKeyCloakEmailActions() {
        assertEquals("CONFIGURE_TOTP", KeycloakEmailActions.TOTP.propKey());
        assertEquals("TERMS_AND_CONDITIONS", KeycloakEmailActions.TERMS.propKey());
    }
}