/*
 * Copyright since 2014 Web Firm Framework
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.webfirmframework.wffweb.InvalidValueException;

/**
 * @author WFF
 * @since 12.0.0-beta.2
 */
public final class URIUtil {

    private URIUtil() {
        throw new AssertionError();
    }

    /**
     *
     * <pre>
     * <code>
     * Map&lt;String, String&gt; pathParams = URIUtil.parseValues("/user/itemGroups/{itemGroupId}/items/view/{itemId}", "/user/itemGroups/1/items/view/2");
     * Here, pathParams is {itemGroupId=1, itemId=2}.
     * </code>
     * </pre>
     *
     * <pre>
     * <code>
     * Map&lt;String, String&gt; pathParams = URIUtil.parseValues("/user/itemGroups/{itemGroupId}/[uriAsParam]", "/user/itemGroups/1/items/view/2");
     * Here, pathParams is {itemGroupId=1, uriAsParam=items/view/2}.
     * </code>
     * </pre>
     *
     * <pre>
     * <code>
     * Map&lt;String, String&gt; pathParams = URIUtil.parseValues("/user/itemGroups/{itemGroupId}/[uriAsParam]/item/{itemId}", "/user/itemGroups/1/items/view/item/2");
     * Here, pathParams is {itemGroupId=1, uriAsParam=items/view, itemId=2}.
     * </code>
     * </pre>
     *
     * <pre>
     * <code>
     * Map&lt;String, String&gt; pathParams = URIUtil.parseValues("/user/itemGroups/{itemGroupId}/[uriAsParam1]/item/{itemId}/[uriAsParam2]", "/user/itemGroups/1/items/view/item/2/details/yes");
     * Here, pathParams is {itemGroupId=1, uriAsParam1=items/view, itemId=2, uriAsParam2=details/yes}.
     * </code>
     * </pre>
     *
     * <pre>
     * <code>
     * Map&lt;String, String&gt; pathParams = URIUtil.parseValues("/user/itemGroups/{itemGroupId}/[uriAsParam]/{itemId}", "/user/itemGroups/1/items/view/2");
     * Here, pathParams is {itemGroupId=1, uriAsParam=items/view, itemId=2}.
     * </code>
     * </pre>
     *
     * @param pattern
     * @param uri
     * @return map containing the variable name and its corresponding value parsed
     *         from the uri.
     * @since 12.0.0-beta.2
     * @since 12.0.0-beta.7 Supports [uriAsParam] type variables
     * @since 12.0.0-beta.9 Supports query params in the uri, but it will not parse
     *        query params
     */
    public static Map<String, String> parseValues(final String pattern, final String uri) {
        return parsePathParams(pattern, toURIInfo(uri).pathname);
    }

