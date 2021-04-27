/*
 * Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.
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
package io.radien.ms.openid.security;

import junit.framework.TestCase;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Bruno Gama
 **/
public class TokenHolderTest extends TestCase {

    TokenHolder tkHolder = new TokenHolder();

    public TokenHolderTest() {
        tkHolder.setAccessToken("accessToken");
        tkHolder.setRefreshToken("refreshToken");
    }

    @Test
    public void testGetAccessToken() {
        assertEquals("accessToken", tkHolder.getAccessToken());
    }

    @Test
    public void testSetAccessToken() {
        tkHolder.setAccessToken("newAccessToken");
        assertEquals("newAccessToken", tkHolder.getAccessToken());
    }

    @Test
    public void testGetRefreshToken() {
        assertEquals("refreshToken", tkHolder.getRefreshToken());
    }

    @Test
    public void testSetRefreshToken() {
        tkHolder.setRefreshToken("newRefreshToken");
        assertEquals("newRefreshToken", tkHolder.getRefreshToken());
    }
}