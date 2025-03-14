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

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * To parse json object string:
 *
 * <pre><code>
 * JsonMapNode jsonMap = JsonConcurrentMap.parse("""
 *             {
 *             "key" : "value",
 *             "key1" : "value1"
 *             }
 *             """);
 * // To get value
 * String key1Value = jsonMap.getValueAsString("key1");
 *
 * // Prints value
 * System.out.println("key1Value = " + key1Value); // key1Value = value1
 *
 * String jsonString = jsonMap.toJsonString();
 * // Prints json string
 * System.out.println("jsonString = " + jsonString); // jsonString = {"key1":"value1","key":"value"}
 * </code></pre>
 *
 * @since 12.0.4
 */
public non-sealed class JsonConcurrentMap extends ConcurrentHashMap<String, Object>
        implements JsonMapNode, Serializable {

    private static final JsonParser JSON_PARSER = JsonParser.newBuilder()
            .jsonObjectType(JsonObjectType.JSON_CONCURRENT_MAP).jsonArrayType(JsonArrayType.JSON_LINKED_LIST)
            .validateEscapeSequence(true).build();

    public JsonConcurrentMap() {
        super();
    }

    public JsonConcurrentMap(final int initialCapacity) {
        super(initialCapacity);
    }

    public JsonConcurrentMap(final Map<? extends String, ?> m) {
        super(m);
    }

    public JsonConcurrentMap(final int initialCapacity, final float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public JsonConcurrentMap(final int initialCapacity, final float loadFactor, final int concurrencyLevel) {
        super(initialCapacity, loadFactor, concurrencyLevel);
    }

    /**
     * @param json the json string to parse
     * @return the JsonConcurrentMap object which is equalent to JSON object.
     * @since 12.0.4
     */
    public static JsonConcurrentMap parse(final String json) {
        if (JSON_PARSER.parseJsonObject(json) instanceof final JsonConcurrentMap jsonMap) {
            return jsonMap;
        }
        return null;
    }

    @Override
    public String toString() {
        return toJsonString();
    }
}
