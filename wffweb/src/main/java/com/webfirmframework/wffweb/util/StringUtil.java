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
package com.webfirmframework.wffweb.util;

import java.util.Collection;

/**
 *
 * @author WFF
 * @since 1.0.0
 */
public final class StringUtil {

    private static final int SPACE_CODE_POINT;

    private static final int COMMA_CODE_POINT;

    private static final int SEMICOLON_CODE_POINT;

    private static final int COLON_CODE_POINT;

    private static final int MINUS_CODE_POINT;

    private static final int PLUS_CODE_POINT;

    static {
        final int[] codePoints = " ,;:-+".codePoints().toArray();

        SPACE_CODE_POINT = codePoints[0];
        COMMA_CODE_POINT = codePoints[1];
        SEMICOLON_CODE_POINT = codePoints[2];
        COLON_CODE_POINT = codePoints[3];
        MINUS_CODE_POINT = codePoints[4];
        PLUS_CODE_POINT = codePoints[5];
    }

    private StringUtil() {
        throw new AssertionError();
    }

    /**
     * Converts all continues multiple spaces and new line/lines followed by
     * space/spaces to a single space.
     *
     * @param input the string to convert
     * @return the converted string having a single space in between characters.
     * @author WFF
     * @since 1.0.0
     */
    public static String convertToSingleSpace(final String input) {
        if (input.length() == 0) {
            return input;
        }
        if (!input.contains("  ")) {
            return input;
        }
        return input.replaceAll("\\s+", " ");
    }

    /**
     * Converts all continues multiple spaces to a single space.
     *
     * @param input the string to convert
     * @return the converted string having a single space in between characters.
     * @author WFF
     * @since 3.0.15 it is unicode aware
     */
    public static String convertSpacesToSingleSpace(final String input) {

        if (input.length() == 0) {
            return input;
        }

        final StringBuilder builder = new StringBuilder(input.length());

        int prevCP = 0;
        boolean zerothIndex = true;
        final int[] codePoints = input.codePoints().toArray();

        for (final int c : codePoints) {
            if (zerothIndex || prevCP != SPACE_CODE_POINT || c != SPACE_CODE_POINT) {
                builder.appendCodePoint(c);
            }

            prevCP = c;
            zerothIndex = false;
        }

        return builder.toString();
    }

    /**
     * Converts all continues multiple whitespaces to a single space. Please know
     * that new line and tab are also considered as whitespace.
     *
     * @param input the string to convert
     * @return the converted string having a single space in between characters.
     * @author WFF
     * @since 3.0.15 it is unicode aware.
     */
    public static String convertWhitespacesToSingleSpace(final String input) {

        if (input.length() == 0) {
            return input;
        }

        final StringBuilder builder = new StringBuilder(input.length());

        int prevCP = 0;
        boolean zerothIndex = true;

        final int[] codePoints = input.codePoints().toArray();
        for (final int c : codePoints) {
            final boolean ws = Character.isWhitespace(c);

            if (zerothIndex) {
                if (ws) {
                    builder.append(' ');
                } else {
                    builder.appendCodePoint(c);
                }
            } else if (!Character.isWhitespace(prevCP)) {
                if (ws) {
                    builder.append(' ');
                } else {
                    builder.appendCodePoint(c);
                }
            } else if (!ws) {
                builder.appendCodePoint(c);
            }

            prevCP = c;
            zerothIndex = false;
        }

        return builder.toString();
    }

    /**
     * Removes all spaces from the string.
     *
     * @param input
     * @return the string without spaces.
     * @since 3.0.15 it is unicode aware.
     */
    public static String removeSpaces(final String input) {
        if (input.length() == 0) {
            return input;
        }

        final StringBuilder builder = new StringBuilder(input.length());

        final int[] codePoints = input.codePoints().toArray();
        for (final int c : codePoints) {
            if (c != SPACE_CODE_POINT) {
                builder.appendCodePoint(c);
            }
        }

        return builder.toString();
    }

