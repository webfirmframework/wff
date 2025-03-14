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

/**
 * @since 12.0.4
 */
public enum JsonStringValueType {

    /**
     * To parse the value as String.
     *
     * @since 12.0.4
     */
    STRING {
        @Override
        String parse(final int[] codePoints, final int[] startEndIndices) {
            return JsonCodePointUtil.toString(codePoints, startEndIndices);
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
            return new JsonValue(JsonCodePointUtil.cut(codePoints, startEndIndices), JsonValueType.STRING);
        }
    };

    JsonStringValueType() {
    }

    Object parse(final int[] codePoints, final int[] startEndIndices) {
        throw new AssertionError();
    }
}
