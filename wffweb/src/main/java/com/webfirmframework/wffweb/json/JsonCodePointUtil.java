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
package com.webfirmframework.wffweb.json;

/**
 * Only for internal use.
 *
 * @since 12.0.4
 */
final class JsonCodePointUtil {

    private static final int[] LONG_MIN_VALUE_CODE_POINTS = String.valueOf(Long.MIN_VALUE).codePoints().toArray();

    private static final int[] LONG_MAX_VALUE_CODE_POINTS = String.valueOf(Long.MAX_VALUE).codePoints().toArray();

//    private static final int MINUS_CODE_POINT = "-".codePointAt(0);
    private static final int MINUS_CODE_POINT = 45;

    // static final int E_UPPER_CASE_CODE_POINT = "E".codePointAt(0);
    private static final int E_UPPER_CASE_CODE_POINT = 69;

//    static final int E_LOWER_CASE_CODE_POINT = "e".codePointAt(0);
    private static final int E_LOWER_CASE_CODE_POINT = 101;

//    static final int ESCAPE_CODE_POINT = "\\".codePointAt(0);
    static final int ESCAPE_CODE_POINT = 92;

//    static final int DOUBLE_QUOTES_CODE_POINT = "\"".codePointAt(0);
    static final int DOUBLE_QUOTES_CODE_POINT = 34;

//    private static final int SPACE_CODE_POINT = " ".codePointAt(0);
    private static final int SPACE_CODE_POINT = 32;

//    static final int SLASH_N_CODE_POINT = "\n".codePointAt(0);
    static final int SLASH_N_CODE_POINT = 10;

//    static final int SLASH_R_CODE_POINT = "\r".codePointAt(0);
    static final int SLASH_R_CODE_POINT = 13;

//    static final int SLASH_T_CODE_POINT = "\t".codePointAt(0);
    static final int SLASH_T_CODE_POINT = 9;

//    static final int U_CODE_POINT = "u".codePointAt(0);
    static final int U_CODE_POINT = 117;

//    private static final int ZERO_CODE_POINT = "0".codePointAt(0);
    private static final int ZERO_CODE_POINT = 48;

//    private static final int NINE_CODE_POINT = "9".codePointAt(0);
    private static final int NINE_CODE_POINT = 57;

//    private static final int A_UPPER_CASE_CODE_POINT = "A".codePointAt(0);
    private static final int A_UPPER_CASE_CODE_POINT = 65;

//    private static final int A_LOWER_CASE_CODE_POINT = "a".codePointAt(0);
    private static final int A_LOWER_CASE_CODE_POINT = 97;

//    private static final int F_UPPER_CASE_CODE_POINT = "F".codePointAt(0);
    private static final int F_UPPER_CASE_CODE_POINT = 70;

//    private static final int F_LOWER_CASE_CODE_POINT = "f".codePointAt(0);
    private static final int F_LOWER_CASE_CODE_POINT = 102;

    // Note: it should be sorted in ascending order, call
    // Arrays.sort(HEXA_CODE_POINTS_SORTED_ASC); to dynamically sort
//    private static final int[] HEXA_CODE_POINTS_SORTED_ASC = {
//            "0".codePointAt(0),
//            "1".codePointAt(0),
//            "2".codePointAt(0),
//            "3".codePointAt(0),
//            "4".codePointAt(0),
//            "5".codePointAt(0),
//            "6".codePointAt(0),
//            "7".codePointAt(0),
//            "8".codePointAt(0),
//            "9".codePointAt(0),
//            "A".codePointAt(0),
//            "B".codePointAt(0),
//            "C".codePointAt(0),
//            "D".codePointAt(0),
//            "E".codePointAt(0),
//            "F".codePointAt(0),
//            "a".codePointAt(0),
//            "b".codePointAt(0),
//            "c".codePointAt(0),
//            "d".codePointAt(0),
//            "e".codePointAt(0),
//            "f".codePointAt(0),
//    };

    private JsonCodePointUtil() {
        throw new AssertionError();
    }