    /**
     * Removes all whitespaces from the string. New line and tab are also
     * whiltespace.
     *
     * @param input
     * @return the string without spaces.
     * @since 3.0.15 it is unicode aware.
     */
    public static String removeWhitespaces(final String input) {

        if (input.length() == 0) {
            return input;
        }

        final StringBuilder builder = new StringBuilder(input.length());

        final int[] codePoints = input.codePoints().toArray();
        for (final int c : codePoints) {
            if (!Character.isWhitespace(c)) {
                builder.appendCodePoint(c);
            }
        }

        return builder.toString();
    }

    /**
     * For future development. Removes all given codePoints from the string.
     *
     * @param input
     * @param codePoints
     * @return the string without spaces.
     * @since 3.0.15 it is unicode aware.
     */
    @SuppressWarnings("unused")
    private static String removeCodePoints(final String input, final int[] codePoints) {

        if (input.length() == 0) {
            return input;
        }

        final StringBuilder builder = new StringBuilder(input.length());

        final int[] codePointsAry = input.codePoints().toArray();
        for (final int c : codePointsAry) {

            boolean remove = false;
            for (final int cp : codePoints) {
                if (cp == c) {
                    remove = true;
                    break;
                }
            }
            if (!remove) {
                builder.appendCodePoint(c);
            }
        }

        return builder.toString();
    }

    /**
     * gets the first substring which starts and ends with the given values.
     *
     * @param inputString  the string from which the substring will be extracted.
     * @param startingWith to match the starting substring
     * @param endingWith   to match the ending substring
     * @return the substring which starts and ends with the given
     *         {@code startingWith} and {@code endingWith} values. Or returns null
     *         if it doesn't contain.
     * @author WFF
     * @since 1.0.0
     */
    public static String getFirstSubstring(final String inputString, final String startingWith,
            final String endingWith) {
        if (!inputString.contains(startingWith) || !inputString.contains(endingWith)) {
            return null;
        }
        final int startIndex = inputString.indexOf(startingWith);

        if (!((startIndex + 1) < inputString.length())) {
            return null;
        }

        final int endIndex = inputString.indexOf(endingWith, startIndex + 1) + 1;
        if (startIndex > endIndex || startIndex < 0 || endIndex < 0) {
            return null;
        }
        return inputString.substring(startIndex, endIndex);
    }

    /**
     * gets the first substring which starts and ends with the given values.
     *
     * @param inputString     the string from which the substring will be extracted.
     * @param startingWith    to match the starting substring
     * @param endingWith      to match the ending substring
     * @param searchFromIndex the starting index from where the substring should be
     *                        searched.
     *
     * @return the substring which starts and ends with the given
     *         {@code startingWith} and {@code endingWith} values. Or returns null
     *         if it doesn't contain.
     * @author WFF
     * @since 1.0.0
     */
    public static String getFirstSubstring(final String inputString, final String startingWith, final String endingWith,
            final int searchFromIndex) {
        if (!inputString.contains(startingWith) || !inputString.contains(endingWith)) {
            return null;
        }
        final int startIndex = inputString.indexOf(startingWith, searchFromIndex);

        if (!((startIndex + 1) < inputString.length())) {
            return null;
        }

        final int endIndex = inputString.indexOf(endingWith, startIndex + 1) + 1;
        if (startIndex > endIndex || startIndex < 0 || endIndex < 0) {
            return null;
        }
        return inputString.substring(startIndex, endIndex);
    }

