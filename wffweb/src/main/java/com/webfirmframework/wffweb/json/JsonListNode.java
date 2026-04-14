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
import com.webfirmframework.wffweb.TriConsumer;
import com.webfirmframework.wffweb.util.StringUtil;
import com.webfirmframework.wffweb.wffbm.data.BMValueType;
import com.webfirmframework.wffweb.wffbm.data.WffBMArray;
import com.webfirmframework.wffweb.wffbm.data.WffBMNumberArray;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @since 12.0.4
 */
public sealed interface JsonListNode extends JsonBaseNode, List<Object> permits JsonList, JsonLinkedList {

    /**
     * @param index the index to get value.
     * @return the string value or null.
     * @since 12.0.4
     */
    default String getValueAsString(final int index) {
        final Object o = get(index);
        if (o == null) {
            return null;
        }
        if (o instanceof final String s) {
            return s;
        }
        if (o instanceof final JsonValue jsonValue) {
            return JsonValueType.ENCODED_STRING.equals(jsonValue.valueType()) ? jsonValue.asString(true) : jsonValue.asString(false);
        }
        return o.toString();
    }

    /**
     * @param index                      the index to get value.
     * @param decodeUnicodeCharsSequence true to decode the Unicode chars sequences like <span>\</span><span>u2122</span> to Java chars or false to use as it is.
     * @return the string value or null.
     * @since 12.0.11
     */
    default String getValueAsString(final int index, final boolean decodeUnicodeCharsSequence) {
        final Object o = get(index);
        if (o == null) {
            return null;
        }
        if (o instanceof final String s) {
            if (decodeUnicodeCharsSequence) {
                final int[] codePoints = StringUtil.toCodePoints(s);
                return JsonStringUtil.toUnicodeCharsDecodedString(codePoints, 0, codePoints.length);
            }
            return s;
        }
        if (o instanceof final JsonValue jsonValue) {
            return JsonValueType.ENCODED_STRING.equals(jsonValue.valueType()) ? jsonValue.asString(true, decodeUnicodeCharsSequence) : jsonValue.asString(false, decodeUnicodeCharsSequence);
        }
        return o.toString();
    }

    /**
     * @param index the index to get value.
     * @return the Boolean value or null. It will throw exception if the value is
     * not parsable to Boolean.
     * @since 12.0.11
     */
    default Boolean getValueAsBoolean(final int index) {
        final Object o = get(index);
        if (o == null) {
            return null;
        }
        if (o instanceof final Boolean i) {
            return i;
        }
        if (o instanceof final JsonValue jsonValue) {
            return jsonValue.asBoolean();
        }
        return Boolean.valueOf(o.toString());
    }

    /**
     * @param index the index to get value.
     * @return the Integer value or null. It will throw exception if the value is
     * not parsable to integer.
     * @since 12.0.4
     */
    default Integer getValueAsInteger(final int index) {
        final Object o = get(index);
        if (o == null) {
            return null;
        }
        if (o instanceof final Integer i) {
            return i;
        }
        if (o instanceof final JsonValue jsonValue) {
            return jsonValue.asInteger();
        }
        return Integer.valueOf(o.toString());
    }

    /**
     * @param index the index to get value.
     * @return the Long value or null. It will throw exception if the value is not
     * parsable to Long.
     * @since 12.0.4
     */
    default Long getValueAsLong(final int index) {
        final Object o = get(index);
        if (o == null) {
            return null;
        }
        if (o instanceof final Long l) {
            return l;
        }
        if (o instanceof final JsonValue jsonValue) {
            return jsonValue.asLong();
        }
        return Long.valueOf(o.toString());
    }

    /**
     * @param index the index to get value.
     * @return the Double value or null. It will throw exception if the value is not
     * parsable to Double.
     * @since 12.0.4
     */
    default Double getValueAsDouble(final int index) {
        final Object o = get(index);
        if (o == null) {
            return null;
        }
        if (o instanceof final Double l) {
            return l;
        }
        if (o instanceof final JsonValue jsonValue) {
            return jsonValue.asDouble();
        }
        return Double.valueOf(o.toString());
    }

    /**
     * @param index the index to get value.
     * @return the BigInteger value or null. It will throw exception if the value is
     * not parsable to BigInteger.
     * @since 12.0.4
     */
    default BigInteger getValueAsBigInteger(final int index) {
        final Object o = get(index);
        if (o == null) {
            return null;
        }
        if (o instanceof final BigInteger bi) {
            return bi;
        }
        if (o instanceof final JsonValue jsonValue) {
            return jsonValue.asBigInteger();
        }
        return new BigInteger(o.toString());
    }

    /**
     * @param index the index to get value.
     * @return the BigDecimal value or null. It will throw exception if the value is
     * not parsable to BigDecimal.
     * @since 12.0.4
     */
    default BigDecimal getValueAsBigDecimal(final int index) {
        final Object o = get(index);
        if (o == null) {
            return null;
        }
        if (o instanceof final BigDecimal bd) {
            return bd;
        }
        if (o instanceof final JsonValue jsonValue) {
            return jsonValue.asBigDecimal();
        }
        return new BigDecimal(o.toString());
    }