    /**
     * @param codePoints the codePoints of string to split.
     * @param delim      by which the given string to be split. It is the code point
     *                   value of char.
     * @return the 2D array of code points split by the given char codes.
     * @since 12.0.4
     */
    static int[][] splitByAnyOld(final int[] codePoints, final int delim) {
        // TODO remove this method later
        if (codePoints.length == 0) {
            return new int[][] { codePoints };
        }

        int[] delimPositionInit = new int[codePoints.length];

        int delimCount = 0;

        for (int i = 0; i < codePoints.length; i++) {
            int codePoint = codePoints[i];
            // char/code point will never be -Ve value, we use -Ve value for id which can be
            // ignored
            if (codePoint < 0) {
                final int idIndex0 = i;
                final int idIndex2 = i + 2;
                if (idIndex2 < codePoints.length && codePoints[idIndex0] == codePoints[idIndex2]) {
                    final int idIndex1 = i + 1;
                    final int idLength = Math.negateExact(codePoints[idIndex1]);
                    i += idLength - 1;
                    codePoint = codePoints[i];
                    if (codePoint < 0) {
                        continue;
                    }
                } else {
                    continue;
                }
            }
            if (codePoint == delim) {
                delimPositionInit[delimCount] = i;
                delimCount++;
            }
        }

        if (delimCount == 0) {
            return new int[][] { codePoints };
        }

        final int[] delimPositionsFinal = new int[delimCount];
        System.arraycopy(delimPositionInit, 0, delimPositionsFinal, 0, delimPositionsFinal.length);
        delimPositionInit = null;

        final int[][] splitCodePoints = new int[delimCount + 1][];

        int startIndex = 0;
        for (int i = 0; i < delimPositionsFinal.length; i++) {
            final int delimPosition = delimPositionsFinal[i];

            int range = codePoints.length - (codePoints.length - delimPosition);
            range = range - startIndex;

            if (range > 0) {
                final int[] part = new int[range];
                System.arraycopy(codePoints, startIndex, part, 0, part.length);
                splitCodePoints[i] = part;
            } else {
                splitCodePoints[i] = new int[0];
            }

            startIndex = delimPosition + 1;
        }

        final int lastDelimPosition = delimPositionsFinal[delimPositionsFinal.length - 1];

        startIndex = lastDelimPosition + 1;

        final int range = codePoints.length - startIndex;

        if (range > 0) {
            final int[] part = new int[range];
            System.arraycopy(codePoints, startIndex, part, 0, part.length);
            splitCodePoints[splitCodePoints.length - 1] = part;
        } else {
            splitCodePoints[splitCodePoints.length - 1] = new int[0];
        }

        return splitCodePoints;
    }

    /**
     * @param codePoints the codePoints of string to split.
     * @param delim      by which the given string to be split. It is the code point
     *                   value of char.
     * @return the 2D array of code points split by the given char codes.
     * @since 12.0.4
     */
    static int[][] splitByAny(final int[] codePoints, final int delim) {
        // TODO remove this method later
        return splitByAnyRangeBound(codePoints, 0, codePoints.length - 1, delim);
    }