    /**
     * gets all substrings as an array which starts and ends with the given
     * values.<br>
     * Note:- it will never return null instead it will return an empty array
     * (having length zero).
     *
     * <pre>
     *
     * final String[] allSubstrings = getAllSubstrings(&quot;abcd&quot;, &quot;ab&quot;, &quot;cd&quot;);
     * for (String each : allSubstrings) {
     *     System.out.println(each);
     * }
     * the output will be : <i>abcd</i>
     * </pre>
     *
     * @param inputString  the string from which the substrings will be extracted.
     * @param startingWith to match the starting substring
     * @param endingWith   to match the ending substring
     * @return the array of substrings which contains strings starting and ending
     *         with the given {@code startingWith} and {@code endingWith} values. Or
     *         returns an empty array (i.e an array having length zero) if it
     *         doesn't contain.
     * @author WFF
     * @since 1.0.0
     */
    public static String[] getAllSubstrings(final String inputString, final String startingWith,
            final String endingWith) {
        if (!inputString.contains(startingWith) || !inputString.contains(endingWith)) {
            return new String[0];
        }

        final int inputStringLength = inputString.length();

        final int endingWithLength = endingWith.length();
        final int maxArrayLength = inputStringLength / (startingWith.length() + endingWithLength);

        if (maxArrayLength == 0) {
            return new String[0];
        }

        final String[] maxSubstrings = new String[maxArrayLength];
        int startSearchFromIndex = 0;
        int count = 0;
        do {
            final int startIndex = inputString.indexOf(startingWith, startSearchFromIndex);
            if (!((startIndex + 1) < inputStringLength)) {
                break;
            }
            final int indexOfEndingWith = inputString.indexOf(endingWith, startIndex + 1);

            if (startIndex < 0 || indexOfEndingWith < 0) {
                break;
            }

            final int endIndex = indexOfEndingWith + endingWithLength;
            startSearchFromIndex = endIndex;
            if ((startIndex > endIndex)) {
                startSearchFromIndex = inputStringLength;
            } else {
                maxSubstrings[count] = inputString.substring(startIndex, endIndex);
                count++;
            }
        } while (startSearchFromIndex < inputStringLength);

        final String[] substrings = new String[count];

        // TODO check later if System.arraycopy is better than small sized
        // arrays.
        System.arraycopy(maxSubstrings, 0, substrings, 0, count);

        return substrings;
    }

    /**
     * @param inputString  the string from the index should be found.
     * @param startingWith the substring to check whether it comes in the given
     *                     inputString before {@code endingWith} substring.
     * @param endingWith   the substring to check whether it comes in the given
     *                     inputString after {@code startingWith} substring.
     * @return the index of {@code startingWith} substring if the given
     *         {@code inputString} contains a substring starting with
     *         {@code startingWith} string and ending with {@code endingWith}
     *         string, otherwise returns -1.
     * @author WFF
     * @since 1.0.0
     */
    public static int startIndexOf(final String inputString, final String startingWith, final String endingWith) {

        if (!inputString.contains(startingWith) || !inputString.contains(endingWith)) {
            return -1;
        }
        final int startIndex = inputString.indexOf(startingWith);
        if (!((startIndex + 1) < inputString.length())) {
            return -1;
        }
        final int endIndex = inputString.indexOf(endingWith, startIndex + 1);
        if (startIndex > endIndex || startIndex < 0 || endIndex < 0) {
            return -1;
        }
        return startIndex;
    }

    /**
     * @param inputString  the string from the index should be found.
     * @param startingWith the substring to check whether it comes in the given
     *                     inputString before {@code endingWith} substring.
     * @param endingWith   the substring to check whether it comes in the given
     *                     inputString after {@code startingWith} substring.
     * @return the index of {@code endingWith} substring if the given
     *         {@code inputString} contains a substring starting with
     *         {@code startingWith} string and ending with {@code endingWith}
     *         string, otherwise returns -1.
     * @author WFF
     * @since 1.0.0
     */
    public static int endIndexOf(final String inputString, final String startingWith, final String endingWith) {

        if (!inputString.contains(startingWith) || !inputString.contains(endingWith)) {
            return -1;
        }
        final int startIndex = inputString.indexOf(startingWith);
        if (!((startIndex + 1) < inputString.length())) {
            return -1;
        }
        final int endIndex = inputString.indexOf(endingWith, startIndex + 1);
        if (startIndex > endIndex || startIndex < 0 || endIndex < 0) {
            return -1;
        }
        return endIndex;
    }