    /**
     * @param index the index to get value.
     * @return the value as JsonValue object.
     * @throws InvalidValueException if the value is not suitable to create an object of JsonValue.
     * @since 12.0.11
     */
    default JsonValue getValueAsJsonValue(final int index) {
        final Object o = get(index);
        if (o == null) {
            return new JsonValue();
        }
        if (o instanceof final JsonValue jsonValue) {
            return jsonValue;
        }
        if (o instanceof final String s) {
            return new JsonValue(StringUtil.toCodePoints(s), JsonValueType.STRING);
        }
        if (o instanceof final Number n) {
            return new JsonValue(StringUtil.toCodePoints(n.toString()), JsonValueType.NUMBER);
        }
        if (o instanceof final Boolean b) {
            return new JsonValue(StringUtil.toCodePoints(b.toString()), JsonValueType.BOOLEAN);
        }
        throw new InvalidValueException("""
                The underlying value is not suitable to create an object of JsonValue.
                The underlying value should be one of the types of JsonValue, String, Number, Boolean or null.""");
    }

    /**
     * @param index the index to get value.
     * @return the JsonBaseNode value or null. It will throw exception if the value
     * is not an instance of JsonBaseNode.
     * @since 12.0.11
     */
    default JsonBaseNode getValueAsJsonBaseNode(final int index) {
        final Object o = get(index);
        if (o == null) {
            return null;
        }
        return (JsonBaseNode) o;
    }

    /**
     * @param index the index to get value.
     * @return the JsonListNode value or null. It will throw exception if the value
     * is not an instance of JsonListNode.
     * @since 12.0.4
     */
    default JsonListNode getValueAsJsonListNode(final int index) {
        final Object o = get(index);
        if (o == null) {
            return null;
        }
        return (JsonListNode) o;
    }

    /**
     * @param index the index to get value.
     * @return the JsonMapNode value or null. It will throw exception if the value
     * is not an instance of JsonMapNode.
     * @since 12.0.4
     */
    default JsonMapNode getValueAsJsonMapNode(final int index) {
        final Object o = get(index);
        if (o == null) {
            return null;
        }
        return (JsonMapNode) o;
    }

    /**
     * @return the JSON array string.
     * @since 12.0.4
     */
    @Override
    default String toJsonString() {
        return stream().map(JsonStringUtil::buildJsonValue).collect(Collectors.joining(",", "[", "]"));
    }

    /**
     * @return the JSON array string.
     * @since 12.0.9
     */
    @Override
    default String toBigJsonString() {
        // Note: never use parallel stream as its index order of values should be
        // unchanged.
        return stream().map(JsonStringUtil::buildBigJsonValue).collect(Collectors.joining(",", "[", "]"));
    }

    /**
     * @param outputStream the OutputStream to write the json.
     * @param charset      the charset
     * @param flushOnWrite true to flush after each write operation otherwise false.
     * @throws IOException if writing to OutputStream throws an exception.
     * @since 12.0.9
     */
    @Override
    default void toOutputStream(final OutputStream outputStream, final Charset charset, final boolean flushOnWrite)
            throws IOException {
        final byte squareBracketOpen = "[".getBytes(charset)[0];
        final byte squareBracketClose = "]".getBytes(charset)[0];
        final byte comma = ",".getBytes(charset)[0];
        outputStream.write(squareBracketOpen);
        boolean first = true;
        for (final Object value : this) {
            if (!first) {
                outputStream.write(comma);
            }
            JsonStringUtil.writeJsonValue(outputStream, charset, flushOnWrite, value);
            first = false;
        }
        outputStream.write(squareBracketClose);
        if (flushOnWrite) {
            outputStream.flush();
        }
    }

