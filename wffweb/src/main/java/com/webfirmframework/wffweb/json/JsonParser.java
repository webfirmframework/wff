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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;

/**
 * The fastest way to parse JSON in the world.
 *
 * @since 12.0.4
 */
public record JsonParser(JsonObjectType jsonObjectType, JsonArrayType jsonArrayType,
        boolean jsonNumberArrayUniformValueType, JsonNumberValueType jsonNumberValueTypeForObject,
        JsonNumberValueType jsonNumberValueTypeForArray, JsonStringValueType jsonStringValueTypeForObject,
        JsonStringValueType jsonStringValueTypeForArray, JsonBooleanValueType jsonBooleanValueTypeForObject,
        JsonBooleanValueType jsonBooleanValueTypeForArray, JsonNullValueType jsonNullValueTypeForObject,
        JsonNullValueType jsonNullValueTypeForArray, boolean validateEscapeSequence) {

//    private static final int CURLY_BRACE_START_CODE_POINT = "{".codePointAt(0);
    private static final int CURLY_BRACE_START_CODE_POINT = 123;

//    private static final int CURLY_BRACE_END_CODE_POINT = "}".codePointAt(0);
    private static final int CURLY_BRACE_END_CODE_POINT = 125;

//    private static final int SQUARE_BRACKET_START_CODE_POINT = "[".codePointAt(0);
    private static final int SQUARE_BRACKET_START_CODE_POINT = 91;

//    private static final int SQUARE_BRACKET_END_CODE_POINT = "]".codePointAt(0);
    private static final int SQUARE_BRACKET_END_CODE_POINT = 93;

    private static final int COMMA_CODE_POINT = ",".codePointAt(0);

    private static final int COLON_CODE_POINT = ":".codePointAt(0);

    private static final int ESCAPE_CODE_POINT = JsonCodePointUtil.ESCAPE_CODE_POINT;

    private static final int DOUBLE_QUOTES_CODE_POINT = JsonCodePointUtil.DOUBLE_QUOTES_CODE_POINT;

    private static final int[] NULL_CODE_POINTS = "null".codePoints().toArray();

    // Note: it should be sorted in ascending order, call
    // Arrays.sort(JSON_NODE_DELIMS_SORTED_ASC); to dynamically sort
    // this is only for buildOuterMostJsonSection method
    // value is: [ 34, 91, 93, 123, 125 ]
//    private static final int[] JSON_NODE_DELIMS_SORTED_ASC = {DOUBLE_QUOTES_CODE_POINT,
//            SQUARE_BRACKET_START_CODE_POINT, SQUARE_BRACKET_END_CODE_POINT, CURLY_BRACE_START_CODE_POINT,
//            CURLY_BRACE_END_CODE_POINT};

    public interface Builder {

        /**
         * @param jsonObjectType the JsonObjectType to use.
         * @return the builder.
         * @since 12.0.4
         */
        Builder jsonObjectType(final JsonObjectType jsonObjectType);

        /**
         * @param jsonArrayType the JsonArrayType to use.
         * @return the builder.
         * @since 12.0.4
         */
        Builder jsonArrayType(final JsonArrayType jsonArrayType);

        /**
         * @param uniform true to make all number values in an array to be the same
         *                class type otherwise false.
         * @return the builder.
         * @since 12.0.4
         */
        Builder jsonNumberArrayUniformValueType(final boolean uniform);

        /**
         * @param jsonNumberValueType the JsonNumberValueType to use in JSON object.
         * @return the builder.
         * @since 12.0.4
         */
        Builder jsonNumberValueTypeForObject(final JsonNumberValueType jsonNumberValueType);

        /**
         * @param jsonNumberValueType the JsonNumberValueType to use in JSON array.
         * @return the builder.
         * @since 12.0.4
         */
        Builder jsonNumberValueTypeForArray(final JsonNumberValueType jsonNumberValueType);

        /**
         * @param jsonStringValueType the JsonStringValueType to use in JSON object.
         * @return the builder.
         * @since 12.0.4
         */
        Builder jsonStringValueTypeForObject(final JsonStringValueType jsonStringValueType);

        /**
         * @param jsonStringValueType the JsonStringValueType to use in JSON array.
         * @return the builder.
         * @since 12.0.4
         */
        Builder jsonStringValueTypeForArray(final JsonStringValueType jsonStringValueType);

        /**
         * @param jsonBooleanValueType the JsonBooleanValueType to use in JSON object.
         * @return the builder.
         * @since 12.0.4
         */
        Builder jsonBooleanValueTypeForObject(final JsonBooleanValueType jsonBooleanValueType);

        /**
         * @param jsonBooleanValueType the JsonBooleanValueType to use in JSON array.
         * @return the builder.
         * @since 12.0.4
         */
        Builder jsonBooleanValueTypeForArray(final JsonBooleanValueType jsonBooleanValueType);

        /**
         * @param jsonNullValueType the JsonNullValueType to use in JSON object.
         * @return the builder.
         * @since 12.0.4
         */
        Builder jsonNullValueTypeForObject(final JsonNullValueType jsonNullValueType);

        /**
         * @param jsonNullValueType the JsonNullValueType to use in JSON array.
         * @return the builder.
         * @since 12.0.4
         */
        Builder jsonNullValueTypeForArray(final JsonNullValueType jsonNullValueType);

        /**
         * @param validateEscapeSequence true to validate escape char sequence
         *                               (including the Unicode escape sequence) while
         *                               parsing otherwise false.
         * @return the builder.
         * @since 12.0.4
         */
        Builder validateEscapeSequence(final boolean validateEscapeSequence);

        JsonParser build();
    }

    public JsonParser(final JsonObjectType jsonObjectType, final JsonArrayType jsonArrayType,
            final boolean jsonNumberArrayUniformValueType, final JsonNumberValueType jsonNumberValueTypeForObject,
            final JsonNumberValueType jsonNumberValueTypeForArray,
            final JsonStringValueType jsonStringValueTypeForObject,
            final JsonStringValueType jsonStringValueTypeForArray,
            final JsonBooleanValueType jsonBooleanValueTypeForObject,
            final JsonBooleanValueType jsonBooleanValueTypeForArray, final JsonNullValueType jsonNullValueTypeForObject,
            final JsonNullValueType jsonNullValueTypeForArray, final boolean validateEscapeSequence) {
        this.jsonObjectType = jsonObjectType != null ? jsonObjectType : JsonObjectType.UNMODIFIABLE_MAP;
        this.jsonArrayType = jsonArrayType != null ? jsonArrayType : JsonArrayType.UNMODIFIABLE_LIST;
        this.jsonNumberArrayUniformValueType = jsonNumberArrayUniformValueType;
        this.jsonNumberValueTypeForObject = jsonNumberValueTypeForObject != null ? jsonNumberValueTypeForObject
                : JsonNumberValueType.AUTO_INTEGER_LONG_BIG_DECIMAL;
        this.jsonNumberValueTypeForArray = jsonNumberValueTypeForArray != null ? jsonNumberValueTypeForArray
                : JsonNumberValueType.AUTO_INTEGER_LONG_BIG_DECIMAL;
        this.jsonStringValueTypeForObject = jsonStringValueTypeForObject != null ? jsonStringValueTypeForObject
                : JsonStringValueType.STRING;
        this.jsonStringValueTypeForArray = jsonStringValueTypeForArray != null ? jsonStringValueTypeForArray
                : JsonStringValueType.STRING;
        this.jsonBooleanValueTypeForObject = jsonBooleanValueTypeForObject != null ? jsonBooleanValueTypeForObject
                : JsonBooleanValueType.BOOLEAN;
        this.jsonBooleanValueTypeForArray = jsonBooleanValueTypeForArray != null ? jsonBooleanValueTypeForArray
                : JsonBooleanValueType.BOOLEAN;
        this.jsonNullValueTypeForObject = jsonNullValueTypeForObject != null ? jsonNullValueTypeForObject
                : JsonNullValueType.NULL;
        this.jsonNullValueTypeForArray = jsonNullValueTypeForArray != null ? jsonNullValueTypeForArray
                : JsonNullValueType.NULL;
        this.validateEscapeSequence = validateEscapeSequence;
    }

    public JsonParser() {
        this(JsonObjectType.UNMODIFIABLE_MAP, JsonArrayType.UNMODIFIABLE_LIST, false,
                JsonNumberValueType.AUTO_INTEGER_LONG_BIG_DECIMAL, JsonNumberValueType.AUTO_INTEGER_LONG_BIG_DECIMAL,
                JsonStringValueType.STRING, JsonStringValueType.STRING, JsonBooleanValueType.BOOLEAN,
                JsonBooleanValueType.BOOLEAN, JsonNullValueType.NULL, JsonNullValueType.NULL, true);
    }

    /**
     * Eg:
     *
     * <pre><code>
     *     JsonParser jsonParser = JsonParser.newBuilder()
     *                 .jsonObjectType(JsonObjectType.JSON_MAP)
     *                 .jsonArrayType(JsonArrayType.JSON_LIST)
     *                 .jsonNumberArrayUniformValueType(false)
     *                 .jsonNumberValueTypeForObject(JsonNumberValueType.BIG_DECIMAL)
     *                 .jsonNumberValueTypeForArray(JsonNumberValueType.BIG_DECIMAL)
     *                 .jsonStringValueTypeForObject(JsonStringValueType.STRING)
     *                 .jsonStringValueTypeForArray(JsonStringValueType.STRING)
     *                 .jsonBooleanValueTypeForObject(JsonBooleanValueType.BOOLEAN)
     *                 .jsonBooleanValueTypeForArray(JsonBooleanValueType.BOOLEAN)
     *                 .jsonNullValueTypeForObject(JsonNullValueType.NULL)
     *                 .jsonNullValueTypeForArray(JsonNullValueType.NULL)
     *                 .validateEscapeSequence(true).build();
     *
     *     JsonMap jsonObject = jsonParser.parseJsonObject("""
     *                 {
     *                 "key1" : "val1",
     *                 "key2" : "val2"
     *                 }""") instanceof JsonMap jsonMap ? jsonMap : null;
     *
     *
     *     System.out.println(jsonObject.toJsonString()); //Prints {"key1":"val1","key2":"val2"}
     * </code></pre>
     *
     * @return the builder object.
     */
    public static Builder newBuilder() {
        return new JsonParserBuilderImpl();
    }

    private static boolean isNullValue(final int[] jsonCodePoints) {
        return Arrays.equals(NULL_CODE_POINTS, jsonCodePoints);
    }

    private static boolean isNullValue(final int[] jsonCodePoints, final int[] startEndIndices) {
        if (startEndIndices == null) {
            return isNullValue(jsonCodePoints);
        }
        return Arrays.equals(NULL_CODE_POINTS, 0, NULL_CODE_POINTS.length, jsonCodePoints, startEndIndices[0],
                (startEndIndices[1] + 1));
    }

    static boolean isBooleanValue(final int[] jsonCodePoints, final int[] startEndIndices) {
        return JsonBooleanValueType.isBooleanValue(jsonCodePoints, startEndIndices);
    }

    private static boolean isJsonString(final int[] jsonCodePoints, final int[] startEndIndices) {
        if (startEndIndices == null) {
            return false;
        }
        final int startIndex = startEndIndices[0];
        final int endIndex = startEndIndices[1];
        if (startIndex == endIndex) {
            return false;
        }
        return jsonCodePoints[startIndex] == DOUBLE_QUOTES_CODE_POINT
                && jsonCodePoints[endIndex] == DOUBLE_QUOTES_CODE_POINT;
    }

    private static boolean isJsonObject(final int[] jsonCodePoints, final int[] startEndIndices) {
        if (startEndIndices == null) {
            return isJsonObject(jsonCodePoints);
        }
        return isJsonObject(jsonCodePoints, startEndIndices[0], startEndIndices[1]);
    }

    private static boolean isJsonObject(final int[] jsonCodePoints) {
        if (jsonCodePoints.length <= 1) {
            return false;
        }
        return jsonCodePoints[0] == CURLY_BRACE_START_CODE_POINT
                && jsonCodePoints[jsonCodePoints.length - 1] == CURLY_BRACE_END_CODE_POINT;
    }

    private static boolean isJsonObject(final int[] jsonCodePoints, final int startIndex, final int endIndex) {
        if (startIndex == endIndex) {
            return false;
        }
        return jsonCodePoints[startIndex] == CURLY_BRACE_START_CODE_POINT
                && jsonCodePoints[endIndex] == CURLY_BRACE_END_CODE_POINT;
    }

    private static boolean isJsonArray(final int[] jsonCodePoints, final int[] startEndIndices) {
        if (startEndIndices == null) {
            return isJsonArray(jsonCodePoints);
        }
        final int startIndex = startEndIndices[0];
        final int endIndex = startEndIndices[1];
        if (startIndex == endIndex) {
            return false;
        }
        return isJsonArray(jsonCodePoints, startIndex, endIndex);
    }

    private static boolean isJsonArray(final int[] jsonCodePoints) {
        if (jsonCodePoints.length <= 1) {
            return false;
        }
        return jsonCodePoints[0] == SQUARE_BRACKET_START_CODE_POINT
                && jsonCodePoints[jsonCodePoints.length - 1] == SQUARE_BRACKET_END_CODE_POINT;
    }

    private static boolean isJsonArray(final int[] jsonCodePoints, final int startIndex, final int endIndex) {
        if (startIndex == endIndex) {
            return false;
        }
        return jsonCodePoints[startIndex] == SQUARE_BRACKET_START_CODE_POINT
                && jsonCodePoints[endIndex] == SQUARE_BRACKET_END_CODE_POINT;
    }

    private static boolean isIdValue(final int[] jsonCodePoints) {
        if (jsonCodePoints.length == 0) {
            throw new IllegalJsonFormatException("Invalid JSON");
        }
        final int codePointAtStartIndex = jsonCodePoints[0];
        final int codePointAtEndIndex = jsonCodePoints[jsonCodePoints.length - 1];
        if (codePointAtStartIndex < 0 || codePointAtEndIndex < 0) {
            if (codePointAtStartIndex != codePointAtEndIndex) {
                throw new IllegalJsonFormatException("Invalid JSON!");
            }
            return true;
        }

        return false;
    }

    private static boolean isIdValue(final int[] jsonCodePoints, final int[] startEndIndices) {
        if (startEndIndices == null) {
            return isIdValue(jsonCodePoints);
        }
        if (jsonCodePoints.length == 0) {
            throw new IllegalJsonFormatException("Invalid JSON");
        }
        final int codePointAtStartIndex = jsonCodePoints[startEndIndices[0]];
        final int codePointAtEndIndex = jsonCodePoints[startEndIndices[1]];
        if (codePointAtStartIndex < 0 || codePointAtEndIndex < 0) {
            if (codePointAtStartIndex != codePointAtEndIndex) {
                throw new IllegalJsonFormatException("Invalid JSON!");
            }
            return true;
        }

        return false;
    }

    private Map<String, Object> newMap(final int length) {
        if (JsonObjectType.JSON_MAP.equals(jsonObjectType)) {
            return new JsonMap(length);
        }
        if (JsonObjectType.JSON_CONCURRENT_MAP.equals(jsonObjectType)) {
            return new JsonConcurrentMap(length);
        }
        if (JsonObjectType.JSON_LINKED_MAP.equals(jsonObjectType)) {
            return new JsonLinkedMap(length);
        }
        return new JsonMap(length);
    }

    private List<Object> newList(final int initialCapacity) {
        if (JsonArrayType.JSON_LIST.equals(jsonArrayType)) {
            return new JsonList(initialCapacity);
        }
        if (JsonArrayType.JSON_LINKED_LIST.equals(jsonArrayType)) {
            return new JsonLinkedList();
        }
        return new JsonList(initialCapacity);
    }

    private Map<String, Object> parseJsonObject(final int[] jsonCodePoints, final int startIndex, final int endIndex,
            final List<JsonSection> idToJsonSection) {
        if (isJsonObject(jsonCodePoints, startIndex, endIndex)) {

            final int length = endIndex - startIndex + 1;
            if (length == 2) {
                return JsonObjectType.UNMODIFIABLE_MAP.equals(jsonObjectType) ? Collections.unmodifiableMap(newMap(0))
                        : newMap(0);
            }

            final int[][] keyValueStrings = JsonCodePointUtil.splitByAnyRangeBound(jsonCodePoints, (startIndex + 1),
                    (endIndex - 1), COMMA_CODE_POINT);

            if (keyValueStrings.length == 0) {
                return JsonObjectType.UNMODIFIABLE_MAP.equals(jsonObjectType) ? Collections.unmodifiableMap(newMap(0))
                        : newMap(0);
            }

            final Map<String, Object> keyToValue = newMap(keyValueStrings.length);
            for (final int[] keyValueCodePoints : keyValueStrings) {
                final int[] startEndIndices = JsonCodePointUtil
                        .findFirstAndLastNonWhitespaceIndices(keyValueCodePoints);

                final int[][] keyValue;
                if (startEndIndices != null) {
                    keyValue = JsonCodePointUtil.splitByAnyRangeBound(keyValueCodePoints, startEndIndices[0],
                            startEndIndices[1], COLON_CODE_POINT);
                } else {
                    if (keyValueStrings.length == 1 && JsonCodePointUtil.isBlank(keyValueCodePoints)) {
                        continue;
                    }
                    keyValue = new int[0][0];
                }

                if (keyValue.length != 2) {
                    throw new IllegalJsonFormatException(
                            "Invalid JSON! It contains illegal JSON key-value pair format!");
                }

                final int[] keyPartCodePoints = keyValue[0];
                final int[] keyPartCodePointsIndices = JsonCodePointUtil
                        .findFirstAndLastNonWhitespaceIndices(keyPartCodePoints);
                if (keyPartCodePointsIndices == null) {
                    throw new IllegalJsonFormatException("Invalid JSON! No key found!");
                }
                final int[] valuePartCodePoints = keyValue[1];
                final int[] valuePartCodePointsIndices = JsonCodePointUtil
                        .findFirstAndLastNonWhitespaceIndices(valuePartCodePoints);
                if (valuePartCodePointsIndices == null) {
                    throw new IllegalJsonFormatException("Invalid JSON! No value found!");
                }
                String key;
                if (isIdValue(keyPartCodePoints, keyPartCodePointsIndices)) {
                    final int id = keyPartCodePoints[keyPartCodePointsIndices[0]];
                    final int indexOfId = Math.negateExact(id) - 1;
                    final JsonSection jsonSection = idToJsonSection.get(indexOfId);
                    idToJsonSection.set(indexOfId, null);
                    if (JsonSectionType.STRING.equals(jsonSection.jsonSectionType())) {
                        key = jsonSection.getJsonStringPart();
                    } else {
                        throw new IllegalJsonFormatException(
                                "Invalid JSON! It contains illegal JSON object key format!");
                    }
                } else {
                    key = parseJsonString(keyPartCodePoints, keyPartCodePointsIndices[0], keyPartCodePointsIndices[1]);
                }
                final Object previous;
                if (isIdValue(valuePartCodePoints, valuePartCodePointsIndices)) {
                    final int id = valuePartCodePoints[valuePartCodePointsIndices[0]];
                    final int indexOfId = Math.negateExact(id) - 1;
                    final JsonSection jsonSection = idToJsonSection.get(indexOfId);
                    idToJsonSection.set(indexOfId, null);
                    previous = keyToValue.put(key, jsonSection.getJsonObjectArrayOrStringPart());
                } else if (isNullValue(valuePartCodePoints, valuePartCodePointsIndices)) {
                    previous = keyToValue.put(key, jsonNullValueTypeForObject.nullValue());
                } else if (isBooleanValue(valuePartCodePoints, valuePartCodePointsIndices)) {
                    previous = keyToValue.put(key,
                            jsonBooleanValueTypeForObject.parse(valuePartCodePoints, valuePartCodePointsIndices));
                } else {
                    previous = keyToValue.put(key,
                            jsonNumberValueTypeForObject.parse(valuePartCodePoints, valuePartCodePointsIndices));
                }
                if (previous != null) {
                    throw new IllegalJsonFormatException("Invalid JSON! Duplicate key '%s' found.".formatted(key));
                }
            }
            // Map.copyOf will throw NPE for null values and toString will be different
            return JsonObjectType.UNMODIFIABLE_MAP.equals(jsonObjectType) ? Collections.unmodifiableMap(keyToValue)
                    : keyToValue;
        }

        throw new IllegalJsonFormatException("Invalid json. It contains invalid JSON object.");
    }

    private List<Object> parseJsonArray(final int[] jsonCodePoints, final int startIndex, final int endIndex,
            final List<JsonSection> idToJsonSection) {
        if (isJsonArray(jsonCodePoints, startIndex, endIndex)) {
            final int length = endIndex - startIndex + 1;
            if (length == 2) {
                return JsonArrayType.UNMODIFIABLE_LIST.equals(jsonArrayType) ? Collections.unmodifiableList(newList(0))
                        : newList(0);
            }
            final int[][] valueCodePointsArray = JsonCodePointUtil.splitByAnyRangeBound(jsonCodePoints,
                    (startIndex + 1), (endIndex - 1), COMMA_CODE_POINT);

            if (valueCodePointsArray.length == 0) {
                return JsonArrayType.UNMODIFIABLE_LIST.equals(jsonArrayType) ? Collections.unmodifiableList(newList(0))
                        : newList(0);
            }

            List<Object> values = newList(valueCodePointsArray.length);
            boolean nullOrNumberValuesOnly = true;

            for (final int[] fullPartValueCodePoints : valueCodePointsArray) {
                final int[] fullPartValueCodePointsIndices = JsonCodePointUtil
                        .findFirstAndLastNonWhitespaceIndices(fullPartValueCodePoints);
                if (fullPartValueCodePointsIndices == null) {
                    if (valueCodePointsArray.length == 1 && JsonCodePointUtil.isBlank(fullPartValueCodePoints)) {
                        continue;
                    }
                    throw new IllegalJsonFormatException("Invalid json. It contains invalid JSON array.");
                }
                if (isIdValue(fullPartValueCodePoints, fullPartValueCodePointsIndices)) {
                    final int id = fullPartValueCodePoints[fullPartValueCodePointsIndices[0]];
                    final int indexOfId = Math.negateExact(id) - 1;
                    final JsonSection jsonSection = idToJsonSection.get(indexOfId);
                    idToJsonSection.set(indexOfId, null);
                    final Object v = jsonSection.getJsonObjectArrayOrStringPart();
                    values.add(v);
                    nullOrNumberValuesOnly = false;
                } else if (isNullValue(fullPartValueCodePoints, fullPartValueCodePointsIndices)) {
                    values.add(jsonNullValueTypeForArray.nullValue());
                } else if (isBooleanValue(fullPartValueCodePoints, fullPartValueCodePointsIndices)) {
                    values.add(jsonBooleanValueTypeForArray.parse(fullPartValueCodePoints,
                            fullPartValueCodePointsIndices));
                    nullOrNumberValuesOnly = false;
                } else {
                    values.add(
                            jsonNumberValueTypeForArray.parse(fullPartValueCodePoints, fullPartValueCodePointsIndices));
                }
            }
            if (jsonNumberArrayUniformValueType && nullOrNumberValuesOnly
                    && (JsonNumberValueType.AUTO_INTEGER_LONG_BIG_DECIMAL.equals(jsonNumberValueTypeForArray)
                            || JsonNumberValueType.AUTO_INTEGER_LONG_DOUBLE.equals(jsonNumberValueTypeForArray))) {

                boolean containsMixedNumberTypes = false;
                boolean convertToLong = false;

                Class<?> previousValueClass = null;
                for (final Object value : values) {
                    if (value != null) {
                        final Class<?> valueClass = value.getClass();
                        if (previousValueClass != null && !previousValueClass.equals(valueClass)) {
                            containsMixedNumberTypes = true;
                            break;
                        }
                        previousValueClass = valueClass;
                    }
                }

                if (containsMixedNumberTypes) {
                    for (final Object value : values) {
                        if (value != null) {
                            if (value instanceof Long || value instanceof Integer) {
                                convertToLong = true;
                            } else {
                                convertToLong = false;
                                break;
                            }
                        }
                    }
                }

                if (containsMixedNumberTypes) {
                    final List<Object> convertedValues = newList(values.size());
                    if (convertToLong) {
                        for (final Object value : values) {
                            if (value != null) {
                                convertedValues.add(Long.valueOf(value.toString()));
                            } else {
                                convertedValues.add(null);
                            }
                        }
                    } else {
                        if (JsonNumberValueType.AUTO_INTEGER_LONG_DOUBLE.equals(jsonNumberValueTypeForArray)) {
                            for (final Object value : values) {
                                if (value != null) {
                                    convertedValues.add(Double.valueOf(value.toString()));
                                } else {
                                    convertedValues.add(null);
                                }
                            }
                        } else {
                            for (final Object value : values) {
                                if (value != null) {
                                    convertedValues.add(new BigDecimal(value.toString()));
                                } else {
                                    convertedValues.add(null);
                                }
                            }
                        }
                    }

                    values = convertedValues;
                }
            }

            // List.copyOf will throw NPE for null values and toString will be different
            return JsonArrayType.UNMODIFIABLE_LIST.equals(jsonArrayType) ? Collections.unmodifiableList(values)
                    : values;
        }
        throw new IllegalJsonFormatException("Invalid json. It contains invalid JSON array.");
    }

    private String parseJsonString(final int[] codePoints, final int startIndex, final int endIndex) {
        final int startIndexAfterDoubleQuote = startIndex + 1;
        final int endIndexBeforeDoubleQuote = endIndex - 1;
        final int length = endIndexBeforeDoubleQuote - startIndexAfterDoubleQuote + 1;
        if (length <= 0) {
            return "";
        }
        return new String(codePoints, startIndexAfterDoubleQuote, length);
    }

    private JsonSection buildOuterMostJsonSection(final int[] jsonCodePoints, final int[] startEndIndices) {
        if (!isJsonArray(jsonCodePoints, startEndIndices) && !isJsonObject(jsonCodePoints, startEndIndices)
                && !isJsonString(jsonCodePoints, startEndIndices)) {
            throw new IllegalJsonFormatException(
                    "Invalid JSON! The JSON should either be a JSON object, a JSON array or a JSON encoded string value!");
        }

        final List<JsonSection> idToJsonSection = new ArrayList<>();
        // should decrement
        int nodeId = 0;

        final Deque<JsonSection> objectNodeDeque = new ArrayDeque<>();
        final Deque<JsonSection> arrayNodeDeque = new ArrayDeque<>();
        JsonSection jsonStringNode = null;

        JsonSection outerMostJsonSection = null;

        final int endIndex = startEndIndices[1];
        for (int i = startEndIndices[0]; i <= endIndex; i++) {
            final int codePoint = jsonCodePoints[i];
            final boolean jsonStringNodeIsNull = jsonStringNode == null;
            if (!jsonStringNodeIsNull && ESCAPE_CODE_POINT == codePoint) {
                if (validateEscapeSequence) {
                    final int nextCodePoint = jsonCodePoints[i + 1];
                    if (!JsonStringUtil.isJsonStringEscapableDelim(nextCodePoint)) {
                        throw new IllegalJsonFormatException(
                                "Invalid JSON! The JSON string contains illegal escape sequence (\\)!");
                    }
                    if (nextCodePoint == JsonCodePointUtil.U_CODE_POINT) {
                        if (!JsonCodePointUtil.isValidUnicodeEscapeSequence(jsonCodePoints, i, i + 5)) {
                            throw new IllegalJsonFormatException(
                                    "Invalid JSON! The JSON string contains illegal Unicode escape sequence! check "
                                            .concat(new String(jsonCodePoints, i, Math.min(6, (endIndex - i + 1)))));
                        }
                        i += 5;
                        continue;
                    }
                }
                i++;
                continue;
            }

            // should be after ESCAPE_CODE_POINT checking
            if (!jsonStringNodeIsNull && codePoint != DOUBLE_QUOTES_CODE_POINT) {
                continue;
            }
            switch (codePoint) {
            case CURLY_BRACE_START_CODE_POINT -> {
                nodeId--;
                final JsonSection jsonSection = new JsonSection(nodeId, JsonSectionType.OBJECT, i);
                objectNodeDeque.offerFirst(jsonSection);
                idToJsonSection.add(jsonSection);

                if (outerMostJsonSection == null) {
                    outerMostJsonSection = jsonSection;
                }
            }
            case CURLY_BRACE_END_CODE_POINT -> {
                final JsonSection jsonSection = objectNodeDeque.poll();
                if (jsonSection == null || (jsonSection.startIndex() == 0 && i != endIndex)) {
                    throw new IllegalJsonFormatException("Invalid JSON! It contains invalid JSON object.");
                }
                jsonSection.setEndIndex(i);
                jsonSection.setJsonObject(parseJsonObject(jsonCodePoints, jsonSection.startIndex(),
                        jsonSection.getEndIndex(), idToJsonSection));
                for (int j = idToJsonSection.size() - 1; j > 0; j--) {
                    if (idToJsonSection.get(j) == null) {
                        idToJsonSection.remove(j);
                        nodeId = -j;
                    } else {
                        break;
                    }
                }
                fillJsonSectionId(jsonCodePoints, jsonSection);
            }
            case SQUARE_BRACKET_START_CODE_POINT -> {
                nodeId--;
                final JsonSection jsonSection = new JsonSection(nodeId, JsonSectionType.ARRAY, i);
                arrayNodeDeque.offerFirst(jsonSection);
                idToJsonSection.add(jsonSection);
                if (outerMostJsonSection == null) {
                    outerMostJsonSection = jsonSection;
                }
            }
            case SQUARE_BRACKET_END_CODE_POINT -> {
                final JsonSection jsonSection = arrayNodeDeque.poll();
                if (jsonSection == null || (jsonSection.startIndex() == 0 && i != endIndex)) {
                    throw new IllegalJsonFormatException("Invalid JSON! It contains invalid JSON array.");
                }
                jsonSection.setEndIndex(i);
                jsonSection.setJsonArray(parseJsonArray(jsonCodePoints, jsonSection.startIndex(),
                        jsonSection.getEndIndex(), idToJsonSection));
                for (int j = idToJsonSection.size() - 1; j > 0; j--) {
                    if (idToJsonSection.get(j) == null) {
                        idToJsonSection.remove(j);
                        nodeId = -j;
                    } else {
                        break;
                    }
                }
                fillJsonSectionId(jsonCodePoints, jsonSection);
            }
            case DOUBLE_QUOTES_CODE_POINT -> {
                if (jsonStringNodeIsNull) {
                    nodeId--;
                    final JsonSection jsonSection = new JsonSection(nodeId, JsonSectionType.STRING, i);
                    jsonStringNode = jsonSection;
                    idToJsonSection.add(jsonSection);
                    if (outerMostJsonSection == null) {
                        outerMostJsonSection = jsonSection;
                    }
                } else {
                    final JsonSection jsonSection = jsonStringNode;
                    jsonSection.setEndIndex(i);
                    jsonStringNode = null;
                    jsonSection.setJsonStringPart(
                            parseJsonString(jsonCodePoints, jsonSection.startIndex(), jsonSection.getEndIndex()));
                    fillJsonSectionId(jsonCodePoints, jsonSection);
                }
            }
            }
        }

        if (jsonStringNode != null) {
            throw new IllegalJsonFormatException("Invalid JSON! Closing \" is missing!");
        }
        if (!objectNodeDeque.isEmpty()) {
            throw new IllegalJsonFormatException("Invalid JSON! Closing } is missing!");
        }
        if (!arrayNodeDeque.isEmpty()) {
            throw new IllegalJsonFormatException("Invalid JSON! Closing ] is missing!");
        }

        return outerMostJsonSection;
    }

    private static void fillJsonSectionId(final int[] jsonCodePoints, final JsonSection jsonSection) {
        final int id = jsonSection.id();
        final int startIndex = jsonSection.startIndex();
        final int endIndex = jsonSection.getEndIndex();
        final int indicesDiff = endIndex - startIndex;
        jsonCodePoints[startIndex] = id;
        jsonCodePoints[endIndex] = id;
        if (indicesDiff > 1) {
            jsonCodePoints[startIndex + 1] = indicesDiff;
        }
    }

    /**
     * @param json the JSON text. It can be an object, array, json encoded string
     *             value, a boolean value or a number value.
     * @return the object after parsing.
     * @since 12.0.4
     */
    public Object parseJson(final String json) {
        final int[] jsonCodePoints = json.codePoints().toArray();
        final int[] startEndIndices = JsonCodePointUtil.findFirstAndLastNonWhitespaceIndices(jsonCodePoints);
        final boolean isBooleanValue = isBooleanValue(jsonCodePoints, startEndIndices);
        final boolean notJsonArrayObjectStringValue = !isJsonString(jsonCodePoints, startEndIndices)
                && !isJsonArray(jsonCodePoints, startEndIndices) && !isJsonObject(jsonCodePoints, startEndIndices);
        if (!isBooleanValue && notJsonArrayObjectStringValue) {
            try {
                return new BigDecimal(json.strip());
            } catch (final NumberFormatException e) {
                throw new IllegalJsonFormatException("Invalid JSON!", e);
            }
        }
        if (isBooleanValue) {
            final Boolean b = JsonBooleanValueType.parseBooleanOtherwiseNull(jsonCodePoints, startEndIndices);
            if (b != null) {
                return b;
            }
        }
        if (notJsonArrayObjectStringValue) {
            throw new IllegalJsonFormatException(
                    "Invalid JSON! The JSON should either be a JSON object, a JSON array, a JSON encoded string value, a boolean value or a number value!");
        }
        final JsonSection outerMostJsonSection = buildOuterMostJsonSection(jsonCodePoints, startEndIndices);
        if (outerMostJsonSection == null) {
            return null;
        }
        if (JsonSectionType.OBJECT.equals(outerMostJsonSection.jsonSectionType())) {
            return outerMostJsonSection.getJsonObject();
        }
        if (JsonSectionType.ARRAY.equals(outerMostJsonSection.jsonSectionType())) {
            return outerMostJsonSection.getJsonArray();
        }
        if (JsonSectionType.STRING.equals(outerMostJsonSection.jsonSectionType())) {
            return outerMostJsonSection.getJsonStringPart();
        }
        return null;
    }

    /**
     * @param json the JSON string which starts with { and ends with }.
     * @return the Map from the JSON object.
     * @since 12.0.4
     */
    public Map<String, Object> parseJsonObject(final String json) {
        final int[] jsonCodePoints = json.codePoints().toArray();
        final int[] startEndIndices = JsonCodePointUtil.findFirstAndLastNonWhitespaceIndices(jsonCodePoints);
        if (isJsonObject(jsonCodePoints, startEndIndices)) {
            final JsonSection outerMostJsonSection = buildOuterMostJsonSection(jsonCodePoints, startEndIndices);
            return outerMostJsonSection.getJsonObject();
        }
        throw new IllegalJsonFormatException("Invalid JSON object!");
    }

    /**
     * @param json the JSON string which starts with [ and ends with ].
     * @return the List from the JSON array.
     * @since 12.0.4
     */
    public List<Object> parseJsonArray(final String json) {
        final int[] jsonCodePoints = json.codePoints().toArray();
        final int[] startEndIndices = JsonCodePointUtil.findFirstAndLastNonWhitespaceIndices(jsonCodePoints);
        if (isJsonArray(jsonCodePoints, startEndIndices)) {
            final JsonSection outerMostJsonSection = buildOuterMostJsonSection(jsonCodePoints, startEndIndices);
            return outerMostJsonSection.getJsonArray();
        }
        throw new IllegalJsonFormatException("Invalid JSON array!");
    }

    /**
     * @param json the JSON string which starts and ends with double quotes (").
     * @return the String after parsing.
     * @since 12.0.4
     */
    public String parseJsonEncodedStringValue(final String json) {
        final int[] jsonCodePoints = json.codePoints().toArray();
        final int[] startEndIndices = JsonCodePointUtil.findFirstAndLastNonWhitespaceIndices(jsonCodePoints);
        if (isJsonString(jsonCodePoints, startEndIndices)) {
            final JsonSection outerMostJsonSection = buildOuterMostJsonSection(jsonCodePoints, startEndIndices);
            return outerMostJsonSection.getJsonStringPart();
        }
        throw new IllegalJsonFormatException("Invalid JSON encoded string value! It should start and end with \"");
    }

}
