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

        final String[] uriBuilderArray = new String[urlParts.length];

        final Map<String, String> variableNameValue = new HashMap<>(Math.min(uriBuilderArray.length, 16));

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

                uriBuilderArray[i] = uriValue;
            } else {
                uriBuilderArray[i] = patternPart;
            }
        }

        final String builtURI = String.join("/", uriBuilderArray);

        if (!builtURI.equals(uri)) {
            throw new InvalidValueException("The pattern doesn't match with the uri");
        }

        return Map.copyOf(variableNameValue);
    }

    /**
     * @param pattern
     * @param uri
     * @return true if the path of the uri matches with the pattern
     *
     * @since 12.0.0-beta.2
     */
    public static boolean patternMatches(final String pattern, final String uri) {
        return parseValues(pattern, uri).size() > 0;
    }

    /**
     * @param pattern
     * @param uri
     * @return true if the base path of the uri matches with the pattern
     *
     * @since 12.0.0-beta.2
     */
    public static boolean patternMatchesBase(final String pattern, final String uri) {

        final String[] patternParts = StringUtil.split(pattern, '/');
        final String[] urlParts = StringUtil.split(uri, '/');

        if (patternParts.length > urlParts.length) {
            return false;
        }

        final String[] uriBuilderArray = new String[patternParts.length];

        final Map<String, String> variableNameValue = new HashMap<>(Math.min(uriBuilderArray.length, 16));

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

                uriBuilderArray[i] = uriValue;
            } else {
                uriBuilderArray[i] = patternPart;
            }
        }

        final String builtURI = String.join("/", uriBuilderArray);

        if (builtURI.equals(uri)) {
            return true;
        }

        if (builtURI.length() < uri.length()) {
            return uri.substring(0, builtURI.length()).equals(builtURI);
        }

        return false;
    }

}
