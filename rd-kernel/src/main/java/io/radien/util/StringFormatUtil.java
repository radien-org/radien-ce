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

package io.radien.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * String value Formatter Utilities class
 *
 * @author Marco Weiland
 * @author Rafael Fernandes
 */
public class StringFormatUtil {

    private static final Logger log = LoggerFactory.getLogger(StringFormatUtil.class);

    private static Pattern EMPTY_PLACEHOLDER = Pattern.compile("\\{}");
    private static Pattern VALID_PLACEHOLDER = Pattern.compile("\\{\\d+}");

    /**
     * Writes a formatted string to this object's destination using the specified format string and arguments.
     * The locale used is the one defined during the construction of this formatter.
     * @param pattern A format string as described in Format string syntax.
     * @param arguments Arguments referenced by the format specifiers in the format string.
     *                  If there are more arguments than format specifiers, the extra arguments are ignored.
     * @return the formatted value
     */
    public static String format(String pattern, Object ... arguments) {
        validate(pattern, arguments);

        if(arguments.length == 0){
            return pattern;
        } else {
            if(EMPTY_PLACEHOLDER.matcher(pattern).find()) {
                int i = 0;
                while (pattern.contains("{}")) {
                    pattern = pattern.replaceFirst(EMPTY_PLACEHOLDER.pattern(), "{" + i++ + "}");
                }
            }
            return MessageFormat.format(pattern, arguments);
        }
    }

    /**
     * Validates if with the given string pattern and arguments the following combination can be formatted or not
     * @param pattern A format string as described in Format string syntax.
     * @param arguments Arguments referenced by the format specifiers in the format string.
     *                  If there are more arguments than format specifiers, the extra arguments are ignored.
     */
    private static void validate(String pattern, Object ... arguments){
        boolean containsEmptyPlaceholder = EMPTY_PLACEHOLDER.matcher(pattern).find();
        boolean containsValidPlaceholder = VALID_PLACEHOLDER.matcher(pattern).find();
        if(containsEmptyPlaceholder && containsValidPlaceholder){
            throw new IllegalArgumentException("Cannot format a pattern that contains both \"{}\" and \"{[int]}\" as placeholders," +
                    " use either all empty placeholders or placeholders with the corresponding index int values");
        } else {
            if (containsEmptyPlaceholder) {
                emptyPLaceHolderValidator(pattern, arguments);
            } else if (containsValidPlaceholder){
                validPlaceHolderValidator(pattern, arguments);
            } else {
                if(arguments.length != 0) {
                    log.warn("No valid placeholder was detected in given pattern, but arguments where received," +
                            " as such this arguments will be ignored");
                }
            }
        }
    }

    private static void emptyPLaceHolderValidator(String pattern, Object[] arguments) {
        List<String> temp = new ArrayList<>();
        Matcher matcher = EMPTY_PLACEHOLDER.matcher(pattern);
        while(matcher.find()) {
            temp.add(matcher.group());
        }
        if(temp.size() != arguments.length){
            throw new IllegalArgumentException("Number of arguments doesn't match placeholders in pattern");
        }
    }

    private static void validPlaceHolderValidator(String pattern, Object[] arguments) {
        List<String> temp = new ArrayList<>();
        Matcher matcher = VALID_PLACEHOLDER.matcher(pattern);
        while(matcher.find()) {
            temp.add(matcher.group());
        }
        Optional<Integer> max = temp.stream().map(s -> Integer.valueOf(s.replace("{", "").replace("}", ""))).max(Integer::compareTo);
        assert max.isPresent();
        if(max.get() != arguments.length - 1){
            throw new IllegalArgumentException("Number of arguments doesn't match placeholders in pattern");
        }
    }

}
