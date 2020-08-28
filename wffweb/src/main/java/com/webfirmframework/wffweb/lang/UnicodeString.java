/*
 * Copyright 2014-2020 Web Firm Framework
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
package com.webfirmframework.wffweb.lang;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.IntStream.Builder;

/**
 * An immutable class for unicode based string manipulation.
 *
 * @author WFF
 * @since 3.0.15
 *
 */
public final class UnicodeString {

    private static final int SPACE_CODE_POINT;

    private static final int COMMA_CODE_POINT;

    private static final int SEMICOLON_CODE_POINT;

    private static final int COLON_CODE_POINT;

    private static final int MINUS_CODE_POINT;

    private static final int PLUS_CODE_POINT;

    private static final int SLASH_T_CODE_POINT;

    private static final int SLASH_N_CODE_POINT;

    static {
        final int[] codePoints = " ,;:-+\t\n".codePoints().toArray();

        SPACE_CODE_POINT = codePoints[0];
        COMMA_CODE_POINT = codePoints[1];
        SEMICOLON_CODE_POINT = codePoints[2];
        COLON_CODE_POINT = codePoints[3];
        MINUS_CODE_POINT = codePoints[4];
        PLUS_CODE_POINT = codePoints[5];
        SLASH_T_CODE_POINT = codePoints[6];
        SLASH_N_CODE_POINT = codePoints[7];
    }

    private final int[] codePoints;

    private int hash;

    public UnicodeString(final String s) {
        this(s != null ? s.codePoints().toArray() : null);
    }

    public UnicodeString(final int[] codePoints) {
        super();
        this.codePoints = codePoints;
    }

    /**
     * @return the number of unicode chars contained in it or -1 if it is a null
     *         UnicodeString.
     */
    public int length() {
        if (codePoints != null) {
            return codePoints.length;
        }
        return -1;
    }

    public int[] codePoints() {
        if (codePoints != null) {
            return Arrays.copyOf(codePoints, codePoints.length);
        }
        return null;
    }

    public String newString() {
        if (codePoints != null) {
            return new String(codePoints, 0, codePoints.length);
        }
        return null;
    }

