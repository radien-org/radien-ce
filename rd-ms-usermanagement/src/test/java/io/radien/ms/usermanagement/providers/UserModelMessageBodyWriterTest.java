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
package io.radien.ms.usermanagement.providers;

import io.radien.ms.usermanagement.entities.User;
import io.radien.ms.usermanagement.legacy.UserFactory;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UserModelMessageBodyWriterTest extends TestCase {

    @Test
    public void testIsWriteable() {
        UserModelMessageBodyWriter target = new UserModelMessageBodyWriter();
        assertTrue(target.isWriteable(User.class,null,null,null));
    }

    @Test
    public void testGetSize() {
        UserModelMessageBodyWriter target = new UserModelMessageBodyWriter();
        assertEquals(0L,target.getSize(null,null,null,null,null));
    }

    @Test
    public void testWriteTo() throws IOException {
        String result = "{\"" +
                "id\":null," +
                "\"logon\":\"logon\"," +
                "\"userEmail\":\"email@server.pt\"," +
                "\"createUser\":null," +
                "\"lastUpdateUser\":null," +
                "\"sub\":\"sub\"," +
                "\"firstname\":\"a\"," +
                "\"lastname\":\"b\"" +
                "}";
        UserModelMessageBodyWriter target = new UserModelMessageBodyWriter();
        User user = UserFactory.create("a","b","logon","sub","email@server.pt", null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        target.writeTo(user,null,null,null, null,null, baos);

        assertEquals(result,baos.toString());
    }
}