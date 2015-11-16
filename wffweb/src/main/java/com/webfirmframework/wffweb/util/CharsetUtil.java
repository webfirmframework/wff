/*
 * Copyright 2014-2015 Web Firm Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * @author WFF
 */
package com.webfirmframework.wffweb.util;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author WFF
 * @since 1.0.0
 */
public class CharsetUtil {

    private static final Map<Integer, char[]> CHACHED_UPPER_CASE_CHARSETS = new HashMap<Integer, char[]>();

    private static final Map<Integer, char[]> CHACHED_LOWER_CASE_CHARSETS = new HashMap<Integer, char[]>();

    private CharsetUtil() {
        throw new AssertionError();
    }

    /**
     * gets the char array of upper case chars from 0 to the given {@code upto}
     * value.
     *
     * @param upto
     * @return
     * @since 1.0.0
     * @author WFF
     */
    public static char[] getUpperCaseCharset(int upto) {
        upto++;
        final char[] characters = CHACHED_UPPER_CASE_CHARSETS.get(upto);
        if (characters != null) {
            return characters;
        }
        final char[] charset = new char[upto];
        char index = 0;
        while (index < upto) {
            charset[index] = Character.toUpperCase(index);
            index++;
        }
        CHACHED_UPPER_CASE_CHARSETS.put(upto, charset);
        return charset;
    }

    /**
     * gets the char array of lower case chars from 0 to the given {@code upto}
     * value.
     *
     * @param upto
     * @return
     * @since 1.0.0
     * @author WFF
     */
    public static char[] getLowerCaseCharset(int upto) {
        upto++;
        final char[] characters = CHACHED_LOWER_CASE_CHARSETS.get(upto);
        if (characters != null) {
            return characters;
        }
        final char[] charset = new char[upto];
        char index = 0;
        while (index < upto) {
            charset[index] = Character.toLowerCase(index);
            index++;
        }
        CHACHED_LOWER_CASE_CHARSETS.put(upto, charset);
        return charset;
    }

}
