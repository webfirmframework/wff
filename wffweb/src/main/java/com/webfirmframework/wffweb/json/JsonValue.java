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
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;

import com.webfirmframework.wffweb.InvalidValueException;

/**
 * @param codePoints the code points of string representation of value.
 * @param valueType  the JsonValueType.
 * @since 12.0.4
 */
public record JsonValue(int[] codePoints, JsonValueType valueType) implements JsonPart {

    public JsonValue {
        if (valueType == null) {
            throw new InvalidValueException("valueType in JsonValue cannot be null.");
        }
    }

    /**
     * @param value     the value as a string. If the value type is a BOOLEAN or
     *                  NUMBER, it should not contain whitespaces. If the value type
     *                  is NULL, it should be null.
     * @param valueType the json type of value.
     * @since 12.0.4
     */
    public JsonValue(final String value, final JsonValueType valueType) {
        this(value != null ? value.codePoints().toArray() : null, valueType);
    }

    /**
     * For null value and NULL type.
     *
     * @since 12.0.4
     */
    public JsonValue() {
        this((int[]) null, JsonValueType.NULL);
    }

    /**
     * The value type will be STRING.
     *
     * @param value the json string value.
     * @since 12.0.4
     */
    public JsonValue(final String value) {
        this(Objects.requireNonNull(value).codePoints().toArray(), JsonValueType.STRING);
    }

    /**
     * The value type will be NUMBER.
     *
     * @param value the number value.
     * @since 12.0.4
     */
    public JsonValue(final Number value) {
        this(Objects.requireNonNull(value).toString().codePoints().toArray(), JsonValueType.NUMBER);
    }

    /**
     * The value type will be BOOLEAN.
     *
     * @param value the boolean value.
     * @since 12.0.4
     */
    public JsonValue(final boolean value) {
        this(String.valueOf(value).codePoints().toArray(), JsonValueType.BOOLEAN);
    }

    @Override
    public int[] codePoints() {
        return codePoints != null ? Arrays.copyOfRange(codePoints, 0, codePoints.length) : null;
    }

    /**
     * @return the value as Integer or null. It will throw exception if the value is
     *         not parsable to Integer.
     * @since 12.0.4
     */
    public Integer asInteger() {
        if (codePoints == null) {
            return null;
        }
        return Integer.valueOf(new String(codePoints, 0, codePoints.length));
    }

    /**
     * @return the value as Long or null. It will throw exception if the value is
     *         not parsable to Long.
     * @since 12.0.4
     */
    public Long asLong() {
        if (codePoints == null) {
            return null;
        }
        return Long.valueOf(new String(codePoints, 0, codePoints.length));
    }

    /**
     * @return the value as Double or null. It will throw exception if the value is
     *         not parsable to Double.
     * @since 12.0.4
     */
    public Double asDouble() {
        if (codePoints == null) {
            return null;
        }
        return Double.valueOf(new String(codePoints, 0, codePoints.length));
    }

    /**
     * @return the value as BigDecimal or null. It will throw exception if the value
     *         is not parsable to BigDecimal.
     * @since 12.0.4
     */
    public BigDecimal asBigDecimal() {
        if (codePoints == null) {
            return null;
        }
        return new BigDecimal(new String(codePoints, 0, codePoints.length));
    }

    /**
     * @return the value as BigInteger or null. It will throw exception if the value
     *         is not parsable to BigInteger.
     * @since 12.0.4
     */
    public BigInteger asBigInteger() {
        if (codePoints == null) {
            return null;
        }
        return new BigInteger(new String(codePoints, 0, codePoints.length));
    }

    /**
     * @return the value as Boolean or null. It will throw exception if the value is
     *         not parsable to Boolean.
     * @since 12.0.4
     */
    public Boolean asBoolean() {
        if (codePoints == null) {
            return null;
        }
        final Boolean b = JsonBooleanValueType.parseBooleanOtherwiseNull(codePoints, null);
        if (b != null) {
            return b;
        }
        return Boolean.valueOf(new String(codePoints, 0, codePoints.length));
    }

    /**
     * @return the value as String or null.
     * @since 12.0.4
     */
    public String asString() {
        if (codePoints == null) {
            return null;
        }
        return new String(codePoints, 0, codePoints.length);
    }

    public JsonValueType getValueType() {
        return valueType;
    }

    /**
     * @return the JSON value string.
     * @since 12.0.4
     */
    @Override
    public String toJsonString() {
        final String s = asString();
        if (s == null) {
            return "null";
        }
        if (JsonValueType.STRING.equals(valueType)) {
            return "\"".concat(s).concat("\"");
        }
        return s;
    }

    @Override
    public String toString() {
        return String.valueOf(asString());
    }
}