    @Override
    public boolean equals(final Object thatObj) {
        if (thatObj == null) {
            return false;
        }
        if (this == thatObj) {
            return true;
        }
        if (thatObj instanceof UnicodeString) {
            final UnicodeString obj = (UnicodeString) thatObj;

            if (codePoints == null && obj.codePoints == null) {
                return true;
            }

            if (hashCode() != obj.hashCode()) {
                return false;
            }

            return Objects.equals(obj.codePoints, codePoints);
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int h = hash;
        if (h == 0 && codePoints != null && codePoints.length > 0) {
            hash = Objects.hash(codePoints);
        }
        return h;
    }

    /**
     * @param startIndex
     * @param endIndex   exclusive
     * @return the substring
     */
    public UnicodeString substring(final int startIndex, final int endIndex) {
        final int[] ary = new int[endIndex - startIndex];
        System.arraycopy(codePoints, startIndex, ary, 0, ary.length);
        return new UnicodeString(ary);
    }

    /**
     * @param startIndex
     * @param endIndex   exclusive
     * @return the substring
     */
    public UnicodeString substring(final int startIndex) {
        final int[] ary = new int[codePoints.length - startIndex];
        System.arraycopy(codePoints, startIndex, ary, 0, ary.length);
        return new UnicodeString(ary);
    }

    /**
     * @param c the unicode char code
     * @return the index
     * @since 3.0.15
     */
    public int indexOf(final int c) {
        int index = 0;
        for (final int i : codePoints) {
            if (i == c) {
                return index;
            }
            index++;
        }
        return -1;
    }

    /**
     * @param c c the unicode char code
     * @return the index
     * @since 3.0.15
     */
    public int lastIndexOf(final int c) {

        for (int i = codePoints.length - 1; i >= 0; i--) {
            if (codePoints[i] == c) {
                return i;
            }
        }

        return -1;
    }

    /**
     *
     * @param forString the string for which the replacement to be done.
     * @param with      the matching to be replaced with this value.
     * @return the processed string
     * @since 3.0.15
     */
    public UnicodeString replace(final String forString, final String with) {
        return replace(codePoints, forString.codePoints().toArray(), with.codePoints().toArray());
    }

    /**
     *
     * @param forString the string for which the replacement to be done.
     * @param with      the matching to be replaced with this value.
     * @return the processed string
     * @since 3.0.15
     */
    public UnicodeString replace(final UnicodeString forString, final UnicodeString with) {
        return replace(codePoints, forString.codePoints, with.codePoints);
    }

    /**
     * @return the stripped UnicodeString
     * @since 3.0.15
     */
    public UnicodeString strip() {
        return new UnicodeString(strip(codePoints));
    }

    private static boolean isWhitespace(final int c) {
        return c == SPACE_CODE_POINT || c == SLASH_N_CODE_POINT || c == SLASH_T_CODE_POINT || Character.isWhitespace(c);
    }

    /**
     * @param codePoints         codePoints of string for processing.
     * @param codePointsSequence the array of code points for matching in its
     *                           sequential order.
     * @param withCodePoints     the matching to be replaced with this value.
     * @return the processed UnicodeString
     * @since 3.0.15
     */
    static UnicodeString replace(final int[] codePoints, final int[] codePointsSequence, final int[] withCodePoints) {

        if (codePoints == null || codePointsSequence == null || withCodePoints == null) {
            return new UnicodeString(codePoints);
        }

        if (codePoints.length == codePointsSequence.length && Arrays.equals(codePoints, codePointsSequence)) {
            return new UnicodeString(withCodePoints);
        }

        final int maxPossibleMatches = codePoints.length - (codePointsSequence.length - 1);

        if (maxPossibleMatches < 1) {
            return new UnicodeString(codePoints);
        }

        final Builder resultBuilder = IntStream.builder();

        if (maxPossibleMatches == codePoints.length && codePointsSequence.length == 1) {
            final int cpToMatch = codePointsSequence[0];
            if (withCodePoints.length > 0) {
                for (final int c : codePoints) {
                    if (c == cpToMatch) {
                        for (final int w : withCodePoints) {
                            resultBuilder.add(w);
                        }

                    } else {
                        resultBuilder.add(c);
                    }
                }
            } else {
                for (final int c : codePoints) {
                    if (c != cpToMatch) {
                        resultBuilder.add(c);
                    }
                }
            }
        } else {
            for (int i = 0; i < maxPossibleMatches; i++) {

                final int[] part = new int[codePointsSequence.length];

                System.arraycopy(codePoints, i, part, 0, part.length);

                if (Arrays.equals(part, codePointsSequence)) {
                    if (withCodePoints.length > 0) {
                        for (final int w : withCodePoints) {
                            resultBuilder.add(w);
                        }
                    }
                    i = i + (codePointsSequence.length - 1);
                } else {
                    resultBuilder.add(codePoints[i]);
                }

                if (i >= (maxPossibleMatches - 1)) {
                    final int nextIndex = i + 1;
                    final int total = codePoints.length - nextIndex;
                    for (int w = 0; w < total; w++) {
                        resultBuilder.add(codePoints[nextIndex + w]);
                    }
                }
            }

        }

        return new UnicodeString(resultBuilder.build().toArray());
    }

    /**
     * Removes the trailing and leading whitespaces
     *
     * @param codePoints
     * @return the striped codePoints string
     * @since 3.0.15
     */
    private static int[] strip(final int[] codePoints) {

        if (codePoints.length == 0) {
            return codePoints;
        }

        int first;
        int last;

        for (first = 0; first < codePoints.length; first++) {
            if (!isWhitespace(codePoints[first])) {
                break;
            }
        }

        for (last = codePoints.length; last > first; last--) {
            if (!isWhitespace(codePoints[last - 1])) {
                break;
            }
        }

        if (first == 0 && last == codePoints.length) {
            return codePoints;
        }

        final int lastRemovedCount = codePoints.length - last;

        final int codePointsCount = codePoints.length - (first + lastRemovedCount);

        final int[] result = new int[codePointsCount];
        System.arraycopy(codePoints, first, result, 0, result.length);

        return result;
    }

}
