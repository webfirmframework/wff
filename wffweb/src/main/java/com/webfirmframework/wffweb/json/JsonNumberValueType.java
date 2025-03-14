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

import java.math.BigDecimal;

import com.webfirmframework.wffweb.util.NumberUtil;

/**
 * @since 12.0.4
 */
public enum JsonNumberValueType {

    /**
     * To parse the value as Double.
     *
     * @since 12.0.4
     */
    DOUBLE {
        @Override
        Double parse(final int[] codePoints, final int[] startEndIndices) {
            final String s = JsonCodePointUtil.toString(codePoints, startEndIndices);
            return Double.valueOf(s);
        }
    },

    /**
     * To parse the value as BigDecimal.
     *
     * @since 12.0.4
     */
    BIG_DECIMAL {
        @Override
        BigDecimal parse(final int[] codePoints, final int[] startEndIndices) {
            final String s = JsonCodePointUtil.toString(codePoints, startEndIndices);
            return new BigDecimal(s);
        }
    },

    /**
     * To represent JsonValue wrapper object as null value.
     *
     * @since 12.0.4
     */
    JSON_VALUE {
        @Override
        JsonValue parse(final int[] codePoints, final int[] startEndIndices) {
            return new JsonValue(codePoints, JsonValueType.NUMBER);
        }
    },

    /**
     * If the number is in the range of Java Integer and not a decimal number it
     * will be parsed to Integer, if the number is in the range of Java Long and not
     * a decimal number it will be parsed to Long, otherwise it will be parsed to
     * Double.
     *
     * @since 12.0.4
     */
    AUTO_INTEGER_LONG_DOUBLE {
        @Override
        Object parse(final int[] codePoints, final int[] startEndIndices) {
            final String s = JsonCodePointUtil.toString(codePoints, startEndIndices);
            if (JsonCodePointUtil.isStrictLong(codePoints, startEndIndices)) {
                final long l = Long.parseLong(s);
                if ((int) l != l) {
                    return l;
                }
                return Math.toIntExact(l);
            }
            final double value = Double.parseDouble(s);
            if (Double.isInfinite(value) || Double.isNaN(value) || value % 1 != 0) {
                return value;
            }

            if (JsonCodePointUtil.hasExponentialNotation(codePoints, startEndIndices)) {
                final String s2 = new BigDecimal(s).stripTrailingZeros().toPlainString();
                if (NumberUtil.isStrictLong(s2)) {
                    final long l = Long.parseLong(s2);
                    if ((int) l != l) {
                        return l;
                    }
                    return Math.toIntExact(l);
                }
            }
            return value;
        }
    },

    /**
     * If the number is in the range of Java Integer and not a decimal number it
     * will be parsed to Integer, if the number is in the range of Java Long and not
     * a decimal number it will be parsed to Long, otherwise it will be parsed to
     * BigDecimal.
     *
     * @since 12.0.4
     */
    AUTO_INTEGER_LONG_BIG_DECIMAL {
        @Override
        Object parse(final int[] codePoints, final int[] startEndIndices) {
            final String s1 = JsonCodePointUtil.toString(codePoints, startEndIndices);
            if (JsonCodePointUtil.isStrictLong(codePoints, startEndIndices)) {
                final long l = Long.parseLong(s1);
                if ((int) l != l) {
                    return l;
                }
                return Math.toIntExact(l);
            }

            final BigDecimal value = new BigDecimal(s1);
            if (value.scale() > 0) {
                return value;
            }
            if (JsonCodePointUtil.hasExponentialNotation(codePoints, startEndIndices)) {
                final String s2 = value.stripTrailingZeros().toPlainString();
                if (NumberUtil.isStrictLong(s2)) {
                    final long l = Long.parseLong(s2);
                    if ((int) l != l) {
                        return l;
                    }
                    return Math.toIntExact(l);
                }
            }
            return value;
        }
    };

    JsonNumberValueType() {
    }

    Object parse(final int[] codePoints, final int[] startEndIndices) {
        throw new AssertionError();
    }

}