    /**
     * @param inputString     the string from the index should be found.
     * @param startingWith    the substring to check whether it comes in the given
     *                        inputString before {@code endingWith} substring.
     * @param endingWith      the substring to check whether it comes in the given
     *                        inputString after {@code startingWith} substring.
     * @param searchFromIndex the starting index from where the substring should be
     *                        searched.
     * @return the index of {@code startingWith} substring if the given
     *         {@code inputString} contains a substring starting with
     *         {@code startingWith} string and ending with {@code endingWith}
     *         string, otherwise returns -1.
     * @author WFF
     * @since 1.0.0
     */
    public static int startIndexOf(final String inputString, final String startingWith, final String endingWith,
            final int searchFromIndex) {

        if (!inputString.contains(startingWith) || !inputString.contains(endingWith)) {
            return -1;
        }
        final int startIndex = inputString.indexOf(startingWith, searchFromIndex);
        if (!((startIndex + 1) < inputString.length())) {
            return -1;
        }
        final int endIndex = inputString.indexOf(endingWith, startIndex + 1);
        if (startIndex > endIndex || startIndex < 0 || endIndex < 0) {
            return -1;
        }
        return startIndex;
    }

    /**
     * @param inputString     the string from the index should be found.
     * @param startingWith    the substring to check whether it comes in the given
     *                        inputString before {@code endingWith} substring.
     * @param endingWith      the substring to check whether it comes in the given
     *                        inputString after {@code startingWith} substring.
     * @param searchFromIndex the starting index from where the substring should be
     *                        searched.
     * @return the index of {@code endingWith} substring if the given
     *         {@code inputString} contains a substring starting with
     *         {@code startingWith} string and ending with {@code endingWith}
     *         string, otherwise returns -1.
     * @author WFF
     * @since 1.0.0
     */
    public static int endIndexOf(final String inputString, final String startingWith, final String endingWith,
            final int searchFromIndex) {

        if (!inputString.contains(startingWith) || !inputString.contains(endingWith)) {
            return -1;
        }
        final int startIndex = inputString.indexOf(startingWith, searchFromIndex);

        if (!((startIndex + 1) < inputString.length())) {
            return -1;
        }

        final int endIndex = inputString.indexOf(endingWith, startIndex + 1);
        if (startIndex > endIndex || startIndex < 0 || endIndex < 0) {
            return -1;
        }
        return endIndex;
    }

    /**
     * @param inputString  the string from the index should be found.
     * @param startingWith the substring to check whether it comes in the given
     *                     inputString before {@code endingWith} substring.
     * @param endingWith   the substring to check whether it comes in the given
     *                     inputString after {@code startingWith} substring.
     * @return an array containing the index of {@code startingWith} substring if
     *         the given {@code inputString} contains a substring starting with
     *         {@code startingWith} string and ending with {@code endingWith}
     *         string, otherwise returns an empty array.
     * @author WFF
     * @since 1.0.0
     */
    public static int[] startIndexAndEndIndexOf(final String inputString, final String startingWith,
            final String endingWith) {

        if (!inputString.contains(startingWith) || !inputString.contains(endingWith)) {
            return new int[] {};
        }

        final int startIndex = inputString.indexOf(startingWith);

        if (!((startIndex + 1) < inputString.length())) {
            return new int[] {};
        }

        final int endIndex = inputString.indexOf(endingWith, startIndex + 1);
        if (startIndex > endIndex || startIndex < 0 || endIndex < 0) {
            return new int[] {};
        }
        return new int[] { startIndex, endIndex };
    }

    /**
     * @param inputString     the string from the index should be found.
     * @param startingWith    the substring to check whether it comes in the given
     *                        inputString before {@code endingWith} substring.
     * @param endingWith      the substring to check whether it comes in the given
     *                        inputString after {@code startingWith} substring.
     * @param searchFromIndex the starting index from where the substring should be
     *                        searched.
     * @return an array containing the index of {@code startingWith} substring if
     *         the given {@code inputString} contains a substring starting with
     *         {@code startingWith} string and ending with {@code endingWith}
     *         string, otherwise returns an empty array.
     * @author WFF
     * @since 1.0.0
     */
    public static int[] startIndexAndEndIndexOf(final String inputString, final String startingWith,
            final String endingWith, final int searchFromIndex) {

        if (!inputString.contains(startingWith) || !inputString.contains(endingWith)) {
            return new int[] {};
        }

        final int startIndex = inputString.indexOf(startingWith, searchFromIndex);

        if (!((startIndex + 1) < inputString.length())) {
            return new int[0];
        }

        final int endIndex = inputString.indexOf(endingWith, startIndex + 1);
        if (startIndex > endIndex || startIndex < 0 || endIndex < 0) {
            return new int[] {};
        }
        return new int[] { startIndex, endIndex };
    }