    private static Map<String, String> parsePathParams(final String pattern, final String uriWithoutQString) {

        final String patternWithoutQString = toURIInfo(pattern).pathname;

        final String[] patternParts = StringUtil.split(patternWithoutQString, '/');
        final String[] urlParts = StringUtil.split(uriWithoutQString, '/');

        if (containsSquareParam(patternParts)) {
            if (patternParts.length > urlParts.length) {
                throw new InvalidValueException("The pattern doesn't match with the uri");
            }
        } else {
            if (patternParts.length != urlParts.length) {
                throw new InvalidValueException("The pattern doesn't match with the uri");
            }
        }
        if (startsWithSlash(uriWithoutQString) && !startsWithSlash(patternWithoutQString)) {
            throw new InvalidValueException("The pattern doesn't match with the uri");
        }

        final Map<String, String> variableNameValue = new HashMap<>(Math.min(urlParts.length, 16));

        for (int i = 0, j = 0; i < patternParts.length; i++, j++) {

            final String patternPart = patternParts[i];
            if (j >= urlParts.length) {
                throw new InvalidValueException("The pattern doesn't match with the uri");
            }
            final String uriValue = urlParts[j];

            if (patternPart.length() > 1 && startsWithCurlyBracket(patternPart) && endsWithCurlyBracket(patternPart)) {

                if (i == (patternParts.length - 1) && j < (urlParts.length - 1)) {
                    throw new InvalidValueException("The pattern doesn't match with the uri");
                }

                final String variableName = patternPart.substring(1, patternPart.length() - 1);

                final String uriValueDecoded = URLDecoder.decode(uriValue, StandardCharsets.UTF_8);
                final String previous = variableNameValue.put(variableName, uriValueDecoded);

                if (previous != null) {
                    throw new InvalidValueException(
                            "duplicate variable name %s found in the uri pattern".formatted(variableName));
                }

            } else if (patternPart.length() > 1 && startsWithSquareBracket(patternPart)
                    && endsWithSquareBracket(patternPart)) {

                final String variableName = patternPart.substring(1, patternPart.length() - 1);

                if ((i + 1) < patternParts.length) {
                    final String nextPatternPart = patternParts[i + 1];
                    if ((startsWithCurlyBracket(nextPatternPart) && endsWithCurlyBracket(nextPatternPart))
                            || (startsWithSquareBracket(nextPatternPart) && endsWithSquareBracket(nextPatternPart))) {
                        final int lastIndex = patternParts.length - (i + 1);
                        final String[] uriPartsForJoin = new String[(urlParts.length - lastIndex) - j];
                        final int initialK = j;
                        for (int k = initialK; k < urlParts.length - lastIndex; k++) {
                            final String uriValueTmp = urlParts[k];
                            final String uriValueDecoded = URLDecoder.decode(uriValueTmp, StandardCharsets.UTF_8);
                            uriPartsForJoin[k - initialK] = uriValueDecoded;
                            j = k;
                        }
                        variableNameValue.put(variableName, String.join("/", uriPartsForJoin));
                    } else {
                        for (int k = j; k < urlParts.length; k++) {
                            final String uriValueTmp = urlParts[k];
                            if (!uriValueTmp.equals(nextPatternPart)) {
                                final String uriValueDecoded = URLDecoder.decode(uriValueTmp, StandardCharsets.UTF_8);
                                variableNameValue.merge(variableName, uriValueDecoded, (a, b) -> a + '/' + b);
                                j = k;
                            } else {
                                break;
                            }
                        }
                    }
                } else {
                    final int initialK = j;
                    final String[] uriPartsForJoin = new String[urlParts.length - initialK];
                    for (int k = initialK; k < urlParts.length; k++) {
                        final String uriValueTmp = urlParts[k];
                        final String uriValueDecoded = URLDecoder.decode(uriValueTmp, StandardCharsets.UTF_8);
                        uriPartsForJoin[k - initialK] = uriValueDecoded;
                        j = k;
                    }
                    variableNameValue.put(variableName, String.join("/", uriPartsForJoin));
                }
            } else {
                if (!patternPart.equals(uriValue)) {
                    throw new InvalidValueException("The pattern doesn't match with the uri");
                }
            }
        }

        return Map.copyOf(variableNameValue);
    }

    private static boolean startsWithCurlyBracket(final String s) {
        if (s.length() == 0) {
            return false;
        }
        return s.codePointAt(0) == '{';
    }

    private static boolean endsWithCurlyBracket(final String s) {
        if (s.length() == 0) {
            return false;
        }
        return s.codePointAt(s.length() - 1) == '}';
    }

    private static boolean startsWithSquareBracket(final String s) {
        if (s.length() == 0) {
            return false;
        }
        return s.codePointAt(0) == '[';
    }

    private static boolean endsWithSquareBracket(final String s) {
        if (s.length() == 0) {
            return false;
        }
        return s.codePointAt(s.length() - 1) == ']';
    }

    private static boolean startsWithSlash(final String s) {
        if (s.length() == 0) {
            return false;
        }
        return s.codePointAt(0) == '/';
    }

    private static boolean containsSquareParam(final String[] patternParts) {
        boolean squareParamExists = false;
        for (final String patternPart : patternParts) {
            if (startsWithSquareBracket(patternPart) && endsWithSquareBracket(patternPart)) {
                squareParamExists = true;
                break;
            }
        }
        return squareParamExists;
    }

