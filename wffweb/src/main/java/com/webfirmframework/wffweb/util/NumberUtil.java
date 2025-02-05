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

}
