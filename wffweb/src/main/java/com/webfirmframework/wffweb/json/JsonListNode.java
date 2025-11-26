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

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

import com.webfirmframework.wffweb.InvalidValueException;
import com.webfirmframework.wffweb.wffbm.data.BMValueType;
import com.webfirmframework.wffweb.wffbm.data.WffBMArray;
import com.webfirmframework.wffweb.wffbm.data.WffBMNumberArray;

/**
 * @since 12.0.4
 */
public sealed interface JsonListNode extends JsonBaseNode, List<Object>permits JsonList, JsonLinkedList {

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
        return o.toString();
    }

    /**
     * @param index the index to get value.
     * @return the Integer value or null. It will throw exception if the value is
     *         not parsable to integer.
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
        return Integer.valueOf(o.toString());
    }

    /**
     * @param index the index to get value.
     * @return the Long value or null. It will throw exception if the value is not
     *         parsable to Long.
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
        return Long.valueOf(o.toString());
    }

    /**
     * @param index the index to get value.
     * @return the Double value or null. It will throw exception if the value is not
     *         parsable to Double.
     * @since 12.0.4
     */
    default Double getValueAsDouble(final int index) {
        final Object o = get(index);
        if (o == null) {
            return null;
        }
        if (o instanceof final Double d) {
            return d;
        }
        return Double.valueOf(o.toString());
    }

    /**
     * @param index the index to get value.
     * @return the BigInteger value or null. It will throw exception if the value is
     *         not parsable to BigInteger.
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
        return new BigInteger(o.toString());
    }

    /**
     * @param index the index to get value.
     * @return the BigDecimal value or null. It will throw exception if the value is
     *         not parsable to BigDecimal.
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
        return new BigDecimal(o.toString());
    }

    /**
     * @param index the index to get value.
     * @return the JsonListNode value or null. It will throw exception if the value
     *         is not an instance of JsonListNode.
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
     *         is not an instance of JsonMapNode.
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
}
