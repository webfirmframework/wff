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

import com.webfirmframework.wffweb.InvalidValueException;

/**
 * @param value
 * @param jsonMapNode
 * @param jsonListNode
 * @since 12.0.15
 */
public record JsonBoxedValue(Object value, JsonMapNode jsonMapNode, JsonListNode jsonListNode) {

    static JsonBoxedValue create(final Object value) {
        if (value instanceof final JsonMapNode jsonMapNode) {
            return new JsonBoxedValue(null, jsonMapNode, null);
        }
        if (value instanceof final JsonListNode jsonListNode) {
            return new JsonBoxedValue(null, null, jsonListNode);
        }
        return new JsonBoxedValue(value, null, null);
    }

    public JsonBoxedValue getValue(final String key) {
        return jsonMapNode.getValue(key);
    }

    public JsonBoxedValue getValue(final int index) {
        return jsonListNode.getValue(index);
    }

    /**
     * @return the value as JsonValue object.
     * @throws InvalidValueException if the value is not suitable to create an object of JsonValue.
     * @since 12.0.15
     */
    public JsonValue asJsonValue() {
        return JsonValue.create(value);
    }

    /**
     * @return the value as String or null.
     * @since 12.0.15
     */
    public String asString() {
        if (value instanceof final JsonValue jsonValue) {
            return jsonValue.asString();
        }
        if (value != null) {
            return value.toString();
        }
        if (jsonMapNode != null) {
            return jsonListNode.toJsonString();
        }
        if (jsonListNode != null) {
            return jsonListNode.toJsonString();
        }
        return null;
    }

    /**
     * @return the value as Boolean or null.
     * @throws InvalidValueException if the value is not convertible.
     * @since 12.0.15
     */
    public Boolean asBoolean() {
        if (value instanceof final Boolean booleanValue) {
            return booleanValue;
        }
        if (value instanceof final JsonValue jsonValue) {
            return jsonValue.asBoolean();
        }
        if (value != null) {
            return Boolean.valueOf(value.toString());
        }
        if (jsonMapNode != null) {
            throw new InvalidValueException("The value is JsonMapNode which cannot be converted to Boolean.");
        }
        if (jsonListNode != null) {
            throw new InvalidValueException("The value is JsonListNode which cannot be converted to Boolean.");
        }
        return null;
    }

    /**
     * @return the value as Boolean or null.
     * @throws InvalidValueException if the value is not convertible.
     * @since 12.0.15
     */
    public Short asShort() {
        if (value instanceof final Short shortValue) {
            return shortValue;
        }
        if (value instanceof final JsonValue jsonValue) {
            return jsonValue.asShort();
        }
        if (value != null) {
            return Short.valueOf(value.toString());
        }
        if (jsonMapNode != null) {
            throw new InvalidValueException("The value is JsonMapNode which cannot be converted to Short.");
        }
        if (jsonListNode != null) {
            throw new InvalidValueException("The value is JsonListNode which cannot be converted to Short.");
        }
        return null;
    }

    /**
     * @return the value as Integer or null.
     * @throws InvalidValueException if the value is not convertible.
     * @since 12.0.15
     */
    public Integer asInteger() {
        if (value instanceof final Integer shortValue) {
            return shortValue;
        }
        if (value instanceof final JsonValue jsonValue) {
            return jsonValue.asInteger();
        }
        if (value != null) {
            return Integer.valueOf(value.toString());
        }
        if (jsonMapNode != null) {
            throw new InvalidValueException("The value is JsonMapNode which cannot be converted to Integer.");
        }
        if (jsonListNode != null) {
            throw new InvalidValueException("The value is JsonListNode which cannot be converted to Integer.");
        }
        return null;
    }

    /**
     * @return the value as Long or null.
     * @throws InvalidValueException if the value is not convertible.
     * @since 12.0.15
     */
    public Long asLong() {
        if (value instanceof final Long longValue) {
            return longValue;
        }
        if (value instanceof final JsonValue jsonValue) {
            return jsonValue.asLong();
        }
        if (value != null) {
            return Long.valueOf(value.toString());
        }
        if (jsonMapNode != null) {
            throw new InvalidValueException("The value is JsonMapNode which cannot be converted to Long.");
        }
        if (jsonListNode != null) {
            throw new InvalidValueException("The value is JsonListNode which cannot be converted to Long.");
        }
        return null;
    }

    /**
     * @return the value as Double or null.
     * @throws InvalidValueException if the value is not convertible.
     * @since 12.0.15
     */
    public Double asDouble() {
        if (value instanceof final Double doubleValue) {
            return doubleValue;
        }
        if (value instanceof final JsonValue jsonValue) {
            return jsonValue.asDouble();
        }
        if (value != null) {
            return Double.valueOf(value.toString());
        }
        if (jsonMapNode != null) {
            throw new InvalidValueException("The value is JsonMapNode which cannot be converted to Double.");
        }
        if (jsonListNode != null) {
            throw new InvalidValueException("The value is JsonListNode which cannot be converted to Double.");
        }
        return null;
    }

    /**
     * @return the value as BigInteger or null.
     * @throws InvalidValueException if the value is not convertible.
     * @since 12.0.15
     */
    public BigInteger asBigInteger() {
        if (value instanceof final BigInteger bigInteger) {
            return bigInteger;
        }
        if (value instanceof final JsonValue jsonValue) {
            return jsonValue.asBigInteger();
        }
        if (value != null) {
            return new BigInteger(value.toString());
        }
        if (jsonMapNode != null) {
            throw new InvalidValueException("The value is JsonMapNode which cannot be converted to BigInteger.");
        }
        if (jsonListNode != null) {
            throw new InvalidValueException("The value is JsonListNode which cannot be converted to BigInteger.");
        }
        return null;
    }

    /**
     * @return the value as BigDecimal or null.
     * @throws InvalidValueException if the value is not convertible.
     * @since 12.0.15
     */
    public BigDecimal asBigDecimal() {
        if (value instanceof final BigDecimal bigDecimal) {
            return bigDecimal;
        }
        if (value instanceof final JsonValue jsonValue) {
            return jsonValue.asBigDecimal();
        }
        if (value != null) {
            return new BigDecimal(value.toString());
        }
        if (jsonMapNode != null) {
            throw new InvalidValueException("The value is JsonMapNode which cannot be converted to BigDecimal.");
        }
        if (jsonListNode != null) {
            throw new InvalidValueException("The value is JsonListNode which cannot be converted to BigDecimal.");
        }
        return null;
    }

    @Override
    public String toString() {
        if (value != null) {
            return value.toString();
        }
        if (jsonMapNode != null) {
            return jsonListNode.toJsonString();
        }
        if (jsonListNode != null) {
            return jsonListNode.toJsonString();
        }
        return "";
    }
}
