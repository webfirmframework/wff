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

import com.webfirmframework.wffweb.InvalidValueException;
import com.webfirmframework.wffweb.util.StringUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;

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
        this(value != null ? StringUtil.toCodePoints(value) : null, valueType);
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
        this(StringUtil.toCodePoints(Objects.requireNonNull(value)), JsonValueType.STRING);
    }

    /**
     * The value type will be NUMBER.
     *
     * @param value the number value.
     * @since 12.0.4
     */
    public JsonValue(final Number value) {
        this(StringUtil.toCodePoints(Objects.requireNonNull(value).toString()), JsonValueType.NUMBER);
    }

    /**
     * The value type will be BOOLEAN.
     *
     * @param value the boolean value.
     * @since 12.0.4
     */
    public JsonValue(final boolean value) {
        this(StringUtil.toCodePoints(String.valueOf(value)), JsonValueType.BOOLEAN);
    }

    @Override
    public int[] codePoints() {
        return codePoints != null ? Arrays.copyOfRange(codePoints, 0, codePoints.length) : null;
    }

    int[] originalCodePoints() {
        return codePoints;
    }

    /**
     * @return the value as Integer or null. It will throw exception if the value is
     * not parsable to Integer.
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
     * not parsable to Long.
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
     * not parsable to Double.
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
     * is not parsable to BigDecimal.
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
     * is not parsable to BigInteger.
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
     * not parsable to Boolean.
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
     * This method returns raw JSON string value (i.e. without decoding to Java string) if the object is created by JsonParser.
     * Use {@link #asString(boolean)} with argument true if the returned string to be JSON decoded value.
     * Use {@link #asString(boolean)} with arguments (true, true) if the returned string to be JSON decoded value and Unicode chars sequence to be parsed to Java chars.
     *
     * @return the value as String or null.
     * @since 12.0.4
     */
    public String asString() {
        return asString(false, false);
    }

    /**
     * @param decode true to decode the json string value or false to get the raw text.
     * @return the value as String or null.
     * @since 12.0.11
     */
    public String asString(final boolean decode) {
        return asString(decode, false);
    }

    /**
     * @param decodeJson                 true to decode the json string value or false to get the raw text.
     * @param decodeUnicodeCharsSequence true to decode the Unicode chars sequences like <span>\</span><span>u2122</span> to Java chars or false to use as it is.
     * @return the value as String or null.
     * @since 12.0.11
     */
    public String asString(final boolean decodeJson, final boolean decodeUnicodeCharsSequence) {
        if (codePoints == null) {
            return null;
        }
        if (decodeJson) {
            return JsonStringUtil.replaceEscapeCharSequenceWithJavaChars(codePoints, 0, codePoints.length, decodeUnicodeCharsSequence);
        } else if (decodeUnicodeCharsSequence) {
            return JsonStringUtil.toUnicodeCharsDecodedString(codePoints, 0, codePoints.length);
        }
        return new String(codePoints, 0, codePoints.length);
    }

    /**
     * @return the valueType.
     * @deprecated use the alternative method {@link #valueType()} which does the same job.
     */
    @Deprecated(since = "12.0.11", forRemoval = true)
    public JsonValueType getValueType() {
        return valueType;
    }

    /**
     * @return the JSON value string.
     * @since 12.0.4
     */
    @Override
    public String toJsonString() {
        if (codePoints == null) {
            return "null";
        }
        if (JsonValueType.STRING.equals(valueType)) {
            return JsonStringUtil.placeEscapeCharInJsonStringValueIfRequired(codePoints, true);
        } else if (JsonValueType.ENCODED_STRING.equals(valueType)) {
            return "\"".concat(new String(codePoints, 0, codePoints.length)).concat("\"");
        }
        return new String(codePoints, 0, codePoints.length);
    }

    @Override
    public String toString() {
        return String.valueOf(asString());
    }
}
