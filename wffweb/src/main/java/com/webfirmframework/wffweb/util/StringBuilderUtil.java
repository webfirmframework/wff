/*
 * Copyright 2014-2023 Web Firm Framework
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

/**
 *
 * @author WFF
 * @since 1.0.0
 */
public final class StringBuilderUtil {
    private StringBuilderUtil() {
        throw new AssertionError();
    }

    /**
     * @param sb the {@code StringBuilder} object to create a trimmed string from
     *           it.
     * @return
     * @author WFF
     * @since 1.0.0 initial implementation.
     * @since 3.0.15 It is unicode aware.
     *
     */
    public static String getTrimmedString(final StringBuilder sb) {

        if (sb.length() == 0) {
            return sb.toString();
        }

        int first;
        int last;

        final int[] codePoints = sb.codePoints().toArray();

        for (first = 0; first < codePoints.length; first++) {
            if (!Character.isWhitespace(codePoints[first])) {
                break;
            }
        }

        for (last = codePoints.length; last > first; last--) {
            if (!Character.isWhitespace(codePoints[last - 1])) {
                break;
            }
        }
        if (first == 0 && last == codePoints.length) {
            return sb.toString();
        }

        final int lastRemovedCount = codePoints.length - last;

        final int codePointsCount = codePoints.length - (first + lastRemovedCount);

        return new String(codePoints, first, codePointsCount);
    }

    /**
     *
     * NB: Old implementation just kept for future reference
     *
     * @param sb the {@code StringBuilder} object to create a trimmed string from
     *           it.
     * @return
     * @author WFF
     * @since 1.0.0 initial implementation.
     * @since 3.0.15 It is unicode aware.
     *
     */
    @SuppressWarnings("unused")
    private static String getTrimmedStringOldImpl(final StringBuilder sb) {
        // Old implementation just kept for future reference
        int first;
        int last;

        for (first = 0; first < sb.length(); first++) {
            if (!Character.isWhitespace(sb.charAt(first))) {
                break;
            }
        }

        for (last = sb.length(); last > first; last--) {
            if (!Character.isWhitespace(sb.charAt(last - 1))) {
                break;
            }
        }

        return sb.substring(first, last);
    }

    /**
     * @param sb StringBuilder object to be stripped.
     * @return the same StringBuilder object after stripped.
     * @since 3.0.15
     */
    public static StringBuilder strip(final StringBuilder sb) {

        if (sb.length() == 0) {
            return sb;
        }

        int first;
        int last;

        final int[] codePoints = sb.codePoints().toArray();

        for (first = 0; first < codePoints.length; first++) {
            if (!Character.isWhitespace(codePoints[first])) {
                break;
            }
        }

        for (last = codePoints.length; last > first; last--) {
            if (!Character.isWhitespace(codePoints[last - 1])) {
                break;
            }
        }

        if (first == 0 && last == codePoints.length) {
            return sb;
        }

        final int lastRemovedCount = codePoints.length - last;

        final int codePointsCount = codePoints.length - (first + lastRemovedCount);

        sb.replace(0, sb.length(), new String(codePoints, first, codePointsCount));

//        int first;
//        int last;
//
//        for (first = 0; first < sb.length(); first++) {
//            if (!Character.isWhitespace(sb.charAt(first))) {
//                break;
//            }
//        }
//
//        if (first > 0) {
//            sb.delete(0, first);
//        }
//
//        for (last = sb.length(); last > first; last--) {
//            if (!Character.isWhitespace(sb.charAt(last - 1))) {
//                break;
//            }
//        }
//
//        if (sb.length() > last) {
//            sb.delete(last, sb.length());
//        }
//
        return sb;
    }

    /**
     * @param sb StringBuilder object to be trimmed.
     * @return the same StringBuilder object after trimming.
     * @since 3.0.15
     */
    @SuppressWarnings("unused")
    private static StringBuilder trimWithoutCodePoint(final StringBuilder sb) {

        int first;
        int last;

        for (first = 0; first < sb.length(); first++) {
            if (!Character.isWhitespace(sb.charAt(first))) {
                break;
            }
        }

        if (first > 0) {
            sb.delete(0, first);
        }

        for (last = sb.length(); last > first; last--) {
            if (!Character.isWhitespace(sb.charAt(last - 1))) {
                break;
            }
        }

        if (sb.length() > last) {
            sb.delete(last, sb.length());
        }

        return sb;
    }

    /**
     * NB: Only for internal purpose. Not recommended for developer use.
     *
     * @param from       content to be replaced from this StringBuilder
     * @param thisString the string to be replaced
     * @param with       with this string the thisString will be replaced.
     * @return the modified StringBuilder which is the same from.
     * @since 3.0.15
     */
    public static StringBuilder replaceFirst(final StringBuilder from, final String thisString, final String with) {

        final int idx = from.indexOf(thisString);

        if (idx > -1) {
            return from.replace(idx, idx + thisString.length(), with);
        }
        return from;
    }

}