    /**
     * gets all start and end indexes as an array of array which start and end with
     * the given values.<br>
     * Note:- it will never return null instead it will return an empty array
     * (having length zero).
     *
     * <pre>
     *
     *         final int[][] startAndEndIndexesOf = startAndEndIndexesOf(
     *                 "121 131 141 151 161 171 181 191 101", "1", "1");
     *
     *         for (final int[] startIndexAndEndIndex : startAndEndIndexesOf) {
     *             System.out.println(startIndexAndEndIndex[0] + " - "
     *                     + startIndexAndEndIndex[1]);
     *         }
     *
     * the output will be :
     * <i>
     * 0 - 3
     * 4 - 7
     * 8 - 11
     * 12 - 15
     * 16 - 19
     * 20 - 23
     * 24 - 27
     * 28 - 31
     * 32 - 35
     * </i>
     *
     *         int[] startIndexAndEndIndex = startAndEndIndexesOf[0]; // 1, 2 etc..
     *         System.out.println(startIndexAndEndIndex[0] + " - "
     *                 + startIndexAndEndIndex[1]);
     *
     * the output will be : <i>0 - 3</i>
     * </pre>
     *
     * @param inputString  the string from which the substrings will be extracted.
     * @param startingWith to match the starting substring
     * @param endingWith   to match the ending substring
     * @return the array of substrings which contains strings starting and ending
     *         with the given {@code startingWith} and {@code endingWith} values. Or
     *         returns an empty array (i.e an array having length zero) if it
     *         doesn't contain.
     * @author WFF
     * @since 1.0.0
     */
    public static int[][] startAndEndIndexesOf(final String inputString, final String startingWith,
            final String endingWith) {
        if (!inputString.contains(startingWith) || !inputString.contains(endingWith)) {
            return new int[0][0];
        }

        final int inputStringLength = inputString.length();

        final int endingWithLength = endingWith.length();
        final int maxArrayLength = inputStringLength / (startingWith.length() + endingWithLength);

        if (maxArrayLength == 0) {
            return new int[0][0];
        }

        final int[][] maxIndexes = new int[maxArrayLength][0];
        int startSearchFromIndex = 0;
        int count = 0;
        do {
            final int startIndex = inputString.indexOf(startingWith, startSearchFromIndex);

            if (!((startIndex + 1) < inputStringLength)) {
                return new int[0][0];
            }

            final int indexOfEndingWith = inputString.indexOf(endingWith, startIndex + 1);

            if (startIndex < 0 || indexOfEndingWith < 0) {
                break;
            }

            final int endIndex = indexOfEndingWith + endingWithLength - 1;
            startSearchFromIndex = endIndex;
            if ((startIndex > endIndex)) {
                startSearchFromIndex = inputStringLength;
            } else {
                final int[] is = { startIndex, endIndex };
                maxIndexes[count] = is;
                count++;
            }
        } while (startSearchFromIndex < inputStringLength);

        final int[][] indexes = new int[count][0];

        // TODO check later if System.arraycopy is better than small sized
        // arrays.
        System.arraycopy(maxIndexes, 0, indexes, 0, count);

        return indexes;
    }

    /**
     * To make the clone copy of the given String array. This method is faster than
     * the clone method of String array.
     *
     * @param inputArray
     * @return the cloned array of the given string array.
     * @author WFF
     * @since 1.0.0
     */
    public static String[] cloneArray(final String[] inputArray) {
        final String[] array = new String[inputArray.length];
        System.arraycopy(inputArray, 0, array, 0, inputArray.length);
        return array;
    }

    /**
     *
     * @author WFF
     * @return true if all strings are equal or null.
     * @since 1.0.0
     */
    public static boolean isEqual(final String... strings) {
        final Object[] objects = strings;
        return ObjectUtil.isEqual(objects);
    }