    /**
     * @param codePoints the codePoints of string to split.
     * @param startIndex
     * @param endIndex
     * @param delim      by which the given string to be split. It is the code point
     *                   value of char.
     * @return the 2D array of code points split by the given char codes.
     * @since 12.0.4
     */
    static int[][] splitByAnyRangeBound(final int[] codePoints, final int startIndex, final int endIndex,
            final int delim) {
        if (startIndex > endIndex) {
            return new int[][] { new int[0] };
        }
        if (startIndex == endIndex && codePoints[startIndex] == delim) {
            return new int[][] { new int[0], new int[0] };
        }
        final int length = endIndex - startIndex + 1;
        final int[] delimPositionInit = new int[length];

        int delimCount = 0;

        for (int i = startIndex; i <= endIndex; i++) {
            final int codePoint = codePoints[i];
            // char/code point will never be -Ve value, we use -Ve value for id which can be
            // ignored
            if (codePoint < 0) {
                final int idIndex0 = i;
                final int idIndex1 = i + 1;
                if (idIndex1 < endIndex && codePoints[idIndex0] != codePoints[idIndex1]) {
                    final int indicesDiff = codePoints[idIndex1];
                    i += indicesDiff;
                    continue;
                }
                i++;
                continue;
            }
            if (codePoint == delim) {
                delimPositionInit[delimCount] = i;
                delimCount++;
            }
        }

        if (delimCount == 0) {
            final int[] part = new int[length];
            System.arraycopy(codePoints, startIndex, part, 0, part.length);
            return new int[][] { part };
        }

        final int[][] splitCodePoints = new int[delimCount + 1][];

        int startIndexTemp = startIndex;
        for (int i = 0; i < delimCount; i++) {
            final int delimPosition = delimPositionInit[i];

            final int range = delimPosition - startIndexTemp;

            if (range > 0) {
                final int[] part = new int[range];
                System.arraycopy(codePoints, startIndexTemp, part, 0, part.length);
                splitCodePoints[i] = part;
            } else {
                splitCodePoints[i] = new int[0];
            }

            startIndexTemp = delimPosition + 1;
        }

        final int lastDelimPosition = delimPositionInit[delimCount - 1];

        startIndexTemp = lastDelimPosition + 1;

        final int range = endIndex - startIndexTemp + 1;

        if (range > 0) {
            final int[] part = new int[range];
            System.arraycopy(codePoints, startIndexTemp, part, 0, part.length);
            splitCodePoints[splitCodePoints.length - 1] = part;
        } else {
            splitCodePoints[splitCodePoints.length - 1] = new int[0];
        }

        return splitCodePoints;
    }

    private static boolean isWhitespace(final int c) {
        if (c < 0) {
            // char/code point will never be -Ve value, we use -Ve value for id which is not
            // considered as whitespace
            return false;
        }
        return switch (c) {
        case SPACE_CODE_POINT, SLASH_N_CODE_POINT, SLASH_T_CODE_POINT, SLASH_R_CODE_POINT -> true;
        default -> Character.isWhitespace(c);
        };
    }

    static boolean isBlank(final int[] codePoints) {
        for (final int codePoint : codePoints) {
            if (!isWhitespace(codePoint)) {
                return false;
            }
        }
        return true;
    }

    static int[] strip(final int[] codePoints) {
        // TODO remove this method later
        int first;
        int last;

        for (first = 0; first < codePoints.length; first++) {
            if (!JsonCodePointUtil.isWhitespace(codePoints[first])) {
                break;
            }
        }
        for (last = codePoints.length; last > first; last--) {
            if (!JsonCodePointUtil.isWhitespace(codePoints[last - 1])) {
                break;
            }
        }

        if (first == 0 && last == codePoints.length) {
            return codePoints;
        }
        final int lastRemovedCount = codePoints.length - last;
        final int codePointsCount = codePoints.length - (first + lastRemovedCount);

        final int[] strippedCodePoints = new int[codePointsCount];
        System.arraycopy(codePoints, first, strippedCodePoints, 0, codePointsCount);
        return strippedCodePoints;
    }

    static int[] findFirstAndLastNonWhitespaceIndices(final int[] codePoints) {
        int first;
        int last;

        for (first = 0; first < codePoints.length; first++) {
            if (!JsonCodePointUtil.isWhitespace(codePoints[first])) {
                break;
            }
        }
        if (first == codePoints.length) {
            return null;
        }
        for (last = (codePoints.length - 1); last > first; last--) {
            if (!JsonCodePointUtil.isWhitespace(codePoints[last])) {
                break;
            }
        }
        return new int[] { first, last };
    }

    static int[] cut(final int[] codePoints, final int[] startEndIndices) {
        if (startEndIndices == null) {
            return codePoints;
        }
        final int[] validCodePoints = new int[startEndIndices[1] - startEndIndices[0] + 1];
        System.arraycopy(codePoints, startEndIndices[0], validCodePoints, 0, validCodePoints.length);
        return validCodePoints;
    }

    static String toString(final int[] codePoints, final int[] startEndIndices) {
        return startEndIndices != null
                ? new String(codePoints, startEndIndices[0], (startEndIndices[1] - startEndIndices[0] + 1))
                : new String(codePoints, 0, codePoints.length);
    }

