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
import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class UserModelMessageBodyReaderTest extends TestCase {

    @Test
    public void testIsReadable() {
        UserModelMessageBodyReader target = new UserModelMessageBodyReader();
        assertTrue(target.isReadable(User.class,null,null,null));
    }

    @Test
    public void testReadFrom() throws IOException {
        String read = "{\"" +
                "id\":null," +
                "\"logon\":\"logon\"," +
                "\"userEmail\":\"email@server.pt\"," +
                "\"createUser\":null," +
                "\"lastUpdateUser\":null," +
                "\"sub\":\"sub\"," +
                "\"firstname\":\"a\"," +
                "\"lastname\":\"b\"" +
                "}";
        UserModelMessageBodyReader target = new UserModelMessageBodyReader();
        InputStream inputStream = new ByteArrayInputStream(read.getBytes());
        User user =target.readFrom(null,null,null,null,null, inputStream);
        assertNull(user.getId());
        assertEquals("logon",user.getLogon());
        assertEquals("email@server.pt",user.getUserEmail());
        assertNull(user.getCreateUser());
        assertNull(user.getLastUpdateUser());
        assertEquals("sub",user.getSub());
        assertEquals("a",user.getFirstname());
        assertEquals("b",user.getLastname());
    }
}