    /**
     *
     * @author WFF
     * @return true if two strings are equal or null.
     * @since 1.0.0
     */
    public static boolean isEqual(final String string1, final String string2) {
        return ObjectUtil.isEqual(string1, string2);
    }

    /**
     * @param value
     * @return true if the given value is starting with a white space
     * @since 2.1.8 initial implementation.
     * @since 3.0.15 unicode aware.
     * @author WFF
     */
    public static boolean startsWithWhitespace(final String value) {
        if (value.length() == 0) {
            return false;
        }

        // Old
        // return Character.isWhitespace(value.charAt(0));

        // New
        // NB: doesn't work
//      return Character.isWhitespace(value.codePointAt((int) (value.codePoints().count() - 1)));

        return Character.isWhitespace(value.codePoints().toArray()[0]);
    }

    /**
     * @param value
     * @return true if the given value is ending with a white space
     * @since 2.1.8 initial implementation.
     * @since 3.0.15 unicode aware.
     * @author WFF
     */
    public static boolean endsWithWhitespace(final String value) {
        if (value.length() == 0) {
            return false;
        }

        // NB: doesn't work
//        return Character.isWhitespace(value.codePointAt((int) (value.codePoints().count() - 1)));

        final int[] codePoints = value.codePoints().toArray();

        return Character.isWhitespace(codePoints[codePoints.length - 1]);
    }

    /**
     * @param string the string to split.
     * @param delim  to by which the given string to be split.
     * @return the array of strings split by the given char.
     * @since 3.0.0 public
     * @author WFF
     */
    public static String[] split(final String string, final char delim) {

        final CharSequence[] tmp = new CharSequence[(string.length() / 2) + 1];
        int subCount = 0;
        int i = 0;
        // first substring
        int j = string.indexOf(delim, 0);

        while (j >= 0) {
            tmp[subCount++] = string.substring(i, j);
            i = j + 1;
            // rest of substrings
            j = string.indexOf(delim, i);
        }
        // last substring
        tmp[subCount++] = string.substring(i);

        final String[] finalArray = new String[subCount];
        System.arraycopy(tmp, 0, finalArray, 0, subCount);
        return finalArray;
    }

    /**
     * @param string the string to split.
     * @param delim  to by which the given string to be split. It is the code point
     *               value of char.
     * @return the array of strings split by the given char.
     * @since 3.0.15
     * @author WFF
     */
    public static String[] split(final String string, final int delim) {

        if (string.length() == 0) {
            return new String[] { string };
        }

        final int[] codePoints = string.codePoints().toArray();

        final int[] delimPositionInit = new int[codePoints.length];

        int delimCount = 0;
        for (int i = 0; i < codePoints.length; i++) {
            if (codePoints[i] == delim) {
                delimPositionInit[delimCount] = i;
                delimCount++;
            }
        }

        if (delimCount == 0) {
            return new String[] { string };
        }

        final int[] delimPositionsFinal = new int[delimCount];

        System.arraycopy(delimPositionInit, 0, delimPositionsFinal, 0, delimPositionsFinal.length);

        final String[] splittedStrings = new String[delimCount + 1];

        int startIndex = 0;
        for (int i = 0; i < delimPositionsFinal.length; i++) {
            final int delimPosition = delimPositionsFinal[i];

            int range = codePoints.length - (codePoints.length - delimPosition);
            range = range - startIndex;

            if (range > 0) {
                splittedStrings[i] = new String(codePoints, startIndex, range);
            } else {
                splittedStrings[i] = "";
            }

            startIndex = delimPosition + 1;
        }

        final int lastDelimPosition = delimPositionsFinal[delimPositionsFinal.length - 1];

        startIndex = lastDelimPosition + 1;

        final int range = codePoints.length - startIndex;

        if (range > 0) {
            splittedStrings[splittedStrings.length - 1] = new String(codePoints, startIndex, range);
        } else {
            splittedStrings[splittedStrings.length - 1] = "";
        }

        return splittedStrings;
    }