    static boolean hasExponentialNotation(final int[] codePoints, final int[] startEndIndices) {
        if (startEndIndices != null) {
            for (int i = startEndIndices[0]; i < startEndIndices[1]; i++) {
                switch (codePoints[i]) {
                case E_LOWER_CASE_CODE_POINT, E_UPPER_CASE_CODE_POINT -> {
                    return true;
                }
                }
            }
        }
        for (final int codePoint : codePoints) {
            switch (codePoint) {
            case E_LOWER_CASE_CODE_POINT, E_UPPER_CASE_CODE_POINT -> {
                return true;
            }
            }
        }
        return false;
    }

    static boolean isStrictLong(final int[] codePoints, final int[] startEndIndices) {
        if (codePoints == null || (startEndIndices == null && codePoints.length == 0)) {
            return false;
        }
        final int startIndex = startEndIndices != null ? startEndIndices[0] : 0;
        final int endIndex = startEndIndices != null ? startEndIndices[1] : codePoints.length - 1;

        final int valueLength = endIndex - startIndex + 1;
        if (valueLength <= LONG_MIN_VALUE_CODE_POINTS.length) {
            final int codePointAt0 = codePoints[startIndex];
            if (valueLength == 1 && codePointAt0 == ZERO_CODE_POINT) {
                return true;
            }
            final boolean startsWithMinus = codePointAt0 == MINUS_CODE_POINT;
            if (startsWithMinus) {
                if ((valueLength == 1) || (codePoints[startIndex + 1] == ZERO_CODE_POINT)) {
                    return false;
                }
            } else if (codePointAt0 == ZERO_CODE_POINT) {
                return false;
            }

            boolean notAStrictNumber = false;
            final int initialI = startsWithMinus ? startIndex + 1 : startIndex;
            for (int i = initialI; i <= endIndex; i++) {
                final int cp = codePoints[i];
                if (cp < ZERO_CODE_POINT || cp > NINE_CODE_POINT) {
                    notAStrictNumber = true;
                    break;
                }
            }

            if (!notAStrictNumber) {
                if (valueLength == LONG_MAX_VALUE_CODE_POINTS.length) {
                    for (int i = 0; i < LONG_MAX_VALUE_CODE_POINTS.length; i++) {
                        final int index = startIndex + i;
                        if (index > endIndex) {
                            break;
                        }
                        if (codePoints[index] > LONG_MAX_VALUE_CODE_POINTS[i]) {
                            return false;
                        }
                    }
                } else if (valueLength == LONG_MIN_VALUE_CODE_POINTS.length) {
                    for (int i = 1; i < LONG_MIN_VALUE_CODE_POINTS.length; i++) {
                        final int index = startIndex + i;
                        if (index > endIndex) {
                            break;
                        }
                        if (codePoints[index] > LONG_MIN_VALUE_CODE_POINTS[i]) {
                            return false;
                        }
                    }
                }
            }

            return !notAStrictNumber;
        }

        return false;
    }

    static boolean isValidUnicodeEscapeSequence(final int[] codePoints, final int startIndex, final int endIndex) {
        // should be 6 code points
        if (endIndex < codePoints.length && (endIndex - startIndex) == 5) {
            // better than Arrays.binarySearch(HEXA_CODE_POINTS_SORTED_ASC
            return codePoints[startIndex] == ESCAPE_CODE_POINT && codePoints[startIndex + 1] == U_CODE_POINT
                    && isValidHexNumber(codePoints[startIndex + 2]) && isValidHexNumber(codePoints[startIndex + 3])
                    && isValidHexNumber(codePoints[startIndex + 4]) && isValidHexNumber(codePoints[startIndex + 5]);
        }
        return false;
    }

    private static boolean isValidHexNumber(final int n) {
        if ((n >= ZERO_CODE_POINT && n <= NINE_CODE_POINT)
                || (n >= A_UPPER_CASE_CODE_POINT && n <= F_UPPER_CASE_CODE_POINT)) {
            return true;
        }
        return n >= A_LOWER_CASE_CODE_POINT && n <= F_LOWER_CASE_CODE_POINT;
    }

}
