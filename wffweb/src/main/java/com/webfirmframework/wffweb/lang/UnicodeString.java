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

    @SuppressWarnings("unused")
    private static final int COMMA_CODE_POINT;

    @SuppressWarnings("unused")
    private static final int SEMICOLON_CODE_POINT;

    @SuppressWarnings("unused")
    private static final int COLON_CODE_POINT;

    @SuppressWarnings("unused")
    private static final int MINUS_CODE_POINT;

    @SuppressWarnings("unused")
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
    public int hashCode() {
        final int h = hash;
        if (h == 0) {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.hashCode(codePoints);
            hash = result;
            return result;
        }

        return h;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final UnicodeString other = (UnicodeString) obj;
        if (!Arrays.equals(codePoints, other.codePoints)) {
            return false;
        }

        return true;
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
     * @param uc the UnicodeString
     * @return the index
     * @since 3.0.15
     */
    public int indexOf(final UnicodeString uc) {
        if (uc == null) {
            return -1;
        }
        return indexOf(codePoints, uc.codePoints);
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
     * @param uc the UnicodeString
     * @return the index from last
     * @since 3.0.15
     */
    public int lastIndexOf(final UnicodeString uc) {

        if (uc == null) {
            return -1;
        }

        return lastIndexOf(codePoints, uc.codePoints);
    }

    /**
     * @param c c the unicode char code
     * @return the index from last
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
     * @param us
     * @return true if this UnicodeString starts with the given UnicodeString.
     * @since 3.0.15
     */
    public boolean startsWith(final UnicodeString us) {
        if (codePoints == null || us == null || us.codePoints == null || codePoints.length < us.codePoints.length) {
            return false;
        }

        final int[] ary = new int[us.codePoints.length];
        System.arraycopy(codePoints, 0, ary, 0, ary.length);

        return Arrays.equals(ary, us.codePoints);
    }

    /**
     * @param us
     * @return true if this UnicodeString ends with the given UnicodeString.
     * @since 3.0.15
     */
    public boolean endsWith(final UnicodeString us) {
        if (codePoints == null || us == null || us.codePoints == null || codePoints.length < us.codePoints.length) {
            return false;
        }

        final int[] ary = new int[us.codePoints.length];
        final int startIndex = codePoints.length - us.codePoints.length;
        System.arraycopy(codePoints, startIndex, ary, 0, ary.length);

        return Arrays.equals(ary, us.codePoints);
    }

    /**
     * @return the stripped UnicodeString
     * @since 3.0.15
     */
    public UnicodeString strip() {
        return new UnicodeString(strip(codePoints));
    }

    /**
     * @param delim to by which the given string to be split. It is the code point
     *              value of char.
     * @return the array of UnicodeStrings split by the given unicode char code.
     * @since 3.0.15
     */
    public UnicodeString[] split(final int delim) {
        return split(codePoints, delim);
    }

    /**
     * @param delims to by which the given string to be split. It is the code point
     *               values of chars, i.e. unicode values.
     * @return the array of UnicodeStrings split by the given chars.
     * @since 3.0.15
     */
    public UnicodeString[] splitByAny(final int[] delims) {
        return splitByAny(codePoints, delims);
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

            final int[] part = new int[codePointsSequence.length];

            for (int i = 0; i < maxPossibleMatches; i++) {

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
     * @param codePoints         codePoints of string for processing.
     * @param codePointsSequence the array of code points for matching in its
     *                           sequential order.
     * @return the index of codePointsSequence in codePoints
     * @since 3.0.15
     */
    static int indexOf(final int[] codePoints, final int[] codePointsSequence) {

        if (codePoints == null || codePointsSequence == null) {
            return -1;
        }

        if (codePoints.length == codePointsSequence.length && Arrays.equals(codePoints, codePointsSequence)) {
            return 0;
        }

        final int maxPossibleMatches = codePoints.length - (codePointsSequence.length - 1);

        if (maxPossibleMatches < 1) {
            return -1;
        }

        if (maxPossibleMatches == codePoints.length && codePointsSequence.length == 1) {
            final int cpToMatch = codePointsSequence[0];
            int index = 0;
            for (final int c : codePoints) {
                if (c == cpToMatch) {
                    return index;
                }
                index++;
            }
        } else {

            final int[] part = new int[codePointsSequence.length];
            for (int i = 0; i < maxPossibleMatches; i++) {

                System.arraycopy(codePoints, i, part, 0, part.length);

                if (Arrays.equals(part, codePointsSequence)) {
                    return i;
                }

            }

        }

        return -1;
    }

    /**
     * @param codePoints         codePoints of string for processing.
     * @param codePointsSequence the array of code points for matching in its
     *                           sequential order.
     * @return the last index of codePointsSequence in codePoints
     * @since 3.0.15
     */
    static int lastIndexOf(final int[] codePoints, final int[] codePointsSequence) {

        if (codePoints == null || codePointsSequence == null) {
            return -1;
        }

        if (codePoints.length == codePointsSequence.length && Arrays.equals(codePoints, codePointsSequence)) {
            return 0;
        }

        final int maxPossibleMatches = codePoints.length - (codePointsSequence.length - 1);

        if (maxPossibleMatches < 1) {
            return -1;
        }

        if (maxPossibleMatches == codePoints.length && codePointsSequence.length == 1) {
            final int cpToMatch = codePointsSequence[0];

            for (int i = codePoints.length - 1; i >= 0; i--) {
                if (codePoints[i] == cpToMatch) {
                    return i;
                }
            }

        } else {

            final int[] part = new int[codePointsSequence.length];

            for (int i = codePoints.length - codePointsSequence.length; i >= 0; i--) {

                System.arraycopy(codePoints, i, part, 0, part.length);

                if (Arrays.equals(part, codePointsSequence)) {
                    return i;
                }
            }

        }

        return -1;
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

    /**
     * @param codePoints the codePoints to split.
     * @param delim      to by which the given string to be split. It is the code
     *                   point value of char.
     * @return the array of UnicodeStrings split by the given char.
     * @since 3.0.15
     */
    private static UnicodeString[] split(final int[] codePoints, final int delim) {

        if (codePoints == null || codePoints.length == 0) {
            return new UnicodeString[] { new UnicodeString(codePoints) };
        }

        final int[] delimPositionInit = new int[codePoints.length];

        int delimCount = 0;
        for (int i = 0; i < codePoints.length; i++) {
            if (codePoints[i] == delim) {
                delimPositionInit[delimCount] = i;
                delimCount++;
            }
        }

        if (delimCount == 0) {
            return new UnicodeString[] { new UnicodeString(codePoints) };
        }

        final int[] delimPositionsFinal = new int[delimCount];

        System.arraycopy(delimPositionInit, 0, delimPositionsFinal, 0, delimPositionsFinal.length);

        final UnicodeString[] splittedStrings = new UnicodeString[delimCount + 1];

        int startIndex = 0;
        for (int i = 0; i < delimPositionsFinal.length; i++) {
            final int delimPosition = delimPositionsFinal[i];

            int range = codePoints.length - (codePoints.length - delimPosition);
            range = range - startIndex;

            if (range > 0) {
                final int[] part = new int[range];
                System.arraycopy(codePoints, startIndex, part, 0, part.length);

                splittedStrings[i] = new UnicodeString(part);
            } else {
                splittedStrings[i] = new UnicodeString(new int[0]);
            }

            startIndex = delimPosition + 1;
        }

        final int lastDelimPosition = delimPositionsFinal[delimPositionsFinal.length - 1];

        startIndex = lastDelimPosition + 1;

        final int range = codePoints.length - startIndex;

        if (range > 0) {
            final int[] part = new int[range];
            System.arraycopy(codePoints, startIndex, part, 0, part.length);
            splittedStrings[splittedStrings.length - 1] = new UnicodeString(part);
        } else {
            splittedStrings[splittedStrings.length - 1] = new UnicodeString(new int[0]);
        }

        return splittedStrings;
    }

    /**
     * @param codePoints the codePoints to split.
     * @param delims     to by which the given string to be split. It is the code
     *                   point values of chars.
     * @return the array of UnicodeStrings split by the given chars.
     * @since 3.0.15
     */
    private static UnicodeString[] splitByAny(final int[] codePoints, final int[] delims) {

        if (codePoints == null || codePoints.length == 0) {
            return new UnicodeString[] { new UnicodeString(codePoints) };
        }

        final int[] delimPositionInit = new int[codePoints.length];

        final int[] sortedDelims = Arrays.copyOf(delims, delims.length);
        Arrays.sort(sortedDelims);
        int delimCount = 0;
        for (int i = 0; i < codePoints.length; i++) {

            // NB: should be >= 0 it should not be != -1
            if (Arrays.binarySearch(sortedDelims, codePoints[i]) >= 0) {
                delimPositionInit[delimCount] = i;
                delimCount++;
            }
        }

        if (delimCount == 0) {
            return new UnicodeString[] { new UnicodeString(codePoints) };
        }

        final int[] delimPositionsFinal = new int[delimCount];

        System.arraycopy(delimPositionInit, 0, delimPositionsFinal, 0, delimPositionsFinal.length);

        final UnicodeString[] splittedStrings = new UnicodeString[delimCount + 1];

        int startIndex = 0;
        for (int i = 0; i < delimPositionsFinal.length; i++) {
            final int delimPosition = delimPositionsFinal[i];

            int range = codePoints.length - (codePoints.length - delimPosition);
            range = range - startIndex;

            if (range > 0) {
                final int[] part = new int[range];
                System.arraycopy(codePoints, startIndex, part, 0, part.length);

                splittedStrings[i] = new UnicodeString(part);
            } else {
                splittedStrings[i] = new UnicodeString(new int[0]);
            }

            startIndex = delimPosition + 1;
        }

        final int lastDelimPosition = delimPositionsFinal[delimPositionsFinal.length - 1];

        startIndex = lastDelimPosition + 1;

        final int range = codePoints.length - startIndex;

        if (range > 0) {
            final int[] part = new int[range];
            System.arraycopy(codePoints, startIndex, part, 0, part.length);
            splittedStrings[splittedStrings.length - 1] = new UnicodeString(part);
        } else {
            splittedStrings[splittedStrings.length - 1] = new UnicodeString(new int[0]);
        }

        return splittedStrings;
    }

}
