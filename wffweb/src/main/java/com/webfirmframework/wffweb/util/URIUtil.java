/*
 * Copyright 2014-2022 Web Firm Framework
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
 */
package com.webfirmframework.wffweb.util;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.webfirmframework.wffweb.InvalidValueException;

/**
 * @author WFF
 * @since 12.0.0-beta.2
 *
 */
public final class URIUtil {

    private URIUtil() {
        throw new AssertionError();
    }

    /**
     *
     * <pre>
     * Map<String, String> pathParams = URIUtil.parseValues("/user/itemGroups/{itemGroupId}/items/view/{itemId}", "/user/itemGroups/1/items/view/2");
     * Here, pathParams is {itemGroupId=1, itemId=2}.
     * </pre>
     *
     * @param pattern
     * @param uri
     * @return map containing the variable name and its corresponding value parsed
     *         from the uri.
     *
     * @since 12.0.0-beta.2
     */
    public static Map<String, String> parseValues(final String pattern, final String uri) {

        final String[] patternParts = StringUtil.split(pattern, '/');
        final String[] urlParts = StringUtil.split(uri, '/');

        if (patternParts.length != urlParts.length) {
            throw new InvalidValueException("The pattern doesn't match with the uri");
        }

        final Map<String, String> variableNameValue = new HashMap<>(Math.min(urlParts.length, 16));

        for (int i = 0; i < patternParts.length; i++) {
            final String patternPart = patternParts[i];
            final String uriValue = urlParts[i];

            if (patternPart.length() > 1 && patternPart.indexOf('{') == 0
                    && patternPart.indexOf('}') == patternPart.length() - 1) {

                final String variableName = patternPart.substring(1, patternPart.length() - 1);

                final String uriValueDecoded = URLDecoder.decode(uriValue, StandardCharsets.UTF_8);
                final String previous = variableNameValue.put(variableName, uriValueDecoded);

                if (previous != null) {
                    throw new InvalidValueException("duplicate variable name found in the uri pattern");
                }

            } else {
                if (!patternPart.equals(uriValue)) {
                    throw new InvalidValueException("The pattern doesn't match with the uri");
                }
            }
        }

        return Map.copyOf(variableNameValue);
    }

    /**
     *
     * <pre>
     * boolean matches = URIUtil.patternMatches("/user/items/view/{itemId}", "/user/items/view/123");
     * Here, matches is true.
     * </pre>
     *
     * <pre>
     * boolean matches = URIUtil.patternMatches("/user/items/view/123", "/user/items/view/123");
     * Here, matches is true.
     * </pre>
     *
     * <pre>
     * boolean matches = URIUtil.patternMatches("/user/items/edit/{itemId}", "/user/items/view/123");
     * Here, matches is false.
     * </pre>
     *
     * <pre>
     * boolean matches = URIUtil.patternMatches("/user/items/view", "/user/items/view/123");
     * Here, matches is false.
     * </pre>
     *
     * @param pattern
     * @param uri
     * @return true if the path of the uri matches with the pattern
     *
     * @since 12.0.0-beta.2
     */
    public static boolean patternMatches(final String pattern, final String uri) {
        if (pattern.equals(uri)) {
            return true;
        }
        final String[] patternParts = StringUtil.split(pattern, '/');
        final String[] urlParts = StringUtil.split(uri, '/');

        if (patternParts.length != urlParts.length) {
            return false;
        }

        final Map<String, String> variableNameValue = new HashMap<>(Math.min(urlParts.length, 16));

        for (int i = 0; i < patternParts.length; i++) {
            final String patternPart = patternParts[i];
            final String uriValue = urlParts[i];

            if (patternPart.length() > 1 && patternPart.indexOf('{') == 0
                    && patternPart.indexOf('}') == patternPart.length() - 1) {

                final String variableName = patternPart.substring(1, patternPart.length() - 1);

                final String uriValueDecoded = URLDecoder.decode(uriValue, StandardCharsets.UTF_8);
                final String previous = variableNameValue.put(variableName, uriValueDecoded);

                if (previous != null) {
                    throw new InvalidValueException("duplicate variable name found in the uri pattern");
                }

            } else {
                if (!patternPart.equals(uriValue)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Eg:
     *
     * <pre>
     * boolean matches = URIUtil.patternMatchesBase("/user/items/view/{itemId}", "/user/items/view/123");
     * Here, matches is true.
     * </pre>
     *
     * <pre>
     * boolean matches = URIUtil.patternMatchesBase("/user/items/view/123", "/user/items/view/123");
     * Here, matches is true.
     * </pre>
     *
     * <pre>
     * boolean matches = URIUtil.patternMatchesBase("/user/items/view", "/user/items/view/123");
     * Here, matches is true.
     * </pre>
     *
     * <pre>
     * boolean matches = URIUtil.patternMatchesBase("/user/items", "/user/items/view/123");
     * Here, matches is true.
     * </pre>
     *
     * <pre>
     * boolean matches = URIUtil.patternMatchesBase("/user/items/edit/{itemId}", "/user/items/view/123");
     * Here, matches is false.
     * </pre>
     *
     * @param pattern
     * @param uri
     * @return true if the base path of the uri matches with the pattern
     *
     * @since 12.0.0-beta.2
     */
    public static boolean patternMatchesBase(final String pattern, final String uri) {

        if (pattern.equals(uri)) {
            return true;
        }

        final String[] patternParts = StringUtil.split(pattern, '/');
        final String[] urlParts = StringUtil.split(uri, '/');

        if (patternParts.length > urlParts.length) {
            return false;
        }

        final Map<String, String> variableNameValue = new HashMap<>(Math.min(patternParts.length, 16));

        for (int i = 0; i < patternParts.length; i++) {
            final String patternPart = patternParts[i];
            final String uriValue = urlParts[i];

            if (patternPart.length() > 1 && patternPart.indexOf('{') == 0
                    && patternPart.indexOf('}') == patternPart.length() - 1) {

                final String variableName = patternPart.substring(1, patternPart.length() - 1);

                final String previous = variableNameValue.put(variableName, uriValue);

                if (previous != null) {
                    throw new InvalidValueException("duplicate variable name found in the uri pattern");
                }

            } else {
                if (!patternPart.equals(uriValue)) {
                    return false;
                }
            }
        }

        return true;
    }

}
