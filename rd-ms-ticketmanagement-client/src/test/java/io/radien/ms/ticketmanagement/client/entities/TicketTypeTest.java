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
 *
 */

package io.radien.ms.ticketmanagement.client.entities;

import junit.framework.TestCase;
import org.junit.Test;

public class TicketTypeTest extends TestCase {


    @Test
    public void testGetId() {
        TicketType ticketType = TicketType.EMAIL_CHANGE;
        assertEquals(java.util.Optional.of(1L), java.util.Optional.of(ticketType.getId()));
    }

    @Test
    public void testGetExpirationPeriod() {
        TicketType ticketType = TicketType.EMAIL_CHANGE;
        assertEquals(java.util.Optional.of(5), java.util.Optional.of(ticketType.getExpirationPeriod()));
    }

    @Test
    public void testGetType() {
        TicketType ticketType = TicketType.EMAIL_CHANGE;
        assertEquals("email_change", ticketType.getType());
    }

    @Test
    public void testGetById() {
        TicketType ticketType = TicketType.getById(1L);
        assertEquals(TicketType.EMAIL_CHANGE, ticketType);
    }
}