    /**
     * Splits the given string by space.
     *
     * @param string
     * @return the array of strings
     * @since 2.1.15
     * @author WFF
     */
    public static String[] splitBySpace(final String string) {
        return split(string, SPACE_CODE_POINT);
    }

    /**
     * Splits the given string by comma (,).
     *
     * @param string
     * @return the array of strings
     * @since 2.1.15
     * @author WFF
     */
    public static String[] splitByComma(final String string) {
        return split(string, COMMA_CODE_POINT);
    }

    /**
     * Splits the given string by semicolon (;).
     *
     * @param string
     * @return the array of strings
     * @since 2.1.15
     * @author WFF
     */
    public static String[] splitBySemicolon(final String string) {
        return split(string, SEMICOLON_CODE_POINT);
    }

    /**
     * Splits the given string by colon (:).
     *
     * @param string
     * @return the array of strings
     * @since 2.1.15
     * @author WFF
     */
    public static String[] splitByColon(final String string) {
        return split(string, COLON_CODE_POINT);
    }

    @SuppressWarnings("unused")
    private static boolean startsWith(final String string, final char c) {
        if (string.length() == 0) {
            return false;
        }
        return string.charAt(0) == c;
    }

    @SuppressWarnings("unused")
    private static boolean endsWith(final String string, final char c) {
        if (string.length() == 0) {
            return false;
        }
        return string.charAt(string.length() - 1) == c;
    }

    /**
     * @param string
     * @param c      codePoint
     * @return true or false
     * @since 3.0.15
     */
    private static boolean startsWith(final String string, final int c) {
        if (string.length() == 0) {
            return false;
        }
        return string.codePoints().toArray()[0] == c;
    }

    /**
     * @param string
     * @param c      codePoint
     * @return true or false
     * @since 3.0.15
     */
    private static boolean endsWith(final String string, final int c) {
        if (string.length() == 0) {
            return false;
        }
        final int[] codePoints = string.codePoints().toArray();
        return codePoints[codePoints.length - 1] == c;
    }

    /**
     * Checks if the last char is a space char
     *
     * @param string
     * @return true if the last character is a space char
     * @since 2.1.15
     * @author WFF
     */
    public static boolean endsWithSpace(final String string) {
        return endsWith(string, SPACE_CODE_POINT);
    }

    /**
     * Checks if the last char is a colon (:) char
     *
     * @param string
     * @return true if the last character is a colon (:) char
     * @since 2.1.15
     * @author WFF
     */
    public static boolean endsWithColon(final String string) {
        return endsWith(string, COLON_CODE_POINT);
    }

    /**
     * Checks if the first char is a space char
     *
     * @param string
     * @return true if the first character is a space char
     * @since 2.1.15
     * @author WFF
     */
    public static boolean startsWithSpace(final String string) {
        return startsWith(string, SPACE_CODE_POINT);
    }

    @SuppressWarnings("unused")
    private static boolean contains(final String string, final char c) {
        return string.indexOf(c) != -1;
    }

    /**
     * @param string
     * @param c      codePoint
     * @return
     */
    private static boolean contains(final String string, final int c) {
        return string.codePoints().anyMatch(e -> c == e);
    }

    /**
     * Checks if the given string contains space.
     *
     * @param string
     * @return true if the given string contains space char.
     * @since 2.1.15
     * @author WFF
     */
    public static boolean containsSpace(final String string) {
        return contains(string, SPACE_CODE_POINT);
    }

