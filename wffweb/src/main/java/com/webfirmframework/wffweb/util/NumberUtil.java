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

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author WFF
 * @since 12.0.3
 */
public final class NumberUtil {

    // length = 11
    private static final int[] INT_MIN_VALUE_CODE_POINTS = String.valueOf(Integer.MIN_VALUE).codePoints().toArray();

    // length = 10
    private static final int[] INT_MAX_VALUE_CODE_POINTS = String.valueOf(Integer.MAX_VALUE).codePoints().toArray();

    private static final int[] NUMBER_CODE_POINTS = "0123456789".codePoints().toArray();

    private static final int ZERO_CODE_POINT = NUMBER_CODE_POINTS[0];

    private static final int NINE_CODE_POINT = NUMBER_CODE_POINTS[NUMBER_CODE_POINTS.length - 1];

    private NumberUtil() {
        throw new AssertionError();
    }

    /**
     * Checks if x is equal to y.
     *
     * @param x the BigDecimal value to check.
     * @param y the BigDecimal value to check.
     * @return true if both x and y are equal otherwise false.
     * @since 12.0.3
     */
    public static boolean isEqual(final BigDecimal x, final BigDecimal y) {
        return x.compareTo(y) == 0;
    }

    /**
     * Checks if x is greater than y.
     *
     * @param x the BigDecimal value to check.
     * @param y the BigDecimal value to check.
     * @return true if x is greater than y otherwise false.
     * @since 12.0.3
     */
    public static boolean isGreaterThan(final BigDecimal x, final BigDecimal y) {
        return x.compareTo(y) > 0;
    }

    /**
     * Checks if x is less than y.
     *
     * @param x the BigDecimal value to check.
     * @param y the BigDecimal value to check.
     * @return true if x is less than y otherwise false.
     * @since 12.0.3
     */
    public static boolean isLessThan(final BigDecimal x, final BigDecimal y) {
        return x.compareTo(y) < 0;
    }

    /**
     * Checks if x is less than or equal to y.
     *
     * @param x the BigDecimal value to check.
     * @param y the BigDecimal value to check.
     * @return true if x is less than or equal to y otherwise false.
     * @since 12.0.3
     */
    public static boolean isLessThanOrEqualTo(final BigDecimal x, final BigDecimal y) {
        return x.compareTo(y) <= 0;
    }

    /**
     * Checks if x is greater than or equal to y.
     *
     * @param x the BigDecimal value to check.
     * @param y the BigDecimal value to check.
     * @return true if x is greater than or equal to y otherwise false.
     * @since 12.0.3
     */
    public static boolean isGreaterThanOrEqualTo(final BigDecimal x, final BigDecimal y) {
        return x.compareTo(y) >= 0;
    }

    /**
     * Checks if x is equal to y.
     *
     * @param x the BigInteger value to check.
     * @param y the BigInteger value to check.
     * @return true if both x and y are equal otherwise false.
     * @since 12.0.3
     */
    public static boolean isEqual(final BigInteger x, final BigInteger y) {
        return x.compareTo(y) == 0;
    }

    /**
     * Checks if x is greater than y.
     *
     * @param x the BigInteger value to check.
     * @param y the BigInteger value to check.
     * @return true if x is greater than y otherwise false.
     * @since 12.0.3
     */
    public static boolean isGreaterThan(final BigInteger x, final BigInteger y) {
        return x.compareTo(y) > 0;
    }

    /**
     * Checks if x is less than y.
     *
     * @param x the BigInteger value to check.
     * @param y the BigInteger value to check.
     * @return true if x is less than y otherwise false.
     * @since 12.0.3
     */
    public static boolean isLessThan(final BigInteger x, final BigInteger y) {
        return x.compareTo(y) < 0;
    }

    /**
     * Checks if x is less than or equal to y.
     *
     * @param x the BigInteger value to check.
     * @param y the BigInteger value to check.
     * @return true if x is less than or equal to y otherwise false.
     * @since 12.0.3
     */
    public static boolean isLessThanOrEqualTo(final BigInteger x, final BigInteger y) {
        return x.compareTo(y) <= 0;
    }

    /**
     * Checks if x is greater than or equal to y.
     *
     * @param x the BigInteger value to check.
     * @param y the BigInteger value to check.
     * @return true if x is greater than or equal to y otherwise false.
     * @since 12.0.3
     */
    public static boolean isGreaterThanOrEqualTo(final BigInteger x, final BigInteger y) {
        return x.compareTo(y) >= 0;
    }

    /**
     * Checks if the value contains a 32 bit int. True for the following formats:
     * -1401, 1401, 0. False for the following values/formats: +1401, 01401, 001401,
     * 14.01, 14-01, 1401-, abcd
     *
     * @param value the value to check if it contains an 32 bit int value
     * @return true if it contains 32 bit int value otherwise false.
     * @since 12.0.3
     */
    public static boolean isStrictInt(final String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        final int valueLength = value.length();
        if (valueLength <= INT_MIN_VALUE_CODE_POINTS.length) {
            final int codePointAt0 = value.codePointAt(0);
            if (valueLength == 1 && codePointAt0 == ZERO_CODE_POINT) {
                return true;
            }
            final boolean startsWithMinus = codePointAt0 == StringUtil.MINUS_CODE_POINT;
            if (startsWithMinus) {
                if ((valueLength == 1) || (value.codePointAt(1) == ZERO_CODE_POINT)) {
                    return false;
                }
            } else if (codePointAt0 == ZERO_CODE_POINT) {
                return false;
            }

            final boolean notAStrictNumber = startsWithMinus
                    ? value.codePoints().skip(1).anyMatch(cp -> cp < ZERO_CODE_POINT || cp > NINE_CODE_POINT)
                    : value.codePoints().anyMatch(cp -> cp < ZERO_CODE_POINT || cp > NINE_CODE_POINT);

            if (!notAStrictNumber) {
                if (valueLength == INT_MAX_VALUE_CODE_POINTS.length) {
                    for (int i = 0; i < INT_MAX_VALUE_CODE_POINTS.length; i++) {
                        if (value.codePointAt(i) > INT_MAX_VALUE_CODE_POINTS[i]) {
                            return false;
                        }
                    }
                } else if (valueLength == INT_MIN_VALUE_CODE_POINTS.length) {
                    for (int i = 1; i < INT_MIN_VALUE_CODE_POINTS.length; i++) {
                        if (value.codePointAt(i) > INT_MIN_VALUE_CODE_POINTS[i]) {
                            return false;
                        }
                    }
                }
            }

            return !notAStrictNumber;
        }
        return false;
    }

}
