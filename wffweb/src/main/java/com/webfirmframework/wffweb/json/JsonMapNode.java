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
import java.util.Map;
import java.util.stream.Collectors;

import com.webfirmframework.wffweb.InvalidValueException;
import com.webfirmframework.wffweb.wffbm.data.WffBMObject;

/**
 * @since 12.0.4
 */
public sealed interface JsonMapNode
        extends JsonBaseNode, Map<String, Object>permits JsonMap, JsonConcurrentMap, JsonLinkedMap {

    /**
     * @param key the key to get value.
     * @return the string value or null.
     * @since 12.0.4
     */
    default String getValueAsString(final String key) {
        if (key == null) {
            return null;
        }
        final Object o = get(key);
        if (o == null) {
            return null;
        }
        if (o instanceof final String s) {
            return s;
        }
        return o.toString();
    }

    /**
     * @param key the key to get value.
     * @return the Integer value or null. It will throw exception if the value is
     *         not parsable to integer.
     * @since 12.0.4
     */
    default Integer getValueAsInteger(final String key) {
        if (key == null) {
            return null;
        }
        final Object o = get(key);
        if (o == null) {
            return null;
        }
        if (o instanceof final Integer i) {
            return i;
        }
        return Integer.valueOf(o.toString());
    }

    /**
     * @param key the key to get value.
     * @return the Long value or null. It will throw exception if the value is not
     *         parsable to Long.
     * @since 12.0.4
     */
    default Long getValueAsLong(final String key) {
        if (key == null) {
            return null;
        }
        final Object o = get(key);
        if (o == null) {
            return null;
        }
        if (o instanceof final Long l) {
            return l;
        }
        return Long.valueOf(o.toString());
    }

    /**
     * @param key the key to get value.
     * @return the Double value or null. It will throw exception if the value is not
     *         parsable to Double.
     * @since 12.0.4
     */
    default Double getValueAsDouble(final String key) {
        if (key == null) {
            return null;
        }
        final Object o = get(key);
        if (o == null) {
            return null;
        }
        if (o instanceof final Double l) {
            return l;
        }
        return Double.valueOf(o.toString());
    }

    /**
     * @param key the key to get value.
     * @return the BigInteger value or null. It will throw exception if the value is
     *         not parsable to BigInteger.
     * @since 12.0.4
     */
    default BigInteger getValueAsBigInteger(final String key) {
        if (key == null) {
            return null;
        }
        final Object o = get(key);
        if (o == null) {
            return null;
        }
        if (o instanceof final BigInteger bi) {
            return bi;
        }
        return new BigInteger(o.toString());
    }

    /**
     * @param key the key to get value.
     * @return the BigDecimal value or null. It will throw exception if the value is
     *         not parsable to BigDecimal.
     * @since 12.0.4
     */
    default BigDecimal getValueAsBigDecimal(final String key) {
        if (key == null) {
            return null;
        }
        final Object o = get(key);
        if (o == null) {
            return null;
        }
        if (o instanceof final BigDecimal bd) {
            return bd;
        }
        return new BigDecimal(o.toString());
    }

    /**
     * @param key the key to get value.
     * @return the JsonListNode value or null. It will throw exception if the value
     *         is not an instance of JsonListNode.
     * @since 12.0.4
     */
    default JsonListNode getValueAsJsonListNode(final String key) {
        if (key == null) {
            return null;
        }
        final Object o = get(key);
        if (o == null) {
            return null;
        }
        return (JsonListNode) o;
    }

    /**
     * @param key the key to get value.
     * @return the JsonMapNode value or null. It will throw exception if the value
     *         is not an instance of JsonMapNode.
     * @since 12.0.4
     */
    default JsonMapNode getValueAsJsonMapNode(final String key) {
        if (key == null) {
            return null;
        }
        final Object o = get(key);
        if (o == null) {
            return null;
        }
        return (JsonMapNode) o;
    }

    /**
     * @return the JSON object string.
     * @since 12.0.4
     */
    @Override
    default String toJsonString() {
        return entrySet().stream().map(JsonStringUtil::buildJsonKeyValue).collect(Collectors.joining(",", "{", "}"));
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
        final byte curlyBraceOpen = "{".getBytes(charset)[0];
        final byte curlyBraceClose = "}".getBytes(charset)[0];
        final byte comma = ",".getBytes(charset)[0];
        outputStream.write(curlyBraceOpen);
        boolean first = true;
        for (final Map.Entry<String, Object> entry : entrySet()) {
            if (!first) {
                outputStream.write(comma);
            }
            JsonStringUtil.writeJsonKeyValue(outputStream, charset, flushOnWrite, entry);
            first = false;
        }
        outputStream.write(curlyBraceClose);
        if (flushOnWrite) {
            outputStream.flush();
        }
    }

    /**
     * It internally utilizes parallel stream to build json string. The order of
     * json object entries will be unpredictable but the json array will maintain
     * its values order.
     *
     * @return the JSON object string.
     * @since 12.0.4
     */
    @Override
    default String toBigJsonString() {
        return entrySet().stream().parallel().map(JsonStringUtil::buildBigJsonKeyValue)
                .collect(Collectors.joining(",", "{", "}"));
    }

    /**
     * @return the WffBMObject.
     * @since 12.0.4
     */
    default WffBMObject toWffBMObject() {
        final WffBMObject bmObject = new WffBMObject();
        for (final Entry<String, Object> entry : entrySet()) {
            final Object value = entry.getValue();
            final String key = entry.getKey();
            if (value == null) {
                bmObject.putNull(key);
            } else if (value instanceof final Number n) {
                bmObject.put(key, n);
            } else if (value instanceof final Boolean b) {
                bmObject.put(key, b);
            } else if (value instanceof final String s) {
                bmObject.putString(key, s);
            } else if (value instanceof final JsonListNode l) {
                bmObject.put(key, l.toWffBMArray());
            } else if (value instanceof final JsonMapNode m) {
                bmObject.put(key, m.toWffBMObject());
            } else {
                throw new InvalidValueException(
                        "%s type object is not allowed to convert to WffBMObject!".formatted(value.getClass()));
            }
        }

        return bmObject;
    }
}