    /**
     * Checks if the given string contains whitespace.
     *
     * @param string
     * @return true if the given string contains space char.
     * @since 3.0.1 initial implementation.
     * @since 3.0.15 it is unicode aware.
     * @author WFF
     */
    public static boolean containsWhitespace(final String string) {
        if (string.length() == 0) {
            return false;
        }

        final int[] codePoints = string.codePoints().toArray();

        for (int i = 0; i < codePoints.length; i++) {
            if (Character.isWhitespace(codePoints[i])) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the given string contains minus (-).
     *
     * @param string
     * @return true if the given string contains minus (-) char.
     * @since 2.1.15
     * @author WFF
     */
    public static boolean containsMinus(final String string) {
        return contains(string, MINUS_CODE_POINT);
    }

    /**
     * Checks if the given string contains plus (+) char.
     *
     * @param string
     * @return true if the given string contains plus (+) char.
     * @since 2.1.15
     * @author WFF
     */
    public static boolean containsPlus(final String string) {
        return contains(string, PLUS_CODE_POINT);
    }

    /**
     *
     * Eg:
     *
     * <pre>
     * String join = StringUtil.join(',', ':', ';', "one", "two", "three", "four");
     * prints <i>:one,two,three,four;</i>
     * </pre>
     *
     * This method must be faster than Java 8's StringJoiner.
     *
     * @param delimiter
     * @param prefix
     * @param suffix
     * @param items
     * @return String
     * @since 3.0.1
     */
    public static String join(final char delimiter, final char prefix, final char suffix, final String... items) {

        // total delimiters: items.length -1
        // prefix and suffix: 2
        int capacity = (items.length - 1) + 2;
        for (final String item : items) {
            capacity += item.length();
        }

        final StringBuilder builder = new StringBuilder(capacity);
        builder.append(prefix);

        for (final String item : items) {
            builder.append(item).append(delimiter);
        }

        builder.deleteCharAt(builder.length() - 1);
        builder.append(suffix);

        return builder.toString();
    }

    /**
     * Eg:
     *
     * <pre>
     * String join = StringUtil.join(',', "one", "two", "three", "four");
     * prints <i>one,two,three,four</i>
     * </pre>
     *
     * This method must be faster than Java 8's String.join method.
     *
     * @param delimiter
     * @param items
     * @return String
     * @since 3.0.1
     */
    public static String join(final char delimiter, final String... items) {

        // total delimiters: items.length -1
        int capacity = (items.length - 1);
        for (final String item : items) {
            capacity += item.length();
        }

        final StringBuilder builder = new StringBuilder(capacity);

        for (final String item : items) {
            builder.append(item).append(delimiter);
        }

        builder.deleteCharAt(builder.length() - 1);

        return builder.toString();
    }

    /**
     * Eg:
     *
     * <pre>
     * String join = StringUtil.join(',', "one", "two", "three", "four");
     * prints <i>one,two,three,four</i>
     * </pre>
     *
     * This method must be faster than Java 8's String.join method.
     *
     * @param delimiter
     * @param items
     * @return String
     * @since 3.0.1
     */
    public static String join(final char delimiter, final Collection<String> items) {

        // total delimiters: items.length -1
        int capacity = (items.size() - 1);
        for (final String item : items) {
            capacity += item.length();
        }

        final StringBuilder builder = new StringBuilder(capacity);

        for (final String item : items) {
            builder.append(item).append(delimiter);
        }

        builder.deleteCharAt(builder.length() - 1);

        return builder.toString();
    }

    /**
     * @param s String to check
     * @return true if the given string doesn't contain any char other than
     *         whitespace.
     * @since 3.0.1 initial implementation.
     * @since 3.0.15 it is unicode aware.
     */
    public static boolean isBlank(final String s) {
        if (s.length() == 0) {
            return true;
        }

        final int[] codePoints = s.codePoints().toArray();
        for (int i = 0; i < codePoints.length; i++) {
            if (!Character.isWhitespace(codePoints[i])) {
                return false;
            }
        }

        return true;
    }

    /**
     * Removes the trailing and leading whitespaces
     *
     * @param s
     * @return the striped string
     * @since 3.0.1 initial implementation.
     * @since 3.0.15 it is unicode aware.
     */
    public static String strip(final String s) {

        if (s.length() == 0) {
            return s;
        }

        int first;
        int last;

        final int[] codePoints = s.codePoints().toArray();
        for (first = 0; first < s.length(); first++) {
            if (!Character.isWhitespace(codePoints[first])) {
                break;
            }
        }

        for (last = s.length(); last > first; last--) {
            if (!Character.isWhitespace(codePoints[last - 1])) {
                break;
            }
        }

        final int lastRemovedCount = codePoints.length - last;

        final int codePointsCount = codePoints.length - (first + lastRemovedCount);

        return new String(codePoints, first, codePointsCount);
    }

}
