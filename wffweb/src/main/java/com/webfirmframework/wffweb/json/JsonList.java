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

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * To parse json array string:
 *
 * <pre><code>
 *     JsonListNode jsonList = JsonList.parse("[1, 4, 0, 1]");
 *
 *     //To get the value from an index
 *     int intValueAt1 = jsonList.getValueAsInteger(1);
 *     System.out.println("intValueAt1 = " + intValueAt1); // intValueAt1 = 4
 *
 *     //Prints the json string
 *     System.out.println("toJsonString = " + jsonList.toJsonString()); // toJsonString = [1,4,0,1]
 * </code></pre>
 *
 * @since 12.0.4
 */
public non-sealed class JsonList extends ArrayList<Object> implements JsonListNode, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final JsonParser JSON_PARSER = JsonParser.newBuilder().jsonObjectType(JsonObjectType.JSON_MAP)
            .jsonArrayType(JsonArrayType.JSON_LIST).validateEscapeSequence(true).build();

    public JsonList(final int initialCapacity) {
        super(initialCapacity);
    }

    public JsonList() {
        super();
    }

    public JsonList(final Collection<?> c) {
        super(c);
    }

    /**
     * @param json the json array string to parse
     * @return the JsonList object which is equalent to JSON array.
     * @since 12.0.4
     */
    public static JsonList parse(final String json) {
        if (JSON_PARSER.parseJsonArray(json) instanceof final JsonList jsonList) {
            return jsonList;
        }
        return null;
    }

    @Override
    public String toString() {
        return toJsonString();
    }
}