    /**
     * NB: The array should not contain null values.
     *
     * @return the WffBMArray.
     * @since 12.0.4
     */
    default WffBMArray toWffBMArray() {
        if (isEmpty()) {
            return new WffBMArray(BMValueType.STRING);
        }
        Object one = null;

        for (final Object o : this) {
            if (o == null) {
                throw new InvalidValueException("null is not allowed in the array to convert to WffBMArray.");
            }
        }
        boolean booleanType = false;
        boolean numberType = false;
        boolean stringType = false;
        boolean listType = false;
        boolean mapType = false;
        for (final Object o : this) {
            if (one != null) {
                if (one instanceof Number && o instanceof Number) {
                    numberType = true;
                } else if (one instanceof Boolean && o instanceof Boolean) {
                    booleanType = true;
                } else if (one instanceof String && o instanceof String) {
                    stringType = true;
                } else if (one instanceof JsonListNode && o instanceof JsonListNode) {
                    listType = true;
                } else if (one instanceof JsonMapNode && o instanceof JsonMapNode) {
                    mapType = true;
                } else {
                    throw new InvalidValueException("All items in the array should be same type.");
                }
            }
            one = o;
        }
        if (size() == 1) {
            if (one instanceof Number) {
                numberType = true;
            } else if (one instanceof Boolean) {
                booleanType = true;
            } else if (one instanceof String) {
                stringType = true;
            } else if (one instanceof JsonListNode) {
                listType = true;
            } else if (one instanceof JsonMapNode) {
                mapType = true;
            } else {
                throw new InvalidValueException(
                        "The item in the list should be one of Number, Boolean, String, JsonListNode, or JsonMapNode");
            }
        }

        if (stringType) {
            final WffBMArray stringArray = new WffBMArray(BMValueType.STRING);
            for (final Object o : this) {
                stringArray.add(o.toString());
            }
            return stringArray;
        }
        if (numberType) {
            final WffBMNumberArray<Number> bmNumberArray = new WffBMNumberArray<>();
            for (final Object o : this) {
                bmNumberArray.addNumber((Number) o);
            }
            return bmNumberArray;
        }
        if (booleanType) {
            final WffBMArray booleanArray = new WffBMArray(BMValueType.BOOLEAN);
            booleanArray.addAll(this);
            return booleanArray;
        }
        if (listType) {
            final WffBMArray arrayOfArray = new WffBMArray(BMValueType.BM_ARRAY);
            for (final Object o : this) {
                final JsonListNode listNode = (JsonListNode) o;
                arrayOfArray.add(listNode.toWffBMArray());
            }
            return arrayOfArray;
        }
        if (mapType) {
            final WffBMArray arrayOfArray = new WffBMArray(BMValueType.BM_ARRAY);
            for (final Object o : this) {
                final JsonMapNode mapNode = (JsonMapNode) o;
                arrayOfArray.add(mapNode.toWffBMObject());
            }
            return arrayOfArray;
        }

        return new WffBMArray(BMValueType.STRING);
    }

    /**
     * <pre><code>
     *         class CustomJsonMap extends JsonMap {
     *         }
     *         class CustomList extends JsonList {
     *         }
     *
     *         JsonMap jsonMap = new  JsonMap();
     *         jsonMap.put("number", new JsonValue("14", JsonValueType.NUMBER));
     *         jsonMap.put("string", "string value");
     *         jsonMap.put("bool", true);
     *         jsonMap.put("fornull1", new JsonValue());
     *         jsonMap.put("fornull2", null);
     *
     *         JsonList jsonList = new JsonList();
     *         jsonList.add(jsonMap);
     *
     *         CustomList convertedObject = jsonList.convertTo(
     *         CustomJsonMap::new, CustomJsonMap::put, CustomList::new, CustomList::add, true);
     * </code></pre>
     *
     * @param jsonObjectSupplier the supplier to provide new JSON object.
     * @param putOperation       the TriConsumer of the supplied JSON object, the key and value.
     * @param jsonArraySupplier  the supplier to provide new JSON array.
     * @param addOperation       the BiConsumer of the supplied JSON array and value.
     * @param <T>                the JSON object class type.
     * @param <U>                the JSON array class type. It is the return type of the method.
     * @param parseJsonValue     true to parse the JsonValue to appropriate value types if the underlying value is a JsonValue object.
     * @return the converted object.
     * @since 12.0.11
     */
    default <T, U> U convertTo(final Supplier<T> jsonObjectSupplier, final TriConsumer<T, String, Object> putOperation,
                               final Supplier<U> jsonArraySupplier, final BiConsumer<U, Object> addOperation, final boolean parseJsonValue) {
        final U u = jsonArraySupplier.get();
        for (final Object value : this) {
            if (value instanceof final JsonMapNode jsonMapNode) {
                addOperation.accept(u, jsonMapNode.convertTo(jsonObjectSupplier, putOperation, jsonArraySupplier, addOperation, parseJsonValue));
            } else if (value instanceof final JsonListNode jsonListNode) {
                addOperation.accept(u, jsonListNode.convertTo(jsonObjectSupplier, putOperation, jsonArraySupplier, addOperation, parseJsonValue));
            } else if (parseJsonValue && value instanceof final JsonValue jsonValue) {
                switch (jsonValue.valueType()) {
                    case NULL -> addOperation.accept(u, null);
                    case ENCODED_STRING -> addOperation.accept(u, jsonValue.asString(true));
                    case STRING -> addOperation.accept(u, jsonValue.asString());
                    case NUMBER -> {
                        final int[] codePoints = jsonValue.originalCodePoints();
                        if (codePoints != null) {
                            addOperation.accept(u, JsonNumberValueType.AUTO_INTEGER_LONG_BIG_DECIMAL.parse(
                                    codePoints, new int[]{0, codePoints.length - 1}));
                        } else {
                            addOperation.accept(u, value);
                        }
                    }
                    case BOOLEAN -> addOperation.accept(u, jsonValue.asBoolean());
                }
            } else {
                addOperation.accept(u, value);
            }
        }
        return u;
    }

}