    /**
     * <pre>
     * <code>
     * boolean matches = URIUtil.patternMatches("/user/items/view/{itemId}", "/user/items/view/123");
     * Here, matches is true.
     * </code>
     * </pre>
     *
     * <pre>
     * <code>
     * boolean matches = URIUtil.patternMatches("/user/items/view/{itemId}/[uriAsParam]", "/user/items/view/123/details/full");
     * Here, matches is true.
     * </code>
     * </pre>
     *
     * <pre>
     * <code>
     * boolean matches = URIUtil.patternMatches("/user/items/view/123", "/user/items/view/123");
     * Here, matches is true.
     * </code>
     * </pre>
     *
     * <pre>
     * <code>
     * boolean matches = URIUtil.patternMatches("/user/items/edit/{itemId}", "/user/items/view/123");
     * Here, matches is false.
     * </code>
     * </pre>
     *
     * <pre>
     * <code>
     * boolean matches = URIUtil.patternMatches("/user/items/view", "/user/items/view/123");
     * Here, matches is false.
     * </code>
     * </pre>
     *
     * @param pattern
     * @param uri
     * @return true if the path of the uri matches with the pattern
     * @since 12.0.0-beta.2
     * @since 12.0.0-beta.7 Supports [uriAsParam] type variables
     * @since 12.0.0-beta.9 Supports query params in the uri
     */
    public static boolean patternMatches(final String pattern, final String uri) {
        if (pattern.equals(uri)) {
            return true;
        }
        final String patternWithoutQString = toURIInfo(pattern).pathname;
        final String uriWithoutQString = toURIInfo(uri).pathname;

        if (patternWithoutQString.equals(uriWithoutQString)) {
            return true;
        }

        final String[] patternParts = StringUtil.split(patternWithoutQString, '/');
        final String[] urlParts = StringUtil.split(uriWithoutQString, '/');

        if (containsSquareParam(patternParts)) {
            if (patternParts.length > urlParts.length) {
                return false;
            }
        } else {
            if (patternParts.length != urlParts.length) {
                return false;
            }
        }
        if (startsWithSlash(uriWithoutQString) && !startsWithSlash(patternWithoutQString)) {
            return false;
        }

        final Map<String, String> variableNameValue = new HashMap<>(Math.min(urlParts.length, 16));

        for (int i = 0, j = 0; i < patternParts.length; i++, j++) {

            final String patternPart = patternParts[i];
            if (j >= urlParts.length) {
                return false;
            }
            final String uriValue = urlParts[j];

            if (patternPart.length() > 1 && startsWithCurlyBracket(patternPart) && endsWithCurlyBracket(patternPart)) {

                if (i == (patternParts.length - 1) && j < (urlParts.length - 1)) {
                    return false;
                }

                final String variableName = patternPart.substring(1, patternPart.length() - 1);

                final String uriValueDecoded = URLDecoder.decode(uriValue, StandardCharsets.UTF_8);
                final String previous = variableNameValue.put(variableName, uriValueDecoded);

                if (previous != null) {
                    throw new InvalidValueException(
                            "duplicate variable name %s found in the uri pattern".formatted(variableName));
                }
            } else if (patternPart.length() > 1 && startsWithSquareBracket(patternPart)
                    && endsWithSquareBracket(patternPart)) {

                if ((i + 1) < patternParts.length) {
                    final String nextPatternPart = patternParts[i + 1];
                    if ((startsWithCurlyBracket(nextPatternPart) && endsWithCurlyBracket(nextPatternPart))
                            || (startsWithSquareBracket(nextPatternPart) && endsWithSquareBracket(nextPatternPart))) {
                        final int lastIndex = patternParts.length - (i + 1);
                        final int initialK = j;
                        for (int k = initialK; k < urlParts.length - lastIndex; k++) {
                            j = k;
                        }
                    } else {
                        for (int k = j; k < urlParts.length; k++) {
                            final String uriValueTmp = urlParts[k];
                            if (!uriValueTmp.equals(nextPatternPart)) {
                                j = k;
                            } else {
                                break;
                            }
                        }
                    }
                } else {
                    j = urlParts.length - 1;
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
     * <code>
     * boolean matches = URIUtil.patternMatchesBase("/user/items/view/{itemId}", "/user/items/view/123");
     * Here, matches is true.
     * </code>
     * </pre>
     *
     * <pre>
     * <code>
     * boolean matches = URIUtil.patternMatchesBase("/user/items/view/{itemId}/[uriAsParam]", "/user/items/view/123/basic-details/yes/other-details/yes");
     * Here, matches is true.
     * </code>
     * </pre>
     *
     * <pre>
     * <code>
     * boolean matches = URIUtil.patternMatchesBase("/user/items/view/123", "/user/items/view/123");
     * Here, matches is true.
     * </code>
     * </pre>
     *
     * <pre>
     * <code>
     * boolean matches = URIUtil.patternMatchesBase("/user/items/view", "/user/items/view/123");
     * Here, matches is true.
     * </code>
     * </pre>
     *
     * <pre>
     * <code>
     * boolean matches = URIUtil.patternMatchesBase("/user/items", "/user/items/view/123");
     * Here, matches is true.
     * </code>
     * </pre>
     *
     * <pre>
     * <code>
     * boolean matches = URIUtil.patternMatchesBase("/user/items/edit/{itemId}", "/user/items/view/123");
     * Here, matches is false.
     * </code>
     * </pre>
     *
     * @param pattern
     * @param uri
     * @return true if the base path of the uri matches with the pattern
     * @since 12.0.0-beta.2
     * @since 12.0.0-beta.7 Supports [uriAsParam] type variables
     * @since 12.0.0-beta.9 Supports query params in the uri
     */
    public static boolean patternMatchesBase(final String pattern, final String uri) {

        if (pattern.equals(uri)) {
            return true;
        }

        final String patternWithoutQString = toURIInfo(pattern).pathname;
        final String uriWithoutQString = toURIInfo(uri).pathname;

        if (patternWithoutQString.equals(uriWithoutQString)) {
            return true;
        }

        final String[] patternParts = StringUtil.split(patternWithoutQString, '/');
        final String[] urlParts = StringUtil.split(uriWithoutQString, '/');

        if ((containsSquareParam(patternParts) && patternParts.length > urlParts.length)
                || (startsWithSlash(uriWithoutQString) && !startsWithSlash(patternWithoutQString))) {
            return false;
        }

        final Map<String, String> variableNameValue = new HashMap<>(Math.min(urlParts.length, 16));

        for (int i = 0, j = 0; i < patternParts.length; i++, j++) {

            final String patternPart = patternParts[i];
            if (j >= urlParts.length) {
                return false;
            }
            final String uriValue = urlParts[j];

            if (patternPart.length() > 1 && startsWithCurlyBracket(patternPart) && endsWithCurlyBracket(patternPart)) {

                final String variableName = patternPart.substring(1, patternPart.length() - 1);

                final String uriValueDecoded = URLDecoder.decode(uriValue, StandardCharsets.UTF_8);
                final String previous = variableNameValue.put(variableName, uriValueDecoded);

                if (previous != null) {
                    throw new InvalidValueException(
                            "duplicate variable name %s found in the uri pattern".formatted(variableName));
                }
            } else if (patternPart.length() > 1 && startsWithSquareBracket(patternPart)
                    && endsWithSquareBracket(patternPart)) {

                if ((i + 1) < patternParts.length) {
                    final String nextPatternPart = patternParts[i + 1];
                    if ((startsWithCurlyBracket(nextPatternPart) && endsWithCurlyBracket(nextPatternPart))
                            || (startsWithSquareBracket(nextPatternPart) && endsWithSquareBracket(nextPatternPart))) {
                        final int lastIndex = patternParts.length - (i + 1);
                        final int initialK = j;
                        for (int k = initialK; k < urlParts.length - lastIndex; k++) {
                            j = k;
                        }
                    } else {
                        for (int k = j; k < urlParts.length; k++) {
                            final String uriValueTmp = urlParts[k];
                            if (!uriValueTmp.equals(nextPatternPart)) {
                                j = k;
                            } else {
                                break;
                            }
                        }
                    }
                } else {
                    j = urlParts.length - 1;
                }
            } else {
                if (!patternPart.equals(uriValue)) {
                    return false;
                }
            }

            if (i == (patternParts.length - 1)) {
                return true;
            }
        }

        return true;
    }

    record URIInfo(String pathname, String queryString, String hash) {

    }

    static URIInfo toURIInfo(final String uri) {
        final int indexOfIQ = uri.indexOf('?');

        final int indexOfHash = uri.indexOf('#');

        final String uriWithoutQString;

        final String qString;

        if (indexOfIQ < indexOfHash) {
            if (indexOfIQ != -1) {
                uriWithoutQString = uri.substring(0, indexOfIQ);
                qString = uri.substring(indexOfIQ + 1, indexOfHash);
            } else {
                uriWithoutQString = uri.substring(0, indexOfHash);
                qString = "";
            }
        } else {
            if (indexOfHash != -1) {
                uriWithoutQString = uri.substring(0, indexOfHash);
                qString = "";
            } else {
                if (indexOfIQ != -1) {
                    uriWithoutQString = uri.substring(0, indexOfIQ);
                    qString = uri.substring(indexOfIQ + 1);
                } else {
                    uriWithoutQString = uri;
                    qString = "";
                }
            }
        }

        final String hash = indexOfHash != -1 ? uri.substring(indexOfHash + 1) : "";

        return new URIInfo(uriWithoutQString, qString, hash);
    }

    private static Map<String, List<String>> parseQueryString(final String qString) {
        final String[] splitByAmp = StringUtil.split(qString, '&');
        final List<Map.Entry<String, String>> entries = new ArrayList<>(splitByAmp.length);
        for (final String qParam : splitByAmp) {
            if (!qParam.isBlank()) {
                final int indexOfEqual = qParam.indexOf('=');
                final Map.Entry<String, String> entry;
                if (indexOfEqual != -1 && indexOfEqual < qParam.length() - 1) {
                    entry = Map.entry(URLDecoder.decode(qParam.substring(0, indexOfEqual), StandardCharsets.UTF_8),
                            URLDecoder.decode(qParam.substring(indexOfEqual + 1), StandardCharsets.UTF_8));
                } else if (indexOfEqual != -1) {
                    entry = Map.entry(URLDecoder.decode(qParam.substring(0, indexOfEqual), StandardCharsets.UTF_8), "");
                } else {
                    entry = Map.entry(URLDecoder.decode(qParam, StandardCharsets.UTF_8), "");
                }
                entries.add(entry);
            }
        }
        final Map<String, List<String>> queryParams = new HashMap<>();
        for (final Map.Entry<String, String> entry : entries) {
            queryParams.computeIfAbsent(entry.getKey(), s -> new ArrayList<>(1)).add(entry.getValue());
        }
        return Map.copyOf(queryParams);
    }

    /**
     * @param uri
     * @return the map containing query parameters and its values
     * @since 12.0.0-beta.9
     */
    public static Map<String, List<String>> parseQueryParameters(final String uri) {
        return parseQueryString(toURIInfo(uri).queryString);
    }

    /**
     * @param pattern
     * @param uri
     * @return the {@code ParsedURI} which contains parsed info of the uri.
     * @since 12.0.0-beta.9
     */
    public static ParsedURI parse(final String pattern, final String uri) {

        final URIInfo uriInfo = toURIInfo(uri);
        final String uriWithoutQString = uriInfo.pathname;

        return new ParsedURI(uriWithoutQString, parsePathParams(pattern, uriWithoutQString),
                parseQueryString(uriInfo.queryString), URLDecoder.decode(uriInfo.hash, StandardCharsets.UTF_8));
    }

    /**
     * @param uri
     * @return the {@code ParsedURI} which contains parsed info of the uri.
     * @since 12.0.0-beta.9
     */
    public static ParsedURI parse(final String uri) {

        final URIInfo uriInfo = toURIInfo(uri);
        final String uriWithoutQString = uriInfo.pathname;

        return new ParsedURI(uriWithoutQString, null, parseQueryString(uriInfo.queryString),
                URLDecoder.decode(uriInfo.hash, StandardCharsets.UTF_8));
    }

    /**
     * @param parameters the map of parameters
     * @return the url query string
     * @since 12.0.2
     */
    public static String buildQueryStringFromNameToCollectionOfValues(
            final Map<String, Collection<String>> parameters) {
        return parameters.entrySet().stream().<Map.Entry<String, String>>mapMulti((entry, downstream) -> {
            for (final String value : entry.getValue()) {
                downstream.accept(Map.entry(entry.getKey(), value));
            }
        }).map(URIUtil::buildNameEqualsValueEncodedString).collect(Collectors.joining("&"));
    }

    /**
     * @param parameters the map of parameters
     * @return the url query string
     * @since 12.0.2
     */
    public static String buildQueryStringFromNameToValues(final Map<String, String[]> parameters) {
        return parameters.entrySet().stream().<Map.Entry<String, String>>mapMulti((entry, downstream) -> {
            for (final String value : entry.getValue()) {
                downstream.accept(Map.entry(entry.getKey(), value));
            }
        }).map(URIUtil::buildNameEqualsValueEncodedString).collect(Collectors.joining("&"));
    }

    /**
     * @param parameters the map of parameters
     * @return the url query string
     * @since 12.0.2
     */
    public static String buildQueryStringFromNameToValue(final Map<String, String> parameters) {
        return parameters.entrySet().stream().map(URIUtil::buildNameEqualsValueEncodedString)
                .collect(Collectors.joining("&"));
    }

    private static String buildNameEqualsValueEncodedString(final Map.Entry<String, String> nameValue) {
        return URLEncoder.encode(nameValue.getKey(), StandardCharsets.UTF_8) + "="
                + URLEncoder.encode(nameValue.getValue(), StandardCharsets.UTF_8);
    }
}
