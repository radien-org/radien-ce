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

package io.radien.ms.ecm.client.util;

public class TextUtil {
    public static String escapeIllegalJcrChars(String name) {
        return escapeIllegalChars(name, "%/:[]*|\t\r\n");
    }

    private static String escapeIllegalChars(String name, String illegal) {
        StringBuilder buffer = new StringBuilder(name.length() * 2);

        for(int i = 0; i < name.length(); ++i) {
            char ch = name.charAt(i);
            if (illegal.indexOf(ch) == -1 && (ch != '.' || name.length() >= 3) && (ch != ' ' || i != 0 && i != name.length() - 1)) {
                buffer.append(ch);
            } else {
                buffer.append('%');
                buffer.append(Character.toUpperCase(Character.forDigit(ch / 16, 16)));
                buffer.append(Character.toUpperCase(Character.forDigit(ch % 16, 16)));
            }
        }

        return buffer.toString();
    }
}
